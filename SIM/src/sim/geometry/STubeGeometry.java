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
 * La classe <b>STubeGeometry</b> représente la géométrie d'un tube. Un tube correspond à un cylindre sans les extrémités en forme de cercle.
 * Il est donc un rectangle refermé sur lui-même sans épaisseur. Il n'est donc pas une géométrie volumique (avec zone intérieure).
 * Le tube sera définit à l'aide de deux points correspondant à la ligne passant par le centre du tube et d'un rayon.
 * 
 * @author Simon Vézina
 * @since 2015-06-21
 * @version 2015-12-21
 */
public class STubeGeometry extends SAbstractGeometry {

  //--------------
  // CONSTANTES //
  //--------------

  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_RAY };
  
  /**
   * La constante <b>DEFAULT_P1</b> correspond à la position du début du tube par défaut.
   */
  protected static final SVector3d DEFAULT_P1 = new SVector3d(0.0, 0.0, 0.0);   
  
  /**
   * La constante <b>DEFAULT_P1</b> correspond à la position de fin du tube par défaut.
   */
  protected static final SVector3d DEFAULT_P2 = new SVector3d(0.0, 0.0, 1.0);   
  
  /**
   * La constante <b>DEFAULT_R</b> correspond au rayon du tube par défaut. 
   */
  protected static final double DEFAULT_R = 1.0;                                
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>P1</b> correspond à la position du début du tube.
   */
  protected SVector3d P1; 
  
  /**
   * La variable <b>P1</b> correspond à la position de fin du tube.
   */
  protected SVector3d P2; 
  
  /**
   * La variable <b>R</b> correspond au rayon du tube.
   */
  protected double R;     
  
  /**
   * La variable <b>reading_point</b> correspond au numéro du point qui sera en lecture.
   */
  protected int reading_point;
  
  //--------------------------
  // PARAMÈTRE DE PRÉCALCUL //
  //--------------------------
  
  /**
   * La variable <b>S12</b> correspond à l'axe du tube orienté de P1 à P2 (début vers la fin du tube).
   * L'axe du tube se doit d'être <b>normalisé</b>.
   */
  protected SVector3d S12;      
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Construction d'un tube par défaut. 
   */
  public STubeGeometry()
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R);
  }

  /**
   * Constructeur d'un tube.
   * @param P1 - Le 1ier point du tube. 
   * @param P2 - Le 2ième point du tube.
   * @param R - Le rayon du tube.
   */
  public STubeGeometry(SVector3d P1, SVector3d P2, double R)
  {
    this(P1, P2, R, null);
  }

  /**
   * Constructeur d'un tube avec une primitive comme parent.
   * @param P1 - Le 1ier point du tube. 
   * @param P2 - Le 2ième point du tube.
   * @param R - Le rayon du tube.
   * @param parent - La primitive en parent.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   */
  public STubeGeometry(SVector3d P1, SVector3d P2, double R, SPrimitive parent)throws SConstructorException
  {
    super(parent);
    
    //Vérification que le rayon soit positif
    if(R < 0.0)
      throw new SConstructorException("Erreur STubeGeometry 001 : Le tube se fait affecter un rayon R = " + R + " qui est négatif.");
  
    if(P1.equals(P2))
      throw new SConstructorException("Erreur STubeGeometry 002 : Le point P1 = " + P1 + " et le point P2 = " + P2 + " sont égaux.");
        
    this.P1 = P1;
    this.P2 = P2;
    this.R = R;
    
    reading_point = 2;  //pas d'autres points à lire
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur STubeGeometry 003 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }
  
  /**
   * Constructeur du tube à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lancée.
   * @throws SConstructorException Si une erreur est survenue à la construction.
   * @see SBufferedReader
   * @see SPrimitive
   */
  public STubeGeometry(SBufferedReader sbr, SPrimitive parent) throws IOException, SConstructorException
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R, parent);   
    
    reading_point = 0;  //aucune point lu
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur STubeGeometry 004 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }
  
  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.TUBE_CODE;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur STubeGeometry 003 : Le rayon a déjà intersecté une autre géométrie.");
  
    return ray;
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

  /**
   * Méthode qui détermine si le vecteur <i>v</i> se retrouve à l'intérieur des deux extrémités du tube.
   * Ce teste correspond à vérifier si le vecteur <i>v</i> se retrouve à l'intérieur du tube s'il avait un <b>rayon infini</b>.
   * @param v - Le vecteur de position à vérifier.
   * @return <b>true</b> si le vecteur <i>v</i> est à l'intérieur des extrémités et <b>false</b> sinon.
   */
  protected boolean isInsideExtremity(SVector3d v)
  {
    return false;
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_TUBE);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SCercleGeometry et ses paramètres hérités
    writeSTubeGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe STubeGeometry et ses paramètres hérités.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSTubeGeometryParameter(BufferedWriter bw)throws IOException
  {
    bw.write(SKeyWordDecoder.KW_POSITION);
    bw.write("\t\t");
    P1.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_POSITION);
    bw.write("\t\t");
    P2.write(bw);
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
    try{
      // Former l'axe du tube et le normaliser
      S12 = P2.substract(P1).normalize();
    }catch(SImpossibleNormalizationException e){
      throw new SInitializationException("Erreur STubeGeometry 004 : Le point P1 = " + P1 + " et P2 =" + P2 + " ne peuvent pas former un axe pouvant être normalisé." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
  }

  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
  {
    switch(code)
    {
      // On accepte ici deux type de mots clé pour la lecture du début et la fin du cylindre
      case SKeyWordDecoder.CODE_POINT :
      case SKeyWordDecoder.CODE_POSITION :  readPosition(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_RAY :       R = readDoubleGreaterThanZero(remaining_line, "rayon"); return true;   
      
      default : return false;
    }
  }

  /**
   * Méthode pour faire la lecture d'un point et l'affectation dépendra du numéro du point en lecture déterminé par la variable <i>reading_point</i>.
   * @param remaining_line - L'expression en string du vecteur positionnant le point du triangle.
   * @throws SReadingException - S'il y a une erreur de lecture.
   */
  private void readPosition(String remaining_line)throws SReadingException
  {
    switch(reading_point)
    {
      case 0 :  P1 = new SVector3d(remaining_line); break;
      
      case 1 :  SVector3d v = new SVector3d(remaining_line);
      
                if(v.equals(P1))
                  throw new SReadingException("Erreur STubeGeometry 004 : Le point P2 = " + v + " est identique au point P1 ce qui n'est pas acceptable.");
                
                P2 = v; 
                break;
      
      default : throw new SReadingException("Erreur STubeGeometry 005 : Il y a déjà 2 points de défini dans ce tube.");
    }
    
    reading_point++;      
  }

  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    return false;
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    //Évaluation de la normale à la surface extérieure à l'endroit de l'intersection (normalisée)
    SVector3d ext_normal = S12.cross( ray.getPosition(intersection_t).substract(P1) ).cross(S12).normalize();
    
    //Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure du tube
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return ext_normal;                            //orientation opposée, donc intersection venant de l'extérieur
    else
      return ext_normal.multiply(-1.0);             //orientation dans le même sens, donc intersection venant de l'intérieur
  }

  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur STubeGeometry 006 : La méthode n'a pas été implémentée.");
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
    return SKeyWordDecoder.KW_TUBE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin classe STubeGeometry
