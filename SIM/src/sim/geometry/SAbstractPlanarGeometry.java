/**
 * 
 */
package sim.geometry;

import sim.graphics.SPrimitive;
import sim.math.SVector3d;

/**
 * La classe abstraite <b>SAbstractPlanarGeometry</b> représentant une géométrie planaire. 
 * Cette classe permet l'évaluation du sens adéquat de la normale à la surface lors d'une intersection.
 * 
 * @author Simon Vézina
 * @since 2015-02-19
 * @version 2015-12-14
 */
public abstract class SAbstractPlanarGeometry extends SAbstractGeometry {

  /**
	 * Constructeur vide d'une géométrie abstraite planaire. 
	 */
	public SAbstractPlanarGeometry() 
	{
		this(null);
	}

	/**
	 * Constructeur d'une géométrie abstraite planaire avec parent comme primitive.  
	 * @param parent - La primitive parent à cette géométrie.
	 */
	public SAbstractPlanarGeometry(SPrimitive parent) 
	{
		super(parent);
	}
   
	/**
	 * Méthode pour obtenir la normale à la surface de la géométrie planaire sur le côté "devant" du rayon.
	 * Si l'intersection est réalisée lorsque la normale à la surface et l'orientation du rayon sont dans le même sens,
	 * la méthode retournera une normale à la surface dans la direction opposée (pointant vers le rayon).
	 * @param ray_direction - L'orientation du rayon.
	 * @param plane_normal - La normale à la surface de la géométrie planaire.
	 * @return L'orientation de la normale à la surface dans le sens opposé à la direction du rayon.
	 */
	protected SVector3d evaluateFrontIntersectionNormal(SVector3d ray_direction, SVector3d plane_normal)
	{	
		//Comparaison du sens de la normale à la surface avec l'orientation du rayon
		//Si la normale est de sens opposé à l'orientation rayon, la normale est orientée vers le devant du rayon, donc pas d'inversion à réaliser
		if(ray_direction.dot(plane_normal) < 0.0)
			return plane_normal;					//deux en un sens opposés, donc bonne normale
		else
			return plane_normal.multiply(-1.0);	//deux dans le même sens, donc inverser la normale 
	}
		
}//fin classe abstraite SAbstractPlanarGeometry
