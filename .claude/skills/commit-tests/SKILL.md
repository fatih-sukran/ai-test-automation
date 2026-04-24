---
name: commit-tests
description: Stage and commit test-related changes in this project with a standardized commit message template. Manual-invocation only — Claude will not trigger this automatically. Use when ready to commit after adding or modifying tests.
arguments: [message]
argument-hint: "<short message>"
disable-model-invocation: true
allowed-tools: Bash(git status*) Bash(git diff*) Bash(git add*) Bash(git commit*)
---

# Commit Tests

Stage and commit test changes using a standardized message.

## Current Git State

```!
git status --short
```

Diff summary:

```!
git diff --stat HEAD 2>/dev/null || echo "(no prior commits or diff unavailable)"
```

## Procedure

### 1. Verify the working tree
- Parse the `git status --short` output above. If nothing is staged and nothing is unstaged, report "Nothing to commit" and stop.
- If changes exist outside `src/test/`, `CLAUDE.md`, `.claude/`, or `README.md`, warn the user and ask for explicit confirmation before proceeding — this skill is scoped to test changes.

### 2. Stage relevant paths
- `git add src/test/ CLAUDE.md .claude/ README.md` — but only paths that actually have changes (check with `git status` first).
- Never use `git add .` or `git add -A`.

### 3. Build the commit message
- First line: `test: $0` when the user passes a message, otherwise infer from the diff (e.g. `test: add <resource> CRUD tests`).
- Body (2-4 lines) summarizing:
  - Which endpoints / resources were touched
  - How many new scenarios were added (if known from the diff)
  - Whether step definitions changed
- Keep the first line under 72 characters; prefer conventional-commits style.

### 4. Commit
- Use a heredoc to preserve formatting:
  ```bash
  git commit -m "$(cat <<'EOF'
  test: $0

  <body lines>
  EOF
  )"
  ```
- Do NOT skip hooks (no `--no-verify`).
- Do NOT amend — always create a new commit.

### 5. Report
- Run `git status` once more, then show the new commit hash with `git log -1 --oneline`.
- Do NOT push — pushing is a separate explicit user action.

## Safety

- This skill is `disable-model-invocation: true`: Claude will never trigger it on its own.
- Never force-push, reset, or rebase from this skill.
- If a pre-commit hook fails, report the failure and stop — let the user decide what to do.

## Examples

- `/commit-tests "add store CRUD tests"` → stages test changes and commits with `test: add store CRUD tests`.
- `/commit-tests` (no argument) → Claude infers the message from the staged diff.
