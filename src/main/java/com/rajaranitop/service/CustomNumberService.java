package com.rajaranitop.service;

import com.rajaranitop.beans.CustomNumber;
import com.rajaranitop.repository.CustomNumberRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class CustomNumberService {

    @Autowired
    CustomNumberRepo customNumberRepo;

    @Autowired
    AuthenticationService authenticationService;

    /**
     * Sets the custom number after checking if it is in the valid range.
     * If a number already exists, it updates the existing number.
     *
     * @param customNumber The custom number to be set or updated.
     * @return The set or updated custom number.
     */
    public CustomNumber setCustomNumber(CustomNumber customNumber, String token) {

        authenticationService.authenticate(token);

        log.info(String.valueOf(customNumber.getCustomNumberData()));

        // Check if the provided number is within the valid range
        if (!checkNumberInRange(customNumber.getCustomNumberData())) {
            throw new RuntimeException("Number out of range");
        }

        // Check if any number exists and update it
        if (!customNumberRepo.findAll().isEmpty() && checkNumberInRange(customNumber.getCustomNumberData())) {
            CustomNumber existingNumber = customNumberRepo.findById(1).orElseThrow(() -> new RuntimeException("No numbers"));
            existingNumber.setCustomNumberData(customNumber.getCustomNumberData());
            customNumberRepo.save(existingNumber);
            return existingNumber;
        }

        // Save and return the number
        CustomNumber adminGeneratedNumber = new CustomNumber();
        adminGeneratedNumber.setCustomId(1);
        adminGeneratedNumber.setCustomNumberData(customNumber.getCustomNumberData());
        customNumberRepo.save(adminGeneratedNumber);
        return adminGeneratedNumber;
    }

    /**
     * Checks if the given number is within the valid range (0 to 99).
     *
     * @param number The number to be checked.
     * @return true if the number is within the valid range, false otherwise.
     */
    public boolean checkNumberInRange(int number) {
        return number >= 0 && number <= 99;
    }

    /**
     * Retrieves the admin-generated custom number.
     *
     * @return The admin-generated custom number.
     */
    public CustomNumber getAdminGeneratedNumber() {
        List<CustomNumber> customNumberList = customNumberRepo.findAll();

        if (customNumberList.isEmpty()) {
            throw new RuntimeException("No numbers");
        }

        return customNumberList.get(0);
    }

    public CustomNumber generateNewNumber(String token, int data) {
        authenticationService.authenticate(token);

        log.info(String.valueOf(data));

        // Check if the provided number is within the valid range
        if (!checkNumberInRange(data)) {
            throw new RuntimeException("Number out of range");
        }

        // Check if any number exists and update it
        if (!customNumberRepo.findAll().isEmpty() && checkNumberInRange(data)) {
            CustomNumber existingNumber = customNumberRepo.findById(1).orElseThrow(() -> new RuntimeException("No numbers"));
            existingNumber.setCustomNumberData(data);
            customNumberRepo.save(existingNumber);
            return existingNumber;
        }

        // Save and return the number
        CustomNumber adminGeneratedNumber = new CustomNumber();
        adminGeneratedNumber.setCustomId(1);
        adminGeneratedNumber.setCustomNumberData(data);
        customNumberRepo.save(adminGeneratedNumber);
        return adminGeneratedNumber;

    }
}
