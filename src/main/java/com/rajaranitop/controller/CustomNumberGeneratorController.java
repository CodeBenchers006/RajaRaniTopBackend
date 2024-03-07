package com.rajaranitop.controller;

import com.rajaranitop.beans.CustomNumber;
import com.rajaranitop.service.CustomNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/custom")
@CrossOrigin
public class CustomNumberGeneratorController {

    @Autowired
    CustomNumberService customNumberService;

    @PostMapping("/generate")
    public CustomNumber setCustomNumber(@RequestBody CustomNumber customNumber, @RequestParam String token){

        return customNumberService.setCustomNumber(customNumber,token);
    }

//    @GetMapping("/generateNewNumber")
//    public CustomNumber generateNewNumber(@RequestParam String token, @RequestParam int data){
//        return customNumberService.generateNewNumber(token,data);
//    }

    @GetMapping("/getCustomNumber")
    public CustomNumber getAdminGeneratedNumber(){
        return customNumberService.getAdminGeneratedNumber();
    }
}
