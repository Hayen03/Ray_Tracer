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
 * La classe </b>SConeGeometry</b> repr�sentant la g�om�trie d'un c�ne. 
 * La base du c�ne est situ�e en son point P1 et la point du c�ne est situ�e en son point P2. 
 * 
 * @author Simon V�zina
 * @since 2015-08-11
 * @version 2015-12-02
 */
public class SConeGeometry extends STubeGeometry {

  //---------------------------
  // Param�tres de pr�calcul //
  //---------------------------
  
  /**
   * Construction d'un c�ne par d�faut. 
   */
  public SConeGeometry()
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R);
  }

  /**
   * Constructeur d'un c�ne.
   * 
   * @param P1 - Le point de la base du c�ne. 
   * @param P2 - Le point su sommet du c�ne.
   * @param R - Le rayon de la base du c�ne.
   */
  public SConeGeometry(SVector3d P1, SVector3d P2, double R)
  {
    this(P1, P2, R, null);
  }

  /**
   * Constructeur d'un c�ne avec une primitive comme parent.
   * 
   * @param P1 - Le point de la base du c�ne. 
   * @param P2 - Le point su sommet du c�ne.
   * @param R - Le rayon de la base du c�ne.
   * @param parent - La primitive en parent.
   * @throws SConstructorException S'il y a eu une erreur lors de la construction de la g�om�trie.
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
   * Constructeur du c�ne � partir d'information lue dans un fichier de format txt.
   * Puisqu'une g�om�trie est construite � l'int�rieure d'une primitive, une r�f�rence � celle-ci doit �tre int�gr�e au constructeur pour y a voir acc�s.
   *
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette g�om�trie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lanc�e.
   * @throws SConstructorException S'il y a eu une erreur lors de la construction de la g�om�trie.
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
    //S'assurer que le rayon n'a rien intersect� auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SSphereGeometry 003 : Le rayon a d�j� intersect� une autre g�om�trie.");
  
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
      
      // V�rifier si le rayon poss�de une orientation dans le sens oppos� � la normal ext�rieure du c�ne
      if(ray.getDirection().dot(ext_normal) < 0.0)
        return false;                            // orientation oppos�e, donc intersection venant de l'ext�rieur
      else
        return true;                             // orientation dans le m�me sens, donc intersection venant de l'int�rieur
      
    }catch(SImpossibleNormalizationException e){
      //Si l'intersection est trop pr�s du point P2, alors un trouvera une r�ponse � �a !!!
      SLog.logWriteLine("Message SConeGeometry : Intersection trop pr�s de la point du c�ne. Est-ce possible de l'int�rieur ?");
      return false;
    }
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    try{
      
      SVector3d edge = P2.substract(ray.getPosition(intersection_t)).normalize();
      
      SVector3d ext_normal = edge.cross(S12).cross(edge);
      
      //V�rifier si le rayon poss�de une orientation dans le sens oppos� � la normal ext�rieure du c�ne
      if(ray.getDirection().dot(ext_normal) < 0.0)
        return ext_normal;                            //orientation oppos�e, donc intersection venant de l'ext�rieur
      else
        return ext_normal.multiply(-1.0);             //orientation dans le m�me sens, donc intersection venant de l'int�rieur

    }catch(SImpossibleNormalizationException e){
      //Si l'intersection est trop pr�s du point P2, alors un trouvera une r�ponse � �a !!!
      SLog.logWriteLine("Message SConeGeometry : Intersection trop pr�s de la point du c�ne.");
      return S12;
    }
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
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_CONE);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //�crire les propri�t�s de la classe SCercleGeometry et ses param�tres h�rit�s
    writeSTubeGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

 
  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SConeGeometry 004 : Cette m�thode n'a pas encore �t� impl�ment�e.");
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
