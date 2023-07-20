package com.example.accountregistrationv2.controllers;

import com.example.accountregistrationv2.models.ConfirmationToken;
import com.example.accountregistrationv2.models.LoginDTO;
import com.example.accountregistrationv2.models.RegistrationDTO;
import com.example.accountregistrationv2.services.AuthenticationService;
import com.example.accountregistrationv2.services.ConfirmationTokenService;
import com.example.accountregistrationv2.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@Transactional
public class AuthenticationController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDTO body){

        //Checks if the email is a valid email
        if (emailService.EmailIsValid(body.getEmail())){
            return authenticationService.RegisterUser(body.getEmail(), body.getUsername(), body.getPassword());
        }else {
            return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO body){
        return authenticationService.loginUser(body.getEmail(), body.getPassword());
    }
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmAccountRegistrationToken(@RequestParam("token") String token){
        return confirmationTokenService.ConfirmToken(token);
    }
}
