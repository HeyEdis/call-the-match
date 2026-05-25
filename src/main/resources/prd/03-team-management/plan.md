# Plan: Team Management

> Source PRD: `src/main/resources/prd/03-team-management-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 4: team creation, teampagina and invitecode actions.
2. School guidelines: `Slides_Spring&JPA_mySql.pdf`, `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf` and security guidance.
3. Lesson notes: `13-03-26-Validation.md`, `03-04-2026-MySQL.md` and `Project.md`.
4. Exercise projects identified for JPA, validation and security patterns in the school reference map.
5. Existing `call-the-match` codebase: current team model, member model, invitecode behavior, team service and team screens.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: admin exclusion from teams and authenticated current-user lookup.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: keep the team flow under user-only `/team/**` MVC routes, including `/team/dashboard`, team detail, create, join, owner invitecode regeneration and owner member removal. Public home/ranking/match routes stay separate and admin-only match management does not enter the team route family.
- **Schema**: reuse `Team`, `TeamMember`, `MyUser` and `TeamRole`. `Team` keeps unique name, unique invitecode and owner relation; `TeamMember` remains the membership relation with team role, score and join timestamp.
- **Key models**: `TeamDTO` and team-member view data shape private team screens. Team create and join input use request DTO concepts where form validation belongs rather than binding writable entities or trusting request-provided user ids.
- **Security**: guest and admin actors do not use team participation flows. Current-user lookup resolves team creator and join actor; membership checks guard private detail/score data and owner checks guard invitecode and member-management mutations in service behavior as well as the UI.
- **Validation/i18n**: create and join forms use Jakarta DTO annotations for required input where applicable, plus service/repository checks for persisted uniqueness and invitecode/membership rules. Validation messages, flash feedback and user-facing team form messages live in `src/main/resources/i18n/messages.properties`.
- **REST/WebClient**: this feature stays MVC/Thymeleaf with service/repository boundaries. REST controllers and WebClient demo work remain a separate late block and do not weaken private team access.
- **Testing**: close the feature with MVC tests for team forms/views/redirects, security tests for guest/user/admin/member/owner access, validation tests for DTO annotations, and focused service evidence for membership rules when that is the clearest boundary.

---

## Phase 1: Private Team Dashboard

**User stories**: `8`

### What To Build

Turn the existing dashboard into the first private team slice. `/team/dashboard` stays an MVC user route, but its team list is loaded through membership-aware service/repository data for the authenticated user so the current screen stops exposing every team in the system.

### Acceptance Criteria

- [ ] An authenticated normal user can open `/team/dashboard` and receives team rows loaded through the team service from persisted memberships for that current user.
- [ ] The dashboard no longer renders teams that the current user has not joined or created.
- [ ] Guest access remains blocked by the `/team/**` security boundary, and admin access remains excluded from team participation flows.
- [ ] Dashboard links and summary rows lead only into member-visible team paths without turning private team data into a public route.

---

## Phase 2: Team Creation With Owner Membership

**User stories**: `1`, `2`, `3`, `4`

### What To Build

Complete the create-team path from dashboard form to persisted usable team. The MVC form validates request input, the service resolves the creator from the authenticated user, enforces durable uniqueness rules for name and invitecode, creates the owner membership, and redirects back into the private team flow.

### Acceptance Criteria

- [ ] The team dashboard exposes a Thymeleaf create form backed by a request DTO and a controller path that keeps create decisions in the service layer.
- [ ] Required create input and form errors use existing Jakarta validation annotations on the request DTO with `@Valid`, immediate `BindingResult` handling and bundle-backed messages.
- [ ] A persisted team name remains unique through the service/repository and database boundary, with clear user-facing feedback when a duplicate name is attempted.
- [ ] Invitecode generation produces a code of at least eight characters and checks generated uniqueness before the new team is accepted.
- [ ] The authenticated normal user becomes the created team's owner and receives an owner `TeamMember` relation so the team is immediately visible on the private dashboard.
- [ ] Create behavior never accepts a request-provided owner id, guest creator or admin participant.

---

## Phase 3: Join Team By Invite Code

**User stories**: `5`, `6`, `7`

### What To Build

Finish the existing invitecode join slice. A user submits the dashboard join form, the service resolves the actor from authentication, finds the team through repository-backed invitecode lookup, records a single membership, and returns clear bundle-backed feedback for success and rule failures.

### Acceptance Criteria

- [ ] The dashboard join form submits invitecode input through MVC to service/repository behavior and joins the matched persisted team for the authenticated current user.
- [ ] Join input uses a request DTO validation boundary for required invitecode data with Jakarta annotations and field feedback when the form is empty.
- [ ] An unknown invitecode resolves to clear school-style MVC error or form feedback rather than a raw framework page or silent failure.
- [ ] Duplicate membership is prevented by service/repository membership checks so the same user appears only once in a team.
- [ ] Join success and join failure messages used by the screen come from the resource bundle rather than hardcoded controller strings.
- [ ] Guests and admins remain outside the team join path even though normal users can join through invitecodes.

---

## Phase 4: Private Team Detail And Score View

**User stories**: `9`, `10`, `14`

### What To Build

Make team detail a private scoreboard slice for members. The detail route uses member-aware service decisions before Thymeleaf receives team data, then shows the team identity, owner, membership rows, personal scores and team total without exposing invite or score information to non-members.

### Acceptance Criteria

- [ ] A team member can open the private detail route and see team name, owner and member rows backed by persisted team/member data through the service layer.
- [ ] Each rendered member row includes the personal score data intended for the team page and the team total uses the domain/service score source rather than Thymeleaf-side calculation.
- [ ] A non-member cannot open another team's private page or receive private member score data through the detail MVC path.
- [ ] Guest and admin behavior stays explicit: guests are stopped at team-route security and admins are not admitted as team participants.
- [ ] Missing team identifiers and invalid team detail identifiers resolve through the chosen MVC exception/error handling direction.

---

## Phase 5: Invite Sharing And Owner-Only Member Controls

**User stories**: `11`, `12`, `13`, `14`

### What To Build

Add the invite/share and owner management slice on top of private detail. Members can clearly see and copy the invite code for sharing without any mail service, while owners can invalidate old invitecodes and remove members. Normal members receive a read-only team view for owner controls and the service layer still blocks forged owner-only requests.

### Acceptance Criteria

- [ ] The private team detail page shows the current invite code to team members in a clear share panel.
- [ ] The invite code is rendered in a read-only/copyable field with a small copy button or equivalent simple copy affordance.
- [ ] The share implementation only shows/copies the code; it does not add email delivery, mail templates, external share APIs or invitation persistence.
- [ ] A team owner can regenerate the team's invitecode through a CSRF-protected MVC mutation and receives bundle-backed feedback on the private team screen.
- [ ] Invitecode regeneration enforces owner checks and preserves invitecode uniqueness before the replacement code is used.
- [ ] A team owner can remove a member through an owner-only MVC action that updates persisted membership data and returns to the private team view.
- [ ] Non-owner members do not see owner management controls in Thymeleaf and are denied when they submit forged invitecode or removal requests directly.
- [ ] Owner management decisions remain in service/repository behavior rather than controller or view logic.

---

## Phase 6: MVC, Security And Validation Closure Tests

**User stories**: `1`-`15`

### What To Build

Close the team feature after the private dashboard, create, join, detail and owner slices have settled. Add school-aligned automated evidence for controller/model-view behavior, access rules and DTO validation while keeping REST/WebClient work out of the team block.

### Acceptance Criteria

- [ ] MVC controller tests verify dashboard data is current-user scoped and cover observable create, join, detail and redirect/error behavior introduced by this feature.
- [ ] Security tests verify guest denial, normal user access to team routes, admin exclusion from team participation, non-member detail denial and non-owner owner-action denial.
- [ ] Validation tests using a Jakarta `Validator` cover the team create and join DTO annotations introduced for required input and message behavior.
- [ ] Service-focused tests or equivalent focused evidence cover owner membership creation, duplicate join prevention, invalid invitecode handling and uniqueness-sensitive membership rules where MVC tests would hide the business boundary.
- [ ] MVC/security evidence verifies members can see the invite code share panel, non-members cannot, and invitecode regeneration remains owner-only.
- [ ] Test assertions use status, view, model, redirect, binding and access outcomes instead of brittle visual assertions.
- [ ] The closure phase does not add REST controller tests, WebClient setup or unrelated scoring recalculation work to team management.
