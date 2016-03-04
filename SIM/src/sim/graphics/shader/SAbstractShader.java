/**
 * 
 */
package sim.graphics.shader;

import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SNoImplementationException;
import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.geometry.space.SGeometrySpace;
import sim.graphics.SColor;
import sim.graphics.light.SAmbientLight;
import sim.graphics.light.SDirectionalLight;
import sim.graphics.light.SLight;
import sim.graphics.light.SPointLight;
import sim.graphics.light.SShadowRay;
import sim.graphics.material.SMaterial;
import sim.math.SVector3d;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * Classe abstraite contenant plusieurs fonctionnalités de base d'un shader.
 * 
 * @author Simon Vézina
 * @since 2015-02-01
 * @version 2015-09-20
 */
public abstract class SAbstractShader implements SShader {

	//type de mode de réflexion spéculaire disponible
  final public static String[] SPECULAR_REFLEXION = { "none", "phong", "blinn" };
	final public static int NO_SPECULAR_REFLEXION = 0;
	final public static int PHONG_SPECULAR_REFLEXION = 1;
	final public static int BLINN_SPECULAR_REFLEXION = 2;
	
	final protected SGeometrySpace geometry_space;	   //l'espace des géométries
	
	final protected double t_max;                      //temps de déplacement maximal d'un rayon
	
	final protected List<SLight> light_list;				   //liste des lumières
	
	final private int specular_reflexion_algo;       //type d'algorithme pour la réflexion spéculaire
	
	
	private static int MULTIPLE_INSIDE_GEOMETRY_ERROR = 0;      //code d'erreur lorsqu'il y a plusieurs géométries imbriquées ensemble
	
	/**
	 * Constructeur d'une shader abstrait avec le modèle de réflexion spéculaire de <b><i>Blinn</i></b>.
	 * @param geometry_space - L'espace des géométries.
	 * @param t_max - Temps de déplacement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumières.
	 */
	public SAbstractShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list) 
	{
		this(geometry_space, t_max, light_list, BLINN_SPECULAR_REFLEXION);
	}

	/**
	 * Constructeur d'une shader abstrait.
	 * @param geometry_space - L'espace des géométries.
	 * @param t_max - Temps de déplacement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumières.
	 * @param specular_reflexion_algo - Le type d'algorithme pour réaliser le calcul de la réflexion spéculaire.
	 * @throws SConstructorException Si la valeur de t_max est inférieur à une valeur de seuil.
	 * @throws SConstructorException Si le type d'algorithme de réflexion spéculaire n'est pas reconnu.
	 * @see NO_SPECULAR_REFLEXION
	 * @see PHONG_SPECULAR_REFLEXION
	 * @see BLINN_SPECULAR_REFLEXION
	 */
	public SAbstractShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list, int specular_reflexion_algo) throws SConstructorException 
	{
		this.geometry_space = geometry_space;
		
		if(t_max < SRay.getEpsilon())
		  throw new SConstructorException("Erreur SAbstractShader 001 : Le temps maximal '" + t_max + "' doit être supérieur à '" + SRay.getEpsilon() +"'.");
		else
		  this.t_max = t_max;
		
		this.light_list = light_list;
		
		if(specular_reflexion_algo != NO_SPECULAR_REFLEXION && 
		   specular_reflexion_algo != PHONG_SPECULAR_REFLEXION &&
		   specular_reflexion_algo != BLINN_SPECULAR_REFLEXION)
			throw new SConstructorException("Erreur SAbstractShader 002 : L'algorithme de réflexion spéculaire est mal défini.");
		else
			this.specular_reflexion_algo = specular_reflexion_algo;
	}
	
	/**
	 * Méthode qui fait l'analyse du type de source de lumière et redirige vers l'appel de la 
	 * méthode appropriée pour faire le calcul de shading particulier de chaque source de lumière.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumière.
	 * @param material - Le matériel qui interagit avec la source de lumière.
	 * @return la couleur résultat du calcul du shading.
	 * @throws SNoImplementationException Si le type de source n'a pas été implémenté dans le calcul de shading. 
	 */
	protected SColor shadeWithLight(SRay ray, SLight light, SMaterial material)throws SNoImplementationException
	{
		if(light instanceof SAmbientLight)
			return shadeWithAmbientLight(ray, (SAmbientLight)light, material);
		else
			if(light instanceof SDirectionalLight)
				return shadeWithDirectionalLight(ray, (SDirectionalLight)light, material);
			else
				if(light instanceof SPointLight)
					return shadeWithPointLight(ray, (SPointLight)light, material);
				else
					throw new SNoImplementationException("Erreur SAbstractShader 003 : La source de lumière est de type indéterminé.");
	}
	
	/**
	 * Méthode qui effectue le calcul de shading pour une source de lumière ambiante.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumière ambiante.
	 * @param material - Le matériel qui interagit avec la source de lumière.
	 * @return la couleur résultant d'un shading avec une source de lumière ambiante.
	 */
	protected SColor shadeWithAmbientLight(SRay ray, SAmbientLight light, SMaterial material)
	{
		if(ray.asUV())
		  return SIllumination.ambientReflexion(light.getColor(), material.ambientColor(ray.getUV()));    //avec coordonnée uv
		else
		  return SIllumination.ambientReflexion(light.getColor(), material.ambientColor());               //sans coordonnée uv
	}
	
	/**
	 * Méthode qui effectue le calcul de shading pour une source de lumière directionnelle.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumière directionnelle.
	 * @param material - Le matériel qui interagit avec la source de lumière.
	 * @return la couleur résultant d'un shading avec une source de lumière directionnelle.
	 */
	protected SColor shadeWithDirectionalLight(SRay ray, SDirectionalLight light, SMaterial material)
	{
		SShadowRay shadow_ray = new SShadowRay(ray, light, geometry_space);
		
		//Si la surface intersectée est dans l'ombrage
		if(shadow_ray.isInShadow())
			return SIllumination.NO_ILLUMINATION;
		else
		{				
		  //Couleur à déterminer
		  SColor color;
		  
		  //Ajouter la luminosité diffuse (Ldif) à la somme des couleurs
			if(ray.asUV())
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(ray.getUV()), ray.getNormal(), light.getOrientation());
			else
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(), ray.getNormal(), light.getOrientation());
		  
			//Ajouter la luminosité spéculaire (Lspe)à la somme des couleurs en fonction du choix de l'algorithme
			switch(specular_reflexion_algo)
			{
				case NO_SPECULAR_REFLEXION : 		break;
				
				case PHONG_SPECULAR_REFLEXION :	if(ray.asUV())
				                                  color = color.add(SIllumination.phongSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(ray.getUV()), ray.getNormal(), ray.getDirection(), light.getOrientation(), material.getShininess())); 
				                                else
				                                  color = color.add(SIllumination.phongSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(), ray.getNormal(), ray.getDirection(), light.getOrientation(), material.getShininess())); 
				                                
				                                break;  
				                                
				case BLINN_SPECULAR_REFLEXION : if(ray.asUV())	
				                                  color = color.add(SIllumination.blinnSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(ray.getUV()), ray.getNormal(), ray.getDirection(), light.getOrientation(), material.getShininess()));	
				                                else
				                                  color = color.add(SIllumination.blinnSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(), ray.getNormal(), ray.getDirection(), light.getOrientation(), material.getShininess())); 
				                                
				                                break;
			}
			
			return color;
		}
	}
	
	/**
	 * Méthode qui effectue le calcul de shading pour une source de lumière ponctuelle.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumière ponctuelle.
	 * @param material - Le matériel qui interagit avec la source de lumière.
	 * @return la couleur résultant d'un shading avec une source de lumière ponctuelle.
	 */
	protected SColor shadeWithPointLight(SRay ray, SPointLight light, SMaterial material)
	{
		SShadowRay shadow_ray = new SShadowRay(ray, light, geometry_space);
		
		//Si la surface intersectée est dans l'ombrage
		if(shadow_ray.isInShadow())
			return SIllumination.NO_ILLUMINATION;
		else
		{				
			//Orientation de la source de lumière
			SVector3d light_orientation = light.getOrientation(ray.getIntersectionPosition());
			
			//Couleur à déterminer
      SColor color;
			
      //Ajouter la luminosité diffuse (Ldif) à la somme des couleurs
			if(ray.asUV())
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(ray.getUV()), ray.getNormal(), light_orientation);
			else
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(), ray.getNormal(), light_orientation);
			
			//Ajouter la luminosité spéculaire (Lspe)à la somme des couleurs en fonction du choix de l'algorithme
			switch(specular_reflexion_algo)
			{
				case NO_SPECULAR_REFLEXION : 		break;
				
				case PHONG_SPECULAR_REFLEXION : if(ray.asUV())
				                                  color = color.add(SIllumination.phongSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(ray.getUV()), ray.getNormal(), ray.getDirection(), light_orientation, material.getShininess()));
                                				else
                                				  color = color.add(SIllumination.phongSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(), ray.getNormal(), ray.getDirection(), light_orientation, material.getShininess()));  
                                				
				                                break;
												
				case BLINN_SPECULAR_REFLEXION :	if(ray.asUV())
				                                  color = color.add(SIllumination.blinnSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(ray.getUV()), ray.getNormal(), ray.getDirection(), light_orientation, material.getShininess())); 
                                				else
                                				  color = color.add(SIllumination.blinnSpecularReflexion(shadow_ray.filtredLight(), material.specularColor(), ray.getNormal(), ray.getDirection(), light_orientation, material.getShininess())); 
                                				
                                				break;
			}
			
			//Ajouter le facteur d'attenuation à la source de lumière retournée
			return color.multiply(light.attenuation(ray.getIntersectionPosition()));
		}
	}
	
	/* (non-Javadoc)
   * @see simGraphics.SShader# evaluateRefractiveIndex(simMath.SVector3d)
   */ 
  @Override
  public double evaluateRefractiveIndex(SVector3d position)
  {
    //Pour déterminer l'indice de réfraction d'un milieu ambiant, 
    //il faut vérifier dans quelle géométrie le rayon est situé.
    //- Si le rayon est situé à la frontière d'une géométrie (sur la surface), il sera considéré comme à l'extérieur et l'indice n = 1.0 sera affecté.
    //- Si le rayon est situé dans une géométrie, il faudra obtenir son indice de réfraction s'il possède une primitive comme parent, sinon n = 1.0 sera affecté.
    //- Si le rayon est situé dans plusieurs géométrie, NOUS AVONS PRÉSENTEMENT UN PROBLÈME !!! qui devra être résolu dans le futur.
    
    List<SGeometry> list_inside = geometry_space.listInsideGeometry(position);
    
    if(list_inside.isEmpty())
      return 1.0;               //indice de réfraction du vide
    else
      if(list_inside.size() == 1)
        return list_inside.get(0).getPrimitiveParent().getMaterial().refractiveIndex();
      else
      {
        //-----------------------------------------
        //C'est ici que nous avons un problème!!!
        //-----------------------------------------
        MULTIPLE_INSIDE_GEOMETRY_ERROR++;
        
        //Pour le moment, nous allons faire la moyenne de tout les indices de réfraction pour définir l'indice du milieu
        double total_refractive_index = 0.0;
                
        for(SGeometry g : list_inside)
          total_refractive_index += g.getPrimitiveParent().getMaterial().refractiveIndex();   //pour faire la moyenne des indices de réfraction
         
        //Message de la situation de plusieurs géométrie transparente imbriquée
        if(MULTIPLE_INSIDE_GEOMETRY_ERROR == 1)
          SLog.logWriteLine(SStringUtil.END_LINE_CARACTER + "Message SAbstractShader : Il y a une situation où " + list_inside.size() + " géométries sont imbriquées et l'identification de l'indice de réfraction sera une moyenne des indices des matériaux.");
                  
        return total_refractive_index / list_inside.size();
     }     
  }
  
}//fin classe SAbstractShader
