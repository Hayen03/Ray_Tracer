/**
 * 
 */
package sim.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test de la classe SBufferedReader.
 * 
 * @author Simon Vézina
 * @since 2015-12-05
 * @version 2015-12-05
 */
public class SBufferedReaderTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    fail("Not yet implemented");
  }

  // À faire ...
  
  /**
   * @param args
   */
  public static void main(String[] args) 
  {
    test1();
  }

  private static void test1()
  {
    try{
      
      java.io.FileReader fr = new java.io.FileReader("SBufferedReaderTest.txt");
      SBufferedReader sbr = new SBufferedReader(fr);
      
      String line = null;
      
      do
      {
        line = sbr.readLine();
        if(line != null)
          System.out.println((sbr.atLine()-1) + " : " + line);
          
      }while(line != null);
    
      sbr.close();
      fr.close();
    }catch(Exception e){ System.out.println(e.getMessage()); }
  }
  
}
