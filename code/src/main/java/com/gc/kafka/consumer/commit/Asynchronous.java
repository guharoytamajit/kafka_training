package com.gc.kafka.consumer.commit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;


public class Asynchronous implements Runnable {
		  private final KafkaConsumer<String, String> consumer;
		  private final List<String> topics;
		  private final int id;
	
		  public Asynchronous(int id,
		                      String groupId, 
		                      List<String> topics) {
		    this.id = id;
		    this.topics = topics;
		    Properties props = new Properties();
		    props.put("bootstrap.servers", "localhost:9091");
		    props.put("group.id", groupId);
		    props.put("key.deserializer", StringDeserializer.class.getName());
		    props.put("value.deserializer", StringDeserializer.class.getName());
		    this.consumer = new KafkaConsumer<>(props);
		  }
		 
		  @Override
		  public void run() {
		    try {
		      consumer.subscribe(topics);
	
		      while (true) {
		        ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
		        for (ConsumerRecord<String, String> record : records) {
		          Map<String, Object> data = new HashMap<>();
		          data.put("partition", record.partition());
		          data.put("offset", record.offset());
		          data.put("value", record.value());
		          System.out.println(this.id + ": " + data);
		        }
		        consumer.commitAsync();
		      }
		    } catch (WakeupException e) {
		      // ignore for shutdown 
		    } finally {
		      consumer.close();
		    }
		  }
	
		  public void shutdown() {
		    consumer.wakeup();
		  }
		  
		  public static void main(String[] args) { 
			  int numConsumers = 1;
			  String groupId = "consumer-ASynchronous-Commit";
			  List<String> topics = Arrays.asList("testA");
			  ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
	
			  final List<Asynchronous> consumers = new ArrayList<>();
			  for (int i = 0; i < numConsumers; i++) {
			    Asynchronous consumer = new Asynchronous(i, groupId, topics);
			    consumers.add(consumer);
			    executor.submit(consumer);
			  }
	
			  Runtime.getRuntime().addShutdownHook(new Thread() {
			    @Override
			    public void run() {
			      for (Asynchronous consumer : consumers) {
			        consumer.shutdown();
			      } 
			      executor.shutdown();
			      try {
			        executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
			      } catch (InterruptedException e) {
			      }
			    }
			  });
			}
		}
	


