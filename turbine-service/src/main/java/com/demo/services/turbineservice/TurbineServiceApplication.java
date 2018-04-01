package com.demo.services.turbineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableCircuitBreaker
@EnableEurekaClient
@EnableTurbine
@EnableHystrixDashboard
@SpringBootApplication
public class TurbineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineServiceApplication.class, args);
	}
}
