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
 * La classe <b>SMultiVoxelSpace</b> repr�sente un espace de g�om�trie partitionn� en plusieurs niveaux de r�solution de voxel.
 * Une g�om�trie sera ainsi introduite dans un seul espace de g�om�trie en fonction de sa taille afin de regrouper les g�om�tries
 * de taille semblable.
 * 
 * @author Simon V�zina
 * @since 2015-11-25
 * @version 2015-12-29
 */
public class SMultiVoxelSpace extends SAbstractVoxelSpace {

  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable 'voxel_map_list' correspond � la liste des cartes des voxels o� sont situ�es des g�om�tries admettant une bo�te englobante.
   */
  private final List<Map<SVoxel, List<SGeometry>>> voxel_map_list;             
  
  /**
   * La variable 'voxel_builder_list' correspond � la liste des constructeurs de voxel. 
   * La taille des voxels sera d�termin�e par un objet de type SVoxelDimensionEvaluator. 
   */
  private List<SVoxelBuilder> voxel_builder_list;                   
  
  /**
   * La variable 'absolute_extremum_voxel_list' correspond � la liste des voxels de coordonn�e extremums en valeur absolue � celle contenue dans la carte de voxel attitr�e.
   * Ceci permet de conna�tre la taille maximal de la carte des voxels.
   */
  private List<SVoxel> absolute_extremum_voxel_list;               
 
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un espace de g�om�trie � taille de voxel multiple par d�faut. 
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
    // V�rifier si le rayon a d�j� intersect� une g�om�trie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SMultiVoxelSpace 001 : Le rayon en param�tre a d�j� intersect� une g�om�trie.");
    
    // V�rifier la valeur de t_max est ad�quate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 002 : Le temps maximale ne peut pas �tre n�gative.");
   
    // V�rifier si l'espace a �t� initialis�
    if(!space_initialized)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 003 : L'espace de g�om�tries de voxel n'a pas �t� initialis�.");
    
    //R�sultat de l'intersection avec la carte de voxel. Sera �gale � "ray" s'il y en a pas eu.
    SRay intersection_in_voxel = nearestIntersectionInVoxelMapList(ray, t_max);
    
    //R�sultat de l'intersection avec les g�om�tries hors voxel
    List<SRay> list_intersection_not_in_voxel = intersections(linear_list, ray, t_max);
    
    // Ajouter l'intersection de la carte de voxel � la liste lin�aire.
    // Cette liste sera ainsi pas vide.
    list_intersection_not_in_voxel.add(intersection_in_voxel);
    
    // Trier la liste pour le dernier �l�ment ajout�.
    Collections.sort(list_intersection_not_in_voxel); 
    
    // Retourner le permier �l�ment de la liste (sera sans intersection s'il n'y en a pas eu).
    return list_intersection_not_in_voxel.get(0);
  }

  /**
   * M�thode pour obtenir l'intersection la plus pr�s entre un rayon et des g�om�tries situ�es dans la liste des cartes de voxel.
   * 
   * @param ray - Le rayon � intersecter avec les g�om�tries.
   * @param t_max - Le temps maximal.
   * @return Le rayon avec les caract�ristiques de l'intersection (s'il y en a eu une).
   */
  private SRay nearestIntersectionInVoxelMapList(SRay ray, double t_max)
  {
    //R�aliser des calculs d'intersection seulement si la liste des cartes n'est pas vide
    if(voxel_map_list.isEmpty())
      return ray;
    else
    {
      // Cr��er toutes les lignes de voxels pour l'ensemble des listes de carte
      List<SFastTraversalVoxelAlgorithm> FTVA_list = new ArrayList<SFastTraversalVoxelAlgorithm>();

      // It�rer sur l'ensemble des cartes de voxel et utiliser l'indexage pour avoir acc�s � leurs informations attitr�es
      for(int i = 0; i < voxel_map_list.size(); i++)
      {
        Map<SVoxel, List<SGeometry>> map = voxel_map_list.get(i);

        // Analyser la carte et construire son FTVA � so indexe
        if(map == null)
          FTVA_list.add(null);    // pas de carte, donc pas de FTVA
        else
          if(!map.isEmpty())
            FTVA_list.add(new SFastTraversalVoxelAlgorithm(voxel_builder_list.get(i).getDimension(), ray, t_max, absolute_extremum_voxel_list.get(i)));
          else
            FTVA_list.add(null);  // pas de g�om�trie, donc pas de FTVA
      } 
      
      
      
      // Le rayon ayant r�alis� l'intersection la plus pr�s
      SRay minimum_ray = ray;
      
      
      
      
      // STRAT�GIE 1 : It�ration en "s�rie" en compl�tant chaque FTVA au complet
      /*
      // It�rer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          SRay ray_found = ray;
          
          // It�ration sur la ligne de voxels
          while(FTVA.asNextVoxel() && !ray_found.asIntersected())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            ray_found = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
          }
          
          // Mise � jour de l'intersection la plus pr�s en comparaison entre carte de voxels
          if(ray_found.asIntersected())
            if(ray_found.getT() < minimum_ray.getT())
              minimum_ray = ray_found;
        }
      }
      */
      
      
      // STRAT�GIE 2 : It�ration en "s�rie" en compl�tant chaque FTVA avec condition d'arr�t sp�ciale
      /*
      // It�rer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          // It�ration sur la ligne de voxels
          while(FTVA.asNextVoxel() && FTVA.nextMinTime() < minimum_ray.getT())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            SRay new_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
          
            // Mise � jour de l'intersection la plus pr�s en comparaison entre carte de voxels
            if(new_intersection.asIntersected())
              if(new_intersection.getT() < minimum_ray.getT())
                minimum_ray = new_intersection;
          }
        }
      }
      */
      
      
     
      
      
      // STRAT�GIE 3 : It�ration en "parall�le"
      
      // Faire les passages suppl�mentaires
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
          
          // Pas de FTVA, donc son it�ration est termin�
          if(FTVA == null)                
            FTVA_over++;
          else
            // Pas de voxel suppl�mentaire � parcourir dans le FTVA, donc son it�ration est termin�
            if(!FTVA.asNextVoxel()) 
              FTVA_over++;
            else
              // Une intersection � temps plus court a �t� trouv�, donc son it�ration est termin�
              if(minimum_ray.asIntersected() && FTVA.nextMinTime() > minimum_ray.getT())
              {
                FTVA.close();
                FTVA_over++;
              }
              else
              {
                // V�rifier si l'on avance dans ce FTVA ou s'il est d�j� trop en avance sur le max_time
                if(FTVA.nextMinTime() < max_time)
                {
                  //Mise � jour du max_time
                  if(FTVA.nextMaxTime() > max_time)
                    max_time = FTVA.nextMaxTime();
                    
                  SVoxel voxel = FTVA.nextVoxel();
                    
                  SRay new_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel);
                  
                  // Mise � jour de l'intersection la plus pr�s en comparaison entre carte de voxels
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
        
        // Si la somme des FTVA en attente et les FTVA en arr�t �gale l'ensemble des FTVA, �a va bloquer.
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
    // V�rifier si le rayon a d�j� intersect� une g�om�trie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SMultiVoxelSpace 004 : Le rayon en param�tre a d�j� intersect� une g�om�trie.");
    
    // V�rifier la valeur de t_max est ad�quate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 005 : Le temps maximale ne peut pas �tre n�gative.");
   
    // V�rifier si l'espace a �t� initialis�
    if(!space_initialized)
      throw new SRuntimeException("Erreur SMultiVoxelSpace 006 : L'espace de g�om�tries de voxel n'a pas �t� initialis�.");
    
    // Obtenir la liste de l'intersection la plus pr�s � partir de la liste lin�aire des g�om�tries
    List<SRay> list = nearestOpaqueIntersection(linear_list, ray, t_max);
            
    // Retourner la fusion de la liste lin�raire avec la liste obtenue par la liste des cartes de voxels.
    return mergeNearestOpaqueIntersection(list, nearestOpaqueIntersectionInVoxelMapList(ray, t_max));
  }

  /**
   * M�thode pour obtenir l'intersection opaque la plus pr�s � partir de la liste des cartes de voxels.
   * 
   * @param ray - Le rayon � intersecter avec les g�om�tries.
   * @param t_max - Le temps maximal.
   * @return La liste contenant l'intersection opaque la plus pr�s.
   */
  private List<SRay> nearestOpaqueIntersectionInVoxelMapList(SRay ray, double t_max) 
  {
    // La liste � retourner
    List<SRay> return_list = new ArrayList<SRay>();
        
    //R�aliser des calculs d'intersection seulement si la liste des cartes n'est pas vide
    if(voxel_map_list.isEmpty())
      return return_list;
    else
    {
      // Cr��er toutes les lignes de voxels pour l'ensemble des listes de carte
      List<SFastTraversalVoxelAlgorithm> FTVA_list = new ArrayList<SFastTraversalVoxelAlgorithm>();

      // It�rer sur l'ensemble des cartes de voxel et utiliser l'indexage pour avoir acc�s � leurs informations attitr�es
      for(int i = 0; i < voxel_map_list.size(); i++)
      {
        Map<SVoxel, List<SGeometry>> map = voxel_map_list.get(i);

        // Analyser la carte et construire son FTVA � so indexe
        if(map == null)
          FTVA_list.add(null);    // pas de carte, donc pas de FTVA
        else
          if(!map.isEmpty())
            FTVA_list.add(new SFastTraversalVoxelAlgorithm(voxel_builder_list.get(i).getDimension(), ray, t_max, absolute_extremum_voxel_list.get(i)));
          else
            FTVA_list.add(null);  // pas de g�om�trie, donc pas de FTVA
      }
      
      // VERSION 1 : ITERER EN S�RIE SUR TOUT !!!
      /*
      // It�rer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          boolean opaque_intersection_found = false;
          
          // It�ration sur la ligne de voxels
          while(FTVA.asNextVoxel() && !opaque_intersection_found)
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
            
            // Condition d'arr�t si une intersection opaque a �t� trouv�e dans le FTVA courant
            if(!list.isEmpty())
              if(!list.get(0).getGeometry().isTransparent())
                opaque_intersection_found = true;
            
            // Ajouter la nouvelle liste de l'intersection opaque � la liste cumulative
            return_list = mergeNearestOpaqueIntersection(return_list, list);
          }
        }
      }//fin for
      */
      
      
      
      // VERSION 2 : IT�RER EN S�RIE AVEC CONDITION D'ARR�T
      /*
      SRay minimum_opaque_ray = ray;
      
      // It�rer sur l'ensemble des FTVA
      for(int i = 0; i < FTVA_list.size(); i++)
      {
        SFastTraversalVoxelAlgorithm FTVA = FTVA_list.get(i);
        
        if(FTVA != null) 
        {
          // It�ration sur la ligne de voxels
          while(FTVA.asNextVoxel() && FTVA.nextMinTime() < minimum_opaque_ray.getT())
          {
            SVoxel voxel = FTVA.nextVoxel();
            
            List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
            
            // Condition d'arr�t si une intersection opaque a �t� trouv�e dans le FTVA courant
            if(!list.isEmpty())
              if(!list.get(0).getGeometry().isTransparent())
                if(list.get(0).getT() < minimum_opaque_ray.getT())
                  minimum_opaque_ray = list.get(0);
              
            // Ajouter la nouvelle liste de l'intersection opaque � la liste cumulative
            return_list = mergeNearestOpaqueIntersection(return_list, list);
          }
        }
      }//fin for
     */
      
      // VERSION 3 : IT�RER EN PARALL�LE
      
      SRay minimum_opaque_ray = ray;
      
      // Faire les passages suppl�mentaires
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
          
          // Pas de FTVA, donc son it�ration est termin�
          if(FTVA == null)                
            FTVA_over++;
          else
            // Pas de voxel suppl�mentaire � parcourir dans le FTVA, donc son it�ration est termin�
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
                // V�rifier si l'on avance dans ce FTVA ou s'il est d�j� trop en avance sur le max_time
                if(FTVA.nextMinTime() < max_time)
                {
                  //Mise � jour du max_time
                  if(FTVA.nextMaxTime() > max_time)
                    max_time = FTVA.nextMaxTime();
                  
                  SVoxel voxel = FTVA.nextVoxel();
                  
                  List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map_list.get(i), voxel_builder_list.get(i), voxel); 
                  
                  // Condition d'arr�t si une intersection opaque a �t� trouv�e dans le FTVA courant
                  if(!list.isEmpty())
                    if(!list.get(0).getGeometry().isTransparent())
                    {
                      FTVA.close();
                      FTVA_over++;
                      
                      // Mise � jour de l'intersection opaque de moindre temps
                      if(list.get(0).getT() < minimum_opaque_ray.getT())
                        minimum_opaque_ray = list.get(0);
                    }
                  
                  // Ajouter la nouvelle liste de l'intersection opaque � la liste cumulative
                  return_list = mergeNearestOpaqueIntersection(return_list, list);
                  
                }
                else
                  if(FTVA.nextMinTime() == max_time)
                    on_limite++;  
         
              }
          
        }//fin for
        
        // Si la somme des FTVA en attente et les FTVA en arr�t �gale l'ensemble des FTVA, �a va bloquer.
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
    // Obtenir la liste des g�om�tries o� le vecteur v s'y retrouve dans la liste lin�aire
    List<SGeometry> return_list = listInsideGeometry(linear_list, v);
    
    // Iterer sur l'ensemble des cartes de voxel et y ajouter les g�om�tries o� le vecteur v s'y retrouve
    for(int i = 0; i < voxel_map_list.size(); i++)
      return_list.addAll(listInsideGeometryInMap(voxel_map_list.get(i), voxel_builder_list.get(i), v));
    
    return return_list;
  }

  @Override
  public void initialize()
  {
    SLog.logWriteLine("Message SMultiVoxelSpace : Construction de l'espace des g�om�tries avec voxel multiple.");
    
    // G�n�rateur de bo�te englobante
    SBoundingBoxBuilder box_builder = new SBoundingBoxBuilder();            
    
    // S�parateur de la collection de g�om�trie
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_BOX_AND_NO_BOX);
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_HALF_AND_HALF);
    //SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_AT_AVERAGE_SIZE);
    SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_IN_FOUR_AVERAGE_SIZE);
    
    
    // Obtenir la liste des g�om�tries avec bo�te englobante en liste s�par�e
    List<List<SGeometry>> split_list = splitter.getSplitList();
    
    // Obtenir la liste des g�om�tries sans bo�te englobante et l'affecter � la liste lin�aire
    linear_list = splitter.getNoBoxList();
    
    // Remplir les cartes de voxel pour chaque liste de g�om�tries tri�es
    int list_count = 0;  // compter les cartes de voxel NON VIDE
    
    for(List<SGeometry> l : split_list)
    {
      // Liste des bo�tes englobantes autour des g�om�trie de la liste
      List<SBoundingBox> bounding_box_list = new ArrayList<SBoundingBox>();   
      
      // Obtenir toutes les bo�tes englobantes disponibles
      for(SGeometry g : l)
      {
        SBoundingBox box = box_builder.buildBoundingBox(g);
        
        if(box != null)
          bounding_box_list.add(box);
        else
          throw new SRuntimeException("Erreur SMultiVoxelSpace XXX : Ceci n'est pas suppos� se produire!");
      }
      
      // S'assurer que la liste n'est pas vide, sinon il n'y a pas de carte de voxel � construire
      if(!bounding_box_list.isEmpty())
      {
        // Faire l'�valuation de la dimension des voxels et construire le g�n�rateur de voxel
        SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.MID_AVERAGE_LENGHT_ALGORITHM);
        
        // G�n�rateur de voxel pour la carte de voxel en construction
        SVoxelBuilder voxel_builder = new SVoxelBuilder(evaluator.getDimension()); 
        voxel_builder_list.add(voxel_builder);
        
        // Construire la carte des voxels qui vont accueillir ces g�om�tries
        Map<SVoxel, List<SGeometry>> voxel_map = new HashMap<SVoxel, List<SGeometry>>();
        voxel_map_list.add(voxel_map);
        
        // Construire le voxel d'extr�me � la nouvelle carte et le mettre � jour � chaque insertion de g�om�trie
        SVoxel extremum_voxel = new SVoxel(0, 0, 0);
                
        // Iterer sur l'ensemble bo�tes englobante.
        // Int�grer leur voxels attitr�s avec leur g�om�trie � la carte des voxels.
        // Mettre � jour le voxel d'extr�me
        for(SBoundingBox box : bounding_box_list)
          extremum_voxel = addGeometryToMap(voxel_map, extremum_voxel, box.getGeometry(), voxel_builder.buildVoxel(box));     
        
        // Mettre le voxel d'extr�me dans la liste (pour le faire correspondre avec la bonne carte)
        absolute_extremum_voxel_list.add(extremum_voxel);
        
        // Messages multiples � afficher
        SLog.logWriteLine("Message SMultiVoxelSpace : Construction de l'espace des voxels #" + (list_count+1) +".");
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre de g�om�tries dans la carte de voxels : " + bounding_box_list.size() + " g�om�tries.");
        SLog.logWriteLine("Message SMultiVoxelSpace : Taille des voxels : " + evaluator.getDimension() + " unit�s.");  
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre de r�f�rence � des g�om�tries : " + evaluateNbGeometryReference(voxel_map) + " r�f�rences.");
        SLog.logWriteLine("Message SMultiVoxelSpace : Nombre moyen de r�f�rence � des g�om�tries par voxel : " + evaluateNbGeometryReferencePerVoxel(voxel_map) + " r�f�rences/voxel.");
        
        SLog.logWriteLine();
        
        list_count++;
      }//fin if
      else
      {
        // La liste des bo�tes englobantes �tant vide (pas de g�om�trie dans la liste),
        // il n'y aura pas de carte � cet index dans la liste
        voxel_map_list.add(null);
        voxel_builder_list.add(null);
        absolute_extremum_voxel_list.add(null);
      }
    }//fin for
  
    // Remarque : le comptage s'effectuant � la fin de la boucle for, le nombre est pr�sentement �gal � list_count (et non list_count+1)
    SLog.logWriteLine("Message SMultiVoxelSpace : Fin de la construction des " + (list_count) + " espaces multiples de voxels.");
    SLog.logWriteLine();
    
    space_initialized = true;
  }
  
}//fin SMultiVoxelSpace
