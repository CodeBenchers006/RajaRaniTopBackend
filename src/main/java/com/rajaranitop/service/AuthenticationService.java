package com.rajaranitop.service;

import com.rajaranitop.beans.Admin;
import com.rajaranitop.beans.AuthenticationToken;
import com.rajaranitop.repository.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {

    @Autowired
    TokenRepo tokenRepo;

    public void saveConfirmationToken(AuthenticationToken authenticationToken) {
        tokenRepo.save(authenticationToken);
    }

    public AuthenticationToken getToken(Admin admin) {
        return tokenRepo.findByAdmin(admin);
    }

    public Admin getUserfromToken(String token){
        AuthenticationToken authenticationToken = tokenRepo.findByToken(token);
        if(Objects.isNull(authenticationToken)){
            return null;
        }
        else
            return authenticationToken.getAdmin();
    }

    public void authenticate(String token){
        if(Objects.isNull(token)){
            throw new RuntimeException("Token not Present");
        }

        if(Objects.isNull(getUserfromToken(token))){
            throw new RuntimeException("Token not valid");
        }
    }

    public AuthenticationToken getAuthenticationData(String token){
        return tokenRepo.findByToken(token);
    }

}
