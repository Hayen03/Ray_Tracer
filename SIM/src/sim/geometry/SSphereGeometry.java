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
 * La classe <b>SSphereGeometry</b> repr�sente la g�om�trie d'une sph�re. 
 * 
 * @author Simon V�zina
 * @since 2014-12-30
 * @version 2015-12-21
 */
public class SSphereGeometry extends SAbstractGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_RAY };
  
	/**
	 * La constante 'DEFAULT_POSITION' correspond � la position par d�faut d'une sph�re �tant � l'origine.
	 */
  private static final SVector3d DEFAULT_POSITION = new SVector3d(0.0, 0.0, 0.0);	
   
  /**
   * La constante 'DEFAULT_RAY' correspond au rayon par d�faut d'une sph�re �tant �gale � {@value}.
   */
	private static final double DEFAULT_RAY = 1.0;									                
	
	//-------------
  // VARIABLES //
  //-------------
	
	/**
	 * La variable 'position' correspond � la position de la sph�re.
	 */
	private SVector3d position;	
	
	/**
	 * La variable 'R' correspond au rayon de la sph�re. Cette valeur ne peut pas �tre n�gative.
	 */
	private double R;				    
	
	//-----------------
	// CONSTRUCTEURS //
	//-----------------
	
	/**
	 * Constructeur qui initialise une sph�re unitaire centr�e � l'origine.
	 */
	public SSphereGeometry()
	{
		this(DEFAULT_POSITION, DEFAULT_RAY);
	}
	
	/**
	 * Constructeur qui initialise une sph�re avec une position et un rayon.
	 * 
	 * @param position - La position de la sph�re.
	 * @param ray - Le rayon de la sph�re.
	 */
	public SSphereGeometry(SVector3d position, double ray)
	{
		this(position, ray, null);
	}
	
	/**
	 * Constructeur de la g�om�trie d'une sph�re avec param�tres.
	 * 
	 * @param position - La position du centre de la sph�re.
	 * @param ray - Le rayon de la sph�re. S'il est n�gatif, il sera affect� � une valeur positive.
	 * @param parent - La primitive parent � cette g�om�trie.
	 * @throws SConstructorException Si le rayon de la sph�re est n�gatif.
	 */
	public SSphereGeometry(SVector3d position, double ray, SPrimitive parent)throws SConstructorException
	{
		super(parent);
		
		//V�rification que le rayon soit positif
		if(ray < 0.0)
		  throw new SConstructorException("Erreur SSphereGeometry 001 : Une sph�re de rayon R = " + ray + " qui est n�gatif n'est pas une d�finition valide.");
		
		this.position = position;
		R = ray;
		
		try{
		  initialize();
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SSphereGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Constructeur d'une sph�re � partir d'information lue dans un fichier de format txt.
	 * Puisqu'une g�om�trie est construite � l'int�rieure d'une primitive, une r�f�rence � celle-ci doit �tre int�gr�e au constructeur pour y a voir acc�s.
	 * 
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @param parent - La primitive qui fait la construction de cette g�om�trie (qui est le parent).
	 * @throws IOException Si une erreur de de type I/O est lanc�e.
	 * @throws SConstructorException Si une ereur est survenue lors de la construction de la g�om�trie.
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
	// M�THODES //
	//------------
	
	@Override
  public int getCodeName()
  {
    return SAbstractGeometry.SPHERE_CODE;
  }
	
	@Override
	public SRay intersection(SRay ray) throws SRuntimeException 
	{
		// S'assurer que le rayon n'a rien intersect� auparavant
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur SSphereGeometry 004 : Le rayon a d�j� intersect� une autre g�om�trie.");
	
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
		// V�rifier si le vecteur est � l'int�rieur de la sph�re
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
		
		//�crire les propri�t�s de la classe SSphereGeometry et ses param�tres h�rit�s
		writeSSphereGeometryParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * M�thode pour �crire les param�tres associ�s � la classe SSphereGeometry et ses param�tres h�rit�s.
	 * @param bw - Le BufferedWriter �crivant l'information dans un fichier txt.
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
   * M�thode pour faire l'initialisation de l'objet apr�s sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
		
	}
	
	@Override
	protected boolean isInsideIntersection(SRay ray, double intersection_t)
	{
	  //�valuation de la normale � la surface ext�rieure � l'endroit de l'intersection (normalis�e)
    SVector3d ext_normal = ray.getPosition(intersection_t).substract(position).normalize();
    
    //V�rifier si le rayon poss�de une orientation dans le sens oppos� � la normal ext�rieure de la sph�re
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return false;            //orientation oppos�e, donc intersection venant de l'ext�rieur
    else
      return true;             //orientation dans le m�me sens, donc intersection venant de l'int�rieur
	}

	@Override
	protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
	{
	  // �valuation de la normale � la surface ext�rieure � l'endroit de l'intersection (normalis�e)
    SVector3d ext_normal = ray.getPosition(intersection_t).substract(position).normalize();
    
    // V�rifier si le rayon poss�de une orientation dans le sens oppos� � la normal ext�rieure de la sph�re
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return ext_normal;                            //orientation oppos�e, donc intersection venant de l'ext�rieur
    else
      return ext_normal.multiply(-1.0);             //orientation dans le m�me sens, donc intersection venant de l'int�rieur
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
