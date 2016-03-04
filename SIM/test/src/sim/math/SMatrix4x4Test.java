package sim.math;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test permettant de valider les fonctionnalités de la classe <b>SMatrix4x4</b>.
 * 
 * @author Simon Vézina
 * @since 2015-08-12
 * @version 2015-12-21
 */
public class SMatrix4x4Test {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testRzyx() 
  {
    //---------------------------------------------------------------------------------------------------------------
    /*
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    SVector3d v = new SVector3d(30.0, 40.0, 50.0);
    
    SMatrix4x4 mX = SMatrix4x4.rotationX(v.getX());
    SMatrix4x4 mY = SMatrix4x4.rotationY(v.getY());
    SMatrix4x4 mZ = SMatrix4x4.rotationZ(v.getZ());
    
    SMatrix4x4 matrix_expected = new SMatrix4x4(0.49240387650610407, -0.456825992585671,   0.7408430568614907,  0.0,
                                                0.5868240888334652,   0.8028723374794714,  0.10504046113295196, 0.0,
                                                -0.6427876096865393,  0.38302222155948895, 0.6634139481689384,  0.0,
                                                0.0,                  0.0,                 0.0,                 1.0);
                                             
    Assert.assertEquals(matrix_expected, SMatrix4x4.Rzyx(v));                                          
    */
    //----------------------------------------------------------------------------------------------------------------
  }

  @Test
  public void testRxyz() 
  {
    //----------------------------------------------------------------------------------------------------------------
    /*
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    SVector3d v = new SVector3d(30.0, 40.0, 50.0);
    
    SMatrix4x4 mX = SMatrix4x4.rotationX(v.getX());
    SMatrix4x4 mY = SMatrix4x4.rotationY(v.getY());
    SMatrix4x4 mZ = SMatrix4x4.rotationZ(v.getZ());
    
    SMatrix4x4 matrix_expected = new SMatrix4x4(0.49240387650610407, -0.5868240888334652,   0.6427876096865393,  0.0,
                                                0.8700019037522058,   0.31046846097336755, -0.38302222155948895, 0.0,
                                                0.025201386257487246, 0.7478280708194912,   0.6634139481689384,  0.0,
                                                0.0,                  0.0,                  0.0,                 1.0);

    Assert.assertEquals(matrix_expected, SMatrix4x4.Rxyz(v));
    */
    //----------------------------------------------------------------------------------------------------------------
  }
  
  /**
   * JUnit test évaluant la propriété de l'identité associée au produit des matrices Rxyz et Rzyx
   * et la propriété de la commutativité dans le résultat de ce produit.
   */
  @Test
  public void testIdentityRxyzAndRzyx() 
  {
    //------------------------------------------------
    //POUR LABORATOIRE :
    // Mettre en commentaire ce bloc et laisser l'indication qui suit.
    //
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    //----------------------------------------------------------------
    /*
    SVector3d v = new SVector3d(30.0, 40.0, 50.0);
    SVector3d inv_v = v.multiply(-1);
    
    Assert.assertEquals(SMatrix4x4.identity(), SMatrix4x4.Rxyz(v).multiply(SMatrix4x4.Rzyx(inv_v)));
    Assert.assertEquals(SMatrix4x4.identity(), SMatrix4x4.Rzyx(v).multiply(SMatrix4x4.Rxyz(inv_v)));
    */
  }
  
  @Test
  public void testTrRzyxSc()
  {
    //----------------------------------------------------------------------------------------------------------------
    /*
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    SVector3d scale = new SVector3d(2.3, 4.5, 7.6);
    SVector3d rotation = new SVector3d(30.0, 60.0, -15.0);
    SVector3d translation = new SVector3d(-4.5, 3.2, -5.3);
    
    SMatrix4x4 matrix_expected = new SMatrix4x4(1.1108147002324287,  2.8908090895991276,  4.52226483845811,  -4.5,
                                                -0.2976419018678989, 3.2599996637256057, -5.145786696982827,  3.2,
                                                -1.9918584287042087, 1.125,               3.2908965343808676, -5.3,
                                                0.0,                 0.0,                 0.0,                 1.0);
    
    Assert.assertEquals(matrix_expected, SMatrix4x4.TrRzyxSc(translation, rotation, scale));
    */
    //----------------------------------------------------------------------------------------------------------------
  }
  
  @Test
  public void testScRxyzTr()
  {
    //-------------------------------------------------------------------------------------------------------------
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    /*
    SVector3d scale = new SVector3d(2.3, 4.5, 7.6);
    SVector3d rotation = new SVector3d(30.0, 60.0, -15.0);
    SVector3d translation = new SVector3d(-4.5, 3.2, -5.3);
     
    SMatrix4x4 matrix_expected = new SMatrix4x4(1.1108147002324287, 0.2976419018678989, 1.9918584287042087, -14.603061737200958,
                                                0.8735142772210072, 4.268647069914666,  -1.125,              15.691356376232397,
                                                -6.489289581237268, 2.1952495828140908, 3.290896534380868,   18.7848501483542,
                                                0.0,                0.0,                0.0,                 1.0);

    
    Assert.assertEquals(matrix_expected, SMatrix4x4.ScRxyzTr(scale, rotation, translation));
    */
    //-------------------------------------------------------------------------------------------------------------
  }
  
  /**
   * JUnit test évaluant la propriété de l'identité associée au produit des matrices TrRzyxSc et ScRxyzTr
   * et la propriété de la commutativité dans le résultat de ce produit.
   */
  @Test
  public void testIdentityTrRzyxScAndScRxyzTr() 
  {
    //------------------------------------------------
    //POUR LABORATOIRE :
    // Mettre en commentaire ce bloc et laisser l'indication qui suit.
    //
    // RETIRER LES COMMENTAIRES LORSQUE LE TEST DOIT ÊTRE EXÉCUTÉ.
    //----------------------------------------------------------------
    /*
    SVector3d scale = new SVector3d(2.3, 4.5, 7.6);
    SVector3d scale_inv = new SVector3d(1.0/scale.getX(), 1.0/scale.getY(), 1.0/scale.getZ());
    
    SVector3d translation = new SVector3d(-4.5, 3.2, -5.3);
    SVector3d translation_inv = translation.multiply(-1.0);
    
    SVector3d rotation = new SVector3d(30.0, 60.0, -15.0);
    SVector3d rotation_inv = rotation.multiply(-1.0);
      
    Assert.assertEquals(SMatrix4x4.identity(), SMatrix4x4.TrRzyxSc(translation, rotation, scale).multiply(SMatrix4x4.ScRxyzTr(scale_inv, rotation_inv, translation_inv)));
    Assert.assertEquals(SMatrix4x4.identity(), SMatrix4x4.ScRxyzTr(scale, rotation, translation).multiply(SMatrix4x4.TrRzyxSc(translation_inv, rotation_inv, scale_inv)));
    */
  }
  
}//fin de la classe SMatrix4x4Test
