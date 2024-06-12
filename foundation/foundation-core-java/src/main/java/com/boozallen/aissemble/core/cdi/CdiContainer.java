package com.boozallen.aissemble.core.cdi;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.commons.collections4.CollectionUtils;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.Extension;
import java.util.Arrays;
import java.util.List;

/**
 * {@link CdiContainer} class is used to create a {@link WeldContainer} based on
 * the {@link CdiContext} that has been passed in, using the configured classes
 * and extensions.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class CdiContainer {

    private static final Logger logger = LoggerFactory.getLogger(CdiContainer.class);

    public static WeldContainer create(CdiContext cdiContext) {
        return create(Arrays.asList(cdiContext));
    }

    /**
     * Create a {@link CdiContainer}.
     *
     */
    public CdiContainer() {
        super();
    }

    /**
     * Creates an instance of the {@link CdiContainer} with the classes and
     * extensions supplied by the {@link CdiContext}s.
     *
     * @param cdiContext
     */
    public CdiContainer(CdiContext cdiContext) {
        this();
        create(cdiContext);
    }

    /**
     * Creates a {@link WeldContainer} based on the passed in
     * {@link CdiContext}.
     * 
     * @param cdiContexts the list of CDI contexts to add
     * @return weldContainer the configured Weld Container
     */
    public static WeldContainer create(List<CdiContext> cdiContexts) {

        // Create a CDI container -- in this case, Weld
        Weld weld = new Weld();

        // disable discovery so that all the jars aren't being scanned
        weld.disableDiscovery();

        for (CdiContext cdiContext : cdiContexts) {

            // Add any extensions that are needed
            addExtensions(cdiContext, weld);

            // Add any classes that are needed
            addClasses(cdiContext, weld);
        }

        // start container
        return weld.initialize();
    }

    /**
     * Adds all the extensions for this context.
     * 
     * @param cdiContext
     * @param weld
     */
    private static void addExtensions(CdiContext cdiContext, Weld weld) {

        // Add any extensions that are needed
        List<Extension> extensions = cdiContext.getExtensions();
        if (CollectionUtils.isNotEmpty(extensions)) {
            for (Extension extension : extensions) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding extension {} as a CDI extension", extension.getClass());
                }
                weld.addExtension(extension);
            }
        }

    }

    /**
     * Adds all the classes for this context.
     * 
     * @param cdiContext
     * @param weld
     */
    private static void addClasses(CdiContext cdiContext, Weld weld) {

        // Add any classes that are needed
        List<Class<?>> classes = cdiContext.getCdiClasses();
        if (CollectionUtils.isNotEmpty(classes)) {

            for (Class<?> clazz : classes) {

                // debug if we need it
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding class {} as a CDI Bean", clazz.getName());
                }
                weld.addBeanClass(clazz);
            }
        }
    }
}
