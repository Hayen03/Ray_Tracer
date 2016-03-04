/**
 * 
 */
package sim.graphics.shader;

import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.geometry.SRay;
import sim.geometry.space.SGeometrySpace;
import sim.graphics.SColor;
import sim.graphics.light.SLight;
import sim.graphics.material.SMaterial;
import sim.math.SVector3d;
import sim.physics.SOptics;

/**
 * La classe <b>SRecursiveShader</b> représente un shader avec lancé de rayon récursif.
 * @author Simon Vézina
 * @since 2015-02-03
 * @version 2015-12-01
 */
public class SRecursiveShader extends SAbstractShader {

	private static final int MINIMAL_MAX_DEPT = 1; //niveau de récursivité minimal
	
  private final int max_dept;	//niveau de récursivité des rayons maximal
	
	/**
	 * Constructeur d'un shader avec lancé de rayon récursif.
	 * @param geometry_space - L'espace des géométries.
	 * @param t_max - Le temps de déplacement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumières.
	 * @param specular_reflexion_algo - Le type d'algorithme pour réaliser le calcul de la réflexion spéculaire.
	 * @param max_dept - Le niveau de rayon récursif.
	 * @throws SConstructorException Si le niveau de récursivité des rayons est inférieur au seuil minimal (habituellement 1).
   */
	public SRecursiveShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list, int specular_reflexion_algo, int max_dept)throws SConstructorException 
	{
		super(geometry_space, t_max, light_list, specular_reflexion_algo);
		
		if(max_dept < MINIMAL_MAX_DEPT)
      throw new SConstructorException("Erreur SRecursiveShader 002 : Le niveau '" + max_dept + "' de récursivité des rayons doit être supérieur à '" + MINIMAL_MAX_DEPT + "'.");
    
		this.max_dept = max_dept;
	}

	/* (non-Javadoc)
	 * @see simGraphics.SShader#shade(simGeometry.SRay)
	 */
	@Override
	public SColor shade(SRay ray) throws SRuntimeException 
	{
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur SRecursiveShader 003 : Le rayon a déjà intersecté une géométrie préalablement.");
		
		//Lancer un rayon de niveau de récursivité 1
		return recursiveShade(ray, 1);
	}

	/**
	 * Méthode qui effectue l'illumination d'un rayon récursivement.
	 * @param ray - Le rayon à illuminer.
	 * @param depth - Le niveau de récursivité du rayon.
	 * @return La couleur associée à l'illumination du rayon récursif.
	 */
	private SColor recursiveShade(SRay ray, int depth)
	{
		//Si le niveau de rayon récursif est trop profond, tout arrêter et retourner la couleur noire
		if(depth > max_dept)
			return SIllumination.NO_ILLUMINATION;
		else
		{
			//Effectuer l'intersection avec l'espace des géométries
			ray = geometry_space.nearestIntersection(ray, t_max);
			
			//S'il n'y a pas d'intersection, la couleur affectée sera noire
			if(!ray.asIntersected())
				return SIllumination.NO_ILLUMINATION;
					
			//----------------------
			//	Illumination direct	
			//----------------------
			//Faire une somme de couleur et débuter avec une couleur noire
			SColor color = SIllumination.NO_ILLUMINATION;	
				
			//Obtenir le matériel de la primitive
			SMaterial material = ray.getGeometry().getPrimitiveParent().getMaterial(); 
						
			//Iterer sur l'ensemble des lumières et ajouter leur contribution à la somme des couleurs.
			for(SLight light : light_list)
				color = color.add(shadeWithLight(ray, light, material));
			
			//-----------------------------------------------
			//	Illumination indirecte : Loi de la réflexion	
			//-----------------------------------------------
			if(material.isReflective())
			{
				SVector3d R = SOptics.reflexion(ray.getDirection(), ray.getNormal());
			
				//Rayon de réflexion, sans changement de milieu (indice de réfraction du rayon intersecté)
				SRay reflexion_ray = new SRay(ray.getIntersectionPosition(), R, ray.getRefractiveIndex());
			
				//Ajouter la luminosité réflexive à la somme des couleurs en augmentant le niveau de 1
				color = color.add(recursiveShade(reflexion_ray, depth+1).multiply(material.reflectivity()));
			}
			
			//------------------------------------------------
			//	Illumination indirecte : Loi de la réfraction
			//------------------------------------------------
			if(material.isTransparent())
			{
				//Réfraction uniquement si la géométrie intersectée est une géométrie fermée (pas une surface)
				if(ray.getGeometry().isClosedGeometry())
				{
					//Indice de réfraction à déterminer (n1 = incident, n2 = réfracté)
					double n1 = ray.getRefractiveIndex();
					
					//Pour déterminer n2, il y a deux scénarios à considérer :
					//1) L'intersection vient de l'extérieur, donc n2 sera l'indice interne de la géométrie intersectée.
					//2) L'intersection vient de l'intérieur, donc on utilise le shader pour déterminer l'indice à l'extérieur de la géométrie intersectée de l'intérieur.
					double n2;
					
					boolean from_outside;
					
					if(!ray.isInsideIntersection())                                  //de l'extérieur
					{
					  n2 = material.refractiveIndex();
					  from_outside = true;
					}
					else                                                             //de l'intérieur
					{
					  n2 = evaluateRefractiveIndex(ray.getIntersectionPosition());
					  from_outside = false;
					}
					
					//Vérifier qu'il n'y a pas réflexion totale interne
					if(!SOptics.isTotalInternalReflection(ray.getDirection(), ray.getNormal(), n1, n2))
					{
						SVector3d T = SOptics.refraction(ray.getDirection(), ray.getNormal(), n1, n2);
					
						//Rayon de réfraction avec changement de milieu (indice de réfraction n2)
						SRay refraction_ray = new SRay(ray.getIntersectionPosition(), T, n2);
					
						//Ajouter la luminosité réfractive à la somme des couleurs en augmentant le niveau de 1
						
						// Venant de l'extérieur, nous allons appliquer un filtrage à la couleur
						if(from_outside)
						  color = color.add(recursiveShade(refraction_ray, depth+1).multiply(material.transparencyColor()));  // couleur avec filtrage
						else  
						  color = color.add(recursiveShade(refraction_ray, depth+1).multiply(material.transparency()));       // couleur déjà filtrée
					}
				}
			}
			
			return color;
		}
	}
	
}//fin classe SRecursiveShader
