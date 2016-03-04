/**
 * 
 */
package sim.geometry;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SRuntimeException;
import sim.exception.SConstructorException;
import sim.graphics.SPrimitive;
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SSphereGeometry</b> représente la géométrie d'une sphère. 
 * 
 * @author Simon Vézina
 * @since 2014-12-30
 * @version 2015-12-21
 */
public class SSphereGeometry extends SAbstractGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_RAY };
  
	/**
	 * La constante 'DEFAULT_POSITION' correspond à la position par défaut d'une sphère étant à l'origine.
	 */
  private static final SVector3d DEFAULT_POSITION = new SVector3d(0.0, 0.0, 0.0);	
   
  /**
   * La constante 'DEFAULT_RAY' correspond au rayon par défaut d'une sphère étant égale à {@value}.
   */
	private static final double DEFAULT_RAY = 1.0;									                
	
	//-------------
  // VARIABLES //
  //-------------
	
	/**
	 * La variable 'position' correspond à la position de la sphère.
	 */
	private SVector3d position;	
	
	/**
	 * La variable 'R' correspond au rayon de la sphère. Cette valeur ne peut pas être négative.
	 */
	private double R;				    
	
	//-----------------
	// CONSTRUCTEURS //
	//-----------------
	
	/**
	 * Constructeur qui initialise une sphère unitaire centrée à l'origine.
	 */
	public SSphereGeometry()
	{
		this(DEFAULT_POSITION, DEFAULT_RAY);
	}
	
	/**
	 * Constructeur qui initialise une sphère avec une position et un rayon.
	 * 
	 * @param position - La position de la sphère.
	 * @param ray - Le rayon de la sphère.
	 */
	public SSphereGeometry(SVector3d position, double ray)
	{
		this(position, ray, null);
	}
	
	/**
	 * Constructeur de la géométrie d'une sphère avec paramètres.
	 * 
	 * @param position - La position du centre de la sphère.
	 * @param ray - Le rayon de la sphère. S'il est négatif, il sera affecté à une valeur positive.
	 * @param parent - La primitive parent à cette géométrie.
	 * @throws SConstructorException Si le rayon de la sphère est négatif.
	 */
	public SSphereGeometry(SVector3d position, double ray, SPrimitive parent)throws SConstructorException
	{
		super(parent);
		
		//Vérification que le rayon soit positif
		if(ray < 0.0)
		  throw new SConstructorException("Erreur SSphereGeometry 001 : Une sphère de rayon R = " + ray + " qui est négatif n'est pas une définition valide.");
		
		this.position = position;
		R = ray;
		
		try{
		  initialize();
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SSphereGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Constructeur d'une sphère à partir d'information lue dans un fichier de format txt.
	 * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
	 * 
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
	 * @throws IOException Si une erreur de de type I/O est lancée.
	 * @throws SConstructorException Si une ereur est survenue lors de la construction de la géométrie.
	 * @see SBufferedReader
	 * @see SPrimitive
	 */
	public SSphereGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
	{
		this(DEFAULT_POSITION, DEFAULT_RAY, parent);		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SSphereGeometry 003 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	//------------
	// MÉTHODES //
	//------------
	
	@Override
  public int getCodeName()
  {
    return SAbstractGeometry.SPHERE_CODE;
  }
	
	@Override
	public SRay intersection(SRay ray) throws SRuntimeException 
	{
		// S'assurer que le rayon n'a rien intersecté auparavant
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur SSphereGeometry 004 : Le rayon a déjà intersecté une autre géométrie.");
	
		return ray;
	}

	@Override
	public boolean isClosedGeometry()
	{ 
	  return true;
	}
	
	@Override
	public boolean isInside(SVector3d v)
	{
		// Vérifier si le vecteur est à l'intérieur de la sphère
	  if(SGeometricUtil.isOnSphereSurface(position, R, v) < 0)
		  return true;
		else
		  return false;
	}
	
	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
	{
		switch(code)
		{
			case SKeyWordDecoder.CODE_POSITION : position = new SVector3d(remaining_line); return true;
													
			case SKeyWordDecoder.CODE_RAY : 		 R = readDoubleGreaterThanZero(remaining_line, SKeyWordDecoder.KW_RAY); return true;
					
			default : return false;
		}
	}
	
	@Override
	public void write(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_SPHERE);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les propriétés de la classe SSphereGeometry et ses paramètres hérités
		writeSSphereGeometryParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SSphereGeometry et ses paramètres hérités.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException Si une erreur I/O s'est produite.
	 * @see IOException
	 */
	protected void writeSSphereGeometryParameter(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_POSITION);
		bw.write("\t");
		position.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_RAY);
		bw.write("\t\t");
		bw.write(Double.toString(R));
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
		
	}
	
	@Override
	protected boolean isInsideIntersection(SRay ray, double intersection_t)
	{
	  //Évaluation de la normale à la surface extérieure à l'endroit de l'intersection (normalisée)
    SVector3d ext_normal = ray.getPosition(intersection_t).substract(position).normalize();
    
    //Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure de la sphère
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return false;            //orientation opposée, donc intersection venant de l'extérieur
    else
      return true;             //orientation dans le même sens, donc intersection venant de l'intérieur
	}

	@Override
	protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
	{
	  // Évaluation de la normale à la surface extérieure à l'endroit de l'intersection (normalisée)
    SVector3d ext_normal = ray.getPosition(intersection_t).substract(position).normalize();
    
    // Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure de la sphère
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return ext_normal;                            //orientation opposée, donc intersection venant de l'extérieur
    else
      return ext_normal.multiply(-1.0);             //orientation dans le même sens, donc intersection venant de l'intérieur
	}
	  
	@Override
	protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
	  return SGeometricUVMapping.sphereUVMapping(position, intersection_t, ray.getPosition(intersection_t));
  }

	@Override
  protected void readingInitialization() throws SInitializationException
  {
    super.readingInitialization();
    
    initialize();
  }
	
	@Override
	public String getReadableName()
	{
	  return SKeyWordDecoder.KW_SPHERE;
	}
	
	@Override
	public String[] getReadableParameterName()
	{
	  String[] other_parameters = super.getReadableParameterName();
	  
	  return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
	}
	
}//fin de la classe SSphereGeometry
