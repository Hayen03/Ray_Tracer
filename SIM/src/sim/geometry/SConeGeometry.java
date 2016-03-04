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
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe </b>SConeGeometry</b> représentant la géométrie d'un cône. 
 * La base du cône est située en son point P1 et la point du cône est située en son point P2. 
 * 
 * @author Simon Vézina
 * @since 2015-08-11
 * @version 2015-12-02
 */
public class SConeGeometry extends STubeGeometry {

  //---------------------------
  // Paramètres de précalcul //
  //---------------------------
  
  /**
   * Construction d'un cône par défaut. 
   */
  public SConeGeometry()
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R);
  }

  /**
   * Constructeur d'un cône.
   * 
   * @param P1 - Le point de la base du cône. 
   * @param P2 - Le point su sommet du cône.
   * @param R - Le rayon de la base du cône.
   */
  public SConeGeometry(SVector3d P1, SVector3d P2, double R)
  {
    this(P1, P2, R, null);
  }

  /**
   * Constructeur d'un cône avec une primitive comme parent.
   * 
   * @param P1 - Le point de la base du cône. 
   * @param P2 - Le point su sommet du cône.
   * @param R - Le rayon de la base du cône.
   * @param parent - La primitive en parent.
   * @throws SConstructorException S'il y a eu une erreur lors de la construction de la géométrie.
   */
  public SConeGeometry(SVector3d P1, SVector3d P2, double R, SPrimitive parent)throws SConstructorException
  {
    super(P1, P2, R, parent);
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SConeGeometry 001 : Une erreur lors de l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
  }
  
  /**
   * Constructeur du cône à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   *
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lancée.
   * @throws SConstructorException S'il y a eu une erreur lors de la construction de la géométrie.
   * @see SBufferedReader
   * @see SPrimitive
   */
  public SConeGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
  {
    super(sbr, parent);
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SConeGeometry 002 : Une erreur lors de l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
  }

  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.CONE_CODE;
  }

  @Override
  public boolean isClosedGeometry()
  {
    return true;
  }

  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SSphereGeometry 003 : Le rayon a déjà intersecté une autre géométrie.");
  
    return ray;
  }
    
  @Override
  public boolean isInside(SVector3d v)
  {
    return false;
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    try{
      
      SVector3d edge = P2.substract(ray.getPosition(intersection_t)).normalize();
      
      SVector3d ext_normal = edge.cross(S12).cross(edge);
      
      // Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure du cône
      if(ray.getDirection().dot(ext_normal) < 0.0)
        return false;                            // orientation opposée, donc intersection venant de l'extérieur
      else
        return true;                             // orientation dans le même sens, donc intersection venant de l'intérieur
      
    }catch(SImpossibleNormalizationException e){
      //Si l'intersection est trop près du point P2, alors un trouvera une réponse à ça !!!
      SLog.logWriteLine("Message SConeGeometry : Intersection trop près de la point du cône. Est-ce possible de l'intérieur ?");
      return false;
    }
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    try{
      
      SVector3d edge = P2.substract(ray.getPosition(intersection_t)).normalize();
      
      SVector3d ext_normal = edge.cross(S12).cross(edge);
      
      //Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure du cône
      if(ray.getDirection().dot(ext_normal) < 0.0)
        return ext_normal;                            //orientation opposée, donc intersection venant de l'extérieur
      else
        return ext_normal.multiply(-1.0);             //orientation dans le même sens, donc intersection venant de l'intérieur

    }catch(SImpossibleNormalizationException e){
      //Si l'intersection est trop près du point P2, alors un trouvera une réponse à ça !!!
      SLog.logWriteLine("Message SConeGeometry : Intersection trop près de la point du cône.");
      return S12;
    }
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
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_CONE);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SCercleGeometry et ses paramètres hérités
    writeSTubeGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

 
  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SConeGeometry 004 : Cette méthode n'a pas encore été implémentée.");
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
    return SKeyWordDecoder.KW_CONE;
  }
  
}//fin de la classe SConeGeometry
