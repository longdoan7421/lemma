/**
 * generated by Xtext 2.12.0
 */
package de.fhdo.ddmm.data.tests;

import com.google.inject.Inject;
import de.fhdo.ddmm.data.Version;
import de.fhdo.ddmm.data.tests.DataDslInjectorProvider;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(DataDslInjectorProvider.class)
@SuppressWarnings("all")
public class DataDslParsingTest {
  @Inject
  private ParseHelper<Version> parseHelper;
  
  @Test
  public void loadModel() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Hello Xtext!");
      _builder.newLine();
      final Version result = this.parseHelper.parse(_builder);
      Assert.assertNotNull(result);
      Assert.assertTrue(result.eResource().getErrors().isEmpty());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}