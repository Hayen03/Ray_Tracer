/**
 * 
 */
package sim.graphic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sim.graphics.STextureReader;
import sim.util.SLog;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe STexture.
 * 
 * @author Simon Vézina
 * @since 2015-10-03
 * @version 2016-01-08
 */
public class STextureTest {

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
  public void testEquals()
  {
    SLog.setConsoleLog(false);
    SLog.setFileLog(false);
    
    STextureReader r1 = new STextureReader("star_wars_1.png");
    STextureReader r2 = new STextureReader("star_wars_1.png");
    STextureReader r3 = new STextureReader("star_wars_2.png");
    STextureReader r4 = new STextureReader("star_wars_3.png");  // légèrement différente

    if(r1.asRead())
      Assert.assertEquals(true, r1.getValue().equals(r1.getValue()));
    else
      fail("Texture #1 = " + r1.getFileName() +" pas trouvée.");
  
    
    if(r1.asRead() && r2.asRead())
      Assert.assertEquals(true, r1.getValue().equals(r2.getValue()));
    else
      fail("Texture #1 = " + r1.getFileName() + " et/ou Texture #2 = " + r2.getFileName() + " pas trouvées.");
  
    
    if(r1.asRead() && r3.asRead())
      Assert.assertEquals(true, r1.getValue().equals(r3.getValue()));
    else
      fail("Texture #1 = " + r1.getFileName() + " et/ou Texture #3 = " + r3.getFileName() + " pas trouvées.");
  
    
    if(r1.asRead() && r4.asRead())
      Assert.assertEquals(false, r1.getValue().equals(r4.getValue()));
    else
      fail("Texture #1 = " + r1.getFileName() + " et/ou Texture #4 = " + r4.getFileName() + " pas trouvées.");
  }

  //À faire ...

  public static void main(String[] arg)
  {
    test1();
  }
  
  private static void test1()
  {
    /*
    try{
    STexture t = new STextureLoader().loadTexture("GlassEnvironmentMap.jpg");
    
    File file = new File("test_image" + "." + "png");
    
    
    try{
    ImageIO.write(buffered_image,"png", file);
    }catch(IOException e){
      
    }
    
    
    }catch(SLoaderException e){
      
    }
    
    */
  }
  
  
}
