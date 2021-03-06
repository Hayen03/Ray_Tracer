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
import sim.math.SMath;
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>STriangleGeometry</b> repr�sente la g�om�trie d'un triangle.
 * Un triangle est constitu� de trois points non colin�aire permettant de d�finir une normale � la surface.
 * 
 * @author Simon V�zina
 * @since 2015-02-17
 * @version 2015-12-21
 */
public class STriangleGeometry extends SAbstractPlanarGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POINT };
  
  
	/**
	 * La constante <b>DEFAULT_P0</b> correspond � la position par d�faut du point P0 du triangle.
	 */
  protected static final SVector3d DEFAULT_P0 = new SVector3d(0.0, 0.0, 0.0);
	
	/**
   * La constante <b>DEFAULT_P1</b> correspond � la position par d�faut du point P1 du triangle.
   */
  protected static final SVector3d DEFAULT_P1 = new SVector3d(0.0, 1.0, 0.0);
	
	/**
   * La constante <b>DEFAULT_P2</b> correspond � la position par d�faut du point P2 du triangle.
   */
  protected static final SVector3d DEFAULT_P2 = new SVector3d(1.0, 1.0, 0.0);
	
	//-------------
	// VARIABLES //
	//-------------
	
	/**
	 * La variable <b>P0</b> correspond au 1ier point du triangle. L'ordre des points d�termine le sens de la normale � la surface selon la r�gle de la main droite.
	 */
	protected SVector3d P0;	
	
	/**
   * La variable <b>P1</b> correspond au 2i�me point du triangle. L'ordre des points d�termine le sens de la normale � la surface selon la r�gle de la main droite.
   */
	protected SVector3d P1;	
	
	/**
   * La variable <b>P2</b> correspond au 3i�me point du triangle. L'ordre des points d�termine le sens de la normale � la surface selon la r�gle de la main droite.
   */
	protected SVector3d P2;	
	
	/**
	 * La variable <b>reading_point</b> correspond au num�ro du point qui sera le prochain � �tre lu lors d'une lecture dans un fichier.
	 */
	protected int reading_point; 
	
	/**
   * La variable <b>normal</b> correspond � la normale � la surface du triangle d�termin�e par la r�gle de la main droite dans l'ordre P0, P1 et P2 des points du triangle.
   */
	protected SVector3d normal; 
	
	//---------------------------
	// PARAM�TRES DE PR�CALCUL //
	//---------------------------
	
	private SVector3d u01, u02, u12;
	
	//-----------------
	// CONSTRUCTEURS //
	//-----------------
	
	/**
	 * Constructeur d'un triangle par d�faut.
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
	 * Constructeur d'un triangle avec une primitive comme parent en r�f�rence.
	 * 
	 * @param p0 - La position du point P0 du triangle.
	 * @param p1 - La position du point P1 du triangle.
	 * @param p2 - La position du point P2 du triangle.
	 * @param parent - La primitive parent � cette g�om�trie.
	 * @throws SConstructorException Si les trois points ne sont pas ad�quats pour d�finir un triangle (ex: colin�aire).
	 */
	public STriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, SPrimitive parent) throws SConstructorException 
	{
		super(parent);
		
		P0 = p0;
		P1 = p1;
		P2 = p2;
		
		reading_point = 3;	//pas d'autre point � lire
		
		try{
		  initialize();
		}catch(SInitializationException e){
		  //Les trois points sont colin�aire ce qui ne permet pas de d�finir une normale � la surface au triangle. 
		  throw new SConstructorException("Erreur STriangleGeometry 001 : Les points {" + P0 + "," + P1 + "," + P2 + "} ne sont pas ad�quats pour d�finir un triangle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
		
	}

	/**
	 * Constructeur d'une g�om�trie � partir d'information lue dans un fichier de format txt.
	 * Puisqu'une g�om�trie est construite � l'int�rieure d'une primitive, une r�f�rence � celle-ci doit �tre int�gr�e au constructeur pour y a voir acc�s.
	 * 
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @param parent - La primitive qui fait la construction de cette g�om�trie (qui est le parent).
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lanc�e.
	 * @throws SConstructorException Si le triangle ne peut pas �tre construit en raison de mauvais choix de points lus pour d�finir le triangle.
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
		  //Les trois points sont colin�aire ce qui ne permet pas de d�finir une normale � la surface au triangle.
		  throw new SConstructorException("Erreur STriangleGeometry 002 : Les points {" + P0 + "," + P1 + "," + P2 + "} qui ont �t� lus ne sont pas ad�quats pour d�finir un triangle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
	}
	
	//------------
	// M�THODES //
	//------------
	
	@Override
  public int getCodeName()
  {
    return SAbstractGeometry.TRIANGLE_CODE;
  }
	
	/**
	 * M�thode pour obtenir le 1ier point d�finissant le triangle. Par d�finition, il correspond � P0.
	 * 
	 * @return Le point P0 du triangle.
	 */
	public SVector3d getP0()
	{
	  return P0;
	}
	
	/**
   * M�thode pour obtenir le 2i�me point d�finissant le triangle. Par d�finition, il correspond � P1.
   * 
   * @return Le point P1 du triangle.
   */
	public SVector3d getP1()
	{
	  return P1;
	}
	
	/**
   * M�thode pour obtenir le 3i�me point d�finissant le triangle. Par d�finition, il correspond � P2.
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
		//S'assurer que le rayon n'a rien intersect� auparavant
		if(ray.asIntersected())
			throw new SRuntimeException("Erreur STriangleGeometry 003 : Le rayon a d�j� intersect� une autre g�om�trie.");
		
		double[] ints = SGeometricIntersection.planeIntersection(ray, P0, normal);
		if (ints.length == 0 || ints[0] < SMath.EPSILON)
			return ray;
/*
 		double ti = normal.dot(P0.substract(ray.getOrigin()));
		SVector3d ri = ray.getOrigin().add(ray.getDirection().multiply(ti));
 		
		SVector3d s1 = P1.substract(P0);
		SVector3d s2 = P2.substract(P0);
		SVector3d w = ri.substract(P0);
		
		double 	ws1 = w.dot(s1),
				ws2 = w.dot(s2),
				s2s2 = s2.dot(s2),
				s1s1 = s1.dot(s1),
				s1s2 = s1.dot(s2),
				denominateur = s1s1*s2s2 - s1s2*s1s2;
		
		double 	t1 = (ws1*s2s2 - ws2*s1s2)/denominateur,
				t2 = (ws2*s1s1 - ws1*s1s2)/denominateur;
*/
		
		return ray;
	}

	@Override
	public void write(BufferedWriter bw) throws IOException
	{
		bw.write(SKeyWordDecoder.KW_TRIANGLE);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//�crire les propri�t�s de la classe SSphereGeometry et ses param�tres h�rit�s
		writeSTriangleGeometryParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}

	/**
	 * M�thode pour �crire les param�tres associ�s � la classe STriangleGeometry et ses param�tres h�rit�s.
	 * 
	 * @param bw - Le BufferedWriter �crivant l'information dans un fichier txt.
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
   * M�thode pour �valuer la normale � la surface d'un plan � partir de trois points. 
   * Cette normale sera calcul� selon <i>l'ordre de la r�gle de la main droite</i> et sera <b>unitaire</b>.
   * 
   * @param p0 - Le point repr�sentant l'origine du plan.
   * @param p1 - Le point repr�sentant le point suivant p0 (le 2i�me) dans l'�num�ration des points appartenant � la g�om�trie dans le plan.
   * @param p2 - Le point repr�sentant le point pr�c�dant p0 (le dernier) dans l'�num�ration des points appartenant � la g�om�trie dans le plan.
   * @return La normale � la surface dans l'ordre respectant la <i>r�gle la main droite</i> qui sera unitaire (normalis�e). 
   * @throws SImpossibleNormalizationException Si les trois points ne permettent pas de construire une normale pouvant �tre normalis�e. 
   */
  protected SVector3d evaluateNormal(SVector3d p0, SVector3d p1, SVector3d p2) throws SImpossibleNormalizationException
  {
    SVector3d s01 = p1.substract(p0);
    SVector3d s02 = p2.substract(p0);
    
    SVector3d normal = s01.cross(s02);
    
    return normal.normalize();
  }
  
	/**
   * M�thode pour faire l'initialisation de l'objet apr�s sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
    // �valuer la normale � la surface
    try{
      normal = evaluateNormal(P0, P1, P2);
      computeInteriorVector();
    }catch(SImpossibleNormalizationException e){
      throw new SInitializationException("Erreur STriangleGeometry XXX : Les trois points du triangle ne permettent pas de construire un vecteur normale � la surface pouvant �tre normalis�e." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
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
	 * M�thode pour faire la lecture d'un point et l'affectation d�pendra du num�ro du point en lecture d�termin� par la variable <i>reading_point</i>.
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
			
			default : throw new SReadingException("Erreur STriangleGeometry 004 : Il y a d�j� 3 points de d�fini dans ce triangle.");
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
    throw new SNoImplementationException("Erreur STriangleGeometry 005 : La m�thode n'a pas �t� impl�ment�e.");
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
  
  private void computeInteriorVector(){
	  SVector3d 	s01 = P1.substract(P0),
			  		s02 = P2.substract(P0),
	  				s12 = P2.substract(P1);
	  u01 = normal.cross(s01).normalize();
	  u02 = normal.cross(s02).normalize();
	  u12 = normal.cross(s12).normalize();
	  
  }
  
}//fin de la classe STriangle
