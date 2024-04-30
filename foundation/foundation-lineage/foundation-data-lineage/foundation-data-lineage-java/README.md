# foundation-data-lineage-java
This module serves as a generic wrapper for data lineage 
types, objects, and functions.  While it is designed to be 
leveraged through the aiSSEMBLE&trade; ecosystem, it is not dependent
on aiSSEMBLE for execution.  It is intentionally designed for 
portability.

This Readme is intended to provide technical insight into the
implementation of this package.  For consumption guidance,
please refer to the [aiSSEMBLE Github Pages](https://boozallen.github.io/aissemble/current/data-lineage.html)

### Core Functionality

This module presently provides three main capabilities:

* Generic types to represent Data Lineage metadata
* Convenience methods for emitting Data Lineage through various mediums
* Conversion functions to transform the generic types to and from popular Data Lineage formats.

Through these capabilities, this module fulfills the need for an easy-to-use, implementation-agnostic 
Data Lineage interface.

### Developer Guidance

* This module should display little to no dependence on any other aiSSEMBLE module.  It is intentionally generic.
* Any changes to method or function signatures must be reflected in any relevant template code within `foundation-mda`.
* Any change or addition in functionality must be accompanied by associated automated tests.
  * As we are serving as an interface to third party libraries and services, any input parameters must be exhaustively validated.
