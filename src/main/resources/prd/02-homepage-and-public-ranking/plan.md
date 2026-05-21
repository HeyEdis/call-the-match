# Plan: Homepage And Public Ranking

> Source PRD: `src/main/resources/prd/02-homepage-and-public-ranking-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 5: home page and public top-10 ranking requirements.
2. `03-04-2026-ErrorMessageEnI18n.md`: resource bundle and fragment guidance.
3. `Project.md`: type mismatch and global exception handling reminders.
4. Current home and ranking implementation in the repository.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Routes**: `/home`, `/ranking`, and `/competition/{id}` remain public.
- **Navigation**: shared navigation becomes role-aware after security is available.
- **Data ordering**: matches sort by date and time; top teams sort by recalculated total score descending.
- **Scores**: official match scores display only when both scores are known.
- **i18n**: resource bundle work is concentrated on the competition add/edit screen, but public labels should still avoid unnecessary hardcoding when touched.
- **Testing timing**: MVC and security coverage is added in the late project test block.

---

## Phase 1: Public Schedule Baseline

**User stories**: 1, 2, 3, 10, 11

### What To Build

Make the public schedule reliable and demoable for guests. It should show all needed match information, sort correctly, and display pending scores cleanly.

### Acceptance Criteria

- [ ] Guest can open `/home`.
- [ ] Matches are sorted by date and time.
- [ ] Match rows show countries, date, time, stadium, and city.
- [ ] Future or unplayed matches do not show confusing null scores.
- [ ] Each match links to public match detail.

---

## Phase 2: Public Match Detail

**User stories**: 3, 10, 11

### What To Build

Keep match detail public while preparing it for role-specific controls. Guests see public match details; users and admins will later see extra controls.

### Acceptance Criteria

- [ ] Guest can open a match detail page.
- [ ] Match detail shows countries, date, time, stadium, city, and official score when known.
- [ ] Missing match ids are handled through a friendly error flow.
- [ ] Invalid path variable values are handled without a raw error page.

---

## Phase 3: Public Top 10 Ranking

**User stories**: 5, 8, 9, 10

### What To Build

Make the public top-10 ranking reflect team totals and member counts. It should be usable by guests and remain compatible with later score recalculation.

### Acceptance Criteria

- [ ] Guest can open `/ranking`.
- [ ] Ranking shows max 10 teams.
- [ ] Ranking is sorted by team total score descending.
- [ ] Each row shows rank, team name, total score, and member count.
- [ ] Ranking uses the same score source as team scoreboards after scoring is implemented.

---

## Phase 4: Role-Aware Navigation

**User stories**: 4, 6, 7, 10

### What To Build

After security exists, update shared navigation so guests, users, and admins see only relevant links.

### Acceptance Criteria

- [ ] Guest sees home, ranking, login, and registration.
- [ ] User sees teams, predictions, and logout.
- [ ] Admin sees match management and logout.
- [ ] Admin does not see user team/prediction actions.

---

## Phase 5: Public MVC Test Closure

**User stories**: 1-12

### What To Build

Add late-stage MVC and security tests for public pages.

### Acceptance Criteria

- [ ] Home controller test covers match list exposure.
- [ ] Ranking controller test covers top-10 ordering.
- [ ] Public match detail test covers model data and not-found handling.
- [ ] Security tests confirm guest access to public routes.
