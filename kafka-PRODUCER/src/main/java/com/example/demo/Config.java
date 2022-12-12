package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
@Configuration
@EnableKafka
public class Config {

    public static String topic;

    public long start = System.currentTimeMillis();
    public static int maxMessages;
    public static AtomicInteger increment = new AtomicInteger(1);

    @Bean
    public Sender sender(
            KafkaTemplate<String, SmsMessage> template) {
        return new Sender(template);
    }

//    Сюда дополнять характеристики
    @Bean
    public ProducerFactory<String, SmsMessage> producerFactory(@Value("${bootstrap}") String bootstrap,
                                                               @Value("${producer.linger.ms.config}") int linger,
                                                               @Value("${maxMessages}") int maxMessages,
                                                               @Value("${duration}")String duration) {
        Config.maxMessages = maxMessages;
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        //.../ See https://kafka.apache.org/documentation/#producerconfigs for more properties

        //Создаем поток на самоубийство через заданное время/
        Thread exitTread = new Thread(() -> {
            while (true) {
                if (System.currentTimeMillis() > start + Long.parseLong(duration) * 1000) {
                    System.exit(0);
                }
            }
        });
        exitTread.start();
        return new DefaultKafkaProducerFactory<>(props);
    }

//    private Map<String, Object> senderProps(@Value("${bootstrap}") String bootstrap,
//                                            @Value("${consumer.linger.ms.config}") int linger){
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
//
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        //.../ See https://kafka.apache.org/documentation/#producerconfigs for more properties
//        return props;
//    }

    @Bean
    public
    KafkaTemplate<String, SmsMessage> kafkaTemplate(ProducerFactory<String, SmsMessage> producerFactory) {
        return new KafkaTemplate<String, SmsMessage>(producerFactory);
    }



//    Создаем топик, в который записываем
    @Bean
    public KafkaAdmin admin(@Value("${bootstrap}") String bootstrap) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1(@Value("${topic.name}") String topic,
                           @Value("${topic.partitions}") int partitions,
                           @Value("${topic.replicas}") int replicas
                           ) {
        Config.topic= topic;
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }


}
