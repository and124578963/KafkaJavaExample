package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
@Configuration
@EnableKafka
public class Config {

    public long start = System.currentTimeMillis();

    @Bean
    public ConsumerFactory<String, SmsMessage> consumerFactory(@Value("${bootstrap}") String bootstrap,
                                                               @Value("${group.id}") String groupId,
                                                               @Value("${duration}")String duration,
                                                               @Value("${auto.offset.reset}") String offsetReset,
                                                               @Value("${auto.commit}") boolean autoCommit
                                                               ) {


        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        //Создаем поток на самоубийство через заданное время/
        Thread exitTread = new Thread(() -> {
            while (true) {
                if (System.currentTimeMillis() > start + Long.parseLong(duration) * 1000) {
                    System.exit(0);
                }
            }
        });
        exitTread.start();


        return new DefaultKafkaConsumerFactory<>(props,  new StringDeserializer(), new JsonDeserializer<>(SmsMessage.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String , SmsMessage>
    kafkaListenerContainerFactory(ConsumerFactory<String, SmsMessage> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SmsMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
//  Необходим для ручного коммита MANUAL_IMMEDIATE:
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }




    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Listener listener(String id, String topic, String groupId) {
        return new Listener(id, topic, groupId);
    }

    @Bean
    @Qualifier("start")
    public String start(AnnotationConfigApplicationContext context, @Value("${list.id}") String[] listId,
                        @Value("${topic}") String  topic,  @Value("${group.id}") String groupId  ) {

        for(String id: listId){
            context.getBean(Listener.class, id, topic, groupId);
        }
           return "Ok";
    }


}
