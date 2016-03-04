/**
 * 
 */
package sim.geometry;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test - Implémentations de la classe SSphereGeometry.java
 * @author Simon Vézina
 * @since 2015-10-19
 * @version 2015-10-19
 */
public class SSphereGeometryTest {

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
    test2();
  }

  /**
   * Test #1 : Test pour vérifier si la fonction d'intersection avec la sphère fonctionne et si l'image est bien positionner sur l'écran.
   */
  public static void test1()
  {
    /*
    sim.graphics.SViewport viewport = new sim.graphics.SViewport(500, 500, "TESTsphere");
    
    //Reproduction de l'exemple dans les notes de cours
    sim.graphics.SCamera camera = new sim.graphics.SCamera(new SVector3d(0.0, 0.0, 0.0),  //camera à l'origine
                                                     new SVector3d(1.0, 0.0, 0.0),  //regarde en x
                                                     new SVector3d(0.0, 0.0, 1.0)); //up en z
    
    //sim.graphics.SViewFrustum view_frustum = new sim.graphics.SViewFrustum(60, 1.0, 6.0);
    sim.graphics.SViewFrustum view_frustum = new sim.graphics.SViewFrustum(camera, viewport);
    
    SSphereGeometry sphere = new SSphereGeometry(new SVector3d(3.0, 1.0, 0.0), 1.0);
    
    try{
      
      while(viewport.hasNextPixel())
      {
        sim.graphics.SVectorPixel p = viewport.nextPixel();
        SVector3d position_pixel = view_frustum.viewportToViewFrustum(p);
        
        SRay ray = new SRay(camera.getPosition(), position_pixel);
        
        ray = sphere.intersection(ray);
        
        //Mettre une coleur avec un dégradé sous l'effet de la profondeur t
        if(ray.asIntersected())
        {
          float degrade = 1.0f - (float)ray.getT()/(float)camera.getZFar();
          viewport.setColor(p, new java.awt.Color(1.0f*degrade, 0.0f, 0.0f)); //mettre en rouge s'il y a une intersection
        }
        else
          viewport.setColor(p, new java.awt.Color(0.0f, 1.0f, 0.0f));   //mettre en vert s'il n'y a pas d'intersection
      }
      
      viewport.writeImage();
      
    }catch(Exception e){ System.out.println(e.getMessage()); }
    
    */
  }
  
  /**
   * Test #2 : Test pour vérifier l'ordre du paramètre t lors d'une intersection possible sur deux sphères.
   */
  public static void test2()
  {
    /*
    sim.graphics.SViewport viewport = new sim.graphics.SViewport(1000, 500);
    
    //Reproduction de l'exemple dans les notes de cours
    sim.graphics.SCamera camera = new sim.graphics.SCamera(new SVector3d(0.0, 0.0, 0.0),  //camera à l'origine
                                                   new SVector3d(1.0, 0.0, 0.0),  //regarde en x
                                                   new SVector3d(0.0, 0.0, 1.0)); //up en z
    
    //sim.graphics.SViewFrustum view_frustum = new sim.graphics.SViewFrustum(60, 1.0, 6.0);
    sim.graphics.SViewFrustum view_frustum = new sim.graphics.SViewFrustum(camera, viewport);
    
    SSphereGeometry sphere1 = new SSphereGeometry(new SVector3d(3.0, 1.0, 0.0), 1.0);
    SSphereGeometry sphere2 = new SSphereGeometry(new SVector3d(2.0, 0.0, 0.0), 0.5);
    
    try{
      
      while(viewport.hasNextPixel())
      {
        sim.graphics.SVectorPixel p = viewport.nextPixel();
        SVector3d position_pixel = view_frustum.viewportToViewFrustum(p);
        
        SRay ray = new SRay(camera.getPosition(), position_pixel);
        
        SRay ray1 = sphere1.intersection(ray);
        SRay ray2 = sphere2.intersection(ray);
                
        int code = 5;
        
        //Sélection du rayon dont le t est le plus près
        if(!ray1.asIntersected() && !ray2.asIntersected())
          code = 0;
        else
          if(ray1.asIntersected() || ray2.asIntersected())
            if(ray1.asIntersected() && !ray2.asIntersected())
              code = 1;
            else
              code = 2;
            
        //Application du choix du rayon
        switch(code)
        {
          case 0 :  viewport.setColor(p, new java.awt.Color(0.0f, 1.0f, 0.0f));   //mettre en vert s'il n'y a pas d'intersection
                break;
          
          case 1 :  float degrade1 = 1.0f - (float)ray1.getT()/(float)camera.getZFar();
                viewport.setColor(p, new java.awt.Color(1.0f*degrade1, 0.0f, 0.0f));  //mettre en rouge la sphère #1
                break;
          
          case 2 :  float degrade2 = 1.0f - (float)ray2.getT()/(float)camera.getZFar();
                viewport.setColor(p, new java.awt.Color(0.0f, 0.0f, 1.0f*degrade2));  //mettre en bleu la sphère #2
                break;
                
          default : viewport.setColor(p, new java.awt.Color(0.0f, 0.0f, 0.0f));   //mettre en noir s'il y a un problème !!!
                break;
        }//fin switch
      }//fin while  
      
      viewport.writeImage();
      
    }catch(Exception e){ System.out.println(e.getMessage()); }
    
    */
  }
  
}
