# aiSSEMBLE&trade; Library Helm Chart
Baseline Helm chart containing helper functions and libraries for use in other aiSSEMBLE-produced charts. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Provided Charts

| Definition            | Description                                                          |
|-----------------------|----------------------------------------------------------------------|
| filegather.configmap  | Creates a configmap containing all YAML files from a given directory |
| filegather.util.merge | Merges two yaml files to form a single compound YAML file            |