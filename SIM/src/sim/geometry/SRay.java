/**
 * 
 */
package sim.geometry;

import java.lang.Comparable;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.math.SMath;
import sim.math.SVector3d;
import sim.math.SVectorUV;

/**
 * La classe <b>SRay</b> représente un rayon pouvant réaliser une intersection avec une géométrie. 
 * <p>Cette classe implémente l'interface <b>Comparable</b> (permettant les usages des méthodes de triage des librairies de base de java). 
 * La comparaison sera effectuée sur la <b>valeur du champ local t</b> qui représente le <b>temps afin d'intersecter une géométrie</b>. 
 * S'il n'y a <b>pas d'intersection</b>, la valeur de t sera égale à <b>l'infini</b>.</p> 
 *
 * @author Simon Vézina
 * @since 2014-12-30
 * @version 2015-12-28
 */
public class SRay implements Comparable<SRay> {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante 'MINIMUM_EPSILON' correspond à <b>la plus petite valeur du temps mininal de parcours</b> qu'un rayon doit effectuer avant de pouvoir intersecter une géométrie. 
   * Cette contrainte est nécessaire pour une raison de stabilité numérique. Elle est présentement définie à {@value} elle et doit <b>toujours être supérieure à SMath.EPSILON</b>.
   */
  public static final double MINIMUM_EPSILON = 1e-8;	//
	
  /**
   * La constante 'MAXIMAM_T' correspond à un temps signifiant que le rayon n'a pas réussi à intersecter de géométrie. 
   * Cette valeur correspond à un <b>temps infini</b>.
   */
	private static final double MAXIMUM_T = SMath.INFINITY;	
	
	/**
	 * La constante 'DEFAULT_REFRACTIV_INDEX' correspond à la valeur de l'indice de réfraction par défaut où un rayon voyage.
	 * Cet indice de réfraction est égal à celui du vide (n = {@value}).
	 */
	public static final double DEFAULT_REFRACTIVE_INDEX = 1.0;			  
	
	//-----------------------
  // VARIABLES STATIQUES //
  //-----------------------
	
	/**
   * La variable 'epsilon' correspond au temps mininal de parcours qu'un rayon doit effectuer avant de pouvoir intersecter une géométrie.
   */
  private static double epsilon = MINIMUM_EPSILON;    
  
  //-------------
  // VARIABLES //
  //-------------
  
	/**
	 * La variable 'origin' correspond à l'origine du rayon (d'où est lancé le rayon).
	 */
	private final SVector3d origin;		  
	
	/**
	 * La variable 'direction' correspond à la direction du rayon (dans quelle direction le rayon voyagera).
	 * On peut également comparer cette variable à la vitesse du rayon.
	 */
	private final SVector3d direction;	
	
	/**
	 * La variable 'as_intersected' détermine si le rayon a déjà réalisé une intersection.
	 */
	private final boolean as_intersected;	         
	
	/**
	 * La variable 'is_inside_intersection' détermine si l'intersection (s'il y en a une) sur la géométrie s'est réalisée à l'intérieur de celle-ci.
	 */
	private final boolean is_inside_intersection;  //détermine si l'intersection s'est effectuée à l'intérieur de la géométrie
	
	/**
	 * La varibale 'geometry' correspond à la géométrie intersecté par le rayon.
	 */
	private final SGeometry geometry;
	
	/**
	 * La variable 'normal' correspond à la normale à la surface sur la géométrie intersectée à l'endroit où l'intersection a été réalisée.
	 */
	private final SVector3d normal;			     
	
	/**
	 * La variable 'uv' correspond à la coordonnée <i>uv</i> de texture associée à la géométrie intersectée par le rayon.
	 */
	private final SVectorUV uv;          
	
	/**
	 * La variable 't' correspond au temps requis au rayon pour intersecter la géometrie.
	 */
	private final double t;					         
	
	/**
	 * La variable refractive_index correspond à l'indice de réfraction du milieu où voyage le rayon.
	 */
	private final double refractive_index;
	
	//----------------
	// CONSTRUCTEUR //
	//----------------
	/**
	 * Constructeur d'un rayon dont l'objectif sera de tenter d'intersecter une géométrie.
	 * 
	 * @param origin - L'origine du rayon.
	 * @param direction - La direction du rayon.
	 * @param refractive_index - L'indice de réfraction du milieu où voyage le rayon.
	 */
	public SRay(SVector3d origin, SVector3d direction, double refractive_index)
	{
		this.origin = origin;
		this.direction = direction;
		
		as_intersected = false;
		is_inside_intersection = false;
		
		geometry = null;
		normal = null;
		uv = null;
		t = MAXIMUM_T;		
		
		this.refractive_index = refractive_index;
	}
	
	/**
	 * Constructeur d'un rayon ayant réalisé une intersection avec une géométrie (sans coordonnée de texture uv).
	 * 
	 * @param origin - L'origine du rayon.
	 * @param direction - La direction du rayon.
	 * @param geometry - La géométrie intersectée.
	 * @param normal - L'orientation de la normale à la surface.
	 * @param t - Le temps pour le rayon afin d'atteindre la géométrie.
	 * @param is_inside_intersection - Le status de l'intersection du rayon avec la géométrie (intersection venant de l'intérieur ou de l'extérieur de la géométrie).
	 * @param refractive_index - L'indice de réfraction du milieu où voyage le rayon.
	 * @throws SConstructorException Si le temps du rayon est inférieur à <i>epsilon</i>.
	 */
	private SRay(SVector3d origin, SVector3d direction, SGeometry geometry, SVector3d normal, double t, boolean is_inside_intersection, double refractive_index)throws SConstructorException
	{
	  this(origin, direction, geometry, normal, null, t, is_inside_intersection, refractive_index);
	}
	
	/**
   * Constructeur d'un rayon ayant réalisé une intersection avec une géométrie.
   * 
   * @param origin - L'origine du rayon.
   * @param direction - La direction du rayon.
   * @param geometry - La géométrie intersectée.
   * @param normal - L'orientation de la normale à la surface.
   * @param t - Le temps pour le rayon afin d'atteindre la géométrie.
   * @param is_inside_intersection - Le status de l'intersection du rayon avec la géométrie (intersection venant de l'intérieur ou de l'extérieur de la géométrie).
   * @param refractive_index - L'indice de réfraction du milieu où voyage le rayon.
   * @throws SConstructorException Si le temps du rayon est inférieur à <i>epsilon</i>.
   */
  private SRay(SVector3d origin, SVector3d direction, SGeometry geometry, SVector3d normal, SVectorUV uv, double t, boolean is_inside_intersection, double refractive_index)throws SConstructorException
  {
    //Vérification pour teste numérique
    if(t < epsilon)
      throw new SConstructorException("Erreur SRay 001 : Pour des raisons numériques, le temps de l'intersection t = " + t + " ne peut pas être inférieur à epsilon = " + epsilon + ".");
    
    this.origin = origin;
    this.direction = direction;
    
    this.as_intersected = true;
    this.is_inside_intersection = is_inside_intersection;
    
    this.geometry = geometry;
    this.normal = normal;
    this.uv = uv;
    this.t = t;
    
    this.refractive_index = refractive_index;
  }
  
  //------------
  // MÉTHODES //
  //------------
  
	/**
	 * Méthode pour définir le temps/distance minimal que doit effectuer un rayon afin de valider une intersection.
	 * Cette valeur est nécessaire pour des raisons numériques. Elle doit être positive et supérieure à un valeur minimal de 1e-6. 
	 * Il peut être affecté en fonction de la position du front clipping plane d'une pyramide de vue (view Frustum).
	 * @param e - La valeur d'epsilon.
	 * @throws SRuntimeException Si la nouvelle valeur d'epsilon est inférieure à la valeur minimale acceptée par la classe.
	 * @see MINIMUM_EPSILON
	 */
	public static void setEpsilon(double e)throws SRuntimeException
	{ 	
		//Mettre la valeur d'epsilon positive
		if(e < MINIMUM_EPSILON)
		  throw new SRuntimeException("Erreur SRay 003 : Pour des raisons numériques, la nouvelle valeur d'epsilon '" + e + "' doit être supérieure à " + MINIMUM_EPSILON + ".");
			
		epsilon = e;
	}
	
	/**
	 * Méthode pour obtenir la valeur d'epsilon présentement en vigueur pour les calculs d'intersection entre les rayons et les géométries.
	 * @return La valeur d'epsilon.
	 */
	public static double getEpsilon()
	{ 
	  return epsilon;
	}
	
	/**
	 * Méthode pour obtenir l'origine du rayon.
	 * @return L'origine du rayon.
	 */
	public SVector3d getOrigin()
	{ 
	  return origin;
	}
	
	/**
	 * Méthode pour obtenir la direction du rayon.
	 * @return La direction du rayon.
	 */
	public SVector3d getDirection()
	{ 
	  return direction; 
	}
		
	/**
	 * Méthode pour obtenir la position d'un rayon après un déplacement de celui-ci.
	 * @param t_value - Le temps/distance à écouler dans le déplacement du rayon.
	 * @return Le vecteur position du rayon après déplacement.
	 */
	public SVector3d getPosition(double t_value)
	{
		//Version lente (usage de plus d'allocation de mémoire)
		//return origin.add( direction.multiply(t_value) );
	  
	  //Version rapide (une allocation en mémoire de SVector3d de moins)
	  return SVector3d.AmultiplyBaddC(t_value, direction, origin);
	}
	
	/**
	 * Méthode pour déterminer si le rayon a effectué une intersection avec une géométrie.
	 * @return <b>true</b> s'il y a eu une intersection et <b>false</b> sinon. 
	 */
	public boolean asIntersected()
	{ 
	  return as_intersected;
	}
	
	/**
	 * Méthode pour déterminer si le rayon a effectué une intersection de l'intérieur de la géométrie.
	 * @return <b>true</b> s'il y a eu une intersection à l'intérieur de la géométrie et <b>false</b> sinon. 
	 * @throws SRuntimeException Si le rayon n'a pas effectué d'intersection.
	 */
	public boolean isInsideIntersection()throws SRuntimeException
	{
	  if(!as_intersected)
	    throw new SRuntimeException("Erreur SRay 004 : Le rayon n'a pas effectué d'intersection, donc ce test n'est pas pertinent.");
	  
	  return is_inside_intersection;
	}
		
	/**
	 * Méthode pour obtenir le temps (ou la distance puisque la direction est unitaire) afin de réaliser 
	 * une intersection entre le rayon et une géométrie. Si le rayon n'a pas été intersecté, la valeur sera <b> l'infini </b>.
	 * @return Le temps/distance pour effectuer l'intersection et <b>l'infini</b> s'il n'y a <b>pas eu d'intersection</b>.
	 */
	public double getT()
	{ 
	  return t;
	}
		
	/**
	 * Méthode pour obtenir l'indice de réfraction du milieu dans lequel le rayon voyage.
	 * @return l'indice de réfraction du milieu où voyage le rayon.
	 */
	public double getRefractiveIndex()
	{ 
	  return refractive_index;
	}
	
	/**
	 * Méthode pour obtenir la position de l'intersection entre le rayon et la géométrie.
	 * @return La position de l'intersection
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, le point d'intersection est indéterminé.
	 */
	public SVector3d getIntersectionPosition()throws SRuntimeException
	{
		//S'il y a intersection, la valeur de "t" correspond au temps de l'intersection 
	  if(as_intersected)
		  return getPosition(t);
		else
			throw new SRuntimeException("Erreur SRay 005 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
	}
	
	/**
	 * Méthode pour obtenir la géométrie qui est en intersection avec le rayon.
	 * @return La géométrie en intersection.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la géométrie est indéterminée.
	 */
	public SGeometry getGeometry()throws SRuntimeException
	{
		if(as_intersected)
			return geometry;
		else
			throw new SRuntimeException("Erreur SRay 006 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
	}
	
	/**
	 * Méthode pour obtenir la normal à la surface de la géométrie en intersection avec le rayon.
	 * @return La normal à la surface à l'endroit de l'intersection.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la normale est indéterminée.
	 */
	public SVector3d getNormal()throws SRuntimeException
	{
		if(as_intersected)
			return normal;
		else
			throw new SRuntimeException("Erreur SRay 007 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
	}

	/**
	 * Méthode pour obtenir la coordonnée <i>uv</i> associée à l'intersection sur la géométrie. 
	 * 
	 * @return La coordonnée uv associé à l'intersection sur la géométrie. 
	 * S'il n'y a pas de coordonnée uv associée à l'intersection, la valeur <b>null</b> sera retournée.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la coordonnée <i>uv</i> est indéterminée.
	 */
	public SVectorUV getUV()throws SRuntimeException
	{
	  if(as_intersected)
	    return uv;
	  else
	    throw new SRuntimeException("Erreur SRay 008 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
	}
	
	/**
	 * Méthode pour déterminer si le rayon intersecté possède une coordonnée de <i>uv</i> de texture.
	 * 
	 * @return <b>true</b> s'il y a une coordonnée <i>uv</i> et <b>false</b> sinon.
	 * @throws SRuntimeException Si le rayon n'a pas été intersecté, il ne peut pas y avoir de coordonnée <i>uv</i> d'attribuée au rayon.
	 */
	public boolean asUV()throws SRuntimeException
	{
	  if(as_intersected)
	    if(uv != null)
	      return true;
	    else
	      return false;
	  else
	    throw new SRuntimeException("Erreur SRay 009 : Le rayon n'a pas effectué d'intersection avec une géométrie.");
	}
	/**
	 * Méthode pour générer un rayon intersecté à partir d'un rayon lancée et de ses caractéristiques définissant l'intersection.
	 * @param geometry - La géométrie qui est en intersection avec le rayon.
	 * @param normal - La normale à la surface de la géométrie intersectée.
	 * @param t - Le temps requis pour se rendre au lieu de l'intersection sur la géométrie.
	 * @param is_inside_intersection - Détermine si l'intersection se fait par l'intérieur de la géométrie (nécessaire s'il y a de la réfraction à évaluer). 
	 * @return Le rayon avec les caractéristiques de l'intersection.
	 * @throws SRuntimeException S'il y a déjà eu une intersection avec ce rayon.
	 */
	public SRay intersection(SGeometry geometry, SVector3d normal, double t, boolean is_inside_intersection)throws SRuntimeException
	{
	  if(as_intersected)
	    throw new SRuntimeException("Erreur SRay 010 : Ce rayon ne peut pas se faire intersecter, car il est présentement déjà intersecté.");
	  else
	    return new SRay(this.origin, this.direction, geometry, normal, t, is_inside_intersection, this.refractive_index);
	}
	
	/**
   * Méthode pour générer un rayon intersecté à partir d'un rayon lancée et de ses caractéristiques définissant l'intersection.
   * @param geometry - La géométrie qui est en intersection avec le rayon.
   * @param normal - La normale à la surface de la géométrie intersectée.
   * @param uv - La coordonnée uv associée à l'intersection.
   * @param t - Le temps requis pour se rendre au lieu de l'intersection sur la géométrie.
   * @param is_inside_intersection - Détermine si l'intersection se fait par l'intérieur de la géométrie (nécessaire s'il y a de la réfraction à évaluer). 
   * @return Le rayon avec les caractéristiques de l'intersection.
   * @throws SRuntimeException S'il y a déjà eu une intersection avec ce rayon.
   */
  public SRay intersection(SGeometry geometry, SVector3d normal, SVectorUV uv, double t, boolean is_inside_intersection)throws SRuntimeException
  {
    if(as_intersected)
      throw new SRuntimeException("Erreur SRay 011 : Ce rayon ne peut pas se faire intersecter, car il est présentement déjà intersecté.");
    else
      return new SRay(this.origin, this.direction, geometry, normal, uv, t, is_inside_intersection, this.refractive_index);
  }
  
	@Override
	public String toString()
	{
		return "[t = " + t + "]";
	}
	
	@Override
	public int compareTo(SRay ray) 
	{
		return Double.compare(this.t, ray.t);
	}
	
	/* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SRay)) {
      return false;
    }
    SRay other = (SRay) obj;
    if (as_intersected != other.as_intersected) {
      return false;
    }
    if (direction == null) {
      if (other.direction != null) {
        return false;
      }
    } else if (!direction.equals(other.direction)) {
      return false;
    }
    if (geometry == null) {
      if (other.geometry != null) {
        return false;
      }
    } else if (!geometry.equals(other.geometry)) {
      return false;
    }
    if (is_inside_intersection != other.is_inside_intersection) {
      return false;
    }
    if (normal == null) {
      if (other.normal != null) {
        return false;
      }
    } else if (!normal.equals(other.normal)) {
      return false;
    }
    if (origin == null) {
      if (other.origin != null) {
        return false;
      }
    } else if (!origin.equals(other.origin)) {
      return false;
    }
    if (Double.doubleToLongBits(refractive_index) != Double.doubleToLongBits(other.refractive_index)) {
      return false;
    }
    if (Double.doubleToLongBits(t) != Double.doubleToLongBits(other.t)) {
      return false;
    }
    return true;
  }
	
}//fin classe SRay
