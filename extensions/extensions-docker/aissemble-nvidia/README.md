This module builds an aiSSEMBLE&trade; extension of the [Nvidia CUDA docker image](https://hub.docker.com/r/nvidia/cuda). The
base image has a strict EOL policy for tags, so this module will need to be updated as new versions are released. Read
the [official policy](https://gitlab.com/nvidia/container-images/cuda/-/blob/master/doc/support-policy.md) for more
details.

Importantly, the planned EOL and deletion date for all versions of the Nvidia are documented in the [container tag
matrix](https://gitlab.com/nvidia/container-images/cuda/-/blob/master/doc/container_tags.pdf).  If the build is ever 
failing because the base image is not found, this is the first place to check.