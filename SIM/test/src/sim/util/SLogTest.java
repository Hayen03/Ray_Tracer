/**
 * 
 */
package sim.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SLog.
 * 
 * @author Simon Vézina
 * @since 2016-01-14
 * @version 2016-01-14
 */
public class SLogTest {

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
   * Méthode teste pour les fonctionnalités de la classe SLog.
   * @param args
   */
  public static void main(String[] args)
  {
    Test1();
    Test2();
    Test3();
  }
  
  private static void Test1()
  {
    try{
    SLog.setLogFileName("logTest.TXT");
    }catch(Exception e){System.out.println(e);}
    
    SLog.logWriteLine("Ceci est un test!");
    SLog.logWriteLine("Ceci est encore un test!");
    
    try{
      SLog.closeLogFile();
    }catch(IOException ioe){}
  }
  
  private static void Test2()
  {
    try{
      SLog.setLogFileName("logTest.abc");  
    }catch(Exception e){System.out.println(e);}
    
    try{
      SLog.setLogFileName("logTest.ABC");  
      }catch(Exception e){System.out.println(e);}
  }
  
  private static void Test3()
  {
    try{
      SLog.setLogFileName("log_fichier1.txt");
    }catch(Exception e){System.out.println(e);}
    
    SLog.logWriteLine("Fichier 1 : Ceci est un test!");
    SLog.logWriteLine("Fichier 1 : Ceci est encore un test!");
    
    try{
      SLog.setLogFileName("log_fichier2.txt");
    }catch(Exception e){System.out.println(e);}
      
    SLog.logWriteLine("Fichier 2 : Ceci est un test!");
    SLog.logWriteLine("Fichier 2 : Ceci est encore un test!");
      
    try{
      SLog.closeLogFile();
    }catch(IOException ioe){}
  }
  
}
