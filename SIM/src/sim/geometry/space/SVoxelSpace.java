/**
 * 
 */
package sim.geometry.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sim.exception.SRuntimeException;
import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.math.SVector3d;
import sim.util.SLog;

/**
 * <p>
 * La classe SVoxelSpace représentant un espace de géométries distribuées dans un espace de voxels (en trois dimensions).
 * Cette organisation permettra d'effecter l'intersection d'un rayon avec un nombre plus limités de géométries ce qui va accélérer les calculs.
 * </p> 
 * 
 * <p>
 * Distribuée dans une grille régulière de voxel, chaque géométrie sera localisée dans un ou plusieurs voxels 
 * et le lancer d'un rayon parcourera un nombre limité de voxels ce qui ainsi limitera le nombre de tests d'intersection.
 * </p>
 * 
 * @author Simon Vézina
 * @since 2015-08-04
 * @version 2015-12-29
 */
public class SVoxelSpace extends SAbstractVoxelSpace {

  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>voxel_map</b> correspond à la carte des voxels où sont situées des géométries admettant une boîte englobante.
   */
  private final Map<SVoxel, List<SGeometry>> voxel_map;  
  
  /**
   * La variable <b>voxel_builder</b> correspond au constructeur de voxel. 
   * La taille des voxels sera déterminée par un objet de type SVoxelDimensionEvaluator. 
   */
  private SVoxelBuilder voxel_builder;                   
  
  /**
   * La variable <b>absolute_extremum_voxel</b> correspond à un voxel de coordonnée extremums en valeur absolue à celle contenue dans la carte de voxel.
   * Ceci permet de connaître la taille maximal de la carte des voxels.
   */
  private SVoxel absolute_extremum_voxel;               
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un espace de voxel par défaut. 
   */
  public SVoxelSpace()
  {
    super();
    
    voxel_map = new HashMap<SVoxel, List<SGeometry>>();
    voxel_builder = null;
    absolute_extremum_voxel = null;
  }

  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public SRay nearestIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // Vérifier si le rayon a déjà intersecté une géométrie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SVoxelSpace 003 : Le rayon en paramètre a déjà intersecté une géométrie.");
    
    // Vérifier la valeur de t_max est adéquate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SVoxelSpace 004 : Le temps maximale ne peut pas être négative.");
   
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 005 : L'espace de géométries de voxel n'a pas été initialisé.");
    
    //Résultat de l'intersection avec la carte de voxel. Sera égale à "ray" s'il y en a pas eu.
    SRay intersection_in_voxel = nearestIntersectionInVoxelMap(ray, t_max);
    
    //Résultat de l'intersection avec les géométries hors voxel
    List<SRay> list_intersection_not_in_voxel = intersections(linear_list, ray, t_max);
    
    // Ajouter l'intersection de la carte de voxel à la liste linéaire.
    // Cette liste sera ainsi pas vide.
    list_intersection_not_in_voxel.add(intersection_in_voxel);
    
    // Trier la liste pour le dernier élément ajouté.
    Collections.sort(list_intersection_not_in_voxel); 
    
    // Retourner le permier élément de la liste (sera sans intersection s'il n'y en a pas eu).
    return list_intersection_not_in_voxel.get(0);
    
    /*
    //Identification de l'intersection la plus près (de plus petite valeur de ray.getT())
    if(list_intersection_not_in_voxel.isEmpty())                                            
      return intersection_in_voxel;                                                       //intersection seulement dans la carte OU aucune intersection
    else
      if(!intersection_in_voxel.asIntersected())
        return list_intersection_not_in_voxel.get(0);                                     //intersection seulement dans la liste
      else
        if(list_intersection_not_in_voxel.get(0).getT() < intersection_in_voxel.getT())
          return list_intersection_not_in_voxel.get(0);                                   //intersection plus rapide dans la liste
        else
          return intersection_in_voxel;                                                   //intersection plus rapide dans la carte
     */
  }

  /**
   * Méthode pour obtenir l'intersection la plus près entre un rayon et des géométries situées dans la carte de voxel.
   * @param ray - Le rayon à intersecter avec les géométries.
   * @param t_max - Le temps maximal.
   * @return Le rayon avec les caractéristiques de l'intersection (s'il y en a eu une).
   */
  private SRay nearestIntersectionInVoxelMap(SRay ray, double t_max)
  {
    //Réaliser des calculs d'intersection avec les géométries de la carte uniquement si elle n'est pas vide
    if(!voxel_map.isEmpty())
    {
      //Créer la ligne de voxel à parcourir un à un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      //Faire l'itération sur la ligne de voxel depuis l'origine du rayon
      while(line_of_voxel.asNextVoxel())
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        // Faire le test de l'intersection des géométries situées dans le voxel en cours
        SRay ray_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map, voxel_builder, voxel);
        
        // Retourner le résultat de l'intersection s'il y en a une, car elle sera nécessairement de plus court temps
        if(ray_intersection.asIntersected())
          return ray_intersection;
      }
    }
    
    // Aucune intersection valide n'a été trouvée.
    return ray;
  }
  
  @Override
  public List<SRay> nearestOpaqueIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // Vérifier si le rayon a déjà intersecté une géométrie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SVoxelSpace 006 : Le rayon en paramètre a déjà intersecté une géométrie.");
    
    // Vérifier la valeur de t_max est adéquate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SVoxelSpace 007 : Le temps maximale ne peut pas être négative.");
   
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 008 : L'espace de géométries de voxel n'a pas été initialisé.");
   
    // La liste déterminée dans la carte des géométries
    List<SRay> list_in_voxel = nearestOpaqueIntersectionInVoxelMap(ray, t_max);
    
    // La liste déterminée dans la liste linéaire des géométries
    List<SRay> list_not_in_voxel = nearestOpaqueIntersection(linear_list, ray, t_max);
    
    // La liste fusionnée adéquatement
    return mergeNearestOpaqueIntersection(list_in_voxel, list_not_in_voxel);
    
    /*
    int nb_opaque_intersection = 0;   //débutons avec l'hypothèse qu'il n'y a pas d'intersection opaque
    
    List<SRay> result_list = nearestOpaqueIntersectionInVoxelMap(ray, t_max);                 //la liste résultante avec voxel
    
    //Vérifier s'il y a une intersection opaque dans la liste avec voxel
    if(!result_list.isEmpty())
      if(!result_list.get(0).getGeometry().isTransparent())
        nb_opaque_intersection++;
    
    List<SRay> result_list_not_in_voxel = nearestOpaqueIntersection(linear_list, ray, t_max); //la liste résultante avec voxel
    
    //Vérifier s'il y a une intersection opaque dans la liste sans voxel
    if(!result_list_not_in_voxel.isEmpty())
      if(!result_list_not_in_voxel.get(0).getGeometry().isTransparent())
        nb_opaque_intersection++;
    
    //Fusionner les listes
    result_list.addAll(result_list_not_in_voxel);   
    
    //Retourner la liste vide si c'est le cas, car il n'y a plus rien à faire
    if(result_list.isEmpty())
      return result_list;
    
    //Trier le tout selon la même stratégie
    Collections.sort(result_list);        //Trier dans l'ordre croissant
    Collections.reverse(result_list);     //Inverser l'ordre en ordre décroissant
    
    //Vérifier l'état de la liste et retirer des éléments au besoin
    if(nb_opaque_intersection < 1)
      return result_list;                 //sans intersection opaque, la liste est complète
    else
    {
      int found = 0;                                          //nombre d'intersection opaque trouvée dans la liste
      
      //Nous avons la garantie ici de trouvé au moins une géométrie opaque
      //Itérer tant qu'on a pas trouvé toutes les géométries opaques
      while(found != nb_opaque_intersection)                  
      {    
        if(result_list.get(0).getGeometry().isTransparent())  
          result_list.remove(0);                              //retirer le 1ier élément de la liste si elle est transparente
        else
        {
          found++;                                            //nous en avons trouvé 1 de plus
          
          if(found < nb_opaque_intersection)                  
            result_list.remove(0);                            //retirer la géomtrie opaque s'il y en a d'autres dans la liste
        }
      }
      
      return result_list;   //la liste commence par une géométrie opaque suivit de géométrie transparente
    }
    */
    
  }

  /**
   * Méthode pour obtenir la liste des intersections transparente en ordre décroissant dont la plus éloigné (première de la liste) sera une géométrie opaque s'il y a eu intersection de cette nature.
   * 
   * @param ray - Le rayon à intersecter.
   * @param t_max - Le temps maximal pouvant être parcouru par le rayon.
   * @return La liste des intersections transparente en odre décroissant dont le premier élément sera une géométrie opaque s'il y a eu intersection de cette nature.
   */
  private List<SRay> nearestOpaqueIntersectionInVoxelMap(SRay ray, double t_max)
  {
    List<SRay> return_list = new ArrayList<SRay>();
    
    if(!voxel_map.isEmpty())
    {
      //Créer la ligne de voxel à parcourir un à un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      while(line_of_voxel.asNextVoxel())
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        // Obtenir la liste de l'intersection opaque associé au voxel courant
        List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map, voxel_builder, voxel);
        
        // Ajouter cette liste à la liste à retourner
        return_list = mergeNearestOpaqueIntersection(return_list, list);
        
        // Retourner cette liste si l'intersection opaque a déjà été trouvée.
        if(!return_list.isEmpty())
          if(!return_list.get(0).getGeometry().isTransparent())
            return return_list;
      }
    }
    
    // La liste est vide ou elle contient uniquement des géométries transparentes
    return return_list;
    
    /*
    SRay nearest_opaque_intersection = ray;                             //l'intersection opaque la plus près ... initialement, il n'y en a pas !
    
    List<SRay> list_transparent_intersection = new LinkedList<SRay>();  //liste des intersections avec géométrie transparente
    
    //Réaliser des calculs d'intersection avec les géométries de la carte uniquement si elle n'est pas vide
    if(!voxel_map.isEmpty())
    {
      //Créer la ligne de voxel à parcourir un à un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      //Faire l'itération sur la ligne de voxel depuis l'origine du rayon
      boolean opaque_intersection_not_found = true;
      
      while(line_of_voxel.asNextVoxel() && opaque_intersection_not_found)
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        List<SGeometry> list = voxel_map.get(voxel);
        
        if(list != null)          //si le voxel est dans la carte
          if(!list.isEmpty())     //si la liste de géométrie associé à ce voxel n'est pas vide
          {
            List<SRay> list_intersection = intersections(list, ray, t_max);   //réaliser les intersections avec la liste
            
            //- Regarder la liste des intersections 
            //- Conserver les intersection avec géométrie transparente, mais qui se retrouve dans le voxel courant
            //- Conserver l'intersection avec la géométrie opaque la plus près
            if(!list_intersection.isEmpty())
              for(SRay r : list_intersection)
                if(voxel_builder.buildVoxel(r.getIntersectionPosition()).equals(voxel))   //si le voxel où est réalisé l'intersection se trouve à être le voxel courant
                  if(r.getGeometry().isTransparent())
                    list_transparent_intersection.add(r);                 //Ajouter la géométrie transparente à la liste des transparentes
                  else
                    if(r.getT() < nearest_opaque_intersection.getT())     
                    {  
                      nearest_opaque_intersection = r;                    //Remplacer l'ancienne géométrie opaque si la nouvelle est plus près 
                      opaque_intersection_not_found = true;
                    }
          }
      }//fin while
    }
    
    //Construire la liste officielle des intersections transparentes en gardant  
    //uniquement les intersections transparentes avant celle qui est opaque et  
    //mettre l'intersection opaque dans la liste s'il y en a eu une.
    //IMPORTANT : Il faut trier la liste en ordre décroissant par la suite (celle opaque sera alors la première).
    if(nearest_opaque_intersection.asIntersected())       //S'il y a pas eu d'intersection opaque
    { 
      List<SRay> list_return = new LinkedList<SRay>();    //Nouvelle liste à retourner
      
      for(SRay r : list_transparent_intersection)
        if(r.getT() < nearest_opaque_intersection.getT()) //Ajouter les géométries transparente plus près que l'intersection opaque
          list_return.add(r);
      
      list_return.add(nearest_opaque_intersection);       //Ajouter l'intersection opaque
      
      Collections.sort(list_return);                      //Trier dans l'ordre croissant
      Collections.reverse(list_return);                   //Inverser l'ordre en ordre décroissant (celle opaque sera la première de la liste)
      return list_return;
    }
    else
    {
      Collections.sort(list_transparent_intersection);    //Trier dans l'ordre croissant
      Collections.reverse(list_transparent_intersection); //Inverser l'ordre en ordre décroissant (ici, il n'y a pas de géométrie opaque)
      return list_transparent_intersection;
    }
    */
  }
  
  @Override
  public List<SGeometry> listInsideGeometry(SVector3d v)
  {
    // Vérifier que l'initialisation a été complétée
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 009 : L'espace de voxel n'a pas été initialisé.");
    
    // Liste des géométries où le vecteur v sera situé à l'intérieur.
    // Débutons avec la liste disponible à partir des informations de la carte des voxels.
    List<SGeometry> inside_list = listInsideGeometryInMap(voxel_map, voxel_builder, v);
    
    // Ajouter les géométries sans boîte où le vecteur v s'y retrouve.
    inside_list.addAll(listInsideGeometry(linear_list, v));
        
    return inside_list;
  }

  @Override
  public void initialize()
  {
    SLog.logWriteLine("Message SVoxelSpace : Construction de l'espace des géométries avec voxel.");
    
    // Générateur de boîte englobante
    SBoundingBoxBuilder box_builder = new SBoundingBoxBuilder();            
    
    // Séparateur de la collection de géométrie
    SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_BOX_AND_NO_BOX);
    
    // Obtenir la liste des géométries sans boîte englobante et l'affecter à la liste linéaire
    linear_list = splitter.getNoBoxList();
    
    // Obtenir la liste des géométries avec boîte englobante
    List<SGeometry> list_with_box = splitter.getSplitList().get(0);
    
    // Liste des boîtes englobantes autour des géométrie de la liste
    List<SBoundingBox> bounding_box_list = new ArrayList<SBoundingBox>();   
    
    // Obtenir toutes les boîtes englobantes disponibles
    for(SGeometry g : list_with_box)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        bounding_box_list.add(box);
      else
        throw new SRuntimeException("Erreur SVoxelSpace XXX : Ceci n'est pas supposé se produire!");
    }
    
    // S'assurer que la liste des boîtes n'est pas vide, sinon il n'y a pas de carte de voxel à construire
    if(!bounding_box_list.isEmpty())
    {
      // Faire l'évaluation de la dimension des voxels et construire le générateur de voxel
      //-------------------------------------------------------------------------------------------------------------------------------------------------
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.ONE_FOR_ONE_ALGORITHM);
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.BIGGEST_AVERAGE_LENGHT_ALGORITHM);
      SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.MID_AVERAGE_LENGHT_ALGORITHM);
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.SMALLEST_AVERAGE_LENGHT_ALGORITHM); 
      
      // Générateur de voxel pour la carte de voxel en construction
      voxel_builder = new SVoxelBuilder(evaluator.getDimension()); 
            
      // Construire le voxel d'extrême à la nouvelle carte et le mettre à jour à chaque insertion de géométrie
      absolute_extremum_voxel = new SVoxel(0, 0, 0);
              
      // Iterer sur l'ensemble boîtes englobante.
      // Intégrer leur voxels attitrés avec leur géométrie à la carte des voxels.
      // Mettre à jour le voxel d'extrême
      for(SBoundingBox box : bounding_box_list)
        absolute_extremum_voxel = addGeometryToMap(voxel_map, absolute_extremum_voxel, box.getGeometry(), voxel_builder.buildVoxel(box));     
      
      // Messages multiples à afficher
      SLog.logWriteLine("Message SVoxelSpace : Nombre de géométries dans la carte de voxels : " + bounding_box_list.size() + " géométries.");
      SLog.logWriteLine("Message SVoxelSpace : Taille des voxels : " + evaluator.getDimension() + " unités.");  
      SLog.logWriteLine("Message SVoxelSpace : Nombre de référence à des géométries : " + evaluateNbGeometryReference(voxel_map) + " références.");
      SLog.logWriteLine("Message SVoxelSpace : Nombre moyen de référence à des géométries par voxel : " + evaluateNbGeometryReferencePerVoxel(voxel_map) + " références/voxel.");
      
      SLog.logWriteLine();
    }//fin if
    else
    {
      // Il n'y a pas de boîte englobante de disponible pour l'espace avec voxel
      SLog.logWriteLine("Message SVoxelSpace : Aucune géométrie ne possède de boîte englobante! Le choix d'un espace de géométries en voxel devient inéfficace.");
     
      voxel_builder = null;   // Il n'y a pas de constructeur de voxel disponible
    }
    
    SLog.logWriteLine("Message SVoxelSpace : Fin de la construction de l'espace des géométries avec voxel.");
    SLog.logWriteLine();
    
    // Initialisation de l'espace des voxels complétée
    space_initialized = true;
  }
  
}//fin de la classe SVoxelSpace
