/**
 * 
 */
package sim.geometry;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SVector3d;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SLens</b> représente une géométrie correspondant à une lentille. 
 * Cette lentille est représentée par une forme cylindrique dont les deux extrémités sont des coquilles sphériques.
 * Cette lentile permet d'évaluer la déviation d'un rayon <b>sans l'approximation des lentilles minces</b>, 
 * car elle permet d'évaluer exactement la normale à la surface lors d'une intersection 
 * ce qui permet d'appliquer un calcul de la loi de la réfraction sans approximation.
 * <p>La courbure des coquilles sphériques respecte la convension de signe des dioptres sphériques suivante :
 * <ul>- Courbure positive (R > 0) : Courbure convexe par rapport à sa surface du disque (dans le sens de la normale à la surface du disque).</ul>
 * <ul>- Courbure négative (R < 0) : Courbure concave par rapport à sa surface du disque (dans le sens opposé de la normale à la surface du disque).</ul>
 * </p>
 * 
 * @author Simon Vézina
 * @since 2015-11-17
 * @version 2015-12-21
 */
public class SLens extends STubeGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_CURVATURE };
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>curvature1</b> correspond au rayon de courbure de la calotte sphérique associé au point P1 du cylindre.
   */
  private double curvature1;
  
  /**
   * La variable <b>curvature2</b> correspond au rayon de courbure de la calotte sphérique associé au point P1 du cylindre.
   */
  private double curvature2;
  
  /**
   * La variable <b>reading_point</b> correspond au numéro du point qui sera en lecture.
   */
  private int reading_curvature;
  
  //--------------------------
  // PARAMÈTRE EN PRÉCALCUL //
  //--------------------------
  
 
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * ...
   */
  public SLens()
  {
    // Construction par défaut avec rayon de courbure positif égal au rayon du cylindre de la lentille.
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R, DEFAULT_R, DEFAULT_R);
  }
  
  /**
   * Constructeur d'une lentille avec deux rayon de courbure. 
   * 
   * @param P1 - La position P1 de la 1ière extrémité de la lentille.
   * @param P2 - La position P2 de la 2ième extrémité de le lentille.
   * @param R - Le rayon cylindrique de la lentille.
   * @param curvature1 - Le rayon de courbure du côté P1 de la lentille (nulle = plan, positif = convexe, négatif = concave).
   * @param curvature2 - Le rayon de courbure du côté P2 de la lentille (nulle = plan, positif = convexe, négatif = concave).
   */
  public SLens(SVector3d P1, SVector3d P2, double R, double curvature1, double curvature2)
  {
    this(P1, P2, R, curvature1, curvature2, null);
  }

  /**
   * Constructeur d'une lentille avec deux rayon de courbure et parent primitive. 
   * 
   * @param P1 - La position P1 de la 1ière extrémité de la lentille.
   * @param P2 - La position P2 de la 2ième extrémité de le lentille.
   * @param R - Le rayon cylindrique de la lentille.
   * @param curvature1 - Le rayon de courbure du côté P1 de la lentille (nulle = plan, positif = convexe, négatif = concave).
   * @param curvature2 - Le rayon de courbure du côté P2 de la lentille (nulle = plan, positif = convexe, négatif = concave).
   * @param parent - La primitive parent à cette géométrie.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   */
  public SLens(SVector3d P1, SVector3d P2, double R, double curvature1, double curvature2, SPrimitive parent) throws SConstructorException
  {
    super(P1, P2, R, parent);
    
    this.curvature1 = curvature1;
    this.curvature2 = curvature2;
    
    reading_curvature = 2;  //pas de courbure à lire
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SLens 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }    
  }

  /**
   * ...
   * 
   * @param sbr
   * @param parent
   * @throws IOException
   */
  public SLens(SBufferedReader sbr, SPrimitive parent) throws IOException, SConstructorException
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R, DEFAULT_R, DEFAULT_R, parent);
    
    reading_point = 0;      //pas de point encore lu
    reading_curvature = 0;  //pas de courbure encore lu
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SLens 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.LENS_CODE;
  }
  
  @Override
  public boolean isClosedGeometry()
  { 
    return true;
  }
  
  @Override
  public boolean isInside(SVector3d v)
  {
    return false;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException 
  {
    return ray;
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
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
  {
    //Analyse du code du mot clé dans la classe hérité STubeGeometry
    if(super.read(sbr, code, remaining_line))
      return true;
    else
      switch(code)
      {
        case SKeyWordDecoder.CODE_CURVATURE : readCurvature(remaining_line); return true;
            
        default : return false;
      }
  }
  
  /**
   * Méthode pour faire la lecture d'un rayon de courbure et l'affectation dépendra du numéro en lecture déterminé par la variable <i>reading_curvature</i>.
   * @param remaining_line - L'expression en string du rayon de courbure.
   * @throws SReadingException - S'il y a une erreur de lecture.
   */
  private void readCurvature(String remaining_line)throws SReadingException
  {
    try{
      switch(reading_curvature)
      {
        case 0 :  curvature1 = Double.parseDouble(remaining_line); break;
                    
        case 1 :  curvature2 = Double.parseDouble(remaining_line); break;
        
        default : throw new SReadingException("Erreur STubeGeometry 005 : Il y a déjà 2 rayon de courbure de défini dans cette lentille.");
      }
    }catch(NullPointerException e){
      throw new SReadingException("Erreur SLens XXX : L'expression est vide pour définir le rayon de courbure.");
    }catch(NumberFormatException e){
      throw new SReadingException("Erreur SLens XXX : L'expression '" + remaining_line + "ne peut pas être utilisé pour finir un rayon de courbure.");
    }
    
    reading_curvature++;      
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_SPHERICAL_CAP);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SDiskGeometry et ses paramètres hérités
    writeSLensGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  /**
   * Méthode pour écrire les paramètres associés à la classe SLensGeometry et ses paramètres hérités.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSLensGeometryParameter(BufferedWriter bw)throws IOException
  {
    //Écrire les paramètres hérités de la classe SDiskGeometry
    writeSTubeGeometryParameter(bw);   
    
    bw.write(SKeyWordDecoder.KW_CURVATURE);
    bw.write("\t\t");
    bw.write(Double.toString(curvature1));
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_CURVATURE);
    bw.write("\t\t");
    bw.write(Double.toString(curvature2));
    bw.write(SStringUtil.END_LINE_CARACTER);
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
    return SKeyWordDecoder.KW_LENS;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SLens
