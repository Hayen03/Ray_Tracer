/**
 * 
 */
package sim.util;

import java.io.IOException;

/**
 * L'interface <b>SReadable</b> permet à l'objet de se configurer à l'aide d'une lecture dans un fichier txt.
 * 
 * @author Simon Vézina
 * @since 2015-01-09
 * @version 2015-12-02
 */
public interface SReadable {

	/**
	 * Méthode qui permet la lecture d'un fichier de format txt pour faire l'initialisation des paramètres de l'objet SReadable.
	 * 
	 * @param sbr - Le buffer cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de type I/O est survenue lors de la lecture.
	 * @throws SInitializationException Si une erreur est survenue lors de l'initialisation de l'objet.
	 * @see SBufferedReader
	 */
	public void read(SBufferedReader sbr) throws IOException, SInitializationException;
	
	/**
   * Méthode pour obtenir le nom de l'objet implémentant l'interface <b>SReadable</b>.
   * Ce nom correspond également au <b>mot clé</b> à rechercher lors d'une lecture avec un <b>SBufferedReader</b> pour en faire la construction de l'objet en question.
   *    
   * @return Le nom de l'objet lisible.
   */
  public String getReadableName();
  
  /**
   * Méthode pour obtenir un tableau des <b>mots clés</b> permettant de définir les paramètres de l'objet implémentant l'interface <b>SReadable</b> lors de sa lecture.
   * 
   * @return Un tableau des <b>mots clés</b> des paramètres.
   */
  public String[] getReadableParameterName();
  
}//fin interface SReadable
