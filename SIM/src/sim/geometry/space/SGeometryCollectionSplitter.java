/**
 * 
 */
package sim.geometry.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.exception.SConstructorException;
import sim.geometry.SGeometry;

/**
 * La classe <b>SGeometryCollectionSplitter</b> permet de séparer en sous-groupe une collection de géométrie.
 * Ces géométries sont regroupé en fonction de la taille des <b>boîtes englobantes</b> les recouvrant.
 * 
 * @author Simon Vézina
 * @since 2015-12-15
 * @version 2015-12-29
 */
public class SGeometryCollectionSplitter {

  //Les codes de référence des différents algorithmes disponibles
  public static final int SPLIT_BOX_AND_NO_BOX  = 0;
  public static final int SPLIT_HALF_AND_HALF = 1;
  public static final int SPLIT_AT_AVERAGE_SIZE = 2;
  public static final int SPLIT_IN_FOUR_AVERAGE_SIZE = 3;
  
  /**
   * La variable <b>box_builder</b> correspond au constructeur de boîte englobante des géométries.
   */
  private final SBoundingBoxBuilder box_builder;
  
  /**
   * La variable <b>geometry_split_list</b> correspond à la liste de listes de géométries ayant été séparées.
   */
  private final List<List<SGeometry>> geometry_split_list;
  
  /**
   * La variable <b>geometry_no_box_list</b> correspond à la liste de géométries n'ayant pas de boîte englobante.
   * Ces géométries ne se retrouveront donc pas dans la liste <b>geometry_split_list</b>.
   */
  private final List<SGeometry> geometry_no_box_list;
  
  /**
   * Constructeur d'un séparateur de collection de géométrie par défaut.
   * 
   * @param list - La liste de géométrie à séparer.
   * @param split_code - Le code de l'algorithme de séparation utilisé par le séparateur.
   * @throws SConstructorException Si le code de séparation (split_code) n'est pas valide.
   */
  public SGeometryCollectionSplitter(List<SGeometry> list, int split_code) throws SConstructorException
  {
    box_builder = new SBoundingBoxBuilder();
    geometry_split_list = new ArrayList<List<SGeometry>>();
    geometry_no_box_list = new ArrayList<SGeometry>();
    
    // Appliquer l'algorithme de séparation demandé
    switch(split_code)
    {
      case SPLIT_BOX_AND_NO_BOX :       splitBoxAndNoBox(list); break;
      case SPLIT_HALF_AND_HALF :        splitHalfAndHalf(list); break;
      case SPLIT_AT_AVERAGE_SIZE :      splitAtAverageSize(list); break;
      case SPLIT_IN_FOUR_AVERAGE_SIZE : splitInFourAverageSize(list); break;
      
      default : throw new SConstructorException("Erreur SGeometryCollectionSplitter 001 : Le code de référence d'algorithme " + split_code + " n'est pas reconnu.");
    }
  }

  /**
   * Méthode pour obtenir la liste des listes de géométries après le fractionnement de l'ensemble.
   * 
   * @return La liste des listes de géométries.
   */
  public List<List<SGeometry>> getSplitList()
  {
    return geometry_split_list;
  }
  
  /**
   * Méthode pour obtenir la liste des géométries n'étant pas fractionnées dans les ensembles
   * puisque ces géométries ne possède pas de boîte englobante.
   * 
   * @return La liste des géométries sans boîte englobante.
   */
  public List<SGeometry> getNoBoxList()
  {
    return geometry_no_box_list;
  }
  
  /**
   * Méthode qui effectue la séparation d'une liste de géométrie en deux catégories : 
   * <ul>- avec boîte englobante</ul>
   * <ul>- sans boîte englobante</ul>
   * 
   * @param list - La liste des géométries à séparer.
   */
  private void splitBoxAndNoBox(List<SGeometry> list)
  {
    List<SGeometry> list_with_box = new ArrayList<SGeometry>();
    
    // Iterer sur l'ensemble de la liste et vérifier s'il y a une boîte qui peut être construite
    for(SGeometry g : list)
      if(box_builder.buildBoundingBox(g) != null)
        list_with_box.add(g);
      else
        geometry_no_box_list.add(g);
    
    geometry_split_list.add(list_with_box);
  }
  
  /**
   * Méthode qui effectue la séparation d'une liste de géométrie en deux ensembles égaux tel que
   * les plus petites boîtes englobantes seront dans la 1ière liste et les plus grandes boîtes
   * englobantes seront dans la 2ième liste.
   * 
   * @param list - La liste des géométries à séparer.
   */
  private void splitHalfAndHalf(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de boîtes englobantes et la liste des géométries sans boîtes englobante
    for(SGeometry g : list)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        list_box.add(box);
      else
        geometry_no_box_list.add(g);
    }
    
    // Trier la liste des boîtes englobantes en fonction de leur taille (selon l'implémentation de comparable) 
    Collections.sort(list_box);
    
    // Remplir les deux listes avec la moitié de la liste trié
    List<SGeometry> list_1 = new ArrayList<SGeometry>();
    List<SGeometry> list_2 = new ArrayList<SGeometry>();
    
    int middle = list_box.size() / 2;
    
    // Remplir la 1ière liste
    for(int i = 0; i < middle; i++)
      list_1.add(list_box.get(i).getGeometry());
    
    // Remplir la 2ième liste
    for(int i = middle; i < list_box.size(); i++)
      list_2.add(list_box.get(i).getGeometry());
        
    geometry_split_list.add(list_1);
    geometry_split_list.add(list_2);
  }
  
  /**
   * Méthode qui effectue la séparation d'une liste de géométries en deux ensembles
   * où le point de séparation est la taille moyenne de la boîte englobante de la liste.
   * 
   * @param list - La liste des géométries.
   */
  private void splitAtAverageSize(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de boîtes englobantes et la liste des géométries sans boîtes englobante
    for(SGeometry g : list)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        list_box.add(box);
      else
        geometry_no_box_list.add(g);
    }
    
    if(!list_box.isEmpty())
    {
      // Trier la liste des boîtes englobantes en fonction de leur taille (selon l'implémentation de comparable) 
      Collections.sort(list_box);
      
      // Évaluer la taille moyenne des boîtes
      double average_lenght = averageLenght(list_box);
      
      // Séparer en deux listes : Inférieur à la moyenne et supérieur à la moyenne
      List<SBoundingBox> list_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_up = new ArrayList<SBoundingBox>();
      
      for(SBoundingBox b : list_box)
        if(b.getMaxLenght() < average_lenght)
          list_down.add(b);
        else
          list_up.add(b);
      
      // Construire les listes de géométries frationnées
      geometry_split_list.add(fillGeometryList(list_down));
      geometry_split_list.add(fillGeometryList(list_up));
    }
  }
  
  /**
   * Méthode qui effectue la séparation d'une liste de géométries en deux groupes séparés
   * à la taille moyenne des boîtes de la liste et de ces deux groupes en deux sous-groupes de la même façon.
   * On y retrouvera ainsi 4 groupes.
   * 
   * @param list - La liste des géométries à séparer.
   */
  private void splitInFourAverageSize(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de boîtes englobantes et la liste des géométries sans boîtes englobante
    for(SGeometry g : list)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        list_box.add(box);
      else
        geometry_no_box_list.add(g);
    }
    
    if(!list_box.isEmpty())
    {
      // Trier la liste des boîtes englobantes en fonction de leur taille (selon l'implémentation de comparable) 
      Collections.sort(list_box);
      
      // Évaluer la taille moyenne des boîtes
      double average_lenght = averageLenght(list_box);
      
      // Séparer en deux listes : Inférieur à la moyenne et supérieur à la moyenne
      List<SBoundingBox> list_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_up = new ArrayList<SBoundingBox>();
      
      for(SBoundingBox b : list_box)
        if(b.getMaxLenght() < average_lenght)
          list_down.add(b);
        else
          list_up.add(b);
      
      // Évaluer la taille moyenne des boîtes des deux sous-groupe
      double average_lenght_down = averageLenght(list_down);
      double average_lenght_up = averageLenght(list_up);
      
      List<SBoundingBox> list_down_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_down_up = new ArrayList<SBoundingBox>();
      
      List<SBoundingBox> list_up_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_up_up = new ArrayList<SBoundingBox>();
      
      // Remplir les sous-groupes de DOWN
      for(SBoundingBox b : list_down)
        if(b.getMaxLenght() < average_lenght_down)
          list_down_down.add(b);
        else
          list_down_up.add(b);

      // Remplir les sous-groupes de UP
      for(SBoundingBox b : list_up)
        if(b.getMaxLenght() < average_lenght_up)
          list_up_down.add(b);
        else
          list_up_up.add(b);
      
      // Construire les listes de géométries frationnées
      geometry_split_list.add(fillGeometryList(list_down_down));
      geometry_split_list.add(fillGeometryList(list_down_up));
      geometry_split_list.add(fillGeometryList(list_up_down));
      geometry_split_list.add(fillGeometryList(list_up_up));
    } 
  }
  
  /**
   * Méthode pour obtenir la taille moyenne des boîtes englobantes d'une liste de boîtes.
   * 
   * @param list - La liste des boîtes englobantes.
   * @return La taille moyenne des boîtes englobantes.
   */
  private double averageLenght(List<SBoundingBox> list)
  {
    double average_lenght = 0.0;
    
    for(SBoundingBox b : list)
      average_lenght += b.getMaxLenght();
      
    return average_lenght / list.size();
  }
  
  /**
   * Méthode qui fait la construction d'une liste de géométries à partir d'une liste de boîtes englobantes
   * où une référence à une géométrie s'y retrouve.
   * 
   * @param list - La liste des boîtes englobantes.
   * @return La liste des géométries.
   */
  private List<SGeometry> fillGeometryList(List<SBoundingBox> list)
  {
    List<SGeometry> geometry_list = new ArrayList<SGeometry>();
    
    for(SBoundingBox b : list)
      geometry_list.add(b.getGeometry());
    
    return geometry_list;
  }
  
}//fin de la classe SGeometryCollectionSplitter
