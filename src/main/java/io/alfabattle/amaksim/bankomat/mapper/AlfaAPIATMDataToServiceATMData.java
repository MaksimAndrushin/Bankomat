package io.alfabattle.amaksim.bankomat.mapper;

import io.alfabattle.amaksim.bankomat.model.ATMData;
import io.alfabattle.amaksim.bankomat.model.ServiceATMData;

public class AlfaAPIATMDataToServiceATMData {
    public static ServiceATMData map(ATMData atmData) {
        ServiceATMData serviceATMData = new ServiceATMData();
        serviceATMData.setCity(atmData.getAddress().getCity());
        serviceATMData.setDeviceId(atmData.getDeviceId());
        serviceATMData.setLocation(atmData.getAddress().getLocation());
        serviceATMData.setLatitude(atmData.getCoordinates().getLatitude());
        serviceATMData.setLongitude(atmData.getCoordinates().getLongitude());

        if(atmData.getServices().getPayments() != null
                && !atmData.getServices().getPayments().isEmpty()
                && atmData.getServices().getPayments().equals("Y")) {
            serviceATMData.setPayments(true);
        } else {
            serviceATMData.setPayments(false);
        }

        return serviceATMData;
    }
}
