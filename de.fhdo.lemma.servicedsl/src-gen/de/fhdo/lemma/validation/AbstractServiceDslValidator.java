/*
 * generated by Xtext 2.21.0
 */
package de.fhdo.lemma.validation;

import de.fhdo.lemma.technology.validation.TechnologyDslValidator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;

public abstract class AbstractServiceDslValidator extends TechnologyDslValidator {
	
	@Override
	protected List<EPackage> getEPackages() {
		List<EPackage> result = new ArrayList<EPackage>(super.getEPackages());
		result.add(EPackage.Registry.INSTANCE.getEPackage("de.fhdo.lemma.service"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("de.fhdo.lemma.technology"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("de.fhdo.lemma.data"));
		return result;
	}
}
