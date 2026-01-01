# GroupDetailPage

## Route

`/groups/:groupname`

## Access Control

- [x] Public (for public/viewable groups)
- [x] Requires Authentication (for private groups - members only)
- [ ] Requires Admin

Access depends on group visibility:

- Public groups: Anyone can view
- Viewable groups: Anyone can view, members can post
- Private groups: Members only

## Page Purpose

Display group details, members, posts, and activity. Members can view posts shared with the group, manage membership, and access group settings (admins only).

## Layout Structure

- **Header**: Global navigation
- **Group Header**:
  - Group icon/avatar
  - Group name (display name)
  - Description
  - Visibility badge
  - Member count
  - Admin actions (Edit, Settings - admins only)
  - Join/Leave/Request button
- **Tab Navigation**:
  - Posts (default)
  - Members
  - About
- **Main Content Area**:
  - Content changes based on active tab
  - Posts tab: Group posts with filters
  - Members tab: Member list with roles
  - About tab: Full description, hierarchy, preset tags
- **Sidebar** (right):
  - Quick Stats (posts, members)
  - Parent/Subgroups (if hierarchical)
  - Admins list
  - Preset tags

## Components Used

- `AppHeader`
- `GroupHeader`
- `GroupAvatar`
- `VisibilityBadge`
- `JoinGroupButton`
- `LeaveGroupButton`
- `RequestJoinButton`
- `TabNav`
- `PostList`
- `MemberList`
- `MemberCard`
- `GroupAboutSection`
- `GroupHierarchy`
- `AdminsList`
- `PresetTagsList`
- `EditGroupButton`

## API Calls

```typescript
// On mount
GET /api/v2/groups/{groupname}

// Posts tab
GET /api/v2/groups/{groupname}/posts?offset=0&limit=20

// Members tab
GET /api/v2/groups/{groupname}/members

// Join group (public groups)
POST /api/v2/groups/{groupname}/members
{
  "username": "{currentUser}",
  "role": "member"
}

// Request to join (private/viewable groups)
POST /api/v2/groups/{groupname}/join-requests
{
  "message": "I'd like to join..."
}

// Leave group
DELETE /api/v2/groups/{groupname}/members/{currentUser}

// Pending invitations/requests (members only)
GET /api/v2/groups/{groupname}/invitations
GET /api/v2/groups/{groupname}/join-requests
```

## State Management

- **vue-query**:
  - `useQuery(['group', groupname], fetchGroup)`
  - `useQuery(['group-posts', groupname, filters], fetchGroupPosts)`
  - `useQuery(['group-members', groupname], fetchGroupMembers)`
  - `useMutation(joinGroup, { onSuccess: refetch group })`
  - `useMutation(leaveGroup, { onSuccess: refetch group })`
  - `useMutation(requestJoin, { onSuccess: show toast })`
- **Pinia**:
  - `authStore` (check if current user is member/admin)
- **Component State**:
  - `activeTab: 'posts' | 'members' | 'about'`
  - `postFilters: FilterParams`
  - `isMember: boolean`
  - `isAdmin: boolean`

## User Interactions

1. User clicks "Posts" tab → Shows group posts
2. User clicks "Members" tab → Shows member list
3. User clicks "About" tab → Shows full description and hierarchy
4. User clicks "Join" (public group) → Calls POST → Becomes member → Button changes to "Leave"
5. User clicks "Request to Join" (private/viewable group) → Opens modal → Submits request → Shows toast
6. User clicks "Leave" → Confirms → Calls DELETE → No longer member
7. User clicks post → Navigates to `/posts/{postId}`
8. User clicks member → Navigates to `/users/{username}`
9. User clicks parent/subgroup → Navigates to that group
10. User clicks "Edit" (admin only) → Navigates to `/groups/{groupname}/edit`
11. User clicks "Settings" (admin only) → Navigates to `/groups/{groupname}/settings`
12. User clicks preset tag → Filters posts by that tag

## URL Parameters & Query Strings

- `groupname`: Group name (path parameter)
- `tab`: Active tab (`posts` | `members` | `about`) (default: `posts`)
- **Posts tab query params**:
  - `resourceType`: `all` | `bookmark` | `bibtex`
  - `tags`: Comma-separated tags
  - `sortBy`: `date` | `title`
  - `order`: `asc` | `desc`

Example: `/groups/research-group?tab=posts&resourceType=bibtex`

## Page States

- **Loading state**: Skeleton UI for group header and content
- **Empty state**:
  - No posts: "No posts in this group yet" (members: "Be the first to share a post!")
  - No members: "No members yet" (should be rare - at least one admin)
- **Error state - 404**: "Group not found"
- **Error state - 403**: "This is a private group. Request to join or ask for an invitation."
- **Error state - 500**: "Failed to load group"
- **Success state**: Group loaded with posts/members/about
- **Join request pending state**: "Your join request is pending approval"

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar
- **Tablet (768-1024px)**: Sidebar below group header, tabs full width
- **Mobile (<768px)**: Single column, sticky tab navigation, floating join/leave button

## Accessibility Requirements

- Page title: "{Group Display Name} - BibSonomy"
- Group header uses semantic HTML (`<header>`, `<h1>` for name)
- Tab navigation with `role="tablist"` and `aria-selected`
- Member list as `<ul>` with member cards as `<li>`
- Join/Leave buttons with clear `aria-label` (e.g., "Join research-group")
- Admin actions only visible/accessible to admins

## i18n Keys

```
page.groupDetail.title
page.groupDetail.tabs.posts
page.groupDetail.tabs.members
page.groupDetail.tabs.about
page.groupDetail.actions.join
page.groupDetail.actions.leave
page.groupDetail.actions.requestJoin
page.groupDetail.actions.edit
page.groupDetail.actions.settings
page.groupDetail.visibility.public
page.groupDetail.visibility.private
page.groupDetail.visibility.viewable
page.groupDetail.stats.members
page.groupDetail.stats.posts
page.groupDetail.members.role.admin
page.groupDetail.members.role.moderator
page.groupDetail.members.role.member
page.groupDetail.members.joined
page.groupDetail.about.description
page.groupDetail.about.hierarchy.parent
page.groupDetail.about.hierarchy.subgroups
page.groupDetail.about.presetTags
page.groupDetail.about.sharedDocuments
page.groupDetail.join.confirm.title
page.groupDetail.join.confirm.message
page.groupDetail.join.success
page.groupDetail.leave.confirm.title
page.groupDetail.leave.confirm.message
page.groupDetail.leave.success
page.groupDetail.requestJoin.modal.title
page.groupDetail.requestJoin.modal.message
page.groupDetail.requestJoin.modal.submit
page.groupDetail.requestJoin.modal.cancel
page.groupDetail.requestJoin.success
page.groupDetail.requestJoin.pending
page.groupDetail.noPosts
page.groupDetail.noPostsMember
page.groupDetail.noMembers
page.groupDetail.notFound
page.groupDetail.forbidden
page.groupDetail.error
```

## Mockup Notes

- Group header with subtle background color
- Group avatar as icon or uploaded image
- Visibility badge color-coded (green=public, yellow=viewable, red=private)
- Join/Leave button as prominent CTA
- Tabs with underline indicator
- Posts displayed as cards (same as PostListPage)
- Members displayed with avatar, name, role badge, joined date
- Admin badge on member cards
- Hierarchy tree diagram for parent/subgroups
- Preset tags as pill-shaped badges
- Admins list in sidebar with avatars
- Quick stats cards with icons
- Smooth transitions when switching tabs
- Join request modal with textarea for message
- Leave confirmation dialog
- Request pending indicator if applicable
