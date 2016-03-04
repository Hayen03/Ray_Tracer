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
 * La classe <b>SBoundingBoxBuilder</b> repr�sente un constructeur de bo�te englobante autour de diff�rentes g�om�tries.
 * 
 * <p>
 * Les g�om�tries pouvant �tre englob�es par une bo�tes sont les suivantes :
 * <ul>- STriangleGeometry</ul>
 * <ul>- SBtriangleGeometry</ul>
 * <p>
 * 
 * @author Simon V�zina
 * @since 2015-08-04
 * @since 2015-12-17
 */
public class SBoundingBoxBuilder {

  /**
   * La constante <b>POSITIVE_MULTIPLICATOR</b> correspond � une constante faiblement sup�rieure � 1.0. 
   */
  private static final double POSITIVE_MULTIPLICATOR = 1.0 + 10.0*SMath.EPSILON;
  
  /**
   * La constante <b>POSITIVE_MULTIPLICATOR</b> correspond � une constante faiblement inf�rieure � 1.0. 
   */
  private static final double NEGATIVE_MULTIPLICATOR = 1.0 - 10.0*SMath.EPSILON;
  
  /**
   * Constructeur d'un fabriquant de bo�te englobante par d�faut. 
   */
  public SBoundingBoxBuilder()
  {
    
  }

  /**
   * M�thode pour obtenir une bo�te englobante pour une g�om�trie. 
   * Si la g�om�trie n'est pas admissible � recevoir une bo�te englobante, la r�f�rence <b>null</b> sera retourn�e.
   * @param geometry - La g�om�trie � �tre embo�t�e.
   * @return La bo�te englobante autour de la g�om�trie ou <b>null</b> si aucune bo�te n'a �t� construite.
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
   * M�thode pour obtenir une bo�te englobante autour de la g�om�trie du STriangleGeometry.
   * @param triangle - La g�om�trie du triangle.
   * @return La bo�te englobante autour du triangle.
   */
  private SBoundingBox buildBoundingBoxForSTriangleGeometry(STriangleGeometry triangle)
  {
    SVector3d[] tab = { triangle.getP0(), triangle.getP1(), triangle.getP2() };
    
    SVector3d min = SVector3d.findMinValue(tab);
    SVector3d max = SVector3d.findMaxValue(tab);
    
    //Si le triangle est parall�le � l'un des 6 plans de la bo�te, la bo�te sera mal d�finie
    //Modifions par un "delta" nos coordonn�es pour �viter cette situation  
    min = new SVector3d(min.getX()*NEGATIVE_MULTIPLICATOR, min.getY()*NEGATIVE_MULTIPLICATOR, min.getZ()*NEGATIVE_MULTIPLICATOR);
    max = new SVector3d(max.getX()*POSITIVE_MULTIPLICATOR, max.getY()*POSITIVE_MULTIPLICATOR, max.getZ()*POSITIVE_MULTIPLICATOR);
    
    return new SBoundingBox(triangle, min, max);
  }
  
  /**
   * M�thode pour obtenir une bo�te englobante autour de la g�om�trie du STriangleGeometry.
   * @param triangle - La g�om�trie du triangle.
   * @return La bo�te englobante autour du triangle.
   */
  private SBoundingBox buildBoundingBoxForSBTriangleGeometry(SBTriangleGeometry triangle)
  {
    SVector3d[] tab = { triangle.getP0(), triangle.getP1(), triangle.getP2() };
    
    SVector3d min = SVector3d.findMinValue(tab);
    SVector3d max = SVector3d.findMaxValue(tab);
    
    //Si le triangle est parall�le � l'un des 6 plans de la bo�te, la bo�te sera mal d�finie
    //Modifions par un "delta" nos coordonn�es pour �viter cette situation  
    min = new SVector3d(min.getX()*NEGATIVE_MULTIPLICATOR, min.getY()*NEGATIVE_MULTIPLICATOR, min.getZ()*NEGATIVE_MULTIPLICATOR);
    max = new SVector3d(max.getX()*POSITIVE_MULTIPLICATOR, max.getY()*POSITIVE_MULTIPLICATOR, max.getZ()*POSITIVE_MULTIPLICATOR);
    
    return new SBoundingBox(triangle, min, max);
  }
  
}//fin de la classe SBoundingBoxBuilder
