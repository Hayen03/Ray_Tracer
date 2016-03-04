/**
 * 
 */
package sim.geometry;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SMath;
import sim.math.SVector3d;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SStringUtil;

/**
 * La classe <b>SCylinderGeometry</b> repr�sente la g�om�trie d'un cylindre. 
 * H�ritant de la classe STubeGeometry, le cylindre poss�de deux disques fermant les deux extr�mit�s du tube.
 * Le cylindre est une g�om�trie ferm�e.
 * 
 * @author Simon V�zina
 * @since 2015-07-07
 * @version 2015-12-21
 */
public class SCylinderGeometry extends STubeGeometry {

  //--------------------------
  // PARAM�TRE DE PR�CALCUL //
  //--------------------------
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un cylindre par d�faut. 
   */
  public SCylinderGeometry()
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R);
  }

  /**
   * Constructeur d'un cylindre.
   * 
   * @param P1 - Le 1ier point du cylindre. 
   * @param P2 - Le 2i�me point du cylindre.
   * @param R - Le rayon du cylindre.
   */
  public SCylinderGeometry(SVector3d P1, SVector3d P2, double R)
  {
    this(P1, P2, R, null);
  }

  /**
   * Constructeur d'un tube avec une primitive comme parent.
   * 
   * @param P1 - Le 1ier point du cylindre. 
   * @param P2 - Le 2i�me point du cylindre.
   * @param R - Le rayon du cylindre.
   * @param parent - La primitive en parent.
   * @throws SConstructorException Si une erreur lors de la construction de la g�om�trie est survenue.
   */
  public SCylinderGeometry(SVector3d P1, SVector3d P2, double R, SPrimitive parent) throws SConstructorException
  {
    super(P1, P2, R, parent);
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SCylinderGeometry 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }

  /**
   * Constructeur d'un cylindre � partir d'information lue dans un fichier de format txt.
   * Puisqu'une g�om�trie est construite � l'int�rieure d'une primitive, une r�f�rence � celle-ci doit �tre int�gr�e au constructeur pour y a voir acc�s.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette g�om�trie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lanc�e.
   * @see SBufferedReader
   * @see SPrimitive
   */
  public SCylinderGeometry(SBufferedReader sbr, SPrimitive parent) throws IOException, SConstructorException
  {
    super(sbr, parent);
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SCylinderGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
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
  public int getCodeName()
  {
    return SAbstractGeometry.CYLINDER_CODE;
  }
  
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    //S'assurer que le rayon n'a rien intersect� auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SCylinderGeometry 003 : Le rayon a d�j� intersect� une autre g�om�trie.");
    
    return ray;
  }
  
  @Override
  public boolean isClosedGeometry()
  {
    return true;
  }
  
  @Override
  public boolean isInside(SVector3d v)
  {
    //V�rifier si le vecteur v est � l'int�rieur des deux extr�mit�s P1 et P2 du tube
    if(!isInsideExtremity(v))
      return false;
    
    //Puisque le vecteur v se retrouve entre P1 et P2, v�rifions qu'il est � l'int�rieur du rayon R
    SVector3d D = S12.cross( v.substract(P1) ).cross(S12);
    if(D.modulus() < (1 - SMath.EPSILON) * R)     //distance entre v et l'axe du cylindre s < rayon R
      return true;
    else                                          //distance entre v et l'axe du cylindre s > rayon R 
      return false;
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    //�valuation de la normale � la surface ext�rieure � l'endroit de l'intersection (normalis�e)
    SVector3d ext_normal = S12.cross( ray.getPosition(intersection_t).substract(P1) ).cross(S12).normalize();
    
    //V�rifier si le rayon poss�de une orientation dans le sens oppos� � la normal ext�rieure du tube
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return false;            //orientation oppos�e, donc intersection venant de l'ext�rieur
    else
      return true;             //orientation dans le m�me sens, donc intersection venant de l'int�rieur
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_CYLINDER);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //�crire les propri�t�s de la classe SCercleGeometry et ses param�tres h�rit�s
    writeSTubeGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
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
    return SKeyWordDecoder.KW_CYLINDER;
  }
  
}//fin classe SCylinderGeometry
