package com.example.demo;


import com.example.demo.message.SmsMessage;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Sender {

	private final
	KafkaTemplate<String, SmsMessage> template;
    private AtomicInteger iter = new AtomicInteger(1);

	public Sender(
			KafkaTemplate<String, SmsMessage> template) {
		this.template = template;
	}



//	Здесь происходит отправка
	public void send(SmsMessage smsMessage) {
		int nowIncrement = Config.increment.getAndIncrement();
        String key = String.valueOf(iter.getAndIncrement());
        String msg = String.valueOf(nowIncrement).concat(" ".concat(Main.message));
		smsMessage.setIntErrorText(msg);

		ListenableFuture<SendResult<String, SmsMessage>> future = this.template.send(Config.topic, key, smsMessage);

		try {
			SendResult<String, SmsMessage> result = future.get();

//	Можно полуить метаданные отправленного сообщения
//			System.out.println(result.getRecordMetadata().offset());
//			System.out.println(result.getRecordMetadata().topic());
//			System.out.println(result.getRecordMetadata().partition());

			System.out.println("Отправлено: {'ключ': " + key+ ", 'значение': "  + msg + "}");
			if (Config.maxMessages == nowIncrement){
				System.exit(0);
			}
			send(smsMessage);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			send(smsMessage);

		}



	}

}

