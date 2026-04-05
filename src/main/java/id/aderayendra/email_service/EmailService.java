package id.aderayendra.email_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "order-queue")
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);
        try {
            String orderId = extractValue(message, "\"order\"\\s*:\\s*\\{[^}]*\"id\"\\s*:\\s*(\\d+)");
            String productName = extractValue(message, "\"product\"\\s*:\\s*\\{[^}]*\"nama\"\\s*:\\s*\"([^\"]+)\"");
            String quantity = extractValue(message, "\"order\"\\s*:\\s*\\{[^}]*\"jumlah\"\\s*:\\s*(\\d+)");
            String total = extractValue(message, "\"order\"\\s*:\\s*\\{[^}]*\"total\"\\s*:\\s*([\\d.]+)");
            String status = extractValue(message, "\"status\"\\s*:\\s*\"([^\"]+)\"");

            String subject = "Order Confirmation - Order #" + orderId;
            String body = String.format(
                    "Your order with ID %s has been %s.\n\n" +
                    "Details:\n" +
                    "- Product: %s\n" +
                    "- Quantity: %s\n" +
                    "- Total: Rp %s\n\n" +
                    "Thank you for your order!",
                    orderId, status, productName, quantity, total
            );

            sendEmail("ervan@pnp.ac.id", subject, body);
        } catch (Exception e) {
            System.err.println("Failed to parse and send email: " + e.getMessage());
        }
    }

    private String extractValue(String json, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom("ade.rayendra@gmail.com");
        mailSender.send(mailMessage);
    }
}
