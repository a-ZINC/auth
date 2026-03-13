package com.authms.service.svc;

import com.authms.service.config.AuthServiceProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuthServiceProperties properties;

    // Send magic link email
    public void sendMagicLink(String toEmail, String token) {
        String link = properties.getEmail().getBaseUrl()
                + "/auth/verify?provider=EMAIL&token=" + token;

        int expiryMins = properties.getEmail()
                .getMagicLinkExpirationMinutes();
        String html = """
            <div style="font-family:Arial,sans-serif;
                        max-width:600px;margin:0 auto;">
                <h2 style="color:#333;">Login to %s</h2>
                <p>Click the button below to login.
                   This link expires in <b>%d minutes</b>.</p>
                <div style="margin:30px 0;">
                    <a href="%s"
                       style="background:#4F46E5;color:white;
                              padding:14px 28px;
                              text-decoration:none;
                              border-radius:6px;
                              font-size:16px;">
                        Login Now
                    </a>
                </div>
                <p style="color:#888;font-size:12px;">
                    If you didn't request this, ignore this email.
                    Link expires in %d minutes.
                </p>
            </div>
            """.formatted(
                properties.getEmail().getFromName(),
                expiryMins,
                link,
                expiryMins
        );

        sendHtml(toEmail, "Your login link", html);
        log.info("Magic link sent to: {}**", toEmail.substring(0, 3));
    }


    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(
                    properties.getEmail().getFromAddress(),
                    properties.getEmail().getFromName()
            );
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Email error: {}", e.getMessage());
            throw new RuntimeException("Email service error", e);
        }
    }
}