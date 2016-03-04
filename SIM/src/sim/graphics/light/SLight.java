/**
 * 
 */
package sim.graphics.light;

import sim.graphics.SColor;
import sim.util.SWriteable;


/**
 * L'interface <b>SLight</b> représentant une source de lumière.
 * 
 * @author Simon Vézina
 * @since 2015-01-09
 * @version 2015-11-22
 */
public interface SLight extends SWriteable {

	/**
	 * Méthode pour obtenir la couleur de la source de lumière.
	 * 
	 * @return La couleur de la source de lumière.
	 */
	public SColor getColor();
		
}//fin interface SLight
