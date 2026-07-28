package com.datn.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.ModerationService;
import com.datn.project.service.ProfanityFilterService;

@RestController
@RequestMapping("/api/v1/moderation")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private ProfanityFilterService profanityFilterService;

    @PostMapping("/check")
    public Map<String, Object> check(@RequestBody Map<String, String> body) {
        String text = body.get("text");

    if (profanityFilterService.containsProfanity(text)) {
        return Map.of("flagged", true, "reason", "profanity");
    }

    var result = moderationService.check(text);
    return Map.of("flagged", result.flagged(), "reason", result.reason() != null ? result.reason() : "");
    }
}
