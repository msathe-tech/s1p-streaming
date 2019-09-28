package com.example.kafkajustridei2.bindings;

import com.example.kafkajustridei2.domain.CarPodEvent;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface SpeedCheckBinding {
	public final String CAR_PODS_IN = "car-pods-in";
	public final String OVERSPEED_OUT = "overspeed-out";

	@Input(CAR_PODS_IN)
	KStream<String, CarPodEvent> podsIn();

	@Output(OVERSPEED_OUT)
	KStream<String, CarPodEvent> podsOut();
}
