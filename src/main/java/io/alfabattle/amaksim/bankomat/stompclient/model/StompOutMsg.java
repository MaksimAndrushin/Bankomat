package io.alfabattle.amaksim.bankomat.stompclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StompOutMsg {
    private Integer deviceId;
}
