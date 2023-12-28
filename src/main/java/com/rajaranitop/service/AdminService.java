package com.rajaranitop.service;

import com.rajaranitop.beans.Admin;
import com.rajaranitop.beans.AuthenticationToken;
import com.rajaranitop.dto.LoginDTO;
import com.rajaranitop.dto.LoginDTOResponse;
import com.rajaranitop.dto.ResponseDto;
import com.rajaranitop.dto.SignUpDto;
import com.rajaranitop.repository.AdminRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
public class AdminService {

    private static final Logger logger = LogManager.getLogger(AdminService.class);

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    AuthenticationService aserve;


    public LoginDTOResponse loginAdmin(LoginDTO loginDTO) {

        Admin admin = adminRepo.findByUsername(loginDTO.getUsername());

        if(Objects.isNull(admin)){
            throw new RuntimeException("Authentication failed");
        }

        try{
            if(!admin.getPassword().equals(hashPassword(loginDTO.getPassword()))){
                throw new RuntimeException("Authentication failed");
            }
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        AuthenticationToken authenticationToken=aserve.getToken(admin);

        if(Objects.isNull(authenticationToken)){
            throw new RuntimeException("Authentication failed");
        }

        LoginDTOResponse response = new LoginDTOResponse(admin.getUsername(), authenticationToken.getToken(), "success");
        logger.info("Sign In process completed");
        return response;

    }

    private String hashPassword(String encryptedPassword) throws NoSuchAlgorithmException {

        logger.info("Encrypting the password");

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptedPassword.getBytes());
        byte[] digest= md.digest();
        String hash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return hash;
    }

    @Transactional
    public ResponseDto signUpAdmin(SignUpDto signUpDto) {

        //check if user already exists
        if(Objects.nonNull(adminRepo.findByUsername(signUpDto.getUserName()))){
            //user already exist
            logger.error("User already exist with username {}",signUpDto.getUserName());
            throw new RuntimeException(("user already exists "));
        }

        //hash the password
        String encryptedPassword = signUpDto.getPassword();
        try{
            encryptedPassword = hashPassword(encryptedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //save the user
        Admin newUser = new Admin();
        newUser.setUsername(signUpDto.getUserName());
        newUser.setPassword(encryptedPassword);
        adminRepo.save(newUser);

        //create the token
        final AuthenticationToken authenticationToken = new AuthenticationToken(newUser);
        aserve.saveConfirmationToken(authenticationToken);

        ResponseDto responseDto = new ResponseDto("success","New User is created");
        logger.info("Signup process is completed");
        return responseDto;
    }

}
