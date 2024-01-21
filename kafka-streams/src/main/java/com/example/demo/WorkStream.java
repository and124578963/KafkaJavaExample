package com.example.demo;

import com.example.demo.message.SmsMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class WorkStream {

    @Bean
    public Stream matStreamBuilder(@Value("${kafka.topic.input}") String inTopic,
                                   @Value("${kafka.topic.output}") String outTopic) {

        return builder -> builder
                .stream(inTopic, Consumed.with(Serdes.String(), new JsonSerde<>(SmsMessage.class)))
                .flatMapValues(StreamsStarter::doSomeAction)
                .to(outTopic, Produced.valueSerde(new JsonSerde<>(SmsMessage.class)));

    }
}
