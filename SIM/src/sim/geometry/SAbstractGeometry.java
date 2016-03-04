/**
 * 
 */
package sim.geometry;

import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SAbstractReadableWriteable;
import sim.util.SInitializationException;

/**
 * La classe abstraite <b>SAbstractGeometry</b> représentant une géométrie générale sans définition spatiale. 
 * Une géométrie permet une intersection avec un rayon.
 * 
 * @author Simon Vézina
 * @since 2014-12-30
 * @version 2015-11-28
 */
public abstract class SAbstractGeometry extends SAbstractReadableWriteable implements SGeometry {

  //Numéro d'identification des types de géométrie 
  public static final int PLANE_CODE = 1;
  public static final int DISK_CODE = 2;
  public static final int SPHERE_CODE = 3;
  public static final int TUBE_CODE = 4;
	public static final int CYLINDER_CODE = 5;
	public static final int CONE_CODE = 6;
	public static final int TRIANGLE_CODE = 7;
	public static final int BTRIANGLE_CODE = 8;
	public static final int TRANSFORMABLE_CODE = 9;
	public static final int CUBE_CODE = 10;
	public static final int SPHERICAL_CAP_CODE = 11;
	public static final int LENS_CODE = 12;
	
  private static long next_id = 1;		   //prochaine numéro d'identification à attribuer à la prochaine géométrie qui sera instanciée
	
  /**
   * La variable <b>id</b> correspond au numéro d'identification unique de la géométrie.
   */
	private long id;						           
	
	/**
	 * La variable <b>primitive_parent</b> correspond à la primitive qui est le propriétaire de la géométrie (son parent).
	 */
	private SPrimitive primitive_parent;	 
	
	/**
	 * Constructeur d'une géométrie sans parent primitive.
	 */
	public SAbstractGeometry()
	{
		this(null);
	}
	
	/**
	 * Constructeur par défaut d'une géométrie ayant une primitive comme parent.
	 * @param parent - La primitive parent de la géométrie.
	 */
	public SAbstractGeometry(SPrimitive parent)
	{
		id = next_id;						//attribuer l'ID à la géométrie lors de la création
		next_id++;							//augmenter l'ID de 1
	
		primitive_parent = parent;
	}
	
	@Override
	public long getID()
	{ 
	  return id;
	}
	
	@Override
	public int hashCode()
	{
	  return (int) id;
	}
	
	@Override
  public SPrimitive getPrimitiveParent()throws SRuntimeException
  { 
    if(primitive_parent != null)
      return primitive_parent;
    else
      throw new SRuntimeException("Erreur SAbstractGeometry 001 : La géométrie ne possède pas de primitive comme parent.");
    
  }
  
  @Override
  public void setPrimitiveParent(SPrimitive parent)throws SRuntimeException
  {
    if(primitive_parent != null)
      throw new SRuntimeException("Erreur SAbstractGeometry 002 : La géométrie ne peut pas se faire affecter un nouveau parent, car cette valeur a été préalablement déterminée.");
    else
      primitive_parent = parent;
  }
  
	@Override
	public boolean isTransparent()
	{
	  if(primitive_parent != null)
	    if(primitive_parent.getMaterial() != null)
	      return primitive_parent.getMaterial().isTransparent();
	    else
	      return false;
	  else
	    return false;
  } 
		
	/**
	 * Méthode pour déterminer si l'intersection est réalisée sur la géométrie par l'intérieur.
	 * Avant d'utiliser cette méthode, il faut préalablement évaluer le temps pour réaliser l'intersection.
	 * Cette méthode prendra pour acquis que ces tests ont été réalisés adéquatement.
	 * @param ray - Le rayon réalisant l'intersection avec la géométrie.
	 * @param intersection_t - Le temps requis au rayon pour réaliser l'intersection. 
	 * @return <b>true</b> si le rayon intersecte la géométrie par l'intérieur et <b>false</b> sinon.
	 */
	abstract protected boolean isInsideIntersection(SRay ray, double intersection_t);
	
	/**
	 * Méthode pour déterminer la normale à la surface de la géométrie intersecté par le rayon.
	 * Cette méthode évalue adéquatement le sens de la normale dépendant si l'intersection se réalise de l'extérieur ou de l'intérieur de la géométrie.
	 * Avant d'utiliser cette méthode, il faut préalablement évaluer le temps pour réaliser l'intersection.
	 * Cette méthode prendra pour acquis que ces tests ont été réalisés adéquatement.
	 * @param ray - Le rayon réalisant l'intersection avec la géométrie.
	 * @param intersection_t - Le temps requis au rayon pour réaliser l'intersection. 
	 * @return La normale à la surface <b>normalisée</b> où est réalisée l'intersection.
	 */
	abstract protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t);
	
	/**
	 * Méthode pour déterminer les coordonnées uv de la géométrie à l'endroit où il y a intersectin avec un rayon.
	 * Avant d'utiliser cette méthode, il faut préalablement évaluer le temps pour réaliser l'intersection.
   * Cette méthode prendra pour acquis que ces tests ont été réalisés adéquatement.
	 * @param ray - Le rayon réalisant l'intersection avec la géométrie.
	 * @param intersection_t - Le temps requis au rayon pour réaliser l'intersection. 
	 * @return La coordonnée uv de la surface où est réalisée l'intersection.
	 */
	abstract protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t);
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null) 					//Test du null
	    	return false;
	    
	    if (other == this)					//Test de la même référence 
	    	return true;
	    
	    if (!(other instanceof SGeometry))	//Test d'un type différent
	    	return false;
	    
	    //Vérification du numéro d'identification, car il doit être unique pour chaque géométrie construite
	    SAbstractGeometry o = (SAbstractGeometry)other;
	    
	    if(id == o.getID())
	    	return true;
	    else
	      return false;
	}
			
	@Override
  protected void readingInitialization() throws SInitializationException
  {
   
  }
		
}//fin interface SGeometry
