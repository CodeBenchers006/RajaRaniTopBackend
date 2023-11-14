package com.rajaranitop.service;

import com.rajaranitop.beans.LuckyNumber;
import com.rajaranitop.repository.NumberGeneratorRepo;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log
public class NumberGeneratorService {

    @Autowired
    private NumberGeneratorRepo numberGeneratorRepo;

    // Set the expiration time to 48 hours
    private static final long EXPIRATION_HOURS = 24;

    public int generateLuckyNumber() {
        Random random = new Random();
        int number = random.nextInt(100);

        // Set the time zone to IST
        ZoneId istTimeZone = ZoneId.of("Asia/Kolkata");

        // Get the current date and time in IST
        ZonedDateTime creationTime = ZonedDateTime.now(istTimeZone);

        // Add one second and one millisecond
        creationTime = creationTime.plusSeconds(1);

        // Check if the hour is between 9 and 23 (inclusive) and the minute and second are 0
        if (creationTime.getHour() >= 9 && creationTime.getHour() <= 23 &&
                creationTime.getMinute() == 0 && creationTime.getSecond() == 0) {

            LuckyNumber luckyNumber = new LuckyNumber();
            luckyNumber.setNumber(number);
            luckyNumber.setNumberGenerationDate(creationTime.toLocalDateTime());

            numberGeneratorRepo.save(luckyNumber);
            log.info("Number generated successfully at " + creationTime.toLocalDateTime().toString());

            return number;
        } else {
            // Log or handle the case where the condition is not met
            log.info("Number not generated as per the specified condition.");
            return -1; // or any other value indicating that the number was not generated
        }
    }

    public ResponseEntity<Object> getNumber() {
        List<LuckyNumber> listOfNumbers = filterByExpiration(numberGeneratorRepo.findAll());
        if (listOfNumbers.isEmpty()) {
            return new ResponseEntity<>("Number not yet generated, check again", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(listOfNumbers.get(listOfNumbers.size() - 1), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> showResultTable() {
        List<LuckyNumber> resultList = filterByExpiration(numberGeneratorRepo.findAll());
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("empty", HttpStatus.NOT_FOUND);
        }
    }

    private List<LuckyNumber> filterByExpiration(List<LuckyNumber> numbers) {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(EXPIRATION_HOURS);
        return numbers.stream()
                .filter(number -> number.getNumberGenerationDate().isAfter(expirationTime))
                .collect(Collectors.toList());
    }
}
