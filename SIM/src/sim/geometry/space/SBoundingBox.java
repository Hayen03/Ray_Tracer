/**
 * 
 */
package sim.geometry.space;

import sim.geometry.SGeometry;
import sim.math.SVector3d;

/**
 * La classe <b>SBoundingBox</b> repr�sente une bo�te englobant une g�om�trie align� sur les axes x,y et z.
 * Cette bo�te contient une r�f�rence � une g�om�trie � l'int�rieur de celle-ci.
 * 
 * <p>
 * Puisque la classe <b>SBoundingBox</b> impl�mente la classe <b>Comparable</b>, elle peut �tre
 * tri�e en fonction de sa taille maximale d�termin�e par la m�thode getMaxLenght().
 * <p>
 * 
 * @author Simon V�zina
 * @since 2015-08-04
 * @version 2015-12-15
 */
public class SBoundingBox implements Comparable<SBoundingBox> {

  private final SVector3d min_point;    //la coordonn�e (x,y,z) minimale de la bo�te
  private final SVector3d max_point;    //la coordonn�e (x,y,z) maximale de la bo�te
  
  /**
   * La variable <b>geometry</b> correspond � la g�om�trie � l'int�rieur de la bo�te englobante.
   */
  private final SGeometry geometry;     
  
  /**
   * Constructeur d'une bo�te englobante.
   * 
   * @param geometry - La g�om�trie � l'int�rieur de la bo�te.
   * @param min_point - Le point minimal de la bo�te.
   * @param max_point - Le point maximal de la bo�te.
   */
  public SBoundingBox(SGeometry geometry, SVector3d min_point, SVector3d max_point)
  {
    this.geometry = geometry;
    this.min_point = min_point;
    this.max_point = max_point;
  }

  /**
   * M�thode pour obtenir la g�om�trie � l'int�rieur de la bo�te englobante.
   * @return La g�om�trie � l'int�rieur de la bo�te englobante.
   */
  public SGeometry getGeometry()
  {
    return geometry;
  }
  
  /**
   * M�thode pour obtenir la coordonn�e minimale de la bo�te englobante.
   * @return Le point minimal de la bo�te englobante.
   */
  public SVector3d getMinPoint()
  {
    return min_point;
  }
  
  /**
   * M�thode pour obtenir la coordonn�e maximale de la bo�te englobante.
   * @return Le point maximal de la bo�te englobante.
   */
  public SVector3d getMaxPoint()
  {
    return max_point;
  }
  
  /**
   * M�thode pour obtenir le volume de la bo�te englobante.
   * Puisque la bo�te est align�e sur l'axe x,y et z, l'�quation du volume V correspond �
   * <ul> V = dx * dy * dz</ul>
   * <p>Il y a cependant une mise en garde avec cette m�thode. Si une g�om�trie est planaire (comme un triangle) et qu'elle
   * est situ�e dans l'un des 6 plans des axes x,y et z, cette m�thode retournera un volume tr�s pr�s de <b>z�ro</b>.</p>
   * @return Le volume de la bo�te englobante.
   */
  public double getVolume()
  {
    return (max_point.getX() - min_point.getX()) * (max_point.getY() - min_point.getY()) * (max_point.getZ() - min_point.getZ());
  }
  
  /**
   * M�thode pour obtenir la longueur moyenne de la bo�te englobante.
   * Puisque la bo�te est align�e sur l'axe x,y et z, l'�quation de la longueur moyenne correspond �
   * <ul> L_moy = ( dx + dy + dz ) / 3</ul>
   * @return La longueur moyenne de la bo�te.
   */
  public double getAverageLenght()
  {
    return ( (max_point.getX() - min_point.getX()) + (max_point.getY() - min_point.getY()) + (max_point.getZ() - min_point.getZ()) ) / 3;
  }
  
  /**
   * M�thode pour obtenir la dimension la plus grande de la bo�te englobante.
   * Puisuqe la bo�te est align�e sur l'axe x, y et z, l'�quation de la dimension la plus grande correspond �
   * <ul> L_max = max { dx, dy, dz }</ul>
   * @return La dimension la plus grande de la bo�te.
   */
  public double getMaxLenght()
  {
    double dx = max_point.getX() - min_point.getX();
    double dy = max_point.getY() - min_point.getY();
    double dz = max_point.getZ() - min_point.getZ();
    
    double max = dx;
    
    if(max < dy)
      max = dy;
    
    if(max < dz)
      max = dz;
    
    return max;
  }

  @Override
  public int compareTo(SBoundingBox box)
  {
    double max = getMaxLenght();
    
    if(max < box.getMaxLenght())    
      return -1;
    else
      if(max > box.getMaxLenght())  
        return 1;
      else
        return 0;   
  }
  
}//fin de la classe SBoundingBox
