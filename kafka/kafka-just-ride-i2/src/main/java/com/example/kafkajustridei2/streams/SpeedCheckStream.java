package com.example.kafkajustridei2.streams;

import com.example.kafkajustridei2.bindings.SpeedCheckBinding;
import com.example.kafkajustridei2.domain.CarEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;

import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;


@EnableBinding(SpeedCheckBinding.class)
public class SpeedCheckStream {

	Logger log = LoggerFactory.getLogger(getClass());
	final String WINDOW_STORE = "violations-store";
	final int WINDOW_SIZE_MS = 30000;

	@StreamListener(SpeedCheckBinding.CAR_EVENTS_IN)
	@SendTo(SpeedCheckBinding.VIOLATIONS_OUT)
	public KStream<String, ViolationEvent> speedCheck(KStream<String, CarEvent> carEvents) {
		carEvents
				.foreach((k, v) -> log.info("CarEvent: " + "key = " + k + ", speed = " + v.getSpeed()));

		ObjectMapper violationEventMapper = new ObjectMapper();
		Serde<ViolationEvent> violationEventSerde = new JsonSerde<>(ViolationEvent.class, violationEventMapper);

		KStream<String, ViolationEvent> violations = carEvents
				.groupByKey()
				.windowedBy(TimeWindows.of(WINDOW_SIZE_MS))
				.aggregate(ViolationEvent::new,
						(k, carPodEvent, violationEvent) -> violationEvent.addCarEvent(carPodEvent),
						Materialized.<String, ViolationEvent, WindowStore<Bytes, byte[]>>as(WINDOW_STORE)
								.withKeySerde(Serdes.String())
								.withValueSerde(violationEventSerde))
				.mapValues((violationEvent) -> violationEvent.closeWindow())
				.toStream()
				.filter((k, v) -> v.getViolationCount() > 2)
				.selectKey((k, v) -> k.key())
				;

		violations
				.foreach((k, v) -> log.info("VIOLATION key= " + k + ", value =" + v.toString()));

		return violations;
	}

}
