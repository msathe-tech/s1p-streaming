package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarPodEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Windowed;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface SpeedCheckBinding {
	public final String CAR_PODS_IN = "car-pods-in";

	@Input(CAR_PODS_IN)
	KStream<String, CarPodEvent> podsIn();

	public final String VIOLATIONS_OUT = "violations-out";

	@Output(VIOLATIONS_OUT)
	KStream<String, ViolationEvent> violationsOut();
}
