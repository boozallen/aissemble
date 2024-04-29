package com.boozallen.aissemble.core.policy.configuration;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;
import org.aeonbits.owner.KrauseningConfig.KrauseningSources;

/**
 * {@link PolicyConfiguration} is used to configure the policy location and
 * defaults.
 * 
 * @author Booz Allen Hamilton
 *
 */
@KrauseningSources(value = "policy-configuration.properties")
public interface PolicyConfiguration extends KrauseningConfig {

	/**
	 * Configures the location and file name of the file that contains the policies.
	 * 
	 * @return the location of the file that contains the policies
	 */
	@Key("policies-location")
	String getPoliciesLocation();

}
