package de.fhdo.lemma.service.openapi.test

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.fhdo.lemma.service.openapi.test.ValidationTest


@RunWith(Suite)
@SuiteClasses(ValidationTest, GeneratorsTest)
class LemmaGeneratorAllTests {

}
