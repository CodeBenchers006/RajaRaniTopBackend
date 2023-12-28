package com.rajaranitop.repository;

import com.rajaranitop.beans.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin, Integer> {

    public Admin findByUsername(String username);
}
