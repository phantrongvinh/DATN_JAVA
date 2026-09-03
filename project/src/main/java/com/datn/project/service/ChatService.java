package com.datn.project.service;

import com.datn.project.dto.chatbox.*;
import com.datn.project.entity.Order;
import com.datn.project.entity.ProductImage;
import com.datn.project.entity.ProductVariant;
import com.datn.project.repository.IOrderRepository;
import com.datn.project.repository.IProductVariantRepository;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {

    private final OkHttpClient client = new OkHttpClient();
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final int RETURN_WINDOW_DAYS = 7;

    @Value("${openai.api-key}")
    private String apiKey;

    @Autowired
    private IProductVariantRepository productVariantRepository;
    @Autowired
    private IOrderRepository orderRepository;

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý bán hàng của Maison Calcio — cửa hàng thiết bị bóng đá.

            QUY TẮC BẮT BUỘC:
            1. Khi khách mô tả sản phẩm muốn tìm (tên, loại, màu, size...), LUÔN gọi tool "search_products"
               để tìm hàng thật trong kho. KHÔNG được tự bịa tên sản phẩm, giá, hay tồn kho.
            2. Sau khi tìm được, giới thiệu ngắn gọn 1-3 sản phẩm phù hợp nhất, để khách xem và bấm vào xem chi tiết.
            3. Nếu khách nói muốn ĐẶT MUA 1 sản phẩm cụ thể (đã rõ variant, có hoặc chưa rõ số lượng —
               nếu chưa rõ số lượng thì mặc định 1), gọi tool "propose_items" để tạo đề xuất đơn hàng.
               Khách sẽ tự xác nhận số lượng cuối cùng ở giao diện trước khi đặt.
            4. Khi khách nhắc tới "trả hàng"/"đổi trả"/"hoàn hàng", gọi tool "list_returnable_orders" ngay,
               KHÔNG hỏi mã đơn thủ công. Sau khi có kết quả, LUÔN kèm câu:
               "Các đơn hàng chỉ có thể trả hàng trong vòng 7 ngày kể từ khi giao."
            5. Trả lời ngắn gọn, thân thiện, bằng tiếng Việt.
            """;

    public ChatResponse chat(Integer userId, List<ChatMessage> history, String newMessage) {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", SYSTEM_PROMPT));
        for (ChatMessage m : history) {
            messages.put(new JSONObject().put("role", m.getRole()).put("content", m.getContent()));
        }
        messages.put(new JSONObject().put("role", "user").put("content", newMessage));

        List<ProposedItem> proposedItems = null;
        List<RecommendedProduct> recommendedProducts = null;
        List<ReturnableOrder> returnableOrders = null;

        for (int loop = 0; loop < 5; loop++) {
            JSONObject responseJson = callOpenAI(messages);
            JSONObject choice = responseJson.getJSONArray("choices").getJSONObject(0);
            JSONObject messageObj = choice.getJSONObject("message");
            String finishReason = choice.getString("finish_reason");

            if (!"tool_calls".equals(finishReason)) {
                String reply = messageObj.optString("content", "");
                return ChatResponse.builder()
                        .reply(reply)
                        .proposedItems(proposedItems)
                        .recommendedProducts(recommendedProducts)
                        .returnableOrders(returnableOrders)
                        .build();
            }

            messages.put(messageObj);
            JSONArray toolCalls = messageObj.getJSONArray("tool_calls");

            for (int t = 0; t < toolCalls.length(); t++) {
                JSONObject toolCall = toolCalls.getJSONObject(t);
                String toolCallId = toolCall.getString("id");
                String functionName = toolCall.getJSONObject("function").getString("name");
                JSONObject args = new JSONObject(toolCall.getJSONObject("function").getString("arguments"));

                String toolResult;
                switch (functionName) {
                    case "search_products" -> {
                        recommendedProducts = executeSearchProducts(args);
                        toolResult = toJsonForAI(recommendedProducts);
                    }
                    case "propose_items" -> {
                        proposedItems = executeProposeItems(args);
                        toolResult = "Đã tạo đề xuất đơn hàng, hiển thị cho khách xác nhận số lượng.";
                    }
                    case "list_returnable_orders" -> {
                        returnableOrders = executeListReturnableOrders(userId);
                        toolResult = returnableOrders.isEmpty()
                                ? "Khách không có đơn hàng nào đủ điều kiện trả hàng."
                                : toJsonForAI(returnableOrders);
                    }
                    default -> toolResult = "Tool không tồn tại";
                }

                messages.put(new JSONObject()
                        .put("role", "tool")
                        .put("tool_call_id", toolCallId)
                        .put("content", toolResult));
            }
        }

        return ChatResponse.builder()
                .reply("Xin lỗi, mình chưa xử lý được yêu cầu này, bạn thử diễn đạt lại nhé.")
                .proposedItems(proposedItems)
                .recommendedProducts(recommendedProducts)
                .returnableOrders(returnableOrders)
                .build();
    }

    // ─── Tool 1: tìm sản phẩm thật ─────────────────────────
    private List<RecommendedProduct> executeSearchProducts(JSONObject args) {
        String query = args.optString("query", "");
        String color = args.has("color") && !args.isNull("color") ? args.getString("color") : null;
        String size = args.has("size") && !args.isNull("size") ? args.getString("size") : null;

        List<ProductVariant> results = productVariantRepository
                .searchForChat(query, color, size, PageRequest.of(0, 5));

        return results.stream()
                .map(v -> RecommendedProduct.builder()
                        .productId(v.getProduct().getId())
                        .productVariantId(v.getId())
                        .name(v.getProduct().getName())
                        .color(v.getColor())
                        .sizeName(v.getSize().getName())
                        .price(v.getPrice())
                        .image(resolveImage(v))
                        .build())
                .toList();
    }

    // ─── Tool 2: chốt đề xuất đơn hàng ──────────────────────
    private List<ProposedItem> executeProposeItems(JSONObject args) {
        JSONArray items = args.getJSONArray("items");
        List<ProposedItem> result = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Integer variantId = item.getInt("productVariantId");
            Integer quantity = item.has("quantity") ? item.getInt("quantity") : 1;

            ProductVariant v = productVariantRepository.findByIdWithProductAndImages(variantId).orElse(null);
            if (v == null)
                continue;

            result.add(ProposedItem.builder()
                    .productVariantId(v.getId())
                    .productId(v.getProduct().getId())
                    .productName(v.getProduct().getName())
                    .color(v.getColor())
                    .sizeName(v.getSize().getName())
                    .quantity(Math.min(quantity, v.getStock()))
                    .stock(v.getStock())
                    .price(v.getPrice())
                    .image(resolveImage(v))
                    .build());
        }
        return result;
    }

    // ─── Tool 3: đơn có thể trả hàng ────────────────────────
    private List<ReturnableOrder> executeListReturnableOrders(Integer userId) {
        if (userId == null)
            return List.of();

        LocalDateTime cutoff = LocalDateTime.now().minusDays(RETURN_WINDOW_DAYS);
        return orderRepository.findReturnableOrders(userId, cutoff).stream()
                .map(o -> ReturnableOrder.builder()
                        .orderId(o.getId())
                        .deliveredAt(o.getDeliveredAt())
                        .finalPrice(o.getFinalPrice())
                        .productNames(o.getOrderDetails().stream()
                                .map(d -> d.getProductName()).toList())
                        .build())
                .toList();
    }

    private String resolveImage(ProductVariant v) {
        return v.getProduct().getProductImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElseGet(() -> v.getProduct().getProductImages().stream()
                        .findFirst().map(ProductImage::getImageUrl).orElse(null));
    }

    private String toJsonForAI(Object obj) {
        return new org.json.JSONObject(Map.of("data", obj)).toString();
    }

    // ─── Gọi OpenAI với tool definitions ───────────────────
    private JSONObject callOpenAI(JSONArray messages) {
        JSONObject body = new JSONObject();
        body.put("model", "gpt-4o-mini");
        body.put("messages", messages);
        body.put("tools", buildToolDefinitions());
        body.put("tool_choice", "auto");

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI lỗi: " + response.code() + " - " + response.body().string());
            }
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("Gọi OpenAI thất bại", e);
        }
    }

    private JSONArray buildToolDefinitions() {
        JSONArray tools = new JSONArray();

        JSONObject searchFn = new JSONObject();
        searchFn.put("name", "search_products");
        searchFn.put("description", "Tìm sản phẩm/biến thể có thật trong kho theo tên, màu sắc, size");
        JSONObject searchParams = new JSONObject().put("type", "object");
        JSONObject searchProps = new JSONObject();
        searchProps.put("query",
                new JSONObject().put("type", "string").put("description", "Tên hoặc từ khóa sản phẩm"));
        searchProps.put("color", new JSONObject().put("type", "string").put("description", "Màu sắc nếu khách có nêu"));
        searchProps.put("size", new JSONObject().put("type", "string").put("description", "Size nếu khách có nêu"));
        searchParams.put("properties", searchProps);
        searchParams.put("required", new JSONArray().put("query"));
        searchFn.put("parameters", searchParams);
        tools.put(new JSONObject().put("type", "function").put("function", searchFn));

        JSONObject proposeFn = new JSONObject();
        proposeFn.put("name", "propose_items");
        proposeFn.put("description", "Tạo đề xuất đơn hàng với sản phẩm cụ thể + số lượng, chờ khách xác nhận");
        JSONObject proposeParams = new JSONObject().put("type", "object");
        JSONObject itemSchema = new JSONObject().put("type", "object");
        JSONObject itemProps = new JSONObject();
        itemProps.put("productVariantId", new JSONObject().put("type", "integer"));
        itemProps.put("quantity", new JSONObject().put("type", "integer").put("description",
                "Số lượng, mặc định 1 nếu khách không nói rõ"));
        itemSchema.put("properties", itemProps);
        itemSchema.put("required", new JSONArray().put("productVariantId"));
        JSONObject itemsArraySchema = new JSONObject().put("type", "array").put("items", itemSchema);
        JSONObject proposeProps = new JSONObject().put("items", itemsArraySchema);
        proposeParams.put("properties", proposeProps);
        proposeParams.put("required", new JSONArray().put("items"));
        proposeFn.put("parameters", proposeParams);
        tools.put(new JSONObject().put("type", "function").put("function", proposeFn));

        JSONObject returnFn = new JSONObject();
        returnFn.put("name", "list_returnable_orders");
        returnFn.put("description",
                "Lấy danh sách đơn hàng của khách đủ điều kiện trả hàng (đã giao, trong 7 ngày, chưa có yêu cầu đang chờ)");
        JSONObject returnParams = new JSONObject().put("type", "object").put("properties", new JSONObject());
        returnFn.put("parameters", returnParams);
        tools.put(new JSONObject().put("type", "function").put("function", returnFn));

        return tools;
    }
}