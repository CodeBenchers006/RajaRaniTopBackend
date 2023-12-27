package com.rajaranitop.service;

import com.rajaranitop.beans.CustomNumber;
import com.rajaranitop.beans.LuckyNumber;
import com.rajaranitop.repository.NumberGeneratorRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Log
public class NumberGeneratorService {

    @Autowired
    private NumberGeneratorRepo numberGeneratorRepo;

    private static final long EXPIRATION_DAYS = 7;

    @Scheduled(cron = "0 0 3 * * MON")
    public void deleteOldData() {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(EXPIRATION_DAYS);
        List<LuckyNumber> numbersToDelete = numberGeneratorRepo.findByNumberGenerationDateBefore(expirationTime);
        numberGeneratorRepo.deleteAll(numbersToDelete);
        log.info("Old data deleted successfully.");
    }

    public synchronized int generateLuckyNumber() throws RestClientException {
        ZoneId istTimeZone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime creationTime = ZonedDateTime.now(istTimeZone);

        if (isTimeInRange(creationTime)) {
            if (!isLuckyNumberGeneratedForCurrentDayAndHour(creationTime.getHour())) {
                int number = generateCustomNumber();
                String formattedNumber = String.format("%02d", number);
                saveLuckyNumber(number, creationTime);
                return Integer.parseInt(formattedNumber);
            } else {
                log.info("Number not generated as a number already exists for the current day and hour.");
                return -1;
            }
        } else {
            log.info("Number not generated as the current time is not within the allowed range.");
            return -1;
        }
    }

    /*
    public int generateLuckyNumber() {
        Random random = new Random();
        int number = random.nextInt(100);

        ZoneId istTimeZone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime creationTime = ZonedDateTime.now(istTimeZone);
        creationTime = creationTime.plusSeconds(1);

        if (creationTime.getHour() >= 9 && creationTime.getHour() <= 23 &&
                creationTime.getMinute() == 0 && creationTime.getSecond() == 0) {

            LuckyNumber luckyNumber = new LuckyNumber();
            luckyNumber.setNumber(number);
            luckyNumber.setNumberGenerationDate(creationTime.toLocalDateTime());

            numberGeneratorRepo.save(luckyNumber);
            log.info("Number generated successfully at " + creationTime.toLocalDateTime().toString());

            return number;
        } else {
            log.info("Number not generated as per the specified condition.");
            return -1;
        }
    }
    */

    private boolean isTimeInRange(ZonedDateTime creationTime) {
        return creationTime.getHour() >= 9 && creationTime.getHour() <= 17;
    }

    private int generateCustomNumber() {
//        Random random = new Random();
//        int number = random.nextInt(100);
        RestTemplate restTemplate = new RestTemplate();
        CustomNumber customNumber = restTemplate.getForObject("http://localhost:8080/custom/getCustomNumber", CustomNumber.class);
        return customNumber != null ? customNumber.getCustomNumberData() : 0;
    }

    private void saveLuckyNumber(int number, ZonedDateTime creationTime) {
        LuckyNumber luckyNumber = new LuckyNumber();
        luckyNumber.setNumber(number);
        luckyNumber.setNumberGenerationDate(creationTime.toLocalDateTime());
        numberGeneratorRepo.save(luckyNumber);
        log.info("Number generated successfully at " + creationTime.toLocalDateTime().toString());
    }

    private boolean isLuckyNumberGeneratedForCurrentDayAndHour(int hour) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime startOfCurrentDay = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime startOfCurrentHour = startOfCurrentDay.plusHours(hour);
        LocalDateTime endOfCurrentHour = startOfCurrentHour.plusHours(1);

        List<LuckyNumber> luckyNumbers = numberGeneratorRepo.findByNumberGenerationDateBetween(startOfCurrentHour, endOfCurrentHour);
        return !luckyNumbers.isEmpty();
    }

    public ResponseEntity<Object> getNumber() {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(48);
        List<LuckyNumber> resultList = filterBy24HourExpiration(numberGeneratorRepo.findAll(), expirationTime);
        return resultList.isEmpty()
                ? new ResponseEntity<>("Number not yet generated, check again", HttpStatus.OK)
                : new ResponseEntity<>(resultList.get(resultList.size() - 1), HttpStatus.OK);
    }

    public ResponseEntity<Object> showResultTable() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime startOfDay = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<LuckyNumber> resultList = filterByCurrentDay(numberGeneratorRepo.findAll(), startOfDay, endOfDay);
        return resultList.isEmpty()
                ? new ResponseEntity<>("empty", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    public List<LuckyNumber> getResultsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<LuckyNumber> results = numberGeneratorRepo.findByNumberGenerationDateBetween(startOfDay, endOfDay);
        return filterBy24HourExpiration(results, startOfDay.minusHours(24));
    }

    public ResponseEntity<Object> showAllData() {
        List<LuckyNumber> allData = numberGeneratorRepo.findAll();
        return new ResponseEntity<>(allData, HttpStatus.OK);
    }

    public void deleteAllData() {
        numberGeneratorRepo.deleteAll();
        log.info("data erased");
    }

    private List<LuckyNumber> filterBy24HourExpiration(List<LuckyNumber> numbers, LocalDateTime expirationTime) {
        return numbers.stream()
                .filter(number -> number.getNumberGenerationDate().isAfter(expirationTime))
                .collect(Collectors.toList());
    }

    private List<LuckyNumber> filterByCurrentDay(List<LuckyNumber> numbers, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return numbers.stream()
                .filter(number -> number.getNumberGenerationDate().isAfter(startOfDay) && number.getNumberGenerationDate().isBefore(endOfDay))
                .collect(Collectors.toList());
    }
}
