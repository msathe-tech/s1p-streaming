package com.example.kafkajustridei2.streams;

import com.example.kafkajustridei2.bindings.SpeedCheckBinding;
import com.example.kafkajustridei2.domain.CarPodEvent;
import com.example.kafkajustridei2.domain.ViolationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;

@EnableBinding(SpeedCheckBinding.class)
public class SpeedCheckStream {

	@Autowired
	private InteractiveQueryService iqs;


	Logger log = LoggerFactory.getLogger(getClass());
	static final int WINDOW_SIZE_MS = 10000;
	static final String WINDOW_STORE = "violation-events";


	@StreamListener(SpeedCheckBinding.CAR_PODS_IN)
	@SendTo(SpeedCheckBinding.VIOLATIONS_OUT)
	public KStream<String, ViolationEvent> speedCheck(KStream<String, CarPodEvent> podEvents) {
		podEvents
				.foreach((k, v) -> log.info("PodEvent: " + "key = " + k + ", speed = " + v.getSpeed()));

		ObjectMapper violationEventMapper = new ObjectMapper();
		Serde<ViolationEvent> violationEventSerde = new JsonSerde<>(ViolationEvent.class, violationEventMapper);

		KStream<String, ViolationEvent> violations = podEvents
				.groupByKey()
				.windowedBy(TimeWindows.of(WINDOW_SIZE_MS))
				.<ViolationEvent>aggregate(ViolationEvent::new,
						(k, carPodEvent, violationEvent) -> violationEvent.addCarPodEvent(carPodEvent),
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

	@Scheduled(fixedRate = 3000, initialDelay = 5000)
	public void printViolatons() {
		System.out.println("Hitting the store");
		ReadOnlyWindowStore<Object, Object> violationsStore = iqs
				.getQueryableStore(WINDOW_STORE, QueryableStoreTypes.windowStore());

		KeyValueIterator all = violationsStore.all();

		all.forEachRemaining(o -> {
			log.info("From store " + ((KeyValue)o).key + ", count = " + ((ViolationEvent)((KeyValue)o).value).getViolationCount());
		});

	}

}
