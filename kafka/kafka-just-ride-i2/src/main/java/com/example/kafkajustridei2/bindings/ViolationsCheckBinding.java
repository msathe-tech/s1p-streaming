package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarPodEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface ViolationsCheckBinding {
	public final String VIOLATIONS_IN = "violations-in";

	@Input(VIOLATIONS_IN)
	KStream<String, ViolationEvent> violationsIn();

}
