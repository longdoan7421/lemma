/**
 * generated by Xtext 2.12.0
 */
package de.fhdo.lemma.technology.ui;

import de.fhdo.lemma.eclipse.ui.editor.LiveValidationCapableXtextEditor;
import de.fhdo.lemma.eclipse.ui.editor.LiveValidationXtextDocumentProvider;
import de.fhdo.lemma.eclipse.ui.editor.server.ServerConnection;
import de.fhdo.lemma.technology.ui.highlighting.HighlightingCalculator;
import de.fhdo.lemma.technology.ui.highlighting.HighlightingConfiguration;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;
import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 * 
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class TechnologyDslUiModule extends AbstractTechnologyDslUiModule {
  public Class<? extends XtextEditor> bindXtextEditor() {
    return LiveValidationCapableXtextEditor.class;
  }
  
  public Class<? extends XtextDocumentProvider> bindXtextDocumentProvider() {
    return LiveValidationXtextDocumentProvider.class;
  }
  
  @SingletonBinding(eager = true)
  public ServerConnection bindServerConnection() {
    return ServerConnection.instance();
  }
  
  public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }
  
  public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
    return HighlightingCalculator.class;
  }
  
  public Class<? extends XtextDocumentReconcileStrategy> bindXtextDocumentReconcileStrategy() {
    return TechnologyDslReconcileStrategy.class;
  }
  
  public TechnologyDslUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
}
