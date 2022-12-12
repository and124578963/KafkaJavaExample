package com.example.demo;


import com.example.demo.message.SmsMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Main  {
    public static void main(String[] args){

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        Sender beam = context.getBean(Sender.class);
        SmsMessage smsMessage = new SmsMessage("id не в kafka");
        beam.send(smsMessage);

    }

}