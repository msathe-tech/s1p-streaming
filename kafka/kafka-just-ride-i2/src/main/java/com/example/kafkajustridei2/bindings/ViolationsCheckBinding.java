package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarPodEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface ViolationsCheckBinding {
	public final String OVERSPEED_IN = "overspeed-in";

	@Input(OVERSPEED_IN)
	KStream<String, CarPodEvent> overSpeedIn();

//	public final String VIOLATIONS_OUT = "violations-out";
//
//	@Input(VIOLATIONS_OUT)
//	KStream<String, ViolationEvent> violationsOut();
}
