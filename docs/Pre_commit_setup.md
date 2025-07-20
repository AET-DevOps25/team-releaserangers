[← Back to Main README](../README.md).

# Pre-commit Hooks Setup

This repository now includes pre-commit hooks to ensure code quality and security.

## What's Included

The `.pre-commit-config.yaml` file configures the following hooks:

### 1. pnpm lint

- Runs ESLint on TypeScript/JavaScript files in the `client/` directory
- Command: `pnpm lint`

### 2. flake8 Basic Check

- Python code style and complexity check
- Command: `flake8 . --count --exit-zero --max-complexity=10 --max-line-length=127 --statistics`

### 3. flake8 Critical Errors

- Catches critical Python errors
- Command: `flake8 . --count --select=E9,F63,F7,F82 --show-source --statistics`

### 4. Secret Detection

- Scans for potential secrets using detect-secrets
- Uses `.secrets.baseline` for known false positives

### 5. Code Quality

- Trims trailing whitespace
- Fixes end-of-file newlines

## Usage

### Install Pre-commit

```bash
pip install pre-commit
pip install detect-secrets
```

### Run all hooks manually:

```bash
pre-commit run -a
# or
pre-commit run --all-files
```

### Install hooks to run automatically on commits:

```bash
pre-commit install
```

### Run individual commands as specified in the issue:

```bash
# From client directory
cd client && pnpm lint

# From repository root
flake8 . --count --exit-zero --max-complexity=10 --max-line-length=127 --statistics
flake8 . --count --select=E9,F63,F7,F82 --show-source --statistics
```

## Setup

The hooks are already configured and installed. New contributors should:

1. Install pre-commit: `pip install pre-commit`
2. Install hooks: `pre-commit install`
3. Hooks will now run automatically on commits

## Files Excluded

- `node_modules/`
- `.git/`
- `*.lock` files
- Virtual environment directories
