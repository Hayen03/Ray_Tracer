/**
 * 
 */
package sim.graphic;

import static org.junit.Assert.*;

import java.io.BufferedWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sim.graphics.SCamera;
import sim.util.SBufferedReader;
import sim.util.SLog;

/**
 * JUnit Test de la classe SCamera.
 * 
 * @author Simon Vézina
 * @since 2015-11-28
 * @version 2015-11-28
 */
public class SCameraTest {

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

  //À faire ...
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    test1();
    
    try{
    SLog.closeLogFile();
    }catch(Exception e){}
    
  }

  private static void test1()
  {
    try
    {
      java.io.FileReader fr = new java.io.FileReader("lectureCamera.txt");
      SBufferedReader br = new SBufferedReader(fr);
      
      SCamera camera = new SCamera(br);
            
      java.io.FileWriter fw = new java.io.FileWriter("ecritureCamera.txt");
      BufferedWriter bw = new BufferedWriter(fw);
      
      camera.write(bw);
      
      bw.close(); //  fermer celui-ci en premier, sinon, ERROR !!!
      fw.close();
    }
    catch(java.io.FileNotFoundException e){ SLog.logWriteLine(e.getMessage()); }
    catch(Exception e){ SLog.logWriteLine(e.getMessage()); }    
  }
  
}
