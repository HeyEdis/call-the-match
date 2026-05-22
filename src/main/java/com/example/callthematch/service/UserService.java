package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputRegistrationDTO;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Role;
import com.example.callthematch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(InputRegistrationDTO dto) {
        LocalDateTime registeredAt = LocalDateTime.now();

        MyUser user = MyUser.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .userName(dto.userName())
                .email(dto.email())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(Role.USER)
                .createdAt(registeredAt)
                .updatedAt(registeredAt)
                .build();

        userRepository.save(user);
    }
}
