package com.example.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Message extends RawMessage{
    @JsonProperty("id")
    private final String id;

    public Message(@JsonProperty("id") String id) {
        super();
        this.id = id;
    }

    public Message(Message message) {
        super(message);
        this.id = message.id;
    }
}
