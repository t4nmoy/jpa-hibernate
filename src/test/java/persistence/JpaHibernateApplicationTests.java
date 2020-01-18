package persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import persistence.entity.Call;
import persistence.service.CallService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class JpaHibernateApplicationTests {

	@Autowired
	private CallService callService;

	@Test
	void contextLoads() {

		Call call = callService.save(LocalDateTime.now());

		assertNotNull(call);
	}

}
