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
 * La classe <b>SDiskGeometry</b> représente la géométrie d'un disque. 
 * 
 * @author Simon Vézina
 * @since 2015-06-20
 * @version 2015-12-21
 */
public class SDiskGeometry extends SPlaneGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_RAY };
  
  /**
   * La constante 'DEFAULT_R' correspond au rayon par défaut d'un disque.
   */
  protected static final double DEFAULT_R = 1.0;  
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable 'R' correspond au rayon du disque.
   */
  protected double R;                       
  
  //-----------------
  // CONSTRUCTEURS //
  //-----------------
  
  /**
   * Constructeur avec paramètre par défaut d'un disque. 
   */
  public SDiskGeometry()
  {
    this(DEFAULT_POSITION, DEFAULT_SURFACE_NORMAL, DEFAULT_R);
  }

  /**
   * Constructeur d'un disque.
   * 
   * @param position - Le centre du disque.
   * @param normal - L'orientation de la normale à la surface du disque.
   * @param R - Le rayon du disque.
   */
  public SDiskGeometry(SVector3d position, SVector3d normal, double R)
  {
    this(position, normal, R, null);
  }
  
  /**
   * Constructeur d'un disque avec primitive comme parent.
   * 
   * @param position - Le centre du disque.
   * @param normal - L'orientation de la normale à la surface du disque.
   * @param R - Le rayon du disque.
   * @param parent - La primitive en parent.
   * @throws SConstructorException Si le rayon du disque est négatif.
   */
  public SDiskGeometry(SVector3d position, SVector3d normal, double R, SPrimitive parent)throws SConstructorException
  {
    super(position, normal, parent);
    
    //Vérification que le rayon soit positif
    if(R < 0.0)
      throw new SConstructorException("Erreur SDiskGeometry 001 : Le disque se fait affecter un rayon R = " + R + " qui est négatif.");
  
    this.R = R;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SDiskGeometry 002 : Une erreur lors de l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }
  
  /**
   * Constructeur du disque à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   * 
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lancée.
   * @throws SConstructorException Si une erreur lors de la construction de la géométrie est survenue. 
   * @see SBufferedReader
   * @see SPrimitive
   */
  public SDiskGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
  {
    this(DEFAULT_POSITION, DEFAULT_SURFACE_NORMAL, DEFAULT_R, parent);   
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SDiskGeometry 003 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }
  
  //------------
  // MÉTHODES //
  //------------
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.DISK_CODE;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
	//Faire l'intersection avec le plan du disque par l'héritage de l'intersection avec un plan
	    SRay ray_plane_intersection = super.intersection(ray);
	    if (!ray_plane_intersection.asIntersected())
	    	return ray;
	    SVector3d difference = ray_plane_intersection.getIntersectionPosition().substract(this.position);
	    if (R*R >= difference.dot(difference))
	    	return ray_plane_intersection;
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

  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_DISK);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SDiskGeometry et ses paramètres hérités
    writeSDiskGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SDiskGeometry et ses paramètres hérités.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSDiskGeometryParameter(BufferedWriter bw)throws IOException
  {
    //Écrire les paramètres hérités de la classe SPlaneGeometry
    writeSPlaneGeometryParameter(bw);   
    
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
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException
  {
    //Analyse du code du mot clé dans la classe hérité SPlaneGeometry
    if(super.read(sbr, code, remaining_line))
      return true;
    else
      switch(code)
      {
        case SKeyWordDecoder.CODE_RAY : R = readDoubleGreaterThanZero(remaining_line, SKeyWordDecoder.KW_RAY); return true;
            
        default : return false;
      }
  }

  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SDiskGeometry 004 : La méthode n'a pas encore été implémentée.");
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
    return SKeyWordDecoder.KW_DISK;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SDiskGeometry
