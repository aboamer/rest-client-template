package com.amer.restclient;

import com.amer.restclient.infrastructure.rest.test.RestGatewayImpl;
import com.amer.restclient.infrastructure.rest.test.impl.ServiceImpl;
import com.amer.restclient.infrastructure.rest.test.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class RestClientApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		ServiceImpl service = applicationContext.getBean(ServiceImpl.class);

		ResponseDto responseDto = service.callApi("2");

		System.out.println(responseDto);
	}
}
