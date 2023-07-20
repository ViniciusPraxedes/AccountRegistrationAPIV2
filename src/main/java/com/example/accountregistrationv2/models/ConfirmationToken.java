package com.example.accountregistrationv2.models;

import com.example.accountregistrationv2.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime generatedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime confirmedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    public ConfirmationToken(String token, LocalDateTime generatedAt, LocalDateTime expiredAt, User user) {
        this.token = token;
        this.generatedAt = generatedAt;
        this.expiredAt = expiredAt;
        this.user = user;
    }
}
