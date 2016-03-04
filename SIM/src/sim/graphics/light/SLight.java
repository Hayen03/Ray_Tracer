/**
 * 
 */
package sim.graphics.light;

import sim.graphics.SColor;
import sim.util.SWriteable;


/**
 * L'interface <b>SLight</b> repr�sentant une source de lumi�re.
 * 
 * @author Simon V�zina
 * @since 2015-01-09
 * @version 2015-11-22
 */
public interface SLight extends SWriteable {

	/**
	 * M�thode pour obtenir la couleur de la source de lumi�re.
	 * 
	 * @return La couleur de la source de lumi�re.
	 */
	public SColor getColor();
		
}//fin interface SLight
