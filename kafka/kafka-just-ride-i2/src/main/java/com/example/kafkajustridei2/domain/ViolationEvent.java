package com.example.kafkajustridei2.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViolationEvent {

	static final long SPEED_THRESHOLD = 60L;

	private String uuid;
	private List<CarEvent> carEvents;
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
				", count=" + violationCount +
				", start=" + new Date(start) +
				", end=" + new Date(end)  +
				'}';
	}

	public ViolationEvent addCarPodEvent(CarEvent cpe) {
		if(uuid == null)
			uuid = cpe.getUuid();

		if(cpe.getSpeed() > SPEED_THRESHOLD) {
			violationCount++;
			carEvents.add(cpe);
		}

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

	public List<CarEvent> getCarEvents() {
		return carEvents;
	}

	public void setCarEvents(List<CarEvent> carEvents) {
		this.carEvents = carEvents;
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
		carEvents = new ArrayList<CarEvent>();
		start = new Date().getTime();
	}
}
