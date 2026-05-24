# Call The Match Style Guide

This document captures the homepage direction chosen during design exploration and should be used when restyling the rest of the Thymeleaf pages.

## Design Direction

The app should feel clean, confident, and match-focused. The final homepage combines a soft white page surface with strong dark-green match rows and bright green action accents.

Avoid generic Bootstrap-like tables, hard black borders, heavy boxed dashboards, and busy decorative elements. Let the data rows carry the visual weight.

## Colour Palette

Use these colours consistently.

| Token | Hex | Use |
| --- | --- | --- |
| `--action-green` | `#00e46b` | Primary actions, active highlights, winning/score accents |
| `--action-green-soft` | `#32ff7e` | Button hover, bright score accents, gradients |
| `--ink` | `#081226` | Strong dark text where navy-black is preferred |
| `--page-ink` | `#071a12` | Main H1 text and dark text on light pages |
| `--nav-ink` | `#10203b` | Navbar text |
| `--muted` | `#68788f` | Secondary text fallback |
| `--green-muted` | `#3e6b52` | Homepage/body supporting text on light pages |
| `--row-green` | `#063c25` | Main dark-green data rows |
| `--row-text` | `#f2fff6` | Text on dark-green rows |
| `--row-muted` | `#b7cfc1` | Secondary text on dark-green rows |
| `--row-chip` | `#effff5` | Light button/chip background on dark-green rows |
| `--page-bg` | `#ffffff` | Main page and table/list container background |
| `--error` | `#c52222` | Error messages |
| `--success` | `#009f54` | Success messages |

Do not introduce another green unless the current palette cannot solve the state. If a new colour is needed, document it here.

## Typography

Import fonts once in `main.css`:

```css
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@600;700;800&family=Space+Grotesk:wght@600;700&display=swap');
```

Use fonts by role:

| Font | Use |
| --- | --- |
| `Be Vietnam Pro` | Body text, forms, paragraphs, most page content |
| `Space Grotesk` | Main headings, team names, scorelines, important data |
| `Inter` | Navbar, labels, small metadata, button text |

Rules:

- H1s should be large, bold, black/dark, and set in `Space Grotesk`.
- Body copy should use `Be Vietnam Pro`, with relaxed line height.
- Labels, nav items, metadata, and button text should use `Inter` with strong weight.
- Do not use gradients for H1 text in this direction.

## Page Layout

Use a wide shell:

```css
width: min(100%, 1920px);
margin: 0 auto;
```

Page sections should use generous responsive padding:

```css
padding: clamp(2.5rem, 7vw, 7.5rem) clamp(1rem, 3vw, 3rem) 7.5rem;
```

Keep backgrounds light. The homepage and table container currently use `#ffffff`.

Avoid nested cards. A page can have:

- One main content band or section.
- Repeated row/card items for actual data.
- Form panels where a form needs focus.

## Navbar

The selected navbar style is the light glass style from the homepage:

- Sticky at the top.
- Semi-transparent white background.
- `backdrop-filter: blur(16px)`.
- Dark navy text.
- Register/logout buttons use the green gradient.

Keep navbar links plain and calm. Do not add heavy borders or coloured active blocks unless the whole navigation system is redesigned.

## Match Rows And Data Lists

The homepage schedule is the reference pattern for dense data:

- Container background matches page background.
- Rows are separate rounded items.
- Every row uses the same `--row-green` dark-green background.
- No alternating row colours.
- No row hover colour changes.
- Use spacing between rows instead of table divider lines.
- Secondary row text uses `--row-muted`.
- Score text uses `--action-green-soft`.

The Match button is the only hover feedback in each row:

- Default background: `--row-chip`.
- Hover background: `--action-green-soft`.
- Text remains dark green/black.

## Buttons

Primary buttons:

- Pill-shaped with `border-radius: 999px`.
- Green gradient from `--action-green` to `--action-green-soft`.
- Dark text, usually `#03131d`.
- `Inter`, weight `800`.

Secondary row buttons:

- Light green chip background `--row-chip`.
- Hover to `--action-green-soft`.

Avoid rectangular default browser buttons.

## Forms

Login and registration forms should follow the homepage tone:

- Light page background.
- Focused form panel may use dark green to connect with match rows.
- Inputs should be filled, borderless or ghost-bordered, rounded, and high contrast.
- Labels use `Inter`, uppercase or compact metadata styling.
- Submit button uses the green gradient.
- Error messages use `--error`; success messages use `--success`.

Prefer clear vertical rhythm over dense form grids unless the form is large.

## Borders, Shadows, And Corners

Corners:

- Buttons: `999px`.
- Data rows: around `1rem`.
- Panels: around `1.5rem`.

Borders:

- Avoid hard black borders.
- Avoid table/grid divider lines.
- Use background shifts, spacing, and rounded rows instead.

Shadows:

- Keep shadows soft and subtle.
- Use shadows only when an element needs separation from a light background.

## Responsive Rules

On tablet/mobile:

- Collapse data rows into a single column.
- Keep row spacing.
- Reduce H1 to around `3.15rem`.
- Keep buttons full-width only when needed for touch ergonomics.

Never let long team names or labels overflow their container.

## Current Reference Implementation

The current source of truth is:

- `src/main/resources/templates/home.html`
- `src/main/resources/static/css/main.css`

When restyling other pages, prefer extracting and reusing the existing CSS patterns over introducing page-specific one-offs.
