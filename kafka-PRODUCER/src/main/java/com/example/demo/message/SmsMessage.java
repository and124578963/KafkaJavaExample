package com.example.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SmsMessage extends ChannelMessage  {

    public SmsMessage(@JsonProperty("id") String id) { super(id); }
}
