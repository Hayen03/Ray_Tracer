/**
 * 
 */
package sim.math;

import sim.exception.SRuntimeException;
import sim.util.SWriteable;

/**
 * Interface repr�sentant un vecteur math�matique g�n�ral avec des op�rations math�matiques de base.
 * 
 * @author Simon V�zina
 * @since 2015-09-22
 * @version 2015-09-25
 */
public interface SVector extends SWriteable {

  /**
   * M�thode permettant d'effectuer l'addition math�matique entre deux vecteurs.
   * 
   * @param v - Le vecteur � additionner
   * @return Le vecteur r�sultat de l'addition du vecteur courant avec le vecteur v.
   * @throws SRuntimeException Si le type d'objet lan�ant l'appel de la fonction n'est pas de m�me type que l'objet pass� en param�tre, car l'addition ne sera pas d�finie.
   */
  public SVector add(SVector v)throws SRuntimeException;
  
  /**
   * M�thode permettant d'effectuer la multiplication par un scalaire d'un vecteur avec un scalaire.
   * 
   * @param cst - La constante scalaire � multiplier avec le vecteur.
   * @return Le vecteur r�sultant de la multiplication par un scalaire.
   */
  public SVector multiply(double cst);
  
}//fin de l'interface SVector
