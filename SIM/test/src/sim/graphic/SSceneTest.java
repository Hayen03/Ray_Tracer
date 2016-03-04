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

import sim.graphics.SScene;
import sim.util.SBufferedReader;
import sim.util.SLog;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SColor.
 * 
 * @author Simon Vézina
 * @since 2015-10-07
 * @version 2015-10-07
 */
public class SSceneTest {

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

  //À FAIRE ...
  
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

  public static void test1()
  {
    /*
    try
    {
      java.io.FileReader fr = new java.io.FileReader("lectureScene.txt");
      SBufferedReader br = new SBufferedReader(fr);
      
      SScene scene = new SScene(br);
            
      java.io.FileWriter fw = new java.io.FileWriter("ecritureScene.txt");
      BufferedWriter bw = new BufferedWriter(fw);
      
      scene.write(bw);
      
      bw.close(); //  fermer celui-ci en premier, sinon, ERROR !!!
      fw.close();
    }
    catch(java.io.FileNotFoundException e){ SLog.logWriteLine(e.getMessage()); }
    catch(Exception e){ e.printStackTrace(); }  
    */
  }
  
}
