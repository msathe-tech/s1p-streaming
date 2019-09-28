package com.example.kafkajustridei2;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.example.kafkajustridei2.bindings.ViolationsCheckBinding;
import com.example.kafkajustridei2.domain.CarPodEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaJustRideI2Application {

	public static void main(String[] args) {
		SpringApplication.run(KafkaJustRideI2Application.class, args);
	}


	@Component
	@EnableBinding(ViolationsCheckBinding.class)
	public static class ViolationsStream {

		Logger log = LoggerFactory.getLogger(getClass());

		@StreamListener(ViolationsCheckBinding.VIOLATIONS_IN)
		public void processViolations(KStream<String, ViolationEvent> violations) {

			violations
					.foreach((k, v) -> log.info("ViolationCheck: uuid = " + k + ", count= " + v.getCarPodEvents().size()));

		}

	}



//	@Component
//	@EnableBinding({ViolationsCheckBinding.class})
//	public static class ViolationsCheckProcessor {
//		static final int WINDOW_SIZE_MS = 10000;
//		static final String WINDOW_STORE = "violation-events";
//
//		Logger log = LoggerFactory.getLogger(getClass());
//
//		@StreamListener(ViolationsCheckBinding.OVERSPEED_IN)
//		//@SendTo(ViolationsCheckBinding.VIOLATIONS_OUT)
//		public void violationsCheck(KStream<String, CarPodEvent> speedEvents) {
//			speedEvents
//					.foreach((k, v) -> log.info("SpeedEvent" + ": " + k));
//
//			ObjectMapper podEventMapper = new ObjectMapper();
//			Serde<CarPodEvent> podEventSerde = new JsonSerde<>(CarPodEvent.class, podEventMapper);
//
//			ObjectMapper violationEventMapper = new ObjectMapper();
//			Serde<ViolationEvent> violationEventSerde = new JsonSerde<>(ViolationEvent.class, violationEventMapper);
//
//			KStream<Windowed<String>, ViolationEvent> violationEventsWindowedStream = speedEvents
//					.groupBy((k, v) -> v.getUuid(), Serialized.with(Serdes.String(), podEventSerde))
//					.windowedBy(TimeWindows.of(WINDOW_SIZE_MS))
//					.<ViolationEvent>aggregate(ViolationEvent::new,
//							(k, carPodEvent, violationEvent) -> violationEvent.addCarPodEvent(carPodEvent),
//							Materialized.<String, ViolationEvent, WindowStore<Bytes, byte[]>>as(WINDOW_STORE)
//									.withKeySerde(Serdes.String())
//									.withValueSerde(violationEventSerde))
//					.mapValues((violationEvent) -> violationEvent.closeWindow())
//					.toStream();
//
//
//			violationEventsWindowedStream
//					.foreach((k, v) -> log.info(v.toString()));
//		}
//	}

}

