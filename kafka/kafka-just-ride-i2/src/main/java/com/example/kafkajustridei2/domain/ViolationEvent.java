package com.example.kafkajustridei2.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ViolationEvent {

	private String uuid;
	private List<CarPodEvent> carPodEvents;
	private long start;
	private long end;

	@Override
	public String toString() {
		return "ViolationEvent{" +
				"uuid='" + uuid + '\'' +
				", count=" + carPodEvents.size() +
				", start=" + new Date(start * 1000) +
				", end=" + new Date(end * 1000)  +
				'}';
	}

	public ViolationEvent addCarPodEvent(CarPodEvent cpe) {
		if(uuid == null)
			uuid = cpe.getUuid();

		carPodEvents.add(cpe);
		return this;
	}

	public ViolationEvent closeWindow() {
		end = new Date().getTime();
		return this;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<CarPodEvent> getCarPodEvents() {
		return carPodEvents;
	}

	public void setCarPodEvents(List<CarPodEvent> carPodEvents) {
		this.carPodEvents = carPodEvents;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public ViolationEvent() {
		carPodEvents = new ArrayList<CarPodEvent>();
		start = new Date().getTime();
	}
}
