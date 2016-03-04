/**
 * 
 */
package sim.application.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sim.util.SLog;

/**
 * JUnit Test de la classe SConfigurationTest.java
 * 
 * @author Simon Vézina
 * @since 2015-11-28
 * @version 2015-11-28
 */
public class SConfigurationTest {

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
  public void test()
  {
    fail("Not yet implemented");
  }

  // À FAIRE ...
  
  /**
   * @param args
   */
  public static void main(String[] args) 
  {
    //test1();
    test2();
    
    try{
      SLog.closeLogFile();
    }catch(IOException ioe){}
    
  }

  /**
   * Test #1 : Verification du comportement de la classe si le fichier de config n'est pas trouvé.
   */
  public static void test1()
  {
    try{
      
      SConfiguration config = new SConfiguration("bidon");
      config.getReadDataFileName();
    }catch(Exception e){ e.printStackTrace(); }
    
    System.out.println("Fin du test #1");
  }
  
  /**
   * Test #2 : Vérification du bon fonctionnement de la lecture du fichier de config.
   */
  public static void test2()
  {
    try{
      SConfiguration config = new SConfiguration();
      
      SLog.logWriteLine("Read file name : " + config.getReadDataFileName());
      SLog.logWriteLine("Write file name : " + config.getWriteDataFileName());
      
    }catch(Exception e){ e.printStackTrace(); }
    
    System.out.println("Fin du test #2");
  }
  
}
