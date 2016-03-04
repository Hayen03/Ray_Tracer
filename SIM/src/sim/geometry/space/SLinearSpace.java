/**
 * 
 */
package sim.geometry.space;

import java.util.List;

import sim.exception.SRuntimeException;
import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.math.SVector3d;
import sim.util.SLog;

/**
 * Classe qui représente un espace à géométrie linéaire. Cela signifie que les géométries ne sont pas organisées
 * dans une structure complexe. Il sera testé en séquence linéaire à chaque tentative d'intersection avec un rayon.
 * Cette version très naïve est efficace seulement pour des scènes comportant un nombre très très faible de géométries.
 * 
 * @author Simon Vézina
 * @since 2015-01-10
 * @version 2015-12-27
 */
public class SLinearSpace extends SAbstractGeometrySpace {

	/**
	 * Constructeur d'un espace à géométrie linéaire.
	 */
	public SLinearSpace()
	{
	  super();
	}
	
	@Override
	public SRay nearestIntersection(SRay ray, double t_max)throws SRuntimeException
	{
		//Vérifier si le rayon a déjà intersecté une géométrie
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur SLinearSpace 001 : Le rayon en paramètre a déjà intersecté une géométrie.");
				
		//Vérifier la valeur de t_max est adéquate
		if(t_max < 0.0)
			throw new SRuntimeException("Erreur SLinearSpace 002 : Le temps/distance maximale ne peut pas être négative.");
				
		// Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SLinearSpace 003 : L'espace de géométrie n'a pas été initialisé.");
    
		//Obtenir la liste des intersections
		List<SRay> list_intersection = intersections(geometry_list, ray, t_max);
		
		//Si la liste n'est pas vide, la première intersection sera l'élément [0] de la liste, 
		//car elle doit être triée en ordre croissant de temps/distante
		if(!list_intersection.isEmpty())
			return list_intersection.get(0);
		else
			return ray;	//sans intersection, on retourne le rayon d'origine
	}
	
	@Override
	public List<SRay> nearestOpaqueIntersection(SRay ray, final double t_max)throws SRuntimeException
	{
	  //Vérifier si le rayon a déjà intersecté une géométrie
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SLinearSpace 004 : Le rayon en paramètre a déjà intersecté une géométrie.");
        
    //Vérifier la valeur de t_max est adéquate
    if(t_max < 0.0)
      throw new SRuntimeException("Erreur SLinearSpace 005 : Le temps/distance maximale ne peut pas être négative.");
        
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SLinearSpace 006 : L'espace de géométrie n'a pas été initialisé.");
    
	  return nearestOpaqueIntersection(geometry_list, ray, t_max);
	}
	
  @Override
  public List<SGeometry> listInsideGeometry(SVector3d v) throws SRuntimeException
  {
    // Vérifier si l'espace a été initialisé
    if(!space_initialized)
      throw new SRuntimeException("Erreur SLinearSpace 007 : L'espace de géométrie n'a pas été initialisé.");
    
    return listInsideGeometry(geometry_list, v);
  }
 
  @Override
  public void initialize()
  {
    SLog.logWriteLine("Message SLinearSpace : Construction de l'espace linéaire des géométries.");
    
    SLog.logWriteLine("Message SLinearSpace : Fin de la construction de l'espace linéaire des géométries.");
    SLog.logWriteLine();
    
    space_initialized = true;
  }
  
}//fin classe SLinearSpace
