/**
 * 
 */
package sim.math;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SMath.
 * 
 * @author Simon Vézina
 * @since 2015-09-05
 * @version 2015-11-15
 */
public class SMathTest {

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
  public void testnearlyEquals()
  {
    final double epsilon = 1e-10;
    
    //Avec choix de la précision quelconque
    Assert.assertEquals(false, SMath.nearlyEquals(1.0, 2.0, 0.001));
    Assert.assertEquals(true, SMath.nearlyEquals(1.0, 2.0, 10));
    
    //Test des chiffres très près de 1
    Assert.assertEquals(false, SMath.nearlyEquals(1.0, 1.0 + 1e-4, epsilon));
    Assert.assertEquals(false, SMath.nearlyEquals(1.0, 1.0 + 1e-8, epsilon));
    Assert.assertEquals(false, SMath.nearlyEquals(1.0, 1.0 + 1e-10, epsilon));
    Assert.assertEquals(true, SMath.nearlyEquals(1.0, 1.0 + 1e-11, epsilon));
    
    //Test des très gros chiffres
    Assert.assertEquals(false, SMath.nearlyEquals(5.000001e14, 5.0e14, epsilon));
    Assert.assertEquals(false, SMath.nearlyEquals(5.0e14, 5.0e14 + 1e5, epsilon));
    Assert.assertEquals(false, SMath.nearlyEquals(5.0e14, 5.0e14 + 6e4, epsilon));
    Assert.assertEquals(true, SMath.nearlyEquals(5.0e14, 5.0e14 + 1e3, epsilon));
    
    //Test des très petits chiffres avec le zéro
    Assert.assertEquals(false, SMath.nearlyEquals(0.0, 1e-9, epsilon));
    Assert.assertEquals(true, SMath.nearlyEquals(0.0, 1e-11, epsilon));
    
    //Test des très petites chiffres sans le zéro
    Assert.assertEquals(false, SMath.nearlyEquals(1e-6, 1e-8, epsilon));
    Assert.assertEquals(true, SMath.nearlyEquals(1e-10, 1e-12, epsilon));
    Assert.assertEquals(true, SMath.nearlyEquals(1e-12, 1e-26, epsilon));
  }

  @Test
  public void testlinearRealRoot()
  {
    // à faire ...
  }
  
  /**
   * JUnit Test de la méthode quadricRealRoot.
   */
  @Test
  public void testquadricRealRoot()
  {
    // Scénarios tirés de la résolution d'un problème de physique 
    testquadricRealRootScenario(1.0, 0.0, -1.0, new double[0], SMath.EPSILON);
    testquadricRealRootScenario(-1.0, 6.0, -6.0, new double[] {1.27 , 4.73 }, 0.01);
    testquadricRealRootScenario(-4.9, 2.0, 1.0, new double[] {-0.29 , 0.70 }, 0.01);
    
    // Scénarios aléatoires
    testquadricRealRootScenario(-0.52, 2.0, 7.2, new double[] {-2.2655 , 6.1117 }, 0.0001);
    testquadricRealRootScenario(7.52, -8.3, -127.8, new double[] {-3.6074 , 4.7111 }, 0.0001);
    testquadricRealRootScenario(12.82, 6.32, -42.8, new double[] {-2.0902 , 1.5972 }, 0.0001);
    testquadricRealRootScenario(-4.29, 4.12, -12.8, new double[0], 0.0001);
  }
  
  /**
   * Méthode pour faire le test d'un scénario de la résolution d'un polynôme du 2ième degré de la forme
   * <ul>ax^2 + bx + c = 0.</ul>
   * 
   * @param a - Le coefficient devant le paramètre x^2 du polynôme. 
   * @param b - Le coefficient devant le paramètre x du polynôme.
   * @param c - Le coefficient devant le paramètre 1 du polynôme.
   * @param expected_solution - La solution attendue dans un tableau trié en ordre croissant.
   * @param epsilon - Le niveau de précision des solutions.
   */
  public void testquadricRealRootScenario(double a, double b, double c, double[] expected_solution, double epsilon)
  {
    double[] calculated_solution1 = SMath.quadricRealRoot(a, b, c);
    
    for(int i = 0; i < expected_solution.length; i++)
      Assert.assertEquals(expected_solution[i], calculated_solution1[i], epsilon);
  }
  
  @Test
  public void testcubicRealRoot()
  {
    // à faire ...
  }
  
  @Test
  public void testquarticRealRoot()
  {
    // à faire ...
  }
  
}//fin de la classe de test SMathTest
