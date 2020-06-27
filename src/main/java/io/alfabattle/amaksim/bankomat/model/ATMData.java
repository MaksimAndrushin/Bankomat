package io.alfabattle.amaksim.bankomat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ATMData {
    private Integer deviceId;
    private ATMDataAddress address;
    private ATMDataCoordinates coordinates;
    private ATMDataServices services;

// "payments": false
}
