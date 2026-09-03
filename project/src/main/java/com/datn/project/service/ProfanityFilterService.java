package com.datn.project.service;

import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProfanityFilterService {

    // TODO: bổ sung đầy đủ theo thực tế — đây chỉ là danh sách mẫu tối thiểu
    private static final Set<String> BANNED_WORDS = Set.of(
            "lon", "cac", "du ma", "dit", "vcl", "vl", "cak", "loz",
            "ngu", "cho chet", "oc cho", "sung", "chich choac","xau","gom","loi","qq","quan que","ghe"
            // TODO: thêm các biến thể viết tắt/không dấu khác
    );

    public boolean containsProfanity(String text) {
        if (text == null || text.isBlank()) return false;

        // Bỏ dấu tiếng Việt + lowercase để bắt được cả biến thể "lồn"/"lon"/"l0n"...
        String normalized = removeAccents(text.toLowerCase());
        normalized = normalized.replaceAll("[^a-z0-9\\s]", "");

        for (String banned : BANNED_WORDS) {
            // \\b word boundary để tránh match nhầm từ trong từ khác (vd "concho" chứa "cho")
            if (Pattern.compile("\\b" + Pattern.quote(banned) + "\\b").matcher(normalized).find()) {
                return true;
            }
        }
        return false;
    }

    private String removeAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "d").replace("Đ", "D");
    }
}