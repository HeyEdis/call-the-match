# Ralph TODO: 08-school-conform-test-refactor

Human review items, blockers, and deferred decisions.

## Open Items

- Clean up `AccessSecurityTests` into smaller school-style security cases with `@WithMockUser`, `@WithAnonymousUser`, `formLogin`, `logout`, and CSRF checks while preserving full security wiring.
- Run final targeted controller/security verification and then full `.\mvnw.cmd test` before marking `final-verification-and-plan-alignment` as passed.
