# Git branches
A git branch "branches off" from a certain commit, usually from a commit in `main`.

Additional commits to the branch aren't added to `main`, just to the separate branch.
When a feature is done, the changes can be merged back into `main`.

Git always has a certain branch "checked out" and active. To see what branch you're in, run:
```
git branch
```
with no arguments.

# Creating branches

Before making a branch, try to make sure your `main` branch is up-to-date with the server.
New git branches can be created on the command line:

```
git switch main             // Make sure we're on main
git pull                    // Make sure main is up-to-date
git branch <BRANCH_NAME>
git switch <BRANCH_NAME>
```

For example, to make a feature branch called "git\_branches":
```
git switch main             // Make sure we're on main
git pull                    // Make sure main is up-to-date
git branch git_branches
git switch git_branches
```

# Making edits in branches

Just add files and make commits normally.

```
git add *
git commit -m "Add git branch documentation"
```

We're treating feature branches as belonging to their creators.

You wouldn't want to go back and edit old git commits in `main`, because others depend on them.
But it's fine to do so in your feature branches.

# Pushing to the server

When you go to push code to the server, you'll want to push it to the _branch_ on the server,
not to main on the server.

The server should be called "origin" in git, so you can do:
```
git push -u origin <BRANCH_NAME>
```

In our example branch:

```
git push -u origin git_branches
```

# Merging -- Pull Request

Opening up Gitea, you should see a message saying your branch was pushed recently, and offering to open a pull request.

When you do so, you can add a message for your pull request, and others can review it.

When the PR is done, you can have Gitea merge it. Usually it can do so automatically.

# PR merge conflicts

If `main` gets updated past where it was when you created your branch, you'll need to sync the changes to the branch,
before Gitea can merge the changes back into main.

When PRs get out-of-sync, Gitea will offer to sync the branch, using merge or rebase.
Mark prefers that you choose rebase, to avoid extra merge commits in the git history, but it doesn't really matter.

If there's a merge commit, Gitea can't sync automatically. To fix it, go to your branch on your machine, and do:
```
git pull origin main
```

This will pull changes from `main`, and complain that there's a merge conflict.
Go to the conflicted files, and manually resolve the conflict.
Then, push back to your branch.

```
git push
```

Since you've already told git that the local branch is associated with `origin <BRANCH_NAME>`, this should just work.
After you push, the pull request should recognize that the file can be merged.


