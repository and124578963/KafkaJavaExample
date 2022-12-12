package com.example.demo;


import com.example.demo.message.SmsMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@PropertySource(value = "file:./application.properties", ignoreResourceNotFound = true)
@SpringBootApplication
public class Main  {
    public static void main(String[] args){
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        context.getBean("start", context);

    }


}