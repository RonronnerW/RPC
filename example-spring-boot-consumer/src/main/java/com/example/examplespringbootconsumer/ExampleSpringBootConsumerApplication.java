package com.example.examplespringbootconsumer;

import com.wang.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ExampleSpringBootConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleSpringBootConsumerApplication.class, args);
	}

}
