/**
 * 
 */
package sim.util;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit Test de la classe SStringUtil.
 * 
 * @author Simon Vézina
 * @since 2015-12-02
 * @version 2015-12-02
 */
public class SStringUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception
  {
  }

  @Test
  public void testmerge()
  {
    String[] tab1 = { "ab", "ac", "ad" };
    
    String[] tab2 = { "bb", "bc" };
    
    String[] expected = { "ab", "ac", "ad", "bb", "bc" };
    
    Assert.assertEquals(Arrays.asList(expected), Arrays.asList(SStringUtil.merge(tab1, tab2)));
  }

}
