package com.rajaranitop.controller;


import com.rajaranitop.dto.LoginDTO;
import com.rajaranitop.dto.LoginDTOResponse;
import com.rajaranitop.dto.ResponseDto;
import com.rajaranitop.dto.SignUpDto;
import com.rajaranitop.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {


    @Autowired
    AdminService adminService;

    @PostMapping("/login")
    public LoginDTOResponse loginAdmin(@RequestBody LoginDTO loginDTO){

        return adminService.loginAdmin(loginDTO);

    }

    @PostMapping("/signup")
    public ResponseDto signUpUser(@RequestBody SignUpDto signUpDto){
        return adminService.signUpAdmin(signUpDto);
    }

}
