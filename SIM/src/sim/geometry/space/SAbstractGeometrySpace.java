/**
 * 
 */
package sim.geometry.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.math.SVector3d;

/**
 * La classe <b>SAbstractGeometrySpace</b> représente un espace à géométrie abstrait.
 * 
 * @author Simon Vézina
 * @since 2015-01-11
 * @version 2016-01-01
 */
public abstract class SAbstractGeometrySpace implements SGeometrySpace{

  //Mode de sélection du type d'espace des géométries
  public static final String[] TYPE_OF_SPACE = {"linear", "voxel", "multi_voxel"};
  public static final int LINEAR = 0;
  public static final int VOXEL = 1;
  public static final int MULTI_VOXEL = 2;
  
  /**
   * La variable <b>intersection_test_count</b> correspond au nombre de tests d'intersection qui ont été réalisés
   * depuis la création d'un espace des géométries.
   */
  private static int intersection_test_count = 0;
  
  /**
   * La variable <b>geometry_list</b> correspond à la liste complète des géométries dans l'espace des géométries.
   */
  protected final List<SGeometry> geometry_list;
  
  /**
   * La variable <b>space_initialized</b> permet de définir si l'espace des voxels a été initialisé.
   */
  protected boolean space_initialized;
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur par défaut d'un espace de géométrie abstraite.
   */
  public SAbstractGeometrySpace()
  {
    geometry_list = new ArrayList<SGeometry>();
    
    space_initialized = false;
  }
  
  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public void addGeometry(SGeometry geometry)
  {
    geometry_list.add(geometry);
    
    space_initialized = false;
  }
  
	@Override
	public void addGeometry(List<SGeometry> list)
	{
		for(SGeometry g : list)
			addGeometry(g);
	}

	/**
   * Méthode qui évalue toutes les intersections entre un rayon et les différentes géométries d'une liste ne dépassant pas une valeur de temps maximal.
   * Les rayons ayant intersectés seront retournés dans une <b>liste trier en ordre croissant de temps</b> (du plus près au plus éloigé).
   *
   * @param list - La liste des géométries.
   * @param ray - Le rayon à intersecter avec les géométries de l'espace.
   * @param t_max - Le temps maximal.
   * @return Une liste des différentes intersections avec les primitives en ordre croissant de temps. Si la <b>liste est vide</b>, il n'y a <b>pas eu d'intersection</b>.
   */
  protected List<SRay> intersections(List<SGeometry> list, SRay ray, double t_max)
  {
    // Compter le nombre de tests d'intersection qui seront effectués lors de l'appel de cette méthode
    increaseIntersectionCount(list.size());
    
    // Liste des rayons intersectés à générer
    List<SRay> return_list = new ArrayList<SRay>();  
    
    // Itération sur l'ensemble des géométries de la liste
    for(SGeometry g : list)
    {
      // Évaluer l'intersection entre le rayon et la géométrie
      SRay result_ray = g.intersection(ray);  
          
      if(result_ray.asIntersected())    //Vérification de l'intersection
        if(result_ray.getT() < t_max)   //Vérification de ne pas dépasser t_max
          return_list.add(result_ray);
    }
        
    //Trier la list en ordre croissant de valeur t (ce que l'implémentation de Comparable fait pour SRay)
    Collections.sort(return_list); 
        
    return return_list;  
  }
  
  /**
   * <p>
   * Méthode pour fusionner deux listes d'intersections en une nouvelle liste.
   * La nouvelle liste des intersections sera <b>triée en ordre croissant de temps</b>.
   * </p>
   * 
   * <p>
   * Il est important de préciser que cette méthode ne vérifie pas que les rayons à 
   * l'intérieur des deux listes sont des rayons ayant réalisés des intersection.
   * </p>
   * 
   * @param list1 - La première liste à fusionner.
   * @param list2 - La deuxième liste à fusionner.
   * @return La fusion des deux listes en <b>trié en ordre croissant de temps</b>.
   */
  protected List<SRay> mergeIntersections(List<SRay> list1, List<SRay> list2)
  {
    List<SRay> return_list = new ArrayList<SRay>();  
    
    return_list.addAll(list1);
    return_list.addAll(list2);
    
    Collections.sort(return_list); 
    
    return return_list;
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
   * Cette méthode sera utile lors du lancé d'un rayon d'ombre où l'intersection avec une géométrie transparente
   * ne bloquera pas entièrement la lumière (ombre partielle).
   * </p>
   *
   * @param list - La liste des géométries.
   * @param ray - Le rayon à intersecter avec les géométries de l'espace.
   * @param t_max - Le temps maximal.
   * @return Une liste des géométries transparentes intersectées en <b>ordre décroissant</b> débutant par une géométrie opaque s'il y a eu lieu. La liste sera <b>vide</b> s'il n'y a <b>aucune intersection</b> sur le temps maximal.
   */
  protected List<SRay> nearestOpaqueIntersection(List<SGeometry> list, SRay ray, final double t_max)
  {
    // Obtenir la liste complète des intersections triées en ordre croissant
    List<SRay> list_intersection = intersections(list, ray, t_max);
    
    // Iterer sur l'ensemble des intersections et conserver celle 
    // où il y a des géométries transparentes et une seule opaque s'il y en a une
    List<SRay> return_list = new ArrayList<SRay>();
    
    for(SRay r : list_intersection)
    {
      return_list.add(r);   // ajouter cette intersection
      
      // Si la géométrie intersecté n'est pas transparente,
      // ce sera la dernière intersection ajoutée à la liste
      // car elle sera la seule intersection opaque de la liste.
      if(!r.getGeometry().isTransparent())
      {
        Collections.reverse(return_list);       // Inverser l'ordre en ordre décroissant
        return return_list;                     // retourner une liste avec géométrie opaque
      }
    }
    
    Collections.reverse(return_list);           // Inverser l'ordre en ordre décroissant
    return return_list;                         // retourner une liste sans géométrie opaque
    
    // ANCIENNE VERSION ... BEAUCOUP PLUS COMPLIQUÉE !!!
    
    /*
    SRay nearest_opaque_intersection = ray;                             //l'intersection opaque la plus près ... initialement, il n'y en a pas !
    
    List<SRay> list_transparent_intersection = new LinkedList<SRay>();  //liste des intersections avec géométrie transparente
    
    //- Conserver les intersections transparentes 
    //- Garder en gardant en mémoire l'intersection avec la géométrie opaque la plus près
    for(SRay r : list_intersection)
      if(r.getGeometry().isTransparent())                  //Si la géométrie est transparente
        list_transparent_intersection.add(r);
      else                                                 //Si la géométrie est opaque
        if(r.getT() < nearest_opaque_intersection.getT())     //Si l'on trouve une géométrie opaque plus près que celle trouvée avant
          nearest_opaque_intersection = r;
    
    //Construire la liste officielle des intersections transparentes en gardant  
    //uniquement les intersections transparentes avant celle qui est opaque et  
    //mettre l'intersection opaque dans la liste s'il y en a eu une.
    //IMPORTANT : Il faut trier la liste en ordre décroissant par la suite (la géométrie opaque sera la première de la liste, si elle est présente).
    if(nearest_opaque_intersection.asIntersected())       //S'il y a pas eu d'intersection opaque
    { 
      List<SRay> list_return = new LinkedList<SRay>();    //Nouvelle liste à retourner
      
      for(SRay r : list_transparent_intersection)
        if(r.getT() < nearest_opaque_intersection.getT()) //Ajouter les géométries transparente plus près que l'intersection opaque
          list_return.add(r);
      
      list_return.add(nearest_opaque_intersection);     //Ajouter l'intersection opaque
      
      Collections.sort(list_return);              //Trier dans l'ordre croissant
      Collections.reverse(list_return);           //Inverser l'ordre en ordre décroissant
      return list_return;
    }
    else
    {
      Collections.sort(list_transparent_intersection);      //Trier dans l'ordre croissant
      Collections.reverse(list_transparent_intersection);   //Inverser l'ordre en ordre décroissant
      return list_transparent_intersection;
    }   
    */
  }
  
  /**
   * Méthode qui effectue la fusion de deux listes de rayon ayant réalisé le test de l'intersection
   * avec la géométrie opaque la plus près. Ces deux listes seront fusionnées, les premières intersections
   * avec une géométrie transparente seront conservée jusqu'à la 1ière géométrie opaque et cette séquence
   * sera finalement inversée.
   * 
   * @param list1 - La 1ière liste à fusionner.
   * @param list2 - La 2ième liste à fusionner.
   * @return Une nouvelle liste déterminant la 1ière géométrie opaque intersectée.
   */
  protected List<SRay> mergeNearestOpaqueIntersection(List<SRay> list1, List<SRay> list2)
  {
    // Fusionner les deux listes d'intersection en liste triée
    List<SRay> merge_list = mergeIntersections(list1, list2);
    
    // Iterer sur la liste fusionner et ajouter les intersection transparente
    // et garder seulement une seule intersection avec géométrie opaque
    List<SRay> return_list = new ArrayList<SRay>();
    
    for(SRay r : merge_list)
    {
      return_list.add(r);
      
      if(!r.getGeometry().isTransparent())
      {
        Collections.reverse(return_list);       // Inverser l'ordre en ordre décroissant
        return return_list;                     // retourner une liste avec géométrie opaque
      }
    }
    
    Collections.reverse(return_list);           // Inverser l'ordre en ordre décroissant
    return return_list;                         // retourner une liste sans géométrie opaque
  }
  
  /**
   * Méthode qui évalue l'ensemble des géométries d'une liste où un vecteur v se retrouve à l'intérieur.
   * 
   * @param list - La liste des géométries.
   * @param v - La position du vecteur.
   * @return La liste des géométries où le vecteur v se retrouve à l'intérieur.
   */
  protected List<SGeometry> listInsideGeometry(List<SGeometry> list, SVector3d v)
  {
    // Liste des géométries où le vecteur v se retrouvera à l'intérieur
    List<SGeometry> return_list = new ArrayList<SGeometry>();  
    
    //Itération sur l'ensemble des géométries 
    if(list != null)
      for(SGeometry g : list)
        if(g.isClosedGeometry())     // la géométrie doit être fermée  
          if(g.isInside(v))          // le vecteur v doit se retrouver à l'intérieur de la géométrie
            return_list.add(g);
          
    return return_list;
  }
  
  @Override
  public void initialize()
  {
    space_initialized = true;
  }
  
  @Override
  public String toString()
  {
    return new String("Nb geometry : " + geometry_list.size());
  }
  
  /**
   * Méthode pour obtenir le nombre de tests d'intersection qui ont été réalisés.
   * 
   * @return Le nombre de tests d'intersection calculés.
   */
  public static int getIntersectionTestCount()
  {
    return intersection_test_count;
  }
  
  /**
   * Méthode pour réinitialiser le comptage des tests d'intersection.
   */
  public static synchronized void resetIntersectinonTestCount()
  {
    intersection_test_count = 0;
  }
  
  /**
   * Méthode pour augmenter le nombre de tests d'intersection réalisés.
   * Cette méthode sous cette forme est nécessaire afin d'éviter des problèmes lors d'un usage de cette classe en mode <i>multiprocessing</i>.
   * 
   * @param value - Le nombre de tests d'intersection à ajouter.
   */
  private static synchronized void increaseIntersectionCount(int value)
  {
    intersection_test_count += value;
  }
  
}//fin classe abstraite SAbstractGeometrySpace
