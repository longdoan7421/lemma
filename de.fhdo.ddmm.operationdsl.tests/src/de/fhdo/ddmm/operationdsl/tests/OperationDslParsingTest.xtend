/*
 * generated by Xtext 2.12.0
 */
package de.fhdo.ddmm.operationdsl.tests

import com.google.inject.Inject
import de.fhdo.ddmm.operation.OperationModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(OperationDslInjectorProvider)
class OperationDslParsingTest {
    @Inject
    ParseHelper<OperationModel> parseHelper

    @Test
    def void loadModel() {
        // TODO
    }
}
