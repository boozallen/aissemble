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

import javax.enterprise.inject.spi.Extension;
import java.util.List;

/**
 * {@link CdiContext} interface provides a way to compose a cdi context with
 * several different classes and extension depending on what's needed.
 */
public interface CdiContext {

	/**
	 * The classes that could be added to CDI.
	 * 
	 * @return list of CDI classes
	 */
	List<Class<?>> getCdiClasses();

	/**
	 * Extensions that should be added to CDI.
	 * 
	 * @return list of CDI extensions
	 */
	List<Extension> getExtensions();

}
