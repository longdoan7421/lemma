package de.fhdo.lemma.service.open_api;

import de.fhdo.lemma.data.DataFactory;
import de.fhdo.lemma.data.PrimitiveType;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

/**
 * This class collects _static_ utility methods for the OpenAPI plugin.
 * 
 * @author <a href="mailto:jonas.sorgalla@fh-dortmund.de">Jonas Sorgalla</a>
 */
@SuppressWarnings("all")
public final class OpenApiUtil {
  /**
   * Factory to actually create and manipulate a LEMMA DataModel
   */
  private static final DataFactory DATA_FACTORY = DataFactory.eINSTANCE;
  
  public static String removeInvalidCharsFromName(final String str) {
    String ret = str;
    ret = ret.replaceAll("[^a-zA-Z0-9_]", "");
    boolean _isAlphabetic = Character.isAlphabetic(ret.charAt(0));
    boolean _not = (!_isAlphabetic);
    if (_not) {
      ret = ("v" + ret);
    }
    return ret;
  }
  
  /**
   * Serialize a LEMMA data or mapping model to the given file path. Returns true if the file path
   * and the contents extracted from the model's root are not empty.
   */
  public static boolean writeModel(final EObject modelRoot, final String filepath) {
    throw new Error("Unresolved compilation problems:"
      + "\nType mismatch: cannot convert from CharSequence to String");
  }
  
  /**
   * Write string contents to the given file path. Returns true if the file path and the contents
   * are not empty.
   */
  public static boolean writeFile(final String filepath, final String content) {
    try {
      if ((StringExtensions.isNullOrEmpty(filepath) || StringExtensions.isNullOrEmpty(content))) {
        return false;
      }
      Files.write(Paths.get(filepath), content.getBytes());
      return true;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static PrimitiveType deriveIntType(final String typeDesc) {
    PrimitiveType _switchResult = null;
    if (typeDesc != null) {
      switch (typeDesc) {
        case "int32":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveInteger();
          break;
        case "int64":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveLong();
          break;
        default:
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveInteger();
          break;
      }
    } else {
      _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveInteger();
    }
    return _switchResult;
  }
  
  public static PrimitiveType deriveNumberType(final String typeDesc) {
    PrimitiveType _switchResult = null;
    if (typeDesc != null) {
      switch (typeDesc) {
        case "float":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveFloat();
          break;
        case "double":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveDouble();
          break;
        default:
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveDouble();
          break;
      }
    } else {
      _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveDouble();
    }
    return _switchResult;
  }
  
  public static PrimitiveType deriveStringType(final String typeDesc) {
    PrimitiveType _switchResult = null;
    if (typeDesc != null) {
      switch (typeDesc) {
        case "byte":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveString();
          break;
        case "binary":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveString();
          break;
        case "date":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveDate();
          break;
        case "date-time":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveDate();
          break;
        case "password":
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveString();
          break;
        default:
          _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveString();
          break;
      }
    } else {
      _switchResult = OpenApiUtil.DATA_FACTORY.createPrimitiveString();
    }
    return _switchResult;
  }
}
