/**
 * 
 */
package sim.graphics.light;

import sim.exception.SRuntimeException;
import sim.math.SVector3d;

/**
 * L'interface <b>SAttenuatedLight</b> représentant une source de lumière qui peut s'atténuer en fonction de la distance entre la source de lumière et la position éclairée.
 * 
 * @author Simon Vézina
 * @since 2015-02-06
 * @version 2015-11-22
 */
public interface SAttenuatedLight extends SLight {

	/**
	 * Méthode pour obtenir la position de la source de lumière.
	 * 
	 * @return la position de la source de lumière.
	 */
	public SVector3d getPosition();
	
	/**
	 * Méthode pour obtenir l'orientation de la source de lumière en fonction de la position du point à illuminer.
	 * 
	 * @param position_to_illuminate - La position à illuminer
	 * @return L'orientation de la source de lumière (unitaire).
	 * @throws SRuntimeException Si la position à illuminer est située sur la source de lumière.
	 */
	public SVector3d getOrientation(SVector3d position_to_illuminate) throws SRuntimeException;
	
	/**
	 * Méthode pour obtenir le facteur d'atténuation A qui dépend de la distance d 
	 * entre le point à éclairer et la source de lumière. La formule utilisée est 
	 * <ul> A = 1 / [ Ccst + Clin*d + Cquad*d*d ]</ul>
	 * 
	 * @param position_to_illuminate - La position à éclairer.
	 * @return Le facteur d'atténuation à la position à éclairer.
	 */
	public double attenuation(SVector3d position_to_illuminate);
		
}//fin interface SAttenuatedLight
