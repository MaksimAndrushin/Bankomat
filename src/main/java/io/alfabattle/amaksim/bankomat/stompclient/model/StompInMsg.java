package io.alfabattle.amaksim.bankomat.stompclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StompInMsg {
    private Integer deviceId;
    private Integer alfik;
}
