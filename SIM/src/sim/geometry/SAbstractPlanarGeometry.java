/**
 * 
 */
package sim.geometry;

import sim.graphics.SPrimitive;
import sim.math.SVector3d;

/**
 * La classe abstraite <b>SAbstractPlanarGeometry</b> repr�sentant une g�om�trie planaire. 
 * Cette classe permet l'�valuation du sens ad�quat de la normale � la surface lors d'une intersection.
 * 
 * @author Simon V�zina
 * @since 2015-02-19
 * @version 2015-12-14
 */
public abstract class SAbstractPlanarGeometry extends SAbstractGeometry {

  /**
	 * Constructeur vide d'une g�om�trie abstraite planaire. 
	 */
	public SAbstractPlanarGeometry() 
	{
		this(null);
	}

	/**
	 * Constructeur d'une g�om�trie abstraite planaire avec parent comme primitive.  
	 * @param parent - La primitive parent � cette g�om�trie.
	 */
	public SAbstractPlanarGeometry(SPrimitive parent) 
	{
		super(parent);
	}
   
	/**
	 * M�thode pour obtenir la normale � la surface de la g�om�trie planaire sur le c�t� "devant" du rayon.
	 * Si l'intersection est r�alis�e lorsque la normale � la surface et l'orientation du rayon sont dans le m�me sens,
	 * la m�thode retournera une normale � la surface dans la direction oppos�e (pointant vers le rayon).
	 * @param ray_direction - L'orientation du rayon.
	 * @param plane_normal - La normale � la surface de la g�om�trie planaire.
	 * @return L'orientation de la normale � la surface dans le sens oppos� � la direction du rayon.
	 */
	protected SVector3d evaluateFrontIntersectionNormal(SVector3d ray_direction, SVector3d plane_normal)
	{	
		//Comparaison du sens de la normale � la surface avec l'orientation du rayon
		//Si la normale est de sens oppos� � l'orientation rayon, la normale est orient�e vers le devant du rayon, donc pas d'inversion � r�aliser
		if(ray_direction.dot(plane_normal) < 0.0)
			return plane_normal;					//deux en un sens oppos�s, donc bonne normale
		else
			return plane_normal.multiply(-1.0);	//deux dans le m�me sens, donc inverser la normale 
	}
		
}//fin classe abstraite SAbstractPlanarGeometry
