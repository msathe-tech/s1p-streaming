package com.example.kafkajustridei1;

import java.util.Random;

import com.example.kafkajustridei1.domain.PodEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

public class PodEventsGenerator {

	@EnableBinding(PodsSource.class)
	static class PodEventsProducer {
		//14
		private Double[] latidudes = new Double[] {40.742185, 40.752185, 40.652185, 40.65185, 40.95185, 40.99185, 40.9988, 40.7433066, 40.7430, 40.706314, 40.806, 40.816, 40.716, 40.715};
		//14
		private Double[] longitudes = new Double[] {-73.992602, -73.892702, -73.92702, -73.957021, -74.0323752, -74.12375, -73.24577, -74.056995, -74.05698, -74.05698, -74.06698, -74.07798, -73.92602, -74.05798};
		//14
		private Double[] speeds = new Double[] {76D, 75D, 72D, 40D, 25D, 80D, 74D, 78D, 60D, 71D, 62D, 76D, 54D, 82D};
		//4
		private String[] uuids = new String[] {"1", "2", "3", "4"};

		private Random random = new Random();

		@Bean
		@InboundChannelAdapter(channel = PodsSource.PODS_OUT, poller = @Poller(fixedDelay = "1000"))
		public MessageSource<PodEvent> generatePodEvents() {

			return () -> {
				int laIdx, lnIdx, sIdx, uIdx;
				laIdx = random.nextInt(14);
				lnIdx = random.nextInt(14);
				uIdx = random.nextInt(4);
				sIdx = random.nextInt(14);
				PodEvent podEvent = new PodEvent(uuids[uIdx],
						latidudes[laIdx],
						longitudes[lnIdx],
						speeds[sIdx]);

				System.out.println(podEvent.toString());

				return new GenericMessage<PodEvent>(podEvent);
			};
		}
	}

	interface PodsSource {

		String PODS_OUT = "pods-out";

		@Output(PodsSource.PODS_OUT)
		MessageChannel pods();

	}
}
