# PostEditPage

## Route

- **Create**: `/posts/new`
- **Edit**: `/posts/:postId/edit`

## Access Control

- [ ] Public
- [x] Requires Authentication
- [ ] Requires Admin

Additional: For edit mode, user must be the post owner.

## Page Purpose

Create a new post or edit an existing post. Unified form for both bookmarks and publications with resource type toggle. Supports rich metadata input, tag management, group selection, and document upload (for publications).

**Context**: Critical content creation page. Must support:

- Quick bookmark capture (browser extension integration planned)
- Comprehensive publication metadata entry
- Intelligent metadata scraping from URLs and DOIs
- BibTeX import and parsing
- Tag autocomplete and suggestions
- Group permissions and sharing
- Draft autosave to prevent data loss

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 2.2.2 (Create/Edit Routes), Section 5.1.3 (Create Post API)
- DESIGN_SYSTEM.md: Section 7.5 (Patterns - Forms), Section 4 (Form Controls)
- COMPONENT_LIBRARY.md: BookmarkForm, BibTexForm, TagInput, DocumentUploadZone

## Layout Structure

- **Header**: Global navigation with breadcrumb
- **Main Form Area** (centered, max-width):
  - Resource Type Selector (Bookmark / Publication) - only in create mode
  - Resource-specific fields (dynamic based on type)
  - Description/Notes textarea
  - Tag input (autocomplete)
  - Group selector (multi-select)
  - Visibility radio buttons (Public / Private / Groups)
  - Document upload (for publications)
  - Action buttons (Save, Cancel)
- **Sidebar** (optional, right):
  - Quick tips
  - Tag suggestions
  - Recent tags used

## Components Used

- `AppHeader`
- `BreadcrumbNav`
- `ResourceTypeToggle`
- `BookmarkForm` (URL, Title)
- `BibTexForm` (Citation key, Entry type, Title, Authors, Year, Journal, etc.)
- `DescriptionTextarea`
- `TagInput` (with autocomplete)
- `GroupSelector` (multi-select dropdown)
- `VisibilityRadioGroup`
- `DocumentUploadZone` (drag-and-drop for PDFs)
- `FormActions` (Save, Cancel buttons)
- `QuickTips`
- `TagSuggestions`
- `UnsavedChangesWarning`

## API Calls

```typescript
// Create mode - Import from URL (optional scraping)
POST /api/v2/import/url
{
  "url": "https://example.com/article",
  "tags": [],
  "groups": []
}

// Create bookmark
POST /api/v2/posts
{
  "resource": {
    "resourceType": "bookmark",
    "url": "https://example.com",
    "title": "Example"
  },
  "description": "My notes",
  "tags": ["web", "example"],
  "groups": ["public"],
  "visibility": "public"
}

// Create publication
POST /api/v2/posts
{
  "resource": {
    "resourceType": "bibtex",
    "bibtexKey": "Smith2020",
    "entryType": "article",
    "title": "...",
    "authors": [...],
    "year": 2020
  },
  "tags": ["ml"],
  "groups": ["research-group"],
  "visibility": "groups"
}

// Edit mode - Load existing post
GET /api/v2/posts/{postId}

// Update post
PUT /api/v2/posts/{postId}
{
  "description": "Updated notes",
  "tags": ["updated", "tags"],
  "groups": ["public"],
  "visibility": "public"
}

// Upload document (for publications)
POST /api/v2/posts/{postId}/documents
multipart/form-data: file

// Tag autocomplete
GET /api/v2/tags?search={query}&limit=10
```

## State Management

- **vue-query**:
  - `useQuery(['post', postId], fetchPost)` (edit mode only)
  - `useMutation(createPost, { onSuccess: navigate to detail })`
  - `useMutation(updatePost, { onSuccess: navigate to detail })`
  - `useMutation(uploadDocument)`
- **Pinia**:
  - `authStore` (current user)
  - `formStore` (draft posts for autosave)
- **Component State**:
  - `formData: PostFormData`
  - `errors: ValidationErrors`
  - `hasUnsavedChanges: boolean`
  - `isSubmitting: boolean`
  - `documentFiles: File[]`

## User Interactions

1. **Create mode**:
   - User toggles resource type → Form fields change (Bookmark vs BibTeX)
   - User pastes URL → Optionally triggers scraping for metadata
   - User types tag → Shows autocomplete → Selects tag → Adds to tag list
   - User removes tag → Click X on tag chip → Removes from list
   - User selects groups → Multi-select dropdown → Shows selected groups
   - User uploads PDF → Drag-and-drop or file picker → Shows file preview
   - User clicks "Save" → Validates form → Calls POST → Navigates to post detail on success
   - User clicks "Cancel" → Shows unsaved changes warning → Navigates back

2. **Edit mode**:
   - Same as create mode but resource type is fixed
   - Pre-populates form with existing data
   - "Save" calls PUT instead of POST

3. **Autosave**:
   - Form data saved to localStorage every 5 seconds
   - On page reload, asks "Restore draft?"

4. **Validation**:
   - Real-time validation as user types
   - Error messages below invalid fields
   - Submit button disabled until form is valid

## URL Parameters & Query Strings

- **Create mode**:
  - `/posts/new?type=bookmark` (pre-select bookmark)
  - `/posts/new?type=bibtex` (pre-select publication)
  - `/posts/new?url=https://...` (pre-fill URL and scrape)
- **Edit mode**:
  - `postId`: Post ID (path parameter)

## Page States

- **Loading state** (edit mode): Skeleton form while loading post data
- **Empty state**: N/A (form always rendered)
- **Error state - Load failed**: "Failed to load post. Please try again."
- **Error state - Submit failed**: Toast notification with error details, form remains editable
- **Error state - Forbidden**: "You don't have permission to edit this post"
- **Validation error state**: Inline error messages below fields, submit button disabled
- **Submitting state**: Submit button shows spinner, form fields disabled
- **Success state**: Navigate to post detail page with success toast

## Responsive Behavior

- **Desktop (>1024px)**: Full form with sidebar tips
- **Tablet (768-1024px)**: Full form, sidebar below or hidden
- **Mobile (<768px)**: Single column form, no sidebar, sticky bottom action bar

## Accessibility Requirements

- Page title: "Create Post" or "Edit Post - {Title}"
- All form fields have associated `<label>` elements
- Error messages linked to fields via `aria-describedby`
- Required fields marked with asterisk and `aria-required="true"`
- Form submission prevented if invalid, with focus moved to first error
- Unsaved changes warning when navigating away
- Keyboard navigation for tag chips (Tab to navigate, Delete to remove)
- File upload accessible via keyboard (Enter to open file picker)

## i18n Keys

```
page.postEdit.titleCreate
page.postEdit.titleEdit
page.postEdit.breadcrumb.posts
page.postEdit.breadcrumb.new
page.postEdit.breadcrumb.edit
page.postEdit.resourceType.label
page.postEdit.resourceType.bookmark
page.postEdit.resourceType.bibtex
page.postEdit.description.label
page.postEdit.description.placeholder
page.postEdit.tags.label
page.postEdit.tags.placeholder
page.postEdit.tags.add
page.postEdit.groups.label
page.postEdit.groups.placeholder
page.postEdit.visibility.label
page.postEdit.visibility.public
page.postEdit.visibility.private
page.postEdit.visibility.groups
page.postEdit.documents.label
page.postEdit.documents.upload
page.postEdit.documents.dragDrop
page.postEdit.actions.save
page.postEdit.actions.cancel
page.postEdit.unsavedChanges.title
page.postEdit.unsavedChanges.message
page.postEdit.unsavedChanges.stay
page.postEdit.unsavedChanges.leave
page.postEdit.autosave.restored
page.postEdit.autosave.prompt
page.postEdit.success.created
page.postEdit.success.updated
page.postEdit.error.loadFailed
page.postEdit.error.submitFailed
page.postEdit.error.forbidden
page.postEdit.validation.required
page.postEdit.validation.invalidUrl
page.postEdit.validation.invalidEmail
page.postEdit.tips.title
page.postEdit.tips.tags
page.postEdit.tips.groups
page.postEdit.tips.visibility

# Bookmark-specific
page.postEdit.bookmark.url.label
page.postEdit.bookmark.url.placeholder
page.postEdit.bookmark.title.label
page.postEdit.bookmark.title.placeholder
page.postEdit.bookmark.scrape

# BibTeX-specific
page.postEdit.bibtex.key.label
page.postEdit.bibtex.entryType.label
page.postEdit.bibtex.title.label
page.postEdit.bibtex.authors.label
page.postEdit.bibtex.authors.add
page.postEdit.bibtex.year.label
page.postEdit.bibtex.journal.label
page.postEdit.bibtex.booktitle.label
page.postEdit.bibtex.publisher.label
page.postEdit.bibtex.doi.label
page.postEdit.bibtex.abstract.label
```

## Design System References

**Colors**:

- Form background: `white`
- Section headers: `gray-900`
- Input borders: `gray-300`, focus `indigo-500`
- Error states: `red-600` text, `red-100` background
- Success states: `emerald-600` text, `emerald-100` background
- Autosave indicator: `gray-500` text

**Typography**:

- Page title: text-3xl, font-bold, `gray-900`
- Section headings: text-xl, font-semibold, `gray-800`
- Field labels: text-sm, font-medium, `gray-700`
- Helper text: text-sm, `gray-600`
- Error text: text-sm, font-medium, `red-600`

**Spacing**:

- Form container: max-w-4xl, mx-auto, py-8 px-6
- Section gaps: gap-10 (between major sections)
- Field groups: gap-6 (within sections)
- Individual fields: gap-2 (label + input + error)

**Form Controls**:

- Text inputs: Headless UI styled with Tailwind (h-10, px-3, rounded-md)
- Textareas: Headless UI styled (min-h-32, resizable)
- Select dropdowns: Headless UI Listbox with search
- Multi-select: Headless UI Combobox with checkboxes
- Radio groups: Headless UI RadioGroup with descriptive cards

## Mockup Notes

- Clean, focused form design with clear visual hierarchy
- Resource type toggle as large pill buttons at top (Headless UI RadioGroup, only in create mode)
- Form fields grouped by section using semantic `<fieldset>` and `<legend>`
  - **Resource Details** (URL/Title for bookmarks, BibTeX fields for publications)
  - **Description & Notes** (Rich textarea with markdown preview)
  - **Organization** (Tags, Groups)
  - **Sharing & Visibility** (Public/Private/Groups radio)
  - **Documents** (PDF upload, publications only)
- Tag input with autocomplete dropdown (Headless UI Combobox, similar to GitHub topics)
- Tag chips with remove button (X) and color coding by frequency
- Tag suggestions based on content analysis (optional, powered by API)
- Group selector as multi-select Combobox with checkboxes and search
- Visibility options as RadioGroup with descriptive cards (icon + title + explanation)
- Document upload zone with drag-and-drop visual feedback (border-dashed, hover state)
- Action buttons in sticky footer bar on mobile, right-aligned on desktop
  - Cancel = `Button` variant="secondary" (navigates back with unsaved check)
  - Save = `Button` variant="primary" with loading spinner
- Real-time validation with inline error messages (Zod schema validation)
- Loading states for submit button (spinner + "Saving..." text + disabled)
- Unsaved changes warning as Headless UI Dialog with "Stay" / "Leave" options
- Autosave indicator in top-right (text-sm, gray-500, "Draft saved at 12:34" or "Saving...")
- BibTeX author input with dynamic add/remove buttons for multiple authors
  - Each author: First name, Last name, von part, Junior part (BibTeX standard)
  - Drag handles for reordering authors
- DOI/arXiv/PubMed import buttons for publications (pre-fill metadata from identifiers)
- URL scraping for bookmarks (fetch title, description, favicon from URL)
- Preview mode toggle to see how post will appear (split-screen on desktop, modal on mobile)
- Keyboard shortcuts overlay (`?` key shows help):
  - `Cmd/Ctrl + S` - Save
  - `Cmd/Ctrl + Enter` - Save and close
  - `Esc` - Cancel

## Advanced Features

### Metadata Import (Publications)

- **DOI Import**: Paste DOI → fetch metadata from CrossRef/DataCite API
- **arXiv Import**: Paste arXiv ID → fetch from arXiv API
- **PubMed Import**: Paste PMID → fetch from PubMed API
- **BibTeX Import**: Paste raw BibTeX → parse and populate fields
- **PDF Import**: Upload PDF → extract DOI/metadata from document
- Import progress indicator with success/failure states
- Merge conflict resolution if editing existing post

### Smart Tagging

- Tag suggestions based on:
  - Publication title/abstract (NLP analysis if available)
  - User's frequent tags
  - Popular tags in similar posts
  - Group preset tags (if sharing to group)
- Auto-tag from BibTeX keywords field
- Tag validation (max length, allowed characters)
- Tag frequency indicator (how many posts use this tag)

### Autosave & Drafts

- Autosave to localStorage every 5 seconds (debounced)
- Draft restoration prompt on page load ("You have an unsaved draft from 2 hours ago")
- Multiple drafts support (list of recent drafts, choose which to restore)
- Cloud sync for authenticated users (save draft to API)
- Draft expiration (auto-delete after 7 days)

### Form Validation

- Client-side validation with Zod schemas
- Real-time validation on blur (not on every keystroke)
- Field-level error messages (below input)
- Form-level error summary (top of form, scrolls into view)
- Required fields marked with asterisk and `aria-required`
- URL validation with protocol requirement
- BibTeX key uniqueness check (async validation)
- File upload validation (size, type, virus scan if available)

### Accessibility Features

- All fields have associated labels with `for` attribute
- Error messages linked via `aria-describedby`
- Focus management (first error on submit, focus trap in modals)
- Keyboard navigation for all controls (Tab, Arrow keys, Enter, Esc)
- Screen reader announcements for autosave, validation errors
- Field hints exposed to screen readers
- Skip link to form actions

## Performance Considerations

- Debounced tag autocomplete API calls (300ms)
- Lazy-load BibTeX parser library (only when needed)
- Lazy-load PDF upload library (only when document section shown)
- Optimistic UI for tag additions (add immediately, revert on error)
- Form state managed by vue-query mutations for cache invalidation
- File uploads with progress tracking (chunked uploads for large files)
- Preview mode lazy-loads rendering library

## Error Handling

- Network errors during save: Show toast with "Retry" button, preserve form state
- Validation errors: Inline messages + form summary + focus first error
- Import errors (DOI/arXiv): Show error message in import section, allow manual entry
- File upload errors: Show error below upload zone, allow retry
- Autosave errors: Silent failure, retry on next interval
- Concurrent edit detection: Warn if post modified by another user/session
- Browser back button: Trigger unsaved changes warning

## SEO Considerations

- Page title: "Create Post - BibSonomy" or "Edit: {Post Title} - BibSonomy"
- Meta robots: noindex,nofollow (don't index form pages)
- Canonical URL: N/A (form page)
- No structured data (form page)
