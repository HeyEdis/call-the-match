package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class InputTeamJoinValidator implements Validator {

    private final TeamRepository teamRepository;

    @Override
    public boolean supports(Class<?> klass) {
        return InputTeamJoinDTO.class.isAssignableFrom(klass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InputTeamJoinDTO input = (InputTeamJoinDTO) target;

        if (input.inviteCode() != null && !input.inviteCode().isBlank()
                && !teamRepository.existsByInviteCode(input.inviteCode())) {
            errors.rejectValue("inviteCode", "team.inviteCode.invalid");
        }
    }
}
