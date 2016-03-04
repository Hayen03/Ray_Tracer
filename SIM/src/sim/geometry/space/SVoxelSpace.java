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
 * La classe SVoxelSpace repr�sentant un espace de g�om�tries distribu�es dans un espace de voxels (en trois dimensions).
 * Cette organisation permettra d'effecter l'intersection d'un rayon avec un nombre plus limit�s de g�om�tries ce qui va acc�l�rer les calculs.
 * </p> 
 * 
 * <p>
 * Distribu�e dans une grille r�guli�re de voxel, chaque g�om�trie sera localis�e dans un ou plusieurs voxels 
 * et le lancer d'un rayon parcourera un nombre limit� de voxels ce qui ainsi limitera le nombre de tests d'intersection.
 * </p>
 * 
 * @author Simon V�zina
 * @since 2015-08-04
 * @version 2015-12-29
 */
public class SVoxelSpace extends SAbstractVoxelSpace {

  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>voxel_map</b> correspond � la carte des voxels o� sont situ�es des g�om�tries admettant une bo�te englobante.
   */
  private final Map<SVoxel, List<SGeometry>> voxel_map;  
  
  /**
   * La variable <b>voxel_builder</b> correspond au constructeur de voxel. 
   * La taille des voxels sera d�termin�e par un objet de type SVoxelDimensionEvaluator. 
   */
  private SVoxelBuilder voxel_builder;                   
  
  /**
   * La variable <b>absolute_extremum_voxel</b> correspond � un voxel de coordonn�e extremums en valeur absolue � celle contenue dans la carte de voxel.
   * Ceci permet de conna�tre la taille maximal de la carte des voxels.
   */
  private SVoxel absolute_extremum_voxel;               
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un espace de voxel par d�faut. 
   */
  public SVoxelSpace()
  {
    super();
    
    voxel_map = new HashMap<SVoxel, List<SGeometry>>();
    voxel_builder = null;
    absolute_extremum_voxel = null;
  }

  //------------
  // M�THODES //
  //------------
  
  @Override
  public SRay nearestIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // V�rifier si le rayon a d�j� intersect� une g�om�trie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SVoxelSpace 003 : Le rayon en param�tre a d�j� intersect� une g�om�trie.");
    
    // V�rifier la valeur de t_max est ad�quate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SVoxelSpace 004 : Le temps maximale ne peut pas �tre n�gative.");
   
    // V�rifier si l'espace a �t� initialis�
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 005 : L'espace de g�om�tries de voxel n'a pas �t� initialis�.");
    
    //R�sultat de l'intersection avec la carte de voxel. Sera �gale � "ray" s'il y en a pas eu.
    SRay intersection_in_voxel = nearestIntersectionInVoxelMap(ray, t_max);
    
    //R�sultat de l'intersection avec les g�om�tries hors voxel
    List<SRay> list_intersection_not_in_voxel = intersections(linear_list, ray, t_max);
    
    // Ajouter l'intersection de la carte de voxel � la liste lin�aire.
    // Cette liste sera ainsi pas vide.
    list_intersection_not_in_voxel.add(intersection_in_voxel);
    
    // Trier la liste pour le dernier �l�ment ajout�.
    Collections.sort(list_intersection_not_in_voxel); 
    
    // Retourner le permier �l�ment de la liste (sera sans intersection s'il n'y en a pas eu).
    return list_intersection_not_in_voxel.get(0);
    
    /*
    //Identification de l'intersection la plus pr�s (de plus petite valeur de ray.getT())
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
   * M�thode pour obtenir l'intersection la plus pr�s entre un rayon et des g�om�tries situ�es dans la carte de voxel.
   * @param ray - Le rayon � intersecter avec les g�om�tries.
   * @param t_max - Le temps maximal.
   * @return Le rayon avec les caract�ristiques de l'intersection (s'il y en a eu une).
   */
  private SRay nearestIntersectionInVoxelMap(SRay ray, double t_max)
  {
    //R�aliser des calculs d'intersection avec les g�om�tries de la carte uniquement si elle n'est pas vide
    if(!voxel_map.isEmpty())
    {
      //Cr�er la ligne de voxel � parcourir un � un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      //Faire l'it�ration sur la ligne de voxel depuis l'origine du rayon
      while(line_of_voxel.asNextVoxel())
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        // Faire le test de l'intersection des g�om�tries situ�es dans le voxel en cours
        SRay ray_intersection = nearestIntersectionInVoxelMap(ray, t_max, voxel_map, voxel_builder, voxel);
        
        // Retourner le r�sultat de l'intersection s'il y en a une, car elle sera n�cessairement de plus court temps
        if(ray_intersection.asIntersected())
          return ray_intersection;
      }
    }
    
    // Aucune intersection valide n'a �t� trouv�e.
    return ray;
  }
  
  @Override
  public List<SRay> nearestOpaqueIntersection(SRay ray, double t_max) throws SRuntimeException
  {
    // V�rifier si le rayon a d�j� intersect� une g�om�trie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SVoxelSpace 006 : Le rayon en param�tre a d�j� intersect� une g�om�trie.");
    
    // V�rifier la valeur de t_max est ad�quate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SVoxelSpace 007 : Le temps maximale ne peut pas �tre n�gative.");
   
    // V�rifier si l'espace a �t� initialis�
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 008 : L'espace de g�om�tries de voxel n'a pas �t� initialis�.");
   
    // La liste d�termin�e dans la carte des g�om�tries
    List<SRay> list_in_voxel = nearestOpaqueIntersectionInVoxelMap(ray, t_max);
    
    // La liste d�termin�e dans la liste lin�aire des g�om�tries
    List<SRay> list_not_in_voxel = nearestOpaqueIntersection(linear_list, ray, t_max);
    
    // La liste fusionn�e ad�quatement
    return mergeNearestOpaqueIntersection(list_in_voxel, list_not_in_voxel);
    
    /*
    int nb_opaque_intersection = 0;   //d�butons avec l'hypoth�se qu'il n'y a pas d'intersection opaque
    
    List<SRay> result_list = nearestOpaqueIntersectionInVoxelMap(ray, t_max);                 //la liste r�sultante avec voxel
    
    //V�rifier s'il y a une intersection opaque dans la liste avec voxel
    if(!result_list.isEmpty())
      if(!result_list.get(0).getGeometry().isTransparent())
        nb_opaque_intersection++;
    
    List<SRay> result_list_not_in_voxel = nearestOpaqueIntersection(linear_list, ray, t_max); //la liste r�sultante avec voxel
    
    //V�rifier s'il y a une intersection opaque dans la liste sans voxel
    if(!result_list_not_in_voxel.isEmpty())
      if(!result_list_not_in_voxel.get(0).getGeometry().isTransparent())
        nb_opaque_intersection++;
    
    //Fusionner les listes
    result_list.addAll(result_list_not_in_voxel);   
    
    //Retourner la liste vide si c'est le cas, car il n'y a plus rien � faire
    if(result_list.isEmpty())
      return result_list;
    
    //Trier le tout selon la m�me strat�gie
    Collections.sort(result_list);        //Trier dans l'ordre croissant
    Collections.reverse(result_list);     //Inverser l'ordre en ordre d�croissant
    
    //V�rifier l'�tat de la liste et retirer des �l�ments au besoin
    if(nb_opaque_intersection < 1)
      return result_list;                 //sans intersection opaque, la liste est compl�te
    else
    {
      int found = 0;                                          //nombre d'intersection opaque trouv�e dans la liste
      
      //Nous avons la garantie ici de trouv� au moins une g�om�trie opaque
      //It�rer tant qu'on a pas trouv� toutes les g�om�tries opaques
      while(found != nb_opaque_intersection)                  
      {    
        if(result_list.get(0).getGeometry().isTransparent())  
          result_list.remove(0);                              //retirer le 1ier �l�ment de la liste si elle est transparente
        else
        {
          found++;                                            //nous en avons trouv� 1 de plus
          
          if(found < nb_opaque_intersection)                  
            result_list.remove(0);                            //retirer la g�omtrie opaque s'il y en a d'autres dans la liste
        }
      }
      
      return result_list;   //la liste commence par une g�om�trie opaque suivit de g�om�trie transparente
    }
    */
    
  }

  /**
   * M�thode pour obtenir la liste des intersections transparente en ordre d�croissant dont la plus �loign� (premi�re de la liste) sera une g�om�trie opaque s'il y a eu intersection de cette nature.
   * 
   * @param ray - Le rayon � intersecter.
   * @param t_max - Le temps maximal pouvant �tre parcouru par le rayon.
   * @return La liste des intersections transparente en odre d�croissant dont le premier �l�ment sera une g�om�trie opaque s'il y a eu intersection de cette nature.
   */
  private List<SRay> nearestOpaqueIntersectionInVoxelMap(SRay ray, double t_max)
  {
    List<SRay> return_list = new ArrayList<SRay>();
    
    if(!voxel_map.isEmpty())
    {
      //Cr�er la ligne de voxel � parcourir un � un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      while(line_of_voxel.asNextVoxel())
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        // Obtenir la liste de l'intersection opaque associ� au voxel courant
        List<SRay> list = nearestOpaqueIntersectionInVoxelMap(ray, t_max, voxel_map, voxel_builder, voxel);
        
        // Ajouter cette liste � la liste � retourner
        return_list = mergeNearestOpaqueIntersection(return_list, list);
        
        // Retourner cette liste si l'intersection opaque a d�j� �t� trouv�e.
        if(!return_list.isEmpty())
          if(!return_list.get(0).getGeometry().isTransparent())
            return return_list;
      }
    }
    
    // La liste est vide ou elle contient uniquement des g�om�tries transparentes
    return return_list;
    
    /*
    SRay nearest_opaque_intersection = ray;                             //l'intersection opaque la plus pr�s ... initialement, il n'y en a pas !
    
    List<SRay> list_transparent_intersection = new LinkedList<SRay>();  //liste des intersections avec g�om�trie transparente
    
    //R�aliser des calculs d'intersection avec les g�om�tries de la carte uniquement si elle n'est pas vide
    if(!voxel_map.isEmpty())
    {
      //Cr�er la ligne de voxel � parcourir un � un
      SFastTraversalVoxelAlgorithm line_of_voxel = new SFastTraversalVoxelAlgorithm(voxel_builder.getDimension(), ray, t_max, absolute_extremum_voxel);
      
      //Faire l'it�ration sur la ligne de voxel depuis l'origine du rayon
      boolean opaque_intersection_not_found = true;
      
      while(line_of_voxel.asNextVoxel() && opaque_intersection_not_found)
      {
        SVoxel voxel = line_of_voxel.nextVoxel();
        
        List<SGeometry> list = voxel_map.get(voxel);
        
        if(list != null)          //si le voxel est dans la carte
          if(!list.isEmpty())     //si la liste de g�om�trie associ� � ce voxel n'est pas vide
          {
            List<SRay> list_intersection = intersections(list, ray, t_max);   //r�aliser les intersections avec la liste
            
            //- Regarder la liste des intersections 
            //- Conserver les intersection avec g�om�trie transparente, mais qui se retrouve dans le voxel courant
            //- Conserver l'intersection avec la g�om�trie opaque la plus pr�s
            if(!list_intersection.isEmpty())
              for(SRay r : list_intersection)
                if(voxel_builder.buildVoxel(r.getIntersectionPosition()).equals(voxel))   //si le voxel o� est r�alis� l'intersection se trouve � �tre le voxel courant
                  if(r.getGeometry().isTransparent())
                    list_transparent_intersection.add(r);                 //Ajouter la g�om�trie transparente � la liste des transparentes
                  else
                    if(r.getT() < nearest_opaque_intersection.getT())     
                    {  
                      nearest_opaque_intersection = r;                    //Remplacer l'ancienne g�om�trie opaque si la nouvelle est plus pr�s 
                      opaque_intersection_not_found = true;
                    }
          }
      }//fin while
    }
    
    //Construire la liste officielle des intersections transparentes en gardant  
    //uniquement les intersections transparentes avant celle qui est opaque et  
    //mettre l'intersection opaque dans la liste s'il y en a eu une.
    //IMPORTANT : Il faut trier la liste en ordre d�croissant par la suite (celle opaque sera alors la premi�re).
    if(nearest_opaque_intersection.asIntersected())       //S'il y a pas eu d'intersection opaque
    { 
      List<SRay> list_return = new LinkedList<SRay>();    //Nouvelle liste � retourner
      
      for(SRay r : list_transparent_intersection)
        if(r.getT() < nearest_opaque_intersection.getT()) //Ajouter les g�om�tries transparente plus pr�s que l'intersection opaque
          list_return.add(r);
      
      list_return.add(nearest_opaque_intersection);       //Ajouter l'intersection opaque
      
      Collections.sort(list_return);                      //Trier dans l'ordre croissant
      Collections.reverse(list_return);                   //Inverser l'ordre en ordre d�croissant (celle opaque sera la premi�re de la liste)
      return list_return;
    }
    else
    {
      Collections.sort(list_transparent_intersection);    //Trier dans l'ordre croissant
      Collections.reverse(list_transparent_intersection); //Inverser l'ordre en ordre d�croissant (ici, il n'y a pas de g�om�trie opaque)
      return list_transparent_intersection;
    }
    */
  }
  
  @Override
  public List<SGeometry> listInsideGeometry(SVector3d v)
  {
    // V�rifier que l'initialisation a �t� compl�t�e
    if(!space_initialized)
      throw new SRuntimeException("Erreur SVoxelSpace 009 : L'espace de voxel n'a pas �t� initialis�.");
    
    // Liste des g�om�tries o� le vecteur v sera situ� � l'int�rieur.
    // D�butons avec la liste disponible � partir des informations de la carte des voxels.
    List<SGeometry> inside_list = listInsideGeometryInMap(voxel_map, voxel_builder, v);
    
    // Ajouter les g�om�tries sans bo�te o� le vecteur v s'y retrouve.
    inside_list.addAll(listInsideGeometry(linear_list, v));
        
    return inside_list;
  }

  @Override
  public void initialize()
  {
    SLog.logWriteLine("Message SVoxelSpace : Construction de l'espace des g�om�tries avec voxel.");
    
    // G�n�rateur de bo�te englobante
    SBoundingBoxBuilder box_builder = new SBoundingBoxBuilder();            
    
    // S�parateur de la collection de g�om�trie
    SGeometryCollectionSplitter splitter = new SGeometryCollectionSplitter(geometry_list, SGeometryCollectionSplitter.SPLIT_BOX_AND_NO_BOX);
    
    // Obtenir la liste des g�om�tries sans bo�te englobante et l'affecter � la liste lin�aire
    linear_list = splitter.getNoBoxList();
    
    // Obtenir la liste des g�om�tries avec bo�te englobante
    List<SGeometry> list_with_box = splitter.getSplitList().get(0);
    
    // Liste des bo�tes englobantes autour des g�om�trie de la liste
    List<SBoundingBox> bounding_box_list = new ArrayList<SBoundingBox>();   
    
    // Obtenir toutes les bo�tes englobantes disponibles
    for(SGeometry g : list_with_box)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        bounding_box_list.add(box);
      else
        throw new SRuntimeException("Erreur SVoxelSpace XXX : Ceci n'est pas suppos� se produire!");
    }
    
    // S'assurer que la liste des bo�tes n'est pas vide, sinon il n'y a pas de carte de voxel � construire
    if(!bounding_box_list.isEmpty())
    {
      // Faire l'�valuation de la dimension des voxels et construire le g�n�rateur de voxel
      //-------------------------------------------------------------------------------------------------------------------------------------------------
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.ONE_FOR_ONE_ALGORITHM);
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.BIGGEST_AVERAGE_LENGHT_ALGORITHM);
      SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.MID_AVERAGE_LENGHT_ALGORITHM);
      //SVoxelDimensionEvaluator evaluator = new SVoxelDimensionEvaluator(bounding_box_list, SVoxelDimensionEvaluator.SMALLEST_AVERAGE_LENGHT_ALGORITHM); 
      
      // G�n�rateur de voxel pour la carte de voxel en construction
      voxel_builder = new SVoxelBuilder(evaluator.getDimension()); 
            
      // Construire le voxel d'extr�me � la nouvelle carte et le mettre � jour � chaque insertion de g�om�trie
      absolute_extremum_voxel = new SVoxel(0, 0, 0);
              
      // Iterer sur l'ensemble bo�tes englobante.
      // Int�grer leur voxels attitr�s avec leur g�om�trie � la carte des voxels.
      // Mettre � jour le voxel d'extr�me
      for(SBoundingBox box : bounding_box_list)
        absolute_extremum_voxel = addGeometryToMap(voxel_map, absolute_extremum_voxel, box.getGeometry(), voxel_builder.buildVoxel(box));     
      
      // Messages multiples � afficher
      SLog.logWriteLine("Message SVoxelSpace : Nombre de g�om�tries dans la carte de voxels : " + bounding_box_list.size() + " g�om�tries.");
      SLog.logWriteLine("Message SVoxelSpace : Taille des voxels : " + evaluator.getDimension() + " unit�s.");  
      SLog.logWriteLine("Message SVoxelSpace : Nombre de r�f�rence � des g�om�tries : " + evaluateNbGeometryReference(voxel_map) + " r�f�rences.");
      SLog.logWriteLine("Message SVoxelSpace : Nombre moyen de r�f�rence � des g�om�tries par voxel : " + evaluateNbGeometryReferencePerVoxel(voxel_map) + " r�f�rences/voxel.");
      
      SLog.logWriteLine();
    }//fin if
    else
    {
      // Il n'y a pas de bo�te englobante de disponible pour l'espace avec voxel
      SLog.logWriteLine("Message SVoxelSpace : Aucune g�om�trie ne poss�de de bo�te englobante! Le choix d'un espace de g�om�tries en voxel devient in�fficace.");
     
      voxel_builder = null;   // Il n'y a pas de constructeur de voxel disponible
    }
    
    SLog.logWriteLine("Message SVoxelSpace : Fin de la construction de l'espace des g�om�tries avec voxel.");
    SLog.logWriteLine();
    
    // Initialisation de l'espace des voxels compl�t�e
    space_initialized = true;
  }
  
}//fin de la classe SVoxelSpace
