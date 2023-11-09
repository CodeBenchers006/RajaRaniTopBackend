package com.rajaranitop.service;

import com.rajaranitop.beans.LuckyNumber;
import com.rajaranitop.repository.NumberGeneratorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

@Service
public class NumberGeneratorService {


    @Autowired
    private NumberGeneratorRepo numberGeneratorRepo;

    public int generateLuckyNumber() {
        Random random = new Random();
        int number = random.nextInt(100);
        ZoneId timeZone = ZoneId.of("GMT+0530");
        LocalDateTime creationTime = LocalDateTime.now();


        LuckyNumber luckyNumber = new LuckyNumber();
        luckyNumber.setNumber(number);
        luckyNumber.setNumberGenerationDate(creationTime.plusSeconds(1));

        numberGeneratorRepo.save(luckyNumber);
        return number;
    }

    public ResponseEntity<Object> getNumber() {
        List<LuckyNumber> listOfNumbers = numberGeneratorRepo.findAll();
        if(listOfNumbers.isEmpty()){

            return new ResponseEntity<>("Number not yet generated check again", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(listOfNumbers.get(listOfNumbers.size()-1), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> showResultTable() {

        List<LuckyNumber> resultList = numberGeneratorRepo.findAll();
        if(!resultList.isEmpty()){
            return new ResponseEntity<>(resultList,HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("empty",HttpStatus.NOT_FOUND);

    }
}
