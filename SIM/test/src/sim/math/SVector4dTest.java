/**
 * 
 */
package sim.math;

import static org.junit.Assert.*;

import java.io.BufferedWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe SVector4d.
 *
 * @author Simon Vézina
 * @since 2015-10-24
 * @version 2015-10-24
 */
public class SVector4dTest {

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
   * Test #1 : Test pour vérifier la fonctionnalité de la lecture d'un string comme paramètre d'initialisation du vecteur.
   */
  private static void test1()
  {
    String s;
        
    s = "[3.4  4.5  3.2]";
    try{
    SVector4d v = new SVector4d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "(3.7, 1.5, 5.2)";
    try{
    SVector4d v = new SVector4d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "3.4  chien  3.8";
    try{
    SVector4d v = new SVector4d(s);
    System.out.println(v);
    }catch(Exception e){ System.out.println(e); }
    
    s = "(3.4 , 4.6 , 3.8 , 5.8)";
    try{
    SVector4d v = new SVector4d(s);
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
      String file_name = "vector4dTest.txt";
      SVector4d v = new SVector4d(0.2, 0.3, 0.5, 2.4);
      
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
  
}
