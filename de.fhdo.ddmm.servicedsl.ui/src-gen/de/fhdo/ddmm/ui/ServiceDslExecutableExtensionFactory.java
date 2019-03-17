/*
 * generated by Xtext 2.16.0
 */
package de.fhdo.ddmm.ui;

import com.google.inject.Injector;
import de.fhdo.ddmm.servicedsl.ui.internal.ServicedslActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class ServiceDslExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return Platform.getBundle(ServicedslActivator.PLUGIN_ID);
	}
	
	@Override
	protected Injector getInjector() {
		ServicedslActivator activator = ServicedslActivator.getInstance();
		return activator != null ? activator.getInjector(ServicedslActivator.DE_FHDO_DDMM_SERVICEDSL) : null;
	}

}
