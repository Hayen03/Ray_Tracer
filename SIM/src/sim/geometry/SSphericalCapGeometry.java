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
 * La classe <b>SSphericalCapGeometry</b> représente la géométrie d'une calotte sphérique qui correspond à une tranche d'une sphère.
 * Cette tranche possède un rayon selon la géométrie d'un disque et une courbure selon la géométrie d'une sphère.
 * Si l'on coupe une sphère en deux morceaux identiques, la calotte sphérique corespond à une demi-sphère.
 * 
 * <p>Une calotte sphérique étant défini à l'aide d'un rayon de courbure respecte la convension de signe des dioptres sphériques suivante :
 * <ul>- Courbure positive (R > 0) : Courbure convexe par rapport à la surface du disque (dans le sens de la normale à la surface du disque).</ul>
 * <ul>- Courbure négative (R < 0) : Courbure concave par rapport à la surface du disque (dans le sens opposé de la normale à la surface du disque).</ul>
 * </p>
 * 
 * @author Simon Vézina
 * @since 2015-11-08
 * @version 2015-12-21
 */
public class SSphericalCapGeometry extends SDiskGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_CURVATURE };
  
  /**
   * La constante <b>DEFAULT_CURVATURE</b> correspond au rayon de courbure par défaut d'une calotte sphérique étant égal à {@value}.
   */
  private static final double DEFAULT_CURVATURE = DEFAULT_R;
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>raduis_of_curvature</b> correspond au rayon de courbure de la calotte sphérique.
   */
  private double radius_of_curvature;
  
  //--------------------------
  // PARAMÈTRE DE PRÉCALCUL //
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
    
    // S'assurer que le rayon de courbure est plus grand que le rayon du disque de la calotte sphérique
    if(Math.abs(radius_of_curvature) < R)
      throw new SConstructorException("Erreur SSphericalCapGeometry 001 : Le rayon de courbure '" + radius_of_curvature + "' en valeur absolue doit être plus élevé que le rayon du disque était '" + R + "'.");
    
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
  // MÉTHODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.SPHERICAL_CAP_CODE;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException 
  {
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SSphericalCapGeometry 004 : Le rayon a déjà intersecté une autre géométrie.");
    
    return ray;
  }
  
  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Cette méthode n'a pas été implémentée.");
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
    //Analyse du code du mot clé dans la classe hérité SDiskGeometry
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
    
    //Écrire les propriétés de la classe SDiskGeometry et ses paramètres hérités
    writeSSphericalCapGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SSphericalCapGeometry et ses paramètres hérités.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSSphericalCapGeometryParameter(BufferedWriter bw)throws IOException
  {
    //Écrire les paramètres hérités de la classe SDiskGeometry
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
    throw new SNoImplementationException("Erreur SSpherialCapGeometry 005 : La méthode n'a pas encore été implémentée.");
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
