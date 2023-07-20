package com.example.accountregistrationv2.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
@Service
public class EmailService {
    public boolean EmailIsValid(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
