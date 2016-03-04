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
 * La classe <b>SGeometryCollectionSplitter</b> permet de s�parer en sous-groupe une collection de g�om�trie.
 * Ces g�om�tries sont regroup� en fonction de la taille des <b>bo�tes englobantes</b> les recouvrant.
 * 
 * @author Simon V�zina
 * @since 2015-12-15
 * @version 2015-12-29
 */
public class SGeometryCollectionSplitter {

  //Les codes de r�f�rence des diff�rents algorithmes disponibles
  public static final int SPLIT_BOX_AND_NO_BOX  = 0;
  public static final int SPLIT_HALF_AND_HALF = 1;
  public static final int SPLIT_AT_AVERAGE_SIZE = 2;
  public static final int SPLIT_IN_FOUR_AVERAGE_SIZE = 3;
  
  /**
   * La variable <b>box_builder</b> correspond au constructeur de bo�te englobante des g�om�tries.
   */
  private final SBoundingBoxBuilder box_builder;
  
  /**
   * La variable <b>geometry_split_list</b> correspond � la liste de listes de g�om�tries ayant �t� s�par�es.
   */
  private final List<List<SGeometry>> geometry_split_list;
  
  /**
   * La variable <b>geometry_no_box_list</b> correspond � la liste de g�om�tries n'ayant pas de bo�te englobante.
   * Ces g�om�tries ne se retrouveront donc pas dans la liste <b>geometry_split_list</b>.
   */
  private final List<SGeometry> geometry_no_box_list;
  
  /**
   * Constructeur d'un s�parateur de collection de g�om�trie par d�faut.
   * 
   * @param list - La liste de g�om�trie � s�parer.
   * @param split_code - Le code de l'algorithme de s�paration utilis� par le s�parateur.
   * @throws SConstructorException Si le code de s�paration (split_code) n'est pas valide.
   */
  public SGeometryCollectionSplitter(List<SGeometry> list, int split_code) throws SConstructorException
  {
    box_builder = new SBoundingBoxBuilder();
    geometry_split_list = new ArrayList<List<SGeometry>>();
    geometry_no_box_list = new ArrayList<SGeometry>();
    
    // Appliquer l'algorithme de s�paration demand�
    switch(split_code)
    {
      case SPLIT_BOX_AND_NO_BOX :       splitBoxAndNoBox(list); break;
      case SPLIT_HALF_AND_HALF :        splitHalfAndHalf(list); break;
      case SPLIT_AT_AVERAGE_SIZE :      splitAtAverageSize(list); break;
      case SPLIT_IN_FOUR_AVERAGE_SIZE : splitInFourAverageSize(list); break;
      
      default : throw new SConstructorException("Erreur SGeometryCollectionSplitter 001 : Le code de r�f�rence d'algorithme " + split_code + " n'est pas reconnu.");
    }
  }

  /**
   * M�thode pour obtenir la liste des listes de g�om�tries apr�s le fractionnement de l'ensemble.
   * 
   * @return La liste des listes de g�om�tries.
   */
  public List<List<SGeometry>> getSplitList()
  {
    return geometry_split_list;
  }
  
  /**
   * M�thode pour obtenir la liste des g�om�tries n'�tant pas fractionn�es dans les ensembles
   * puisque ces g�om�tries ne poss�de pas de bo�te englobante.
   * 
   * @return La liste des g�om�tries sans bo�te englobante.
   */
  public List<SGeometry> getNoBoxList()
  {
    return geometry_no_box_list;
  }
  
  /**
   * M�thode qui effectue la s�paration d'une liste de g�om�trie en deux cat�gories : 
   * <ul>- avec bo�te englobante</ul>
   * <ul>- sans bo�te englobante</ul>
   * 
   * @param list - La liste des g�om�tries � s�parer.
   */
  private void splitBoxAndNoBox(List<SGeometry> list)
  {
    List<SGeometry> list_with_box = new ArrayList<SGeometry>();
    
    // Iterer sur l'ensemble de la liste et v�rifier s'il y a une bo�te qui peut �tre construite
    for(SGeometry g : list)
      if(box_builder.buildBoundingBox(g) != null)
        list_with_box.add(g);
      else
        geometry_no_box_list.add(g);
    
    geometry_split_list.add(list_with_box);
  }
  
  /**
   * M�thode qui effectue la s�paration d'une liste de g�om�trie en deux ensembles �gaux tel que
   * les plus petites bo�tes englobantes seront dans la 1i�re liste et les plus grandes bo�tes
   * englobantes seront dans la 2i�me liste.
   * 
   * @param list - La liste des g�om�tries � s�parer.
   */
  private void splitHalfAndHalf(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de bo�tes englobantes et la liste des g�om�tries sans bo�tes englobante
    for(SGeometry g : list)
    {
      SBoundingBox box = box_builder.buildBoundingBox(g);
      
      if(box != null)
        list_box.add(box);
      else
        geometry_no_box_list.add(g);
    }
    
    // Trier la liste des bo�tes englobantes en fonction de leur taille (selon l'impl�mentation de comparable) 
    Collections.sort(list_box);
    
    // Remplir les deux listes avec la moiti� de la liste tri�
    List<SGeometry> list_1 = new ArrayList<SGeometry>();
    List<SGeometry> list_2 = new ArrayList<SGeometry>();
    
    int middle = list_box.size() / 2;
    
    // Remplir la 1i�re liste
    for(int i = 0; i < middle; i++)
      list_1.add(list_box.get(i).getGeometry());
    
    // Remplir la 2i�me liste
    for(int i = middle; i < list_box.size(); i++)
      list_2.add(list_box.get(i).getGeometry());
        
    geometry_split_list.add(list_1);
    geometry_split_list.add(list_2);
  }
  
  /**
   * M�thode qui effectue la s�paration d'une liste de g�om�tries en deux ensembles
   * o� le point de s�paration est la taille moyenne de la bo�te englobante de la liste.
   * 
   * @param list - La liste des g�om�tries.
   */
  private void splitAtAverageSize(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de bo�tes englobantes et la liste des g�om�tries sans bo�tes englobante
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
      // Trier la liste des bo�tes englobantes en fonction de leur taille (selon l'impl�mentation de comparable) 
      Collections.sort(list_box);
      
      // �valuer la taille moyenne des bo�tes
      double average_lenght = averageLenght(list_box);
      
      // S�parer en deux listes : Inf�rieur � la moyenne et sup�rieur � la moyenne
      List<SBoundingBox> list_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_up = new ArrayList<SBoundingBox>();
      
      for(SBoundingBox b : list_box)
        if(b.getMaxLenght() < average_lenght)
          list_down.add(b);
        else
          list_up.add(b);
      
      // Construire les listes de g�om�tries frationn�es
      geometry_split_list.add(fillGeometryList(list_down));
      geometry_split_list.add(fillGeometryList(list_up));
    }
  }
  
  /**
   * M�thode qui effectue la s�paration d'une liste de g�om�tries en deux groupes s�par�s
   * � la taille moyenne des bo�tes de la liste et de ces deux groupes en deux sous-groupes de la m�me fa�on.
   * On y retrouvera ainsi 4 groupes.
   * 
   * @param list - La liste des g�om�tries � s�parer.
   */
  private void splitInFourAverageSize(List<SGeometry> list)
  {
    List<SBoundingBox> list_box = new ArrayList<SBoundingBox>();
    
    // Remplir la liste de bo�tes englobantes et la liste des g�om�tries sans bo�tes englobante
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
      // Trier la liste des bo�tes englobantes en fonction de leur taille (selon l'impl�mentation de comparable) 
      Collections.sort(list_box);
      
      // �valuer la taille moyenne des bo�tes
      double average_lenght = averageLenght(list_box);
      
      // S�parer en deux listes : Inf�rieur � la moyenne et sup�rieur � la moyenne
      List<SBoundingBox> list_down = new ArrayList<SBoundingBox>();
      List<SBoundingBox> list_up = new ArrayList<SBoundingBox>();
      
      for(SBoundingBox b : list_box)
        if(b.getMaxLenght() < average_lenght)
          list_down.add(b);
        else
          list_up.add(b);
      
      // �valuer la taille moyenne des bo�tes des deux sous-groupe
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
      
      // Construire les listes de g�om�tries frationn�es
      geometry_split_list.add(fillGeometryList(list_down_down));
      geometry_split_list.add(fillGeometryList(list_down_up));
      geometry_split_list.add(fillGeometryList(list_up_down));
      geometry_split_list.add(fillGeometryList(list_up_up));
    } 
  }
  
  /**
   * M�thode pour obtenir la taille moyenne des bo�tes englobantes d'une liste de bo�tes.
   * 
   * @param list - La liste des bo�tes englobantes.
   * @return La taille moyenne des bo�tes englobantes.
   */
  private double averageLenght(List<SBoundingBox> list)
  {
    double average_lenght = 0.0;
    
    for(SBoundingBox b : list)
      average_lenght += b.getMaxLenght();
      
    return average_lenght / list.size();
  }
  
  /**
   * M�thode qui fait la construction d'une liste de g�om�tries � partir d'une liste de bo�tes englobantes
   * o� une r�f�rence � une g�om�trie s'y retrouve.
   * 
   * @param list - La liste des bo�tes englobantes.
   * @return La liste des g�om�tries.
   */
  private List<SGeometry> fillGeometryList(List<SBoundingBox> list)
  {
    List<SGeometry> geometry_list = new ArrayList<SGeometry>();
    
    for(SBoundingBox b : list)
      geometry_list.add(b.getGeometry());
    
    return geometry_list;
  }
  
}//fin de la classe SGeometryCollectionSplitter
