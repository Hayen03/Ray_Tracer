/**
 * 
 */
package sim.util;

import sim.exception.SRuntimeException;

/**
 * L'interface <b>SReader</b> représente un objet pouvant effectuer la lecture des propriétés d'un objet construit 
 * à partir d'information contenue dans un fichier.
 * 
 * @author Simon Vézina
 * @since 2015-11-07
 * @version 2015-11-07
 */
public interface SReader {

  /**
   * Méthode permettant d'obtenir un objet du type spécifié par la classe implémentant l'interface <b>SReader</b>
   * dont les propriétés ont été lues dans un fichier.
   * 
   * @return L'objet lu par le lecteur.
   * @throws SRuntimeException S'il n'y a pas eu d'objet lu par le lecteur.
   */
  public Object getValue() throws SRuntimeException;
  
  /**
   * Méthode qui détermine si le lecteur a effectué une lecture d'un objet.
   * 
   * @return <b>true</b> si le lecteur a effectué une lecture d'un objet et <b>false</b> sinon.
   */
  public boolean asRead();
  
}//fin de l'interface SReader
