/**
 * 
 */
package sim.geometry.space;

import sim.geometry.SAbstractGeometry;
import sim.geometry.SBTriangleGeometry;
import sim.geometry.SGeometry;
import sim.geometry.STriangleGeometry;
import sim.math.SMath;
import sim.math.SVector3d;

/**
 * La classe <b>SBoundingBoxBuilder</b> représente un constructeur de boîte englobante autour de différentes géométries.
 * 
 * <p>
 * Les géométries pouvant être englobées par une boîtes sont les suivantes :
 * <ul>- STriangleGeometry</ul>
 * <ul>- SBtriangleGeometry</ul>
 * <p>
 * 
 * @author Simon Vézina
 * @since 2015-08-04
 * @since 2015-12-17
 */
public class SBoundingBoxBuilder {

  /**
   * La constante <b>POSITIVE_MULTIPLICATOR</b> correspond à une constante faiblement supérieure à 1.0. 
   */
  private static final double POSITIVE_MULTIPLICATOR = 1.0 + 10.0*SMath.EPSILON;
  
  /**
   * La constante <b>POSITIVE_MULTIPLICATOR</b> correspond à une constante faiblement inférieure à 1.0. 
   */
  private static final double NEGATIVE_MULTIPLICATOR = 1.0 - 10.0*SMath.EPSILON;
  
  /**
   * Constructeur d'un fabriquant de boîte englobante par défaut. 
   */
  public SBoundingBoxBuilder()
  {
    
  }

  /**
   * Méthode pour obtenir une boîte englobante pour une géométrie. 
   * Si la géométrie n'est pas admissible à recevoir une boîte englobante, la référence <b>null</b> sera retournée.
   * @param geometry - La géométrie à être emboîtée.
   * @return La boîte englobante autour de la géométrie ou <b>null</b> si aucune boîte n'a été construite.
   */
  public SBoundingBox buildBoundingBox(SGeometry geometry)
  {
    switch(geometry.getCodeName())
    {
      case SAbstractGeometry.TRIANGLE_CODE : return buildBoundingBoxForSTriangleGeometry((STriangleGeometry)geometry);
      
      case SAbstractGeometry.BTRIANGLE_CODE : return buildBoundingBoxForSBTriangleGeometry((SBTriangleGeometry)geometry);
      
      default : return null;
    }
  }
  
  /**
   * Méthode pour obtenir une boîte englobante autour de la géométrie du STriangleGeometry.
   * @param triangle - La géométrie du triangle.
   * @return La boîte englobante autour du triangle.
   */
  private SBoundingBox buildBoundingBoxForSTriangleGeometry(STriangleGeometry triangle)
  {
    SVector3d[] tab = { triangle.getP0(), triangle.getP1(), triangle.getP2() };
    
    SVector3d min = SVector3d.findMinValue(tab);
    SVector3d max = SVector3d.findMaxValue(tab);
    
    //Si le triangle est parallèle à l'un des 6 plans de la boîte, la boîte sera mal définie
    //Modifions par un "delta" nos coordonnées pour éviter cette situation  
    min = new SVector3d(min.getX()*NEGATIVE_MULTIPLICATOR, min.getY()*NEGATIVE_MULTIPLICATOR, min.getZ()*NEGATIVE_MULTIPLICATOR);
    max = new SVector3d(max.getX()*POSITIVE_MULTIPLICATOR, max.getY()*POSITIVE_MULTIPLICATOR, max.getZ()*POSITIVE_MULTIPLICATOR);
    
    return new SBoundingBox(triangle, min, max);
  }
  
  /**
   * Méthode pour obtenir une boîte englobante autour de la géométrie du STriangleGeometry.
   * @param triangle - La géométrie du triangle.
   * @return La boîte englobante autour du triangle.
   */
  private SBoundingBox buildBoundingBoxForSBTriangleGeometry(SBTriangleGeometry triangle)
  {
    SVector3d[] tab = { triangle.getP0(), triangle.getP1(), triangle.getP2() };
    
    SVector3d min = SVector3d.findMinValue(tab);
    SVector3d max = SVector3d.findMaxValue(tab);
    
    //Si le triangle est parallèle à l'un des 6 plans de la boîte, la boîte sera mal définie
    //Modifions par un "delta" nos coordonnées pour éviter cette situation  
    min = new SVector3d(min.getX()*NEGATIVE_MULTIPLICATOR, min.getY()*NEGATIVE_MULTIPLICATOR, min.getZ()*NEGATIVE_MULTIPLICATOR);
    max = new SVector3d(max.getX()*POSITIVE_MULTIPLICATOR, max.getY()*POSITIVE_MULTIPLICATOR, max.getZ()*POSITIVE_MULTIPLICATOR);
    
    return new SBoundingBox(triangle, min, max);
  }
  
}//fin de la classe SBoundingBoxBuilder
