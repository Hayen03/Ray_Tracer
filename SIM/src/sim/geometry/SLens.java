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
 * La classe <b>SLens</b> repr�sente une g�om�trie correspondant � une lentille. 
 * Cette lentille est repr�sent�e par une forme cylindrique dont les deux extr�mit�s sont des coquilles sph�riques.
 * Cette lentile permet d'�valuer la d�viation d'un rayon <b>sans l'approximation des lentilles minces</b>, 
 * car elle permet d'�valuer exactement la normale � la surface lors d'une intersection 
 * ce qui permet d'appliquer un calcul de la loi de la r�fraction sans approximation.
 * <p>La courbure des coquilles sph�riques respecte la convension de signe des dioptres sph�riques suivante :
 * <ul>- Courbure positive (R > 0) : Courbure convexe par rapport � sa surface du disque (dans le sens de la normale � la surface du disque).</ul>
 * <ul>- Courbure n�gative (R < 0) : Courbure concave par rapport � sa surface du disque (dans le sens oppos� de la normale � la surface du disque).</ul>
 * </p>
 * 
 * @author Simon V�zina
 * @since 2015-11-17
 * @version 2015-12-21
 */
public class SLens extends STubeGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_CURVATURE };
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>curvature1</b> correspond au rayon de courbure de la calotte sph�rique associ� au point P1 du cylindre.
   */
  private double curvature1;
  
  /**
   * La variable <b>curvature2</b> correspond au rayon de courbure de la calotte sph�rique associ� au point P1 du cylindre.
   */
  private double curvature2;
  
  /**
   * La variable <b>reading_point</b> correspond au num�ro du point qui sera en lecture.
   */
  private int reading_curvature;
  
  //--------------------------
  // PARAM�TRE EN PR�CALCUL //
  //--------------------------
  
 
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * ...
   */
  public SLens()
  {
    // Construction par d�faut avec rayon de courbure positif �gal au rayon du cylindre de la lentille.
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R, DEFAULT_R, DEFAULT_R);
  }
  
  /**
   * Constructeur d'une lentille avec deux rayon de courbure. 
   * 
   * @param P1 - La position P1 de la 1i�re extr�mit� de la lentille.
   * @param P2 - La position P2 de la 2i�me extr�mit� de le lentille.
   * @param R - Le rayon cylindrique de la lentille.
   * @param curvature1 - Le rayon de courbure du c�t� P1 de la lentille (nulle = plan, positif = convexe, n�gatif = concave).
   * @param curvature2 - Le rayon de courbure du c�t� P2 de la lentille (nulle = plan, positif = convexe, n�gatif = concave).
   */
  public SLens(SVector3d P1, SVector3d P2, double R, double curvature1, double curvature2)
  {
    this(P1, P2, R, curvature1, curvature2, null);
  }

  /**
   * Constructeur d'une lentille avec deux rayon de courbure et parent primitive. 
   * 
   * @param P1 - La position P1 de la 1i�re extr�mit� de la lentille.
   * @param P2 - La position P2 de la 2i�me extr�mit� de le lentille.
   * @param R - Le rayon cylindrique de la lentille.
   * @param curvature1 - Le rayon de courbure du c�t� P1 de la lentille (nulle = plan, positif = convexe, n�gatif = concave).
   * @param curvature2 - Le rayon de courbure du c�t� P2 de la lentille (nulle = plan, positif = convexe, n�gatif = concave).
   * @param parent - La primitive parent � cette g�om�trie.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   */
  public SLens(SVector3d P1, SVector3d P2, double R, double curvature1, double curvature2, SPrimitive parent) throws SConstructorException
  {
    super(P1, P2, R, parent);
    
    this.curvature1 = curvature1;
    this.curvature2 = curvature2;
    
    reading_curvature = 2;  //pas de courbure � lire
    
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
  // M�THODES //
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
   * M�thode pour faire l'initialisation de l'objet apr�s sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
     
  }
  
  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
  {
    //Analyse du code du mot cl� dans la classe h�rit� STubeGeometry
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
   * M�thode pour faire la lecture d'un rayon de courbure et l'affectation d�pendra du num�ro en lecture d�termin� par la variable <i>reading_curvature</i>.
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
        
        default : throw new SReadingException("Erreur STubeGeometry 005 : Il y a d�j� 2 rayon de courbure de d�fini dans cette lentille.");
      }
    }catch(NullPointerException e){
      throw new SReadingException("Erreur SLens XXX : L'expression est vide pour d�finir le rayon de courbure.");
    }catch(NumberFormatException e){
      throw new SReadingException("Erreur SLens XXX : L'expression '" + remaining_line + "ne peut pas �tre utilis� pour finir un rayon de courbure.");
    }
    
    reading_curvature++;      
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_SPHERICAL_CAP);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //�crire les propri�t�s de la classe SDiskGeometry et ses param�tres h�rit�s
    writeSLensGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  /**
   * M�thode pour �crire les param�tres associ�s � la classe SLensGeometry et ses param�tres h�rit�s.
   * @param bw - Le BufferedWriter �crivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSLensGeometryParameter(BufferedWriter bw)throws IOException
  {
    //�crire les param�tres h�rit�s de la classe SDiskGeometry
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
