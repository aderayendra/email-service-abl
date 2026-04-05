package id.aderayendra.email_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @RabbitListener(queues = "order-queue")
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);
    }
}
