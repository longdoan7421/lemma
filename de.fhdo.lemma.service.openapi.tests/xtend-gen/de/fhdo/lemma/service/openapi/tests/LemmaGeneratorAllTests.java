package de.fhdo.lemma.service.openapi.tests;

import de.fhdo.lemma.service.openapi.test.ValidationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ValidationTest.class, GeneratorsTest.class })
@SuppressWarnings("all")
public class LemmaGeneratorAllTests {
}
