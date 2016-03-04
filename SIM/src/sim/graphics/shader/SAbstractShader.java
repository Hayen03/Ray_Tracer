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
 * Classe abstraite contenant plusieurs fonctionnalit�s de base d'un shader.
 * 
 * @author Simon V�zina
 * @since 2015-02-01
 * @version 2015-09-20
 */
public abstract class SAbstractShader implements SShader {

	//type de mode de r�flexion sp�culaire disponible
  final public static String[] SPECULAR_REFLEXION = { "none", "phong", "blinn" };
	final public static int NO_SPECULAR_REFLEXION = 0;
	final public static int PHONG_SPECULAR_REFLEXION = 1;
	final public static int BLINN_SPECULAR_REFLEXION = 2;
	
	final protected SGeometrySpace geometry_space;	   //l'espace des g�om�tries
	
	final protected double t_max;                      //temps de d�placement maximal d'un rayon
	
	final protected List<SLight> light_list;				   //liste des lumi�res
	
	final private int specular_reflexion_algo;       //type d'algorithme pour la r�flexion sp�culaire
	
	
	private static int MULTIPLE_INSIDE_GEOMETRY_ERROR = 0;      //code d'erreur lorsqu'il y a plusieurs g�om�tries imbriqu�es ensemble
	
	/**
	 * Constructeur d'une shader abstrait avec le mod�le de r�flexion sp�culaire de <b><i>Blinn</i></b>.
	 * @param geometry_space - L'espace des g�om�tries.
	 * @param t_max - Temps de d�placement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumi�res.
	 */
	public SAbstractShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list) 
	{
		this(geometry_space, t_max, light_list, BLINN_SPECULAR_REFLEXION);
	}

	/**
	 * Constructeur d'une shader abstrait.
	 * @param geometry_space - L'espace des g�om�tries.
	 * @param t_max - Temps de d�placement maximal d'un rayon.
	 * @param light_list - La liste des sources de lumi�res.
	 * @param specular_reflexion_algo - Le type d'algorithme pour r�aliser le calcul de la r�flexion sp�culaire.
	 * @throws SConstructorException Si la valeur de t_max est inf�rieur � une valeur de seuil.
	 * @throws SConstructorException Si le type d'algorithme de r�flexion sp�culaire n'est pas reconnu.
	 * @see NO_SPECULAR_REFLEXION
	 * @see PHONG_SPECULAR_REFLEXION
	 * @see BLINN_SPECULAR_REFLEXION
	 */
	public SAbstractShader(SGeometrySpace geometry_space, double t_max, List<SLight> light_list, int specular_reflexion_algo) throws SConstructorException 
	{
		this.geometry_space = geometry_space;
		
		if(t_max < SRay.getEpsilon())
		  throw new SConstructorException("Erreur SAbstractShader 001 : Le temps maximal '" + t_max + "' doit �tre sup�rieur � '" + SRay.getEpsilon() +"'.");
		else
		  this.t_max = t_max;
		
		this.light_list = light_list;
		
		if(specular_reflexion_algo != NO_SPECULAR_REFLEXION && 
		   specular_reflexion_algo != PHONG_SPECULAR_REFLEXION &&
		   specular_reflexion_algo != BLINN_SPECULAR_REFLEXION)
			throw new SConstructorException("Erreur SAbstractShader 002 : L'algorithme de r�flexion sp�culaire est mal d�fini.");
		else
			this.specular_reflexion_algo = specular_reflexion_algo;
	}
	
	/**
	 * M�thode qui fait l'analyse du type de source de lumi�re et redirige vers l'appel de la 
	 * m�thode appropri�e pour faire le calcul de shading particulier de chaque source de lumi�re.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumi�re.
	 * @param material - Le mat�riel qui interagit avec la source de lumi�re.
	 * @return la couleur r�sultat du calcul du shading.
	 * @throws SNoImplementationException Si le type de source n'a pas �t� impl�ment� dans le calcul de shading. 
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
					throw new SNoImplementationException("Erreur SAbstractShader 003 : La source de lumi�re est de type ind�termin�.");
	}
	
	/**
	 * M�thode qui effectue le calcul de shading pour une source de lumi�re ambiante.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumi�re ambiante.
	 * @param material - Le mat�riel qui interagit avec la source de lumi�re.
	 * @return la couleur r�sultant d'un shading avec une source de lumi�re ambiante.
	 */
	protected SColor shadeWithAmbientLight(SRay ray, SAmbientLight light, SMaterial material)
	{
		if(ray.asUV())
		  return SIllumination.ambientReflexion(light.getColor(), material.ambientColor(ray.getUV()));    //avec coordonn�e uv
		else
		  return SIllumination.ambientReflexion(light.getColor(), material.ambientColor());               //sans coordonn�e uv
	}
	
	/**
	 * M�thode qui effectue le calcul de shading pour une source de lumi�re directionnelle.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumi�re directionnelle.
	 * @param material - Le mat�riel qui interagit avec la source de lumi�re.
	 * @return la couleur r�sultant d'un shading avec une source de lumi�re directionnelle.
	 */
	protected SColor shadeWithDirectionalLight(SRay ray, SDirectionalLight light, SMaterial material)
	{
		SShadowRay shadow_ray = new SShadowRay(ray, light, geometry_space);
		
		//Si la surface intersect�e est dans l'ombrage
		if(shadow_ray.isInShadow())
			return SIllumination.NO_ILLUMINATION;
		else
		{				
		  //Couleur � d�terminer
		  SColor color;
		  
		  //Ajouter la luminosit� diffuse (Ldif) � la somme des couleurs
			if(ray.asUV())
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(ray.getUV()), ray.getNormal(), light.getOrientation());
			else
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(), ray.getNormal(), light.getOrientation());
		  
			//Ajouter la luminosit� sp�culaire (Lspe)� la somme des couleurs en fonction du choix de l'algorithme
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
	 * M�thode qui effectue le calcul de shading pour une source de lumi�re ponctuelle.
	 * @param ray - Le rayon en intersection.
	 * @param light - La source de lumi�re ponctuelle.
	 * @param material - Le mat�riel qui interagit avec la source de lumi�re.
	 * @return la couleur r�sultant d'un shading avec une source de lumi�re ponctuelle.
	 */
	protected SColor shadeWithPointLight(SRay ray, SPointLight light, SMaterial material)
	{
		SShadowRay shadow_ray = new SShadowRay(ray, light, geometry_space);
		
		//Si la surface intersect�e est dans l'ombrage
		if(shadow_ray.isInShadow())
			return SIllumination.NO_ILLUMINATION;
		else
		{				
			//Orientation de la source de lumi�re
			SVector3d light_orientation = light.getOrientation(ray.getIntersectionPosition());
			
			//Couleur � d�terminer
      SColor color;
			
      //Ajouter la luminosit� diffuse (Ldif) � la somme des couleurs
			if(ray.asUV())
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(ray.getUV()), ray.getNormal(), light_orientation);
			else
			  color = SIllumination.lambertianReflexion(shadow_ray.filtredLight(), material.diffuseColor(), ray.getNormal(), light_orientation);
			
			//Ajouter la luminosit� sp�culaire (Lspe)� la somme des couleurs en fonction du choix de l'algorithme
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
			
			//Ajouter le facteur d'attenuation � la source de lumi�re retourn�e
			return color.multiply(light.attenuation(ray.getIntersectionPosition()));
		}
	}
	
	/* (non-Javadoc)
   * @see simGraphics.SShader# evaluateRefractiveIndex(simMath.SVector3d)
   */ 
  @Override
  public double evaluateRefractiveIndex(SVector3d position)
  {
    //Pour d�terminer l'indice de r�fraction d'un milieu ambiant, 
    //il faut v�rifier dans quelle g�om�trie le rayon est situ�.
    //- Si le rayon est situ� � la fronti�re d'une g�om�trie (sur la surface), il sera consid�r� comme � l'ext�rieur et l'indice n = 1.0 sera affect�.
    //- Si le rayon est situ� dans une g�om�trie, il faudra obtenir son indice de r�fraction s'il poss�de une primitive comme parent, sinon n = 1.0 sera affect�.
    //- Si le rayon est situ� dans plusieurs g�om�trie, NOUS AVONS PR�SENTEMENT UN PROBL�ME !!! qui devra �tre r�solu dans le futur.
    
    List<SGeometry> list_inside = geometry_space.listInsideGeometry(position);
    
    if(list_inside.isEmpty())
      return 1.0;               //indice de r�fraction du vide
    else
      if(list_inside.size() == 1)
        return list_inside.get(0).getPrimitiveParent().getMaterial().refractiveIndex();
      else
      {
        //-----------------------------------------
        //C'est ici que nous avons un probl�me!!!
        //-----------------------------------------
        MULTIPLE_INSIDE_GEOMETRY_ERROR++;
        
        //Pour le moment, nous allons faire la moyenne de tout les indices de r�fraction pour d�finir l'indice du milieu
        double total_refractive_index = 0.0;
                
        for(SGeometry g : list_inside)
          total_refractive_index += g.getPrimitiveParent().getMaterial().refractiveIndex();   //pour faire la moyenne des indices de r�fraction
         
        //Message de la situation de plusieurs g�om�trie transparente imbriqu�e
        if(MULTIPLE_INSIDE_GEOMETRY_ERROR == 1)
          SLog.logWriteLine(SStringUtil.END_LINE_CARACTER + "Message SAbstractShader : Il y a une situation o� " + list_inside.size() + " g�om�tries sont imbriqu�es et l'identification de l'indice de r�fraction sera une moyenne des indices des mat�riaux.");
                  
        return total_refractive_index / list_inside.size();
     }     
  }
  
}//fin classe SAbstractShader
