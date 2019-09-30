#Overview
This is a connected cars & insurance domain app.
Insurance company has issues pods to car owners. 
The idea is track drivers driving habits, reward good habits, put extra charge on drivers that ovespeed often. 
These pods emmit location and speed as events to Kafka. 
The events are processed by a stateful streaming application that checks for speed violations.
The app is forgiving for overspeeding if they occur below threshold in a time window of "X" seconds. 
If the driver overspeeds more than threshold within a time window then it is counted as a violation.

## App artifacts 

### Dependencies
1. Spring Cloud Streams
1. Spring Kafka
1. Spring Cloud Streams Kafka Streams

### application.yaml
1. Binder Default Key and Value SerDes
1. Spring Cloud Stream binding names and corresponding destinations 
1. Spring Cloud Stream Kafka Stream binding names and corresponding consumer "applicationId"

### Binding interface for KStream application
@Input and @Output stream bindings with KStream input and output map. 
One entry per binding in the application.yaml.
Group the bindings in an internface based on the streaming application needs. 
For instance, in this example we will have SpeedCheckBinding for SpeedCheckStream application. 

### KStream application
1. @EnableBinding(YourBindingClass.class) for the streaming application class
1. @StreamListener(YourBindingClass.InputBindingName)
1. @SendTo(YourBindingClass.OutputBindingName)
1. And your streaming logic  

### Spring Cloud Stream binding
1. @Output stream binding with (Spring Integration) Message Channel output map. 
1. One entry per binding in the application.yaml.
Group the bindings in an internface based on the streaming application needs. 


# S1P demo flow
## Step 1
1. Open start.spring.io, show the dependencies
2. Show the already created project, and take them to application.yaml
3. Show the commit interval and default Key/Value SerDe configuration
4. Live code the bindings - car-pods-out, car-pods-in, violations-out, violations-in
5. Live code the Kstream bindings consumer names - car-pods-in, violations-in

## Step 2
1. Walk through the CarPodEventsGenerator code, show the CarPodSource binding interface. 
1. Show that the class has @EnableBinding annotation
1. Show constructor with binding instance
1. Show the CarPodEvent pojo and quickly show how instance of CarPodEvent is created using random combinations
1. Show how MessageBuilder is used, how to setup MESSAGE_KEY header, point out that string is converted to Bytes. 
1. Show the binding.send method, share that this a silent method, means if there is error it is necessary to capture the Exception in try..catch.

## Step 3 - Live code the SpeedCheckStream app
### setup SpeedCheckStream class
1. Create SpeedCheckStream class in streams package
1. @EnableBinding(SpeedCheckBinding.class)

### setup class variables
1. Logger log = LoggerFactory.getLogger(getClass());
1. static final int WINDOW_SIZE_MS = 10000;
1. static final String WINDOW_STORE = "violation-events";

### create stream method 
1. checkSpeed method with @StreamListener and @SendTo 
2. checkSpeed(KStream<String, CarPodEvent> carPodEvents)
##### create SerDe
1. Serde<CarPodEvent> podEventSerde = new JsonSerde<>(CarPodEvent.class, new ObjectMapper());
1. Serde<ViolationEvent> violationEventSerde = new JsonSerde<>(ViolationEvent.class, new ObjectMapper());
##### process and transform the 'carPodEvents' stream
1. ```.groupByKey() // group by UUID```

1. ```.windowedBy(TimeWindows.of(WINDOW_SIZE_MS)) // window of WINDOW_SIZE_MS for each UUID```

1. ```
		.<ViolationEvent>aggregate(ViolationEvent::new, (k, carPodEvent, violationEvent) -> violationEvent.addCarPodEvent(carPodEvent),
		Materialized<String, ViolationEvent, WindowStore<Bytes, byte[]>>.as(WINDOW_STORE)
			.withKeySerde(Serdes.string())
			.withValueSerde(violationEventSerde))```

1. ```
    .mapValues((violationEvent) -> violationEvent.closeWindow()) // Opportunity to perform action on your event object after the tumbling window is over.
    .toStream() // convert the table to stream ```

1. ```
	  .filter((k,v) -> v.getViolationCount() > 2) // Consider only those entries with 2+ speed violations ``` 
1. ```
      .selectKey((k, v) -> k.key()) // convert the Windowed key to key
      ```






