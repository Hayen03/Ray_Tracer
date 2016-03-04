/**
 * 
 */
package sim.graphic;

import java.awt.Color;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sim.graphics.SColor;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SColor.
 *
 * @author Simon Vézina
 * @since 2015-09-24
 * @version 2015-09-24
 */
public class SColorTest {

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
  public void testConstrutorSColor(){
    
    SColor s_color1 = new SColor(1.0, 0.0, 0.0, 0.0);
    Color color1 = new Color(1.0f, 0.0f, 0.0f, 0.0f);  
    Assert.assertEquals(s_color1, new SColor(color1));
    
    SColor s_color2 = new SColor(0.0, 1.0, 0.0, 0.0);
    Color color2 = new Color(0.0f, 1.0f, 0.0f, 0.0f);
    Assert.assertEquals(s_color2, new SColor(color2));
    
    SColor s_color3 = new SColor(0.2, 0.70, 0.8, 0.0);
    Color color3 = new Color(0.2f, 0.70f, 0.8f, 0.0f);
    Assert.assertEquals(s_color3, new SColor(color3));
    
    SColor s_color4 = new SColor(0.345, 0.721, 0.855, 0.0);
    Color color4 = new Color(0.345f, 0.721f, 0.855f, 0.0f);
    Assert.assertEquals(s_color4, new SColor(color4));
  }
  /*
   
  A FAIRE !!!
  
  public static void main(String[] args)
  {
    test1();
    test2();
    test3();
    test4();
    test5();
  }
  
  private static void test1()
  {
    SColor c = new SColor(1.0, 1.0, 0.2, 0.5);
    System.out.println(c);
    
    System.out.println();
  }

  private static void test2()
  {
    SColor c;
    Color temp ;
    
    c = new SColor(1.0, 2.0, 3.0);
    System.out.println(c);
    temp = c.normalizeColor();
    System.out.println(temp);
    
    c = new SColor(0.5, 0.3, 0.2);
    System.out.println(c);
    temp = c.normalizeColor();
    System.out.println(temp);
    
    System.out.println();
  }
  
  private static void test3()
  {
    SColor c1;
    SColor c2;
    SColor cADD;
    
    c1 = new SColor(0.2, 0.3, 0.4, 0.5);
    System.out.println(c1);
    c2 = new SColor(0.4, 0.8, 0.2, 0.8);
    System.out.println(c2);
    
    cADD = c1.add(c2);
    
    System.out.println("Addition :");
    System.out.println(cADD);
    System.out.println(cADD.normalizeColor());
    
    c1 = new SColor(0.2, 0.3, 0.4, 0.5);
    System.out.println(c1);
    
    double m = 3.0;
    System.out.println("Multiplication par un scalaire : " + m);
    c2 = c1.multiply(m);
    System.out.println(c2);
    
    System.out.println();
  }
  
  private static void test4()
  {
    SColor c = new SColor();
      
    String ligne1 = "(1.0 ,  0.7 ,  0.4 ,  0.3)";
    String ligne2 = "black 0.7";
    String ligne3 = "1.0 abc 0.4 0.3";
    String ligne4 = "BIDON!!!";
    
    try{
    System.out.println(ligne1);
    c = new SColor(ligne1);
    System.out.println(c);
    }catch(Exception e){ System.out.println(e); }
    
    try{
    System.out.println(ligne2);
    c = new SColor(ligne2);
    System.out.println(c);
    }catch(Exception e){ System.out.println(e); }
    
    try{
    System.out.println(ligne3);
    c = new SColor(ligne3);
    System.out.println(c);
    }catch(Exception e){ System.out.println(e); }
    
    try{
    System.out.println(ligne4);
    c = new SColor(ligne4);
    System.out.println(c);
    }catch(Exception e){ System.out.println(e); }
    
    System.out.println();
  }
  
  private static void test5()
  {
    try
    {
      String file_name = "colorTest.txt";
      SColor c = new SColor(0.2, 0.3, 0.5);
      
      System.out.println("Écriture dans le fichier : " + file_name);
      System.out.println("Couleur à écrire : " + c);
      System.out.println();
      
      java.io.FileWriter fw = new java.io.FileWriter(file_name);
      BufferedWriter bw = new BufferedWriter(fw);
      
      c.write(bw);
      
      bw.close(); //  fermer celui-ci en premier, sinon, ERROR !!!
      fw.close();
    }catch(Exception e){ System.out.println(e); }
    
    
  }
  
  */
  
  
}
