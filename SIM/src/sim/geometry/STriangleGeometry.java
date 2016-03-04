/**
 * 
 */
package sim.geometry;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SImpossibleNormalizationException;
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>STriangleGeometry</b> représente la géométrie d'un triangle.
 * Un triangle est constitué de trois points non colinéaire permettant de définir une normale à la surface.
 * 
 * @author Simon Vézina
 * @since 2015-02-17
 * @version 2015-12-21
 */
public class STriangleGeometry extends SAbstractPlanarGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POINT };
  
  
	/**
	 * La constante <b>DEFAULT_P0</b> correspond à la position par défaut du point P0 du triangle.
	 */
  protected static final SVector3d DEFAULT_P0 = new SVector3d(0.0, 0.0, 0.0);
	
	/**
   * La constante <b>DEFAULT_P1</b> correspond à la position par défaut du point P1 du triangle.
   */
  protected static final SVector3d DEFAULT_P1 = new SVector3d(0.0, 1.0, 0.0);
	
	/**
   * La constante <b>DEFAULT_P2</b> correspond à la position par défaut du point P2 du triangle.
   */
  protected static final SVector3d DEFAULT_P2 = new SVector3d(1.0, 1.0, 0.0);
	
	//-------------
	// VARIABLES //
	//-------------
	
	/**
	 * La variable <b>P0</b> correspond au 1ier point du triangle. L'ordre des points détermine le sens de la normale à la surface selon la règle de la main droite.
	 */
	protected SVector3d P0;	
	
	/**
   * La variable <b>P1</b> correspond au 2ième point du triangle. L'ordre des points détermine le sens de la normale à la surface selon la règle de la main droite.
   */
	protected SVector3d P1;	
	
	/**
   * La variable <b>P2</b> correspond au 3ième point du triangle. L'ordre des points détermine le sens de la normale à la surface selon la règle de la main droite.
   */
	protected SVector3d P2;	
	
	/**
	 * La variable <b>reading_point</b> correspond au numéro du point qui sera le prochain à être lu lors d'une lecture dans un fichier.
	 */
	protected int reading_point; 
	
	/**
   * La variable <b>normal</b> correspond à la normale à la surface du triangle déterminée par la règle de la main droite dans l'ordre P0, P1 et P2 des points du triangle.
   */
	protected SVector3d normal; 
  
	//---------------------------
	// PARAMÈTRES DE PRÉCALCUL //
	//---------------------------
	
	
	
	//-----------------
	// CONSTRUCTEURS //
	//-----------------
	
	/**
	 * Constructeur d'un triangle par défaut.
	 */
	public STriangleGeometry() 
	{
		this(DEFAULT_P0, DEFAULT_P1, DEFAULT_P2);
	}

	/**
	 * Constructeur d'un triangle avec ses trois points.
	 * 
	 * @param p0 - La position du point P0 du triangle.
	 * @param p1 - La position du point P1 du triangle.
	 * @param p2 - La position du point P2 du triangle.
	 */
	public STriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2)
	{
		this(p0, p1, p2, null);
	}
	
	/**
	 * Constructeur d'un triangle avec une primitive comme parent en référence.
	 * 
	 * @param p0 - La position du point P0 du triangle.
	 * @param p1 - La position du point P1 du triangle.
	 * @param p2 - La position du point P2 du triangle.
	 * @param parent - La primitive parent à cette géométrie.
	 * @throws SConstructorException Si les trois points ne sont pas adéquats pour définir un triangle (ex: colinéaire).
	 */
	public STriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, SPrimitive parent) throws SConstructorException 
	{
		super(parent);
		
		P0 = p0;
		P1 = p1;
		P2 = p2;
		
		reading_point = 3;	//pas d'autre point à lire
		
		try{
		  initialize();
		}catch(SInitializationException e){
		  //Les trois points sont colinéaire ce qui ne permet pas de définir une normale à la surface au triangle. 
		  throw new SConstructorException("Erreur STriangleGeometry 001 : Les points {" + P0 + "," + P1 + "," + P2 + "} ne sont pas adéquats pour définir un triangle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
		
	}

	/**
	 * Constructeur d'une géométrie à partir d'information lue dans un fichier de format txt.
	 * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
	 * 
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si le triangle ne peut pas être construit en raison de mauvais choix de points lus pour définir le triangle.
	 * @see SBufferedReader
	 * @see SPrimitive
	 */
	public STriangleGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
	{
		this(DEFAULT_P0, DEFAULT_P1, DEFAULT_P2, parent);		
		
		reading_point = 0;	//lecture du point P0 a effectuer en premier
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
		  //Les trois points sont colinéaire ce qui ne permet pas de définir une normale à la surface au triangle.
		  throw new SConstructorException("Erreur STriangleGeometry 002 : Les points {" + P0 + "," + P1 + "," + P2 + "} qui ont été lus ne sont pas adéquats pour définir un triangle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
	}
	
	//------------
	// MÉTHODES //
	//------------
	
	@Override
  public int getCodeName()
  {
    return SAbstractGeometry.TRIANGLE_CODE;
  }
	
	/**
	 * Méthode pour obtenir le 1ier point définissant le triangle. Par définition, il correspond à P0.
	 * 
	 * @return Le point P0 du triangle.
	 */
	public SVector3d getP0()
	{
	  return P0;
	}
	
	/**
   * Méthode pour obtenir le 2ième point définissant le triangle. Par définition, il correspond à P1.
   * 
   * @return Le point P1 du triangle.
   */
	public SVector3d getP1()
	{
	  return P1;
	}
	
	/**
   * Méthode pour obtenir le 3ième point définissant le triangle. Par définition, il correspond à P2.
   * 
   * @return Le point P2 du triangle.
   */
	public SVector3d getP2()
	{
	  return P2;
	}
	
	@Override
	public SRay intersection(SRay ray) throws SRuntimeException
	{
		//S'assurer que le rayon n'a rien intersecté auparavant
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur STriangleGeometry 003 : Le rayon a déjà intersecté une autre géométrie.");
				
		return ray;
	}

	@Override
	public void write(BufferedWriter bw) throws IOException
	{
		bw.write(SKeyWordDecoder.KW_TRIANGLE);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les propriétés de la classe SSphereGeometry et ses paramètres hérités
		writeSTriangleGeometryParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}

	/**
	 * Méthode pour écrire les paramètres associés à la classe STriangleGeometry et ses paramètres hérités.
	 * 
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException Si une erreur I/O s'est produite.
	 * @see IOException
	 */
	protected void writeSTriangleGeometryParameter(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_POINT);
		bw.write("\t");
		P0.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_POINT);
		bw.write("\t");
		P1.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_POINT);
		bw.write("\t");
		P2.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
   * Méthode pour évaluer la normale à la surface d'un plan à partir de trois points. 
   * Cette normale sera calculé selon <i>l'ordre de la règle de la main droite</i> et sera <b>unitaire</b>.
   * 
   * @param p0 - Le point représentant l'origine du plan.
   * @param p1 - Le point représentant le point suivant p0 (le 2ième) dans l'énumération des points appartenant à la géométrie dans le plan.
   * @param p2 - Le point représentant le point précédant p0 (le dernier) dans l'énumération des points appartenant à la géométrie dans le plan.
   * @return La normale à la surface dans l'ordre respectant la <i>règle la main droite</i> qui sera unitaire (normalisée). 
   * @throws SImpossibleNormalizationException Si les trois points ne permettent pas de construire une normale pouvant être normalisée. 
   */
  protected SVector3d evaluateNormal(SVector3d p0, SVector3d p1, SVector3d p2) throws SImpossibleNormalizationException
  {
    SVector3d s01 = p1.substract(p0);
    SVector3d s02 = p2.substract(p0);
    
    SVector3d normal = s01.cross(s02);
    
    return normal.normalize();
  }
  
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
    // Évaluer la normale à la surface
    try{
      normal = evaluateNormal(P0, P1, P2);
    }catch(SImpossibleNormalizationException e){
      throw new SInitializationException("Erreur STriangleGeometry XXX : Les trois points du triangle ne permettent pas de construire un vecteur normale à la surface pouvant être normalisée." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
    
	}

	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException
	{
		switch(code)
		{
			case SKeyWordDecoder.CODE_POINT :	readPoint(remaining_line); return true;
													
			default : return false;
		}
	}

	/**
	 * Méthode pour faire la lecture d'un point et l'affectation dépendra du numéro du point en lecture déterminé par la variable <i>reading_point</i>.
	 * 
	 * @param remaining_line - L'expression en string du vecteur positionnant le point du triangle.
	 * @throws SReadingException - S'il y a une erreur de lecture.
	 */
	private void readPoint(String remaining_line)throws SReadingException
	{
		switch(reading_point)
		{
			case 0 : 	P0 = new SVector3d(remaining_line);	break;
			case 1 : 	P1 = new SVector3d(remaining_line); break;
			case 2 : 	P2 = new SVector3d(remaining_line);	break;
			
			default : throw new SReadingException("Erreur STriangleGeometry 004 : Il y a déjà 3 points de défini dans ce triangle.");
		}
		
		reading_point++;			
	}
	
	@Override
	public boolean isClosedGeometry()
	{ 
	  return false;
	}

	@Override
	public boolean isInside(SVector3d v)
	{ 
	  return false;
	}

  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    return false;
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    return evaluateFrontIntersectionNormal(ray.getDirection(), normal);
  }

  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur STriangleGeometry 005 : La méthode n'a pas été implémentée.");
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
    return SKeyWordDecoder.KW_TRIANGLE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe STriangle
