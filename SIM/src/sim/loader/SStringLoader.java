/**
 * 
 */
package sim.loader;

import sim.loader.SLoaderException;

/**
 * Interface représentant un constructeur d'objet (<i>loader</i>) prenant en paramètre un String.
 * Le String peut avoir plusieurs interprétation comme le nom d'un fichier, un mot clé ou une ligne dans un fichier.
 * L'objet qui implémentera cette classe devra spécifier l'interprétation du String afin d'utiliser cette information judicieusement.
 * @author Simon Vézina
 * @since 2015-03-16
 * @version 2015-04-03
 */
public interface SStringLoader {

  /**
   * Méthode qui fait la construction d'un objet dont le type dépendera de la classe implémentant l'interface SStringLoader.
   * L'utilisateur de cette fonction devra connaître le "type" de loader où 
   * @param string - Les informations sous forme String permettant au loader de fonctionner.
   * @return L'objet construit par le loader. Son type devra être caster par l'utilisateur.
   * @throws SLoaderException S'il y a eu une erreur durant le chargement du fichier.
   */
  public Object load(String string) throws SLoaderException;
  
}//fin Interface SStringLoader
