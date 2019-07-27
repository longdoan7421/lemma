/*
 * generated by Xtext 2.12.0
 */
package de.fhdo.lemma.technology.ui

import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import de.fhdo.lemma.ui.highlighting.HighlightingCalculator
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator

/**
 * Use this class to register components to be used within the Eclipse IDE.
 *
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
@FinalFieldsConstructor
class TechnologyDslUiModule extends AbstractTechnologyDslUiModule {
    def Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
        HighlightingCalculator
    }
}