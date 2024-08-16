#!/bin/bash

# Initialize array to store digest SHAs and all release versions
declare -a digests
declare -a all_release_versions

# Fetch all docker image names
image_names=$(gh api \
  -H "Accept: application/vnd.github+json" \
  -H "-GitHub-Api-Version: 2022-11-28" \
  -H "Authorization: Token ${GITHUB_TOKEN}" \
  --paginate "/orgs/boozallen/packages?package_type=container" | jq -r '.[] | select((.name | startswith("aissemble")) and (.name | endswith("-chart") | not)) | .name')

# For each docker image, find all release versions, excluding 1.7.0
for name in $image_names; do
  release_versions=$(gh api \
    -H "Accept: application/vnd.github+json" \
    -H "-GitHub-Api-Version: 2022-11-28" \
    -H "Authorization: Token ${GITHUB_TOKEN}" \
    --paginate "/orgs/boozallen/packages/container/${name}/versions" \
    | jq -r '.[] | .metadata.container.tags[] | select(test("^\\d+\\.\\d+\\.\\d+$"))' \
    | jq -R -s 'split("\n") | map(select(length > 0)) | map(select(. != "1.7.0"))')

  # Add release versions to all_release_versions array
  all_release_versions+=("$release_versions")

  # Loop through all release versions
  for version in $(echo "$release_versions" | jq -r '.[]'); do
    echo "Processing release image ${name}:${version}"

    # Fetch the base manifest SHA
    # Inspect command will output Name, MediaType, Digest in the first three lines
    # so we can use a regex to pull out the SHA
    manifest_base_sha=$(docker buildx imagetools inspect "ghcr.io/boozallen/${name}:${version}" | head -n 3 | sed -n 's/^Digest: *//p')

    echo "Manifest index SHA: ${manifest_base_sha}"

    # Add to digests array
    digests+=("$manifest_base_sha")

    # Query the raw inpect output to get the nested manifest list SHAs
    manifest_list_shas=$(docker buildx imagetools inspect --raw "ghcr.io/boozallen/${name}:${version}" | jq -r '.manifests[].digest' | paste -s -d ' ' -)

    echo "Manifest List SHAs: $manifest_list_shas"

    # Add to digests array
    digests+=("$manifest_list_shas")
  done
done

# Pass the latest_release_version to GITHUB_OUTPUT so snapshot_images.sh can use it
latest_release_version=$(echo "$all_release_versions" | jq -r .[] | sort -uV -r | head -n 1)
echo "latest-release-version=${latest_release_version}" >> "$GITHUB_OUTPUT"

# Join digests into a single string separated by spaces
digests_string=$(echo "${digests[*]}")

# Save the output to $GITHUB_OUTPUT
echo "multi-arch-digests=${digests_string}" >> "$GITHUB_OUTPUT"