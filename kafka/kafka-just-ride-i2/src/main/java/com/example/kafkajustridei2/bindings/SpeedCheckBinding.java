package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface SpeedCheckBinding {
	public final String CAR_PODS_IN = "car-pods-in";

	@Input(CAR_PODS_IN)
	KStream<String, CarEvent> podsIn();

	public final String VIOLATIONS_OUT = "violations-out";

	@Output(VIOLATIONS_OUT)
	KStream<String, ViolationEvent> violationsOut();
}
