package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class InputTeamValidator implements Validator {

    private final TeamRepository teamRepository;

    @Override
    public boolean supports(Class<?> klass) {
        return InputTeamDTO.class.isAssignableFrom(klass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InputTeamDTO input = (InputTeamDTO) target;

        if (input.name() != null && !input.name().isBlank()
                && teamRepository.existsByName(input.name())) {
            errors.rejectValue("name", "team.name.duplicate");
        }
    }
}
