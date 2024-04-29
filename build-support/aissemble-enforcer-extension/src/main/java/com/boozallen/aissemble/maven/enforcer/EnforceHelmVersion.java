package com.boozallen.aissemble.maven.enforcer;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import javax.inject.Named;
import javax.inject.Inject;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rules.AbstractStandardEnforcerRule;
import org.apache.maven.enforcer.rules.utils.ArtifactMatcher;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import com.boozallen.aissemble.maven.enforcer.helper.HelmVersionHelper;


/**
 * Custom enforcer rule used to require a certain version of helm is installed.
 */
@Named("enforceHelmVersion")
public class EnforceHelmVersion extends AbstractStandardEnforcerRule {
    private ArtifactVersion currentHelmVersion;
    private String requiredHelmVersion;

    @Inject
    private MavenProject project;

    @Override
    public void execute() throws EnforcerRuleException {
        if (this.currentHelmVersion == null) {
            this.currentHelmVersion = new DefaultArtifactVersion(getInstalledHelmVersion());
        }
        getLog().info("Detected Helm Version: " + this.currentHelmVersion);
        enforceVersion(getRequiredHelmVersion(), this.currentHelmVersion);
    }

    @Override
    public String getCacheId() {
        return StringUtils.isNotEmpty(this.requiredHelmVersion)? "" + this.requiredHelmVersion.hashCode() : "0";
    }

    @Override
    public String toString() {
        return String.format("EnforceHelmVersion[requiredHelmVersion=%s]", requiredHelmVersion);
    }

    /**
     * get the installed helm version
     * @return helm version
     */
    public String getInstalledHelmVersion() {
        // check the helm version using the current project directory
        HelmVersionHelper helmVersionHelper = new HelmVersionHelper(project.getBasedir());
        return helmVersionHelper.getCurrentHelmVersion();
    }

    /**
     * set current helm version
     * @param currentHelmVersion
     */
    public void setCurrentHelmVersion(ArtifactVersion currentHelmVersion) {
        this.currentHelmVersion = currentHelmVersion;
    }

    /**
     * get the required helm version
     * @return requiredHelmVersion
     */
    public final String getRequiredHelmVersion() {
        return this.requiredHelmVersion;
    }

    /**
     * set required helm version
     * @param requiredHelmVersion
     */
    public void setRequiredHelmVersion(String requiredHelmVersion) {
        this.requiredHelmVersion = requiredHelmVersion;
    }

    //
    // Functions copied and modified from the AbstractVersionEnforcer for version enforce
    //

    /**
     * print VersionRange in [recommandedVersion,) format
     * @param versionRange
     * @return
     */
    protected static String toString(VersionRange versionRange) {
        return versionRange.getRecommendedVersion() != null ? "[" + versionRange.getRecommendedVersion().toString() + ",)" : versionRange.toString();
    }

    /**
     * Enforce rule with the given helm requiredVersion and the actual helm version
     * @param requiredVersionRange
     * @param actualVersion
     * @throws EnforcerRuleException
     */
    public void enforceVersion(String requiredVersionRange, ArtifactVersion actualVersion) throws EnforcerRuleException {
        if (StringUtils.isEmpty(requiredVersionRange)) {
            throw new EnforcerRuleException("Helm version can't be empty.");
        } else {
            String msg = "Detected Helm Version: " + actualVersion;
            if (actualVersion.toString().equals(requiredVersionRange)) {
                getLog().debug(msg + " is allowed in the range " + requiredVersionRange + ".");
            } else {
                try {
                    VersionRange vr = VersionRange.createFromVersionSpec(requiredVersionRange);
                    if (!ArtifactMatcher.containsVersion(vr, actualVersion)) {
                        String message = this.getMessage();
                        if (StringUtils.isEmpty(message)) {
                            message = msg + " is not in the allowed range " + toString(vr) + ".";
                        }

                        throw new EnforcerRuleException(message);
                    }

                    getLog().debug(msg + " is allowed in the range " + toString(vr) + ".");
                } catch (InvalidVersionSpecificationException e) {
                    throw new EnforcerRuleException("The requested Helm version " + requiredVersionRange + " is invalid.", e);
                }
            }
        }
    }
}
