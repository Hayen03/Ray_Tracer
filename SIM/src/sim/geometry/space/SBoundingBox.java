/**
 * 
 */
package sim.geometry.space;

import sim.geometry.SGeometry;
import sim.math.SVector3d;

/**
 * La classe <b>SBoundingBox</b> représente une boîte englobant une géométrie aligné sur les axes x,y et z.
 * Cette boîte contient une référence à une géométrie à l'intérieur de celle-ci.
 * 
 * <p>
 * Puisque la classe <b>SBoundingBox</b> implémente la classe <b>Comparable</b>, elle peut être
 * triée en fonction de sa taille maximale déterminée par la méthode getMaxLenght().
 * <p>
 * 
 * @author Simon Vézina
 * @since 2015-08-04
 * @version 2015-12-15
 */
public class SBoundingBox implements Comparable<SBoundingBox> {

  private final SVector3d min_point;    //la coordonnée (x,y,z) minimale de la boîte
  private final SVector3d max_point;    //la coordonnée (x,y,z) maximale de la boîte
  
  /**
   * La variable <b>geometry</b> correspond à la géométrie à l'intérieur de la boîte englobante.
   */
  private final SGeometry geometry;     
  
  /**
   * Constructeur d'une boîte englobante.
   * 
   * @param geometry - La géométrie à l'intérieur de la boîte.
   * @param min_point - Le point minimal de la boîte.
   * @param max_point - Le point maximal de la boîte.
   */
  public SBoundingBox(SGeometry geometry, SVector3d min_point, SVector3d max_point)
  {
    this.geometry = geometry;
    this.min_point = min_point;
    this.max_point = max_point;
  }

  /**
   * Méthode pour obtenir la géométrie à l'intérieur de la boîte englobante.
   * @return La géométrie à l'intérieur de la boîte englobante.
   */
  public SGeometry getGeometry()
  {
    return geometry;
  }
  
  /**
   * Méthode pour obtenir la coordonnée minimale de la boîte englobante.
   * @return Le point minimal de la boîte englobante.
   */
  public SVector3d getMinPoint()
  {
    return min_point;
  }
  
  /**
   * Méthode pour obtenir la coordonnée maximale de la boîte englobante.
   * @return Le point maximal de la boîte englobante.
   */
  public SVector3d getMaxPoint()
  {
    return max_point;
  }
  
  /**
   * Méthode pour obtenir le volume de la boîte englobante.
   * Puisque la boîte est alignée sur l'axe x,y et z, l'équation du volume V correspond à
   * <ul> V = dx * dy * dz</ul>
   * <p>Il y a cependant une mise en garde avec cette méthode. Si une géométrie est planaire (comme un triangle) et qu'elle
   * est située dans l'un des 6 plans des axes x,y et z, cette méthode retournera un volume très près de <b>zéro</b>.</p>
   * @return Le volume de la boîte englobante.
   */
  public double getVolume()
  {
    return (max_point.getX() - min_point.getX()) * (max_point.getY() - min_point.getY()) * (max_point.getZ() - min_point.getZ());
  }
  
  /**
   * Méthode pour obtenir la longueur moyenne de la boîte englobante.
   * Puisque la boîte est alignée sur l'axe x,y et z, l'équation de la longueur moyenne correspond à
   * <ul> L_moy = ( dx + dy + dz ) / 3</ul>
   * @return La longueur moyenne de la boîte.
   */
  public double getAverageLenght()
  {
    return ( (max_point.getX() - min_point.getX()) + (max_point.getY() - min_point.getY()) + (max_point.getZ() - min_point.getZ()) ) / 3;
  }
  
  /**
   * Méthode pour obtenir la dimension la plus grande de la boîte englobante.
   * Puisuqe la boîte est alignée sur l'axe x, y et z, l'équation de la dimension la plus grande correspond à
   * <ul> L_max = max { dx, dy, dz }</ul>
   * @return La dimension la plus grande de la boîte.
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
