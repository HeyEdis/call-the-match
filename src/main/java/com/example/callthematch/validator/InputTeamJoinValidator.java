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
        String inviteCode = input.inviteCode();

        if(inviteCode == null || inviteCode.isBlank()) {
            return;
        }

        if (!teamRepository.existsByInviteCode(inviteCode)) {
            errors.rejectValue("inviteCode", "team.inviteCode.invalid");
        }
    }
}
