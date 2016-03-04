/**
 * 
 */
package sim.geometry.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.math.SVector3d;

/**
 * La classe abstraite <b>SAbstractVoxelSpace</b> représente un espace de géométrie dont le partitionnement
 * de l'espace s'effectue par voxel. Cette classe contient des méthodes utilitaires pour analyser cet espace.
 * 
 * @author Simon Vézina
 * @since 2015-12-24
 * @version 2015-12-29
 */
public abstract class SAbstractVoxelSpace extends SAbstractGeometrySpace {

  //------------
  // VARIABLE //
  //------------
  
  /**
   * La variable 'linear_list' correspond à une liste de primitive ne fait pas partie de la carte des voxels puisque ce sont des primitives <b>sans boîte englobante</b>.
   * L'algorithme d'intersection testera alors toutes les primitives de cette liste avec chaque rayon. 
   */
  protected List<SGeometry> linear_list;  
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un espace de voxel abstrait par défaut. 
   */
  public SAbstractVoxelSpace()
  {
    super();
    
    linear_list = new ArrayList<SGeometry>();
  }

  //------------
  // MÉTHODES //
  //------------
  
  /**
   * Méthode pour obtenir l'intersection la plus près entre un rayon et des géométries situées dans une carte de voxels. 
   * 
   * @param ray - Le rayon à intersecter avec les géométries.
   * @param t_max - Le temps maximal.
   * @param map - La carte des voxels.
   * @param builder - Le constructeur de voxel (pour vérifier si l'intersection est à l'intérieur du voxel).
   * @param voxel - Le voxel de la carte où sont réalisés les tests d'intersection.
   * @return Le rayon avec les caractéristiques de l'intersection (s'il y en a eu une) de temps minimum.
   */
  protected SRay nearestIntersectionInVoxelMap(SRay ray, double t_max, Map<SVoxel,List<SGeometry>> map, SVoxelBuilder builder, SVoxel voxel)
  {
    if(map != null)         // Si la carte n'est pas null
      if(!map.isEmpty())    // Si la carte n'est pas vide
      {
        // Obtenir la liste des géométries en référence dans le voxel
        List<SGeometry> list = map.get(voxel);
        
        if(list != null)          // Si le voxel est dans la carte
          if(!list.isEmpty())     // Si la liste de géométrie associée à ce voxel n'est pas vide
          {
            // Réaliser les intersections avec la liste disponible dans le voxel.
            // Cette liste sera triée en ordre croissant de temps (le plus petit temps en premier dans la liste)
            List<SRay> list_intersection = intersections(list, ray, t_max);   
                
            // Regarder la liste des intersections et prendre l'intersection au temps le plus petit,
            // mais qui se retrouve dans le voxel courant (sinon, il est rejeté).
            // Comparer celles trouvées avec le rayon déjà intersecté pour garder celle de moindre temps
            if(!list_intersection.isEmpty())
              for(SRay r : list_intersection)
                if(builder.buildVoxel(r.getIntersectionPosition()).equals(voxel))   
                  return r;
          }
      }
    
    // Il n'y a pas d'intersection adéquate qui a été réalisé, on retourne un rayon sans intersection
    return ray;
  }
    
  /**
   * Méthode pour obtenir la liste des intersections transparente en ordre décroissant dont la plus éloigné (première de la liste) sera une géométrie opaque s'il y a eu intersection de cette nature.
   * 
   * @param ray - Le rayon à intersecter.
   * @param t_max - Le temps maximal pouvant être parcouru par le rayon.
   * @param map - La carte des voxels.
   * @param builder - Le constructeur de voxel (pour vérifier si l'intersection est à l'intérieur du voxel).
   * @param voxel - Le voxel de la carte où sont réalisés les tests d'intersection.
   * @return La liste des intersections transparente en odre décroissant dont le premier élément sera une géométrie opaque s'il y a eu intersection de cette nature.
   */
  protected List<SRay> nearestOpaqueIntersectionInVoxelMap(SRay ray, double t_max, Map<SVoxel,List<SGeometry>> map, SVoxelBuilder builder, SVoxel voxel)
  {
    if(map != null)
      if(!map.isEmpty())
      {
        // Obtenir la liste des géométries en référence dans le voxel
        List<SGeometry> list = map.get(voxel);
        
        if(list != null)          
          if(!list.isEmpty())     
            return nearestOpaqueIntersectionInVoxel(list, ray, t_max, builder, voxel);  // Obtenir la liste de l'intersection opaque la plus près
      }

    // La carte n'étant vide, on retourne une liste vide
    return new ArrayList<SRay>();    
  }
  
  /**
   * <p>
   * Méthode qui évalue l'intersection la plus près entre un rayon et les différentes géométries <b>opaque</b> de l'espace ne dépassant pas un certain temps maximal.
   * Une liste des géométries transparentes sera ordonnée en <b>ordre inverse d'apparition</b> (ordre décroissant du temps).
   * La <b>liste débutera par une géométrie opaque</b> s'il y a eu une intersection avec une géométrie opaque.
   * S'il n'y a pas eu d'intersection avec une géométrie opaque, elle contiendra uniquement des géométries transparentes.
   * </p>
   * 
   * <p>
   * En comparaison avec sa version précédente, cette méthode n'acceptera uniquement les intersections situées à l'intérieur du voxel associé à la liste des géométries.
   * </p>
   *
   * @param list - La liste des géométries.
   * @param ray - Le rayon à intersecter avec les géométries de l'espace.
   * @param t_max - Le temps maximal.
   * @param builder - Le constructeur de voxel (pour vérifier si l'intersection est à l'intérieur du voxel).
   * @param voxel - Le voxel de la carte où sont réalisés les tests d'intersection.
   * @return Une liste des géométries transparentes intersectées en <b>ordre décroissant</b> débutant par une géométrie opaque s'il y a eu lieu. La liste sera <b>vide</b> s'il n'y a <b>aucune intersection</b> sur le temps maximal.
   */
  private List<SRay> nearestOpaqueIntersectionInVoxel(List<SGeometry> list, SRay ray, final double t_max, SVoxelBuilder builder, SVoxel voxel)
  {
    // Obtenir la liste complète des intersections triées en ordre croissant
    List<SRay> list_intersection = intersections(list, ray, t_max);
    
    // Iterer sur l'ensemble des intersections et conserver celle 
    // où il y a des géométries transparentes et une seule opaque s'il y en a une
    List<SRay> return_list = new ArrayList<SRay>();
    
    for(SRay r : list_intersection)
    {
      // Ajouter et traiter l'intersection uniquement si elle se retrouve dans le voxel
      if(builder.buildVoxel(r.getIntersectionPosition()).equals(voxel))
      {
        return_list.add(r);   // ajouter cette intersection
        
        // Si la géométrie intersecté n'est pas transparente,
        // ce sera la dernière intersection ajoutée à la liste
        // car elle sera la seule intersection opaque de la liste.
        if(!r.getGeometry().isTransparent())
        {
          Collections.reverse(return_list);     // Inverser l'ordre en ordre décroissant
          return return_list;                   // retourner une liste avec géométrie opaque
        }
      }
    }
    
    Collections.reverse(return_list);           // Inverser l'ordre en ordre décroissant
    return return_list;                         // retourner une liste sans géométrie opaque
  }
  
  /**
   * Méthode qui évalue l'ensemble des géométries d'une carte de voxel où une vecteur v se retrouve à l'intérieur.
   * 
   * @param map - La carte des voxels.
   * @param builder - Le constructeur de voxel attitré à la carte de voxel.
   * @param v - La position du vecteur.
   * @return La liste des géométries où le vecteur v se retrouve à l'intérieur.
   */
  protected List<SGeometry> listInsideGeometryInMap(Map<SVoxel,List<SGeometry>> map, SVoxelBuilder builder, SVector3d v)
  {
    // Obtenir la liste des géométries dans la cartes des voxel 
    // et obtenir les géométries où le vecteur v s'y retrouve
    if(map != null)        
      if(!map.isEmpty())    
        return listInsideGeometry(map.get(builder.buildVoxel(v)), v);
        
    // La carte étant null ou vide, nous retournons une liste vide.
    return new ArrayList<SGeometry>();
  }
  
  /**
   * Méthode pour ajouter des géométries à la carte de voxel avec les voxels où la géométrie est située.
   * 
   * @param map - La carte des voxels contenant une liste de géométrie.
   * @param extremum_voxel - Le voxel extremum associé à la carte de voxels.
   * @param geometry - La géométrie à ajouter.
   * @param list - La liste des voxels où la géométrie est située.
   */
  protected SVoxel addGeometryToMap(Map<SVoxel, List<SGeometry>> map, SVoxel extremum_voxel, SGeometry geometry, List<SVoxel> list)
  {
    // Iterer sur la liste des voxels
    for(SVoxel v : list)
    {  
      // Si le voxel est déjà dans la carte : ajouter la géométrie à la liste existante de géométrie de ce voxel
      if(map.containsKey(v))            
        map.get(v).add(geometry);
      else                                    
      {
        // Si le voxel n'est pas dans la carte : créer une liste avec la géométrie dedans. 
        // Ajouter le voxel comme clé de recherche dans la carte.
        // Il faudra ajuster le voxel d'extrême également.
        List<SGeometry> l = new ArrayList<SGeometry>();
        
        // Ajouter la géométrie à la liste
        l.add(geometry);                                       
        
        // Ajouter le voxel à la carte
        map.put(v, l);                                           
        
        // Mise à jour du voxel d'extremum (si nécessaire)
        extremum_voxel = updateExtremumVoxel(extremum_voxel, v);   
      }
    }
    
    return extremum_voxel;
  }
  
  /**
   * Méthode pour faire la mise à jour du voxel définissant les extermums en valeur absolue d'une carte de voxels.
   * 
   * @param previous_extremum - Le voxel étant celui définissant l'ancien extremum.
   * @param new_voxel - Le nouveau voxel pouvant redéfinir le voxel extremum.
   * @return Un nouveau voxel définissant un nouvel extremum ou celui passé en paramètre s'il demeure extremum.
   */
  protected SVoxel updateExtremumVoxel(SVoxel previous_extremum, SVoxel new_voxel)
  {
    // Voxel de retour étant l'ancien par défaut (s'il n'est pas changé)
    SVoxel return_voxel = previous_extremum;
    
    //Mise à jour si nécessaire de la coordonnée extermum en x
    if(Math.abs(new_voxel.getX()) > return_voxel.getX())
      return_voxel = new SVoxel(Math.abs(new_voxel.getX()), return_voxel.getY(), return_voxel.getZ());
    
    //Mise à jour si nécessaire de la coordonnée extermum en y
    if(Math.abs(new_voxel.getY()) > return_voxel.getY())
      return_voxel = new SVoxel(return_voxel.getX(), Math.abs(new_voxel.getY()), return_voxel.getZ());
    
    //Mise à jour si nécessaire de la coordonnée extermum en z
    if(Math.abs(new_voxel.getZ()) > return_voxel.getZ())
      return_voxel = new SVoxel(return_voxel.getX(), return_voxel.getY(), Math.abs(new_voxel.getZ()));
    
    return return_voxel;
  }
  
  /**
   * Méthode pour obtenir le nombre de géométries en référence dans les voxels.
   * 
   * @param map - La carte des voxels.
   * @return Le nombre de primitives dans les voxels de la carte.
   */
  protected int evaluateNbGeometryReference(Map<SVoxel, List<SGeometry>> map)
  {
    int count = 0;
    
    // Iterer sur l'ensemble des valeurs de la carte
    for(List<SGeometry> l : map.values())
      count += l.size();
    
    return count;
  }
  
  /**
   * Méthode pour obtenir le nombre de géométries en référence en moyenne par voxel.
   * 
   * @param map - La carte des voxels.
   * @return Le nombre moyen de primitives par voxel.
   */
  protected double evaluateNbGeometryReferencePerVoxel(Map<SVoxel, List<SGeometry>> map)
  {
    // Valeur statistique étant un calcul de moyenne
    return (double) evaluateNbGeometryReference(map) / (double) map.keySet().size();
  }
  
}//fin de la classe SAbstractVoxelSpace
