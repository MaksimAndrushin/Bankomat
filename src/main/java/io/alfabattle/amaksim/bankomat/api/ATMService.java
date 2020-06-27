package io.alfabattle.amaksim.bankomat.api;

import io.alfabattle.amaksim.bankomat.alfaapiclient.AlfaApiClient;
import io.alfabattle.amaksim.bankomat.model.ServiceATMData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class ATMService {

    private Map<Integer, ServiceATMData> atmDataMap;

    private AlfaApiClient alfaApiClient;

    @Autowired
    public ATMService(AlfaApiClient alfaApiClient) {
        this.alfaApiClient = alfaApiClient;
    }

    @PostConstruct
    private synchronized void init() {
        atmDataMap = alfaApiClient.getATMData();
    }

    public ServiceATMData getAtmDataByDeviceId(Integer deviceId) throws Exception {
        if(atmDataMap == null) init();

        if(atmDataMap.containsKey(deviceId)) {
            return atmDataMap.get(deviceId);
        } else {
            throw new Exception("atm not found");
        }
    }

    public ServiceATMData getNearestATM(String latitude, String longitude, Boolean payments) {
        if(atmDataMap == null) init();

        float minLatDiff = Float.MAX_VALUE;
        float minLonDiff = Float.MAX_VALUE;
        ServiceATMData retVal = null;

        float findingLat = Float.parseFloat(latitude);
        float findingLon = Float.parseFloat(longitude);

        for (Map.Entry<Integer, ServiceATMData> entry : atmDataMap.entrySet()) {
            if(payments && !entry.getValue().getPayments()) continue;

            if(entry.getValue().getLatitude() != null && !entry.getValue().getLatitude().isEmpty()
               &&
               entry.getValue().getLongitude() != null && !entry.getValue().getLongitude().isEmpty()) {
                float currLatDiff = Math.abs(Float.parseFloat(entry.getValue().getLatitude()) - findingLat);
                float currLonDiff = Math.abs(Float.parseFloat(entry.getValue().getLongitude()) - findingLon);
                if(minLatDiff > currLatDiff && minLonDiff > currLonDiff) {
                    minLatDiff = currLatDiff;
                    minLonDiff = currLonDiff;
                    retVal = entry.getValue();
                }
            }
        }
        return retVal;

    }



}
