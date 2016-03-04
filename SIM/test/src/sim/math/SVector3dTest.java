package sim.math;

import java.io.BufferedWriter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SVector3d.
 * 
 * @author Simon Vézina
 * @since 2015-08-13
 * @version 2015-08-16
 */
public class SVector3dTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testdot()
  {
    SVector3d i = new SVector3d(1.0, 0.0, 0.0);
    SVector3d j = new SVector3d(0.0, 1.0, 0.0);
    SVector3d k = new SVector3d(0.0, 0.0, 1.0);
    
    Assert.assertEquals(i.dot(i), 1.0, SMath.EPSILON);
    Assert.assertEquals(j.dot(j), 1.0, SMath.EPSILON);
    Assert.assertEquals(k.dot(k), 1.0, SMath.EPSILON);
    
    Assert.assertEquals(i.dot(i.multiply(-1.0)), -1.0, SMath.EPSILON);
    
    Assert.assertEquals(i.dot(j), 0.0, SMath.EPSILON);
    Assert.assertEquals(i.dot(k), 0.0, SMath.EPSILON);
    Assert.assertEquals(j.dot(k), 0.0, SMath.EPSILON);
  }
  
  @Test
  public void testcross()
  {
    SVector3d i = new SVector3d(1.0, 0.0, 0.0);
    SVector3d j = new SVector3d(0.0, 1.0, 0.0);
    SVector3d k = new SVector3d(0.0, 0.0, 1.0);
    
    SVector3d zero = new SVector3d(0.0, 0.0, 0.0);
    
    Assert.assertEquals(i.cross(j), k);
    Assert.assertEquals(j.cross(i), k.multiply(-1.0));
    
    Assert.assertEquals(i.cross(i), zero);
    
    SVector3d a = new SVector3d(1.0, 10.0, 30.0);
    SVector3d b = new SVector3d(4.0, -5.0, 6.0);
        
    Assert.assertEquals(a.dot(a.cross(b)), 0.0, SMath.EPSILON);
    Assert.assertEquals(b.dot(a.cross(b)), 0.0, SMath.EPSILON);
  }
  
  @Test
  public void testAcrossBcrossC() 
  {
    SVector3d a = new SVector3d(1.0, 10.0, 30.0);
    SVector3d b = new SVector3d(4.0, -5.0, 6.0);
    SVector3d c = new SVector3d(7.0, 8.0, -9.0);
    
    Assert.assertEquals(a.cross(b).cross(c), SVector3d.AcrossBcrossC(a, b, c));
  }
 
  @Test
  public void testAcross_BcrossC() 
  {
    SVector3d a = new SVector3d(1.0, 10.0, 30.0);
    SVector3d b = new SVector3d(4.0, -5.0, 6.0);
    SVector3d c = new SVector3d(7.0, 8.0, -9.0);
    
    Assert.assertEquals(a.cross(b.cross(c)), SVector3d.Across_BcrossC(a, b, c));
  }
  
  @Test
  public void testAdotCsubstractBdotC()
  {
    SVector3d a = new SVector3d(1.0, 10.0, 30.0);
    SVector3d b = new SVector3d(4.0, -5.0, 6.0);
    SVector3d c = new SVector3d(7.0, 8.0, -9.0);
    
    Assert.assertEquals(a.dot(c) - b.dot(c), SVector3d.AdotCsubstractBdotC(a, b, c), SMath.EPSILON);
  }

  @Test
  public void testAmultiplyBaddC()
  {
    double a = -10.0;
    SVector3d B = new SVector3d(4.0, -5.0, 6.0);
    SVector3d C = new SVector3d(7.0, 8.0, -9.0);
    
    Assert.assertEquals(B.multiply(a).add(C), SVector3d.AmultiplyBaddC(a,B,C));
  }
  
  @Test
  public void testEquals() 
  {
    SVector3d v1 = new SVector3d(0.0, 1.0, 1.0);
    SVector3d v2 = new SVector3d(0.0, 1.0, 1.0);
    SVector3d v3 = new SVector3d(0.0, 1.0, 1.0000000000001);
    SVector3d v4 = new SVector3d(0.0, 1.0, 1.0000001);
    
    Assert.assertEquals(v1, v2);
    Assert.assertEquals(v1, v3);
    Assert.assertEquals(v2, v3);
    
    Assert.assertNotEquals(v1, v4);
    Assert.assertNotEquals(v2, v4);
    Assert.assertNotEquals(v3, v4);
  }

  //À FAIRE !!!
  
  /**
   * @param args
   */
  public static void main(String[] args) 
  {
    test1();
    test2();
    test3();
  }

  /**
   * Test #1 : Test pour vérifier la fonctionnalité de la lecture d'un string comme paramètre d'initialisation du vecteur.
   */
  private static void test1()
  {
    String s;
        
    s = "[3.4  4.5  3.2]";
    try{
    SVector3d v = new SVector3d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "(3.7, 1.5, 5.2)";
    try{
    SVector3d v = new SVector3d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "3.4  chien  3.8";
    try{
    SVector3d v = new SVector3d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "(3.4 , 4.6 , 3.8 , 5.8)";
    try{
    SVector3d v = new SVector3d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    System.out.println();
  }
  
  /**
   * Test #2 : Test pour vérifier la fonctionnalité de l'écriture du vecteur dans un fichier txt
   */
  private static void test2()
  {
    try
    {
      String file_name = "vector3dTest.txt";
      SVector3d v = new SVector3d(0.2, 0.3, 0.5);
      
      System.out.println("Écriture dans le fichier : " + file_name);
      System.out.println("Vecteur à écrire : " + v);
      System.out.println();
      
      java.io.FileWriter fw = new java.io.FileWriter(file_name);
      BufferedWriter bw = new BufferedWriter(fw);
      
      v.write(bw);
      
      bw.close(); //  fermer celui-ci en premier, sinon, ERROR !!!
      fw.close();
    }catch(Exception e){ System.out.println(e); }
    
    System.out.println();
  }
  
  /**
   * Test #2 : Test pour vérifier la fonctionnalité mathématique du vecteur.
   */
  private static void test3()
  {
    SVector3d v1 = new SVector3d(0.0, 1.0, 1.0);
    SVector3d v2 = new SVector3d(0.0, 1.0, 1.0);
    SVector3d v3 = new SVector3d(0.0, 1.0, 1.0000000001);
    SVector3d v4 = new SVector3d(0.0, 1.0, 1.0000001);
    
    System.out.println("v1 = " + v1 + ", v2 = " + v2 + " : equals = " + v1.equals(v2) +".");
    System.out.println("v1 = " + v1 + ", v3 = " + v3 + " : equals = " + v1.equals(v3) +".");
    System.out.println("v1 = " + v1 + ", v4 = " + v4 + " : equals = " + v1.equals(v4) +".");
  }
  
  
  
}
