package de.fhdo.lemma.ui

import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.core.runtime.IProgressMonitor
import de.fhdo.lemma.service.ServiceModel
import de.fhdo.lemma.utils.LemmaUtils

/**
 * Reconcile strategy that is capable of reloading imported models of a currently constructed model.
 * As a result, the constructed model can immediately refer to elements that have just been added to
 * the imported models without the need to reload those models by closing/opening their associated
 * Eclipse editors.
 *
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
class ServiceDslReconcileStrategy extends XtextDocumentReconcileStrategy {
    /**
     * Post parse callback for Xtext resources which triggers model scoping and thus the resolution
     * of cross-referenced, possibly imported, model elements
     */
    override postParse(XtextResource resource, IProgressMonitor monitor) {
        resource.reloadImportedModels
        super.postParse(resource, monitor)
    }

    /**
     * Helper to reload models being imported by a given Xtext resource
     */
    private def reloadImportedModels(XtextResource resource) {
        if (resource.contents.empty || !(resource.contents.get(0) instanceof ServiceModel))
            return

        val serviceModel = resource.contents.get(0) as ServiceModel
        serviceModel.imports.forEach[
            try {
                val importedResource = LemmaUtils.getResourceFromUri(serviceModel.eResource,
                    it.importURI)
                // Reloading an EMF resource boils down to first unloading it and then immediately
                // loading it again
                importedResource.unload()
                importedResource.load(emptyMap)
            } catch (Exception ex) {
                // NOOP
            }
        ]
    }
}