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
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SSphericalCapGeometry</b> repr�sente la g�om�trie d'une calotte sph�rique qui correspond � une tranche d'une sph�re.
 * Cette tranche poss�de un rayon selon la g�om�trie d'un disque et une courbure selon la g�om�trie d'une sph�re.
 * Si l'on coupe une sph�re en deux morceaux identiques, la calotte sph�rique corespond � une demi-sph�re.
 * 
 * <p>Une calotte sph�rique �tant d�fini � l'aide d'un rayon de courbure respecte la convension de signe des dioptres sph�riques suivante :
 * <ul>- Courbure positive (R > 0) : Courbure convexe par rapport � la surface du disque (dans le sens de la normale � la surface du disque).</ul>
 * <ul>- Courbure n�gative (R < 0) : Courbure concave par rapport � la surface du disque (dans le sens oppos� de la normale � la surface du disque).</ul>
 * </p>
 * 
 * @author Simon V�zina
 * @since 2015-11-08
 * @version 2015-12-21
 */
public class SSphericalCapGeometry extends SDiskGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_CURVATURE };
  
  /**
   * La constante <b>DEFAULT_CURVATURE</b> correspond au rayon de courbure par d�faut d'une calotte sph�rique �tant �gal � {@value}.
   */
  private static final double DEFAULT_CURVATURE = DEFAULT_R;
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>raduis_of_curvature</b> correspond au rayon de courbure de la calotte sph�rique.
   */
  private double radius_of_curvature;
  
  //--------------------------
  // PARAM�TRE DE PR�CALCUL //
  //--------------------------
  
    
  //-----------------
  // CONSTRUCTEURS //
  //-----------------
  
  /**
   * ...
   * 
   * @param position
   * @param normal
   * @param R
   * @param radius_of_curvature
   */
  public SSphericalCapGeometry(SVector3d position, SVector3d normal, double R, double radius_of_curvature) throws SConstructorException
  {
    this(position, normal, R, radius_of_curvature, null);
  }

  /**
   * ...
   * 
   * @param position
   * @param normal
   * @param R
   * @param parent
   * @throws SConstructorException
   */
  public SSphericalCapGeometry(SVector3d position, SVector3d normal, double R, double radius_of_curvature, SPrimitive parent) throws SConstructorException
  {
    super(position, normal, R, parent);
    
    // S'assurer que le rayon de courbure est plus grand que le rayon du disque de la calotte sph�rique
    if(Math.abs(radius_of_curvature) < R)
      throw new SConstructorException("Erreur SSphericalCapGeometry 001 : Le rayon de courbure '" + radius_of_curvature + "' en valeur absolue doit �tre plus �lev� que le rayon du disque �tait '" + R + "'.");
    
    this.radius_of_curvature = radius_of_curvature;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SSphericalCapGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }

  /**
   * ...
   * 
   * @param sbr
   * @param parent
   * @throws IOException
   * @throws SConstructorException
   */
  public SSphericalCapGeometry(SBufferedReader sbr, SPrimitive parent) throws IOException, SConstructorException
  {
    this(DEFAULT_POSITION, DEFAULT_SURFACE_NORMAL, DEFAULT_R, DEFAULT_CURVATURE, parent);
    
    //Faire la lecture
    try{
      read(sbr);    
    }catch(SInitializationException e){    
      throw new SConstructorException("Erreur SSphericalCapGeometry 003 : Une erreur est survenue lors de l'initialisation." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);  
    }
  
  }

  //------------
  // M�THODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.SPHERICAL_CAP_CODE;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException 
  {
    //S'assurer que le rayon n'a rien intersect� auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SSphericalCapGeometry 004 : Le rayon a d�j� intersect� une autre g�om�trie.");
    
    return ray;
  }
  
  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Cette m�thode n'a pas �t� impl�ment�e.");
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
    //Analyse du code du mot cl� dans la classe h�rit� SDiskGeometry
    if(super.read(sbr, code, remaining_line))
      return true;
    else
      switch(code)
      {
        case SKeyWordDecoder.CODE_CURVATURE : radius_of_curvature = readDouble(remaining_line, SKeyWordDecoder.KW_CURVATURE); return true;
            
        default : return false;
      }
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_SPHERICAL_CAP);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //�crire les propri�t�s de la classe SDiskGeometry et ses param�tres h�rit�s
    writeSSphericalCapGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * M�thode pour �crire les param�tres associ�s � la classe SSphericalCapGeometry et ses param�tres h�rit�s.
   * @param bw - Le BufferedWriter �crivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSSphericalCapGeometryParameter(BufferedWriter bw)throws IOException
  {
    //�crire les param�tres h�rit�s de la classe SDiskGeometry
    writeSDiskGeometryParameter(bw);   
    
    bw.write(SKeyWordDecoder.KW_CURVATURE);
    bw.write("\t\t");
    bw.write(Double.toString(radius_of_curvature));
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    return false;
  }
  
  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SSpherialCapGeometry 005 : La m�thode n'a pas encore �t� impl�ment�e.");
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
    return SKeyWordDecoder.KW_SPHERICAL_CAP;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SSphericalCapGeometry
