# BibSonomy Frontend Component Library

This document specifies all reusable components for the BibSonomy Vue 3 frontend application.

## Table of Contents

1. [Base UI Components](#base-ui-components)
2. [Layout Components](#layout-components)
3. [Domain-Specific Components](#domain-specific-components)
4. [Form Components](#form-components)
5. [Navigation Components](#navigation-components)

---

## Base UI Components

These components wrap Headless UI primitives with Tailwind CSS styling and provide consistent, accessible UI elements.

### Button

**Purpose**: Primary interactive element for user actions.

**Props**:

```typescript
interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost' | 'link'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
  type?: 'button' | 'submit' | 'reset'
  icon?: string // Icon component name or path
  iconPosition?: 'left' | 'right'
  fullWidth?: boolean
}
```

**Events**:

- `@click` - Emitted when button is clicked

**Accessibility**:

- Proper `aria-disabled` state
- Loading state announced to screen readers
- Focus visible styling
- Keyboard activation support

**Slots**:

- `default` - Button text/content
- `icon` - Custom icon content

**Example Usage**:

```vue
<Button variant="primary" :loading="isSubmitting" @click="handleSubmit">
  Save Post
</Button>

<Button variant="danger" icon="trash" size="sm">
  Delete
</Button>
```

---

### Input

**Purpose**: Text input field with validation support.

**Props**:

```typescript
interface InputProps {
  modelValue: string
  type?: 'text' | 'email' | 'password' | 'url' | 'search' | 'tel'
  label?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  error?: string
  hint?: string
  icon?: string
  iconPosition?: 'left' | 'right'
  autocomplete?: string
  maxlength?: number
  size?: 'sm' | 'md' | 'lg'
}
```

**Events**:

- `@update:modelValue` - Emitted on input change
- `@blur` - Emitted when input loses focus
- `@focus` - Emitted when input gains focus
- `@enter` - Emitted when Enter key is pressed

**Accessibility**:

- Associated label with `for` attribute
- `aria-describedby` for error/hint messages
- `aria-invalid` when error present
- `aria-required` when required

**Slots**:

- `prepend` - Content before input
- `append` - Content after input

**Example Usage**:

```vue
<Input
  v-model="email"
  type="email"
  label="Email Address"
  placeholder="you@example.com"
  :required="true"
  :error="emailError"
  autocomplete="email"
/>
```

---

### Textarea

**Purpose**: Multi-line text input for longer content.

**Props**:

```typescript
interface TextareaProps {
  modelValue: string
  label?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  error?: string
  hint?: string
  rows?: number
  maxlength?: number
  autoResize?: boolean
  size?: 'sm' | 'md' | 'lg'
}
```

**Events**:

- `@update:modelValue` - Emitted on input change
- `@blur` - Emitted when textarea loses focus
- `@focus` - Emitted when textarea gains focus

**Accessibility**:

- Associated label with `for` attribute
- `aria-describedby` for error/hint messages
- `aria-invalid` when error present
- Character counter announced to screen readers

**Example Usage**:

```vue
<Textarea
  v-model="description"
  label="Description"
  :rows="4"
  :maxlength="500"
  :autoResize="true"
  hint="Describe your bookmark or publication"
/>
```

---

### Select

**Purpose**: Dropdown selection component.

**Props**:

```typescript
interface SelectOption {
  value: string | number
  label: string
  disabled?: boolean
}

interface SelectProps {
  modelValue: string | number | null
  options: SelectOption[]
  label?: string
  placeholder?: string
  disabled?: boolean
  required?: boolean
  error?: string
  hint?: string
  multiple?: boolean
  searchable?: boolean
  size?: 'sm' | 'md' | 'lg'
}
```

**Events**:

- `@update:modelValue` - Emitted when selection changes
- `@blur` - Emitted when dropdown loses focus
- `@search` - Emitted when search query changes (if searchable)

**Accessibility**:

- Uses Headless UI Listbox component
- Keyboard navigation (Arrow keys, Enter, Escape)
- Screen reader announcements for selection state
- ARIA attributes for expanded/collapsed state

**Example Usage**:

```vue
<Select
  v-model="visibility"
  :options="visibilityOptions"
  label="Visibility"
  placeholder="Select visibility"
  :required="true"
/>
```

---

### Checkbox

**Purpose**: Boolean input for single or multiple selections.

**Props**:

```typescript
interface CheckboxProps {
  modelValue: boolean | string[] | number[]
  value?: string | number // For use in checkbox groups
  label?: string
  description?: string
  disabled?: boolean
  required?: boolean
  error?: string
  indeterminate?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when checkbox state changes

**Accessibility**:

- Associated label clickable
- `aria-checked` state
- `aria-describedby` for description
- Indeterminate state supported

**Example Usage**:

```vue
<Checkbox v-model="acceptTerms" label="I accept the terms and conditions" :required="true" />

<!-- Checkbox group -->
<Checkbox v-model="selectedTags" value="java" label="Java" />
```

---

### Radio

**Purpose**: Single selection from multiple options.

**Props**:

```typescript
interface RadioProps {
  modelValue: string | number
  value: string | number
  name: string
  label?: string
  description?: string
  disabled?: boolean
  required?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when radio selection changes

**Accessibility**:

- Uses Headless UI RadioGroup component
- Keyboard navigation
- Associated labels
- Group description support

**Example Usage**:

```vue
<RadioGroup v-model="resourceType" name="resourceType">
  <Radio value="bookmark" label="Bookmark" />
  <Radio value="publication" label="Publication" />
</RadioGroup>
```

---

### Modal

**Purpose**: Dialog overlay for focused interactions.

**Props**:

```typescript
interface ModalProps {
  modelValue: boolean // Open/closed state
  title?: string
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  closeOnBackdrop?: boolean
  closeOnEscape?: boolean
  showClose?: boolean
  persistent?: boolean // Prevents closing
}
```

**Events**:

- `@update:modelValue` - Emitted when modal state changes
- `@close` - Emitted when modal is closed
- `@open` - Emitted when modal is opened

**Accessibility**:

- Uses Headless UI Dialog component
- Focus trap when open
- Focus returns to trigger element when closed
- Escape key to close (unless persistent)
- `aria-modal` and `role="dialog"`

**Slots**:

- `default` - Modal body content
- `header` - Custom header content
- `footer` - Custom footer content (for action buttons)

**Example Usage**:

```vue
<Modal v-model="showDeleteDialog" title="Confirm Delete" size="sm">
  <p>Are you sure you want to delete this post?</p>

  <template #footer>
    <Button variant="ghost" @click="showDeleteDialog = false">Cancel</Button>
    <Button variant="danger" @click="confirmDelete">Delete</Button>
  </template>
</Modal>
```

---

### DropdownMenu

**Purpose**: Contextual menu triggered by a button or other element.

**Props**:

```typescript
interface DropdownMenuItem {
  label: string
  value: string | number
  icon?: string
  disabled?: boolean
  danger?: boolean
  separator?: boolean
}

interface DropdownMenuProps {
  items: DropdownMenuItem[]
  align?: 'left' | 'right'
  disabled?: boolean
}
```

**Events**:

- `@select` - Emitted when menu item is selected, passes item value

**Accessibility**:

- Uses Headless UI Menu component
- Keyboard navigation
- Focus management
- ARIA menu role

**Slots**:

- `trigger` - Custom trigger element
- `item` - Custom item rendering (receives item as slot prop)

**Example Usage**:

```vue
<DropdownMenu :items="postActions" @select="handleAction">
  <template #trigger>
    <Button variant="ghost" icon="more-vertical" />
  </template>
</DropdownMenu>
```

---

### Tabs

**Purpose**: Switch between different views or content sections.

**Props**:

```typescript
interface Tab {
  label: string
  value: string
  icon?: string
  disabled?: boolean
  badge?: string | number
}

interface TabsProps {
  modelValue: string
  tabs: Tab[]
  variant?: 'default' | 'pills' | 'underline'
  vertical?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when active tab changes

**Accessibility**:

- Uses Headless UI Tab component
- Keyboard navigation (Arrow keys)
- `role="tablist"`, `role="tab"`, `role="tabpanel"`
- Selected state announced

**Slots**:

- Named slots for each tab panel content (using tab value as slot name)

**Example Usage**:

```vue
<Tabs v-model="activeTab" :tabs="tabs">
  <template #bookmarks>
    <BookmarkList :bookmarks="userBookmarks" />
  </template>

  <template #publications>
    <PublicationList :publications="userPublications" />
  </template>
</Tabs>
```

---

### Accordion

**Purpose**: Collapsible content sections.

**Props**:

```typescript
interface AccordionItem {
  title: string
  value: string
  disabled?: boolean
}

interface AccordionProps {
  items: AccordionItem[]
  modelValue?: string | string[] // Single or multiple open items
  multiple?: boolean
  collapsible?: boolean // Allow all items to be closed
}
```

**Events**:

- `@update:modelValue` - Emitted when expanded items change

**Accessibility**:

- Uses Headless UI Disclosure component
- Keyboard navigation
- `aria-expanded` state
- Focus management

**Slots**:

- Named slots for each accordion content (using item value as slot name)

**Example Usage**:

```vue
<Accordion v-model="openSections" :items="filterSections" :multiple="true">
  <template #tags>
    <TagFilter v-model="selectedTags" />
  </template>

  <template #date>
    <DateRangeFilter v-model="dateRange" />
  </template>
</Accordion>
```

---

### Badge

**Purpose**: Small label for status, counts, or categories.

**Props**:

```typescript
interface BadgeProps {
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info'
  size?: 'sm' | 'md' | 'lg'
  removable?: boolean
  dot?: boolean // Show dot indicator
}
```

**Events**:

- `@remove` - Emitted when remove button is clicked (if removable)

**Accessibility**:

- Semantic HTML
- Remove button has aria-label
- Screen reader friendly text

**Slots**:

- `default` - Badge content

**Example Usage**:

```vue
<Badge variant="primary">New</Badge>
<Badge variant="warning" :dot="true">3</Badge>
<Badge variant="default" :removable="true" @remove="removeTag">JavaScript</Badge>
```

---

### Alert

**Purpose**: Display important messages to users.

**Props**:

```typescript
interface AlertProps {
  variant?: 'info' | 'success' | 'warning' | 'error'
  title?: string
  dismissible?: boolean
  icon?: string
}
```

**Events**:

- `@dismiss` - Emitted when alert is dismissed

**Accessibility**:

- `role="alert"` for important messages
- `aria-live="polite"` or `aria-live="assertive"`
- Focus management for dismissible alerts

**Slots**:

- `default` - Alert message content

**Example Usage**:

```vue
<Alert variant="success" title="Success" :dismissible="true">
  Your post has been saved successfully.
</Alert>

<Alert variant="error" title="Error">
  Failed to load posts. Please try again.
</Alert>
```

---

### Toast

**Purpose**: Temporary notification messages.

**Props**:

```typescript
interface ToastProps {
  variant?: 'info' | 'success' | 'warning' | 'error'
  title?: string
  message: string
  duration?: number // Auto-dismiss duration in ms
  action?: {
    label: string
    handler: () => void
  }
}
```

**Events**:

- `@dismiss` - Emitted when toast is dismissed

**Accessibility**:

- `role="status"` for non-critical messages
- `role="alert"` for critical messages
- `aria-live` regions
- Pause on hover for accessibility

**Usage**:
Typically managed by a toast service/composable rather than used directly:

```typescript
// In component
const { showToast } = useToast()

showToast({
  variant: 'success',
  title: 'Saved',
  message: 'Your changes have been saved',
  duration: 3000,
})
```

---

### LoadingSpinner

**Purpose**: Indicate loading state.

**Props**:

```typescript
interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg' | 'xl'
  variant?: 'primary' | 'secondary' | 'white'
  label?: string // Accessible label
}
```

**Accessibility**:

- `role="status"`
- `aria-label` or `aria-labelledby`
- Screen reader announcement

**Example Usage**:

```vue
<LoadingSpinner size="md" label="Loading posts..." />
```

---

### SkeletonLoader

**Purpose**: Placeholder loading state that mimics content structure.

**Props**:

```typescript
interface SkeletonLoaderProps {
  variant?: 'text' | 'circular' | 'rectangular'
  width?: string | number
  height?: string | number
  count?: number // Number of skeleton items
  animated?: boolean
}
```

**Accessibility**:

- `aria-busy="true"` on container
- `aria-label` describing loading state

**Example Usage**:

```vue
<SkeletonLoader variant="rectangular" height="200px" :count="3" />
```

---

### Tooltip

**Purpose**: Contextual information on hover/focus.

**Props**:

```typescript
interface TooltipProps {
  content: string
  placement?: 'top' | 'bottom' | 'left' | 'right'
  delay?: number // Show delay in ms
  disabled?: boolean
}
```

**Accessibility**:

- Uses `aria-describedby`
- Keyboard accessible (shows on focus)
- Respects prefers-reduced-motion

**Slots**:

- `default` - Element that triggers tooltip

**Example Usage**:

```vue
<Tooltip content="Add to favorites" placement="top">
  <Button variant="ghost" icon="heart" />
</Tooltip>
```

---

### Pagination

**Purpose**: Navigate through paginated content.

**Props**:

```typescript
interface PaginationProps {
  currentPage: number
  totalPages: number
  totalItems?: number
  itemsPerPage?: number
  showFirstLast?: boolean
  showPageNumbers?: boolean
  maxVisiblePages?: number
  disabled?: boolean
}
```

**Events**:

- `@update:currentPage` - Emitted when page changes
- `@update:itemsPerPage` - Emitted when items per page changes

**Accessibility**:

- `role="navigation"` with `aria-label="Pagination"`
- Current page announced to screen readers
- Keyboard navigation
- Disabled state for unavailable actions

**Example Usage**:

```vue
<Pagination
  :current-page="page"
  :total-pages="totalPages"
  :total-items="totalPosts"
  :items-per-page="20"
  @update:current-page="handlePageChange"
/>
```

---

## Layout Components

These components provide the structural foundation for pages and sections.

### AppHeader

**Purpose**: Main application header with navigation and user menu.

**Props**:

```typescript
interface AppHeaderProps {
  user?: User | null
  showSearch?: boolean
  sticky?: boolean
}
```

**Events**:

- `@search` - Emitted when search is performed
- `@logout` - Emitted when user logs out

**Accessibility**:

- `role="banner"`
- Skip to main content link
- Keyboard navigation for menu items

**Slots**:

- `logo` - Custom logo content
- `nav` - Custom navigation content
- `actions` - Additional header actions

**Example Usage**:

```vue
<AppHeader :user="currentUser" :sticky="true" @logout="handleLogout" />
```

---

### AppFooter

**Purpose**: Main application footer with links and information.

**Props**:

```typescript
interface AppFooterProps {
  showSocial?: boolean
  showNewsletter?: boolean
}
```

**Accessibility**:

- `role="contentinfo"`
- Semantic link structure
- Keyboard navigation

**Slots**:

- `links` - Custom footer links
- `legal` - Legal/policy links
- `social` - Social media links

**Example Usage**:

```vue
<AppFooter :show-social="true" />
```

---

### Sidebar

**Purpose**: Collapsible side navigation or content.

**Props**:

```typescript
interface SidebarProps {
  modelValue: boolean // Open/closed state
  position?: 'left' | 'right'
  overlay?: boolean
  width?: string
  collapsible?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when open state changes

**Accessibility**:

- Focus trap when overlay is open
- Escape key to close
- `aria-label` for navigation
- Screen reader announcements

**Slots**:

- `default` - Sidebar content

**Example Usage**:

```vue
<Sidebar v-model="sidebarOpen" position="left" :overlay="true">
  <FilterPanel v-model="filters" />
</Sidebar>
```

---

### Container

**Purpose**: Responsive content container with max-width constraints.

**Props**:

```typescript
interface ContainerProps {
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full'
  padding?: boolean
  centered?: boolean
}
```

**Slots**:

- `default` - Container content

**Example Usage**:

```vue
<Container max-width="lg" :padding="true" :centered="true">
  <PostList :posts="posts" />
</Container>
```

---

### Card

**Purpose**: Content container with optional header, body, and footer.

**Props**:

```typescript
interface CardProps {
  title?: string
  subtitle?: string
  hoverable?: boolean
  clickable?: boolean
  variant?: 'default' | 'outlined' | 'elevated'
  padding?: 'none' | 'sm' | 'md' | 'lg'
}
```

**Events**:

- `@click` - Emitted when card is clicked (if clickable)

**Accessibility**:

- Semantic HTML structure
- Keyboard navigation if clickable
- Focus visible styling

**Slots**:

- `header` - Card header content
- `default` - Card body content
- `footer` - Card footer content
- `actions` - Action buttons in header

**Example Usage**:

```vue
<Card title="Recent Posts" :hoverable="true">
  <template #actions>
    <Button variant="ghost" size="sm">View All</Button>
  </template>

  <PostList :posts="recentPosts" />
</Card>
```

---

### Section

**Purpose**: Semantic page section with optional heading.

**Props**:

```typescript
interface SectionProps {
  title?: string
  subtitle?: string
  spacing?: 'none' | 'sm' | 'md' | 'lg' | 'xl'
  background?: 'default' | 'alternate' | 'accent'
}
```

**Accessibility**:

- `<section>` element with appropriate ARIA label
- Heading hierarchy maintained

**Slots**:

- `default` - Section content

**Example Usage**:

```vue
<Section title="My Bookmarks" spacing="lg">
  <BookmarkList :bookmarks="bookmarks" />
</Section>
```

---

## Domain-Specific Components

These components are specific to BibSonomy's domain model and business logic.

### PostCard

**Purpose**: Display a single post (bookmark or publication) in a card layout.

**Props**:

```typescript
interface Post {
  id: string
  resourceType: 'bookmark' | 'publication'
  title: string
  description?: string
  url?: string // For bookmarks
  bibtex?: BibtexEntry // For publications
  tags: Tag[]
  user: User
  groups: Group[]
  created: string
  updated?: string
  documents?: Document[]
}

interface PostCardProps {
  post: Post
  variant?: 'compact' | 'detailed' | 'list'
  showActions?: boolean
  showUser?: boolean
  showGroups?: boolean
  showDescription?: boolean
  highlightTags?: string[] // Tags to highlight
  clickable?: boolean
}
```

**Events**:

- `@click` - Emitted when card is clicked (if clickable)
- `@delete` - Emitted when delete action is triggered
- `@edit` - Emitted when edit action is triggered
- `@tag-click` - Emitted when tag is clicked, passes tag
- `@user-click` - Emitted when user is clicked, passes user

**Accessibility**:

- `<article>` element with appropriate ARIA label
- Semantic heading for post title
- Action buttons properly labeled
- Keyboard navigation support

**Slots**:

- `actions` - Custom action buttons
- `footer` - Custom footer content

**Example Usage**:

```vue
<PostCard
  :post="post"
  variant="detailed"
  :show-actions="isOwner"
  :show-user="true"
  :show-groups="true"
  @delete="handleDelete"
  @edit="handleEdit"
  @tag-click="navigateToTag"
/>
```

---

### PostList

**Purpose**: Display a list of posts with filtering and sorting.

**Props**:

```typescript
interface PostListProps {
  posts: Post[]
  loading?: boolean
  variant?: 'grid' | 'list'
  showFilters?: boolean
  showSort?: boolean
  sortOptions?: SortOption[]
  defaultSort?: string
  emptyMessage?: string
  showUser?: boolean
  showActions?: boolean
}
```

**Events**:

- `@post-click` - Emitted when post is clicked
- `@delete` - Emitted when post delete is triggered
- `@edit` - Emitted when post edit is triggered
- `@sort-change` - Emitted when sort option changes
- `@filter-change` - Emitted when filters change

**Accessibility**:

- List role with proper labeling
- Loading state announced
- Empty state message

**Slots**:

- `empty` - Custom empty state content
- `header` - List header content
- `filters` - Custom filter components

**Example Usage**:

```vue
<PostList
  :posts="posts"
  :loading="isLoading"
  variant="grid"
  :show-filters="true"
  :show-sort="true"
  :show-actions="canEdit"
  @delete="handleDelete"
  @sort-change="handleSort"
/>
```

---

### TagCloud

**Purpose**: Visual representation of tags with size based on frequency.

**Props**:

```typescript
interface TagWithCount {
  name: string
  count: number
  color?: string
}

interface TagCloudProps {
  tags: TagWithCount[]
  minSize?: number
  maxSize?: number
  variant?: 'default' | 'colored'
  clickable?: boolean
  limit?: number
}
```

**Events**:

- `@tag-click` - Emitted when tag is clicked, passes tag

**Accessibility**:

- List role with tag count announced
- Keyboard navigation
- Focus visible styling

**Example Usage**:

```vue
<TagCloud
  :tags="popularTags"
  :clickable="true"
  :limit="50"
  variant="colored"
  @tag-click="navigateToTag"
/>
```

---

### TagList

**Purpose**: Display a simple list of tags.

**Props**:

```typescript
interface Tag {
  name: string
  color?: string
}

interface TagListProps {
  tags: Tag[]
  variant?: 'default' | 'badge' | 'inline'
  removable?: boolean
  clickable?: boolean
  limit?: number
  showMore?: boolean
}
```

**Events**:

- `@tag-click` - Emitted when tag is clicked
- `@tag-remove` - Emitted when tag is removed
- `@show-more` - Emitted when show more is clicked

**Accessibility**:

- Semantic list
- Remove buttons properly labeled
- Keyboard navigation

**Example Usage**:

```vue
<TagList
  :tags="post.tags"
  variant="badge"
  :removable="canEdit"
  :clickable="true"
  @tag-click="navigateToTag"
  @tag-remove="removeTag"
/>
```

---

### TagInput

**Purpose**: Input field for adding tags with autocomplete.

**Props**:

```typescript
interface TagInputProps {
  modelValue: Tag[]
  suggestions?: Tag[]
  maxTags?: number
  placeholder?: string
  label?: string
  required?: boolean
  error?: string
  allowCustom?: boolean
  caseSensitive?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when tags change
- `@search` - Emitted when user types, passes query string
- `@tag-add` - Emitted when tag is added
- `@tag-remove` - Emitted when tag is removed

**Accessibility**:

- Combobox ARIA pattern
- Keyboard navigation (Arrow keys, Enter, Backspace)
- Screen reader announcements for tag addition/removal
- Autocomplete suggestions announced

**Example Usage**:

```vue
<TagInput
  v-model="tags"
  :suggestions="popularTags"
  label="Tags"
  placeholder="Add tags..."
  :allow-custom="true"
  :max-tags="10"
  @search="handleTagSearch"
/>
```

---

### UserAvatar

**Purpose**: Display user profile picture or initials.

**Props**:

```typescript
interface User {
  name: string
  email?: string
  avatar?: string
}

interface UserAvatarProps {
  user: User
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  showName?: boolean
  showStatus?: boolean
  status?: 'online' | 'offline' | 'away'
  clickable?: boolean
}
```

**Events**:

- `@click` - Emitted when avatar is clicked

**Accessibility**:

- `alt` text for images
- Proper contrast for initials
- Status announced to screen readers

**Example Usage**:

```vue
<UserAvatar
  :user="currentUser"
  size="md"
  :show-name="true"
  :show-status="true"
  status="online"
  :clickable="true"
  @click="navigateToProfile"
/>
```

---

### GroupBadge

**Purpose**: Display group membership badge.

**Props**:

```typescript
interface Group {
  name: string
  visibility: 'public' | 'private'
  icon?: string
}

interface GroupBadgeProps {
  group: Group
  size?: 'sm' | 'md' | 'lg'
  showIcon?: boolean
  clickable?: boolean
  removable?: boolean
}
```

**Events**:

- `@click` - Emitted when badge is clicked
- `@remove` - Emitted when remove is clicked

**Accessibility**:

- Visibility status announced
- Remove button labeled
- Keyboard navigation

**Example Usage**:

```vue
<GroupBadge
  :group="group"
  :clickable="true"
  :removable="canEdit"
  @click="navigateToGroup"
  @remove="removeFromGroup"
/>
```

---

### BibtexEntryDisplay

**Purpose**: Display formatted BibTeX publication entry.

**Props**:

```typescript
interface BibtexEntry {
  entryType: string
  bibtexKey: string
  title: string
  author?: string
  year?: string
  journal?: string
  booktitle?: string
  publisher?: string
  [key: string]: string | undefined
}

interface BibtexEntryDisplayProps {
  entry: BibtexEntry
  variant?: 'full' | 'compact' | 'citation'
  showCopyButton?: boolean
  showExportButton?: boolean
  highlightFields?: string[]
}
```

**Events**:

- `@copy` - Emitted when BibTeX is copied
- `@export` - Emitted when export is triggered

**Accessibility**:

- Semantic definition list for fields
- Copy button with feedback
- Screen reader friendly formatting

**Slots**:

- `actions` - Custom action buttons

**Example Usage**:

```vue
<BibtexEntryDisplay
  :entry="publication.bibtex"
  variant="full"
  :show-copy-button="true"
  :show-export-button="true"
  @copy="handleCopy"
/>
```

---

### BookmarkDisplay

**Purpose**: Display bookmark with preview and metadata.

**Props**:

```typescript
interface Bookmark {
  url: string
  title: string
  description?: string
  favicon?: string
  preview?: string
  created: string
}

interface BookmarkDisplayProps {
  bookmark: Bookmark
  variant?: 'card' | 'list' | 'compact'
  showPreview?: boolean
  showFavicon?: boolean
  showMetadata?: boolean
  openInNewTab?: boolean
}
```

**Events**:

- `@click` - Emitted when bookmark is clicked

**Accessibility**:

- External link indication
- `rel="noopener noreferrer"` for security
- Clear link text

**Example Usage**:

```vue
<BookmarkDisplay
  :bookmark="bookmark"
  variant="card"
  :show-preview="true"
  :show-metadata="true"
  :open-in-new-tab="true"
/>
```

---

### SearchBar

**Purpose**: Main search interface for posts, users, and tags.

**Props**:

```typescript
interface SearchBarProps {
  modelValue: string
  placeholder?: string
  scopes?: SearchScope[] // ['posts', 'users', 'tags', 'groups']
  defaultScope?: SearchScope
  suggestions?: SearchSuggestion[]
  loading?: boolean
  autofocus?: boolean
}

interface SearchSuggestion {
  type: 'tag' | 'user' | 'post'
  label: string
  value: string
}
```

**Events**:

- `@update:modelValue` - Emitted when search query changes
- `@search` - Emitted when search is submitted
- `@scope-change` - Emitted when search scope changes
- `@suggestion-select` - Emitted when suggestion is selected

**Accessibility**:

- Combobox ARIA pattern
- Keyboard navigation
- Search scope announced
- Results count announced

**Example Usage**:

```vue
<SearchBar
  v-model="searchQuery"
  :scopes="['posts', 'users', 'tags']"
  default-scope="posts"
  :suggestions="suggestions"
  :loading="isSearching"
  @search="handleSearch"
  @scope-change="handleScopeChange"
/>
```

---

### FilterPanel

**Purpose**: Advanced filtering interface for posts.

**Props**:

```typescript
interface FilterOptions {
  resourceType?: 'all' | 'bookmark' | 'publication'
  tags?: string[]
  users?: string[]
  groups?: string[]
  dateRange?: {
    from: string
    to: string
  }
  visibility?: 'all' | 'public' | 'private'
}

interface FilterPanelProps {
  modelValue: FilterOptions
  availableTags?: Tag[]
  availableUsers?: User[]
  availableGroups?: Group[]
  collapsible?: boolean
  showReset?: boolean
}
```

**Events**:

- `@update:modelValue` - Emitted when filters change
- `@reset` - Emitted when filters are reset
- `@apply` - Emitted when filters are applied

**Accessibility**:

- Fieldset grouping
- Clear filter descriptions
- Reset button clearly labeled

**Example Usage**:

```vue
<FilterPanel
  v-model="filters"
  :available-tags="allTags"
  :collapsible="true"
  :show-reset="true"
  @apply="applyFilters"
  @reset="resetFilters"
/>
```

---

### ExportButton

**Purpose**: Export posts in various formats.

**Props**:

```typescript
interface ExportFormat {
  label: string
  value: 'bibtex' | 'json' | 'xml' | 'csv' | 'html'
  icon?: string
}

interface ExportButtonProps {
  formats: ExportFormat[]
  defaultFormat?: string
  disabled?: boolean
  loading?: boolean
}
```

**Events**:

- `@export` - Emitted when export is triggered, passes format

**Accessibility**:

- Dropdown menu accessible
- Format descriptions clear
- Loading state announced

**Example Usage**:

```vue
<ExportButton
  :formats="exportFormats"
  default-format="bibtex"
  :loading="isExporting"
  @export="handleExport"
/>
```

---

### ImportDropzone

**Purpose**: Drag-and-drop file import interface.

**Props**:

```typescript
interface ImportDropzoneProps {
  accept?: string[] // File types (e.g., ['.bib', '.json'])
  maxSize?: number // Max file size in bytes
  multiple?: boolean
  disabled?: boolean
  hint?: string
}
```

**Events**:

- `@files-selected` - Emitted when files are selected
- `@error` - Emitted on validation error

**Accessibility**:

- Keyboard accessible file input
- Screen reader instructions
- Error messages announced
- Focus management

**Example Usage**:

```vue
<ImportDropzone
  :accept="['.bib', '.json', '.xml']"
  :max-size="10485760"
  :multiple="true"
  hint="Drop BibTeX or JSON files here"
  @files-selected="handleImport"
  @error="handleError"
/>
```

---

### DocumentList

**Purpose**: Display and manage attached documents (PDFs).

**Props**:

```typescript
interface Document {
  id: string
  filename: string
  size: number
  mimeType: string
  url: string
  uploadedAt: string
}

interface DocumentListProps {
  documents: Document[]
  editable?: boolean
  showDownload?: boolean
  showPreview?: boolean
  maxFileSize?: number
}
```

**Events**:

- `@upload` - Emitted when new document is uploaded
- `@delete` - Emitted when document is deleted
- `@preview` - Emitted when document preview is requested

**Accessibility**:

- File list with proper labeling
- Download links clearly labeled
- Upload status announced
- File size in human-readable format

**Example Usage**:

```vue
<DocumentList
  :documents="post.documents"
  :editable="canEdit"
  :show-download="true"
  :show-preview="true"
  @upload="handleUpload"
  @delete="handleDelete"
/>
```

---

## Form Components

These components combine base UI elements into complete forms for specific actions.

### LoginForm

**Purpose**: User authentication form.

**Props**:

```typescript
interface LoginFormProps {
  loading?: boolean
  error?: string
  showRegisterLink?: boolean
  showForgotPassword?: boolean
  rememberMe?: boolean
}
```

**Events**:

- `@submit` - Emitted when form is submitted, passes credentials
- `@register-click` - Emitted when register link is clicked
- `@forgot-password` - Emitted when forgot password is clicked

**Accessibility**:

- Proper form labels
- Error messages associated with fields
- Submit button disabled during loading
- Keyboard navigation

**Example Usage**:

```vue
<LoginForm
  :loading="isLoggingIn"
  :error="loginError"
  :show-register-link="true"
  :show-forgot-password="true"
  @submit="handleLogin"
  @register-click="navigateToRegister"
/>
```

---

### RegisterForm

**Purpose**: New user registration form.

**Props**:

```typescript
interface RegisterFormProps {
  loading?: boolean
  error?: string
  showLoginLink?: boolean
  requireEmailVerification?: boolean
}
```

**Events**:

- `@submit` - Emitted when form is submitted, passes user data
- `@login-click` - Emitted when login link is clicked

**Accessibility**:

- Password strength indicator
- Email validation feedback
- Terms acceptance required
- Error messages clear and associated

**Example Usage**:

```vue
<RegisterForm
  :loading="isRegistering"
  :error="registerError"
  :show-login-link="true"
  :require-email-verification="true"
  @submit="handleRegister"
/>
```

---

### PostForm

**Purpose**: Create or edit bookmark or publication.

**Props**:

```typescript
interface PostFormProps {
  mode: 'create' | 'edit'
  resourceType: 'bookmark' | 'publication'
  initialData?: Partial<Post>
  loading?: boolean
  error?: string
  availableTags?: Tag[]
  availableGroups?: Group[]
}
```

**Events**:

- `@submit` - Emitted when form is submitted, passes post data
- `@cancel` - Emitted when form is cancelled
- `@resource-type-change` - Emitted when resource type changes

**Accessibility**:

- Multi-step form with progress indication
- Required fields marked
- Validation feedback
- Autosave indication

**Slots**:

- `actions` - Custom form actions

**Example Usage**:

```vue
<PostForm
  mode="create"
  resource-type="bookmark"
  :available-tags="tags"
  :available-groups="groups"
  :loading="isSaving"
  @submit="handleSave"
  @cancel="handleCancel"
/>
```

---

### UserSettingsForm

**Purpose**: Update user profile and preferences.

**Props**:

```typescript
interface UserSettings {
  name: string
  email: string
  language: 'en' | 'de'
  timezone: string
  emailNotifications: boolean
  visibility: 'public' | 'private'
}

interface UserSettingsFormProps {
  initialData: UserSettings
  loading?: boolean
  error?: string
}
```

**Events**:

- `@submit` - Emitted when form is submitted
- `@cancel` - Emitted when form is cancelled
- `@change-password` - Emitted when change password is requested

**Accessibility**:

- Section grouping with fieldsets
- Toggle switches labeled
- Timezone select accessible
- Changes saved indication

**Example Usage**:

```vue
<UserSettingsForm
  :initial-data="userSettings"
  :loading="isSaving"
  :error="saveError"
  @submit="handleSave"
  @change-password="showPasswordDialog"
/>
```

---

### GroupForm

**Purpose**: Create or edit group.

**Props**:

```typescript
interface GroupFormProps {
  mode: 'create' | 'edit'
  initialData?: Partial<Group>
  loading?: boolean
  error?: string
}
```

**Events**:

- `@submit` - Emitted when form is submitted
- `@cancel` - Emitted when form is cancelled

**Accessibility**:

- Visibility options clearly explained
- Member management accessible
- Invitation system keyboard accessible

**Example Usage**:

```vue
<GroupForm mode="create" :loading="isSaving" @submit="handleSave" @cancel="handleCancel" />
```

---

## Navigation Components

These components handle application navigation and routing.

### Breadcrumbs

**Purpose**: Show current location in navigation hierarchy.

**Props**:

```typescript
interface BreadcrumbItem {
  label: string
  to?: string | RouteLocationRaw
  icon?: string
}

interface BreadcrumbsProps {
  items: BreadcrumbItem[]
  separator?: string
}
```

**Accessibility**:

- `role="navigation"` with `aria-label="Breadcrumb"`
- Current page indicated with `aria-current="page"`
- Separator in CSS (not read by screen readers)

**Example Usage**:

```vue
<Breadcrumbs :items="breadcrumbItems" separator="/" />
```

---

### TopNav

**Purpose**: Main navigation menu.

**Props**:

```typescript
interface NavItem {
  label: string
  to: string | RouteLocationRaw
  icon?: string
  badge?: string | number
  children?: NavItem[]
}

interface TopNavProps {
  items: NavItem[]
  variant?: 'horizontal' | 'vertical'
  activeClass?: string
}
```

**Accessibility**:

- `role="navigation"`
- Current page highlighted
- Keyboard navigation
- Submenu accessibility

**Example Usage**:

```vue
<TopNav :items="navItems" variant="horizontal" />
```

---

### UserMenu

**Purpose**: Dropdown menu for user actions.

**Props**:

```typescript
interface UserMenuProps {
  user: User
  showProfile?: boolean
  showSettings?: boolean
  showLogout?: boolean
}
```

**Events**:

- `@profile-click` - Emitted when profile is clicked
- `@settings-click` - Emitted when settings is clicked
- `@logout` - Emitted when logout is clicked

**Accessibility**:

- Dropdown menu accessible
- User name announced
- Keyboard navigation

**Example Usage**:

```vue
<UserMenu
  :user="currentUser"
  :show-profile="true"
  :show-settings="true"
  :show-logout="true"
  @logout="handleLogout"
/>
```

---

### LanguageSwitcher

**Purpose**: Switch application language.

**Props**:

```typescript
interface Language {
  code: string
  name: string
  nativeName: string
}

interface LanguageSwitcherProps {
  languages: Language[]
  currentLanguage: string
  variant?: 'dropdown' | 'buttons'
}
```

**Events**:

- `@change` - Emitted when language changes, passes language code

**Accessibility**:

- Current language announced
- Language names in native language
- Keyboard accessible

**Example Usage**:

```vue
<LanguageSwitcher
  :languages="availableLanguages"
  :current-language="currentLocale"
  variant="dropdown"
  @change="handleLanguageChange"
/>
```

---

## Usage Guidelines

### Component Composition

Components should be composed from smaller, reusable pieces:

```vue
<!-- Good: Composed from base components -->
<Card>
  <template #header>
    <h2>{{ post.title }}</h2>
  </template>

  <p>{{ post.description }}</p>

  <TagList :tags="post.tags" />

  <template #footer>
    <Button @click="edit">Edit</Button>
    <Button variant="danger" @click="remove">Delete</Button>
  </template>
</Card>
```

### TypeScript Types

All components should have well-defined TypeScript interfaces for props and events:

```typescript
// types/components.ts
export interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

export interface ButtonEmits {
  (event: 'click', payload: MouseEvent): void
}
```

### Accessibility Requirements

All components must meet WCAG 2.1 Level AA standards:

1. Semantic HTML elements
2. Proper ARIA attributes
3. Keyboard navigation
4. Focus management
5. Screen reader announcements
6. Color contrast ratios
7. Text alternatives for images
8. Error message associations

### Internationalization

All user-facing text should use vue-i18n:

```vue
<template>
  <Button>{{ t('common.save') }}</Button>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>
```

### Testing

Each component should have:

1. **Unit tests** for isolated logic
2. **Integration tests** for user interactions
3. **Accessibility tests** using jest-axe or similar
4. **Visual regression tests** for critical components

### Documentation

Each component should include:

1. JSDoc comments for props and events
2. Usage examples in documentation
3. Storybook stories for visual documentation
4. Accessibility notes

---

## Implementation Checklist

When implementing a component:

- [ ] Define TypeScript interfaces for props and events
- [ ] Implement accessibility requirements
- [ ] Add internationalization support
- [ ] Write unit and integration tests
- [ ] Add Storybook story
- [ ] Document usage examples
- [ ] Implement responsive design
- [ ] Test keyboard navigation
- [ ] Test with screen reader
- [ ] Check color contrast
- [ ] Add loading and error states
- [ ] Implement empty states
- [ ] Add hover and focus styles
- [ ] Test on mobile devices

---

## Related Documentation

- [Frontend Architecture](FRONTEND_ARCHITECTURE.md) - Overall frontend structure
- [API Integration](API_INTEGRATION.md) - How to call REST API v2
- [i18n Guide](I18N_GUIDE.md) - Internationalization patterns
- [Testing Strategy](TESTING_STRATEGY.md) - Testing approach and examples
- [Accessibility Guide](ACCESSIBILITY_GUIDE.md) - A11y requirements and patterns
