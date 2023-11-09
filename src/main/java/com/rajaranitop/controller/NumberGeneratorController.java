package com.rajaranitop.controller;

import com.rajaranitop.service.NumberGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lottery")
@CrossOrigin
public class NumberGeneratorController {

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @GetMapping("/generate")
    public int generateLuckyNumber(){
        return numberGeneratorService.generateLuckyNumber();
    }

    @GetMapping( "/getNumber")
    public ResponseEntity<Object> getNumber(){
        return numberGeneratorService.getNumber();
    }

    @GetMapping("/showResult")
    public ResponseEntity<Object> showResultTable(){
        return numberGeneratorService.showResultTable();
    }
}
