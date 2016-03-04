/**
 * 
 */
package sim.geometry.space;

import sim.geometry.SRay;
import sim.math.SVector3d;

/**
 * <p>
 * La classe <b>SFastTraversalVoxelAlgorithm</b> représente un algorithme permettant de traverser 
 * un grillage de voxel en ligne droite en paramétrisant le parcours avec le temps. 
 * </p>
 * 
 * <p>
 * Cette implémentation est inspirée d'un article de <i>John Amanatides</i> and <i>Andrew Woo</i>
 * du département de Computer Science de l'université de Toronto.
 * </p>
 * 
 * <p>
 * http://www.cse.chalmers.se/edu/year/2011/course/TDA361_Computer_Graphics/grid.pdf
 * </p>
 * 
 * @author Simon Vézina
 * @since 2015-08-05
 * @version 2016-01-08
 */
public class SFastTraversalVoxelAlgorithm {

  //-------------
  // VARIABLES //
  //-------------
  private final double t_end_line;            //temps associé au parcours complet de la ligne (correspondant à la distance maximale)
  private final SVoxel extremum_voxel;        //voxel extremum (en valeur absolue) admissible
  
  private final int stepX;                    //direction du changement de voxel selon l'axe x (-1 ou +1)
  private final int stepY;                    //direction du changement de voxel selon l'axe x (-1 ou +1)
  private final int stepZ;                    //direction du changement de voxel selon l'axe x (-1 ou +1)
 
  private final double t_delta_x;             //temps pour un rayon de traverser l'axe x d'un voxel
  private final double t_delta_y;             //temps pour un rayon de traverser l'axe y d'un voxel
  private final double t_delta_z;             //temps pour un rayon de traverser l'axe z d'un voxel
  
  private SVoxel current_voxel;               //voxel courant en analyse
    
  private double t_max_x;                     //temps si un déplacement s'effectue selon l'axe x
  private double t_max_y;                     //temps si un déplacement s'effectue selon l'axe y
  private double t_max_z;                     //temps si un déplacement s'effectue selon l'axe z
  
  /**
   * La variable <b>max_t</b> correspond au temps pour <b>sortir</b> du voxel <b>current_voxel</b>.
   */
  private double max_t;
  
  /**
   * La variable <b>min_t</b> correspond au temps pour <b>entrer</b> dans le voxel <b>current_voxel</b>.
   */
  private double min_t;
  
  /**
   * La variable <b>out</b> détermine si l'on est à la fin de la droite de voxel ou à l'extérieur des extremums.
   */
  boolean out;                                
    
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un parcoureur linéaire de voxel. 
   * 
   * @param dimension - La dimension des voxels ("1" unité voxel = "dimension" unité monde).
   * @param ray - Le rayon voyageant dans l'esapce des voxels.
   * @param t_max - Le temps maximal que le rayon est autorisé à voyager.
   * @param extremum - Le voxel extremum (en valeur absolue) admissible dans l'itération de long de la droite de voxel.
   */
  public SFastTraversalVoxelAlgorithm(final double dimension, final SRay ray, final double t_max, final SVoxel extremum)
  {
    t_end_line = t_max;
    extremum_voxel = extremum;
    
    out = false;
    
    SVector3d ray_direction = ray.getDirection();
    
    //Évaluer la direction du changement de voxel selon l'axe x
    if(ray_direction.getX() > 0)    
      stepX = 1;                    //déplacement dans le sens positif de l'axe x
    else
      stepX = -1;                   //déplacement dans le sens négatif de l'axe x
    
    //Évaluer la direction du changement de voxel selon l'axe y
    if(ray_direction.getY() > 0)    
      stepY = 1;                    //déplacement dans le sens positif de l'axe y
    else
      stepY = -1;                   //déplacement dans le sens négatif de l'axe y
    
    //Évaluer la direction du changement de voxel selon l'axe z
    if(ray_direction.getZ() > 0)    
      stepZ = 1;                    //déplacement dans le sens positif de l'axe z
    else
      stepZ = -1;                   //déplacement dans le sens négatif de l'axe z
    
    //Évaluer la temps requis pour traverser une largeur complète selon l'axe x,y et z d'un voxel
    t_delta_x = Math.abs(dimension/ray_direction.getX());
    t_delta_y = Math.abs(dimension/ray_direction.getY());
    t_delta_z = Math.abs(dimension/ray_direction.getZ());
    
    //Évaluer le temps requis pour traverser le 1ier voxel selon l'axe x,y et z
    SVector3d ray_origin = ray.getOrigin();
    
    current_voxel = new SVoxelBuilder(dimension).buildVoxel(ray_origin);          //le 1ier voxel
  
    if(stepX == 1)  //déplacement en x positif
    {
      double distance = (current_voxel.getX()+1)*dimension - ray_origin.getX();   // distance toujours positive
      t_max_x = distance/ray_direction.getX();                                    // vitesse positive, donc t_max_x positif
    }
    else            //déplacement en x négatif
    {
      double distance = current_voxel.getX()*dimension - ray_origin.getX();       // distance toujours négative
      t_max_x = distance/ray_direction.getX();                                    // vitesse négative, donc t_max_x positif
    }
    
    if(stepY == 1)  //déplacement en y positif
    {
      double distance = (current_voxel.getY()+1)*dimension - ray_origin.getY();
      t_max_y = distance/ray_direction.getY();
    }
    else            //déplacement en  y négatif
    {
      double distance = current_voxel.getY()*dimension - ray_origin.getY();
      t_max_y = distance/ray_direction.getY();
    }
    
    if(stepZ == 1)  //déplacement en z positif
    {
      double distance = (current_voxel.getZ()+1)*dimension - ray_origin.getZ();
      t_max_z = distance/ray_direction.getZ();
    }
    else            //déplacement en z négatif
    {
      double distance = current_voxel.getZ()*dimension - ray_origin.getZ();
      t_max_z = distance/ray_direction.getZ();
    }
    
    // Évaluer le temps min_t 
    min_t = 0.0;
    
    // Évaluer le temps max_t
    max_t = evaluateNextMaxT();
    
  }

  //------------
  // MÉTHODES //
  //------------
  
  /**
   * Méthode pour mettre fin à l'itération de la ligne de voxel.
   */
  public void close()
  {
    out = true;
  }
  
  /**
   * Méthode pour déterminer s'il reste dans l'itération des voxels le long de la droite de voxels.
   * 
   * @return <b>true</b> s'il reste des voxels dans l'itération et <b>false</b> sinon.
   */
  public boolean asNextVoxel()
  {
    return !out;
  }
  
  /**
   * Méthode pour obtenir le prochain voxel de l'itération le long de la droite de voxels.
   * Le premier appel correspond à obtenir le voxel depuis l'endroit où le rayon est lancé.
   * 
   * @return Le prochain voxel.
   */
  public SVoxel nextVoxel()
  {
    if(out)
      return null;
    else
    {
      SVoxel voxel_to_return = current_voxel;   //le voxel courant à retourner
          
      // Mise à jour du voxel courant 
      current_voxel = evaluateNextVoxel();      //le prochain voxel à retourner (au prochain appel de la méthode nextVoxel())
      
      // Mise à jour du prochain temps d'entré et de sortie du prochain voxel  
      min_t = max_t;
      max_t = evaluateNextMaxT();
      
      return voxel_to_return;
    }
  }
  
  /**
   * Méthode pour obtenir le temps de parcours afin de <b>sortir</b> du prochain voxel généré par ce générateur de voxel.
   *
   * @return Le temps de parcours pour sortir du le prochain voxel.
   */
  public double nextMaxTime()
  {
    return max_t;
  }
  
  /**
   * Méthode pour obtenir le temps de parcours afin <b>d'entrer</b> dans le prochain voxel généré par ce générateur de voxel.
   * Cette valeur sera égale à 0 lors d'un appel avant l'appel de la méthode nextVoxel().
   * 
   * @return Le temps de parcours pour entrer dans le prochain voxel.
   */
  public double nextMinTime()
  {
    return min_t;
  }
  
  /**
   * Méthode pour évaluer le prochaine voxel dans l'itération le long de la droite de voxels.
   * 
   * @return Le prochain voxel de l'itération. Si la fin de la droite est atteinte (t > t_end_line), la méthode retourne <b>null</b>.
   */
  private SVoxel evaluateNextVoxel()
  {
    if(t_max_x < t_max_y)
      if(t_max_x < t_max_z)     //temps en x plus petit qu'en y et z, donc déplacement en x
      {
        //- Vérifier si le temps selon l'axe x dépasse déjà le temps maximal.
        //- Vérifier si le voxel courant en x est situé à l'extérieur de l'extremum. 
        // (P.S. en raison d'un sens positif ou négatif de déplacement, j'utilise ... current_voxel.getX()*stepX ... pour toujours avoir un signe positif à comparer !)  
        
        //Pour une des deux raisons, le parcours de la droite est terminé.
        if(t_max_x > t_end_line || current_voxel.getX()*stepX > extremum_voxel.getX())
        {
          out = true;
          return null;
        }
        else
        {
          //puisque nous avons au moins traversé le 1er voxel en x, 
          //le prochain a traverser se fera sur la largeur complète du voxel
          t_max_x = t_max_x + t_delta_x;  
                    
          return new SVoxel(current_voxel.getX() + stepX, current_voxel.getY(), current_voxel.getZ());
        }
      }
      else                     //temps en z plus petit qu'en x et y, donc déplacement en z
      {
        if(t_max_z > t_end_line || current_voxel.getZ()*stepZ > extremum_voxel.getZ())
        {
          out = true;
          return null;
        }
        else
        {
          t_max_z = t_max_z + t_delta_z;
                   
          return new SVoxel(current_voxel.getX(), current_voxel.getY(), current_voxel.getZ() + stepZ);
        }
    }
    else
      if(t_max_y < t_max_z)   //temps en y plus petit qu'en x et z, donc déplacement en y
      {
        if(t_max_y > t_end_line || current_voxel.getY()*stepY > extremum_voxel.getY())
        {
          out = true;
          return null;
        }
        else
        {
          t_max_y = t_max_y + t_delta_y;
                   
          return new SVoxel(current_voxel.getX(), current_voxel.getY() + stepY, current_voxel.getZ());
        }
      }
      else                    //temps en z plus petit qu'en x et y, donc déplacement en z
      {
        if(t_max_z > t_end_line || current_voxel.getZ()*stepZ > extremum_voxel.getZ())
        {
          out = true;
          return null;
        }
        else
        {
          t_max_z = t_max_z + t_delta_z;
                    
          return new SVoxel(current_voxel.getX(), current_voxel.getY(), current_voxel.getZ() + stepZ);
        }
      }
  }
  
  /**
   * Méthode pour évaluer le prochain temps de sortie du prochain voxel.
   * 
   * @return Le temps de sortie du prochain voxel.
   */
  private double evaluateNextMaxT()
  {
    // Il faut trouver le plus petit nombre parmis t_max_x, t_max_y et t_max_z
    if(t_max_x < t_max_y)
      if(t_max_x < t_max_z)
        return t_max_x;
      else
        return t_max_z;
    else
      if(t_max_y < t_max_z)
        return t_max_y;
      else
        return t_max_z;
  }
  
}//fin de la classe SFastTraversalVoxelAlgorithm
