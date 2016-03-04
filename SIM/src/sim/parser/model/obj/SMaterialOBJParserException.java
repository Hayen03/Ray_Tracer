/**
 * 
 */
package sim.parser.model.obj;

/**
 * Classe qui représente une exception lancé lors de la lecture d'un matériel de format OBJ.
 * @author Simon Vézina
 * @since 2015-04-03
 * @version 2015-04-03
 */
public class SMaterialOBJParserException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -3771227664259647037L;

  /**
   * 
   */
  public SMaterialOBJParserException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   */
  public SMaterialOBJParserException(String arg0)
  {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public SMaterialOBJParserException(Throwable cause)
  {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public SMaterialOBJParserException(String message, Throwable cause)
  {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  /*
  public SMaterialOBJParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }
  */
}
