/**
 * 
 */
package sim.exception;

/**
 * La classe <b>SNoImplementationException</b> représente une exception lancé lorsqu'une méthode n'est pas implémentée.
 * 
 * @author Simon Vézina
 * @since 2015-01-25
 * @version 2016-01-13
 */
public class SNoImplementationException extends RuntimeException {

  /**
   * La variable <b>serialVersionUID<b> correspond à un code d'identification de l'exception.
   */
	private static final long serialVersionUID = -9055388404780943775L;

	/**
   * Constructeur de l'exception avec message d'erreur.
   * 
   * @param message - Le message de l'erreur.
   */
	public SNoImplementationException(String message)
	{
		super(message);
	}

	/**
   * Constructeur de l'exception avec message d'erreur et cause de l'exception.
   * 
   * @param message - Le message de l'erreur.
   * @param cause - La cause de l'erreur.
   */
	public SNoImplementationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}//fin de la classe SNoImplementationException
