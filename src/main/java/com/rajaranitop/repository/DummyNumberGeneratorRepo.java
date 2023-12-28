package com.rajaranitop.repository;

import com.rajaranitop.beans.DummyLuckyNumberTable;
import com.rajaranitop.beans.LuckyNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DummyNumberGeneratorRepo extends JpaRepository<DummyLuckyNumberTable,Long> {

    List<DummyLuckyNumberTable> findByNumberGenerationDateBefore(LocalDateTime expirationTime);

    List<DummyLuckyNumberTable> findByNumberGenerationDateBetween(LocalDateTime startOfCurrentHour, LocalDateTime endOfCurrentHour);
}
