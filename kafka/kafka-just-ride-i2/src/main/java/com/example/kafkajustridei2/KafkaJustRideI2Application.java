package com.example.kafkajustridei2;

import com.example.kafkajustridei2.bindings.ViolationsCheckBinding;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaJustRideI2Application {

	public static void main(String[] args) {
		SpringApplication.run(KafkaJustRideI2Application.class, args);
	}


	// Downstream streaming app, consumes from "violations" topic and just prints the ViolationEvents
	@Component
	@EnableBinding(ViolationsCheckBinding.class)
	public static class ViolationsStream {

		Logger log = LoggerFactory.getLogger(getClass());

		@StreamListener(ViolationsCheckBinding.VIOLATIONS_IN)
		public void processViolations(KStream<String, ViolationEvent> violations) {

			violations
					.foreach((k, v) -> log.info("ViolationCheck: uuid = " + k + ", " + v.toString()));

		}
	}
}

