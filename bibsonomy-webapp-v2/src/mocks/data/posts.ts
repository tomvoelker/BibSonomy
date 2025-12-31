/**
 * Mock data for posts (publications and bookmarks)
 * Based on OpenAPI specification
 */

export const mockPosts = [
  {
    id: '1a2b3c4d',
    resourceType: 'publication',
    title: 'Deep Learning for Natural Language Processing',
    description: 'A comprehensive survey of deep learning techniques applied to NLP tasks',
    url: null,
    bibTexData: {
      entryType: 'article',
      bibTexKey: 'smith2023deep',
      author: 'Smith, John and Doe, Jane',
      title: 'Deep Learning for Natural Language Processing',
      journal: 'Journal of Machine Learning Research',
      year: '2023',
      volume: '24',
      number: '5',
      pages: '1234-1256',
      abstract:
        'This paper presents a comprehensive survey of deep learning techniques applied to natural language processing tasks, including sentiment analysis, machine translation, and question answering.',
    },
    user: {
      id: 'user123',
      name: 'john_smith',
      firstName: 'John',
      lastName: 'Smith',
      email: 'john.smith@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'deep-learning', globalCount: 1245 },
      { name: 'nlp', globalCount: 892 },
      { name: 'machine-learning', globalCount: 3421 },
    ],
    createdAt: '2023-12-01T10:30:00Z',
    updatedAt: '2023-12-01T10:30:00Z',
  },
  {
    id: '2b3c4d5e',
    resourceType: 'bookmark',
    title: 'Vue.js 3 Documentation',
    description: 'Official documentation for Vue.js version 3',
    url: 'https://vuejs.org/',
    bibTexData: null,
    user: {
      id: 'user456',
      name: 'jane_doe',
      firstName: 'Jane',
      lastName: 'Doe',
      email: 'jane.doe@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'vue', globalCount: 567 },
      { name: 'javascript', globalCount: 2341 },
      { name: 'frontend', globalCount: 1876 },
      { name: 'documentation', globalCount: 432 },
    ],
    createdAt: '2023-12-02T14:20:00Z',
    updatedAt: '2023-12-02T14:20:00Z',
  },
  {
    id: '3c4d5e6f',
    resourceType: 'publication',
    title: 'Attention Is All You Need',
    description: 'The seminal paper introducing the Transformer architecture',
    url: null,
    bibTexData: {
      entryType: 'inproceedings',
      bibTexKey: 'vaswani2017attention',
      author:
        'Vaswani, Ashish and Shazeer, Noam and Parmar, Niki and Uszkoreit, Jakob and Jones, Llion and Gomez, Aidan N and Kaiser, Łukasz and Polosukhin, Illia',
      title: 'Attention Is All You Need',
      booktitle: 'Advances in Neural Information Processing Systems',
      year: '2017',
      pages: '5998-6008',
      abstract:
        'The dominant sequence transduction models are based on complex recurrent or convolutional neural networks. We propose a new simple network architecture, the Transformer, based solely on attention mechanisms.',
    },
    user: {
      id: 'user789',
      name: 'ml_researcher',
      firstName: 'Research',
      lastName: 'Bot',
      email: 'research@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
      {
        id: 'group2',
        name: 'ml-papers',
      },
    ],
    tags: [
      { name: 'transformer', globalCount: 834 },
      { name: 'attention', globalCount: 567 },
      { name: 'deep-learning', globalCount: 1245 },
      { name: 'nlp', globalCount: 892 },
    ],
    createdAt: '2023-11-28T09:15:00Z',
    updatedAt: '2023-11-28T09:15:00Z',
  },
  {
    id: '4d5e6f7g',
    resourceType: 'bookmark',
    title: 'TypeScript Handbook',
    description: 'The official TypeScript documentation and handbook',
    url: 'https://www.typescriptlang.org/docs/handbook/intro.html',
    bibTexData: null,
    user: {
      id: 'user456',
      name: 'jane_doe',
      firstName: 'Jane',
      lastName: 'Doe',
      email: 'jane.doe@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'typescript', globalCount: 1234 },
      { name: 'javascript', globalCount: 2341 },
      { name: 'programming', globalCount: 3456 },
    ],
    createdAt: '2023-12-03T16:45:00Z',
    updatedAt: '2023-12-03T16:45:00Z',
  },
  {
    id: '5e6f7g8h',
    resourceType: 'publication',
    title: 'Building Maintainable Software',
    description: 'Best practices for writing clean, maintainable code',
    url: null,
    bibTexData: {
      entryType: 'book',
      bibTexKey: 'martin2008clean',
      author: 'Martin, Robert C.',
      title: 'Clean Code: A Handbook of Agile Software Craftsmanship',
      publisher: 'Prentice Hall',
      year: '2008',
      isbn: '978-0132350884',
      abstract:
        "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.",
    },
    user: {
      id: 'user123',
      name: 'john_smith',
      firstName: 'John',
      lastName: 'Smith',
      email: 'john.smith@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'software-engineering', globalCount: 876 },
      { name: 'clean-code', globalCount: 543 },
      { name: 'best-practices', globalCount: 765 },
    ],
    createdAt: '2023-11-30T11:20:00Z',
    updatedAt: '2023-11-30T11:20:00Z',
  },
  {
    id: '6f7g8h9i',
    resourceType: 'bookmark',
    title: 'MDN Web Docs',
    description: 'Resources for developers, by developers',
    url: 'https://developer.mozilla.org/',
    bibTexData: null,
    user: {
      id: 'user456',
      name: 'jane_doe',
      firstName: 'Jane',
      lastName: 'Doe',
      email: 'jane.doe@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'documentation', globalCount: 432 },
      { name: 'web', globalCount: 1234 },
      { name: 'reference', globalCount: 567 },
    ],
    createdAt: '2023-12-04T09:30:00Z',
    updatedAt: '2023-12-04T09:30:00Z',
  },
  {
    id: '7g8h9i0j',
    resourceType: 'publication',
    title: 'The Pragmatic Programmer',
    description: 'Your Journey to Mastery',
    url: null,
    bibTexData: {
      entryType: 'book',
      bibTexKey: 'hunt1999pragmatic',
      author: 'Hunt, Andrew and Thomas, David',
      title: 'The Pragmatic Programmer: Your Journey to Mastery',
      publisher: 'Addison-Wesley Professional',
      year: '2019',
      edition: '2nd',
      isbn: '978-0135957059',
      abstract:
        'A guide to thinking like a programmer and building better software.',
    },
    user: {
      id: 'user789',
      name: 'ml_researcher',
      firstName: 'Research',
      lastName: 'Bot',
      email: 'research@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'programming', globalCount: 3456 },
      { name: 'software-engineering', globalCount: 876 },
      { name: 'best-practices', globalCount: 765 },
    ],
    createdAt: '2023-11-29T14:15:00Z',
    updatedAt: '2023-11-29T14:15:00Z',
  },
  {
    id: '8h9i0j1k',
    resourceType: 'bookmark',
    title: 'GitHub',
    description: 'Where the world builds software',
    url: 'https://github.com',
    bibTexData: null,
    user: {
      id: 'user123',
      name: 'john_smith',
      firstName: 'John',
      lastName: 'Smith',
      email: 'john.smith@example.com',
    },
    groups: [
      {
        id: 'group1',
        name: 'public',
      },
    ],
    tags: [
      { name: 'git', globalCount: 2341 },
      { name: 'version-control', globalCount: 1234 },
      { name: 'collaboration', globalCount: 876 },
    ],
    createdAt: '2023-12-05T16:20:00Z',
    updatedAt: '2023-12-05T16:20:00Z',
  },
]

export const getMockPost = (id: string) => {
  return mockPosts.find((post) => post.id === id)
}

export const getMockPosts = (params?: {
  user?: string
  tag?: string
  resourceType?: 'publication' | 'bookmark'
  limit?: number
  offset?: number
}) => {
  let filtered = [...mockPosts]

  if (params?.user) {
    filtered = filtered.filter((post) => post.user.name === params.user)
  }

  if (params?.tag) {
    filtered = filtered.filter((post) => post.tags.some((tag) => tag.name === params.tag))
  }

  if (params?.resourceType) {
    filtered = filtered.filter((post) => post.resourceType === params.resourceType)
  }

  const total = filtered.length
  const offset = params?.offset ?? 0
  const limit = params?.limit ?? 10

  const paginated = filtered.slice(offset, offset + limit)

  return {
    posts: paginated,
    total,
    offset,
    limit,
  }
}
