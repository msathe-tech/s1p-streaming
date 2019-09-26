package com.example.kafkajustridei1.bindings;

import com.example.kafkajustridei1.domain.CarPodEvent;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface ViolationsCheckBinding {
	public final String OVERSPEED_IN = "overspeed-in";

	@Input(OVERSPEED_IN)
	KStream<String, CarPodEvent> overSpeedIn();

}
