package com.boozallen.aiops.mda.metamodel.element.java;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pipeline decorator to ease generation of Java files.
 */
public class JavaPipeline extends BasePipelineDecorator {

	/**
	 * {@inheritDoc}
	 */
	public JavaPipeline(Pipeline pipelineToDecorate) {
		super(pipelineToDecorate);

	}

	/**
	 * Returns all steps as JavaSteps that have either an inbound type of messaging or an outbound type of messaging.
	 *
	 * @return all Java messaging steps
	 */
	public List<JavaStep> getMessagingSteps() {
		return wrapped.getSteps().stream()
				.map(JavaStep::new)
				.filter(step -> step.hasMessagingInbound() || step.hasMessagingOutbound())
				.collect(Collectors.toList());
	}

	/**
	 * Returns all the distinct impl imports from Java steps contained in this pipeline.
	 *
	 * @return all distinct Java steps impl imports in the pipeline
	 */
	public Set<String> getAllStepsImplImports() {
		Set<String> allStepsImplImports = new TreeSet<>();
		wrapped.getSteps().stream()
				.map(JavaStep::new)
				.forEach(step -> {
					allStepsImplImports.addAll(step.getImplImports());
				});
		return allStepsImplImports;

	}
}
