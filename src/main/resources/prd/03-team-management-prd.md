## Problem Statement

Users need to create teams, invite friends through invite codes, join teams, and view private team information. The current application has team entities and partial invite-code behavior, but it still lacks authenticated ownership, team creation, private access checks, and owner-only member management.

## Solution

Build a complete team management flow for authenticated users. A user can create a uniquely named team, receive an invite code, join an existing team with an invite code, view teams they belong to, and see a private team page. Team owners can regenerate invite codes and remove members.

## User Stories

1. As a user, I want to create a team, so that I can compete with friends.
2. As a user, I want my new team to have a unique name, so that teams are easy to identify.
3. As a user, I want my team to receive an automatic invite code of at least 8 characters, so that others can join.
4. As a user, I want to become the owner of the team I create, so that I can manage membership.
5. As a user, I want to join a team by entering an invite code, so that I can participate in a friend's team.
6. As a user, I want a clear error when an invite code is invalid, so that I know why joining failed.
7. As a user, I want duplicate joins to be prevented, so that I do not appear twice in the same team.
8. As a user, I want to see only teams I belong to in my dashboard, so that private team data is not leaked.
9. As a team member, I want to view the team page, so that I can see team information and scores.
10. As a team member, I want to see team name and member list, so that I know who is participating.
11. As a team member, I want the owner to be marked clearly, so that I know who manages the team.
12. As a team member, I want to see personal scores per member, so that I can compare within the team.
13. As a team member, I want to see the total team score, so that I know how the team ranks.
14. As a team owner, I want to regenerate the invite code, so that I can stop old codes from being reused.
15. As a team owner, I want to remove members, so that I can manage my team.
16. As a non-owner member, I should not see or use owner-only actions, so that team management stays controlled.
17. As a non-member, I should not access a private team page, so that team information remains private.

## Implementation Decisions

- Use the authenticated user as owner or joining user.
- Store team ownership separately from membership role, but ensure the creator is also a team member with owner role.
- Keep invite-code generation in the domain or service layer with uniqueness enforced through the repository.
- Validate unique team names.
- Show only teams where the current user is a member.
- Add membership checks before displaying team pages.
- Add owner checks before regenerating invite codes or removing members.
- Use flash messages from resource bundles for join, create, regenerate, remove, and error outcomes.
- Handle invalid invite codes through a controller advice or form-level error flow.

## Testing Decisions

- Service tests should cover team creation, invite-code generation, joining, duplicate join prevention, and owner membership creation.
- Controller tests should verify dashboard, team detail, create, join, regenerate, and remove flows.
- Security tests should verify guests cannot access team pages.
- Authorization tests should verify non-members cannot view private team pages and non-owners cannot perform owner actions.
- Validation tests should cover unique team names and required team name input.

## Out of Scope

- Email invitations.
- Team chat.
- Team logos or profile customization.
- Multiple owners per team.
- Public team detail pages.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3-4: team creation, invite codes, team page, and owner actions.
2. Notes in `Project.md`: DRY, validation, redirect flash messages, and exception handling.
3. Current repository implementation of team model, team service, team controller, and team templates.
4. School JPA examples in WorkspacesIntelij: service-repository separation and JPA query conventions.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

This PRD depends on the access and roles PRD. The temporary hardcoded user id must be removed before this feature can be considered complete.
