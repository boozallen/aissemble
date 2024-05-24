## Overview
This directory contains several sample values-sets for use with the spark-history-server chart.  
Each is intended to be an illustrative example of how one could use this chart to suit a particular
scenario.  

## Usage
Each value set is fully functional.  From the root of this module (the parent to this directory), 
execute the following command, substituting the intended example file for `EXAMPLE_FILE`:

```shell
helm install spark-history-server . -f values.yaml -f examples/EXAMPLE_FILE.yaml
```

## Examples
### values-local.yaml
This file demonstrates how one may wish to configure this chart for a local testing environment.  It
disables ingress in favor of a LoadBalancer, and enables the event volume, creating a persistent 
volume backed by the local storage on the node.  Mounting this volume on your spark drivers for log 
creation is left as an exercise for the user.

Note that the default implementation of the event volume uses node-local storage.  This is suitable 
for single-node clusters, but is unsuitable for larger clusters as may be encountered in production 
environments.

### values-cloud.yaml
This file demonstrates how one may wish to configure this chart in a cloud-deployed environment.  It 
configures the backing `Service` to use a ClusterIP, and instead provides UI access through its configured
Ingress.  

Unlike `values-local.yaml`, this example does not provide an accessible storage layer.  We recommend
creating a custom `PersistentVolume` backed by your cloud storage solution of choice.  

### values-s3.yaml
This file demonstrates several key features of this spark history helm chart.  It enables accessing of
event logs through the AWS S3 protocol by performing the following actions:
1. Downloading the `hadoop-aws` jar to provide the necessary interfaces to communicate with `s3`
2. Disabling the unneeded event volume
3. Loading AWS credentials into the environment securely from a Kubernetes `Secret`
4. Providing a custom `spark-defaults.conf` definition, specifying the bucket to connect to