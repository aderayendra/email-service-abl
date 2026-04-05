package id.aderayendra.email_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "order-queue")
    @SuppressWarnings("unchecked")
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);
        try {
            JsonParser parser = JsonParserFactory.getJsonParser();
            Map<String, Object> map = parser.parseMap(message);

            Map<String, Object> orderMap = (Map<String, Object>) map.get("order");
            Map<String, Object> productMap = (Map<String, Object>) map.get("product");

            String orderId = String.valueOf(orderMap.get("id"));
            String productName = String.valueOf(productMap.get("nama"));
            String quantity = String.valueOf(orderMap.get("jumlah"));
            String total = String.valueOf(orderMap.get("total"));
            String status = String.valueOf(map.get("status"));

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
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.err.println("Failed to parse and send email: " + e.getMessage());
        }
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
