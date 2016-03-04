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
 * La classe <b>SBTriangleGeometry</b> représentant la géométrie d'un triangle en <b>coordonnée barycentrique</b>. 
 * Ce triangle permet la définition d'une normale à chaque sommet du triangle ce qui permet d'évaluer une normale locale 
 * à la surface du triangle pour chaque coordonnée barycentrique du triangle. 
 * Cela aura pour effet de donner un "effet de courbure" au triangle lorsque celui-ci sera dessiné à l'écran. 
 * 
 * @author Simon Vézina
 * @since 2015-03-11
 * @version 2015-12-21
 */
public class SBTriangleGeometry extends STriangleGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { 
    SKeyWordDecoder.KW_NORMAL, SKeyWordDecoder.KW_UV
  };
  
  /**
   * La constante <b>DEFAULT_UV0</b> correspond à la coordonnée uv associée au point P0 par défaut.
   */
  private static final SVectorUV DEFAULT_UV0 = new SVectorUV(0.0, 0.0);
  
  /**
   * La constante <b>DEFAULT_UV1</b> correspond à la coordonnée uv associée au point P1 par défaut.
   */
  private static final SVectorUV DEFAULT_UV1 = new SVectorUV(1.0, 0.0);
  
  /**
   * La constante <b>DEFAULT_UV2</b> correspond à la coordonnée uv associée au point P2 par défaut.
   */
  private static final SVectorUV DEFAULT_UV2 = new SVectorUV(1.0, 1.0);
    
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>N0</b> correspond à la normale à la surface associée au point P0 du triangle.
   */
  private SVector3d N0; 
  
  /**
   * La variable <b>N1</b> correspond à la normale à la surface associée au point P1 du triangle.
   */
  private SVector3d N1; 
  
  /**
   * La variable <b>N2</b> correspond à la normale à la surface associée au point P2 du triangle.
   */
  private SVector3d N2; 
    
  /**
   * La variable <b>UV0</b> correspond à la coordonnée uv associée au point P0 du triangle.
   */
  private SVectorUV UV0;  
  
  /**
   * La variable <b>UV1</b> correspond à la coordonnée uv associée au point P1 du triangle.
   */
  private SVectorUV UV1;  
  
  /**
   * La variable <b>UV2</b> correspond à la coordonnée uv associée au point P2 du triangle.
   */
  private SVectorUV UV2;  
  
  /**
   * La variable <b>reading_normal</b> correspond au numéro de la normale à la surface à effectuer en lecture.
   */
  private int reading_normal; 
  
  /**
   * La variable <b>reading_uv</b> correspond au numéro de la coordonnée uv à effectuer en lecture.
   */
  private int reading_uv;     
  
  //---------------------------------------------------------
  // PARAMÈTRES DE PRÉCALCUL pour coordonnée barycentrique //
  //---------------------------------------------------------
  
 
  
  //-----------------
  // CONSTRUCTEURS //
  //-----------------
    
  /**
   * Constructeur d'un triangle barycentrique <b>sans coordonnées uv</b>.
   * 
   * @param p0 - La position du point P0 du triangle.
   * @param p1 - La position du point P1 du triangle.
   * @param p2 - La position du point P2 du triangle.
   * @param n0 - La normale associée au point P0 du triangle.
   * @param n1 - La normale associée au point P1 du triangle.
   * @param n2 - La normale associée au point P2 du triangle.
   * @throws SConstructorExeception Si les trois points ne sont pas adéquats pour définir un triangle (ex: colinéaire) ou une normale à un point est nulle.
   */
  public SBTriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, 
                            SVector3d n0, SVector3d n1, SVector3d n2) throws SConstructorException
  {
    this(p0, p1, p2, n0, n1, n2, DEFAULT_UV0, DEFAULT_UV1, DEFAULT_UV2, null);
  }
  
  /**
   * Constructeur d'un triangle barycentrique <b>sans normale</b>.
   * 
   * @param p0 - La position du point P0 du triangle.
   * @param p1 - La position du point P1 du triangle.
   * @param p2 - La position du point P2 du triangle.
   * @param uv0 - La coordonnée uv associée au point P0 du triangle.
   * @param uv1 - La coordonnée uv associée au point P1 du triangle.
   * @param uv2 - La coordonnée uv associée au point P2 du triangle.
   * @throws SConstructorExeception Si les trois points ne sont pas adéquats pour définir un triangle (ex: colinéaire) ou une normale à un point est nulle.
   */
  public SBTriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, 
                            SVectorUV uv0, SVectorUV uv1, SVectorUV uv2) throws SConstructorException
  {
    this(p0, p1, p2, null, null, null, uv0, uv1, uv2, null);   
  }
  
  /**
   * Constructeur d'un triangle barycentrique.
   * Si l'un des paramètres n0, n1, n2 est 'null', une normale à la surface par défaut
   * étant la normale géométrique du triangle sera affectée au paramètre.
   * 
   * @param p0 - La position du point P0 du triangle.
   * @param p1 - La position du point P1 du triangle.
   * @param p2 - La position du point P2 du triangle.
   * @param n0 - La normale associée au point P0 du triangle.
   * @param n1 - La normale associée au point P1 du triangle.
   * @param n2 - La normale associée au point P2 du triangle.
   * @param uv0 - La coordonnée uv associée au point P0 du triangle.
   * @param uv1 - La coordonnée uv associée au point P1 du triangle.
   * @param uv2 - La coordonnée uv associée au point P2 du triangle.
   * @throws SConstructorExeception Si les trois points ne sont pas adéquats pour définir un triangle (ex: colinéaire) ou une normale à un point est nulle.
   */
  public SBTriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, 
                            SVector3d n0, SVector3d n1, SVector3d n2, 
                            SVectorUV uv0, SVectorUV uv1, SVectorUV uv2) throws SConstructorException
  {
    this(p0, p1, p2, n0, n1, n2, uv0, uv1, uv2, null); 
  }
  
  /**
   * Constructeur d'un triangle barycentrique avec une primitive comme parent en référence.
   * Si l'un des paramètres n0, n1, n2 est 'null', une normale à la surface par défaut
   * étant la normale géométrique du triangle sera affectée au paramètre.
   * 
   * @param p0 - La position du point P0 du triangle.
   * @param p1 - La position du point P1 du triangle.
   * @param p2 - La position du point P2 du triangle.
   * @param n0 - La normale associée au point P0 du triangle.
   * @param n1 - La normale associée au point P1 du triangle.
   * @param n2 - La normale associée au point P2 du triangle.
   * @param uv0 - La coordonnée uv associée au point P0 du triangle.
   * @param uv1 - La coordonnée uv associée au point P1 du triangle.
   * @param uv2 - La coordonnée uv associée au point P2 du triangle.
   * @param parent - La primitive parent à cette géométrie.
   * @throws SConstructorExeception Si les trois points ne sont pas adéquats pour définir un triangle (ex: colinéaire) ou une normale à un point est nulle.
   */
  public SBTriangleGeometry(SVector3d p0, SVector3d p1, SVector3d p2, 
                            SVector3d n0, SVector3d n1, SVector3d n2, 
                            SVectorUV uv0, SVectorUV uv1, SVectorUV uv2, SPrimitive parent) throws SConstructorException
  {
    super(p0, p1, p2, parent);
    
    //Si une normale est 'null', elle sera recalculé dans la méthode initialize()
    N0 = n0;
    N1 = n1;
    N2 = n2;
    
    UV0 = uv0;
    UV1 = uv1;
    UV2 = uv2;
        
    reading_point = 3;   
    reading_normal = 3;   
    reading_uv = 3;
    
    try{
      initialize();
    }catch(SInitializationException e){
      //Les trois points sont colinéaire ce qui ne permet pas de définir une normale à la surface au triangle. 
      throw new SConstructorException("Erreur SBTriangleGeometry 002 : Les normales {" + N0 + "," + N1 + "," + N2 + "} ne sont pas adéquats pour définir un triangle barycentrique."  + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);  
    }
  }
  
  /**
   * Constructeur d'une géométrie à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   * 
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SConstructorExeception Si les trois points lus ne sont pas adéquats pour définir un triangle (ex: colinéaire) ou une normale lue à un point est nulle.
   * @see SBufferedReader
   * @see SPrimitive
   */
  public SBTriangleGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
  {
    this(DEFAULT_P0, DEFAULT_P1, DEFAULT_P2, null, null, null, DEFAULT_UV0, DEFAULT_UV1, DEFAULT_UV2, parent);  
    
    //Puisque la construction par défaut d'un SBTriangleGeometry donne un triangle fonctionnel 
    //avec une définition des vecteurs normales aux trois points du triangle, il faut
    //obligatoirement réinitialiser ces normales à 'null' et recalculer à nouveau celles qui
    //seront après la lecture encore 'null' afin d'avoir une bonne définition de ces normales
    //après avoir effectué la lecture
    
    //Réinitialisation au droit de lecture
    reading_point = 0;    
    reading_normal = 0;   
    reading_uv = 0;
    
    //Réinitialisation des normales à la surface
    N0 = null;
    N1 = null;
    N2 = null;
    
    //Faire la lecture
    try{
      read(sbr);
    }catch(SInitializationException e){
      //Les trois points sont colinéaire ce qui ne permet pas de définir une normale à la surface au triangle. 
      throw new SConstructorException("Erreur SBTriangleGeometry 003 : Les points {" + P0 + "," + P1 + "," + P2 + "} avec normale {" + N0 + "," + N1 + "," + N2 + "} qui ont été lus ne sont pas adéquats pour définir un triangle barycentrique." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);  
    }
  }
  
  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.BTRIANGLE_CODE;
  }
  
  /**
   * Méthode pour obtenir la normale associée au point P0 du triangle barycentrique.
   * 
   * @return La normale au point P0 du triangle barycentrique.
   */
  public SVector3d getN0()
  {
    return N0;
  }
  
  /**
   * Méthode pour obtenir la normale associée au point P1 du triangle barycentrique.
   * 
   * @return La normale au point P1 du triangle barycentrique.
   */
  public SVector3d getN1()
  {
    return N1;
  }
  
  /**
   * Méthode pour obtenir la normale associée au point P2 du triangle barycentrique.
   * 
   * @return La normale au point P2 du triangle barycentrique.
   */
  public SVector3d getN2()
  {
    return N2;
  }
  
  /**
   * Méthode pour obtenir la coordonnée uv associée au point P0 du triangle barycentrique.
   * 
   * @return La coordonnée uv au point P0 du triangle barycentrique.
   */
  public SVectorUV getUV0()
  {
    return UV0;
  }
  
  /**
   * Méthode pour obtenir la coordonnée uv associée au point P1 du triangle barycentrique.
   * 
   * @return La coordonnée uv au point P1 du triangle barycentrique.
   */
  public SVectorUV getUV1()
  {
    return UV1;
  }
  
  /**
   * Méthode pour obtenir la coordonnée uv associée au point P2 du triangle barycentrique.
   * 
   * @return La coordonnée uv au point P2 du triangle barycentrique.
   */
  public SVectorUV getUV2()
  {
    return UV2;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SBTriangleGeometry 004 : Le rayon a déjà intersecté une autre géométrie.");
        
    return ray;
  }

  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_BTRIANGLE);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SSphereGeometry et ses paramètres hérités
    writeSBTriangleGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SBTriangleGeometry et ses paramètres hérités.
   * 
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   */
  protected void writeSBTriangleGeometryParameter(BufferedWriter bw)throws IOException
  {
    writeSTriangleGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_NORMAL);
    bw.write("\t");
    N0.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_NORMAL);
    bw.write("\t");
    N1.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_NORMAL);
    bw.write("\t");
    N2.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_UV);
    bw.write("\t");
    UV0.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_UV);
    bw.write("\t");
    UV1.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_UV);
    bw.write("\t");
    UV2.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  /**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
    // Affectation d'une normale aux trois points s'il n'y en avait pas déjà.
    // À préciser que 'normal' a déjà été définie par la méthode initialize() de STriangleGeometry
    if(N0 == null)
      N0 = normal;
    
    if(N1 == null)
      N1 = normal;
    
    if(N2 == null)
      N2 = normal;
    
    // Normalisation des normales au points (si elles n'étaitent pas normalisées lors de la lecture)
    try{
      N0 = N0.normalize();
      N1 = N1.normalize();
      N2 = N2.normalize();
    }catch(SImpossibleNormalizationException e){
      // Une erreur lors de la normalisation d'un vecteur est survenue.
      throw new SInitializationException("Erreur SBTriangleGeometry 006 - La définition d'une normale en un point ne peut pas être normalisée." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
    
  }

  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
  {
    //Analyse du code du mot clé dans la classe hérité STriangleGeometry
    if(super.read(sbr, code, remaining_line))
      return true;
    else
      switch(code)
      {
        case SKeyWordDecoder.CODE_NORMAL : readNormal(remaining_line);  return true;
        
        case SKeyWordDecoder.CODE_UV : readUV(remaining_line); return true;
                            
        default : return false;
      }
  }

  /**
   * Méthode pour faire la lecture d'une normale associée à un point et l'affectation dépendra du numéro de normale en lecture déterminé par la variable <i>reading_normal</i>.
   * 
   * @param remaining_line - L'expression en string du vecteur normale à un point du triangle.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  private void readNormal(String remaining_line)throws SReadingException
  {
    try{
      
      switch(reading_normal)
      {
        case 0 :  N0 = new SVector3d(remaining_line); N0 = N0.normalize(); break;
        case 1 :  N1 = new SVector3d(remaining_line); N1 = N1.normalize(); break;
        case 2 :  N2 = new SVector3d(remaining_line); N2 = N2.normalize(); break;
        
        default : throw new SReadingException("Erreur SBTriangleGeometry 008 : Il y a déjà 3 normales de défini dans ce triangle.");
      }
      
      reading_normal++; 
      
    }catch(SImpossibleNormalizationException e){
      //Si une normale ne peut pas être normalisée
      throw new SReadingException("Erreur SBTriangleGeometry 009 : Une normale est mal définie ce qui rend la normalisation impossible." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  /**
   * Méthode pour faire la lecture d'une coordonnée uv associée à un point et l'affectation dépendra du numéro de coordonnée uv en lecture déterminé par la variable <i>reading_uv</i>.
   * 
   * @param remaining_line - L'expression en string du vecteur normale à un point du triangle.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  private void readUV(String remaining_line)throws SReadingException
  {
    switch(reading_uv)
    {
      case 0 :  UV0 = new SVectorUV(remaining_line); break;
      case 1 :  UV1 = new SVectorUV(remaining_line); break;
      case 2 :  UV2 = new SVectorUV(remaining_line); break;
  
      default : throw new SReadingException("Erreur SBTriangleGeometry 010 : Il y a déjà 3 coordonnée uv de défini dans ce triangle.");
    }

    reading_uv++;   
  }
  
  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SBTriangleGeometry 011 : La méthode n'est pas définie pour cette géométrie. On doit effectuer ce calcul via les coordonnées barycentriques.");
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
    return SKeyWordDecoder.KW_BTRIANGLE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SBTriangleGeometry
