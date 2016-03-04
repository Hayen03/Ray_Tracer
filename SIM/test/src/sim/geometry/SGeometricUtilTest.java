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

import sim.math.SVector3d;

/**
 * JUnit test de la classe SGeometricUtil.
 * 
 * @author Simon Vézina
 * @since 2015-11-24
 * @version 2015-11-25
 */
public class SGeometricUtilTest {

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
  public void testisOneSphereSurface() 
  {
    SVector3d r_s = new SVector3d(0.0, 0.0, 0.0);
    double R = 1.0;
    
    SVector3d outside = new SVector3d(2.0, 2.0, 2.0);
    SVector3d on = new SVector3d(3.4, 5.58, -4.932).normalize();
    SVector3d inside = new SVector3d(-32.5, -4.78, 36.2).normalize().multiply(0.7);
    
    Assert.assertEquals(1, SGeometricUtil.isOnSphereSurface(r_s, R, outside));
    Assert.assertEquals(0, SGeometricUtil.isOnSphereSurface(r_s, R, on));
    Assert.assertEquals(-1, SGeometricUtil.isOnSphereSurface(r_s, R, inside));
  }

}//fin de la classe SGeometricUtil
