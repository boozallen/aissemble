# Releasing aiSSEMBLE

## Overview
The release itself is configured and performed through the release job and 
[the Maven Release plugin](https://maven.apache.org/maven-release/maven-release-plugin/). The outline below summarizes
the steps of a standard release. Note that for a patch release, [the process](#patch-release-process) is slightly
different.

1. **Pre-Release Steps**
   1. [**Create Release Draft**](#create-release-draft)
   1. [**Preparing the Release Branch**](#preparing-the-release-branch)
   1. [**Testing the Release Branch**](#testing-the-release-branch)
1. [**Performing the Release**](#performing-the-release)
1. **Post-Release Steps**
   1. [**Updating GitHub Issues In-flight**](#updating-github-issues-in-flight)
   1. [**Merging into Master**](#merging-into-master)
   1. [**Merging into Dev**](#merging-into-dev)
   1. [**Documentation**](#documentation)
      1. Finalize the release notes
      1. Create a new fixed version of the technical docs
      1. Create a blog post
   1. [**Testing the Release**](#testing-the-release)

## Pre-Release Steps

### Create Release Draft
1. Make any changes for readability/corrections and commit
1. Create a release on the [aiSSEMBLE GitHub releases page](https://github.com/boozallen/aissemble/releases) and
   copy the contents of _devops/RELEASE_NOTES.md_ into the release draft
1. Title the draft with the _major.minor_ version number (or the full _major.minor.patch_, if a patch release) and click "Save draft". 

### Preparing the Release Branch

Create a **release branch** off of dev named aissemble-release-x.x (e.g. `aissemble-release-1.3`). Prepare the release
branch for the release job with the following steps:

1. Within the `test/src/test/resources/` directory:
   1. Chart.yaml, and Dockerfiles that are currently referencing the SNAPSHOT version should be updated to match the release version 

### Testing the Release Branch

Testing the release job with the release candidate versions is mandatory. This testing is done by running the 
[GitHub Release Action](https://github.com/boozallen/aissemble/actions) with the "releaseVersion" parameter set to a 
release candidate version. This enables a true test of the release before the official deployment. 

_**Note:** If you have changes to the Action release job, ensure that the "Branch Specifier" under "Configure" is set 
to the name of the release branch (e.g. */aissemble-release-1.3)_

1. Click "Build with Parameters"
1. Update the "releaseVersion" parameter to the full `major.minor.patch-rcX` version, where X is the release candidate number. 
1. Run the release job
1. If the release job fails:
   1. Branch off of dev and fix the issue
   1. Recreate the [release branch](#preparing-the-release-branch) from dev after the fix has been merged
   1. Restart this test, incrementing the X in the `major.minor.patch-rcX` in "releaseVersion"
1. Verify the snapshot versions were updated by pulling the release branch, running `mvn clean` and searching for references to `<release_version>.dev` or
   `<release_version>-SNAPSHOT`
1. After a successful RC test release, be sure to delete the previously created release candidate tags on the
   [GitHub tags page](https://github.com/boozallen/aissemble/tags)

### Updating GitHub Issues In-flight

Contact your repository admin, letting them know the current version has been cut and a new Fix Version needs to be 
created. The new Fix Version will be the same as the new development version  without the SNAPSHOT 
(e.g. `1.4.0-SNAPSHOT` → `1.4.0`). To create a new Milestone in GitHub Issues, contact your repository admin. Once the 
new Milestone is created, follow these steps to update all in flight tickets:
1. Navigate to the [the milestone that is being released](https://github.com/boozallen/aissemble/milestones) of GitHub issues
1. Run this query: `project = AIOPS AND status in ("In Definition", "In Progress", "In Review")`
    -  _**Note:** Tickets that are currently in the testing phase have already been merged to dev_
1. In the top right, select "Tools" then "Bulk Change"
1. Select all the issues, except the current ticket for the aiSSEMBLE release, then select "Next"
1. Select "Edit Issues", then select "Next"
1. Check the box for "Change Fix Version/s", then select "Replace all with" from the dropdown and select the new Fix Version from the dropdown below
1. Scroll to the bottom and select "Next", then select "Confirm"

## Performing the Release

### Running the GitHub Action Release Job

The release job can be [found on GitHub Actions](https://github.com/boozallen/aissemble/actions). Ensure that the 
release branch does not have any commits from `GitHub Action`, then follow steps 1-6 of the release candidate testing 
steps, however the "releaseVersion" should exclude the `-rcX` suffix.

### Release failure

Depending on how far into the release job the build failed, it may be necessary to increment the release version. If any
build artifacts were published to the artifact servers, then the next release should be completed with an increase in the 
patch number (e.g. `1.3.0` → `1.3.1`).

Follow the same process for a release candidate failure (step 5) to address the failure, if necessary.

## Post-Release Steps

### Merging into Master

Open a pull request to merge the release branch into `master`. Locally, merge the release tag
(`aissemble-root-{release version}`) into the `master` branch, using the `-X theirs` merge strategy. Verify the diff
between the merge commit and the branch is empty. After approval for the pull request has been confirmed, push the
locally merged `master` branch into remote. GitHub will associate the pushed commit to the previously-raised pull
request and close the PR as merged.

```
git checkout master && git pull
git merge tags/aissemble-root-{RELEASE_VERSION} -X theirs --no-ff
git diff tags/aissemble-root-{RELEASE_VERSION}
git push
```

### Merging into Dev

_**NOTE:** If you're not on a Mac, you may need to remove the empty string argument (`''`) from the `sed` commands._

1. Within the `test/src/test/resources/` directory update the Chart.yaml, and Dockerfiles to match the snapshot version.
1. Clean up the checked-in release notes in `devops/RELEASE_NOTES.md` to remove/update version-specific information.
1. Delete any migrations for the version you're about to release from `foundation-upgrade`.
   1. Edit `foundation/foundation-upgrade/src/main/resources/migrations.json` to remove any migrations with implementations in a versioned package
   1. Delete the source code for the versioned migrations in:
      * `foundation/foundation-upgrade/src/main/java/com/boozallen/aissemble/upgrade/migration/v{RELEASE_VERSION}/`
      * `foundation/foundation-upgrade/src/test/java/com/boozallen/aissemble/upgrade/migration/v{RELEASE_VERSION}/`
      * `foundation/foundation-upgrade/src/test/java/com/boozallen/aissemble/upgrade/migration/extensions/v{RELEASE_VERSION}/`
      ```bash
      rm -rf foundation/foundation-upgrade/src/**/v{MAJ_MIN_PATCH}
      ```
   1. Delete the source code for the versioned migrations in `foundation/foundation-upgrade/src/test/resources/test-files/{RELEASE_VERSION}`
      ```bash
      rm -rf foundation/foundation-upgrade/src/test/resources/test-files/{MAJ.MIN.PATCH}`
      ```

Commit the changes and open a PR to merge the release branch into dev.

### Documentation

Post-release documentation includes posting the official release notes, updating the Antora-based technical 
documentation, and creating a blog post.

#### Release Notes

In the GitHub release notes draft created earlier, update the section **What's Changed** to include info about each 
pull request included in this release. These can be found by reviewing the merge into `master`. This should be a list 
including the commit message, contributor, and link to each pull request. Creating this list can be automated by using 
git's [generated release notes functionality](https://docs.github.com/en/repositories/releasing-projects-on-github/automatically-generated-release-notes). 
Ensure the release notes have the final released _major.minor_ version as the title and are linked to the correct tag
before publishing.

#### Antora Docs

Updating the Antora Docs involves pushing up a docs branch to remote. The docs branch should be named `docs-x.x` 
(where x.x is the current release version), and point to the same commit as the release tag created by the release job 
(`aissemble-root-{release version}`).

### Testing the Release

Once a successful release has been published, the release should be tested.
1. Testing a new project
   1. Create a new project from the archetype of the release candidate
   1. Adding one of each type of pipeline and semantic data models
   1. Completely build and deploy the project
   1. Run each pipeline
1. Testing an upgrade
   1. Create a new project from the archetype of the **previous** release
   1. Adding one of each type of pipeline and semantic data models
   1. Completely build the project 
      - _Note: it is a good idea to save this project state with `git init && git add . && git commit -m'upgrade base'`_
   1. Follow the upgrade instructions as listed in the draft release notes
   1. Completely build and deploy the project
   1. Run each pipeline

If any critical issues are found, follow the patch release process to publish fixes.




## Patch Release Process

The patch release process is largely the same as the standard release process with a few exceptions. The following 
sections will guide you through doing a patch release, calling back to the standard process where appropriate.

### Creating the Release Branch

Create a release branch from the `master` commit corresponding to the version you want to patch named 
aissemble-release-x.x (e.g. aissemble-release-1.2 for patch 1.2.4).

### Preparing the Release Branch

First, the release branch should be bumped to the snapshot version of the eventual patch release (e.g. 1.2.4-SNAPSHOT). 
Complete the following to update a branch to the development iteration:

1. Update the project's various version references throughout the baseline:
   1. To update each `pom.xml`, run this command:
      ```bash
      mvn release:update-versions -DautoVersionSubmodules=true -DdevelopmentVersion={PATCH_SNAPSHOT} -B
      ```
   1. Run the following to update each `pyproject.toml` with the next python development version:
      ```bash
      mvn org.technologybrewery.habushu:habushu-maven-plugin:initialize-habushu -rf :build-parent
      ```
   1. Within `devops/JenkinsfileBuild.groovy`, update `projectVersion` to the development version
   1. Within `docs/modules/ROOT/attachments`, update the `$id` of `json` files with the development version:
      ```bash
      find docs/modules/ROOT/attachments/*.json -type f -exec sed -i '' 's|${OLD_VERSION}|${NEW_VERSION}|g' {} +
      ```
1. Follow steps 1-2 of [Merging into Dev](#merging-into-dev) (omitting the removal of migrations)

The following steps will depend on whether the patch release will contain a single fix, or multiple fixes. In the
latter case, each fix should be flowed as a separate ticket. If only a single fix is to be included, the fix can be
rolled into the actual release ticket instead of flowed separately.

#### Including Multiple Fixes

The process for each bug to be fixed is as follows:

1. Create a ticket for the specific bug to be fixed
   1. Set the **Affects Version** field to both the affected released version (e.g. 1.2.3) and the current minor version 
   in development (e.g. 1.3.0)
   1. Set the **Fix Version field** to both the patch release (1.2.4) and the current minor version in development (e.g. 1.3.0)
1. Create the ticket branch off of the patch release branch
1. Implement and OTS the ticket as usual
1. Create a PR to merge the ticket branch into the release branch
   - Include the info about the fix in  _devops/RELEASE_NOTES.md_
1. Build the release branch in CI after merging
1. Do a "final test" on the patch snapshot version, then move the ticket back to in progress (let the tester know to do this)
1. Create a new ticket branch off of dev (it can be named the same)
1. Cherry-pick the fix from the release branch (the actual commit not the merge commit)
1. Open a PR to merge the cherry-pick branch into dev
1. Build the dev branch in CI after merging
1. Do the actual final test with the same steps but on the dev version (e.g. 1.3.0-SNAPSHOT)

#### Including a Single Fix

If only a single fix is required, simply make and test the fix directly on the release branch. Ensure you also update 
the release notes draft with the relevant fix information.

### Continuing the Process

From this point, follow the standard release process from the section [_Preparing the Release Branch_](#preparing-the-release-branch) 
up to and including the section [_Merging into Dev_](#merging-into-dev).

### Documentation

Post-release documentation includes posting the official release notes, and updating the Antora-based technical documentation.

#### Release Notes

Publish the trimmed down release notes for the patch release, linking back to the original major/minor release for more 
details. Update the upgrade instructions for the major/minor release notes to use the new patch version number directly.

#### Antora Docs

There should already be an existing branch containing the antora docs named docs-{version} (e.g. `docs-1.2`). Checkout 
this branch and run the following commands to update it to the latest patch release version.

```
git checkout docs-{MAJ.MIN}
git reset --hard aissemble-root-{MAJ.MIN.PATCH}
git push --force
```

Next run the [publish job](https://github.com/boozallen/aissemble/actions) from GitHub to rebuild the antora docs. 
Start the job by clicking "Run workflow". The branch to run the workflow on can remain as `dev`. Then click 
"Run workflow" again.

### Testing the Patch Release

Follow the test outlined in [_Testing the Release_](#testing-the-release), including steps to test the specific bug 
that was patched.
