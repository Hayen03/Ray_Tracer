/**
 * 
 */
package sim.util;

/**
 * La classe <b>SInitializationException</b> représente une exception lors d'une initialisation erronée d'un objet.
 * 
 * @author Simon Vézina
 * @since 2015-11-18
 * @version 2016-01-13
 */
public class SInitializationException extends Exception {

  /**
   * La variable <b>serialVersionUID<b> correspond à un code d'identification de l'exception.
   */
  private static final long serialVersionUID = 2751830076897809540L;

  /**
   * Constructeur de l'exception avec message d'erreur.
   * 
   * @param message - Le message de l'erreur.
   */
  public SInitializationException(String message)
  {
    super(message);
  }

  /**
   * Constructeur de l'exception avec message d'erreur et cause de l'exception.
   * 
   * @param message - Le message de l'erreur.
   * @param cause - La cause de l'erreur.
   */
  public SInitializationException(String message, Throwable cause)
  {
    super(message, cause);
  }

}//fin de la classe SInitializationException
