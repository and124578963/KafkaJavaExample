package com.example.demo.message;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ChannelMessage extends CdmMessage {
    @JsonProperty("offers")
    private List<Map<String, Object>> offers = new ArrayList<>();
    @JsonProperty("actions")
    private List<Map<String, Object>> actions = new ArrayList<>();

    @JsonProperty("CTRL_GRP_FLG")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Boolean ctrlGrpFlg;

    @JsonProperty("INT_ERROR_CODE")
    private Integer intErrorCode;
    @JsonProperty("INT_ERROR_TEXT")
    private String intErrorText;

    public ChannelMessage(@JsonProperty("id") String id) {
        super(id);
    }
}
