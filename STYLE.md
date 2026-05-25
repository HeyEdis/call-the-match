# Call The Match Style Guide

This document captures the style decisions made during the homepage, authentication, ranking, match, prediction, and team dashboard design work. Use it when restyling existing pages or creating new Thymeleaf views.

## Creative Direction

Call The Match should feel clean, confident, match-focused, and a little editorial. The chosen direction is not a generic admin dashboard. It combines a light page surface with strong dark-green sports data blocks and bright green action accents.

The interface should feel useful first. Avoid marketing-style landing sections, decorative blobs, nested cards, heavy default tables, and Bootstrap-like controls. Data rows, match cards, and clear form controls should carry the design.

## Colour Palette

Use these colours consistently.

| Token | Hex | Use |
| --- | --- | --- |
| `--action-green` | `#00e46b` | Primary actions, active highlights, important accents |
| `--action-green-soft` | `#32ff7e` | Scores, hover states on row actions, bright metadata on dark green |
| `--ink` | `#081226` | Strong navy-black display text |
| `--page-ink` | `#071a12` | Main dark text on light pages |
| `--nav-ink` | `#10203b` | Navbar text |
| `--muted` | `#68788f` | Secondary text fallback |
| `--green-muted` | `#3e6b52` | Supporting copy on light pages |
| `--row-green` | `#063c25` | Main dark-green rows, hero cards, data blocks |
| `--row-text` | `#f2fff6` | Primary text on dark-green surfaces |
| `--row-muted` | `#b7cfc1` | Secondary text on dark-green surfaces |
| `--row-chip` | `#effff5` | Light chip/button background on dark-green rows |
| `--page-bg` | `#ffffff` | Main page background and table/list container background |
| `--error` | `#c52222` | Validation and error messages |
| `--success` | `#009f54` | Success messages |

Additional accepted colours:

| Hex | Use |
| --- | --- |
| `#f4f7fb` | Soft grey card background on match result/action cards and prediction entry panels |
| `#20252b` | Strong neutral heading text inside light cards |
| `#4e5660` | Secondary copy inside light cards |
| `#dfe6ed` | Soft card border |
| `#a7adb5` | Login/register input border |
| `#b3bac3` | Secondary button border |
| `#eeeeee` | Secondary button hover background |

Do not introduce another green unless the existing palette cannot solve the state. Green should stay meaningful: primary actions, score accents, selected states, and key metadata.

## Typography

Import fonts once in `main.css`:

```css
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@600;700;800&family=Space+Grotesk:wght@600;700&display=swap');
```

Use fonts by role:

| Font | Use |
| --- | --- |
| `Be Vietnam Pro` | Body text, form labels on auth pages, paragraphs, most page content |
| `Space Grotesk` | Main headings, team names, scorelines, rankings, important data |
| `Inter` | Navbar, metadata labels, table headers, button text |

Rules:

- H1s are large, bold, dark/black, and set in `Space Grotesk`.
- Do not use gradient H1 text. If emphasis is needed, use flat `--action-green`.
- Body copy uses `Be Vietnam Pro` with relaxed line height.
- Labels, nav items, table headers, metadata, and buttons use `Inter` with strong weight.
- Do not use negative letter spacing. Keep letter spacing at `0` for normal text and use positive tracking only for small uppercase metadata.

## Layout

Default content pages use a wide shell:

```css
width: min(100%, 1920px);
margin: 0 auto;
```

Default page padding:

```css
padding: clamp(2.5rem, 7vw, 7.5rem) clamp(1rem, 3vw, 3rem) 7.5rem;
```

Exceptions:

- Team dashboard uses a narrower work surface: `width: min(75vw, 1440px)`.
- On mobile, narrow shells should expand to almost full viewport width.
- Match and prediction pages use slightly tighter top padding than the homepage where the page is a tool rather than a hero.

Avoid nested cards. Use cards only for repeated data items, form/action panels, modals, and genuinely framed tools.

## Navbar

Use the selected homepage navbar style:

- Sticky at the top.
- Semi-transparent white background.
- `backdrop-filter: blur(16px)`.
- Dark navy text.
- Plain calm links.
- Register/logout buttons use the green gradient inherited from the navbar pattern.

Do not add heavy borders, bright active blocks, or decorative nav treatments unless the whole navigation system is redesigned.

## Homepage Schedule

The homepage schedule is the reference pattern for dense public data:

- Page and table/list container background are white.
- Rows are separate rounded dark-green items.
- Every row uses `--row-green`; no alternating row colours.
- Row hover must not change the row background.
- Use spacing between rows instead of divider lines.
- Secondary row text uses `--row-muted`.
- Score text uses `--action-green-soft`.
- The Match button is the only row hover feedback.
- Match button default: `--row-chip`.
- Match button hover: `--action-green-soft`.
- Match button text remains dark green/black.

Do not make the homepage table background light green. It should match the page background.

## Buttons

Primary buttons:

- Pill-shaped unless a specific page uses the match-card compact action style.
- `border-radius: 999px`.
- Default background: `--action-green`.
- Dark text: usually `#03131d`.
- `Inter`, weight `800`.
- Hover should not shift to a new green gradient unless the pattern already uses gradients. Prefer stable green with subtle feedback.

Secondary buttons:

- White or very light background.
- Grey border, usually `#b3bac3` or `#98a0aa`.
- Hover background: `#eeeeee`.
- No neon green hover border.

Important button decisions:

- Login submit button is solid `--action-green`; no hover gradient and no shadow.
- Register/sign-up secondary button turns soothing grey on hover.
- Match detail Predict Score button has no drop shadow.
- Homepage row Match button may turn bright green on hover.

## Forms

Authentication forms follow the Goodreads-like centered form direction:

- App name at the top, then the page action title.
- Content centered to the width of the form, not the full viewport.
- Login/register forms use a white page background.
- Inputs are white pills with `2px solid #a7adb5`.
- Input focus changes the border to dark text colour; no green glow/radiance.
- Submit button is solid `--action-green`.
- Secondary auth button is white with grey border and grey hover.
- Keep "Call The Match" on one line.

Auth input reference:

```css
height: 3rem;
border: 2px solid #a7adb5;
border-radius: 999px;
background: #ffffff;
font: 600 1rem 'Be Vietnam Pro', sans-serif;
```

Validation errors:

- Use `--error`.
- Place errors under the field, aligned like helper text.
- Do not render errors inside the input or with large padded boxes.

Registration:

- "Create Account" should be smaller than the first mockup; do not overscale it.
- Do not show the password helper text "Passwords must contain at least 8 characters."

## Ranking Page

The accepted ranking page is the Option B direction:

- Keep only the podium/table design, not the alternate variant.
- Top three teams appear in special cards.
- Top card backgrounds differ to show rank importance.
- The rank number inside top cards is much larger than regular text.
- Rank number is tilted `-5deg`.
- The table under the podium starts from rank 4.
- Table rows use the dark-green row style with visible column headers.

Column headers matter on ranking tables because abbreviating the data into homepage-style cards hides meaning.

## Match Detail Page

The accepted match detail layout:

- Large dark-green hero card on the left.
- Official result card on the right with `#f4f7fb`.
- Below, a compact row of detail/action cards.
- Hero card should show date and time, not stadium.
- Lower cards should include stadium, location, and either prediction action or match controls.
- Cards below the hero should use compact padding around `4px` vertical and enough horizontal padding to avoid looking cramped.
- Use the available page width like the chosen final match reference, not a narrow centered form.

Official result and prediction:

- User prediction belongs in the official result card area when shown with result context.
- Prediction should not be a separate card in that result area.
- On the lower prediction/action card, do not show the predicted score; show the button and helper text only.
- `Adjust your prediction before kickoff.` is shown only for logged-in users with role `User`.

Admin controls:

- The admin card title is exactly `Match Controls`.
- Do not show the user helper text in the admin controls card.

## Prediction Form Page

The accepted prediction form is Option A only:

- Dark-green match card on the left with teams, date, time, and stadium.
- Light grey prediction entry card on the right using `#f4f7fb`.
- Team names and score inputs are large and clear.
- Score input focus can use dark green outline on this score-entry tool.
- Save prediction button is primary green.
- Reset is secondary.
- Losing prototype variants were cut; do not leave variant switchers in production views.

If creating new prediction-like pages, keep the form tool direct and usable rather than adding explanatory marketing copy.

## Team Dashboard

The accepted team dashboard is Option A only:

- Page shell width: `min(75vw, 1440px)`.
- Hero H1 and paragraph use the full shell width; do not cap them with small max-widths.
- Content order is:
  1. Invite code card.
  2. Create a team card.
  3. My Teams section.
- Invite and create cards are stacked vertically.
- Invite code input and button sit on the same row; the button is at the end of the input row, not full-card width.
- Same for the create-team input and button.
- On mobile, form rows may stack for touch ergonomics.
- My Teams heading is left-aligned.
- My Teams list uses the dark-green row/table style.

Rejected dashboard directions:

- Do not keep the Stitch-inspired Option B.
- Do not keep prototype switchers after a direction is chosen.
- Do not use the full `100vw` Stitch layout for the dashboard final.

## Tables And Data Lists

Use true table headers when the column meaning is not obvious, especially ranking and team lists.

Team detail and scoreboard tables:

- Use a rounded white table container with a subtle `rgba(104, 120, 143, 0.14)` outline.
- Header rows use the soft grey `#f4f7fb` background.
- Header cells use muted uppercase Inter labels with positive letter spacing.
- Body cells use generous horizontal and vertical padding, matching the team detail page rhythm.
- Separate body rows with a bottom border of `1px solid rgba(0, 0, 0, 0.4)`.
- Remove the bottom border on the final row.
- Keep important numeric values in `Space Grotesk` with strong weight.

Data rows:

- Prefer separate rounded dark-green rows.
- Use `border-spacing` instead of hard divider lines when using tables.
- Keep row backgrounds stable on hover.
- Use `--action-green-soft` for scores or important numeric values.
- Use `Space Grotesk` for team names and large numeric values.

## Borders, Shadows, Corners

Corners:

- Buttons: `999px`.
- Data rows: around `1rem`.
- Panels/cards: around `1.5rem`.
- Compact match/detail cards may use the same `1.5rem` panel radius.

Borders:

- Avoid hard black borders.
- Avoid table/grid divider lines.
- Use `#dfe6ed` for soft light-card borders.
- Use grey borders for auth inputs and secondary buttons.

Shadows:

- Keep shadows subtle.
- Do not use green shadows behind buttons.
- Remove shadows when the user explicitly wants a flat action, as with the match Predict Score and login button.
- Use ambient shadows only when a light card needs separation.

## Responsive Rules

On tablet/mobile:

- Collapse grids into one column.
- Keep row spacing.
- Reduce H1s to around `3.15rem`, except page-specific tool headings may have their own clamp.
- Buttons can become full width when it improves touch ergonomics.
- Long team names must wrap cleanly and never overflow.
- Text must not overlap controls or following content.

## Prototype Workflow

When using the prototype skill for UI pages:

- Put variants on the same route with a query-param switcher.
- Mark prototype code clearly.
- Once a direction is chosen, delete the losing variants and remove the switcher.
- Absorb the chosen design into the real template/CSS.

Current chosen pages should not keep prototype switchers unless the user is actively comparing variants.

## Current Reference Files

Use these as the source of truth for implemented patterns:

- `src/main/resources/templates/home.html`
- `src/main/resources/templates/account/login.html`
- `src/main/resources/templates/account/register.html`
- `src/main/resources/templates/team/ranking.html`
- `src/main/resources/templates/competition/show.html`
- `src/main/resources/templates/prediction/form.html`
- `src/main/resources/templates/team/dashboard.html`
- `src/main/resources/static/css/main.css`

When restyling other pages, reuse these patterns before introducing page-specific one-offs.
