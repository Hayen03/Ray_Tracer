/**
 * 
 */
package sim.util;

/**
 * La classe <b>SReadingException</b> représente une erreur de lecture lors de la configuration d'un objet à partir d'une lecture dans un fichier.
 * 
 * @author Simon Vézina
 * @since 2015-01-18
 * @version 2016-01-13
 */
public class SReadingException extends Exception {

  /**
   * La variable <b>serialVersionUID<b> correspond à un code d'identification de l'exception.
   */
	private static final long serialVersionUID = -2114350698798255001L;

	/**
   * Constructeur de l'exception avec message d'erreur.
   * 
   * @param message - Le message de l'erreur.
   */
  public SReadingException(String message)
  {
    super(message);
  }

  /**
   * Constructeur de l'exception avec message d'erreur et cause de l'exception.
   * 
   * @param message - Le message de l'erreur.
   * @param cause - La cause de l'erreur.
   */
  public SReadingException(String message, Throwable cause)
  {
    super(message, cause);
  }

}//fin classe SReadingException
