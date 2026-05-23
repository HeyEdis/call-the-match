# Ralph TODO: 08-school-conform-test-refactor

Human review items, blockers, and deferred decisions.

## Open Items

- Convert `CompetitionControllerTests` to a school-style MVC slice or explicitly justify any retained full integration coverage. Risk area: admin-only add/edit/result routes, validators, message source, not-found/type-mismatch handling, and USER-forbidden admin routes.
- Convert `TeamControllerTests` to a school-style MVC slice or explicitly justify retained full integration coverage. Risk area: current-user membership, owner-only actions, duplicate invite/team flows, and guest/user/admin boundaries.
- Convert `PredictionControllerTests` away from repository setup into mocked service responses where practical. Risk area: current-user membership, cutoff/closed prediction behavior, admin-forbidden and guest redirect behavior.
- Clean up `AccessSecurityTests` into smaller school-style security cases with `@WithMockUser`, `@WithAnonymousUser`, `formLogin`, `logout`, and CSRF checks while preserving full security wiring.
- Run final targeted controller/security verification and then full `.\mvnw.cmd test` before marking `final-verification-and-plan-alignment` as passed.
