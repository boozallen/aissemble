#!/bin/bash

# Initialize array to store digest SHAs
declare -a digests

# Fetch all docker image names
image_names=$(gh api \
  -H "Accept: application/vnd.github+json" \
  -H "-GitHub-Api-Version: 2022-11-28" \
  -H "Authorization: Token ${GITHUB_TOKEN}" \
  --paginate "/orgs/boozallen/packages?package_type=container" | jq -r '.[] | select((.name | startswith("aissemble")) and (.name | endswith("-chart") | not)) | .name')

# For each docker image, find their snapshot versions by grabbing any tag that ends with "-SNAPSHOT"
for name in $image_names; do
  all_snapshot_versions=$(gh api \
    -H "Accept: application/vnd.github+json" \
    -H "-GitHub-Api-Version: 2022-11-28" \
    -H "Authorization: Token ${GITHUB_TOKEN}" \
    --paginate "/orgs/boozallen/packages/container/${name}/versions" \
    | jq -r '.[] | .metadata.container.tags[] | select(endswith("-SNAPSHOT"))'| jq -R -s 'split("\n")| map(select(length > 0))')

  # Find the latest snapshot version by sorting all snapshot versions, then selecting the top
  latest_snapshot_version=$(echo "$all_snapshot_versions" | jq -r '.[] | select(endswith("-SNAPSHOT"))' | sort -r | head -n 1)

  echo "Processing snapshot image ${name}:${latest_snapshot_version}"

  # Fetch the base manifest SHA
  # Inspect command will output Name, MediaType, Digest in the first three lines
  # Use regex to pull out the SHA
  latest_snapshot_manifest_base_sha=$(docker buildx imagetools inspect "ghcr.io/boozallen/${name}:${latest_snapshot_version}"  | head -n 3 | sed -n 's/^Digest: *//p')

  echo "Manifest index SHA: ${latest_snapshot_manifest_base_sha}"

  # Add to digests array
  digests+=("$latest_snapshot_manifest_base_sha")

  # Fetch the manifest list SHAs
  latest_snapshot_manifest_list_shas=$(docker buildx imagetools inspect --raw "ghcr.io/boozallen/${name}:${latest_snapshot_version}" | jq -r '.manifests[].digest' | paste -s -d ' ' -)

  echo "Manifest List: $latest_snapshot_manifest_list_shas"

  # Add to digests array
  digests+=("$latest_snapshot_manifest_list_shas")

  # Find if there are any patch versions available
  # Extract the major, minor, and patch components of the latest release version using IFS (Internal Field Separator)
  IFS='.' read -r major minor patch <<< "${LATEST_RELEASE_VERSION}"

  # Patch pattern uses the latest release version's major and minor components
  patch_pattern="${major}\.${minor}\.[1-9]+[0-9]*-SNAPSHOT?"

  for version in $(echo "$all_snapshot_versions" | jq -r '.[]'); do
    if [[ ${version} =~ ^${patch_pattern}$ ]]; then
      echo "Patch version ${version} matches patch pattern ${patch_pattern}"

      # Add to array containing all matching patch versions
      matching_patch_versions+=("$version")
    fi

    # If matching patch versions array is not empty
    if [ ${#matching_patch_versions[@]} -ne 0 ]; then
      echo "Patch Versions array is not empty"
      # Find the latest patch version
      latest_patch_version=($(printf "%s\n" "${matching_patch_versions[@]}" | sort -V -r | head -n 1))

      # Fetch the SHA
      latest_patch_version_sha=$(docker buildx imagetools inspect --raw "ghcr.io/boozallen/${name}:${latest_patch_version}" | jq -r '.manifests[].digest' | paste -s -d ' ' -)

      # Add to the digests array
      digests+=("$latest_patch_version_sha")
    fi
  done
done

# Join digests into a single string separated by spaces
digests_string=$(echo "${digests[*]}")

# Save the output to $GITHUB_OUTPUT
echo "latest-snapshot-digests=${digests_string}" >> "$GITHUB_OUTPUT"
