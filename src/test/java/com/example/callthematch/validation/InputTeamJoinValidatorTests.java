package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.repository.TeamRepository;
import com.example.callthematch.validator.InputTeamJoinValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InputTeamJoinValidatorTests {

    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final InputTeamJoinValidator inputTeamJoinValidator = new InputTeamJoinValidator(teamRepository);

    @Test
    void joinTeamRejectsUnknownInviteCode() {
        InputTeamJoinDTO input = new InputTeamJoinDTO("UNKNOWN1");
        when(teamRepository.existsByInviteCode(input.inviteCode())).thenReturn(false);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("inviteCode")).isTrue();
    }

    @Test
    void joinTeamAllowsExistingInviteCode() {
        InputTeamJoinDTO input = new InputTeamJoinDTO("ABCD1234");
        when(teamRepository.existsByInviteCode(input.inviteCode())).thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void joinTeamSkipsRepositoryForBlankInviteCode() {
        Errors errors = validate(new InputTeamJoinDTO(""));

        assertThat(errors.hasErrors()).isFalse();
        verify(teamRepository, never()).existsByInviteCode("");
    }

    private Errors validate(InputTeamJoinDTO input) {
        Errors errors = new BeanPropertyBindingResult(input, "inputTeamJoinDTO");
        inputTeamJoinValidator.validate(input, errors);
        return errors;
    }
}
