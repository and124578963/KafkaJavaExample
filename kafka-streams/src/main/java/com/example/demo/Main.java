package com.example.demo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main  {
    public static void main(String[] args){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(KafkaStreamsConfig.class);
    }

}