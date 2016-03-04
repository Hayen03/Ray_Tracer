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
import sim.math.SMath;
import sim.math.SVector3d;
import sim.util.SLog;

/**
 * La classe <b>SMultiVoxelSpace</b> représente un espace de géométrie partitionné en plusieurs niveaux de résolution de voxel.
 * Une géométrie sera ainsi introduite dans un seul espace de géométrie en fonction de sa taille afin de regrouper les géométries
 * de taille semblable.
 * 
 * @author Simon Vézina
 * @since 2015-11-25
 * @version 2015-12-29
 */
public class SMultiVoxelSpace extends SAbstractVoxelSpace {

  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable 'voxel_map_list' correspond à la liste des cartes des voxels où sont situées des géométries admettant une boîte englobante.
   */
  private final List<Map<SVoxel, List<SGeometry>>> voxel_map_list;             
  
  /**
   * La variable 'voxel_builder_list' correspond à la liste des constructeurs de voxel. 
   * La taille des voxels sera déterminée par un objet de type SVoxelDimensionEvaluator. 
   */
  private List<SVoxelBuilder> voxel_builder_list;                   
  
  /**
   * La variable 'absolute_extremum_voxel_list' correspond à la liste des voxels de coordonnée extremums en valeur absolue à celle contenue dans la carte de voxel attitrée.
   * Ceci permet de connaître la taille maximal de la carte des voxels.
   */
  private List<SVoxel> absolute_extremum_voxel_list;               
 
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un espace de géométrie à taille de voxel multiple par défaut. 
   */
  public SMultiVoxelSpace()
  {
    super();
    
    voxel_map_list = new ArrayList<Map<SVoxel, List<SGeometry>>>();
        
    voxel_builder_list = new ArrayList<SVoxelBuilder>();
    absolute_extremum_voxel_list = new ArrayList<SVoxel>();
    
    space_initialized = false;
  }

  @Override
  public SRay nearestIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // Vérifier si le rayon a déjà intersecté une géométrie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SMultiVoxelSpace 001 : Le rayon en paramètre a déjà intersecté une géométrie.");
    
    // Vérifier la valeur de t_max est adéquate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 002 : Le temps maximale ne peut pas être négative.");
   
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 003 : L'espace de géométries de voxel n'a pas été initialisé.");
    
    //Résultat de l'intersection avec la carte de voxel. Sera égale à "ray" s'il y en a pas eu.
    SRay intersection_in_voxel = nearestIntersectionInVoxelMapList(ray, t_max);
    
    //Résultat de l'intersection avec les géométries hors voxel
    List<SRay> list_intersection_not_in_voxel = intersections(linear_list, ray, t_max);
    
    // Ajouter l'intersection de la carte de voxel à la liste linéaire.
    // Cette liste sera ainsi pas vide.
    list_intersection_not_in_voxel.add(intersection_in_voxel);
    
    // Trier la liste pour le dernier élément ajouté.
    Collections.sort(list_intersection_not_in_voxel); 
    
    // Retourner le permier élément de la liste (sera sans intersection s'il n'y en a pas eu).
    return list_intersection_not_in_voxel.get(0);
  }

  /**
   * Méthode pour obtenir l'intersection la plus près entre un rayon et des géométries situées dans la liste des cartes de voxel.
   * 
   * @param ray - Le rayon à intersecter avec les géométries.
   * @param t_max - Le temps maximal.
   * @return Le rayon avec les caractéristiques de l'intersection (s'il y en a eu une).
   */
  private SRay nearestIntersectionInVoxelMapList(SRay ray, double t_max)
  {
    //Réaliser des calculs d'intersection seulement si la liste des cartes n'est pas vide
    if(voxel_map_list.isEmpty())
      return ray;
    else
    {
      // Crééer toutes les lignes de voxels pour l'ensemble des listes de carte
      List<SFastTraversalVoxelAlgorithm> FTVA_list = new ArrayList<SFastTraversalVoxelAlgorithm>();

      // Itérer sur l'ensemble des cartes de voxel et utiliser l'indexage pour avoir accès à leurs informations attitrées
      for(int i = 0; i < voxel_map_list.size(); i++)
      {
        Map<SVoxel, List<SGeometry>> map = voxel_map_list.get(i);

        // Analyser la carte et construire son FTVA à so indexe
        if(map == null)
          FTVA_list.add(null);    // pas de carte, donc pas de FTVA
        else
          if(!map.isEmpty())
            FTVA_list.add(new SFastTraversalVoxelAlgorithm(voxel_builder_list.get(i).getDimension(), ray, t_max, absolute_extremum_voxel_list.get(i)));
          else
            FTVA_list.add(null);  // pas de géométrie, donc pas de FTVA
      } 
      
      
      
      // Le rayon ayant réalisé l'intersection la plus près
      SRay minimum_ray = ray;
      
      
      
      
      // STRATÉGIE 1 : Itération en "série" en complétant chaque FTVA au complet
      /*
      // Itérer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          SRay ray_found = ray;
          
          // Itération sur la ligne de voxels
          while(FTVA.asNextVoxel() && !ray_found.asIntersected())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            ray_found = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
          }
          
          // Mise à jour de l'intersection la plus près en comparaison entre carte de voxels
          if(ray_found.asIntersected())
            if(ray_found.getT() < minimum_ray.getT())
              minimum_ray = ray_found;
        }
      }
      */
      
      
      // STRATÉGIE 2 : Itération en "série" en complétant chaque FTVA avec condition d'arrêt spéciale
      /*
      // Itérer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          // Itération sur la ligne de voxels
          while(FTVA.asNextVoxel() && FTVA.nextMinTime() < minimum_ray.getT())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            SRay new_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
          
            // Mise à jour de l'intersection la plus près en comparaison entre carte de voxels
            if(new_intersection.asIntersected())
              if(new_intersection.getT() < minimum_ray.getT())
                minimum_ray = new_intersection;
          }
        }
      }
      */
      
      
     
      
      
      // STRATÉGIE 3 : Itération en "parallèle"
      
      // Faire les passages supplémentaires
      int FTVA_over = 0;
      int on_limite = 0;
      
      double max_time = SMath.EPSILON;
            
      while(FTVA_over < FTVA_list.size())
      {
        FTVA_over = 0;
        on_limite = 0;
        
        /*
        for(int i = 0; i < FTVA_list.size(); i++)
          System.out.print("FTVA " + i + " " + FTVA_list.get(i).nextMinTime() + " ");
        System.out.println();
        */
        
        for(int i = 0; i < FTVA_list.size(); i++)
        {
          SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
          
          // Pas de FTVA, donc son itération est terminé
          if(FTVA == null)                
            FTVA_over++;
          else
            // Pas de voxel supplémentaire à parcourir dans le FTVA, donc son itération est terminé
            if(!FTVA.asNextVoxel()) 
              FTVA_over++;
            else
              // Une intersection à temps plus court a été trouvé, donc son itération est terminé
              if(minimum_ray.asIntersected() && FTVA.nextMinTime() > minimum_ray.getT())
              {
                FTVA.close();
                FTVA_over++;
              }
              else
              {
                // Vérifier si l'on avance dans ce FTVA ou s'il est déjà trop en avance sur le max_time
                if(FTVA.nextMinTime() < max_time)
                {
                  //Mise à jour du max_time
                  if(FTVA.nextMaxTime() > max_time)
                    max_time = FTVA.nextMaxTime();
                    
                  SVoxel voxel = FTVA.nextVoxel();
                    
                  SRay new_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel);
                  
                  // Mise à jour de l'intersection la plus près en comparaison entre carte de voxels
                  if(new_intersection.asIntersected())
                  {
                    FTVA.close();
                    FTVA_over++;
                      
                    if(new_intersection.getT() < minimum_ray.getT())
                      minimum_ray = new_intersection;
                  }
                }
                else
                  if(FTVA.nextMinTime() == max_time)
                    on_limite++;
                  
              }            
        }//fin for  
        
        // Si la somme des FTVA en attente et les FTVA en arrêt égale l'ensemble des FTVA, ça va bloquer.
        // Il faut avancer le max_time pour permettre d'avancer les FTVA en attente
        if(on_limite + FTVA_over == FTVA_list.size())
          max_time = max_time * (1.0 + SMath.EPSILON);
        
      }//fin while
      
      return minimum_ray;
    }
    
    
  }
 
  @Override
  public List<SRay> nearestOpaqueIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // Vérifier si le rayon a déjà intersecté une géométrie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SMultiVoxelSpace 004 : Le rayon en paramètre a déjà intersecté une géométrie.");
    
    // Vérifier la valeur de t_max est adéquate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 005 : Le temps maximale ne peut pas être négative.");
   
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 006 : L'espace de géométries de voxel n'a pas été initialisé.");
    
    // Obtenir la liste de l'intersection la plus près à partir de la liste linéaire des géométries
    List<SRay> list = nearestOpaqueIntersection(linear_list, ray, t_max);
            
    // Retourner la fusion de la liste linéraire avec la liste obtenue par la liste des cartes de voxels.
    return mergeNearestOpaqueIntersection(list, nearestOpaqueIntersectionInVoxelMapList(ray, t_max));
  }

  /**
   * Méthode pour obtenir l'intersection opaque la plus près à partir de la liste des cartes de voxels.
   * 
   * @param ray - Le rayon à intersecter avec les géométries.
   * @param t_max - Le temps maximal.
   * @return La liste contenant l'intersection opaque la plus près.
   */
  private List<SRay> nearestOpaqueIntersectionInVoxelMapList(SRay ray, double t_max) 
  {
    // La liste à retourner
    List<SRay> return_list = new ArrayList<SRay>();
        
    //Réaliser des calculs d'intersection seulement si la liste des cartes n'est pas vide
    if(voxel_map_list.isEmpty())
      return return_list;
    else
    {
      // Crééer toutes les lignes de voxels pour l'ensemble des listes de carte
      List<SFastTraversalVoxelAlgorithm> FTVA_list = new ArrayList<SFastTraversalVoxelAlgorithm>();

      // Itérer sur l'ensemble des cartes de voxel et utiliser l'indexage pour avoir accès à leurs informations attitrées
      for(int i = 0; i < voxel_map_list.size(); i++)
      {
        Map<SVoxel, List<SGeometry>> map = voxel_map_list.get(i);

        // Analyser la carte et construire son FTVA à so indexe
        if(map == null)
          FTVA_list.add(null);    // pas de carte, donc pas de FTVA
        else
          if(!map.isEmpty())
            FTVA_list.add(new SFastTraversalVoxelAlgorithm(voxel_builder_list.get(i).getDimension(), ray, t_max, absolute_extremum_voxel_list.get(i)));
          else
            FTVA_list.add(null);  // pas de géométrie, donc pas de FTVA
      }
      
      // VERSION 1 : ITERER EN SÉRIE SUR TOUT !!!
      /*
      // Itérer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          boolean opaque_intersection_found = false;
          
          // Itération sur la ligne de voxels
          while(FTVA.asNextVoxel() && !opaque_intersection_found)
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
            
            // Condition d'arrêt si une intersection opaque a été trouvée dans le FTVA courant
            if(!list.isEmpty())
              if(!list.get(0).getGeometry().isTransparent())
                opaque_intersection_found = true;
            
            // Ajouter la nouvelle liste de l'intersection opaque à la liste cumulative
            return_list = mergeNearestOpaqueIntersection(return_list, list);
          }
        }
      }//fin for
      */
      
      
      
      // VERSION 2 : ITÉRER EN SÉRIE AVEC CONDITION D'ARRÊT
      /*
      SRay minimum_opaque_ray = ray;
      
      // Itérer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          // Itération sur la ligne de voxels
          while(FTVA.asNextVoxel() && FTVA.nextMinTime() < minimum_opaque_ray.getT())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
            
            // Condition d'arrêt si une intersection opaque a été trouvée dans le FTVA courant
            if(!list.isEmpty())
              if(!list.get(0).getGeometry().isTransparent())
                if(list.get(0).getT() < minimum_opaque_ray.getT())
                  minimum_opaque_ray = list.get(0);
              
            // Ajouter la nouvelle liste de l'intersection opaque à la liste cumulative
            return_list = mergeNearestOpaqueIntersection(return_list, list);
          }
        }
      }//fin for
     */
      
      // VERSION 3 : ITÉRER EN PARALLÈLE
      
      SRay minimum_opaque_ray = ray;
      
      // Faire les passages supplémentaires
      int FTVA_over = 0;
      int on_limite = 0;
      
      double max_time = SMath.EPSILON;
            
      while(FTVA_over < FTVA_list.size())
      {
        FTVA_over = 0;
        on_limite = 0;
        
        for(int i = 0; i < FTVA_list.size(); i++)
        {
          SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
          
          // Pas de FTVA, donc son itération est terminé
          if(FTVA == null)                
            FTVA_over++;
          else
            // Pas de voxel supplémentaire à parcourir dans le FTVA, donc son itération est terminé
            if(!FTVA.asNextVoxel()) 
              FTVA_over++;
            else
              if(minimum_opaque_ray.asIntersected() && FTVA.nextMinTime() > minimum_opaque_ray.getT())
              {
                FTVA.close();
                FTVA_over++;
              }
              else
              {
                // Vérifier si l'on avance dans ce FTVA ou s'il est déjà trop en avance sur le max_time
                if(FTVA.nextMinTime() < max_time)
                {
                  //Mise à jour du max_time
                  if(FTVA.nextMaxTime() > max_time)
                    max_time = FTVA.nextMaxTime();
                  
                  SVoxel voxel = FTVA.nextVoxel();
                  
                  List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
                  
                  // Condition d'arrêt si une intersection opaque a été trouvée dans le FTVA courant
                  if(!list.isEmpty())
                    if(!list.get(0).getGeometry().isTransparent())
                    {
                      FTVA.close();
                      FTVA_over++;
                      
                      // Mise à jour de l'intersection opaque de moindre temps
                      if(list.get(0).getT() < minimum_opaque_ray.getT())
                        minimum_opaque_ray = list.get(0);
                    }
                  
                  // Ajouter la nouvelle liste de l'intersection opaque à la liste cumulative
                  return_list = mergeNearestOpaqueIntersection(return_list, list);
                  
                }
                else
                  if(FTVA.nextMinTime() == max_time)
                    on_limite++;  
         
              }
          
        }//fin for
        
        // Si la somme des FTVA en attente et les FTVA en arrêt égale l'ensemble des FTVA, ça va bloquer.
        // Il faut avancer le max_time pour permettre d'avancer les FTVA en attente
        if(on_limite + FTVA_over == FTVA_list.size())
          max_time = max_time * (1.0 + SMath.EPSILON);
        
      }//fin while
        
      return return_list;
    }//fin else  
  }
  
  @Override
  public List<SGeometry> listInsideGeometry(SVector3d v)
  {
    // Obtenir la liste des géométries où le vecteur v s'y retrouve dans la liste linéaire
    List<SGeometry> return_list = listInsideGeometry(linear_list, v);
    
    // Iterer sur l'ensemble des cartes de voxel et y ajouter les géométries où le vecteur v s'y retrouve
    for(int i = 0; i < voxel_map_list.size(); i++)
      return_list.addAll(listInsideGeometryInMap(voxel_map_list.get(i), voxel_builder_list.get(i), v));
    
    return return_list;
  }

  @Override
  public void initialize()
  {
    SLog.logWriteLine("Message SMultiVoxelSpace : Construction de l'espace des géométries avec voxel multiple.");
    
    // Générateur de boîte englobante
    SBoundingBoxBuilder box_builder = new SBoundingBoxBuilder();            
    
    // Séparateur de la collection de géométrie
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_BOX_AND_NO_BOX);
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_HALF_AND_HALF);
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_AT_AVERAGE_SIZE);
    SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_IN_FOUR_AVERAGE_SIZE);
    
    
    // Obtenir la liste des géométries avec boîte englobante en liste séparée
    List<List<SGeometry>> split_list = splitter.getSplitList();
    
    // Obtenir la liste des géométries sans boîte englobante et l'affecter à la liste linéaire
    linear_list = splitter.getNoBoxList();
    
    // Remplir les cartes de voxel pour chaque liste de géométries triées
    int list_count = 0;  // compter les cartes de voxel NON VIDE
    
    for(List<SGeometry> l : split_list)
    {
      // Liste des boîtes englobantes autour des géométrie de la liste
      List<SBoundingBox> bounding_box_list = new ArrayList<SBoundingBox>();   
      
      // Obtenir toutes les boîtes englobantes disponibles
      for(SGeometry g : l)
      {
        SBoundingBox box = box_builder.buildBoundingBox(g);
        
        if(box != null)
          bounding_box_list.add(box);
        else
          throw new SRuntimeException("Erreur SMultiVoxelSpace XXX : Ceci n'est pas supposé se produire!");
      }
      
      // S'assurer que la liste n'est pas vide, sinon il n'y a pas de carte de voxel à construire
      if(!bounding_box_list.isEmpty())
      {
        // Faire l'évaluation de la dimension des voxels et construire le générateur de voxel
        SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.MID_AVERAGE_LENGHT_ALGORITHM);
        
        // Générateur de voxel pour la carte de voxel en construction
        SVoxelBuilder voxel_builder = new SVoxelBuilder(evaluator.getDimension()); 
        voxel_builder_list.add(voxel_builder);
        
        // Construire la carte des voxels qui vont accueillir ces géométries
        Map<SVoxel, List<SGeometry>> voxel_map = new HashMap<SVoxel, List<SGeometry>>();
        voxel_map_list.add(voxel_map);
        
        // Construire le voxel d'extrême à la nouvelle carte et le mettre à jour à chaque insertion de géométrie
        SVoxel extremum_voxel = new SVoxel(0, 0, 0);
                
        // Iterer sur l'ensemble boîtes englobante.
        // Intégrer leur voxels attitrés avec leur géométrie à la carte des voxels.
        // Mettre à jour le voxel d'extrême
        for(SBoundingBox box : bounding_box_list)
          extremum_voxel = addGeometryToMap(voxel_map, extremum_voxel, box.getGeometry(), voxel_builder.buildVoxel(box));     
        
        // Mettre le voxel d'extrême dans la liste (pour le faire correspondre avec la bonne carte)
        absolute_extremum_voxel_list.add(extremum_voxel);
        
        // Messages multiples à afficher
        SLog.logWriteLine("Message SMultiVoxelSpace : Construction de l'espace des voxels #" + (list_count+1) +".");
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre de géométries dans la carte de voxels : " + bounding_box_list.size() + " géométries.");
        SLog.logWriteLine("Message SMultiVoxelSpace : Taille des voxels : " + evaluator.getDimension() + " unités.");  
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre de référence à des géométries : " + evaluateNbGeometryReference(voxel_map) + " références.");
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre moyen de référence à des géométries par voxel : " + evaluateNbGeometryReferencePerVoxel(voxel_map) + " références/voxel.");
        
        SLog.logWriteLine();
        
        list_count++;
      }//fin if
      else
      {
        // La liste des boîtes englobantes étant vide (pas de géométrie dans la liste),
        // il n'y aura pas de carte à cet index dans la liste
        voxel_map_list.add(null);
        voxel_builder_list.add(null);
        absolute_extremum_voxel_list.add(null);
      }
    }//fin for
  
    // Remarque : le comptage s'effectuant à la fin de la boucle for, le nombre est présentement égal à list_count (et non list_count+1)
    SLog.logWriteLine("Message SMultiVoxelSpace : Fin de la construction des " + (list_count) + " espaces multiples de voxels.");
    SLog.logWriteLine();
    
    space_initialized = true;
  }
  
}//fin SMultiVoxelSpace
