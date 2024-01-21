package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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

    @KafkaListener(id = "#{__listener.id}", topics = "#{__listener.topic}", groupId="#{__listener.groupId}" )
    public void listen1(Acknowledgment ack, SmsMessage message, ConsumerRecordMetadata meta) throws Exception {

        if (ThreadLocalRandom.current().nextInt(0, 10) == 5)
        {
            System.out.println("["+getId() + "] [Партиция " + meta.partition() + "] Прочитано всего" + increment.get()+ ": Сообщение содержит ID = " +  message.getIntErrorText());
            System.out.println("["+getId() + "] [ERROR] Ошибка: " + increment.get());
            throw new Exception("Шанс 10% на ошибку");
        }
        System.out.println("["+getId() + "] [Партиция " + meta.partition() + "] Прочитано всего " + increment.getAndIncrement() + ": Сообщение содержит ID = " +  message.getIntErrorText());
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(0, 20));
        ack.acknowledge();
        System.out.println("["+getId() + "] Обработано и закоммичено:" + incrementComm.getAndIncrement());

    }

}
