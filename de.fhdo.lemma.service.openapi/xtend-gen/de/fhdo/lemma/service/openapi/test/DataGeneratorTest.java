package de.fhdo.lemma.service.openapi.test;

import de.fhdo.lemma.service.openapi.LemmaDataSubGenerator;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.File;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class DataGeneratorTest {
  private Logger logger;
  
  private final String localSchema = new File("test-schemas/openapi.json").toURI().toString();
  
  private LemmaDataSubGenerator dataGenerator;
  
  private OpenAPI openAPI;
  
  @Before
  public void setup() throws Exception {
    this.logger = LoggerFactory.getLogger(ValidationTest.class);
    final ParseOptions parseOptions = new ParseOptions();
    parseOptions.setResolve(true);
    parseOptions.setFlatten(true);
    final SwaggerParseResult result = new OpenAPIParser().readLocation(this.localSchema, null, parseOptions);
    this.openAPI = result.getOpenAPI();
  }
  
  @Test
  public void dataTest() throws Exception {
    this.logger.info("Starting generation of LEMMA Data Model...");
    String _property = System.getProperty("user.dir");
    String _plus = (_property + 
      "/test-model-gen/");
    LemmaDataSubGenerator _lemmaDataSubGenerator = new LemmaDataSubGenerator(this.openAPI, _plus, "test.data");
    this.dataGenerator = _lemmaDataSubGenerator;
    this.dataGenerator.generate();
    String _property_1 = System.getProperty("user.dir");
    String _plus_1 = (_property_1 + "/model-gen/test.data");
    Assert.assertTrue(new File(_plus_1).exists());
  }
}
