package com.datn.project.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.datn.project.entity.User;
import com.datn.project.entity.Voucher;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  private void sendHtmlEmail(String to, String subject, String html) {
    try {
      MimeMessage message = mailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(
          message,
          true,
          "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);

      mailSender.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }

  public void sendMessageEmail(String to, String subject, String body) {

    String html = """
        <pre style="font-family: Georgia, serif; white-space: pre-wrap;">
        %s
        </pre>
        """.formatted(body);

    sendHtmlEmail(to, subject, html);
  }

  public void sendActivationEmail(User user, String activationLink) {
    String html = """
        <!DOCTYPE html>
                   <html>
                   <head>
                     <meta charset="UTF-8" />
                     <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                   </head>
                   <body style="margin:0; padding:0;">
           <div style="font-family: Georgia, 'Times New Roman', serif; background-color:#f5f5f0; padding: 40px 0;">
             <div style="max-width:520px; margin:0 auto; background:#ffffff; border:1px solid #e5e0d8;">

               <div style="background:#0a0a0a; padding:28px 40px; text-align:center;">
                 <span style="font-size:24px; font-weight:600; color:#f5f5f0;">Maison</span>
                 <span style="font-size:24px; font-style:italic; color:#c9a961;">Calcio</span>
               </div>

               <div style="padding:36px 40px;">
                 <p style="font-size:11px; text-transform:uppercase; letter-spacing:3px; color:#c9a961; margin:0 0 12px;">
                   Xác nhận tài khoản
                 </p>
                 <h1 style="font-size:24px; margin:0 0 14px; color:#0a0a0a;">
                   Chào mừng, %s!
                 </h1>
                 <p style="font-size:14px; color:#4a4a4a; line-height:1.6; margin:0 0 28px;">
                   Cảm ơn bạn đã đăng ký tài khoản tại Maison Calcio. Nhấn nút bên dưới để kích hoạt
                   và bắt đầu mua sắm những sản phẩm bóng đá tinh tuyển.
                 </p>

                 <div style="text-align:center; margin-bottom:28px;">
                   <a href="%s" style="display:inline-block; background:#0a0a0a; color:#f5f5f0; padding:14px 36px; text-decoration:none; font-size:13px; text-transform:uppercase; letter-spacing:2px;">
                     Kích hoạt tài khoản
                   </a>
                 </div>

                 <p style="font-size:12px; color:#8a8a8a; line-height:1.6; margin:0;">
                   Liên kết có hiệu lực trong 24 giờ. Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email.
                 </p>
               </div>

               <div style="background:#0a0a0a; padding:20px 40px; text-align:center;">
                 <p style="font-size:11px; color:#8a8a8a; margin:0;">© 2026 Maison Calcio</p>
               </div>
             </div>
           </div>
            </body>
               </html>
           """
        .formatted(user.getFullName(), activationLink);

    sendHtmlEmail(user.getEmail(), "Kích hoạt tài khoản — Maison Calcio", html);
  }

  public void sendResetPasswordEmail(User user, String resetLink) {
    String html = """
        <!DOCTYPE html>
                   <html>
                   <head>
                     <meta charset="UTF-8" />
                     <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                   </head>
                   <body style="margin:0; padding:0;">
           <div style="font-family: Georgia, 'Times New Roman', serif; background-color:#f5f5f0; padding: 40px 0;">
             <div style="max-width:520px; margin:0 auto; background:#ffffff; border:1px solid #e5e0d8;">

               <div style="background:#0a0a0a; padding:28px 40px; text-align:center;">
                 <span style="font-size:24px; font-weight:600; color:#f5f5f0;">Maison</span>
                 <span style="font-size:24px; font-style:italic; color:#c9a961;">Calcio</span>
               </div>

               <div style="padding:36px 40px;">
                 <p style="font-size:11px; text-transform:uppercase; letter-spacing:3px; color:#c9a961; margin:0 0 12px;">
                   Khôi phục mật khẩu
                 </p>
                 <h1 style="font-size:24px; margin:0 0 14px; color:#0a0a0a;">
                   Xin chào, %s
                 </h1>
                 <p style="font-size:14px; color:#4a4a4a; line-height:1.6; margin:0 0 28px;">
                   Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.
                   Nhấn nút bên dưới để tạo mật khẩu mới.
                 </p>

                 <div style="text-align:center; margin-bottom:28px;">
                   <a href="%s" style="display:inline-block; background:#0a0a0a; color:#f5f5f0; padding:14px 36px; text-decoration:none; font-size:13px; text-transform:uppercase; letter-spacing:2px;">
                     Đặt lại mật khẩu
                   </a>
                 </div>

                 <p style="font-size:12px; color:#8a8a8a; line-height:1.6; margin:0;">
                   Liên kết có hiệu lực trong 15 phút. Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email — mật khẩu của bạn sẽ không thay đổi.
                 </p>
               </div>

               <div style="background:#0a0a0a; padding:20px 40px; text-align:center;">
                 <p style="font-size:11px; color:#8a8a8a; margin:0;">© 2026 Maison Calcio</p>
               </div>
             </div>
           </div>
            </body>
               </html>
           """
        .formatted(user.getFullName(), resetLink);

    sendHtmlEmail(user.getEmail(), "Khôi phục mật khẩu — Maison Calcio", html);
  }

  public void sendBirthdayVoucherEmail(User user, Voucher voucher, String monthLabel) {
    String discountText = voucher.getDiscountType().name().equals("PERCENT")
        ? voucher.getDiscountValue() + "%"
        : voucher.getDiscountValue() + "₫";

    String expiresAt = voucher.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    String html = """
        <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                </head>
                <body style="margin:0; padding:0;">
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
            </body>
            </html>
            """
        .formatted(monthLabel, user.getFullName(), voucher.getCode(), discountText, expiresAt);

    sendHtmlEmail(user.getEmail(), "🎂 Quà sinh nhật dành riêng cho bạn — Maison Calcio", html);
  }
}