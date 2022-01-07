package de.fhdo.lemma.service.openapi;

import de.fhdo.lemma.data.DataModel;
import de.fhdo.lemma.technology.Technology;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
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
  /**
   * OpenAPI schema which will be used as source for generation
   */
  private OpenAPI openAPI;
  
  /**
   * SLF4j Logger
   */
  private Logger logger = LoggerFactory.getLogger(LemmaGenerator.class);
  
  /**
   * Log of all encountered exceptions during all transformations
   */
  @Accessors(AccessorType.PUBLIC_GETTER)
  private List<String> transMsgs = CollectionLiterals.<String>newArrayList();
  
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
   * Takes a URL pointing to an OpenAPI specification file (yaml or json) and parses it using
   * the swagger OpenAPI parsing framework. Returns a list of all encountered messages during
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
  public boolean generateModels(final String genPath, final String dataFilename, final String serviceFilename, final String techFilename, final String prefixService) {
    boolean _xblockexpression = false;
    {
      this.logger.info("Starting generation of LEMMA Data Model...");
      final LemmaDataSubGenerator dataGenerator = new LemmaDataSubGenerator(this.openAPI, genPath, dataFilename);
      DataModel _generate = dataGenerator.generate();
      final Pair<String, DataModel> dataModel = Pair.<String, DataModel>of(dataFilename, _generate);
      this.logger.debug("Adding encountered messages to log.");
      this.transMsgs.addAll(dataGenerator.getTransMsgs());
      this.logger.info("Starting generation of LEMMA Technology Model...");
      final LemmaTechnologySubGenerator technologyGenerator = new LemmaTechnologySubGenerator(this.openAPI, genPath, techFilename);
      Technology _generate_1 = technologyGenerator.generate();
      final Pair<String, Technology> techModel = Pair.<String, Technology>of(techFilename, _generate_1);
      this.logger.debug("Adding encountered messages to log.");
      this.transMsgs.addAll(technologyGenerator.getTransMsgs());
      this.logger.info("Starting generation of LEMMA Service Model...");
      final LemmaServiceSubGenerator serviceGenerator = new LemmaServiceSubGenerator(this.openAPI, dataModel, techModel, genPath, serviceFilename);
      serviceGenerator.generate(prefixService);
      this.logger.debug("Adding encountered messages to log.");
      _xblockexpression = this.transMsgs.addAll(serviceGenerator.getTransMsgs());
    }
    return _xblockexpression;
  }
  
  @Pure
  public List<String> getTransMsgs() {
    return this.transMsgs;
  }
}
