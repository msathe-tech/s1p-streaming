package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface SpeedCheckBinding {
	public final String CAR_EVENTS_IN = "car-events-in";

	@Input(CAR_EVENTS_IN)
	KStream<String, CarEvent> carEventsIn();

	public final String VIOLATIONS_OUT = "violations-out";

	@Output(VIOLATIONS_OUT)
	KStream<String, ViolationEvent> violationsOut();
}
