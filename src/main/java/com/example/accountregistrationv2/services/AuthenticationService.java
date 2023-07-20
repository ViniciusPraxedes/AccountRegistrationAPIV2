package com.example.accountregistrationv2.services;

import com.example.accountregistrationv2.models.*;
import com.example.accountregistrationv2.repositories.ConfirmationTokenRepository;
import com.example.accountregistrationv2.repositories.EmailSenderService;
import com.example.accountregistrationv2.repositories.RoleRepository;
import com.example.accountregistrationv2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService tokenService;

    public ResponseEntity<String> RegisterUser(String email, String username, String password){

        //Encodes password
        String encodedPassword = passwordEncoder.encode(password);

        //Check if user already exists
        boolean userExists = userRepository.findByEmail(email).isPresent();
        if (userExists){
            return new ResponseEntity<>("Email taken", HttpStatus.BAD_REQUEST);
        }

        //Creates user
        Role admin = new Role("ADMIN");
        Role userr = new Role("USER");
        roleRepository.save(admin);
        roleRepository.save(userr);
        Role userRole = roleRepository.findByAuthority("USER").get();
        Role adminRole = roleRepository.findByAuthority("ADMIN").get();
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        roles.add(adminRole);
        User user = new User(username,email,encodedPassword, roles);

        //Creates token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);

        //Saves token to database
        confirmationTokenService.SaveConfirmationToken(confirmationToken);

        //Generates and sends email with token and link to confirm token
        String link = "http://localhost:8080/auth/confirm?token=" + token;
        emailSenderService.sendEmail(user.getEmail(),"Confirm your email","Thank you for testing my application! Press on the link below to confirm your account:\n "+link);

        return new ResponseEntity<>("Email sent", HttpStatus.OK);

    }
    public ResponseEntity<String> loginUser(String email, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Generates a jwt token if the authentication goes well
        String token = tokenService.generateJwt(authentication);

        //Returns an OK response along with the jwt token generated
        return new ResponseEntity<>(token,HttpStatus.OK);
    }


}
