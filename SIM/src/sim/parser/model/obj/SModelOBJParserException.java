/**
 * 
 */
package sim.parser.model.obj;

/**
 * Classe représentant une exception lancé lors de la lecture d'un fichier de format obj (Wavefront).
 * @author Simon Vézina
 * @since 2015-03-19
 * @version 2015-04-03
 */
public class SModelOBJParserException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public SModelOBJParserException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   */
  public SModelOBJParserException(String arg0)
  {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   */
  public SModelOBJParserException(Throwable arg0)
  {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   * @param arg1
   */
  public SModelOBJParserException(String arg0, Throwable arg1)
  {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   * @param arg3
   */
  /*
  public SModelOBJParserException(String arg0, Throwable arg1, boolean arg2, boolean arg3)
  {
    super(arg0, arg1, arg2, arg3);
    // TODO Auto-generated constructor stub
  }
  */
}
