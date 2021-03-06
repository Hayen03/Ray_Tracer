/**
 * 
 */
package sim.graphics.light;

import sim.exception.SRuntimeException;
import sim.math.SVector3d;

/**
 * L'interface <b>SAttenuatedLight</b> repr�sentant une source de lumi�re qui peut s'att�nuer en fonction de la distance entre la source de lumi�re et la position �clair�e.
 * 
 * @author Simon V�zina
 * @since 2015-02-06
 * @version 2015-11-22
 */
public interface SAttenuatedLight extends SLight {

	/**
	 * M�thode pour obtenir la position de la source de lumi�re.
	 * 
	 * @return la position de la source de lumi�re.
	 */
	public SVector3d getPosition();
	
	/**
	 * M�thode pour obtenir l'orientation de la source de lumi�re en fonction de la position du point � illuminer.
	 * 
	 * @param position_to_illuminate - La position � illuminer
	 * @return L'orientation de la source de lumi�re (unitaire).
	 * @throws SRuntimeException Si la position � illuminer est situ�e sur la source de lumi�re.
	 */
	public SVector3d getOrientation(SVector3d position_to_illuminate) throws SRuntimeException;
	
	/**
	 * M�thode pour obtenir le facteur d'att�nuation A qui d�pend de la distance d 
	 * entre le point � �clairer et la source de lumi�re. La formule utilis�e est 
	 * <ul> A = 1 / [ Ccst + Clin*d + Cquad*d*d ]</ul>
	 * 
	 * @param position_to_illuminate - La position � �clairer.
	 * @return Le facteur d'att�nuation � la position � �clairer.
	 */
	public double attenuation(SVector3d position_to_illuminate);
		
}//fin interface SAttenuatedLight
