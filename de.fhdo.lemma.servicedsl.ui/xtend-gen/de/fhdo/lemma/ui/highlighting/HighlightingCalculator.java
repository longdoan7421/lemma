package de.fhdo.lemma.ui.highlighting;

import com.google.common.base.Objects;
import de.fhdo.lemma.data.PrimitiveValue;
import de.fhdo.lemma.service.ImportedServiceAspect;
import de.fhdo.lemma.service.Interface;
import de.fhdo.lemma.service.Microservice;
import de.fhdo.lemma.service.Operation;
import de.fhdo.lemma.service.ProtocolSpecification;
import de.fhdo.lemma.service.ReferredOperation;
import de.fhdo.lemma.service.ServicePackage;
import de.fhdo.lemma.service.TechnologyReference;
import de.fhdo.lemma.technology.TechnologyPackage;
import de.fhdo.lemma.technology.TechnologySpecificPropertyValueAssignment;
import de.fhdo.lemma.ui.highlighting.HighlightingConfiguration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Provide custom syntax highlighting for certain elements.
 * 
 * @author <a href="mailto:florian.rademacher@fh-dortmund.de>Florian Rademacher</a>
 */
@SuppressWarnings("all")
public class HighlightingCalculator implements ISemanticHighlightingCalculator {
  /**
   * Provide highlighting
   */
  @Override
  public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor acceptor, final CancelIndicator cancelIndicator) {
    this.provideHighlightingForAnnotations(resource, acceptor);
    this.provideHighlightingForDefaultTypeDefinitionFlag(resource, acceptor);
    this.provideHighlightingForBooleanConstants(resource, acceptor);
  }
  
  /**
   * Provide highlighting for annotations
   */
  private void provideHighlightingForAnnotations(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
    Pair<EReference, Boolean> _mappedTo = Pair.<EReference, Boolean>of(ServicePackage.Literals.MICROSERVICE__ENDPOINTS, Boolean.valueOf(false));
    Pair<Class<Microservice>, List<Pair<EReference, Boolean>>> _mappedTo_1 = Pair.<Class<Microservice>, List<Pair<EReference, Boolean>>>of(Microservice.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo)));
    Pair<EReference, Boolean> _mappedTo_2 = Pair.<EReference, Boolean>of(ServicePackage.Literals.TECHNOLOGY_REFERENCE__TECHNOLOGY, Boolean.valueOf(false));
    Pair<Class<TechnologyReference>, List<Pair<EReference, Boolean>>> _mappedTo_3 = Pair.<Class<TechnologyReference>, List<Pair<EReference, Boolean>>>of(TechnologyReference.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_2)));
    Pair<EReference, Boolean> _mappedTo_4 = Pair.<EReference, Boolean>of(ServicePackage.Literals.INTERFACE__ENDPOINTS, Boolean.valueOf(false));
    Pair<Class<Interface>, List<Pair<EReference, Boolean>>> _mappedTo_5 = Pair.<Class<Interface>, List<Pair<EReference, Boolean>>>of(Interface.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_4)));
    Pair<EReference, Boolean> _mappedTo_6 = Pair.<EReference, Boolean>of(ServicePackage.Literals.OPERATION__ENDPOINTS, Boolean.valueOf(false));
    Pair<Class<Operation>, List<Pair<EReference, Boolean>>> _mappedTo_7 = Pair.<Class<Operation>, List<Pair<EReference, Boolean>>>of(Operation.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_6)));
    Pair<EReference, Boolean> _mappedTo_8 = Pair.<EReference, Boolean>of(ServicePackage.Literals.REFERRED_OPERATION__ENDPOINTS, Boolean.valueOf(false));
    Pair<Class<ReferredOperation>, List<Pair<EReference, Boolean>>> _mappedTo_9 = Pair.<Class<ReferredOperation>, List<Pair<EReference, Boolean>>>of(ReferredOperation.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_8)));
    Pair<EReference, Boolean> _mappedTo_10 = Pair.<EReference, Boolean>of(ServicePackage.Literals.PROTOCOL_SPECIFICATION__PROTOCOL, Boolean.valueOf(false));
    Pair<Class<ProtocolSpecification>, List<Pair<EReference, Boolean>>> _mappedTo_11 = Pair.<Class<ProtocolSpecification>, List<Pair<EReference, Boolean>>>of(ProtocolSpecification.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_10)));
    Pair<EReference, Boolean> _mappedTo_12 = Pair.<EReference, Boolean>of(ServicePackage.Literals.IMPORTED_SERVICE_ASPECT__IMPORTED_ASPECT, Boolean.valueOf(true));
    Pair<Class<ImportedServiceAspect>, List<Pair<EReference, Boolean>>> _mappedTo_13 = Pair.<Class<ImportedServiceAspect>, List<Pair<EReference, Boolean>>>of(ImportedServiceAspect.class, Collections.<Pair<EReference, Boolean>>unmodifiableList(CollectionLiterals.<Pair<EReference, Boolean>>newArrayList(_mappedTo_12)));
    final Map<Class<? extends EObject>, List<Pair<EReference, Boolean>>> annotatableConcepts = Collections.<Class<? extends EObject>, List<Pair<EReference, Boolean>>>unmodifiableMap(CollectionLiterals.<Class<? extends EObject>, List<Pair<EReference, Boolean>>>newHashMap(_mappedTo_1, _mappedTo_3, _mappedTo_5, _mappedTo_7, _mappedTo_9, _mappedTo_11, _mappedTo_13));
    final BiConsumer<Class<? extends EObject>, List<Pair<EReference, Boolean>>> _function = (Class<? extends EObject> concept, List<Pair<EReference, Boolean>> featureDescriptions) -> {
      final Function1<EObject, Boolean> _function_1 = (EObject it) -> {
        return Boolean.valueOf(concept.isInstance(it));
      };
      final Procedure1<EObject> _function_2 = (EObject it) -> {
        final Consumer<Pair<EReference, Boolean>> _function_3 = (Pair<EReference, Boolean> featureDescription) -> {
          final EReference feature = featureDescription.getKey();
          final Boolean highlightImmediately = featureDescription.getValue();
          final Consumer<INode> _function_4 = (INode it_1) -> {
            INode nodeToHighlight = it_1;
            if ((!(highlightImmediately).booleanValue())) {
              while ((((nodeToHighlight != null) && 
                (nodeToHighlight.getNextSibling() != null)) && 
                (!Objects.equal(nodeToHighlight.getNextSibling().getText(), "(")))) {
                nodeToHighlight = nodeToHighlight.getPreviousSibling();
              }
            }
            if ((nodeToHighlight != null)) {
              do {
                {
                  acceptor.addPosition(nodeToHighlight.getOffset(), nodeToHighlight.getLength(), 
                    HighlightingConfiguration.ANNOTATION_ID);
                  nodeToHighlight = nodeToHighlight.getPreviousSibling();
                }
              } while((((nodeToHighlight != null) && 
                (nodeToHighlight.getNextSibling() != null)) && 
                (!Objects.equal(nodeToHighlight.getNextSibling().getText(), "@"))));
            }
          };
          NodeModelUtils.findNodesForFeature(it, feature).forEach(_function_4);
        };
        featureDescriptions.forEach(_function_3);
      };
      IteratorExtensions.<EObject>forEach(IteratorExtensions.<EObject>filter(resource.getAllContents(), _function_1), _function_2);
    };
    annotatableConcepts.forEach(_function);
  }
  
  /**
   * Provide highlighting for default type definition flag of built-in @technology annotation
   */
  private void provideHighlightingForDefaultTypeDefinitionFlag(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
    List<EObject> _list = IteratorExtensions.<EObject>toList(resource.getAllContents());
    for (final EObject eObject : _list) {
      {
        final List<INode> relevantFeatures = NodeModelUtils.findNodesForFeature(eObject, 
          ServicePackage.Literals.TECHNOLOGY_REFERENCE__IS_TYPE_DEFINITION_TECHNOLOGY);
        boolean _isEmpty = relevantFeatures.isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          INode currentNode = relevantFeatures.get(0).getPreviousSibling();
          boolean typedefKeywordColored = false;
          while (((currentNode != null) && (!typedefKeywordColored))) {
            String _keywordValue = this.keywordValue(currentNode);
            boolean _equals = Objects.equal("typedef", _keywordValue);
            if (_equals) {
              acceptor.addPosition(currentNode.getOffset(), currentNode.getLength(), 
                HighlightingConfiguration.DEFAULT_ID);
              typedefKeywordColored = true;
            } else {
              currentNode = currentNode.getPreviousSibling();
            }
          }
        }
      }
    }
  }
  
  /**
   * Helper to return the value of a Keyword INode. Returns an empty string if the passed node is
   * not a Keyword.
   */
  private String keywordValue(final INode node) {
    final EObject grammarElement = node.getGrammarElement();
    String _xifexpression = null;
    if ((grammarElement instanceof Keyword)) {
      _xifexpression = ((Keyword)grammarElement).getValue();
    } else {
      _xifexpression = "";
    }
    return _xifexpression;
  }
  
  /**
   * Provide highlighting for boolean values
   */
  private void provideHighlightingForBooleanConstants(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
    final Function1<EObject, PrimitiveValue> _function = (EObject it) -> {
      return ((ImportedServiceAspect) it).getSinglePropertyValue();
    };
    Pair<Function1<EObject, PrimitiveValue>, EReference> _mappedTo = Pair.<Function1<EObject, PrimitiveValue>, EReference>of(_function, ServicePackage.Literals.IMPORTED_SERVICE_ASPECT__SINGLE_PROPERTY_VALUE);
    Pair<Class<ImportedServiceAspect>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>> _mappedTo_1 = Pair.<Class<ImportedServiceAspect>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>>of(ImportedServiceAspect.class, Collections.<Pair<Function1<EObject, PrimitiveValue>, EReference>>unmodifiableList(CollectionLiterals.<Pair<Function1<EObject, PrimitiveValue>, EReference>>newArrayList(_mappedTo)));
    final Function1<EObject, PrimitiveValue> _function_1 = (EObject it) -> {
      return ((TechnologySpecificPropertyValueAssignment) it).getValue();
    };
    Pair<Function1<EObject, PrimitiveValue>, EReference> _mappedTo_2 = Pair.<Function1<EObject, PrimitiveValue>, EReference>of(_function_1, TechnologyPackage.Literals.TECHNOLOGY_SPECIFIC_PROPERTY_VALUE_ASSIGNMENT__VALUE);
    Pair<Class<TechnologySpecificPropertyValueAssignment>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>> _mappedTo_3 = Pair.<Class<TechnologySpecificPropertyValueAssignment>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>>of(TechnologySpecificPropertyValueAssignment.class, Collections.<Pair<Function1<EObject, PrimitiveValue>, EReference>>unmodifiableList(CollectionLiterals.<Pair<Function1<EObject, PrimitiveValue>, EReference>>newArrayList(_mappedTo_2)));
    final HashMap<Class<? extends EObject>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>> booleanConcepts = CollectionLiterals.<Class<? extends EObject>, List<Pair<Function1<EObject, PrimitiveValue>, EReference>>>newHashMap(_mappedTo_1, _mappedTo_3);
    final Procedure1<EObject> _function_2 = (EObject eObject) -> {
      final Function1<Class<? extends EObject>, Boolean> _function_3 = (Class<? extends EObject> it) -> {
        return Boolean.valueOf(it.isInstance(eObject));
      };
      final Class<? extends EObject> matchingBooleanConcept = IterableExtensions.<Class<? extends EObject>>findFirst(booleanConcepts.keySet(), _function_3);
      if ((matchingBooleanConcept != null)) {
        final List<Pair<Function1<EObject, PrimitiveValue>, EReference>> primitiveValueGetters = booleanConcepts.get(matchingBooleanConcept);
        final Consumer<Pair<Function1<EObject, PrimitiveValue>, EReference>> _function_4 = (Pair<Function1<EObject, PrimitiveValue>, EReference> it) -> {
          final Function1<EObject, PrimitiveValue> getter = it.getKey();
          final EReference feature = it.getValue();
          final PrimitiveValue primitiveValue = getter.apply(eObject);
          if (((primitiveValue != null) && (primitiveValue.getBooleanValue() != null))) {
            final Consumer<INode> _function_5 = (INode it_1) -> {
              acceptor.addPosition(it_1.getOffset(), it_1.getLength(), 
                HighlightingConfiguration.KEYWORD_ID);
            };
            NodeModelUtils.findNodesForFeature(eObject, feature).forEach(_function_5);
          }
        };
        primitiveValueGetters.forEach(_function_4);
      }
    };
    IteratorExtensions.<EObject>forEach(resource.getAllContents(), _function_2);
  }
}