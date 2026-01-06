# GroupSettingsPage

## Route

`/groups/:groupname/settings`

## Access Control

- [ ] Public
- [ ] Requires Authentication
- [x] Requires Admin (Group admin or moderator)

## Page Purpose

Manage group settings, members, invitations, and join requests. Only accessible to group admins and moderators.

## Layout Structure

- **Header**: Global navigation with breadcrumb
- **Settings Navigation** (left sidebar or top tabs on mobile):
  - General
  - Members
  - Invitations
  - Join Requests
  - Danger Zone
- **Main Settings Area**:
  - Content changes based on active section
  - Each section has its own form/interface

## Components Used

- `AppHeader`
- `BreadcrumbNav`
- `SettingsNav`
- `GeneralSettingsForm`
- `MemberManagementTable`
- `InvitationsList`
- `InviteUserModal`
- `JoinRequestsList`
- `ApproveRejectButtons`
- `DeleteGroupModal`

## API Calls

```typescript
// Load group details
GET /api/v2/groups/{groupname}

// General Settings - Update group
PUT /api/v2/groups/{groupname}
{
  "displayName": "...",
  "description": "...",
  "visibility": "public",
  "sharedDocuments": true,
  "allowJoinRequests": true,
  "presetTags": ["tag1", "tag2"]
}

// Members Management
GET /api/v2/groups/{groupname}/members

POST /api/v2/groups/{groupname}/members
{
  "username": "newuser",
  "role": "member"
}

PUT /api/v2/groups/{groupname}/members/{username}
{
  "role": "moderator"
}

DELETE /api/v2/groups/{groupname}/members/{username}

// Invitations
GET /api/v2/groups/{groupname}/invitations

POST /api/v2/groups/{groupname}/invitations
{
  "username": "user123",
  "role": "member",
  "message": "Join our group!"
}

DELETE /api/v2/groups/{groupname}/invitations/{username}

// Join Requests
GET /api/v2/groups/{groupname}/join-requests

PUT /api/v2/groups/{groupname}/join-requests/{username}
{
  "action": "approve",
  "role": "member"
}

// or

PUT /api/v2/groups/{groupname}/join-requests/{username}
{
  "action": "reject"
}

// Delete Group
DELETE /api/v2/groups/{groupname}
```

## State Management

- **vue-query**:
  - `useQuery(['group', groupname], fetchGroup)`
  - `useQuery(['group-members', groupname], fetchGroupMembers)`
  - `useQuery(['group-invitations', groupname], fetchGroupInvitations)`
  - `useQuery(['group-join-requests', groupname], fetchGroupJoinRequests)`
  - `useMutation(updateGroup, { onSuccess: refetch })`
  - `useMutation(updateMemberRole)`
  - `useMutation(removeMember)`
  - `useMutation(inviteUser)`
  - `useMutation(cancelInvitation)`
  - `useMutation(approveJoinRequest)`
  - `useMutation(rejectJoinRequest)`
  - `useMutation(deleteGroup, { onSuccess: redirect })`
- **Pinia**:
  - `authStore` (verify admin/moderator status)
- **Component State**:
  - `activeSection: SettingsSection`
  - `showInviteModal: boolean`
  - `showDeleteGroupModal: boolean`

## User Interactions

### General Settings Section

1. User updates display name → Types → Clicks "Save" → Updates group
2. User updates description → Textarea → Saves
3. User changes visibility → Radio buttons → Saves → Shows warning if making private
4. User toggles "Shared Documents" → Checkbox → Saves
5. User toggles "Allow Join Requests" → Checkbox → Saves
6. User updates preset tags → Tag input → Saves

### Members Section

1. User views member list → Table with name, role, joined date
2. User changes member role → Dropdown → Confirms → Updates role
3. User removes member → Click remove → Confirms → Calls DELETE → Removes from list
4. User adds member directly → Click "Add Member" → Opens modal → Searches user → Adds

### Invitations Section

1. User views pending invitations → List with username, invited by, date, status
2. User clicks "Invite User" → Opens modal → Enters username, role, message → Sends invitation
3. User cancels invitation → Click cancel → Confirms → Calls DELETE

### Join Requests Section

1. User views pending join requests → List with username, requested date, message
2. User approves request → Click "Approve" → Selects role → Confirms → Calls PUT with approve
3. User rejects request → Click "Reject" → Confirms → Calls PUT with reject

### Danger Zone Section

1. User clicks "Delete Group" → Opens modal → Enters group name to confirm → Deletes → Redirects to `/groups`

## URL Parameters & Query Strings

- `groupname`: Group name (path parameter)
- `section`: Active settings section (`general` | `members` | `invitations` | `join-requests` | `danger-zone`)

Example: `/groups/research-group/settings?section=members`

## Page States

- **Loading state**: Skeleton UI for settings content
- **Empty state**:
  - No invitations: "No pending invitations"
  - No join requests: "No pending join requests"
- **Error state - 403**: "You don't have permission to manage this group"
- **Error state - 404**: "Group not found"
- **Error state - Save failed**: Toast notification with error
- **Success state**: Settings loaded and editable
- **Saving state**: Save button shows spinner, form disabled

## Responsive Behavior

- **Desktop (>1024px)**: Left sidebar navigation, wide forms
- **Tablet (768-1024px)**: Top tab navigation, medium forms
- **Mobile (<768px)**: Top tab navigation (horizontal scroll), full-width forms, sticky save button

## Accessibility Requirements

- Page title: "Group Settings - {Group Name} - BibSonomy"
- Breadcrumb navigation
- Settings navigation with `role="navigation"`
- Each section has an `<h2>` heading
- Member table with proper headers
- Role dropdown accessible via keyboard
- Confirmation modals trap focus
- Delete group requires typing group name (prevents accidents)

## i18n Keys

```
page.groupSettings.title
page.groupSettings.breadcrumb.groups
page.groupSettings.breadcrumb.settings
page.groupSettings.nav.general
page.groupSettings.nav.members
page.groupSettings.nav.invitations
page.groupSettings.nav.joinRequests
page.groupSettings.nav.dangerZone

# General Section
page.groupSettings.general.title
page.groupSettings.general.name
page.groupSettings.general.displayName
page.groupSettings.general.description
page.groupSettings.general.visibility
page.groupSettings.general.visibility.public
page.groupSettings.general.visibility.private
page.groupSettings.general.visibility.viewable
page.groupSettings.general.sharedDocuments
page.groupSettings.general.allowJoinRequests
page.groupSettings.general.presetTags
page.groupSettings.general.save
page.groupSettings.general.success
page.groupSettings.general.visibilityWarning

# Members Section
page.groupSettings.members.title
page.groupSettings.members.addMember
page.groupSettings.members.username
page.groupSettings.members.role
page.groupSettings.members.joined
page.groupSettings.members.actions
page.groupSettings.members.changeRole
page.groupSettings.members.remove
page.groupSettings.members.remove.confirm
page.groupSettings.members.remove.success
page.groupSettings.members.updateRole.success

# Invitations Section
page.groupSettings.invitations.title
page.groupSettings.invitations.invite
page.groupSettings.invitations.username
page.groupSettings.invitations.invitedBy
page.groupSettings.invitations.invitedAt
page.groupSettings.invitations.role
page.groupSettings.invitations.status
page.groupSettings.invitations.cancel
page.groupSettings.invitations.cancel.confirm
page.groupSettings.invitations.cancel.success
page.groupSettings.invitations.noInvitations
page.groupSettings.invitations.modal.title
page.groupSettings.invitations.modal.username
page.groupSettings.invitations.modal.role
page.groupSettings.invitations.modal.message
page.groupSettings.invitations.modal.send
page.groupSettings.invitations.modal.cancel
page.groupSettings.invitations.modal.success

# Join Requests Section
page.groupSettings.joinRequests.title
page.groupSettings.joinRequests.username
page.groupSettings.joinRequests.requestedAt
page.groupSettings.joinRequests.message
page.groupSettings.joinRequests.actions
page.groupSettings.joinRequests.approve
page.groupSettings.joinRequests.reject
page.groupSettings.joinRequests.approve.confirm
page.groupSettings.joinRequests.approve.success
page.groupSettings.joinRequests.reject.confirm
page.groupSettings.joinRequests.reject.success
page.groupSettings.joinRequests.noRequests

# Danger Zone Section
page.groupSettings.dangerZone.title
page.groupSettings.dangerZone.deleteGroup.title
page.groupSettings.dangerZone.deleteGroup.warning
page.groupSettings.dangerZone.deleteGroup.button
page.groupSettings.dangerZone.deleteGroup.confirm.title
page.groupSettings.dangerZone.deleteGroup.confirm.message
page.groupSettings.dangerZone.deleteGroup.confirm.placeholder
page.groupSettings.dangerZone.deleteGroup.confirm.cancel
page.groupSettings.dangerZone.deleteGroup.confirm.delete
page.groupSettings.dangerZone.deleteGroup.success

# General
page.groupSettings.error.forbidden
page.groupSettings.error.loadFailed
page.groupSettings.error.saveFailed
```

## Mockup Notes

- Settings navigation as left sidebar with icons (similar to UserSettingsPage)
- Each section clearly separated with headings
- General settings form with grouped fields
- Member table with sortable columns
- Role dropdown with visual indicators (admin=red, moderator=yellow, member=gray)
- Remove member button with warning color
- Invite user modal with user search autocomplete
- Invitations list with status badges
- Join requests with approve/reject buttons (green/red)
- Danger Zone section with prominent red warning border
- Delete group modal requires typing group name exactly
- Success toasts after actions
- Confirmation modals for destructive actions
- Smooth transitions when switching sections
