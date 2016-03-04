/**
 * 
 */
package sim.parser.model.agp;

import java.util.ArrayList;

/**
 * Classe qui représente une séquence de point.
 * 
 * @author Simon Vézina
 * @since 2015-08-01
 * @version 2015-08-01
 */
public class SPointSequence {

  private final ArrayList<SPoint> sequence; //la séquence de point
  
  /**
   * Constructeur d'une séquence de point avec un nombre d'éléments à l'intérieur qui devront être définis. 
   * @param nb - Le nombre de points à l'intérieur de la séquence.
   */
  public SPointSequence(int nb)
  {
    sequence = new ArrayList<SPoint>();
    
    //Ajouter des points non défini à l'intérieur de la séquence
    for(int i=0; i<nb; i++)
      sequence.add(new SPoint());
  }

  /**
   * Méthode pour obtenir un point à un index spécifique de la séquence de point. L'index 0 correspond
   * au premier point.
   * @param index - L'index du point.
   * @return Le point à l'index spécifique
   * @throws IndexOutOfBoundsException Si l'index est à l'extérieur des bornes suivantes (index < 0 || index >= size()) 
   */
  public SPoint getPoint(int index)throws IndexOutOfBoundsException 
  {
    return sequence.get(index);
  }
 
  /**
   * Méthode pour obtenir la taille de la séquence.
   * @return La taille de la séquence.
   */
  public int size()
  {
    return sequence.size();
  }
  
  /**
   * Méthode pour définir la coordonnée x d'un point à un index particulier.
   * @param index - L'index du point dont la coordonnée x sera modifiée.
   * @throws IndexOutOfBoundsException Si l'index est à l'extérieur des bornes suivantes (index < 0 || index >= size()) 
   */
  public void setX(int index, double x)throws IndexOutOfBoundsException 
  {
    sequence.get(index).setX(x);
  }
  
  /**
   * Méthode pour définir la coordonnée y d'un point à un index particulier.
   * @param index - L'index du point dont la coordonnée y sera modifiée.
   * @throws IndexOutOfBoundsException Si l'index est à l'extérieur des bornes suivantes (index < 0 || index >= size()) 
   */
  public void setY(int index, double y)throws IndexOutOfBoundsException 
  {
    sequence.get(index).setY(y);
  }
  
  /**
   * Méthode pour définir la coordonnée z d'un point à un index particulier.
   * @param index - L'index du point dont la coordonnée z sera modifiée.
   * @throws IndexOutOfBoundsException Si l'index est à l'extérieur des bornes suivantes (index < 0 || index >= size()) 
   */
  public void setZ(int index, double z)throws IndexOutOfBoundsException 
  {
    sequence.get(index).setZ(z);
  }
  
}//fin de la classe SPointSequence
