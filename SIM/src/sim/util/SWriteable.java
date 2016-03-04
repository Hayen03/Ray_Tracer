/**
 * 
 */
package sim.util;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * L'interface <b>SWriteable</b> représente un objet pouvant écrire ses paramètres dans un fichier txt.
 * 
 * @author Simon Vézina
 * @since 2015-01-09
 * @version 2016-01-15
 */
public interface SWriteable {

	
  
  /**
	 * Méthode pour écrire un objet <b>SWriteable</b> dans un fichier txt en utilisant un <b>BufferedWriter</b>.
	 * 
	 * @param bw - Le buffer pour l'écriture.
	 * @throws IOException Si une erreur de type I/O a été lancée par l'objet BufferedWriter.
	 * @see BufferedWriter
	 */
	public void write(BufferedWriter bw) throws IOException;
	
}//fin interface SWriteable
