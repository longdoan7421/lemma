/**
 * generated by Xtext 2.12.0
 */
package de.fhdo.ddmm.technology.mappingdsl.ui;

import de.fhdo.ddmm.technology.mappingdsl.ui.AbstractMappingDslUiModule;
import de.fhdo.ddmm.technology.mappingdsl.ui.highlighting.HighlightingCalculator;
import de.fhdo.ddmm.technology.mappingdsl.ui.highlighting.HighlightingConfiguration;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class MappingDslUiModule extends AbstractMappingDslUiModule {
  public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }
  
  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return HighlightingCalculator.class;
  }
  
  public MappingDslUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
}