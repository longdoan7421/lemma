/*
 * generated by Xtext 2.16.0
 */
package de.fhdo.ddmm.operationdsl.ui.tests;

import com.google.inject.Injector;
import de.fhdo.ddmm.operationdsl.ui.internal.OperationdslActivator;
import org.eclipse.xtext.testing.IInjectorProvider;

public class OperationDslUiInjectorProvider implements IInjectorProvider {

	@Override
	public Injector getInjector() {
		return OperationdslActivator.getInstance().getInjector("de.fhdo.ddmm.operationdsl.OperationDsl");
	}

}
