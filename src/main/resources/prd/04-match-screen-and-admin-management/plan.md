# Plan: Match Screen And Admin Management

> Source PRD: `src/main/resources/prd/04-match-screen-and-admin-management-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 4: match screen and admin match management requirements.
2. `13-03-26-Validation.md`: DTO validation, BindingResult, field errors, custom validation, and validator classes.
3. `03-04-2026-ErrorMessageEnI18n.md`: type mismatch messages and resource bundles.
4. School Web Flow and i18n examples: custom annotations, InitBinder advice, and validators.
5. Current competition model, service, controller, and templates in the repository.
6. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Admin scope**: only admins manage match data and official results.
- **User scope**: normal users may view public match detail and later prediction controls.
- **Routes**: public `/competition/{id}`; admin-only add/edit/result routes.
- **Dates**: valid match period is 20 May 2026 through 6 June 2026.
- **Scores**: official scores are nullable until entered by admin.
- **Validation**: request DTO annotations, `@ValidStadiumChecksum`, and `CompetitionValidator`.
- **Binding**: country and stadium select fields require converters or formatters.
- **i18n**: competition add/edit is the complete resource-bundle-backed screen.
- **Testing timing**: validator code should be isolated immediately; full tests can be added in the late test block.

---

## Phase 1: Repair Match Form Binding

**User stories**: 9, 10, 17, 18

### What To Build

Fix the existing add form so countries and stadiums bind correctly, official scores are not required at creation, and validation messages attach to the right fields.

### Acceptance Criteria

- [ ] Team A field binds to team A, not team B.
- [ ] Team B field binds to team B.
- [ ] Country and stadium selections convert from ids correctly.
- [ ] Score fields are nullable on match creation.
- [ ] Field errors render beside the correct inputs.

---

## Phase 2: Admin Match Creation

**User stories**: 8, 9, 10, 11, 12, 13, 14, 17, 18

### What To Build

Make match creation an admin-only flow with all required validations and redirect feedback.

### Acceptance Criteria

- [ ] Guest and user cannot access match creation.
- [ ] Admin can open and submit the create form.
- [ ] Required fields are validated.
- [ ] Team A and Team B must differ.
- [ ] Date must be between 20 May 2026 and 6 June 2026.
- [ ] Stadium checksum validates with the custom annotation.
- [ ] Same stadium and same time conflict is rejected.
- [ ] Successful create redirects with a resource-bundle flash message.

---

## Phase 3: Admin Match Editing

**User stories**: 8, 15, 17, 18

### What To Build

Add admin-only editing for existing matches using the same validation and feedback standards as creation.

### Acceptance Criteria

- [ ] Admin can open edit form for an existing match.
- [ ] Missing match routes through friendly error handling.
- [ ] Edit validates the same rules as create.
- [ ] Successful edit redirects with a resource-bundle flash message.
- [ ] Guest and user cannot edit matches.

---

## Phase 4: Official Result Entry

**User stories**: 5, 16

### What To Build

Add admin-only official result entry after the match has passed. This phase prepares the trigger point for prediction scoring.

### Acceptance Criteria

- [ ] Admin can enter official score after match date/time.
- [ ] Future match result entry is rejected.
- [ ] Scores must be non-negative.
- [ ] Public match detail shows official score only when known.
- [ ] Saving official result triggers scoring integration once scoring exists.

---

## Phase 5: Error And I18n Closure

**User stories**: 10, 17, 18

### What To Build

Make competition add/edit the complete resource-bundle-backed screen and handle common MVC errors.

### Acceptance Criteria

- [ ] Competition add/edit labels come from resource bundles.
- [ ] Validation messages come from resource bundles.
- [ ] Success and failure messages come from resource bundles.
- [ ] Type mismatch for invalid ids routes to a friendly error page.
- [ ] Missing match exceptions route to a friendly error page.

---

## Phase 6: Match Management Test Closure

**User stories**: 1-18

### What To Build

Add late-stage MVC, security, and validation tests.

### Acceptance Criteria

- [ ] MVC tests cover add form, invalid submit, valid submit, edit form, and result entry.
- [ ] Security tests cover admin-only access.
- [ ] Validation tests cover standard annotations.
- [ ] Custom annotation test covers checksum.
- [ ] Validator class tests cover country difference, date range, and stadium/time conflict.
