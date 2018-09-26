package com.gc.kafka.producer.customPartioner;

import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SimplePartition {


    private static Scanner in;
    public static void main(String[] argv)throws Exception {
        String topicName = "testA";
        in = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9091");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
        String line = in.nextLine();
        while(!line.equals("exit")) {
        	
        	/*ProducerRecord(java.lang.String topic, java.lang.Integer partition, K key, V value)
        	Creates a record to be sent to a specified topic and partition
        	ProducerRecord(java.lang.String topic, K key, V value)
        	Create a record to be sent to Kafka
        	ProducerRecord(java.lang.String topic, V value)
        	Create a record with no key
        	*/
        	
        	
        	
            ProducerRecord rec = new ProducerRecord(topicName,2, line);
            producer.send(rec);
            line = in.nextLine();
        }
        in.close();
        producer.close();
    }
  }
