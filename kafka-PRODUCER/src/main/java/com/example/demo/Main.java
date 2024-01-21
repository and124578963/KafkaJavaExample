package com.example.demo;


import com.example.demo.message.SmsMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Main  {

    public static String message;
    public static void main(String[] args){
        try {

            message = args[0];
            System.out.println(message);
        } catch (Exception e){
            message = "test";
        }

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        Sender beam = context.getBean(Sender.class);
        SmsMessage smsMessage = new SmsMessage("id не в kafka");
        beam.send(smsMessage);

    }

}