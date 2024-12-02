# Contributing to the aiSSEMBLE&trade; Docs

## Rationale 
Because our aiSSEMBLE docs cover a variety of modules, it is important that consistent standards are developed and 
strictly adhered to. Navigable and relevant documentation a critical enabler for daily use. To that end, this 
document aims to capture those standards and lay them out clearly so that all future contributors are adhering to the 
established patterns.

## Guiding Principles
The most important guiding principles are the following:
 * The documentation is NOT a demonstrable guide, rather this is a specification guide.
 * The intent for this documentation is to be consumed by aiSSEMBLE users. The usage by maintainers is incidental.

Some additional guiding principles you should follow whilst working on the aiSSEMBLE docs include:
 * Document code as close to the native format as possible rather (_e.g. Javadocs_).
 * Delegate to external documentation wherever possible. 
 * Follow the “_what_, _why_ and _how_” approach:
   * What is it that you’re touching on - rely on official documentation (external links).
   * Why does aiSSEMBLE use it.
   * How does aiSSEMBLE use it.
 * Every page should have an overview to help the reader quickly determine applicability of the material.

## Standards
These are the standards derived from the principles above that provide specific guidance.

### Markup Standards:
 * Be cautious on identifying when to use an ordered vs. unordered list.
   * Use ordered when going through a sequence (e.g. _1_, _2_, _3_ or _a_, _b_, _c_).
   * Use unordered when listing out items in a list.
 * Ensure to wrap the lines of code within your .adoc files; ~100-120 characters.
 * When documenting file paths, use angled brackets (e.g. `<project>/<project>-pipeline-models/src/main/resources/records/`).
 * Use `xref:<page-name>` to link within the docs (e.g. `xref:pipeline.adoc`).
   * Excluding version/module name ensures the links always work correctly.
 * Ensure external links open in a new window for consistency (e.g. `<url>[<text>,role=external,window=_blank]`).
 * Wiki links are prone to break on rename if you just copy them from the address bar. There are two ways to prevent this:
   * Use the Share button to generate a short URL that follows page renames (preferred).
   * Click the edit icon, grab the url and replace `editaction` with `viewaction`.
   * When referencing the version of aiSSEMBLE, use the macro `{page-version}`. This excludes `-SNAPSHOT` for prereleases. 
     See [archetype.adoc](modules/ROOT/pages/archetype.adoc) for an example of including the `-SNAPSHOT` suffix.
### Process Standards:
 * Follow standard ticket workflow .
 * Contribute to the [Graphic Repository](<NB: OSS: need new location for graphic repository>) when adding graphics to the docs.
   * **Step 1:** Copy and paste the original PowerPoint slide and make any necessary changes to that slide within the "_**Graphic-Repository.pptx**_".
   * **Step 2:** The slide following the slide with the graphic, place the link of the original slide.
 * For existing pages, hold a review of the existing content and address feedback based on that.

### Content Standards: 
 * When documenting files and code, use the following examples:
   * For Records: 
     * Name: _TaxPayer_
     * Field Name: _ssn_
   * For Dictionary:
     * Name: _IrsDictionary_
     * Type Name: _SocialSecurityNumber_
   * For Pipelines: _TaxPayerPipeline_
 * Structure the headings with present participle verbs (e.g. Creating a Project, Deploying a Project, etc.).
 * Only include files that are a direct result of relevant component in the What Gets Generated section (e.g. 
   `SchemaBase` is NOT a direct result of Data Validation; DataAccessResource is a direct result of the Data Access 
   component).
 * Follow the standards for api documentation:
   * Java – Javadocs
   * Python – Javadocs
   * REST – Swagger
   * GraphQL - [Hackernoon](https://hackernoon.com/documenting-graphql-apis)
 * Document current functionality only, avoid annotating any upcoming features even if expected in the immediate future!

## Antora
aiSSEMBLE Docs utilizes Antora for its static-site generator. Antora helps create documentation websites from AsciiDoc 
source files stored in one or more source control repository. Refer to the [Antora documentation](https://docs.antora.org/antora/latest/) 
for quick tips and how-to.

