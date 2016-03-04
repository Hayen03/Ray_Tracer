/**
 * 
 */
package sim.graphics.light;


import java.util.List;
import java.util.LinkedList;

import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;
import sim.geometry.SRay;
import sim.geometry.space.SGeometrySpace;
import sim.graphics.SColor;
import sim.graphics.material.SMaterial;
import sim.graphics.shader.SIllumination;
import sim.math.SVector3d;

/**
 * La classe <b>SShadewRay</b> représente un rayon d'ombrage associé à une source de lumière éclairant un point d'intersection.
 * 
 * @author Simon Vézina
 * @since 2015-01-21
 * @version 2015-12-04
 */
public class SShadowRay {

	final private boolean is_in_shadow;			//détermine si la surface éclairée est dans l'ombre de la source de lumière.
	final private SColor filtred_light_color;	//couleur de la source de lumière à l'endroit de l'intersection (ombrage, lumière filtrée ou source de lumière intacte).
	
	/**
	 * Constructeur d'un rayon d'ombre associé à une source de lumière.
	 * @param intersection_ray - Le rayon donnant accès à la surface intersectée qui est éclairée.
	 * @param light - La source de lumière éclairant la surface intersectée.
	 * @param geometry_space - L'espace des géométries de la scène.
	 * @throws SRuntimeException - Si le rayon n'a pas intersecté de géométrie dans l'espace des géométries.
	 */
	public SShadowRay(SRay intersection_ray, SLight light, SGeometrySpace geometry_space)throws SRuntimeException
	{
		if(!intersection_ray.asIntersected())
			throw new SRuntimeException("Erreur SShadowRay 001 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
		
		filtred_light_color = evaluateFilteredLightColor(intersection_ray, light, geometry_space);
		is_in_shadow = evaluateIsInShadow(filtred_light_color);
	}
	
	/**
	 * Méthode pour déterminer si la surface d'intersection est située dans l'ombre
	 * de la source de lumière associée à ce rayon d'ombre.
	 * @return <b>true</b> si la surface intersectée est dans l'ombre de la source de lumière et <b>false</b> sinon.
	 */
	public boolean isInShadow()
	{ 
	  return is_in_shadow;
	}
	
	/**
	 * Méthode pour obtenir la couleur de la source de lumière après filtrage causé par son passage au travers des matériaux transparents. 
	 * @return la <b>couleur d'origine</b> de la lumière si aucune géométrie était entre la surface intersectée et la source de lumière,
	 * la <b>couleur noire</b> s'il y a eu une géométrie opaque entre la surface intersectée et la source de lumière (méthode isInShadow() == true) et
	 * une <b>couleur filtrée</b> s'il y a eu des géométries transparentes entre la surface intersectée et la source de lumière. 
	 */
	public SColor filtredLight()
	{ 
	  return filtred_light_color;
	}
	
	/**
	 * Méthode qui évalue si la surface intersectée est dans l'ombre de la source de lumière ou pas et détermine
	 * la couleur filtrée de la source de lumière si elle se rend à la surface intersectée. 
	 * @param intersection_ray -  Le rayon donnant accès à la surface intersectée à éclairer.
	 * @param light - La source de lumière éclairant la surface intersectée.
	 * @param orientation_light - L'orientation de la source de lumière.
	 * @param distance - La distance entre la source de lumière et la surface intersectée.
	 * @param geometry_space - L'espace des géométries de la scène.
	 */
	private SColor evaluateFilteredLightColor(SRay intersection_ray, SLight light, SGeometrySpace geometry_space)
	{
		//Cas de la source de lumière ambiante
		if(light instanceof SAmbientLight)
			return light.getColor();
		
		SVector3d d;		  //orientation de la source de source de lumière
		double distance;	//distance entre la source et la source de lumière
		
		//Cas de la source de lumière directionnelle
		if(light instanceof SDirectionalLight)
		{
			d = ((SDirectionalLight)light).getOrientation();
			distance = Double.POSITIVE_INFINITY;
		}
		else
			//Cas de la source de lumière ponctuelle
			if(light instanceof SPointLight)
			{
				SPointLight point_light = (SPointLight)light;
				
				d = point_light.getOrientation(intersection_ray.getIntersectionPosition());
				distance = point_light.getPosition().substract(intersection_ray.getIntersectionPosition()).modulus();
			}
			else
				throw new SNoImplementationException("Erreur SShadowRay 002 : La source de lumière n'a pas été implémentée.");
		
		SVector3d L = d.multiply(-1.0);		//orientation inverse de la lumière
			
		//Tester si la normale est orientée dans le même sens que la source de lumière (back clipping plane)
		if(intersection_ray.getNormal().dot(L) <= 0.0)
			return SIllumination.NO_ILLUMINATION;
		else
		{
			//Rayon de l'intersection à la source de lumière (avec indice de réfraction de 1.0, car il n'y aura pas de calcul de réfraction pour ce rayon)
			SRay intersection_to_light_ray = new SRay(intersection_ray.getIntersectionPosition(), L, SRay.DEFAULT_REFRACTIVE_INDEX);
			
			//Liste des géométries rencontrées avant d'atteindre la source de lumière 
			//en ordre inverse et débutant par une géométrie opaque s'il y a eu intersection de ce type
			List<SRay> list_transparent = geometry_space.nearestOpaqueIntersection(intersection_to_light_ray, distance);
			
			//Test de la liste vide, il n'y a pas d'intersection, donc pas d'ombre ni lumière altérée par des effets de transparence
			if(list_transparent.isEmpty())
				return light.getColor();
			else
				//Si la liste possède une géométrie opaque (non transparente) au début, la lumiere sera bloquée
				if(!list_transparent.get(0).getGeometry().isTransparent())
					return SIllumination.NO_ILLUMINATION;
				else
				{
					List<SMaterial> material_list = new LinkedList<SMaterial>();
					
					for(SRay r : list_transparent)
						material_list.add(r.getGeometry().getPrimitiveParent().getMaterial());
						
					return SIllumination.filteredTransparencyLight(material_list, light.getColor());
				}
		}
	}
	
	/**
	 * Méthode qui détermine si une couleur passée en paramètre est considérée commme étant dans l'ombre.
	 * @param c - La couleur à déterminer si elle est dans l'ombrage.
	 * @return <b>true</b> si la couleur est 'noir' (0.0, 0.0, 0.0) et <b>false</b> sinon.
	 */
	private boolean evaluateIsInShadow(final SColor c)
	{
		if(c.equals(SIllumination.NO_ILLUMINATION))
			return true;
		else
			return false;
	}
	
}//fin classe SShadowRay
