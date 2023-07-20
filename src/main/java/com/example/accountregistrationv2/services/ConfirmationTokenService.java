package com.example.accountregistrationv2.services;

import com.example.accountregistrationv2.models.ConfirmationToken;
import com.example.accountregistrationv2.repositories.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ConfirmationTokenService {
    @Autowired
    UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    public void SaveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }
    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepository.findByToken(token);
    }
    public int setConfirmedAt(String token){
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
    @Transactional
    public ResponseEntity<String> ConfirmToken(String token){

        //Gets confirmation token by email, if the token does not exist it will throw an exception
        ConfirmationToken confirmationToken = getToken(token).orElseThrow(() -> new IllegalStateException("Token not found"));

        //Checks if confirmation token is already confirmed
        if (confirmationToken.getConfirmedAt() != null){
            return new ResponseEntity<>("Email already confirmed", HttpStatus.BAD_REQUEST);
        } else {
            //Checks if token is expired
            LocalDateTime expiredAt = confirmationToken.getExpiredAt();

            if (expiredAt.isBefore(LocalDateTime.now())){
                return new ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
            }

        }

        //Set token confirmed at to the current date and time
        setConfirmedAt(token);

        //Enables user account after the token has been confirmed
        userService.EnableUser(confirmationToken.getUser().getEmail());

        return new ResponseEntity<>("Account enabled", HttpStatus.OK);

    }
}
