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

//	@Component
//	@EnableBinding(CarPodsSource.class)
//	public static class CarPodEventsProducer implements ApplicationRunner {
//
//		private final MessageChannel carPodsOut;
//
//		public CarPodEventsProducer(CarPodsSource binding) {
//			carPodsOut = binding.pods();
//		}
//
//		@Override
//		public void run(ApplicationArguments args) throws Exception {
//			//14
//			Double[] latidudes = new Double[] {40.742185, 40.752185, 40.652185, 40.65185, 40.95185, 40.99185, 40.9988, 40.7433066, 40.7430, 40.706314, 40.806, 40.816, 40.716, 40.715};
//			//14
//			Double[] longitudes = new Double[] {-73.992602, -73.892702, -73.92702, -73.957021, -74.0323752, -74.12375, -73.24577, -74.056995, -74.05698, -74.05698, -74.06698, -74.07798, -73.92602, -74.05798};
//			//14
//			Double[] speeds = new Double[] {76D, 75D, 72D, 40D, 25D, 80D, 74D, 78D, 60D, 71D, 62D, 76D, 54D, 82D};
//			//4
//			String[] uuids = new String[] {"1", "2", "3", "4"};
//
//			Random random = new Random();
//
//			Runnable runnable = () -> {
//				int laIdx, lnIdx, sIdx, uIdx;
//				laIdx = random.nextInt(14);
//				lnIdx = random.nextInt(14);
//				uIdx = random.nextInt(4);
//				sIdx = random.nextInt(14);
//				CarPodEvent podEvent = new CarPodEvent(uuids[uIdx],
//						latidudes[laIdx],
//						longitudes[lnIdx],
//						speeds[sIdx]);
//
//				System.out.println(podEvent.toString());
//				Message<?> message = MessageBuilder.withPayload(podEvent)
//						.setHeader(KafkaHeaders.MESSAGE_KEY, podEvent.getUuid()).build();
//
//			};
//
//			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
//		}
//
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
//
//interface CarPodsSource {
//
//	public String CAR_PODS_OUT = "car-pods-out";
//
//	@Output(CarPodsSource.CAR_PODS_OUT)
//	MessageChannel pods();
//
//}
