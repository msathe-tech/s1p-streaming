package com.example.kafkajustridei1;

import com.example.kafkajustridei1.bindings.SpeedCheckBinding;
import com.example.kafkajustridei1.bindings.ViolationsCheckBinding;
import com.example.kafkajustridei1.domain.CarPodEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaJustRideI1Application {

	public static void main(String[] args) {
		SpringApplication.run(KafkaJustRideI1Application.class, args);
	}

//	@Component
//	@EnableBinding(SpeedCheckBinding.class)
//	public static class SpeedCheckProcessor {
//		static final long SPEED_THRESHOLD = 70L;
//		Logger log = LoggerFactory.getLogger(getClass());
//
//		@StreamListener(SpeedCheckBinding.CAR_PODS_IN)
//		@SendTo(SpeedCheckBinding.OVERSPEED_OUT)
//		public KStream<String, CarPodEvent> speedCheck(KStream<Object, CarPodEvent> podEvents) {
//
//			KStream<String, CarPodEvent> speedEvents = podEvents
//					.filter((k, v) -> v.getSpeed() > SPEED_THRESHOLD)
//					.map((k, v) -> new KeyValue<String, CarPodEvent>(new String(v.getUuid()),
//							new CarPodEvent(v.getUuid(), v.getLatitude(), v.getLongitude(), v.getSpeed())));
//
//			speedEvents
//					.foreach((k, v) -> log.info(v.toString()));
//
//			return speedEvents;
//		}
//	}

	@Component
	@EnableBinding({ViolationsCheckBinding.class})
	public static class ViolationsCheckProcessor {
		static final int WINDOW_SIZE_MS = 10000;

		Logger log = LoggerFactory.getLogger(getClass());

		@StreamListener(ViolationsCheckBinding.OVERSPEED_IN)
		public void violationsCheck(KStream<String, CarPodEvent> speedEvents) {
			speedEvents
					.foreach((k, v) -> log.info("SpeedEvent" + ": " + k));
		}
	}



}
