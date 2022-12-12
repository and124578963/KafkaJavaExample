package com.example.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CdmMessage extends Message{
    private List<Map<String, Object>> treatments = new ArrayList<>();

    public CdmMessage(@JsonProperty("id") String id) {
        super(id);
    }

}
