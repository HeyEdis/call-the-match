# Plan: Team Management

> Source PRD: `src/main/resources/prd/03-team-management-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3-4: team creation, invite codes, private team page, and owner actions.
2. `Project.md`: validation, redirect flash messages, and exception handling reminders.
3. `03-04-2026-MySQL.md`: service-repository separation and JPA conventions.
4. Current team model, service, controller, and templates in the repository.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Dependency**: access/accounts must be implemented first because team actions require the authenticated user.
- **Admin scope**: admins do not use team management flows.
- **Routes**: `/team/dashboard`, `/team/{id}`, `/team/create`, `/team/join`, owner-only member and invite-code actions.
- **Schema**: `Team` owns invite code and owner; `TeamMember` stores membership role and score.
- **Ownership**: team creator is both `Team.owner` and a `TeamMember` with owner role.
- **Privacy**: only team members can view private team pages.
- **Testing timing**: service and MVC tests are added in the late project test block.

---

## Phase 1: Authenticated Team Dashboard

**User stories**: 8, 9, 10, 11, 12, 13, 17

### What To Build

Change the team dashboard from all teams to only the teams of the logged-in user. Block guests and admins from the user team workflow.

### Acceptance Criteria

- [ ] Guest is redirected away from team dashboard.
- [ ] Admin cannot use team dashboard as a normal user.
- [ ] User sees only teams where they are a member.
- [ ] Dashboard includes join and create entry points.
- [ ] No hardcoded temporary user id remains in team flows.

---

## Phase 2: Create Team

**User stories**: 1, 2, 3, 4, 14

### What To Build

Implement team creation for a logged-in user with unique name validation, automatic invite code generation, owner assignment, and owner membership creation.

### Acceptance Criteria

- [ ] User can create a team from the dashboard.
- [ ] Team name is required and unique.
- [ ] Invite code is generated automatically and has at least 8 characters.
- [ ] Creator becomes team owner.
- [ ] Creator is also added as owner member.
- [ ] Successful create redirects with a flash message.

---

## Phase 3: Join Team By Invite Code

**User stories**: 5, 6, 7

### What To Build

Implement join-by-code with clear handling for invalid invite codes and duplicate membership.

### Acceptance Criteria

- [ ] User can join a team using a valid invite code.
- [ ] Invalid invite code shows a clear error message.
- [ ] Duplicate membership is prevented.
- [ ] Successful join redirects with a flash message.
- [ ] Join action uses the authenticated user.

---

## Phase 4: Private Team Page And Owner Actions

**User stories**: 9, 10, 11, 12, 13, 14, 15, 16, 17

### What To Build

Complete private team detail access, owner-only invite-code regeneration, and owner-only member removal.

### Acceptance Criteria

- [ ] Team page is accessible only to members.
- [ ] Non-members receive forbidden or friendly denied behavior.
- [ ] Team page shows owner marker, members, personal scores, and total team score.
- [ ] Only owner sees or can use regenerate invite-code action.
- [ ] Only owner can remove non-owner members.
- [ ] Owner cannot accidentally remove themselves if that would orphan the team.

---

## Phase 5: Team Test Closure

**User stories**: 1-17

### What To Build

Add focused tests after team behavior is stable.

### Acceptance Criteria

- [ ] Team service tests cover create, join, duplicate join, invalid code, and owner membership.
- [ ] MVC tests cover dashboard, create, join, and team detail.
- [ ] Security tests cover guest/admin blocking.
- [ ] Authorization tests cover member-only and owner-only behavior.
