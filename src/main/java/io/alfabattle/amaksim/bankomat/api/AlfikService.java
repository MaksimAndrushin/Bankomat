package io.alfabattle.amaksim.bankomat.api;

import io.alfabattle.amaksim.bankomat.model.ServiceATMData;
import io.alfabattle.amaksim.bankomat.stompclient.StompClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

//@Service
public class AlfikService {

    private StompClient stompClient;

    @Autowired
    public AlfikService(StompClient stompClient) {
        this.stompClient = stompClient;
    }

    public Integer getAlfikAmount(Integer deviceId) {
        return stompClient.getAlfikAmountByDeviceId(deviceId);
    }

    public void cacheAllATMAlfik(Map<Integer, ServiceATMData> atmDataMap) {
        for (Map.Entry<Integer, ServiceATMData> entry : atmDataMap.entrySet()) {
            stompClient.processAlfikByDeviceID(entry.getValue().getDeviceId());
        }
    }
}
