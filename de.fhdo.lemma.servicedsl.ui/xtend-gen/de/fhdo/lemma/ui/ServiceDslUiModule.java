/**
 * generated by Xtext 2.12.0
 */
package de.fhdo.lemma.ui;

import de.fhdo.lemma.ui.AbstractServiceDslUiModule;
import de.fhdo.lemma.ui.highlighting.HighlightingCalculator;
import de.fhdo.lemma.ui.highlighting.HighlightingConfiguration;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 * 
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class ServiceDslUiModule extends AbstractServiceDslUiModule {
  public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }
  
  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return HighlightingCalculator.class;
  }
  
  public ServiceDslUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
}