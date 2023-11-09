package com.rajaranitop.repository;

import com.rajaranitop.beans.LuckyNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberGeneratorRepo extends JpaRepository<LuckyNumber,Long> {
}
