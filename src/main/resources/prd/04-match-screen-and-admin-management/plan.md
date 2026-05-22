# Plan: Match Screen And Admin Management

> Source PRD: `src/main/resources/prd/04-match-screen-and-admin-management-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 4 and page 6: match screen, admin match management and technical validation requirements.
2. School guidelines: `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Exceptions.pdf` and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes: `13-03-26-Validation.md`, `03-04-2026-ErrorMessageEnI18n.md` and `Project.md`.
4. Exercise projects identified for validators, form binding, i18n and exception handling in the school reference map.
5. Existing `call-the-match` codebase: current competition domain, DTO annotations, competition MVC flow, messages and error templates.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: chosen date range, admin-only official result management and safe passing first.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: keep public fixture browsing on MVC competition detail routes such as `/competition/{id}` and keep match mutation routes under admin-only add, edit and official-result paths. Public home/ranking and user team/prediction flows stay separate from admin match management.
- **Schema**: reuse `Competition`, `Country`, `Stadium` and `Location`. `Competition` keeps nullable official scores for future matches, a date/time fixture slot and stadium/country relations; stadium code and schedule conflicts are validated before writes rather than patched in views.
- **Key models**: `CompetitionDTO` shapes public match output. Fixture request DTOs carry add/edit input and official result input uses a separate DTO boundary when its score rules differ from fixture rules.
- **Security**: guests and normal users can read public match information. Only admins can create fixtures, correct fixture data or register official results; admin match management does not become team or prediction participation.
- **Validation/i18n**: required fixture input and result-score constraints use existing Jakarta annotations where applicable. Cross-field and repository-backed fixture rules use a validator class, the stadium checksum requirement uses `@ValidStadiumChecksum`, and form labels, validation messages, type mismatch messages and redirect feedback belong in `src/main/resources/i18n/messages.properties`.
- **REST/WebClient**: match data designed here can later feed public REST date/capacity use cases, but this feature remains MVC/Thymeleaf. REST controllers and WebClient demo work stay in the separate late block.
- **Testing**: close the feature with MVC tests for public detail and admin form flows, security tests for public read versus admin-only writes, existing-annotation validation tests, custom annotation tests for stadium checksum and validator-class tests for fixture business rules.

---

## Phase 1: Public Match Screen Baseline

**User stories**: `1`, `2`, `3`, `4`

### What To Build

Finish the public match-detail slice before expanding admin forms. The MVC detail route keeps using service/repository data to render one fixture with complete public context and nullable official result behavior, while leaving a clean integration point for later user prediction context.

### Acceptance Criteria

- [ ] Guests, users and admins can open the public match detail route for an existing competition without entering match mutation behavior.
- [ ] The detail screen receives the selected `CompetitionDTO` data through the controller-to-service boundary rather than direct repository access from MVC presentation code.
- [ ] Both countries, match date, time, stadium and stadium location context render from persisted competition data.
- [ ] Official final score is shown only when both result values are known; future matches keep a clear no-result state.
- [ ] The screen structure leaves later user-owned prediction context possible without implementing prediction writes in this feature slice.

---

## Phase 2: Admin Add Match Form And Binding

**User stories**: `6`, `13`, `14`

### What To Build

Turn the existing add entry point into a reliable admin fixture-create slice. The form binds countries and stadiums from real backend data, required fixture input follows the school DTO validation flow, and successful writes cross the MVC/service/repository path with redirect feedback.

### Acceptance Criteria

- [ ] Only an admin can use the add-match MVC route and mutating form path; guest and normal-user public read access does not grant fixture creation.
- [ ] The add form renders country and stadium options loaded through service/repository-backed data needed by the persisted fixture selection controls.
- [ ] Country and stadium selections bind reliably through a school-style converter, formatter or equivalent DTO binding solution.
- [ ] Required fixture inputs use existing Jakarta Validation annotations on the request DTO with `@Valid`, immediate `BindingResult` handling and Thymeleaf field errors.
- [ ] Invalid add submissions reload select/list model data before returning the form view.
- [ ] A valid add submission persists the fixture through the service/repository boundary and returns redirect feedback from the resource bundle.

---

## Phase 3: Fixture Rules Validator

**User stories**: `7`, `8`, `10`, `14`

### What To Build

Add the validator-class slice for fixture rules that do not belong in simple field annotations. Attach the competition validator to the fixture form flow so add/edit submissions reject impossible countries, out-of-period fixtures and repository-backed stadium/time conflicts with field or form feedback near the input.

### Acceptance Criteria

- [ ] A validator class participates in fixture form validation through the school-style MVC binding/advice pattern where it is needed.
- [ ] Fixture validation rejects the same country chosen for both match sides.
- [ ] Fixture validation rejects match dates outside the project period from `20 May 2026` through `6 June 2026`.
- [ ] Fixture validation checks persisted competition data and rejects conflicting fixtures for the same stadium and match time slot.
- [ ] Validator errors return to the form with bundle-backed messages and the backend-loaded country/stadium options still available.
- [ ] The cross-field and repository-backed rules stay in the validation/service boundary rather than Thymeleaf or controller business logic.

---

## Phase 4: Stadium Checksum Custom Annotation

**User stories**: `9`, `14`

### What To Build

Provide the required custom-annotation validation evidence on the stadium checksum rule. Add `@ValidStadiumChecksum` at the form input boundary chosen for stadium code validation and keep its message and rendering compatible with the admin add/edit forms.

### Acceptance Criteria

- [ ] The project contains a custom `@ValidStadiumChecksum` constraint with a validator that checks the required stadium checksum behavior.
- [ ] The checksum constraint is applied to the fixture form input boundary where stadium code evidence is validated for admin match management.
- [ ] Invalid checksum input produces a bundle-backed validation message near the relevant form input.
- [ ] The custom annotation path stays distinct from the validator-class rules for different countries, date range and stadium/time conflicts.
- [ ] Form handling remains school-conform with DTO validation and `BindingResult` flow when the custom constraint fails.

---

## Phase 5: Admin Edit Fixture Flow

**User stories**: `5`, `11`, `13`, `14`

### What To Build

Make fixture correction a real admin vertical slice. Match screens expose an admin edit entry point, the edit form loads persisted fixture state plus selectable countries/stadiums, the same fixture validation rules protect corrections, and the service updates the stored competition before redirecting with feedback.

### Acceptance Criteria

- [ ] Admin users can reach an edit entry point from the match context while guests and normal users cannot mutate fixture data.
- [ ] The edit form is prefilled from persisted competition data and reloads backend-backed country and stadium choices for valid and invalid submissions.
- [ ] Edit input uses the fixture DTO validation path, including existing Jakarta annotations, validator-class rules and checksum constraint where applicable.
- [ ] A valid edit submission updates fixture data through the service and repository layers rather than requiring reseeding.
- [ ] Invalid edit submissions render field/form errors near inputs and keep the user on the form with selection data restored.
- [ ] Successful edit writes use resource-bundle redirect feedback.

---

## Phase 6: Admin Official Result Flow

**User stories**: `12`, `13`, `14`

### What To Build

Add the official-result slice separately from fixture creation. Admins record official scores after a match using a result-specific MVC form and DTO rules, while fixture creation can keep scores nullable until a result exists for later scoring work.

### Acceptance Criteria

- [ ] Only admins can open and submit the official-result MVC flow for a competition.
- [ ] Official result input uses a dedicated request DTO or equivalent separate validation boundary from fixture add/edit rules.
- [ ] Recorded official scores use existing Jakarta Validation annotations for required and non-negative result values where applicable.
- [ ] Future fixtures can still exist with nullable official scores until an admin records a result.
- [ ] Valid result submission persists through the competition service/repository path and returns bundle-backed redirect feedback.
- [ ] Invalid result submission renders correction feedback near the score inputs without mixing prediction write UI into the admin flow.

---

## Phase 7: Admin Mutation Security And Error Boundaries

**User stories**: `5`, `6`, `11`, `12`, `15`

### What To Build

Close the role and failure boundary around match browsing and administration. Public detail stays readable, admin writes remain protected and missing competition ids or malformed path variables use shared MVC error handling on public and admin paths.

### Acceptance Criteria

- [ ] Security rules make public competition detail readable for guests and users while add, edit and official-result mutations remain admin-only.
- [ ] Guest or normal-user attempts to submit match write actions are denied by Spring Security rather than relying only on hidden navigation controls.
- [ ] Admin match-management access does not introduce prediction or team participation behavior.
- [ ] Missing competition ids on public or admin match routes resolve to friendly MVC not-found behavior.
- [ ] Invalid competition path-variable ids resolve through shared type-mismatch handling and the project error-page direction.
- [ ] Match error handling remains MVC-oriented here and does not add REST error response or WebClient implementation.

---

## Phase 8: Match MVC, Security And Validation Closure Tests

**User stories**: `1`-`15`

### What To Build

Close the match/admin feature with the school-required automated evidence after the match screens and validators are stable. Cover public detail, admin add/edit/result form behavior, role restrictions, required field validation, checksum custom annotation behavior and validator-class rules without pulling the separate REST block into this feature.

### Acceptance Criteria

- [ ] MVC controller tests verify public match detail model/view behavior and admin add, edit and result form success or invalid-submission outcomes.
- [ ] MVC tests cover friendly not-found or type-mismatch behavior for invalid match ids where it is observable at the controller boundary.
- [ ] Security tests verify public read access plus denial of add, edit and result writes for guests and normal users while admins can use match management routes.
- [ ] Validation tests using a Jakarta `Validator` cover existing annotation behavior for required fixture input and official result score constraints introduced by this feature.
- [ ] Custom annotation tests verify `@ValidStadiumChecksum` acceptance and rejection behavior.
- [ ] Validator-class tests verify different-country, project-date-range and stadium/time-conflict rules with the school test shape for validator errors.
- [ ] This closure phase does not add REST controller tests, WebClient setup or prediction-write tests to the MVC admin match feature.
