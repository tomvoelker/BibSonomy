# PostDetailPage

## Route

`/posts/:postId`

**Alternative Routes**:

- `/posts/bibtexkey/:key` - Lookup by BibTeX citation key

## Access Control

- [x] Public (for public posts)
- [x] Requires Authentication (for private/group posts)
- [ ] Requires Admin

Access depends on post visibility:

- Public posts: Anyone
- Private posts: Owner only
- Group posts: Group members only

## Page Purpose

Display complete details for a single post (bookmark or publication), including all metadata, tags, groups, documents, and user comments. Allows owner to edit or delete.

**Context**: Destination page for post discovery. Must support:

- Complete metadata display (all BibTeX fields or bookmark details)
- Citation export in multiple formats
- Document access (PDFs for publications)
- Social features (tags, groups, related posts)
- Owner management (edit, delete)

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 2.2 (Posts Routes), Section 5.1.2 (Post Detail API)
- DESIGN_SYSTEM.md: Section 7.4 (Patterns - Detail Views)
- COMPONENT_LIBRARY.md: BookmarkDetails, BibTexDetails, DocumentList components

## Layout Structure

- **Header**: Global navigation with breadcrumb (Posts > {title})
- **Main Content** (70%):
  - Post header (title, resource type badge)
  - Resource details (URL for bookmarks, BibTeX fields for publications)
  - User description/notes
  - Tags (clickable)
  - Groups (clickable)
  - Metadata footer (created/updated dates, user)
  - Documents section (PDF attachments)
  - Related posts (if available)
- **Sidebar** (right, 30%):
  - Owner actions (Edit, Delete, Export)
  - Sharing options (Copy link, Export formats)
  - Statistics (view count if available)
  - Related tags

## Components Used

- `AppHeader`
- `BreadcrumbNav`
- `PostHeader`
- `BookmarkDetails` (for bookmarks)
- `BibTexDetails` (for publications)
- `TagList`
- `GroupList`
- `DocumentList`
- `DocumentUploadButton`
- `RelatedPostsList`
- `ShareMenu`
- `ExportMenu`
- `ConfirmDeleteModal`
- `EditPostButton`
- `MetadataFooter`

## API Calls

```typescript
// On mount
GET /api/v2/posts/{postId}

// Documents (if BibTeX)
GET /api/v2/posts/{postId}/documents

// Related posts (tags-based)
GET /api/v2/posts?tags={tag1,tag2}&limit=5&exclude={postId}

// Delete
DELETE /api/v2/posts/{postId}

// Export
GET /api/v2/posts/{postId}?format=bibtex
```

## State Management

- **vue-query**:
  - `useQuery(['post', postId], fetchPost)`
  - `useQuery(['post-documents', postId], fetchDocuments)` (if BibTeX)
  - `useMutation(deletePost, { onSuccess: invalidate & redirect })`
- **Pinia**:
  - `authStore` (check if current user is owner)
- **Component State**:
  - `showDeleteModal: boolean`
  - `showShareMenu: boolean`

## User Interactions

1. User clicks "Edit" → Navigates to `/posts/{postId}/edit`
2. User clicks "Delete" → Shows confirmation modal → Confirms → Calls DELETE API → Redirects to `/posts` with success toast
3. User clicks tag → Navigates to `/tags/{tagname}/posts`
4. User clicks group → Navigates to `/groups/{groupname}`
5. User clicks username → Navigates to `/users/{username}`
6. User clicks "Copy link" → Copies URL to clipboard → Shows toast "Link copied"
7. User clicks "Export as BibTeX" → Downloads `.bib` file
8. User clicks document → Opens PDF viewer or downloads file
9. User clicks related post → Navigates to that post

## URL Parameters & Query Strings

- `postId`: Post ID (path parameter, required)

## Page States

- **Loading state**: Skeleton UI for post content
- **Empty state**: N/A (single post)
- **Error state - 404**: "Post not found" with link to browse posts
- **Error state - 403**: "You don't have permission to view this post"
- **Error state - 500**: "Failed to load post. Please try again."
- **Success state**: Full post details rendered
- **Deleting state**: Confirmation modal with loading spinner on confirm button

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar
- **Tablet (768-1024px)**: Sidebar below content, sticky action buttons
- **Mobile (<768px)**: Single column, floating action button for owner actions

## Accessibility Requirements

- Page title: "{Post Title} - BibSonomy"
- Meta description from post description or abstract
- Breadcrumb navigation with `aria-label="Breadcrumb"`
- Tag list as `<nav>` with `aria-label="Post tags"`
- Delete button requires confirmation (prevents accidental deletion)
- Focus returns to post list after deletion
- Document links announce file size and type to screen readers

## i18n Keys

```
page.postDetail.title
page.postDetail.breadcrumb.posts
page.postDetail.type.bookmark
page.postDetail.type.bibtex
page.postDetail.description
page.postDetail.tags
page.postDetail.groups
page.postDetail.documents
page.postDetail.documentCount
page.postDetail.uploadDocument
page.postDetail.relatedPosts
page.postDetail.metadata.created
page.postDetail.metadata.updated
page.postDetail.metadata.by
page.postDetail.actions.edit
page.postDetail.actions.delete
page.postDetail.actions.share
page.postDetail.actions.export
page.postDetail.delete.confirm.title
page.postDetail.delete.confirm.message
page.postDetail.delete.confirm.cancel
page.postDetail.delete.confirm.confirm
page.postDetail.delete.success
page.postDetail.share.copyLink
page.postDetail.share.linkCopied
page.postDetail.export.bibtex
page.postDetail.export.endnote
page.postDetail.export.csv
page.postDetail.notFound
page.postDetail.forbidden
page.postDetail.error

# Bookmark-specific
page.postDetail.bookmark.url
page.postDetail.bookmark.visitSite

# BibTeX-specific
page.postDetail.bibtex.key
page.postDetail.bibtex.entryType
page.postDetail.bibtex.authors
page.postDetail.bibtex.year
page.postDetail.bibtex.journal
page.postDetail.bibtex.booktitle
page.postDetail.bibtex.publisher
page.postDetail.bibtex.doi
page.postDetail.bibtex.abstract
```

## Design System References

**Colors**:

- Post header background: `gray-50` with `gray-200` bottom border
- Resource type badge: `emerald-500` (bookmark), `blue-500` (publication)
- Tags: `indigo-100` background, `indigo-700` text
- Groups: `purple-100` background, `purple-700` text
- Owner actions: `indigo-600` (Edit), `red-600` (Delete)

**Typography**:

- Post title: text-3xl, font-bold, `gray-900`
- BibTeX fields labels: text-sm, font-medium, `gray-700`
- BibTeX fields values: text-base, `gray-900`
- Abstract: text-base, leading-relaxed, `gray-700`
- Metadata footer: text-sm, `gray-600`

**Spacing**:

- Main content: py-8 px-6
- Section gaps: gap-8 (between major sections)
- BibTeX field gaps: gap-4 (between fields)
- Sidebar: p-6, gap-6 (between widgets)

**Interactive Elements**:

- Share button: Headless UI Popover with copy link functionality
- Export menu: Headless UI Menu with format options
- Delete modal: Headless UI Dialog with confirmation

## Mockup Notes

- Clean, readable typography for publication details (uses Inter font from design system)
- BibTeX fields displayed in labeled sections using definition list (`<dl>`) semantic HTML
- Fields rendered with `BibTexFieldDisplay` component (label + value + optional icon)
- Tags as pill-shaped `Badge` components with hover state showing post count
- Groups as `GroupBadge` components with privacy indicator icon
- Documents list with PDF icon, filename, file size, and download/preview buttons
- Document preview opens in modal with PDF.js viewer (or browser default)
- Related posts as compact `PostCard` components in horizontal scroll on mobile
- Owner-only actions clearly separated in sidebar with `isOwner` computed property
- Edit button uses `Button` variant="primary", Delete uses variant="danger"
- Export menu with format icons (BibTeX, EndNote, RIS, CSV) and keyboard shortcuts
- Delete confirmation modal (`ConfirmDialog` component) with warning color and checkbox "I understand this cannot be undone"
- Bookmark URL displayed prominently as clickable link with external link icon
- Bookmark screenshot/preview if available (scraped metadata)
- BibTeX abstract in expandable `Disclosure` section if >500 characters
- Copy citation button with format selector (inline, formatted, raw BibTeX)
- Smooth transitions (transition-all duration-200) when toggling sections
- Print-friendly styles (hide sidebar, optimize for citation printing)

## Advanced Features

### Citation Formatting

- Copy formatted citation in multiple styles (APA, MLA, Chicago, IEEE)
- Live preview of citation format
- Citation style selector (Headless UI Listbox)
- Stored preference for default citation style

### Document Management (Owner only, Publications)

- Upload additional documents (drag-and-drop zone)
- Set primary document (if multiple PDFs)
- Delete documents with confirmation
- Document metadata (upload date, file size, uploader)
- Document preview in modal (PDF.js integration)

### Social Features

- View count (if available from API)
- "Add to my collection" button for other users (forks the post)
- Share on social media (Twitter, LinkedIn, Email)
- Generate QR code for mobile sharing
- Permalink with copy button

### Accessibility Features

- BibTeX fields exposed as structured data (JSON-LD) for screen readers and SEO
- Document downloads announce file size and type
- Keyboard navigation for all interactive elements
- Skip links to main content sections
- Focus trap in modals (delete confirmation, share menu)

## Performance Considerations

- Lazy-load related posts (only fetch when scrolled into view)
- PDF documents loaded on-demand (preview generates thumbnail)
- Optimistic UI updates for owner actions (delete shows immediately, reverts on error)
- Prefetch edit page data when owner hovers "Edit" button
- Cache post data with 5-minute stale-while-revalidate
- Syntax highlighting for raw BibTeX view (if enabled) uses lazy-loaded library

## Error Handling

- 404: "Post not found" with search suggestions based on URL
- 403: "Private post" with login prompt if not authenticated, or "No access" if authenticated
- 410: "Post deleted" with option to view cached version (if available)
- Network error: Show cached version with "Offline" banner
- Document download failure: Retry button with exponential backoff

## SEO & Metadata

- Dynamic page title: "{Post Title} - BibSonomy"
- Meta description from post abstract or description (truncated to 160 chars)
- Open Graph tags for social sharing (title, description, image)
- Twitter Card metadata
- Canonical URL for post detail
- Structured data (JSON-LD) for scholarly article (publications) or WebPage (bookmarks)
- BibTeX citation as `<script type="application/x-bibtex">` for citation managers
