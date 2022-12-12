package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.atomic.AtomicInteger;

public class Listener {

    public static AtomicInteger increment = new AtomicInteger(1);
    public static AtomicInteger incrementComm = new AtomicInteger(1);
    private final String id;
    private final String groupId;
    private final String topic;

    public Listener(String id, String topic, String groupId) {
        this.id = id;
        this.topic = topic;
        this.groupId = groupId;
    }

    public String getId() {
        return this.id;
    }

    public String getTopic() {
        return this.topic;
    }
    public String getGroupId() {
        return this.groupId;
    }

    @KafkaListener(id = "#{__listener.id}", topics = "#{__listener.topic}", groupId="__listener.groupId" )
    public void listen1(Acknowledgment ack, SmsMessage message, ConsumerRecordMetadata meta) {

        System.out.println(message.getIntErrorText());

        System.out.println("Прочитано:" + increment.getAndIncrement());
        ack.acknowledge();
        System.out.println("Закоммичено:" + incrementComm.getAndIncrement());
//        System.out.println(meta.topic());
//        System.out.println(meta.partition());
    }

}
