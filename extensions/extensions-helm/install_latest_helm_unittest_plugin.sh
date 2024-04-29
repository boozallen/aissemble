echo "Updating helm unittest plugin to latest version..."
# uninstall current helm unittest plugin
helm plugin uninstall unittest

# remove helm unittest plugin cache
rm -rf "$(helm env HELM_CACHE_HOME)/plugins/https-github.com-helm-unittest-helm-unittest.git"

# reinstall the helm unittest plugin latest version
helm plugin install https://github.com/helm-unittest/helm-unittest.git
