package com.datn.project.dto.chatbox;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatResponse {
    private String reply;
    private List<ProposedItem> proposedItems;
    private List<RecommendedProduct> recommendedProducts;
    private List<ReturnableOrder> returnableOrders;
}
