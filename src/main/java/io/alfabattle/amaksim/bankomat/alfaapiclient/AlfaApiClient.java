package io.alfabattle.amaksim.bankomat.alfaapiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.alfabattle.amaksim.bankomat.mapper.AlfaAPIATMDataToServiceATMData;
import io.alfabattle.amaksim.bankomat.model.ATMData;
import io.alfabattle.amaksim.bankomat.model.AlfaATMAPIResponce;
import io.alfabattle.amaksim.bankomat.model.ServiceATMData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlfaApiClient {

    @Value("${alfa.api.rest.url}")
    private String restUrl;

    private RestTemplate restTemplate;

    @Autowired
    public AlfaApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<Integer, ServiceATMData> getATMData() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-ibm-client-id", "d6f9189f-ddf1-43f3-8aeb-6e85fdecc62f");
        HttpEntity httpEntity = new HttpEntity(headers);

        Map<Integer, ServiceATMData> atmDataMap = new HashMap<>();

        ResponseEntity<String> result = restTemplate.exchange(
                restUrl, HttpMethod.GET, httpEntity, String.class); //, param);
        if (result.getStatusCodeValue() == HttpStatus.OK.value()) {
            ObjectMapper objectMapper = new ObjectMapper();
            AlfaATMAPIResponce data = null;

            try {
                data = objectMapper.readValue(result.getBody(), new TypeReference<AlfaATMAPIResponce>() {});
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }

            for(ATMData atmData: data.getData().getAtms()){
                ServiceATMData serviceATMData = AlfaAPIATMDataToServiceATMData.map(atmData);
                atmDataMap.put(serviceATMData.getDeviceId(), serviceATMData);
            }
        } else{
            log.error(result.toString());
        }

        return atmDataMap;
    }

}
