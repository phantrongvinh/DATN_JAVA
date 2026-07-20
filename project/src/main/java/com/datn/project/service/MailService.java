package com.datn.project.service;

import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.datn.project.entity.User;
import com.datn.project.entity.Voucher;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMessageEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(subject);

        message.setText(body);

        mailSender.send(message);
    }

    public void sendBirthdayVoucherEmail(User user, Voucher voucher, String monthLabel) {
        String discountText = voucher.getDiscountType().name().equals("PERCENT")
                ? voucher.getDiscountValue() + "%"
                : voucher.getDiscountValue() + "₫";

        String expiresAt = voucher.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String html = """
                <div style="font-family: Georgia, 'Times New Roman', serif; background-color:#f5f5f0; padding: 40px 0;">
                  <div style="max-width:560px; margin:0 auto; background:#ffffff; border:1px solid #e5e0d8;">

                    <div style="background:#0a0a0a; padding:28px 40px; text-align:center;">
                      <span style="font-size:24px; font-weight:600; color:#f5f5f0;">Maison</span>
                      <span style="font-size:24px; font-style:italic; color:#c9a961;">Calcio</span>
                    </div>

                    <div style="padding:36px 40px;">
                      <p style="font-size:11px; text-transform:uppercase; letter-spacing:3px; color:#c9a961; margin:0 0 12px;">
                        🎂 Quà sinh nhật %s
                      </p>
                      <h1 style="font-size:26px; margin:0 0 14px; color:#0a0a0a;">
                        Chúc mừng sinh nhật, %s!
                      </h1>
                      <p style="font-size:14px; color:#4a4a4a; line-height:1.6; margin:0 0 24px;">
                        Maison Calcio xin gửi tặng bạn một ưu đãi dành riêng cho bạn.
                      </p>

                      <div style="border:2px dashed #c9a961; background:#faf8f3; padding:24px; text-align:center; margin-bottom:24px;">
                        <p style="font-size:11px; text-transform:uppercase; letter-spacing:2px; color:#8a8a8a; margin:0 0 8px;">Mã ưu đãi</p>
                        <p style="font-size:28px; font-weight:700; color:#0a0a0a; letter-spacing:2px; margin:0 0 10px; font-family:'Courier New',monospace;">%s</p>
                        <p style="font-size:18px; color:#c9a961; font-style:italic; margin:0;">Giảm %s</p>
                      </div>

                      <p style="font-size:13px; color:#8a8a8a; line-height:1.6; margin:0;">
                        Hạn sử dụng: <b style="color:#4a4a4a;">%s</b> · Áp dụng 1 lần cho đơn hàng bất kỳ.
                      </p>
                    </div>

                    <div style="background:#0a0a0a; padding:20px 40px; text-align:center;">
                      <p style="font-size:11px; color:#8a8a8a; margin:0;">© 2026 Maison Calcio</p>
                    </div>
                  </div>
                </div>
                """.formatted(monthLabel, user.getFullName(), voucher.getCode(), discountText, expiresAt);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("🎂 Quà sinh nhật dành riêng cho bạn — Maison Calcio");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại cho " + user.getEmail(), e);
        }
    }
}
