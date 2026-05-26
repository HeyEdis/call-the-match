# Handoff: Team Form Validator Advice Refactor

## Context

During the audit, the `TeamController` create/join POST handlers were questioned because they use `try/catch` blocks to turn service exceptions into form field errors.

Current relevant file:

- `src/main/java/com/example/callthematch/controller/TeamController.java`

Current behavior:

- `POST /team/create`
  - validates `InputTeamDTO`
  - calls `teamService.createTeam(inputTeamDTO)`
  - catches `TeamNameAlreadyExists`
  - rejects field `name` with message code `team.name.duplicate`
- `POST /team/join`
  - validates `InputTeamJoinDTO`
  - calls `teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode())`
  - catches `InviteCodeNotFound`
  - rejects field `inviteCode` with message code `team.inviteCode.invalid`

The current code works, but the audit verdict was that these are expected form-validation cases, so they fit better in Spring validator classes wired with `@InitBinder`, following the EWD school pattern.

## Evidence From Guidelines And Exercises

Official source:

- `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Web_Flow.pdf`
- Section `2.3 InitBinder: Validator class`
- The slides say it is not necessary to autowire a validator into the controller or manually call `validate`; instead, register the validator with `@InitBinder` in a `@ControllerAdvice` class so it is applied automatically when using `@Valid`.

Exercise examples:

- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation\src\main\java\com\example\spring_boot_validation\advice\RegistrationValidatorAdvice.java`
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Valid_WebFlow_opgave\src\main\java\com\example\spring_boot_valid_webflow_opgave\advice\AccountValidatorAdvice.java`
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_ErrorMessages\Spring_Boot_i18n_Product2\src\main\java\com\example\spring_boot_i18n_product2\advice\PercentValidatorAdvice.java`

Project guideline reference:

- `.agents/skills/project-guidelines/references/validation-i18n-exceptions.md`
- It explicitly says to use a validator class with advice when validation needs cross-field checks or service/repository state.

Existing project precedent:

- `src/main/java/com/example/callthematch/validator/CompetitionValidator.java`
- `src/main/java/com/example/callthematch/advice/CompetitionValidatorAdvice.java`

## Refactor Goal

Move normal team form rejection logic out of controller `try/catch` blocks and into validators:

- duplicate team name -> `InputTeamValidator`
- unknown invite code -> `InputTeamJoinValidator`

The controller should keep the normal school flow:

1. `@Valid` DTO
2. `BindingResult` immediately after DTO
3. if errors, reload dashboard model data and return `team/dashboard`
4. otherwise call the service and redirect with flash message

## Implementation Plan

### 1. Create `InputTeamValidator`

Create:

- `src/main/java/com/example/callthematch/validator/InputTeamValidator.java`

Suggested shape:

```java
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
```

`TeamRepository` already has:

```java
boolean existsByName(String name);
```

### 2. Create `InputTeamJoinValidator`

Create:

- `src/main/java/com/example/callthematch/validator/InputTeamJoinValidator.java`

Suggested shape:

```java
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
```

`TeamRepository` already has:

```java
boolean existsByInviteCode(String inviteCode);
```

### 3. Create `TeamValidatorAdvice`

Create:

- `src/main/java/com/example/callthematch/advice/TeamValidatorAdvice.java`

Suggested shape:

```java
package com.example.callthematch.advice;

import com.example.callthematch.controller.TeamController;
import com.example.callthematch.validator.InputTeamJoinValidator;
import com.example.callthematch.validator.InputTeamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(assignableTypes = TeamController.class)
@RequiredArgsConstructor
public class TeamValidatorAdvice {

    private final InputTeamValidator inputTeamValidator;
    private final InputTeamJoinValidator inputTeamJoinValidator;

    @InitBinder("inputTeamDTO")
    public void initTeamBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamValidator);
    }

    @InitBinder("inputTeamJoinDTO")
    public void initTeamJoinBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamJoinValidator);
    }
}
```

Important: keep the names in `@InitBinder(...)` exactly aligned with the form DTO model names used by Thymeleaf:

- `inputTeamDTO`
- `inputTeamJoinDTO`

The exercise comments warn that missing binder names can cause validators to run too broadly or duplicate messages.

### 4. Simplify `TeamController`

After the validators are wired, remove these imports if unused:

```java
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNameAlreadyExists;
```

Simplify `createTeam`:

```java
@PostMapping("/create")
public String createTeam(
        @Valid InputTeamDTO inputTeamDTO,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        Locale locale) {
    if (result.hasErrors()) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams());
        model.addAttribute("inputTeamJoinDTO", new InputTeamJoinDTO());
        return "team/dashboard";
    }

    teamService.createTeam(inputTeamDTO);

    redirectAttributes.addFlashAttribute("message",
            messageSource.getMessage("team.create.success", null, locale));
    return "redirect:/team/dashboard";
}
```

Simplify `joinTeam`:

```java
@PostMapping("/join")
public String joinTeam(
        @Valid InputTeamJoinDTO inputTeamJoinDTO,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        Locale locale) {
    if (result.hasErrors()) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams());
        model.addAttribute("inputTeamDTO", new InputTeamDTO());
        return "team/dashboard";
    }

    teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode());

    redirectAttributes.addFlashAttribute("message",
            messageSource.getMessage("team.join.success", null, locale));
    return "redirect:/team/dashboard";
}
```

Keep the service checks as defensive business rules. The validators handle normal form feedback; the service still protects against direct service calls or race conditions.

### 5. Tests To Update Or Add

Controller tests should still pass logically:

- duplicate team name returns `team/dashboard` with field error on `inputTeamDTO.name`
- invalid invite code returns `team/dashboard` with field error on `inputTeamJoinDTO.inviteCode`

However, because the errors now come from validators instead of mocked service exceptions, the `@WebMvcTest(TeamController.class)` setup may need validator beans/imports or mocked repository behavior.

Preferred test approach:

- Add unit tests for `InputTeamValidator`.
- Add unit tests for `InputTeamJoinValidator`.
- Keep controller tests focused on MVC behavior.

Possible controller-test options:

1. Import the validator advice and validators into the MVC test and mock `TeamRepository`.
2. Or keep controller tests simpler and verify invalid field annotations only, while validator unit tests cover duplicate/unknown repository-backed errors.

Follow existing validation test style in:

- `src/test/java/com/example/callthematch/validation/CompetitionValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamJoinDTOValidationTests.java`

## Verification

Run focused tests first:

```powershell
.\mvnw.cmd "-Dtest=TeamControllerTests,InputTeamDTOValidationTests,InputTeamJoinDTOValidationTests" test
```

If new validator tests are added, include them:

```powershell
.\mvnw.cmd "-Dtest=TeamControllerTests,InputTeamDTOValidationTests,InputTeamJoinDTOValidationTests,InputTeamValidatorTests,InputTeamJoinValidatorTests" test
```

Then run full tests if time permits:

```powershell
.\mvnw.cmd test
```

## Suggested Skills

- `project-guidelines`: Use first. This refactor is specifically about matching the EWD validation/advice pattern.
- `diagnose`: Use only if the validator advice does not fire or MVC tests start failing unexpectedly.

## Caution

Do not move this logic into Thymeleaf and do not call repositories from the controller. The school-conform location is:

- repository-backed check inside `Validator`
- validator registered through `@ControllerAdvice` + `@InitBinder`
- controller reads only `BindingResult`
