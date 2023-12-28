package com.rajaranitop.service;

import com.rajaranitop.beans.CustomNumber;
import com.rajaranitop.beans.DummyLuckyNumberTable;
import com.rajaranitop.beans.LuckyNumber;
import com.rajaranitop.repository.DummyNumberGeneratorRepo;
import com.rajaranitop.repository.NumberGeneratorRepo;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log
public class DummyNumberGeneratorService {

    @Autowired
    private DummyNumberGeneratorRepo numberGeneratorRepo;

    // Set the expiration time to 7 days
    private static final long EXPIRATION_DAYS = 7;

//    public int generateLuckyNumber() {
//        Random random = new Random();
//        int number = random.nextInt(100);
//
//        // Set the time zone to IST
//        ZoneId istTimeZone = ZoneId.of("Asia/Kolkata");
//
//        // Get the current date and time in IST
//        ZonedDateTime creationTime = ZonedDateTime.now(istTimeZone);
//
//        // Add one second and one millisecond
//        creationTime = creationTime.plusSeconds(1);
//
//        // Check if the hour is between 9 and 23 (inclusive) and the minute and second are 0
//        if (creationTime.getHour() >= 9 && creationTime.getHour() <= 23 &&
//                creationTime.getMinute() == 0 && creationTime.getSecond() == 0) {
//
//            LuckyNumber luckyNumber = new LuckyNumber();
//            luckyNumber.setNumber(number);
//            luckyNumber.setNumberGenerationDate(creationTime.toLocalDateTime());
//
//            numberGeneratorRepo.save(luckyNumber);
//            log.info("Number generated successfully at " + creationTime.toLocalDateTime().toString());
//
//            return number;
//        } else {
//            // Log or handle the case where the condition is not met
//            log.info("Number not generated as per the specified condition.");
//            return -1; // or any other value indicating that the number was not generated
//        }
//    }

    public synchronized int generateLuckyNumber() {
        // Set the time zone to IST
        ZoneId istTimeZone = ZoneId.of("Asia/Kolkata");

        // Get the current date and time in IST
        ZonedDateTime creationTime = ZonedDateTime.now(istTimeZone);

        // Check if the hour is between 9 and 23 (inclusive) and the minute and second are 0
        if (creationTime.getHour() >= 9 && creationTime.getHour() <= 17) {
            // Check if a lucky number already exists for the current day and hour
            if (!isLuckyNumberGeneratedForCurrentDayAndHour(creationTime.getHour())) {
                // Generate a new lucky number
//                Random random = new Random();
//                int number = random.nextInt(100);
                RestTemplate restTemplate = new RestTemplate();

                CustomNumber customNumber = restTemplate.getForObject("https://lotterybackend-wqh2.onrender.com/custom/getCustomNumber", CustomNumber.class);
                int number = customNumber != null ? customNumber.getCustomNumberData() : 0;

                // Format the number to have two digits if it's between 0 and 9
                String formattedNumber = String.format("%02d", number);

                DummyLuckyNumberTable luckyNumber = new DummyLuckyNumberTable();
                luckyNumber.setNumber(number);
                luckyNumber.setNumberGenerationDate(creationTime.toLocalDateTime());

                numberGeneratorRepo.save(luckyNumber);
                log.info("Number generated successfully at " + creationTime.toLocalDateTime().toString());

                return Integer.parseInt(formattedNumber);
            } else {
                // Log or handle the case where a number has already been generated for the current day and hour
                log.info("Number not generated as a number already exists for the current day and hour.");
                return -1; // or any other value indicating that the number was not generated
            }
        } else {
            // Log or handle the case where the current time is not within the allowed range
            log.info("Number not generated as the current time is not within the allowed range.");
            return -1; // or any other value indicating that the number was not generated
        }
    }

    private boolean isLuckyNumberGeneratedForCurrentDayAndHour(int hour) {
        // Get the current date and time in IST
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Check if a lucky number exists for the current day and hour
        LocalDateTime startOfCurrentDay = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime startOfCurrentHour = startOfCurrentDay.plusHours(hour);
        LocalDateTime endOfCurrentHour = startOfCurrentHour.plusHours(1);

        List<DummyLuckyNumberTable> luckyNumbers = numberGeneratorRepo
                .findByNumberGenerationDateBetween(startOfCurrentHour, endOfCurrentHour);

        return !luckyNumbers.isEmpty();
    }


    public ResponseEntity<Object> getNumber() {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(48);
        List<DummyLuckyNumberTable> resultList = filterBy24HourExpiration(numberGeneratorRepo.findAll(), expirationTime);
        if (resultList.isEmpty()) {
            return new ResponseEntity<>("Number not yet generated, check again", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultList.get(resultList.size() - 1), HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> showResultTable() {
        // Get the current date and time in IST
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Get the start and end of the current day
        LocalDateTime startOfDay = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<DummyLuckyNumberTable> resultList = filterByCurrentDay(numberGeneratorRepo.findAll(), startOfDay, endOfDay);
        if (!resultList.isEmpty()) {
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("empty", HttpStatus.NOT_FOUND);
        }
    }

    //Schedule a task to run every Monday at 3 AM to delete old data
    @Scheduled(cron = "0 0 3 * * MON")
    public void deleteOldData() {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(EXPIRATION_DAYS);
        List<DummyLuckyNumberTable> numbersToDelete = numberGeneratorRepo.findByNumberGenerationDateBefore(expirationTime);
        numberGeneratorRepo.deleteAll(numbersToDelete);
        log.info("Old data deleted successfully.");
    }

    private List<DummyLuckyNumberTable> filterBy24HourExpiration(List<DummyLuckyNumberTable> numbers, LocalDateTime expirationTime) {
        return numbers.stream()
                .filter(number -> number.getNumberGenerationDate().isAfter(expirationTime))
                .collect(Collectors.toList());
    }
    private List<DummyLuckyNumberTable> filterByCurrentDay(List<DummyLuckyNumberTable> numbers, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return numbers.stream()
                .filter(number -> number.getNumberGenerationDate().isAfter(startOfDay) && number.getNumberGenerationDate().isBefore(endOfDay))
                .collect(Collectors.toList());
    }

    public List<DummyLuckyNumberTable> getResultsForDate(LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<DummyLuckyNumberTable> results = numberGeneratorRepo.findByNumberGenerationDateBetween(startOfDay, endOfDay);
        return filterBy24HourExpiration(results, startOfDay.minusHours(24));
    }

    public ResponseEntity<Object> showAllData() {

        List<DummyLuckyNumberTable> allData = numberGeneratorRepo.findAll();
        return new ResponseEntity<>(allData,HttpStatus.OK);
    }

    public void deleteAllData(){
        numberGeneratorRepo.deleteAll();
        log.info("data erased");
    }
}
