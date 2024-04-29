# Overview
This module serves as a mechanism for building the `spark-operator` docker image for
aiSSEMBLE with tight coupling with the aiSSEMBLE supported Spark version(s).  The 
`spark-operator` source code, written primarily in Go, is largely created and supported
through the official repository at https://github.com/kubeflow/spark-operator/tree/master.
This repository is cloned with each build to guarantee that aiSSEMBLE is always using the
latest stable materials.  

In the event that divergence is needed from the officially provided and/or maintained
source, the `spark-operator` repository should be forked, and the checkout URLs
within this module updated to the new repo.  The purpose of maintaining the separation 
is to better support tracking of any divergences from the base materials, as well as to
ease the process of staying up-to-date with upstream changes.