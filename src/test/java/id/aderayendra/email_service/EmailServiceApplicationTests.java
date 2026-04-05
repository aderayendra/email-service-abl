package id.aderayendra.email_service;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EmailServiceApplicationTests {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@MockitoBean
	private JavaMailSender mailSender;

	@Test
	void contextLoads() {
	}

	@Test
	void testEmailSentOnMessageReceived() {
		String message = "{\"order\":{\"id\":8,\"produkId\":\"P001\",\"jumlah\":20,\"tanggal\":\"2026-03-15\",\"total\":250000.0},\"status\":\"CREATED\",\"product\":{\"id\":\"P001\",\"nama\":\"Produk A\",\"kategori\":\"Kategori A\",\"harga\":10000,\"stok\":50}}";
		rabbitTemplate.convertAndSend("order-queue", message);

		await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
			verify(mailSender).send(any(SimpleMailMessage.class));
		});
	}

}
