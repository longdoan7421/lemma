/*
 * generated by Xtext 2.12.0
 */
package de.fhdo.ddmm.tests

import com.google.inject.Inject
import de.fhdo.ddmm.service.ServiceModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(ServiceDslInjectorProvider)
class ServiceDslParsingTest {
    @Inject
    ParseHelper<ServiceModel> parseHelper

    @Test
    def void loadModel() {
        // TODO
    }
}
