package com.boozallen.aissemble.data.access.security;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.security.Principal;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;

import com.boozallen.aiops.security.authorization.policy.PolicyDecision;
import com.boozallen.aiops.security.authorization.policy.PolicyDecisionPoint;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

/**
 * Custom security identity augmentor to apply Data Access roles for
 * authorization purposes. See the following documentation for more details:
 * https://quarkus.io/guides/security-customization#security-identity-customization
 */
@ApplicationScoped
public class DataAccessRolesAugmentor implements SecurityIdentityAugmentor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        return Uni.createFrom().item(build(identity));
    }

    private Supplier<SecurityIdentity> build(SecurityIdentity identity) {
        if (identity.isAnonymous()) {
            return () -> identity;
        } else {
            // create a new builder and copy principal, attributes, credentials
            // and roles from the original identity
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);

            // Check if the identity is authorized and add the appropriate roles.
            // This maps the attribute-based allow/deny to a role-based allow/deny.
            // See application.properties for role configurations.
            Principal principal = identity.getPrincipal();
            PolicyDecision denyDecision = PolicyDecisionPoint.getInstance()
                    .isAuthorized(principal.getName(), "", "data-access");
            if (PolicyDecision.PERMIT.equals(denyDecision)) {
                builder.addRole("allow-role");
            } else if (PolicyDecision.DENY.equals(denyDecision)) {
                builder.addRole("deny-role");
            }

            return builder::build;
        }
    }

}
