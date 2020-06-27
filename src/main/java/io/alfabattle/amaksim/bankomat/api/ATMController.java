package io.alfabattle.amaksim.bankomat.api;

import io.alfabattle.amaksim.bankomat.model.ServiceATMData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ATMController {

    private ATMService atmService;

    @Autowired
    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    @RequestMapping(path = "/atms/{deviceId}")
    public ResponseEntity getATMData(@PathVariable(name = "deviceId") Integer deviceId,
                                     HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            ServiceATMData atmData = atmService.getAtmDataByDeviceId(deviceId);
            return new ResponseEntity<>(atmData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"status\": \"atm not found\"}", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/atms/nearest")
    public ResponseEntity getNearestATM(
            @RequestParam(name="latitude")String latitude,
            @RequestParam(name="longitude")String longitude,
            @RequestParam(name="payments", defaultValue = "false")Boolean payments,
            HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            ServiceATMData atmData = atmService.getNearestATM(latitude, longitude, payments);
            return new ResponseEntity<>(atmData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"status\": \"atm not found\"}", HttpStatus.NOT_FOUND);
        }
    }

}
