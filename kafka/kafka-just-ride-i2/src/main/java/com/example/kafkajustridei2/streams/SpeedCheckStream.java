package com.example.kafkajustridei2.streams;

import com.example.kafkajustridei2.bindings.SpeedCheckBinding;
import com.example.kafkajustridei2.domain.CarPodEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(SpeedCheckBinding.class)
public class SpeedCheckStream {

	static final long SPEED_THRESHOLD = 70L;
	Logger log = LoggerFactory.getLogger(getClass());

	@StreamListener(SpeedCheckBinding.CAR_PODS_IN)
	@SendTo(SpeedCheckBinding.OVERSPEED_OUT)
	public KStream<String, CarPodEvent> speedCheck(KStream<String, CarPodEvent> podEvents) {
		podEvents
				.foreach((k, v) -> log.info("PodEvent: " + "key = " + k + ", speed = " + v.getSpeed()));

		KStream<String, CarPodEvent> speedEvents = podEvents
				.filter((k, v) -> v.getSpeed() > SPEED_THRESHOLD)
				;

		speedEvents
				.foreach((k, v) -> log.info("Overspeed: " + "key = " + k + ", speed = " + v.getSpeed()));

		return speedEvents;
	}

}
