package com.example.kafkajustridei1.bindings;

import com.example.kafkajustridei1.domain.PodEvent;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface SpeedCheckBinding {
	public final String PODS_IN = "pods-in";
	public final String OVERSPEED_OUT = "overspeed-out";

	@Input(PODS_IN)
	KStream<?, PodEvent> podsIn();

	@Output(OVERSPEED_OUT)
	KStream<String, PodEvent> podsOut();
}
