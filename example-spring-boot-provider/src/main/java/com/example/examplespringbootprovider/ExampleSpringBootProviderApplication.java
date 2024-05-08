package com.example.examplespringbootprovider;

import com.wang.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ExampleSpringBootProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleSpringBootProviderApplication.class, args);
	}

}
