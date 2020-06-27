package io.alfabattle.amaksim.bankomat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceATMData {
  private Integer deviceId;
  private String latitude;
  private String longitude;
  private String city;
  private String location;
  private Boolean payments;
}
