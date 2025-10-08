package com.boozallen.aiops.data.delivery;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContainer;
import com.boozallen.aissemble.core.cdi.CdiContext;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.ArrayList;
import java.util.List;

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
