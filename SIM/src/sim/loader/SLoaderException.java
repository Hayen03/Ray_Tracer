/**
 * 
 */
package sim.loader;

/**
 * Classe qui représente une exception lancée lors d'une erreur durant un loading.
 * @author Simon Vézina
 * @since 2015-03-28
 * @version 2015-03-28
 */
public class SLoaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3741581997524351290L;

	/**
	 * 
	 */
	public SLoaderException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public SLoaderException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public SLoaderException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SLoaderException(String arg0, Throwable arg1) {
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
	public SLoaderException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	*/
}
