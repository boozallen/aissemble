# aiSSEMBLE&trade; Documentation
## Contributing to Docs
Please refer to [CONTRIBUTING.md](CONTRIBUTING.md). 

## Previewing Documentation Updates
Prior to merging with the dev branch, all documentation updates should be viewed locally via a web server to verify all 
features are functioning properly. In order to accomplish this, utilize the *http-server* package for Node.js. 

### Pre-requisites: 
1. Install Node.js
2. Install *http-server* and *antora* packages
    ```bash
    npm i http-server antora
   ```

### Steps:
1. In a terminal, navigate to `docs/`.
2. Run antora build:
   ```bash
    npx antora antora-playbook-local.yml --to-dir=site
   ```
3. Start local web server: 
   ```bash
   npx http-server site -c-1 
   ```
4. Access webpage from a browser http://localhost:8080/
5. Change version to current snapshot to view changes.

For further information on running *http-server*, check out Antora [docs](https://docs.antora.org/antora/latest/preview-site/).

### Steps to build preview UI (test the Antora UI source changes):
1. In a terminal, navigate to `docs/antora-aissemble-ui` and run:
      ```bash
      npm install
      ```
2. Run "npx gulp preview:build" to build the UI once for preview.
3. Run the following gulp preview command to have it run continuously. This task also launches a local HTTP server so updates get synchronized with the browser (i.e., “live reload”).:
      ```bash
      npx gulp preview
      ```
4. Access webpage from a browser http://localhost:5252/

For further information on build and preview UI, check out Antora [docs](https://docs.antora.org/antora-ui-default/build-preview-ui/).

### Package for previewing:
1. To bundle the UI in order to preview the UI on the real site in local development, run the following command:
    ```bash
    npx gulp bundle
   ```
2. The UI bundle will be available at `build/ui-bundle.zip`.
   _Note: this zip will need to be committed after the changes have been made_

For further information on packaging for previewing, check out Antora [docs](https://docs.antora.org/antora-ui-default/build-preview-ui/).

## Releasing a Fixed Version
TODO: update with content

## GitHub Actions - Documentation Update Workflow

GitHub actions is a CI/CD platform that automates the build, test, and deployment pipeline. It allows
you to run workflows with event triggers.  A workflow is a configurable, automated process that
will run one or more jobs. Within the aiSSEMBLE repository, a workflow has been established to 
deploy all documentation upon merging with the dev branch and is defined in the `.github/workflows/publish.yml` file.

For more information about GitHub actions, see [GitHub Actions](https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions).

