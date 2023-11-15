package com.rajaranitop.repository;

import com.rajaranitop.beans.LuckyNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NumberGeneratorRepo extends JpaRepository<LuckyNumber,Long> {

    List<LuckyNumber> findByNumberGenerationDateBefore(LocalDateTime expirationTime);

    List<LuckyNumber> findByNumberGenerationDateBetween(LocalDateTime startOfCurrentHour, LocalDateTime endOfCurrentHour);
}
