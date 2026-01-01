# BibSonomy Design System

**Version:** 1.0.0
**Last Updated:** 2025-12-15
**Status:** Initial Release for Frontend Modernization

---

## Overview

This design system defines the visual language and interaction patterns for the modernized BibSonomy frontend. It transforms the 2010-era interface into a sleek, contemporary research tool while maintaining the academic integrity and information density that scholars expect.

**Goals:**

- Modern, professional aesthetic suitable for academic/research contexts
- WCAG 2.1 AA accessibility compliance
- Optimal information density without overwhelming users
- High performance and responsive across all devices
- Consistent, predictable user experience

---

## 1. Design Principles

### Core Principles

**1. Clarity Over Decoration**

- Every visual element serves a functional purpose
- Prioritize readability and information architecture
- Remove unnecessary ornamentation

**2. Accessibility First**

- WCAG 2.1 AA compliance as minimum standard
- Semantic HTML and ARIA labels
- Keyboard navigation support
- High contrast ratios (4.5:1 for normal text, 3:1 for large text)
- Focus indicators on all interactive elements

**3. Academic Professionalism**

- Sophisticated, mature design language
- Trust-building visual hierarchy
- Information-dense without being cluttered
- Respect for scholarly content and citations

**4. Performance & Speed**

- Fast loading times through optimized assets
- System fonts for instant text rendering
- Minimal animation overhead
- Progressive enhancement approach

**5. Responsive & Adaptive**

- Mobile-first design strategy
- Fluid layouts that adapt to any screen size
- Touch-friendly targets (minimum 44×44px)
- Optimized information display per breakpoint

---

## 2. Color Palette

### Philosophy

A refined, modern palette that moves away from the bright blue (#3366CC) of the 2010 era. Our colors evoke trust, professionalism, and academic rigor while providing excellent accessibility.

### Primary Colors

**Indigo** - Primary brand color for CTAs, links, and key actions

```
indigo-50:  #EEF2FF  (lightest backgrounds)
indigo-100: #E0E7FF  (hover states)
indigo-200: #C7D2FE
indigo-300: #A5B4FC
indigo-400: #818CF8
indigo-500: #6366F1  ← Primary
indigo-600: #4F46E5  ← Primary Dark
indigo-700: #4338CA
indigo-800: #3730A3
indigo-900: #312E81  (darkest text)
indigo-950: #1E1B4B
```

**Usage:**

- `indigo-600`: Primary buttons, active states, important links
- `indigo-700`: Button hover states
- `indigo-500`: Secondary CTAs, badges
- `indigo-100`: Light backgrounds for highlights

### Secondary Colors

**Emerald** - Success states, positive actions

```
emerald-50:  #ECFDF5
emerald-100: #D1FAE5
emerald-500: #10B981  ← Secondary
emerald-600: #059669  ← Secondary Dark
emerald-700: #047857
```

### Neutral Scale

**Slate** - Primary neutral for text, borders, backgrounds

```
slate-50:   #F8FAFC  (page background light)
slate-100:  #F1F5F9  (card background light)
slate-200:  #E2E8F0  (borders light)
slate-300:  #CBD5E1  (borders)
slate-400:  #94A3B8  (disabled text)
slate-500:  #64748B  (secondary text)
slate-600:  #475569  (body text light mode)
slate-700:  #334155  (headings light mode)
slate-800:  #1E293B  (dark backgrounds)
slate-900:  #0F172A  (darkest backgrounds)
slate-950:  #020617  (deepest black)
```

### Semantic Colors

**Error/Danger** - Red

```
red-50:  #FEF2F2
red-100: #FEE2E2
red-500: #EF4444  ← Error
red-600: #DC2626  ← Error Dark
red-700: #B91C1C
```

**Warning** - Amber

```
amber-50:  #FFFBEB
amber-100: #FEF3C7
amber-500: #F59E0B  ← Warning
amber-600: #D97706  ← Warning Dark
amber-700: #B45309
```

**Info** - Sky

```
sky-50:  #F0F9FF
sky-100: #E0F2FE
sky-500: #0EA5E9  ← Info
sky-600: #0284C7  ← Info Dark
sky-700: #0369A1
```

**Success** - Emerald (shared with secondary)

```
emerald-500: #10B981
emerald-600: #059669
```

### Dark Mode Palette

**Background Scale:**

```
dark-bg-primary:   slate-900  (#0F172A)
dark-bg-secondary: slate-800  (#1E293B)
dark-bg-tertiary:  slate-700  (#334155)
dark-bg-elevated:  slate-800  (cards on dark-bg-primary)
```

**Text Scale:**

```
dark-text-primary:   slate-50   (#F8FAFC)
dark-text-secondary: slate-300  (#CBD5E1)
dark-text-tertiary:  slate-400  (#94A3B8)
```

**Border Scale:**

```
dark-border:        slate-700  (#334155)
dark-border-subtle: slate-800  (#1E293B)
```

**Adjustments:**

- Primary colors remain vibrant but slightly desaturated
- Increase indigo brightness in dark mode: use `indigo-400` for primary actions
- Semantic colors use lighter variants (e.g., `red-400` instead of `red-600`)

### Accessibility Guidelines

**Contrast Ratios:**

- Body text (slate-600) on white (#FFFFFF): 7.6:1 ✓
- Headings (slate-700) on white: 10.7:1 ✓
- Primary button (indigo-600) with white text: 7.1:1 ✓
- Links (indigo-600) on white: 8.2:1 ✓
- Error text (red-600) on white: 6.1:1 ✓

**Color Blindness:**

- Never rely on color alone to convey information
- Combine color with icons, labels, or patterns
- Test with contrast checkers and simulators

---

## 3. Typography

### Font Stacks

**Primary Font Stack** (UI Text):

```css
font-family:
  -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif,
  'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
```

**Rationale:** System fonts provide instant rendering, excellent readability, and native feel on each platform.

**Monospace Font Stack** (Code, URLs, Hashes):

```css
font-family:
  ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Monaco, Consolas, 'Liberation Mono',
  'Courier New', monospace;
```

### Type Scale

**Base:** 16px (1rem) - optimal for readability

| Name        | Size            | Line Height    | Weight  | Usage                          |
| ----------- | --------------- | -------------- | ------- | ------------------------------ |
| `text-xs`   | 0.75rem (12px)  | 1rem (16px)    | 400/500 | Captions, metadata timestamps  |
| `text-sm`   | 0.875rem (14px) | 1.25rem (20px) | 400/500 | Secondary text, table cells    |
| `text-base` | 1rem (16px)     | 1.5rem (24px)  | 400     | Body text, paragraphs          |
| `text-lg`   | 1.125rem (18px) | 1.75rem (28px) | 400/500 | Emphasized body text           |
| `text-xl`   | 1.25rem (20px)  | 1.75rem (28px) | 500/600 | Large UI elements, list titles |
| `text-2xl`  | 1.5rem (24px)   | 2rem (32px)    | 600/700 | H4 heading                     |
| `text-3xl`  | 1.875rem (30px) | 2.25rem (36px) | 700     | H3 heading                     |
| `text-4xl`  | 2.25rem (36px)  | 2.5rem (40px)  | 700     | H2 heading                     |
| `text-5xl`  | 3rem (48px)     | 1              | 800     | H1 heading (main pages)        |
| `text-6xl`  | 3.75rem (60px)  | 1              | 800     | Marketing/hero headings        |

### Font Weights

```
font-light:     300  (rarely used)
font-normal:    400  (body text)
font-medium:    500  (emphasized text, labels)
font-semibold:  600  (subheadings, buttons)
font-bold:      700  (headings)
font-extrabold: 800  (large display headings)
```

### Typography Usage Guidelines

**Headings:**

- `h1`: `text-4xl font-bold text-slate-900` (light mode) / `text-slate-50` (dark)
- `h2`: `text-3xl font-bold text-slate-800`
- `h3`: `text-2xl font-semibold text-slate-800`
- `h4`: `text-xl font-semibold text-slate-700`
- `h5`: `text-lg font-semibold text-slate-700`
- `h6`: `text-base font-semibold text-slate-700`

**Body Text:**

- Primary: `text-base text-slate-600`
- Secondary: `text-sm text-slate-500`
- Tertiary/meta: `text-xs text-slate-400`

**Links:**

- Default: `text-indigo-600 hover:text-indigo-700 underline-offset-2`
- Hover: Add `underline` or increase weight
- Visited: `text-indigo-800` (optional)

**Code/Technical:**

- Inline code: `text-sm font-mono bg-slate-100 px-1.5 py-0.5 rounded text-indigo-600`
- Code blocks: `font-mono text-sm bg-slate-900 text-slate-50 p-4 rounded-lg`

**Academic Citations:**

- Author names: `font-medium text-slate-700`
- Titles: `text-base text-slate-900` (may be links)
- Metadata: `text-sm text-slate-500`

### Line Height Guidelines

- **Tight spacing** (`leading-tight`, 1.25): Large headings, compact UI
- **Normal spacing** (`leading-normal`, 1.5): Body text, optimal readability
- **Relaxed spacing** (`leading-relaxed`, 1.625): Long-form content, article text
- **Loose spacing** (`leading-loose`, 2): Poetry, special formatting

---

## 4. Spacing System

### Philosophy

Use an **8px base unit** for consistent, harmonious spacing across the entire interface. This creates a clear vertical rhythm and simplifies layout decisions.

### Spacing Scale

| Token | Value    | Pixels | Usage                        |
| ----- | -------- | ------ | ---------------------------- |
| `0`   | 0        | 0px    | Reset spacing                |
| `px`  | 1px      | 1px    | Borders, fine separators     |
| `0.5` | 0.125rem | 2px    | Micro spacing                |
| `1`   | 0.25rem  | 4px    | Tiny gaps                    |
| `1.5` | 0.375rem | 6px    | Small inline spacing         |
| `2`   | 0.5rem   | 8px    | ← Base unit, compact spacing |
| `3`   | 0.75rem  | 12px   | Small component padding      |
| `4`   | 1rem     | 16px   | Standard component padding   |
| `5`   | 1.25rem  | 20px   | Medium spacing               |
| `6`   | 1.5rem   | 24px   | Large component padding      |
| `8`   | 2rem     | 32px   | Section spacing              |
| `10`  | 2.5rem   | 40px   | Large section spacing        |
| `12`  | 3rem     | 48px   | Extra large spacing          |
| `16`  | 4rem     | 64px   | Major section dividers       |
| `20`  | 5rem     | 80px   | Page-level spacing           |
| `24`  | 6rem     | 96px   | Hero spacing                 |

### Component Spacing Guidelines

**Buttons:**

- Small: `px-3 py-1.5` (12px × 6px)
- Medium: `px-4 py-2` (16px × 8px)
- Large: `px-6 py-3` (24px × 12px)

**Cards:**

- Padding: `p-6` (24px) standard, `p-4` (16px) compact
- Gap between cards: `gap-4` or `gap-6`

**Form Elements:**

- Input padding: `px-3 py-2` (12px × 8px)
- Label margin: `mb-1.5` (6px)
- Field gap: `space-y-4` (16px between fields)

**Lists:**

- List item padding: `py-3 px-4` (12px × 16px)
- Gap between items: `space-y-2` or border separators

**Page Layout:**

- Container padding: `px-4 md:px-6 lg:px-8`
- Section vertical spacing: `space-y-8` or `space-y-12`
- Content max-width: `max-w-7xl` (1280px)

### Vertical Rhythm

Maintain consistent vertical rhythm using multiples of 4px or 8px:

```
Heading → mb-2 (8px)
Paragraph → mb-4 (16px)
Section → mb-8 (32px)
Major Section → mb-12 (48px)
```

---

## 5. Shadows & Elevation

### Philosophy

Subtle, layered shadows that create depth without overwhelming. Use shadows sparingly to establish hierarchy and draw attention to elevated content.

### Shadow Scale

```css
/* Tailwind Shadows */
shadow-sm:   0 1px 2px 0 rgb(0 0 0 / 0.05)
             /* Subtle borders, slightly raised elements */

shadow:      0 1px 3px 0 rgb(0 0 0 / 0.1),
             0 1px 2px -1px rgb(0 0 0 / 0.1)
             /* Default cards, containers */

shadow-md:   0 4px 6px -1px rgb(0 0 0 / 0.1),
             0 2px 4px -2px rgb(0 0 0 / 0.1)
             /* Elevated cards, hover states */

shadow-lg:   0 10px 15px -3px rgb(0 0 0 / 0.1),
             0 4px 6px -4px rgb(0 0 0 / 0.1)
             /* Dropdowns, popovers */

shadow-xl:   0 20px 25px -5px rgb(0 0 0 / 0.1),
             0 8px 10px -6px rgb(0 0 0 / 0.1)
             /* Modals, dialogs */

shadow-2xl:  0 25px 50px -12px rgb(0 0 0 / 0.25)
             /* High-elevation modals */
```

### Usage Guidelines

**Level 0** (No shadow): Flat UI elements, inline content

- `shadow-none`

**Level 1** (Subtle): Resting cards, containers

- `shadow-sm` or `shadow`
- Example: Post cards, sidebar panels

**Level 2** (Raised): Interactive hover states, active dropdowns

- `shadow-md`
- Example: Card hover, button hover (optional)

**Level 3** (Floating): Dropdowns, tooltips, popovers

- `shadow-lg`
- Example: Autocomplete dropdowns, tag suggestions

**Level 4** (Overlay): Modals, dialogs, important overlays

- `shadow-xl` or `shadow-2xl`
- Example: Login modal, confirmation dialog

### Dark Mode Shadows

In dark mode, shadows are less effective. Enhance with borders:

```css
/* Dark mode elevated card */
.dark .card-elevated {
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.3);
  border: 1px solid theme('colors.slate.700');
}
```

---

## 6. Border Radius

### Philosophy

Modern, approachable roundness without being overly playful. Consistent rounding creates visual cohesion.

### Radius Scale

```css
rounded-none:   0px        /* Sharp corners, tables, borders */
rounded-sm:     0.125rem   (2px)  /* Subtle rounding, tags */
rounded:        0.25rem    (4px)  /* Default, inputs, buttons */
rounded-md:     0.375rem   (6px)  /* Medium, cards */
rounded-lg:     0.5rem     (8px)  /* Large, prominent cards */
rounded-xl:     0.75rem    (12px) /* Extra large, modals */
rounded-2xl:    1rem       (16px) /* Very large, hero elements */
rounded-3xl:    1.5rem     (24px) /* Extreme (rare) */
rounded-full:   9999px     /* Circles, pills, avatars */
```

### Component Rounding

| Component           | Rounding    | Class                        |
| ------------------- | ----------- | ---------------------------- |
| Buttons (default)   | 4px         | `rounded`                    |
| Buttons (pill)      | Full        | `rounded-full`               |
| Input fields        | 4px         | `rounded`                    |
| Cards               | 6-8px       | `rounded-md` or `rounded-lg` |
| Modals              | 12px        | `rounded-xl`                 |
| Badges/Tags         | 2-4px       | `rounded-sm` or `rounded`    |
| Avatars             | Full        | `rounded-full`               |
| Images (thumbnails) | 4px         | `rounded`                    |
| Tooltips            | 6px         | `rounded-md`                 |
| Dropdowns           | 6px         | `rounded-md`                 |
| Tables              | 0px (sharp) | `rounded-none`               |

### Guidelines

- **Consistency:** Use the same rounding for similar components
- **Hierarchy:** Larger elements can have more rounding
- **Nested rounding:** Inner elements should be slightly less rounded than containers

---

## 7. Transitions & Animations

### Philosophy

Subtle, purposeful motion that enhances usability without distraction. Animations should feel fast and responsive, never sluggish.

### Duration Scale

```css
duration-75:   75ms   /* Instant feedback, checkboxes */
duration-100:  100ms  /* Fast, hover states */
duration-150:  150ms  /* Default, most transitions */
duration-200:  200ms  /* Moderate, dropdowns */
duration-300:  300ms  /* Slow, modals, page transitions */
duration-500:  500ms  /* Very slow (rare), large animations */
duration-700:  700ms  /* Extra slow (very rare) */
duration-1000: 1000ms /* Decorative only */
```

### Easing Functions

```css
ease-linear:     linear             /* Constant speed (progress bars) */
ease-in:         cubic-bezier(0.4, 0, 1, 1)     /* Accelerating */
ease-out:        cubic-bezier(0, 0, 0.2, 1)     /* Decelerating (default) */
ease-in-out:     cubic-bezier(0.4, 0, 0.2, 1)   /* Smooth start/end */
```

**Default:** Use `ease-out` for most transitions (feels snappy)

### Common Transition Patterns

**Hover States:**

```css
/* Button hover */
transition:
  background-color 150ms ease-out,
  transform 150ms ease-out;

/* Link hover */
transition: color 100ms ease-out;
```

**Focus States:**

```css
/* Input focus (instant) */
transition:
  border-color 75ms ease-out,
  box-shadow 75ms ease-out;
```

**Dropdowns/Modals:**

```css
/* Fade in + scale */
transition:
  opacity 200ms ease-out,
  transform 200ms ease-out;
transform: scale(0.95);
opacity: 0;

/* Open state */
transform: scale(1);
opacity: 1;
```

**Tooltips:**

```css
/* Fast fade */
transition: opacity 150ms ease-out;
```

### Animation Principles

1. **Prefer CSS transitions** over JavaScript animations
2. **Animate cheap properties:** `opacity`, `transform` (avoid `width`, `height`, `margin`)
3. **Use `will-change` sparingly** for complex animations
4. **Respect user preferences:**
   ```css
   @media (prefers-reduced-motion: reduce) {
     * {
       animation-duration: 0.01ms !important;
       transition-duration: 0.01ms !important;
     }
   }
   ```
5. **Test performance:** Animations should run at 60fps

### Tailwind Transition Utilities

```html
<!-- Default transition (all properties, 150ms) -->
<button class="transition hover:bg-indigo-700">
  <!-- Specific properties -->
  <button class="transition-colors duration-200 hover:bg-indigo-700">
    <!-- Multiple properties -->
    <div class="transition-all duration-300 ease-in-out hover:scale-105"></div>
  </button>
</button>
```

---

## 8. Breakpoints

### Philosophy

Mobile-first responsive design. Start with mobile layout, progressively enhance for larger screens. Optimize content density and interaction patterns per device class.

### Breakpoint Scale

| Breakpoint     | Min Width | Target Devices                   | Usage              |
| -------------- | --------- | -------------------------------- | ------------------ |
| `xs` (default) | 0px       | Mobile portrait                  | Default, no prefix |
| `sm`           | 640px     | Mobile landscape, phablets       | `sm:` prefix       |
| `md`           | 768px     | Tablets portrait                 | `md:` prefix       |
| `lg`           | 1024px    | Tablets landscape, small laptops | `lg:` prefix       |
| `xl`           | 1280px    | Desktops, large laptops          | `xl:` prefix       |
| `2xl`          | 1536px    | Large desktops, wide monitors    | `2xl:` prefix      |

### Tailwind Implementation

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    screens: {
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      '2xl': '1536px',
    },
  },
}
```

### Responsive Patterns

**Container Max Widths:**

```html
<div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
  <!-- Content -->
</div>
```

**Grid Layouts:**

```html
<!-- 1 col mobile, 2 cols tablet, 3 cols desktop -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"></div>
```

**Typography Scaling:**

```html
<h1 class="text-3xl sm:text-4xl lg:text-5xl font-bold"></h1>
```

**Navigation:**

```html
<!-- Mobile: hamburger menu; Desktop: horizontal nav -->
<nav class="lg:flex lg:space-x-8 hidden">
  <button class="lg:hidden">☰</button>
</nav>
```

**Sidebars:**

```html
<!-- Mobile: full width; Desktop: sidebar + content -->
<div class="flex flex-col lg:flex-row">
  <aside class="lg:w-64">Sidebar</aside>
  <main class="flex-1">Content</main>
</div>
```

### Content Density Guidelines

**Mobile (xs-sm):**

- Single column layouts
- Larger touch targets (min 44×44px)
- Simplified navigation (hamburger menu)
- Condensed information (show less metadata)
- Vertically stacked forms

**Tablet (md):**

- Two-column grids where appropriate
- Side-by-side forms (label + input)
- Persistent navigation (if space allows)
- More metadata visible

**Desktop (lg+):**

- Multi-column layouts (3-4 columns)
- Sidebars for filters/navigation
- Expanded metadata and secondary actions
- Hover states and tooltips
- Keyboard shortcuts

---

## 9. Iconography

### Recommended Icon System

**Primary Choice: [Heroicons](https://heroicons.com/)**

**Rationale:**

- Designed by Tailwind Labs (perfect integration)
- Two styles: Outline (default) and Solid (emphasis)
- Consistent 24×24px or 20×20px base size
- Excellent accessibility
- Open source (MIT license)
- Available as Vue components via `@heroicons/vue`

**Alternative Options:**

- **[Lucide](https://lucide.dev/)** - Fork of Feather Icons, more icons
- **[Phosphor Icons](https://phosphoricons.com/)** - Six weights, large library
- **[Tabler Icons](https://tabler-icons.io/)** - Clean, consistent, large set

### Icon Sizes

| Size | Class       | Pixels | Usage                            |
| ---- | ----------- | ------ | -------------------------------- |
| XS   | `w-3 h-3`   | 12px   | Inline with small text           |
| SM   | `w-4 h-4`   | 16px   | Inline with body text, badges    |
| Base | `w-5 h-5`   | 20px   | Default UI icons, buttons        |
| MD   | `w-6 h-6`   | 24px   | Larger buttons, standalone icons |
| LG   | `w-8 h-8`   | 32px   | Feature icons, empty states      |
| XL   | `w-12 h-12` | 48px   | Large feature icons              |
| 2XL  | `w-16 h-16` | 64px   | Hero icons, splash screens       |

### Usage Guidelines

**Inline with Text:**

```html
<span class="inline-flex items-center gap-1.5">
  <BookmarkIcon class="w-4 h-4" />
  <span>Bookmark</span>
</span>
```

**Icon Buttons:**

```html
<button class="p-2 rounded hover:bg-slate-100" aria-label="Edit">
  <PencilIcon class="w-5 h-5" />
</button>
```

**Leading Icon in Button:**

```html
<button class="flex items-center gap-2 px-4 py-2">
  <PlusIcon class="w-5 h-5" />
  <span>Add Post</span>
</button>
```

**Decorative vs. Semantic:**

- Decorative icons: `aria-hidden="true"` (icon duplicates adjacent text)
- Semantic icons: `aria-label="Description"` (icon-only buttons)

### Icon Colors

Match text color for consistency:

```html
<!-- Inherit text color -->
<StarIcon class="w-5 h-5 text-current" />

<!-- Specific color -->
<HeartIcon class="w-5 h-5 text-red-500" />

<!-- State colors -->
<CheckCircleIcon class="w-5 h-5 text-emerald-600" />
<XCircleIcon class="w-5 h-5 text-red-600" />
<InformationCircleIcon class="w-5 h-5 text-sky-600" />
```

### Common Icons for BibSonomy

| Action/Concept       | Icon Name (Heroicons)            |
| -------------------- | -------------------------------- |
| Bookmark             | `BookmarkIcon`                   |
| Publication/Document | `DocumentTextIcon`               |
| Tag                  | `TagIcon`                        |
| User                 | `UserIcon`                       |
| Group                | `UserGroupIcon`                  |
| Search               | `MagnifyingGlassIcon`            |
| Filter               | `FunnelIcon`                     |
| Add/Create           | `PlusIcon`, `PlusCircleIcon`     |
| Edit                 | `PencilIcon`, `PencilSquareIcon` |
| Delete               | `TrashIcon`                      |
| Share                | `ShareIcon`                      |
| Export               | `ArrowDownTrayIcon`              |
| Settings             | `Cog6ToothIcon`                  |
| Logout               | `ArrowRightOnRectangleIcon`      |
| Home                 | `HomeIcon`                       |
| Menu                 | `Bars3Icon`                      |
| Close                | `XMarkIcon`                      |
| Check/Success        | `CheckIcon`, `CheckCircleIcon`   |
| Error                | `XCircleIcon`                    |
| Warning              | `ExclamationTriangleIcon`        |
| Info                 | `InformationCircleIcon`          |
| External Link        | `ArrowTopRightOnSquareIcon`      |
| Calendar             | `CalendarIcon`                   |
| Link                 | `LinkIcon`                       |

---

## 10. Design Tokens

### Philosophy

Design tokens create a single source of truth for design decisions. They decouple semantic intent from visual implementation, enabling theming and consistency.

### Token Structure

**Naming Convention:**

```
[category]-[property]-[variant]-[state]

Examples:
color-bg-primary
color-text-secondary
spacing-component-sm
shadow-elevation-2
```

### Tailwind Configuration

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        // Semantic token mapping
        primary: {
          DEFAULT: '#4F46E5', // indigo-600
          dark: '#4338CA', // indigo-700
          light: '#6366F1', // indigo-500
        },
        text: {
          primary: '#475569', // slate-600
          secondary: '#64748B', // slate-500
          tertiary: '#94A3B8', // slate-400
        },
        bg: {
          primary: '#FFFFFF',
          secondary: '#F8FAFC', // slate-50
          tertiary: '#F1F5F9', // slate-100
        },
      },
      spacing: {
        // Custom spacing if needed
        safe: '1rem', // Safe area for mobile
      },
      fontSize: {
        // Custom type scale additions
      },
      borderRadius: {
        // Custom radius values
      },
      boxShadow: {
        // Custom shadows
        focus: '0 0 0 3px rgba(99, 102, 241, 0.2)',
      },
    },
  },
  plugins: [],
}
```

### CSS Custom Properties (CSS Variables)

For runtime theming (light/dark mode):

```css
/* globals.css */
:root {
  /* Colors */
  --color-primary: 79 70 229; /* RGB for indigo-600 */
  --color-bg-primary: 255 255 255;
  --color-text-primary: 71 85 105;

  /* Spacing */
  --spacing-unit: 0.25rem; /* 4px */

  /* Shadows */
  --shadow-default: 0 1px 3px 0 rgb(0 0 0 / 0.1);

  /* Borders */
  --border-radius-default: 0.25rem;
}

.dark {
  --color-primary: 129 140 248; /* indigo-400 */
  --color-bg-primary: 15 23 42; /* slate-900 */
  --color-text-primary: 248 250 252; /* slate-50 */
}
```

**Usage in Tailwind:**

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: 'rgb(var(--color-primary) / <alpha-value>)',
      },
    },
  },
}
```

```html
<!-- Usage -->
<div class="bg-primary text-white"></div>
```

### Semantic Token Mapping

**Text Tokens:**

```
text-primary    → slate-600 (light) / slate-50 (dark)
text-secondary  → slate-500 (light) / slate-300 (dark)
text-tertiary   → slate-400 (light) / slate-400 (dark)
text-disabled   → slate-400 (light) / slate-600 (dark)
text-link       → indigo-600 (light) / indigo-400 (dark)
text-error      → red-600 (light) / red-400 (dark)
text-success    → emerald-600 (light) / emerald-400 (dark)
```

**Background Tokens:**

```
bg-primary      → white (light) / slate-900 (dark)
bg-secondary    → slate-50 (light) / slate-800 (dark)
bg-tertiary     → slate-100 (light) / slate-700 (dark)
bg-elevated     → white (light) / slate-800 (dark)
bg-overlay      → black/50% (light) / black/70% (dark)
```

**Border Tokens:**

```
border-default  → slate-200 (light) / slate-700 (dark)
border-subtle   → slate-100 (light) / slate-800 (dark)
border-focus    → indigo-500 (light) / indigo-400 (dark)
```

**Component Tokens:**

```
button-primary-bg       → indigo-600
button-primary-hover    → indigo-700
button-primary-text     → white
button-secondary-bg     → slate-100
button-secondary-hover  → slate-200
button-secondary-text   → slate-700
```

### Implementation Strategy

**Phase 1: Use Tailwind Defaults**

- Start with standard Tailwind utility classes
- Use semantic color names (indigo, slate) directly
- Quick prototyping, no token overhead

**Phase 2: Add Semantic Aliases**

- Extend Tailwind config with semantic tokens
- Map to Tailwind colors: `primary: colors.indigo[600]`
- Update components to use semantic classes

**Phase 3: CSS Variables for Theming**

- Convert to CSS custom properties for runtime theming
- Implement dark mode switching
- Support user customization (future)

---

## 11. Component Patterns

### Button Variants

**Primary Button:**

```html
<button
  class="px-4 py-2 bg-indigo-600 text-white font-medium rounded
               hover:bg-indigo-700 focus:outline-none focus:ring-2
               focus:ring-indigo-500 focus:ring-offset-2
               transition-colors duration-150"
>
  Primary Action
</button>
```

**Secondary Button:**

```html
<button
  class="px-4 py-2 bg-slate-100 text-slate-700 font-medium rounded
               hover:bg-slate-200 focus:outline-none focus:ring-2
               focus:ring-slate-400 focus:ring-offset-2
               transition-colors duration-150"
>
  Secondary Action
</button>
```

**Ghost Button:**

```html
<button
  class="px-4 py-2 text-slate-600 font-medium rounded
               hover:bg-slate-100 focus:outline-none focus:ring-2
               focus:ring-slate-400 transition-colors duration-150"
>
  Ghost Action
</button>
```

**Danger Button:**

```html
<button
  class="px-4 py-2 bg-red-600 text-white font-medium rounded
               hover:bg-red-700 focus:outline-none focus:ring-2
               focus:ring-red-500 focus:ring-offset-2
               transition-colors duration-150"
>
  Delete
</button>
```

### Input Fields

```html
<div class="space-y-1.5">
  <label for="title" class="block text-sm font-medium text-slate-700">Title</label>
  <input
    id="title"
    type="text"
    class="w-full px-3 py-2 border border-slate-300 rounded
           text-slate-900 placeholder-slate-400
           focus:outline-none focus:ring-2 focus:ring-indigo-500
           focus:border-indigo-500 transition-colors duration-75"
    placeholder="Enter title"
  />
  <p class="text-xs text-slate-500">Helper text here</p>
</div>
```

### Cards

```html
<article
  class="bg-white rounded-lg shadow p-6 hover:shadow-md
                transition-shadow duration-200"
>
  <h3 class="text-xl font-semibold text-slate-900 mb-2">Card Title</h3>
  <p class="text-slate-600 mb-4">Card content goes here...</p>
  <div class="flex items-center gap-2 text-sm text-slate-500">
    <time>2025-12-15</time>
    <span>·</span>
    <span>John Doe</span>
  </div>
</article>
```

### Badges

```html
<!-- Primary badge -->
<span
  class="inline-flex items-center px-2.5 py-0.5 rounded-sm text-xs
             font-medium bg-indigo-100 text-indigo-800"
>
  Tag
</span>

<!-- Success badge -->
<span
  class="inline-flex items-center px-2.5 py-0.5 rounded-sm text-xs
             font-medium bg-emerald-100 text-emerald-800"
>
  Published
</span>

<!-- Warning badge -->
<span
  class="inline-flex items-center px-2.5 py-0.5 rounded-sm text-xs
             font-medium bg-amber-100 text-amber-800"
>
  Draft
</span>
```

---

## 12. Accessibility Requirements

### WCAG 2.1 AA Compliance Checklist

**Color & Contrast:**

- ✓ Text contrast ≥ 4.5:1 (normal text)
- ✓ Text contrast ≥ 3:1 (large text 18pt+)
- ✓ UI component contrast ≥ 3:1 (borders, icons)
- ✓ Never rely on color alone for information

**Keyboard Navigation:**

- ✓ All interactive elements keyboard accessible
- ✓ Visible focus indicators on all focusable elements
- ✓ Logical tab order (left-to-right, top-to-bottom)
- ✓ Skip to main content link
- ✓ Escape key closes modals/dropdowns

**Screen Readers:**

- ✓ Semantic HTML (`<nav>`, `<main>`, `<article>`, `<button>`)
- ✓ ARIA labels for icon-only buttons
- ✓ `alt` text for all meaningful images
- ✓ `aria-live` regions for dynamic content
- ✓ Form labels associated with inputs

**Forms:**

- ✓ Labels for all inputs
- ✓ Error messages linked with `aria-describedby`
- ✓ Required fields indicated (not just with color)
- ✓ Clear error states and instructions

**Motion:**

- ✓ Respect `prefers-reduced-motion`
- ✓ No auto-playing videos/animations
- ✓ Pause/stop controls for moving content

**Touch Targets:**

- ✓ Minimum 44×44px touch targets on mobile
- ✓ Adequate spacing between interactive elements

### Focus Management

**Focus Styles:**

```css
/* Global focus style */
:focus-visible {
  outline: 2px solid theme('colors.indigo.500');
  outline-offset: 2px;
}

/* Tailwind utility */
.focus-visible:outline-none
.focus-visible:ring-2
.focus-visible:ring-indigo-500
.focus-visible:ring-offset-2
```

**Focus Trapping:**

- Modal dialogs trap focus within dialog
- Tab cycles through modal elements only
- Escape key returns focus to trigger element

### Screen Reader Patterns

**Skip Navigation:**

```html
<a
  href="#main-content"
  class="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4
          focus:z-50 focus:px-4 focus:py-2 focus:bg-indigo-600 focus:text-white"
>
  Skip to main content
</a>
```

**Visually Hidden Class:**

```css
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
```

---

## 13. Dark Mode Implementation

### Strategy

Use Tailwind's `dark:` variant with class-based toggling:

```javascript
// tailwind.config.js
module.exports = {
  darkMode: 'class', // or 'media' for system preference
  // ...
}
```

### Toggle Implementation

```html
<!-- Dark mode toggle button -->
<button
  @click="toggleDarkMode"
  class="p-2 rounded-md text-slate-600 dark:text-slate-300
         hover:bg-slate-100 dark:hover:bg-slate-700"
  aria-label="Toggle dark mode"
>
  <!-- Sun icon (visible in dark mode) -->
  <SunIcon class="w-5 h-5 hidden dark:block" />
  <!-- Moon icon (visible in light mode) -->
  <MoonIcon class="w-5 h-5 block dark:hidden" />
</button>
```

```typescript
// Composition API
const isDark = ref(false)

const toggleDarkMode = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

// Initialize on mount
onMounted(() => {
  const saved = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = saved === 'dark' || (!saved && prefersDark)
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  }
})
```

### Component Dark Mode Example

```html
<div class="bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-50">
  <h2 class="text-2xl font-bold text-slate-800 dark:text-slate-100">Title</h2>
  <p class="text-slate-600 dark:text-slate-300">Body text that adapts to theme.</p>
  <button
    class="bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500
                 dark:hover:bg-indigo-600 text-white"
  >
    Action
  </button>
</div>
```

---

## 14. Performance Guidelines

### Optimization Strategies

**CSS:**

- Purge unused Tailwind classes in production
- Use `@apply` sparingly (increases bundle size)
- Prefer utility classes over custom CSS
- Lazy-load non-critical CSS

**Images:**

- Use WebP/AVIF with JPEG/PNG fallbacks
- Implement lazy loading: `loading="lazy"`
- Responsive images with `srcset`
- Optimize and compress all images

**Fonts:**

- Use system font stack (no web font loading delay)
- If custom fonts needed: preload + font-display: swap

**Animations:**

- Animate only `opacity` and `transform`
- Use `will-change` sparingly
- Respect `prefers-reduced-motion`

**JavaScript:**

- Code-split routes and heavy components
- Tree-shake unused dependencies
- Lazy-load icons (import only what's used)

---

## 15. Implementation Checklist

### Setup Phase

- [ ] Install Tailwind CSS in Vue project
- [ ] Configure `tailwind.config.js` with design tokens
- [ ] Set up CSS custom properties for theming
- [ ] Install Heroicons: `npm install @heroicons/vue`
- [ ] Create base CSS file with global styles
- [ ] Configure dark mode strategy (class-based)
- [ ] Set up Pinia store for theme preference

### Component Library Phase

- [ ] Build button component with all variants
- [ ] Build input/form components
- [ ] Build card component
- [ ] Build badge/tag components
- [ ] Build modal/dialog component
- [ ] Build dropdown/select component
- [ ] Build tooltip component
- [ ] Build navigation components (navbar, sidebar)
- [ ] Build table component (for publications)
- [ ] Document all components in Storybook (optional)

### Accessibility Phase

- [ ] Add focus styles to all interactive elements
- [ ] Implement skip navigation link
- [ ] Add ARIA labels where needed
- [ ] Test keyboard navigation
- [ ] Test with screen reader (VoiceOver, NVDA)
- [ ] Run axe DevTools audit
- [ ] Implement `prefers-reduced-motion` support

### Theming Phase

- [ ] Implement dark mode toggle
- [ ] Test all components in dark mode
- [ ] Persist theme preference to localStorage
- [ ] Respect system preference on first visit
- [ ] Test contrast ratios in both modes

### Documentation Phase

- [ ] Create component usage examples
- [ ] Document common patterns
- [ ] Create accessibility guidelines doc
- [ ] Write contribution guide for designers/developers

---

## 16. Resources & Tools

### Design Tools

- **Color Contrast Checker:** [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)
- **Color Palette Generator:** [Tailwind Color Shades Generator](https://www.tailwindshades.com/)
- **Typography Scale:** [Type Scale Calculator](https://typescale.com/)
- **Spacing Calculator:** [8-Point Grid Calculator](https://spec.fm/specifics/8-pt-grid)

### Accessibility Tools

- **axe DevTools:** Browser extension for accessibility auditing
- **WAVE:** Web accessibility evaluation tool
- **Lighthouse:** Chrome DevTools audit (includes accessibility)
- **Screen Readers:** VoiceOver (Mac), NVDA (Windows), JAWS

### Development Tools

- **Tailwind IntelliSense:** VSCode extension for class autocomplete
- **Headless UI:** Unstyled accessible components ([headlessui.com](https://headlessui.com/))
- **Heroicons:** Icon library ([heroicons.com](https://heroicons.com/))
- **PostCSS:** CSS processing (included with Tailwind)

### Learning Resources

- **Tailwind Documentation:** [tailwindcss.com/docs](https://tailwindcss.com/docs)
- **WCAG Guidelines:** [w3.org/WAI/WCAG21/quickref](https://www.w3.org/WAI/WCAG21/quickref/)
- **Inclusive Components:** [inclusive-components.design](https://inclusive-components.design/)
- **A11y Project:** [a11yproject.com](https://www.a11yproject.com/)

---

## 17. Version History

| Version | Date       | Changes                       |
| ------- | ---------- | ----------------------------- |
| 1.0.0   | 2025-12-15 | Initial design system release |

---

## 18. Contributing

When proposing changes to this design system:

1. **Open Discussion First:** Discuss significant changes before implementation
2. **Maintain Consistency:** Ensure new tokens/patterns align with existing system
3. **Document Rationale:** Explain why changes improve the system
4. **Test Thoroughly:** Verify accessibility and performance impact
5. **Update Examples:** Keep code examples current with changes

---

## Questions or Feedback?

This design system is a living document. If you have questions, suggestions, or find inconsistencies, please open an issue or discussion in the project repository.

**Maintained by:** BibSonomy Frontend Team
**Last Review:** 2025-12-15
