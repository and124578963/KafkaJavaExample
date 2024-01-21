package com.example.demo;

import org.apache.kafka.streams.StreamsBuilder;

public interface Stream {
    void apply(StreamsBuilder builder);
}
