package com.rajaranitop.repository;

import com.rajaranitop.beans.Admin;
import com.rajaranitop.beans.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<AuthenticationToken,Integer> {
    AuthenticationToken findByAdmin(Admin admin);

    AuthenticationToken findByToken(String token);
}
