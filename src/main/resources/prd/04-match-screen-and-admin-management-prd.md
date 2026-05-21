## Problem Statement

The app must show match details to users and allow admins to manage matches and official results. The current application has a match overview and a partial add form, but admin-only access, edit behavior, official result entry, complete validation, converters, and feedback messages are unfinished.

## Solution

Provide a match detail screen for all users with extra prediction context for logged-in users and edit controls for admins. Provide admin-only forms for creating and editing matches and entering official results after a match has been played. Apply all required validation rules and show redirect feedback after successful changes.

## User Stories

1. As anyone, I want to select a match, so that I can inspect its details.
2. As anyone, I want to see land A versus land B, so that I know which teams play.
3. As anyone, I want to see match date and time, so that I know when the match takes place.
4. As anyone, I want to see stadium and city, so that I know where the match takes place.
5. As anyone, I want to see the official final score when known, so that I know the result.
6. As a user, I want to see my own prediction on the match detail screen, so that I know what I submitted.
7. As a user, I want to edit my prediction until one hour before kick-off, so that I can adjust before the deadline.
8. As an admin, I want to see an edit button on match detail screens, so that I can manage match data.
9. As an admin, I want to add a match with land A, land B, date, time, stadium, stadium code, and checksum, so that the schedule can be maintained.
10. As an admin, I want required fields to be validated, so that incomplete matches are rejected.
11. As an admin, I want land A and land B to be different, so that invalid matches are rejected.
12. As an admin, I want match dates to be inside the chosen World Cup period, so that the schedule is realistic.
13. As an admin, I want the checksum to match the stadium code modulo 97, so that the custom validation requirement is met.
14. As an admin, I want two matches at the same time in the same stadium to be rejected, so that the schedule has no location conflicts.
15. As an admin, I want to edit existing match data, so that mistakes can be corrected.
16. As an admin, I want to enter official results only after the match has been played, so that scoring is based on real outcomes.
17. As an admin, I want a success message after adding or editing a match, so that I know the operation succeeded.
18. As an admin, I want validation messages to appear next to the relevant fields, so that I can fix input quickly.

## Implementation Decisions

- Keep match detail public, but render authenticated and admin-only controls conditionally.
- Split match creation/editing input from official result input if that keeps validation simpler.
- Use request DTOs for form input and validation.
- Implement a custom annotation and validator for checksum or cross-field match validity.
- Implement a validator class for rules that require repository access, such as no same stadium and time.
- Register validator classes through controller advice when appropriate.
- Implement converters or formatters so select inputs bind ids to domain entities correctly.
- Let official scores be nullable until entered by an admin.
- Use resource bundles for labels, validation messages, success messages, and date formatting.
- Use exception handling for missing matches and type mismatch ids.

## Testing Decisions

- Validation tests should cover required fields, different countries, date period, checksum, and duplicate stadium-time conflict.
- Controller tests should cover add form, invalid submit, valid submit, edit form, and official result entry.
- Security tests should verify only admins can access add/edit/result management.
- Error handling tests should verify missing match and invalid id behavior.
- Tests should assert external outcomes such as view names, redirects, model attributes, and repository/service calls.

## Out of Scope

- Full tournament bracket generation.
- Automatic official result import.
- Match deletion unless time permits.
- Group standings beyond what is required for predictions and ranking.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 4: match screen and admin match management requirements.
2. Validation notes from `13-03-26-Validation.md`: DTO validation, BindingResult, view errors, and custom validation.
3. Error/i18n notes from `03-04-2026-ErrorMessageEnI18n.md`: type mismatch messages and resource bundles.
4. School Web Flow and i18n examples in WorkspacesIntelij: validators, InitBinder advice, and custom annotations.
5. Current repository implementation of competition model, service, controller, and templates.
6. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

This feature contains several mandatory evaluation points. It should be implemented before visual polish because it demonstrates validation, admin authorization, resource bundles, and error handling.
