package com.rajaranitop.repository;

import com.rajaranitop.beans.CustomNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomNumberRepo extends JpaRepository<CustomNumber,Integer> {
}
