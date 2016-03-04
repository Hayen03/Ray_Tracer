/**
 * 
 */
package sim.math;

/**
 * La classe <b>SImpossibleNormalizationException</b> représente une exception qui est lancée lorsqu'une normalisation est impossible à être réalisée.
 * Par exemple, un vecteur ayant une longueur nulle ne peut pas être normalisé à une longueur unitaire étant donné qu'il n'a pas d'orientation.
 * 
 * @author Simon Vézina
 * @since 2015-08-03
 * @version 2016-01-13
 */
public class SImpossibleNormalizationException extends RuntimeException {

  /**
   * La variable <b>serialVersionUID<b> correspond à un code d'identification de l'exception.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructeur de l'exception avec message d'erreur.
   * 
   * @param message - Le message de l'erreur.
   */
  public SImpossibleNormalizationException(String message)
  {
    super(message);
  }

  /**
   * Constructeur de l'exception avec message d'erreur et cause de l'exception.
   * 
   * @param message - Le message de l'erreur.
   * @param cause - La cause de l'erreur.
   */
  public SImpossibleNormalizationException(String message, Throwable cause)
  {
    super(message, cause);
  }

}//fin de la classe SImpossibleNormalisationException
