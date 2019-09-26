package com.example.kafkajustridei1;

import java.util.concurrent.atomic.LongAdder;

import com.example.kafkajustridei1.bindings.SpeedCheckBinding;
import com.example.kafkajustridei1.bindings.ViolationsCheckBinding;
import com.example.kafkajustridei1.domain.CarPodEvent;
import com.example.kafkajustridei1.domain.ViolationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
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
		static final String WINDOW_STORE = "violation-events";

		Logger log = LoggerFactory.getLogger(getClass());

		@StreamListener(ViolationsCheckBinding.OVERSPEED_IN)
		//@SendTo(ViolationsCheckBinding.VIOLATIONS_OUT)
		public void violationsCheck(KStream<String, CarPodEvent> speedEvents) {
			speedEvents
					.foreach((k, v) -> log.info("SpeedEvent" + ": " + k));

			ObjectMapper podEventMapper = new ObjectMapper();
			Serde<CarPodEvent> podEventSerde = new JsonSerde<>(CarPodEvent.class, podEventMapper);

			ObjectMapper violationEventMapper = new ObjectMapper();
			Serde<ViolationEvent> violationEventSerde = new JsonSerde<>(ViolationEvent.class, violationEventMapper);

			KStream<Windowed<String>, ViolationEvent> violationEventsWindowedStream = speedEvents
					.groupBy((k, v) -> v.getUuid(), Serialized.with(Serdes.String(), podEventSerde))
					.windowedBy(TimeWindows.of(WINDOW_SIZE_MS))
					.<ViolationEvent>aggregate(ViolationEvent::new,
							(k, carPodEvent, violationEvent) -> violationEvent.addCarPodEvent(carPodEvent),
							Materialized.<String, ViolationEvent, WindowStore<Bytes, byte[]>>as(WINDOW_STORE)
									.withKeySerde(Serdes.String())
									.withValueSerde(violationEventSerde))
					.mapValues((violationEvent) -> violationEvent.closeWindow())
					.toStream();


			violationEventsWindowedStream
					.foreach((k, v) -> log.info(v.toString()));
		}
	}


}
