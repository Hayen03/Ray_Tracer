/**
 * 
 */
package sim.geometry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test - Implémentations de la classe SRay.java
 * 
 * @author Simon Vézina
 * @since 2015-08-18
 * @version 2015-12-02
 */
public class SRayTest {

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
  public void testcompareTo()
  {
    java.util.List<SRay> list = new java.util.LinkedList<SRay>();
    java.util.List<SRay> sort_list = new java.util.LinkedList<SRay>();
    
    SRay initial_ray = new SRay(null, null, SRay.DEFAULT_REFRACTIVE_INDEX);
    SRay ray1 = initial_ray.intersection(null, null, 1.0, false);
    SRay ray2 = initial_ray.intersection(null, null, 2.0, false);
    SRay ray3 = initial_ray.intersection(null, null, 3.0, false);
    SRay ray4 = initial_ray.intersection(null, null, 4.0, false);
    SRay ray5 = initial_ray.intersection(null, null, 5.0, false);
        
    //Dans l'ordre
    sort_list.add(ray1);
    sort_list.add(ray2);
    sort_list.add(ray3);
    sort_list.add(ray4);
    sort_list.add(ray5);
    sort_list.add(initial_ray);
    
    //Dans le désordre (avant le tirage par compareTo)
    list.add(ray2);
    list.add(ray4);
    list.add(ray5);
    list.add(initial_ray);
    list.add(ray1);
    list.add(ray3);
    
    //Faire le triage
    java.util.Collections.sort(list);
    
    //Comparer les listes
    Assert.assertEquals(sort_list, list);
  }

}
