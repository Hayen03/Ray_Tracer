/**
 * 
 */
package sim.geometry.space;

/**
 * Classe qui repréente un voxel. Un voxel est un élément d'une grille régulière à 3 dimensions 
 * comparable à un pixel étant à 2 dimensions.
 * 
 * @author Simon Vézina
 * @since 2015-08-04
 * @version 2015-08-04
 */
public class SVoxel {

  private final int x;    //coordonnée x du voxel
  private final int y;    //coordonnée x du voxel
  private final int z;    //coordonnée x du voxel
  
  /**
   * Constructeur d'un voxel.
   * 
   * @param x - La coordonnée x du voxel.
   * @param y - La coordonnée y du voxel.
   * @param z - La coordonnée z du voxel.
   */
  public SVoxel(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Méthode pour obtenir la coordonnée x du voxel.
   * @return La coordonnée x du voxel.
   */
  public int getX()
  {
    return x;
  }
  
  /**
   * Méthode pour obtenir la coordonnée y du voxel.
   * @return La coordonnée y du voxel.
   */
  public int getY()
  {
    return y;
  }
  
  /**
   * Méthode pour obtenir la coordonnée z du voxel.
   * @return La coordonnée z du voxel.
   */
  public int getZ()
  {
    return z;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
    return result;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    
    if (obj == null) {
      return false;
    }
    
    if (!(obj instanceof SVoxel)) {
      return false;
    }
    
    SVoxel other = (SVoxel) obj;
    
    if (x != other.x) {
      return false;
    }
    
    if (y != other.y) {
      return false;
    }
    
    if (z != other.z) {
      return false;
    }
    
    return true;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "SVoxel [x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  
}//fin de la classe SVoxel
