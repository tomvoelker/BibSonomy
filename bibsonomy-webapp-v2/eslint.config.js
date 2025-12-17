import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import * as parserVue from 'vue-eslint-parser'
import * as parserTypeScript from '@typescript-eslint/parser'
import pluginTypeScript from '@typescript-eslint/eslint-plugin'
import pluginPrettier from 'eslint-plugin-prettier'
import configPrettier from 'eslint-config-prettier'

export default [
  // Base config for all files
  {
    ignores: ['**/dist/**', '**/node_modules/**', '**/.vite/**', '**/coverage/**', '**/*.d.ts'],
  },

  // JavaScript base rules
  js.configs.recommended,

  // Add browser globals
  {
    languageOptions: {
      globals: {
        console: 'readonly',
        window: 'readonly',
        document: 'readonly',
        localStorage: 'readonly',
        sessionStorage: 'readonly',
        setTimeout: 'readonly',
        clearTimeout: 'readonly',
        setInterval: 'readonly',
        clearInterval: 'readonly',
        global: 'readonly',
      },
    },
  },

  // Vue files
  ...pluginVue.configs['flat/recommended'],
  {
    files: ['**/*.vue'],
    languageOptions: {
      parser: parserVue,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        parser: parserTypeScript,
        extraFileExtensions: ['.vue'],
      },
    },
    rules: {
      // Vue-specific rules
      'vue/multi-word-component-names': 'off',
      'vue/require-default-prop': 'off',
      'vue/no-v-html': 'warn',
      'vue/component-api-style': ['error', ['script-setup']],
      'vue/block-lang': [
        'error',
        {
          script: {
            lang: 'ts',
          },
        },
      ],
      'vue/component-name-in-template-casing': ['error', 'PascalCase'],
      'vue/custom-event-name-casing': ['error', 'camelCase'],
      'vue/define-macros-order': [
        'error',
        {
          order: ['defineProps', 'defineEmits'],
        },
      ],
      'vue/html-comment-content-spacing': ['error', 'always'],
      'vue/padding-line-between-blocks': ['error', 'always'],
    },
  },

  // TypeScript files (excluding config files)
  {
    files: ['src/**/*.ts', 'src/**/*.tsx', 'src/**/*.vue'],
    plugins: {
      '@typescript-eslint': pluginTypeScript,
    },
    languageOptions: {
      parser: parserTypeScript,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        project: './tsconfig.app.json',
      },
    },
    rules: {
      ...pluginTypeScript.configs.recommended.rules,
      ...pluginTypeScript.configs['recommended-requiring-type-checking'].rules,
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
        },
      ],
      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-non-null-assertion': 'warn',
      '@typescript-eslint/consistent-type-imports': [
        'error',
        {
          prefer: 'type-imports',
        },
      ],
    },
  },

  // Config files (no type checking)
  {
    files: ['*.config.js', '*.config.ts', 'vite.config.ts', 'vitest.config.ts'],
    plugins: {
      '@typescript-eslint': pluginTypeScript,
    },
    languageOptions: {
      parser: parserTypeScript,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
      },
    },
    rules: {
      ...pluginTypeScript.configs.recommended.rules,
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
        },
      ],
    },
  },

  // Test files
  {
    files: ['**/*.test.ts', '**/*.spec.ts', '**/tests/**'],
    rules: {
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-non-null-assertion': 'off',
    },
  },

  // Prettier integration
  {
    plugins: {
      prettier: pluginPrettier,
    },
    rules: {
      ...configPrettier.rules,
      'prettier/prettier': 'error',
    },
  },
]
