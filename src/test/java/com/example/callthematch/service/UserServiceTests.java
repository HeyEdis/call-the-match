package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputRegistrationDTO;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Role;
import com.example.callthematch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

   /* @Test
    void registrationStoresEncodedPasswordAndUserRole() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("team-secret")).thenReturn("encoded-password");

        UserService userService = new UserService(userRepository, passwordEncoder);

        userService.register(new InputRegistrationDTO(
                "Ada",
                "Lovelace",
                "ada",
                "ada@example.com",
                "team-secret"));

        ArgumentCaptor<MyUser> userCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userCaptor.capture());
        verify(passwordEncoder).encode("team-secret");

        MyUser registeredUser = userCaptor.getValue();
        assertThat(registeredUser.getEmail()).isEqualTo("ada@example.com");
        assertThat(registeredUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(registeredUser.getRole()).isEqualTo(Role.USER);
        assertThat(registeredUser.getCreatedAt()).isNotNull();
        assertThat(registeredUser.getUpdatedAt()).isNotNull();
    }*/
}
