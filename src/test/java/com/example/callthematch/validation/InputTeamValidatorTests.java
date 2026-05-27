package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.repository.TeamRepository;
import com.example.callthematch.validator.InputTeamValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InputTeamValidatorTests {

    private final TeamRepository teamRepository = mock(TeamRepository.class);
    private final InputTeamValidator inputTeamValidator = new InputTeamValidator(teamRepository);

    @Test
    void createTeamRejectsDuplicateName() {
        InputTeamDTO input = new InputTeamDTO("Existing Team");
        when(teamRepository.existsByName(input.name())).thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("name")).isTrue();
    }

    @Test
    void createTeamAllowsUniqueName() {
        InputTeamDTO input = new InputTeamDTO("New Team");
        when(teamRepository.existsByName(input.name())).thenReturn(false);

        Errors errors = validate(input);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void createTeamSkipsRepositoryForBlankName() {
        Errors errors = validate(new InputTeamDTO(""));

        assertThat(errors.hasErrors()).isFalse();
        verify(teamRepository, never()).existsByName("");
    }

    private Errors validate(InputTeamDTO input) {
        Errors errors = new BeanPropertyBindingResult(input, "inputTeamDTO");
        inputTeamValidator.validate(input, errors);
        return errors;
    }
}
