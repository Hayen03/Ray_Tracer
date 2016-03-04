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
 * JUnit test permettant de valider les fonctionnalités de la classe SKeyboard.
 * 
 * @author Simon Vézina
 * @since 2016-01-13
 * @version 2016-01-13
 */
public class SKeyboardTest {

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

  // À FAIRE
  
  /**
   * Méthode pour tester les fonctionnalités de la classe SKeyBoard.
   */
  public static void main(String []args)
  {
    //Tester l'entré d'un String
    System.out.print("Tapez un String au clavier : ");
    String s = SKeyboard.readString();
    System.out.println("Vous avez tapé : " + s);
    
    //Tester l'entré d'un float
    System.out.print("Tapez un Float au clavier : ");
    float f = SKeyboard.readFloat();
    System.out.println("Vous avez tapé : " + String.valueOf(f));
  }
  
}
