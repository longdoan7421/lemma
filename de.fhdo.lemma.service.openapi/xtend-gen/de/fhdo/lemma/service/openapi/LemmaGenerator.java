package de.fhdo.lemma.service.openapi;

import de.fhdo.lemma.data.DataModel;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the central entry point for the generation of LEMMA models from
 * an OpenAPI specification file (v3.0.3). It supports the generation from JSON as well as YAML
 * OpenAPI files, e.g.,
 * <a href="https://petstore3.swagger.io/api/v3/openapi.json">Swagger's PET Store example</a>
 * @see <a href="https://www.openapis.org/">https://www.openapis.org/</a>
 * 
 * @author <a href="mailto:jonas.sorgalla@fh-dortmund.de">Jonas Sorgalla</a>
 */
@SuppressWarnings("all")
public class LemmaGenerator {
  private OpenAPI openAPI;
  
  private Logger logger;
  
  public static void main(final String[] args) {
    final String modelLocation = "https://petstore3.swagger.io/api/v3/openapi.json";
    final LemmaGenerator generator = new LemmaGenerator();
    final List<String> parRes = generator.parse(modelLocation);
    final Consumer<String> _function = (String it) -> {
      System.out.println(it);
    };
    parRes.forEach(_function);
    String _property = System.getProperty("user.dir");
    String _plus = (_property + "/model-gen/");
    generator.generateModels(_plus, "generated.data", 
      "generated.services", "openapi.technology", "de.example");
  }
  
  public LemmaGenerator() {
    this.logger = LoggerFactory.getLogger(LemmaGenerator.class);
  }
  
  /**
   * Checks whether there currently is a parsed in-memory to start the generation process
   */
  public boolean isParsed() {
    boolean _xifexpression = false;
    if ((this.openAPI == null)) {
      _xifexpression = false;
    } else {
      _xifexpression = true;
    }
    return _xifexpression;
  }
  
  /**
   * Takes a URL pointing to an openapi specification file (yaml or json) and parses it using
   * the swagger openapi parsing framework. Returns a list of all encountered messages during
   * the parsing.
   */
  public List<String> parse(final String openapi) {
    final ArrayList<String> returnMessages = CollectionLiterals.<String>newArrayList();
    returnMessages.add("Encountered messages while parsing the URL...");
    final ParseOptions parseOptions = new ParseOptions();
    parseOptions.setResolve(true);
    parseOptions.setFlatten(true);
    final SwaggerParseResult result = new OpenAPIParser().readLocation(openapi, null, parseOptions);
    List<String> _messages = result.getMessages();
    boolean _tripleNotEquals = (_messages != null);
    if (_tripleNotEquals) {
      int _length = ((Object[])Conversions.unwrapArray(result.getMessages(), Object.class)).length;
      boolean _equals = (_length == 0);
      if (_equals) {
        returnMessages.add("No errors or warnings encountered!");
      } else {
        returnMessages.addAll(result.getMessages());
      }
    }
    OpenAPI _openAPI = result.getOpenAPI();
    boolean _tripleNotEquals_1 = (_openAPI != null);
    if (_tripleNotEquals_1) {
      this.openAPI = result.getOpenAPI();
      returnMessages.add("In memory model of OpenAPI model loaded!");
    } else {
      returnMessages.add(
        "There was an error generating the in memory model for the given URL :(");
    }
    return returnMessages;
  }
  
  /**
   * Central methods which generates all models
   */
  public void generateModels(final String genPath, final String dataFilename, final String serviceFilename, final String techFilename, final String prefixService) {
    this.logger.info("Starting generation of LEMMA Data Model...");
    final LemmaDataSubGenerator dataGenerator = new LemmaDataSubGenerator(this.openAPI, genPath, dataFilename);
    DataModel _generate = dataGenerator.generate();
    final Pair<String, DataModel> dataModel = Pair.<String, DataModel>of(dataFilename, _generate);
  }
}
