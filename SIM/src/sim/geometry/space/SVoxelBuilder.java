/**
 * 
 */
package sim.geometry.space;

import java.util.ArrayList;
import java.util.List;

import sim.math.SVector3d;

/**
 * <p>Classe représentant un constructeur de voxel. L'objectif sera de faire correspondre une coordonnée espace
 * monde à une coordonnée espace voxel. Pour ce faire, il faudra spécifier <b>1 unité voxel = x unité monde</b>.</p> 
 * <p>Prenant en argument un vecteur ou une boîte englobante,
 * le constructeur de voxel va convertir en espace voxel les coordonnée (x,y,z) des arguments. Dans le cas d'une boîte,
 * il est possible que la boîte nécessite plusieurs voxels en raison de la grande taille de la boîte
 * ou de la petite taille espace du voxel.</p>
 * 
 * @author Simon Vézina
 * @since 2015-08-04
 * @version 2015-08-04
 */
public class SVoxelBuilder {

  private final double dimension;   //la dimension espace d'un unité voxel ("1" unité voxel = "dimension" unité monde)
 
  /**
   * Constructeur d'un constructeur de voxel.
   * @param dimension - La dimension d'un voxel en unité monde.
   */
  public SVoxelBuilder(double dimension)
  {
    this.dimension = dimension;
  }

  /**
   * Méthode pour obtenir la dimension espace d'un voxel ("1" unité voxel = "dimension" unité monde).
   * @return La dimension d'un voxel en espace monde.
   */
  public double getDimension()
  {
    return dimension;
  }
  
  /**
   * Méthode pour construire un voxel dans lequel un vecteur 3d est situé à l'intérieur.
   * @param v - Le vecteur.
   * @return Le voxel où est situé le vecteur 3d.
   */
  public SVoxel buildVoxel(SVector3d v)
  {
    int x_voxel = (int)Math.floor(v.getX()/dimension);
    int y_voxel = (int)Math.floor(v.getY()/dimension);
    int z_voxel = (int)Math.floor(v.getZ()/dimension);
    
    return new SVoxel(x_voxel, y_voxel, z_voxel);
  }
  
  /**
   * Méthode pour construire une liste de voxel dans lequel une boîte englobant est située à l'intérieur.
   * Si la dimension du voxel est grande et que la boîte est petite, la liste peut contenir qu'un seul voxel.
   * @param box - La boîte.
   * @return La liste de voxels où est situé la boîte.
   */
  public List<SVoxel> buildVoxel(SBoundingBox box)
  {
    List<SVoxel> list = new ArrayList<SVoxel>();
    
    SVoxel min = buildVoxel(box.getMinPoint());
    SVoxel max = buildVoxel(box.getMaxPoint());
    
    if(min.equals(max))   //la boîte se retrouve dans un seul voxel
      list.add(min);
    else                  //la boîte se retouve dans plusieurs voxels
      for(int i = min.getX(); i <= max.getX(); i++)
        for(int j = min.getY(); j <= max.getY(); j++)
          for(int k = min.getZ(); k <= max.getZ(); k++)
            list.add(new SVoxel(i, j, k));
    
    return list;
  }
  
}//fin de la classe SVoxelBuilder
