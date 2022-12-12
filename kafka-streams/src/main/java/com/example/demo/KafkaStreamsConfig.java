package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {
    public long start = System.currentTimeMillis();
    public static int pause = 0;

//  Здесь заводим конфиги кафки, если нужны новые https://kafka.apache.org/documentation/
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(@Value("${streams.id}") String id,
                                                     @Value("${kafka.bootstrap}") String host,
                                                     @Value("${streams.pause}") String pause,
                                                     @Value("${duration}")String duration,
                                                     @Value("${streams.threads}") String threads,
                                                     @Value("${streams.replication-factor}") String replicationFactor,
                                                     @Value("${streams.num-standby-replicas}") String numStandbyReplicas,
                                                     @Value("${streams.compression}") String compression,
                                                     @Value("${streams.commit-interval-ms}") String commitIntervalMs,
                                                     @Value("${streams.processing-guarantee}") String processingGuarantee,
                                                     @Value("${streams.auto-reset-offset}") String autoResetOffset,
                                                     @Value("${streams.enable-auto-commit}") String enableAutoCommit,
                                                     @Value("${streams.auto-commit-interval}") String autoCommitInterval,
                                                     @Value("${streams.session-timeout}") String sessionTimeout,
                                                     @Value("${streams.max-request-size}") String maxRequestSize,
                                                     @Value("${streams.max-partition-fetch-bytes}") String maxPartitionFetchBytes
                                                     ) {
        KafkaStreamsConfig.pause = Integer.parseInt(pause);

        Map<String, Object> config = new HashMap<>();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, id);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,host );
        config.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        config.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        config.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, threads);
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, commitIntervalMs);
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, processingGuarantee);
        config.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, replicationFactor);
        config.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, numStandbyReplicas);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compression);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoResetOffset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);

//Создаем поток на самоубийство через заданное время/
        Thread exitTread = new Thread(() -> {
            while (true) {
                if (System.currentTimeMillis() > start + Long.parseLong(duration) * 1000) {
                    System.exit(0);
                }
            }
        });
        exitTread.start();

        return new KafkaStreamsConfiguration(config);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            System.out.println("State transition from " + oldState + " to " + newState);
        });
    }

    @Bean
    public KStream<String, SmsMessage> kStream(StreamsBuilder kStreamBuilder,
                                               @Value("${kafka.topic.input}") String inTopic,
                                               @Value("${kafka.topic.output}") String outTopic
                                            ) {
        KStream<String , SmsMessage> stream = kStreamBuilder.stream(inTopic, Consumed.with(Serdes.String(), new JsonSerde<>(SmsMessage.class)));

//        Здесь у нас происходит выгрузка из одного топика, обработка и загрузка в другой топик
        stream.flatMapValues(KafkaStreamsConfig::doSomeAction)
                .to(outTopic, Produced.valueSerde(new JsonSerde<>(SmsMessage.class)));


        return stream;
    }


//    Функция того, как у нас обрабатывается каждое сообщение
    public static List<SmsMessage> doSomeAction(SmsMessage  message) {

        System.out.println(message.getIntErrorText());
        List<SmsMessage> result = new ArrayList<>();
//        Берем текст ошибки из письма и перезаписываем его в 3 раза больше
        message.setIntErrorText(message.getIntErrorText()+message.getIntErrorText()+message.getIntErrorText());
        result.add(message);

// Искуственная пауза на обработку сообщения
        try {
            TimeUnit.MILLISECONDS.sleep(KafkaStreamsConfig.pause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    //Создаем выходной топик
    @Bean
    public KafkaAdmin admin(@Value("${kafka.bootstrap}") String bootstrap) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1(@Value("${kafka.topic.output}") String topic,
                           @Value("${topic.output.partitions}") int partitions,
                           @Value("${topic.output.replicas}") int replicas
    ) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }

}