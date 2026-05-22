package com.example.callthematch.service;

import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Role;
import com.example.callthematch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Houdt zich enkel bezig met security!!
    // als je later wil opzoeken waar de gebruiker woont maak nog een service klasse!!!
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: %s".formatted(email)));

        return new User(user.getEmail(), user.getPasswordHash(), convertAuthorities(user.getRole()));
    }

    private Collection<? extends GrantedAuthority> convertAuthorities(Role role) {
        // maar 1 rol dus singletonList teruggeven
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_%s".formatted(role.name())));
    }

}
