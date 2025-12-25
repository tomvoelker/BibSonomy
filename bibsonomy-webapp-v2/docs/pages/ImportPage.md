# ImportPage

## Route

`/import`

## Access Control

- [ ] Public
- [x] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Import posts from various sources: BibTeX files, browser bookmarks, URLs, DOIs, PDFs, EndNote files. Unified import interface with step-by-step wizard.

## Layout Structure

- **Header**: Global navigation
- **Page Header**:
  - Title: "Import"
  - Subtitle: "Import bookmarks and publications from various sources"
- **Import Method Selector** (cards):
  - BibTeX File
  - Browser Bookmarks
  - URL/Web Page
  - DOI/ISBN
  - PDF with Metadata
  - EndNote File
- **Import Wizard** (after selecting method):
  - Step 1: Upload/Input
  - Step 2: Preview & Configure
  - Step 3: Confirm & Import
  - Step 4: Results
- **Main Content Area**:
  - Changes based on wizard step
  - Progress indicator at top

## Components Used

- `AppHeader`
- `PageHeader`
- `ImportMethodSelector`
- `ImportMethodCard`
- `ProgressIndicator`
- `BibTexImportWizard`
- `BookmarksImportWizard`
- `UrlImportWizard`
- `DoiImportWizard`
- `PdfImportWizard`
- `EndNoteImportWizard`
- `FileUploadZone`
- `ImportPreviewTable`
- `ImportConfigForm`
- `ImportResultsPanel`

## API Calls

```typescript
// BibTeX Import
POST /api/v2/import/bibtex
multipart/form-data: {
  file: File,
  tags: ["imported", "research"],
  groups: ["public"]
}
Response: {
  imported: PostDto[],
  errors: Array<{ line: number, message: string }>
}

// Browser Bookmarks Import
POST /api/v2/import/bookmarks
multipart/form-data: {
  file: File,
  tags: ["bookmarks"],
  groups: ["public"]
}
Response: {
  imported: PostDto[],
  skipped: number
}

// URL Import
POST /api/v2/import/url
{
  url: "https://example.com/article",
  tags: ["web"],
  groups: ["public"]
}
Response: PostDto

// DOI Import
POST /api/v2/import/doi
{
  identifier: "10.1145/3490099",
  tags: ["research"],
  groups: ["public"]
}
Response: PostDto

// PDF Import
POST /api/v2/import/pdf
multipart/form-data: {
  file: File,
  tags: ["papers"],
  groups: ["research-group"]
}
Response: PostDto

// EndNote Import
POST /api/v2/import/endnote
multipart/form-data: {
  file: File,
  tags: ["imported"],
  groups: ["public"]
}
Response: {
  imported: PostDto[]
}
```

## State Management

- **vue-query**:
  - `useMutation(importBibtex, { onSuccess: show results })`
  - `useMutation(importBookmarks, { onSuccess: show results })`
  - `useMutation(importUrl, { onSuccess: redirect to post })`
  - `useMutation(importDoi, { onSuccess: redirect to post })`
  - `useMutation(importPdf, { onSuccess: redirect to post })`
  - `useMutation(importEndnote, { onSuccess: show results })`
- **Pinia**:
  - `authStore` (current user)
- **Component State**:
  - `selectedMethod: ImportMethod | null`
  - `currentStep: number`
  - `uploadedFile: File | null`
  - `previewData: any[]`
  - `importConfig: ImportConfig`
  - `importResults: ImportResults | null`
  - `isImporting: boolean`

## User Interactions

### Method Selection

1. User clicks import method card → Sets `selectedMethod` → Shows wizard

### BibTeX Import Wizard

1. **Step 1**: User uploads .bib file → Shows preview of entries
2. **Step 2**: User configures tags and groups to apply → Optional: Edit individual entries
3. **Step 3**: User confirms → Calls POST → Shows import progress
4. **Step 4**: Shows results (successful imports + errors) → "View imported posts" or "Import more"

### Browser Bookmarks Import

1. **Step 1**: User uploads HTML bookmark file
2. **Step 2**: Preview list of bookmarks → User can deselect duplicates
3. **Step 3**: Configure tags/groups
4. **Step 4**: Import → Show results with skipped count

### URL Import

1. **Step 1**: User pastes URL
2. **Step 2**: System scrapes metadata → Shows preview (title, description)
3. **Step 3**: User adds tags/groups
4. **Step 4**: Import → Redirects to created post

### DOI Import

1. **Step 1**: User enters DOI or ISBN
2. **Step 2**: System fetches metadata → Shows preview
3. **Step 3**: User adds tags/groups
4. **Step 4**: Import → Redirects to created post

### PDF Import

1. **Step 1**: User uploads PDF
2. **Step 2**: System extracts metadata → Shows preview (may be incomplete)
3. **Step 3**: User fills in missing fields, adds tags/groups
4. **Step 4**: Import → Redirects to created post with PDF attached

### EndNote Import

1. **Step 1**: User uploads EndNote file
2. **Step 2**: Preview entries
3. **Step 3**: Configure tags/groups
4. **Step 4**: Import → Show results

## URL Parameters & Query Strings

- `method`: Pre-select import method (`bibtex` | `bookmarks` | `url` | `doi` | `pdf` | `endnote`)
- `url`: Pre-fill URL for URL import

Example: `/import?method=url&url=https://example.com`

## Page States

- **Loading state**: During metadata scraping or file processing
- **Empty state**: Method selection screen
- **Error state - Upload failed**: "Failed to upload file. Please try again."
- **Error state - Import failed**: Show error messages per entry
- **Success state**: Import results with counts
- **Importing state**: Progress bar or spinner with "Importing X of Y entries..."

## Responsive Behavior

- **Desktop (>1024px)**: Cards in 3-column grid, wizard full width
- **Tablet (768-1024px)**: Cards in 2-column grid, wizard full width
- **Mobile (<768px)**: Cards stacked, wizard full width, sticky bottom action bar

## Accessibility Requirements

- Page title: "Import - BibSonomy"
- Import method cards focusable and activatable via keyboard
- Wizard steps with `aria-label="Step X of Y"`
- File upload accessible via keyboard
- Progress indicator announced to screen readers
- Error messages clearly associated with fields

## i18n Keys

```
page.import.title
page.import.subtitle
page.import.selectMethod
page.import.methods.bibtex.title
page.import.methods.bibtex.description
page.import.methods.bookmarks.title
page.import.methods.bookmarks.description
page.import.methods.url.title
page.import.methods.url.description
page.import.methods.doi.title
page.import.methods.doi.description
page.import.methods.pdf.title
page.import.methods.pdf.description
page.import.methods.endnote.title
page.import.methods.endnote.description

# Wizard Steps
page.import.wizard.step1
page.import.wizard.step2
page.import.wizard.step3
page.import.wizard.step4
page.import.wizard.back
page.import.wizard.next
page.import.wizard.import
page.import.wizard.finish

# Upload
page.import.upload.title
page.import.upload.dragDrop
page.import.upload.browse
page.import.upload.invalidFile
page.import.upload.uploading

# Preview
page.import.preview.title
page.import.preview.count
page.import.preview.edit
page.import.preview.remove

# Configure
page.import.config.tags
page.import.config.groups
page.import.config.applyToAll

# Results
page.import.results.title
page.import.results.success
page.import.results.imported
page.import.results.skipped
page.import.results.errors
page.import.results.error.line
page.import.results.error.message
page.import.results.viewPosts
page.import.results.importMore

# URL Import
page.import.url.placeholder
page.import.url.scraping
page.import.url.scraped

# DOI Import
page.import.doi.placeholder
page.import.doi.fetching
page.import.doi.fetched
page.import.doi.notFound

# PDF Import
page.import.pdf.extracting
page.import.pdf.extracted
page.import.pdf.fillMissing

# Errors
page.import.error.uploadFailed
page.import.error.importFailed
page.import.error.invalidFormat
page.import.error.networkError
```

## Mockup Notes

- Import method cards with icons and descriptions
- Cards with hover effect (slight elevation)
- Wizard with progress dots/steps at top
- File upload zone with drag-and-drop visual feedback
- Preview table with checkboxes to deselect items
- Import configuration form with tag input and group selector
- Import progress with animated progress bar
- Results panel with success/error counts
- Error messages with line numbers and descriptions
- "View imported posts" button links to filtered post list
- PDF preview thumbnail (if possible)
- Metadata fields with edit capability in preview step
- Smooth transitions between wizard steps
- Back/Next buttons with keyboard shortcuts
- Loading spinners during scraping/fetching
