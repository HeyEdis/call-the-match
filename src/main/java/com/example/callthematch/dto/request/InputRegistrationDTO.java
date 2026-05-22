package com.example.callthematch.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InputRegistrationDTO(
        @NotBlank(message = "{registration.firstName.required}")
        String firstName,

        @NotBlank(message = "{registration.lastName.required}")
        String lastName,

        @NotBlank(message = "{registration.userName.required}")
        String userName,

        @NotBlank(message = "{registration.email.required}")
        @Email(message = "{registration.email.valid}")
        String email,

        @NotBlank(message = "{registration.password.required}")
        @Size(min = 8, message = "{registration.password.size}")
        String password
) {
    public InputRegistrationDTO() {
        this("", "", "", "", "");
    }
}
