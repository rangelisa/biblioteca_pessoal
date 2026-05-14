package com.biblioteca;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.biblioteca.config.MongoTestConfig;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MongoTestConfig.class)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
