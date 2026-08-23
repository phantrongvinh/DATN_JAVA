package com.datn.project.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResendMailService {

    private final OkHttpClient client = new OkHttpClient();
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.sender-email}")
    private String senderEmail; 

    public void send(String to, String subject, String htmlContent) {
        JSONObject body = new JSONObject();
        body.put("from", senderEmail);

        JSONArray toArray = new JSONArray();
        toArray.put(to);
        body.put("to", toArray);

        body.put("subject", subject);
        body.put("html", htmlContent);

        RequestBody requestBody = RequestBody.create(
                body.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(RESEND_API_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "no body";
                throw new RuntimeException("Gửi email thất bại: " + response.code() + " - " + errorBody);
            }
        } catch (IOException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }
}