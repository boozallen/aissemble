package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.generator.AbstractModelAgnosticGenerator;

/**
 * Generates Java code with no model interaction. This is often useful for
 * static files that must exist in some form or similar constructs.
 */
public class ModelAgnosticJavaGenerator extends AbstractModelAgnosticGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                             | Template                                                  | Generated File                                 |
     * |------------------------------------|-----------------------------------------------------------|------------------------------------------------|
     * | cucumberPipelineSteps              | cucumber.pipeline.steps.java.vm                           | ${basePackage}/PipelineSteps.java              |
     * | cucumberSurefireHarness            | cucumber.surefire.harness.java.vm                         | ${basePackage}/CucumberTest.java               |
     * | cucumberCdiContext                 | cucumber.test.cdi.context.java.vm                         | ${basePackage}/TestCdiContext.java             |
     * | abstractDataActionImpl             | data-delivery-spark/abstract.data.action.impl.java.vm     | ${basePackage}/AbstractDataActionImpl.java     |
     * | modelAgnosticAbstractPipelineStep  | data-delivery-spark/abstract.pipeline.step.java.vm        | ${basePackage}/AbstractPipelineStep.java       |
     * | dataLineageCustomConsumerImpl      | data-lineage-consumer/data-lineage-consumer-impl.java.vm  | ${basePackage}/DataLineageMessageHandler.java  |
     * | itPipelineSteps                    | integration-test/it.pipeline.steps.vm                     | ${basePackage}/tests/PipelineSteps.java        |
     * | itTestRunner                       | integration-test/it.test.runner.vm                        | ${basePackage}/tests/TestRunner.java           |
     */


	@Override
	protected String getOutputSubFolder() {
		return "java/";
	}

}
