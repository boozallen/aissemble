package com.boozallen.aiops.metadata;

/*-
 * #%L
 * aiSSEMBLE Quickstarts::Data Ingest::Spark::File
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContainer;
import com.boozallen.aissemble.core.cdi.CdiContext;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory that creates the proper CDI Context for this series of pipelines.
 * 
 * Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/cdi.container.factory.java.vm
 */
public final class CdiContainerFactory {

	private CdiContainerFactory() {
		// private construct to prevent instantiation of all static class
	}

	/**
	 * Creates a new WeldContainer with the set of {@link CdiContext}
	 * implementations needed for these pipelines.
	 * 
	 * @return Weld Container instance
	 */
	public static WeldContainer getCdiContainer() {
		return getCdiContainer(null);
	}

	/**
	 * Creates a new WeldContainer with the set of {@link CdiContext}
	 * implementations needed for these pipelines with the ability to add in
	 * additional contexts in an ad-hoc fashion.
	 * 
	 * @param additionalContexts
	 * @return Weld Container instance
	 */
	public static WeldContainer getCdiContainer(List<CdiContext> additionalContexts) {
		List<CdiContext> contexts = getContexts();
		if (CollectionUtils.isNotEmpty(additionalContexts)) {
			contexts.addAll(additionalContexts);
		}
		return CdiContainer.create(contexts);
	}

	protected static List<CdiContext> getContexts() {
		List<CdiContext> contexts = new ArrayList<>();
		return contexts;
	}

}
