/*
 * generated by Xtext 2.22.0
 */
package de.fhdo.lemma.operationdsl.parser.antlr;

import com.google.inject.Inject;
import de.fhdo.lemma.operationdsl.parser.antlr.internal.InternalOperationDslParser;
import de.fhdo.lemma.operationdsl.services.OperationDslGrammarAccess;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;

public class OperationDslParser extends AbstractAntlrParser {

	@Inject
	private OperationDslGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	

	@Override
	protected InternalOperationDslParser createParser(XtextTokenStream stream) {
		return new InternalOperationDslParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "OperationModel";
	}

	public OperationDslGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(OperationDslGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
