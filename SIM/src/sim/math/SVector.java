/**
 * 
 */
package sim.math;

import sim.exception.SRuntimeException;
import sim.util.SWriteable;

/**
 * Interface représentant un vecteur mathématique général avec des opérations mathématiques de base.
 * 
 * @author Simon Vézina
 * @since 2015-09-22
 * @version 2015-09-25
 */
public interface SVector extends SWriteable {

  /**
   * Méthode permettant d'effectuer l'addition mathématique entre deux vecteurs.
   * 
   * @param v - Le vecteur à additionner
   * @return Le vecteur résultat de l'addition du vecteur courant avec le vecteur v.
   * @throws SRuntimeException Si le type d'objet lançant l'appel de la fonction n'est pas de même type que l'objet passé en paramètre, car l'addition ne sera pas définie.
   */
  public SVector add(SVector v)throws SRuntimeException;
  
  /**
   * Méthode permettant d'effectuer la multiplication par un scalaire d'un vecteur avec un scalaire.
   * 
   * @param cst - La constante scalaire à multiplier avec le vecteur.
   * @return Le vecteur résultant de la multiplication par un scalaire.
   */
  public SVector multiply(double cst);
  
}//fin de l'interface SVector
