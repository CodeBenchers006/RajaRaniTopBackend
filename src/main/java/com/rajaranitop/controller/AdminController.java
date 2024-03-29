package com.rajaranitop.controller;


import com.rajaranitop.beans.Admin;
import com.rajaranitop.dto.LoginDTO;
import com.rajaranitop.dto.LoginDTOResponse;
import com.rajaranitop.dto.ResponseDto;
import com.rajaranitop.dto.SignUpDto;
import com.rajaranitop.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/updatePassword")
    public ResponseDto updatePassword(@RequestBody SignUpDto signUpDto){
            return adminService.updatePassword(signUpDto);
    }

    @GetMapping("/adminList")
    public List<Admin> showAdmins(){
        return adminService.showAdmins();
    }
}
