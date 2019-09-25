package com.example.kafkajustridei1;

import com.example.kafkajustridei1.bindings.SpeedCheckBinding;
import com.example.kafkajustridei1.bindings.ViolationsCheckBinding;
import com.example.kafkajustridei1.domain.PodEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaJustRideI1Application {

	public static void main(String[] args) {
		SpringApplication.run(KafkaJustRideI1Application.class, args);
	}

	@Component
	@EnableBinding(SpeedCheckBinding.class)
	public static class SpeedCheckProcessor {


		static final long SPEED_THRESHOLD = 70L;
		Logger log = LoggerFactory.getLogger(getClass());

		@StreamListener(SpeedCheckBinding.PODS_IN)
		@SendTo(SpeedCheckBinding.OVERSPEED_OUT)
		public KStream<String, PodEvent> speedCheck(KStream<Object, PodEvent> podEvents) {

			KStream<String, PodEvent> speedEvents = podEvents
					.filter((k, v) -> v.getSpeed() > SPEED_THRESHOLD)
					.map((k, v) -> new KeyValue<String, PodEvent>(new String(v.getUuid()),
							new PodEvent(v.getUuid(), v.getLatitude(), v.getLongitude(), v.getSpeed())));

			speedEvents
					.foreach((k, v) -> log.info(v.toString()));

			return speedEvents;
		}
	}

//	@EnableBinding(ViolationsCheckBinding.class)
//	public static class ViolationsCheckProcessor {
//		static final int WINDOW_SIZE_MS = 10000;
//
//		Logger log = LoggerFactory.getLogger(getClass());
//
//		@StreamListener(ViolationsCheckBinding.OVERSPEED_IN)
//		public void violationsCheck(KStream<String, PodEvent> speedEvents) {
//			speedEvents
//					.foreach((k, v) -> log.info(v.toString()));
//		}
//	}

}
