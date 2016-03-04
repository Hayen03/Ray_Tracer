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
 * La classe <b>SRecursiveShader</b> repr�sente un shader avec lanc� de rayon r�cursif.
 * @author Simon V�zina
 * @since 2015-02-03
 * @version 2015-12-01
 */
public class SRecursiveShader extends SAbstractShader {

	private static final int MINIMAL_MAX_DEPT = 1; //niveau de r�cursivit� minimal
	
  private final int max_dept;	//niveau de r�cursivit� des rayons maximal
	
	/**
	 * Constructeur d'un shader avec lanc� de rayon r�cursif.
	 * @param geometry_space - L'espace des g�om�tries.
	 * @param t_max - Le temps de d�placement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumi�res.
	 * @param specular_reflexion_algo - Le type d'algorithme pour r�aliser le calcul de la r�flexion sp�culaire.
	 * @param max_dept - Le niveau de rayon r�cursif.
	 * @throws SConstructorException Si le niveau de r�cursivit� des rayons est inf�rieur au seuil minimal (habituellement 1).
   */
	public SRecursiveShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list, int specular_reflexion_algo, int max_dept)throws SConstructorException 
	{
		super(geometry_space, t_max, light_list, specular_reflexion_algo);
		
		if(max_dept < MINIMAL_MAX_DEPT)
      throw new SConstructorException("Erreur SRecursiveShader 002 : Le niveau '" + max_dept + "' de r�cursivit� des rayons doit �tre sup�rieur � '" + MINIMAL_MAX_DEPT + "'.");
    
		this.max_dept = max_dept;
	}

	/* (non-Javadoc)
	 * @see simGraphics.SShader#shade(simGeometry.SRay)
	 */
	@Override
	public SColor shade(SRay ray) throws SRuntimeException 
	{
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur SRecursiveShader 003 : Le rayon a d�j� intersect� une g�om�trie pr�alablement.");
		
		//Lancer un rayon de niveau de r�cursivit� 1
		return recursiveShade(ray, 1);
	}

	/**
	 * M�thode qui effectue l'illumination d'un rayon r�cursivement.
	 * @param ray - Le rayon � illuminer.
	 * @param depth - Le niveau de r�cursivit� du rayon.
	 * @return La couleur associ�e � l'illumination du rayon r�cursif.
	 */
	private SColor recursiveShade(SRay ray, int depth)
	{
		//Si le niveau de rayon r�cursif est trop profond, tout arr�ter et retourner la couleur noire
		if(depth > max_dept)
			return SIllumination.NO_ILLUMINATION;
		else
		{
			//Effectuer l'intersection avec l'espace des g�om�tries
			ray = geometry_space.nearestIntersection(ray, t_max);
			
			//S'il n'y a pas d'intersection, la couleur affect�e sera noire
			if(!ray.asIntersected())
				return SIllumination.NO_ILLUMINATION;
					
			//----------------------
			//	Illumination direct	
			//----------------------
			//Faire une somme de couleur et d�buter avec une couleur noire
			SColor color = SIllumination.NO_ILLUMINATION;	
				
			//Obtenir le mat�riel de la primitive
			SMaterial material = ray.getGeometry().getPrimitiveParent().getMaterial(); 
						
			//Iterer sur l'ensemble des lumi�res et ajouter leur contribution � la somme des couleurs.
			for(SLight light : light_list)
				color = color.add(shadeWithLight(ray, light, material));
			
			//-----------------------------------------------
			//	Illumination indirecte : Loi de la r�flexion	
			//-----------------------------------------------
			if(material.isReflective())
			{
				SVector3d R = SOptics.reflexion(ray.getDirection(), ray.getNormal());
			
				//Rayon de r�flexion, sans changement de milieu (indice de r�fraction du rayon intersect�)
				SRay reflexion_ray = new SRay(ray.getIntersectionPosition(), R, ray.getRefractiveIndex());
			
				//Ajouter la luminosit� r�flexive � la somme des couleurs en augmentant le niveau de 1
				color = color.add(recursiveShade(reflexion_ray, depth+1).multiply(material.reflectivity()));
			}
			
			//------------------------------------------------
			//	Illumination indirecte : Loi de la r�fraction
			//------------------------------------------------
			if(material.isTransparent())
			{
				//R�fraction uniquement si la g�om�trie intersect�e est une g�om�trie ferm�e (pas une surface)
				if(ray.getGeometry().isClosedGeometry())
				{
					//Indice de r�fraction � d�terminer (n1 = incident, n2 = r�fract�)
					double n1 = ray.getRefractiveIndex();
					
					//Pour d�terminer n2, il y a deux sc�narios � consid�rer :
					//1) L'intersection vient de l'ext�rieur, donc n2 sera l'indice interne de la g�om�trie intersect�e.
					//2) L'intersection vient de l'int�rieur, donc on utilise le shader pour d�terminer l'indice � l'ext�rieur de la g�om�trie intersect�e de l'int�rieur.
					double n2;
					
					boolean from_outside;
					
					if(!ray.isInsideIntersection())                                  //de l'ext�rieur
					{
					  n2 = material.refractiveIndex();
					  from_outside = true;
					}
					else                                                             //de l'int�rieur
					{
					  n2 = evaluateRefractiveIndex(ray.getIntersectionPosition());
					  from_outside = false;
					}
					
					//V�rifier qu'il n'y a pas r�flexion totale interne
					if(!SOptics.isTotalInternalReflection(ray.getDirection(), ray.getNormal(), n1, n2))
					{
						SVector3d T = SOptics.refraction(ray.getDirection(), ray.getNormal(), n1, n2);
					
						//Rayon de r�fraction avec changement de milieu (indice de r�fraction n2)
						SRay refraction_ray = new SRay(ray.getIntersectionPosition(), T, n2);
					
						//Ajouter la luminosit� r�fractive � la somme des couleurs en augmentant le niveau de 1
						
						// Venant de l'ext�rieur, nous allons appliquer un filtrage � la couleur
						if(from_outside)
						  color = color.add(recursiveShade(refraction_ray, depth+1).multiply(material.transparencyColor()));  // couleur avec filtrage
						else  
						  color = color.add(recursiveShade(refraction_ray, depth+1).multiply(material.transparency()));       // couleur d�j� filtr�e
					}
				}
			}
			
			return color;
		}
	}
	
}//fin classe SRecursiveShader
