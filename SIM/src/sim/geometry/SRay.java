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
 * La classe <b>SRay</b> repr�sente un rayon pouvant r�aliser une intersection avec une g�om�trie. 
 * <p>Cette classe impl�mente l'interface <b>Comparable</b> (permettant les usages des m�thodes de triage des librairies de base de java). 
 * La comparaison sera effectu�e sur la <b>valeur du champ local t</b> qui repr�sente le <b>temps afin d'intersecter une g�om�trie</b>. 
 * S'il n'y a <b>pas d'intersection</b>, la valeur de t sera �gale � <b>l'infini</b>.</p> 
 *
 * @author Simon V�zina
 * @since 2014-12-30
 * @version 2015-12-28
 */
public class SRay implements Comparable<SRay> {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante 'MINIMUM_EPSILON' correspond � <b>la plus petite valeur du temps mininal de parcours</b> qu'un rayon doit effectuer avant de pouvoir intersecter une g�om�trie. 
   * Cette contrainte est n�cessaire pour une raison de stabilit� num�rique. Elle est pr�sentement d�finie � {@value} elle et doit <b>toujours �tre sup�rieure � SMath.EPSILON</b>.
   */
  public static final double MINIMUM_EPSILON = 1e-8;	//
	
  /**
   * La constante 'MAXIMAM_T' correspond � un temps signifiant que le rayon n'a pas r�ussi � intersecter de g�om�trie. 
   * Cette valeur correspond � un <b>temps infini</b>.
   */
	private static final double MAXIMUM_T = SMath.INFINITY;	
	
	/**
	 * La constante 'DEFAULT_REFRACTIV_INDEX' correspond � la valeur de l'indice de r�fraction par d�faut o� un rayon voyage.
	 * Cet indice de r�fraction est �gal � celui du vide (n = {@value}).
	 */
	public static final double DEFAULT_REFRACTIVE_INDEX = 1.0;			  
	
	//-----------------------
  // VARIABLES STATIQUES //
  //-----------------------
	
	/**
   * La variable 'epsilon' correspond au temps mininal de parcours qu'un rayon doit effectuer avant de pouvoir intersecter une g�om�trie.
   */
  private static double epsilon = MINIMUM_EPSILON;    
  
  //-------------
  // VARIABLES //
  //-------------
  
	/**
	 * La variable 'origin' correspond � l'origine du rayon (d'o� est lanc� le rayon).
	 */
	private final SVector3d origin;		  
	
	/**
	 * La variable 'direction' correspond � la direction du rayon (dans quelle direction le rayon voyagera).
	 * On peut �galement comparer cette variable � la vitesse du rayon.
	 */
	private final SVector3d direction;	
	
	/**
	 * La variable 'as_intersected' d�termine si le rayon a d�j� r�alis� une intersection.
	 */
	private final boolean as_intersected;	         
	
	/**
	 * La variable 'is_inside_intersection' d�termine si l'intersection (s'il y en a une) sur la g�om�trie s'est r�alis�e � l'int�rieur de celle-ci.
	 */
	private final boolean is_inside_intersection;  //d�termine si l'intersection s'est effectu�e � l'int�rieur de la g�om�trie
	
	/**
	 * La varibale 'geometry' correspond � la g�om�trie intersect� par le rayon.
	 */
	private final SGeometry geometry;
	
	/**
	 * La variable 'normal' correspond � la normale � la surface sur la g�om�trie intersect�e � l'endroit o� l'intersection a �t� r�alis�e.
	 */
	private final SVector3d normal;			     
	
	/**
	 * La variable 'uv' correspond � la coordonn�e <i>uv</i> de texture associ�e � la g�om�trie intersect�e par le rayon.
	 */
	private final SVectorUV uv;          
	
	/**
	 * La variable 't' correspond au temps requis au rayon pour intersecter la g�ometrie.
	 */
	private final double t;					         
	
	/**
	 * La variable refractive_index correspond � l'indice de r�fraction du milieu o� voyage le rayon.
	 */
	private final double refractive_index;
	
	//----------------
	// CONSTRUCTEUR //
	//----------------
	/**
	 * Constructeur d'un rayon dont l'objectif sera de tenter d'intersecter une g�om�trie.
	 * 
	 * @param origin - L'origine du rayon.
	 * @param direction - La direction du rayon.
	 * @param refractive_index - L'indice de r�fraction du milieu o� voyage le rayon.
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
	 * Constructeur d'un rayon ayant r�alis� une intersection avec une g�om�trie (sans coordonn�e de texture uv).
	 * 
	 * @param origin - L'origine du rayon.
	 * @param direction - La direction du rayon.
	 * @param geometry - La g�om�trie intersect�e.
	 * @param normal - L'orientation de la normale � la surface.
	 * @param t - Le temps pour le rayon afin d'atteindre la g�om�trie.
	 * @param is_inside_intersection - Le status de l'intersection du rayon avec la g�om�trie (intersection venant de l'int�rieur ou de l'ext�rieur de la g�om�trie).
	 * @param refractive_index - L'indice de r�fraction du milieu o� voyage le rayon.
	 * @throws SConstructorException Si le temps du rayon est inf�rieur � <i>epsilon</i>.
	 */
	private SRay(SVector3d origin, SVector3d direction, SGeometry geometry, SVector3d normal, double t, boolean is_inside_intersection, double refractive_index)throws SConstructorException
	{
	  this(origin, direction, geometry, normal, null, t, is_inside_intersection, refractive_index);
	}
	
	/**
   * Constructeur d'un rayon ayant r�alis� une intersection avec une g�om�trie.
   * 
   * @param origin - L'origine du rayon.
   * @param direction - La direction du rayon.
   * @param geometry - La g�om�trie intersect�e.
   * @param normal - L'orientation de la normale � la surface.
   * @param t - Le temps pour le rayon afin d'atteindre la g�om�trie.
   * @param is_inside_intersection - Le status de l'intersection du rayon avec la g�om�trie (intersection venant de l'int�rieur ou de l'ext�rieur de la g�om�trie).
   * @param refractive_index - L'indice de r�fraction du milieu o� voyage le rayon.
   * @throws SConstructorException Si le temps du rayon est inf�rieur � <i>epsilon</i>.
   */
  private SRay(SVector3d origin, SVector3d direction, SGeometry geometry, SVector3d normal, SVectorUV uv, double t, boolean is_inside_intersection, double refractive_index)throws SConstructorException
  {
    //V�rification pour teste num�rique
    if(t < epsilon)
      throw new SConstructorException("Erreur SRay 001 : Pour des raisons num�riques, le temps de l'intersection t = " + t + " ne peut pas �tre inf�rieur � epsilon = " + epsilon + ".");
    
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
  // M�THODES //
  //------------
  
	/**
	 * M�thode pour d�finir le temps/distance minimal que doit effectuer un rayon afin de valider une intersection.
	 * Cette valeur est n�cessaire pour des raisons num�riques. Elle doit �tre positive et sup�rieure � un valeur minimal de 1e-6. 
	 * Il peut �tre affect� en fonction de la position du front clipping plane d'une pyramide de vue (view Frustum).
	 * @param e - La valeur d'epsilon.
	 * @throws SRuntimeException Si la nouvelle valeur d'epsilon est inf�rieure � la valeur minimale accept�e par la classe.
	 * @see MINIMUM_EPSILON
	 */
	public static void setEpsilon(double e)throws SRuntimeException
	{ 	
		//Mettre la valeur d'epsilon positive
		if(e < MINIMUM_EPSILON)
		  throw new SRuntimeException("Erreur SRay 003 : Pour des raisons num�riques, la nouvelle valeur d'epsilon '" + e + "' doit �tre sup�rieure � " + MINIMUM_EPSILON + ".");
			
		epsilon = e;
	}
	
	/**
	 * M�thode pour obtenir la valeur d'epsilon pr�sentement en vigueur pour les calculs d'intersection entre les rayons et les g�om�tries.
	 * @return La valeur d'epsilon.
	 */
	public static double getEpsilon()
	{ 
	  return epsilon;
	}
	
	/**
	 * M�thode pour obtenir l'origine du rayon.
	 * @return L'origine du rayon.
	 */
	public SVector3d getOrigin()
	{ 
	  return origin;
	}
	
	/**
	 * M�thode pour obtenir la direction du rayon.
	 * @return La direction du rayon.
	 */
	public SVector3d getDirection()
	{ 
	  return direction; 
	}
		
	/**
	 * M�thode pour obtenir la position d'un rayon apr�s un d�placement de celui-ci.
	 * @param t_value - Le temps/distance � �couler dans le d�placement du rayon.
	 * @return Le vecteur position du rayon apr�s d�placement.
	 */
	public SVector3d getPosition(double t_value)
	{
		//Version lente (usage de plus d'allocation de m�moire)
		//return origin.add( direction.multiply(t_value) );
	  
	  //Version rapide (une allocation en m�moire de SVector3d de moins)
	  return SVector3d.AmultiplyBaddC(t_value, direction, origin);
	}
	
	/**
	 * M�thode pour d�terminer si le rayon a effectu� une intersection avec une g�om�trie.
	 * @return <b>true</b> s'il y a eu une intersection et <b>false</b> sinon. 
	 */
	public boolean asIntersected()
	{ 
	  return as_intersected;
	}
	
	/**
	 * M�thode pour d�terminer si le rayon a effectu� une intersection de l'int�rieur de la g�om�trie.
	 * @return <b>true</b> s'il y a eu une intersection � l'int�rieur de la g�om�trie et <b>false</b> sinon. 
	 * @throws SRuntimeException Si le rayon n'a pas effectu� d'intersection.
	 */
	public boolean isInsideIntersection()throws SRuntimeException
	{
	  if(!as_intersected)
	    throw new SRuntimeException("Erreur SRay 004 : Le rayon n'a pas effectu� d'intersection, donc ce test n'est pas pertinent.");
	  
	  return is_inside_intersection;
	}
		
	/**
	 * M�thode pour obtenir le temps (ou la distance puisque la direction est unitaire) afin de r�aliser 
	 * une intersection entre le rayon et une g�om�trie. Si le rayon n'a pas �t� intersect�, la valeur sera <b> l'infini </b>.
	 * @return Le temps/distance pour effectuer l'intersection et <b>l'infini</b> s'il n'y a <b>pas eu d'intersection</b>.
	 */
	public double getT()
	{ 
	  return t;
	}
		
	/**
	 * M�thode pour obtenir l'indice de r�fraction du milieu dans lequel le rayon voyage.
	 * @return l'indice de r�fraction du milieu o� voyage le rayon.
	 */
	public double getRefractiveIndex()
	{ 
	  return refractive_index;
	}
	
	/**
	 * M�thode pour obtenir la position de l'intersection entre le rayon et la g�om�trie.
	 * @return La position de l'intersection
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, le point d'intersection est ind�termin�.
	 */
	public SVector3d getIntersectionPosition()throws SRuntimeException
	{
		//S'il y a intersection, la valeur de "t" correspond au temps de l'intersection 
	  if(as_intersected)
		  return getPosition(t);
		else
			throw new SRuntimeException("Erreur SRay 005 : Le rayon n'a pas effectu� d'intersection avec une g�om�trie.");
	}
	
	/**
	 * M�thode pour obtenir la g�om�trie qui est en intersection avec le rayon.
	 * @return La g�om�trie en intersection.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la g�om�trie est ind�termin�e.
	 */
	public SGeometry getGeometry()throws SRuntimeException
	{
		if(as_intersected)
			return geometry;
		else
			throw new SRuntimeException("Erreur SRay 006 : Le rayon n'a pas effectu� d'intersection avec une g�om�trie.");
	}
	
	/**
	 * M�thode pour obtenir la normal � la surface de la g�om�trie en intersection avec le rayon.
	 * @return La normal � la surface � l'endroit de l'intersection.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la normale est ind�termin�e.
	 */
	public SVector3d getNormal()throws SRuntimeException
	{
		if(as_intersected)
			return normal;
		else
			throw new SRuntimeException("Erreur SRay 007 : Le rayon n'a pas effectu� d'intersection avec une g�om�trie.");
	}

	/**
	 * M�thode pour obtenir la coordonn�e <i>uv</i> associ�e � l'intersection sur la g�om�trie. 
	 * 
	 * @return La coordonn�e uv associ� � l'intersection sur la g�om�trie. 
	 * S'il n'y a pas de coordonn�e uv associ�e � l'intersection, la valeur <b>null</b> sera retourn�e.
	 * @throws SRuntimeException S'il n'y a pas eu d'intersection avec ce rayon, la coordonn�e <i>uv</i> est ind�termin�e.
	 */
	public SVectorUV getUV()throws SRuntimeException
	{
	  if(as_intersected)
	    return uv;
	  else
	    throw new SRuntimeException("Erreur SRay 008 : Le rayon n'a pas effectu� d'intersection avec une g�om�trie.");
	}
	
	/**
	 * M�thode pour d�terminer si le rayon intersect� poss�de une coordonn�e de <i>uv</i> de texture.
	 * 
	 * @return <b>true</b> s'il y a une coordonn�e <i>uv</i> et <b>false</b> sinon.
	 * @throws SRuntimeException Si le rayon n'a pas �t� intersect�, il ne peut pas y avoir de coordonn�e <i>uv</i> d'attribu�e au rayon.
	 */
	public boolean asUV()throws SRuntimeException
	{
	  if(as_intersected)
	    if(uv != null)
	      return true;
	    else
	      return false;
	  else
	    throw new SRuntimeException("Erreur SRay 009 : Le rayon n'a pas effectu� d'intersection avec une g�om�trie.");
	}
	/**
	 * M�thode pour g�n�rer un rayon intersect� � partir d'un rayon lanc�e et de ses caract�ristiques d�finissant l'intersection.
	 * @param geometry - La g�om�trie qui est en intersection avec le rayon.
	 * @param normal - La normale � la surface de la g�om�trie intersect�e.
	 * @param t - Le temps requis pour se rendre au lieu de l'intersection sur la g�om�trie.
	 * @param is_inside_intersection - D�termine si l'intersection se fait par l'int�rieur de la g�om�trie (n�cessaire s'il y a de la r�fraction � �valuer). 
	 * @return Le rayon avec les caract�ristiques de l'intersection.
	 * @throws SRuntimeException S'il y a d�j� eu une intersection avec ce rayon.
	 */
	public SRay intersection(SGeometry geometry, SVector3d normal, double t, boolean is_inside_intersection)throws SRuntimeException
	{
	  if(as_intersected)
	    throw new SRuntimeException("Erreur SRay 010 : Ce rayon ne peut pas se faire intersecter, car il est pr�sentement d�j� intersect�.");
	  else
	    return new SRay(this.origin, this.direction, geometry, normal, t, is_inside_intersection, this.refractive_index);
	}
	
	/**
   * M�thode pour g�n�rer un rayon intersect� � partir d'un rayon lanc�e et de ses caract�ristiques d�finissant l'intersection.
   * @param geometry - La g�om�trie qui est en intersection avec le rayon.
   * @param normal - La normale � la surface de la g�om�trie intersect�e.
   * @param uv - La coordonn�e uv associ�e � l'intersection.
   * @param t - Le temps requis pour se rendre au lieu de l'intersection sur la g�om�trie.
   * @param is_inside_intersection - D�termine si l'intersection se fait par l'int�rieur de la g�om�trie (n�cessaire s'il y a de la r�fraction � �valuer). 
   * @return Le rayon avec les caract�ristiques de l'intersection.
   * @throws SRuntimeException S'il y a d�j� eu une intersection avec ce rayon.
   */
  public SRay intersection(SGeometry geometry, SVector3d normal, SVectorUV uv, double t, boolean is_inside_intersection)throws SRuntimeException
  {
    if(as_intersected)
      throw new SRuntimeException("Erreur SRay 011 : Ce rayon ne peut pas se faire intersecter, car il est pr�sentement d�j� intersect�.");
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
