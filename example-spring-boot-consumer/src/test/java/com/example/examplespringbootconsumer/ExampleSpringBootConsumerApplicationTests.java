package com.example.examplespringbootconsumer;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleSpringBootConsumerApplicationTests {

	@Resource
	private ExampleServiceImpl exampleService;

	@Test
	void test() {

		exampleService.test();

	}

}
