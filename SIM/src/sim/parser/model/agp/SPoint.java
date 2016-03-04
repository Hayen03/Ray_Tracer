/**
 * 
 */
package sim.parser.model.agp;

/**
 * Classe représentant un point 3d d'un modèle 3d de format "agp" (modèle 3d du <i>projet Anaglyphe</i> réalisé par Anik Soulière).
 * 
 * @author Simon Vézina
 * @since 2015-08-01
 * @version 2015-08-01
 */
public class SPoint {

  //Les coordonnées par défaut
  private static final double DEFAULT_X = 0.0;
  private static final double DEFAULT_Y = 0.0;
  private static final double DEFAULT_Z = 0.0;
  
  //Les trois coordonnées du point 3d
  private double x;
  private double y;
  private double z;
  
  /**
   *  Constructeur d'une point par défaut. 
   */
  public SPoint()
  {
    x = DEFAULT_X;
    y = DEFAULT_Y;
    z = DEFAULT_Z;
  }

  /**
   * Méthode pour obtenir la coordonnée <i>x</i> du point.
   * @return la coordonnée <i>x</i>.
   */
  public double getX()
  {
    return x;
  }

  /**
   * Méthode pour obtenir la coordonnée <i>y</i> du point.
   * @return la coordonnée <i>y</i>.
   */
  public double getY()
  {
    return y;
  }

  /**
   * Méthode pour obtenir la coordonnée <i>z</i> du point.
   * @return la coordonnée <i>z</i>.
   */
  public double getZ()
  {
    return z;
  }

  /**
   * Méthode pour définir la coordonnée <i>x</i> du point.
   * @param x - La nouvelle coordonnée <i>x</i> du point.
   */
  public void setX(double x)
  {
    this.x = x;
  }

  /**
   * Méthode pour définir la coordonnée <i>y</i> du point.
   * @param y - La nouvelle coordonnée <i>y</i> du point.
   */
  public void setY(double y)
  {
    this.y = y;
  }

  /**
   * Méthode pour définir la coordonnée <i>z</i> du point.
   * @param z - La nouvelle coordonnée <i>z</i> du point.
   */
  public void setZ(double z)
  {
    this.z = z;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "SPoint [x=" + x + ", y=" + y + ", z=" + z + "]";
  }
  
}//fin de la classe SPoint
