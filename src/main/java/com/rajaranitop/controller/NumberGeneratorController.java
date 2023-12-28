package com.rajaranitop.controller;

import com.rajaranitop.beans.LuckyNumber;
import com.rajaranitop.service.NumberGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/lottery")
@CrossOrigin(origins = "https://lotterybackend-wqh2.onrender.com")
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

    @GetMapping("/showAll")
    public ResponseEntity<Object> showAllData(){
        return numberGeneratorService.showAllData();
    }

    @GetMapping("/resultsByDate")
    public ResponseEntity<Object> showResultTable(@RequestParam("date")
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LuckyNumber> resultList = numberGeneratorService.getResultsForDate(date);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No results found for the specified date", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/manualDeleteOldData")
    public void manualDeleteOldData() {
        numberGeneratorService.deleteOldData();
    }

    @GetMapping("/deleteOldData")
    public void deleteOldData() {
        numberGeneratorService.deleteAllData();
    }
}
