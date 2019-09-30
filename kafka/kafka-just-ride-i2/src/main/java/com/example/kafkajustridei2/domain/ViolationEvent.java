package com.example.kafkajustridei2.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ViolationEvent {

	static final long SPEED_THRESHOLD = 60L;

	private String uuid;
	private List<CarPodEvent> carPodEvents;
	private long start;
	private long end;

	public int getViolationCount() {
		return violationCount;
	}

	private int violationCount;

	@Override
	public String toString() {
		return "ViolationEvent{" +
				"uuid='" + uuid + '\'' +
				", count=" + carPodEvents.size() +
				", start=" + new Date(start) +
				", end=" + new Date(end)  +
				'}';
	}

	public ViolationEvent addCarPodEvent(CarPodEvent cpe) {
		if(uuid == null)
			uuid = cpe.getUuid();

		if(cpe.getSpeed() > SPEED_THRESHOLD)
			violationCount++;

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
