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
 * La classe <b>SCylinderGeometry</b> représente la géométrie d'un cylindre. 
 * Héritant de la classe STubeGeometry, le cylindre possède deux disques fermant les deux extrémités du tube.
 * Le cylindre est une géométrie fermée.
 * 
 * @author Simon Vézina
 * @since 2015-07-07
 * @version 2015-12-21
 */
public class SCylinderGeometry extends STubeGeometry {

  //--------------------------
  // PARAMÈTRE DE PRÉCALCUL //
  //--------------------------
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'un cylindre par défaut. 
   */
  public SCylinderGeometry()
  {
    this(DEFAULT_P1, DEFAULT_P2, DEFAULT_R);
  }

  /**
   * Constructeur d'un cylindre.
   * 
   * @param P1 - Le 1ier point du cylindre. 
   * @param P2 - Le 2ième point du cylindre.
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
   * @param P2 - Le 2ième point du cylindre.
   * @param R - Le rayon du cylindre.
   * @param parent - La primitive en parent.
   * @throws SConstructorException Si une erreur lors de la construction de la géométrie est survenue.
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
   * Constructeur d'un cylindre à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lancée.
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
   * Méthode pour faire l'initialisation de l'objet après sa construction.
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
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SCylinderGeometry 003 : Le rayon a déjà intersecté une autre géométrie.");
    
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
    //Vérifier si le vecteur v est à l'intérieur des deux extrémités P1 et P2 du tube
    if(!isInsideExtremity(v))
      return false;
    
    //Puisque le vecteur v se retrouve entre P1 et P2, vérifions qu'il est à l'intérieur du rayon R
    SVector3d D = S12.cross( v.substract(P1) ).cross(S12);
    if(D.modulus() < (1 - SMath.EPSILON) * R)     //distance entre v et l'axe du cylindre s < rayon R
      return true;
    else                                          //distance entre v et l'axe du cylindre s > rayon R 
      return false;
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    //Évaluation de la normale à la surface extérieure à l'endroit de l'intersection (normalisée)
    SVector3d ext_normal = S12.cross( ray.getPosition(intersection_t).substract(P1) ).cross(S12).normalize();
    
    //Vérifier si le rayon possède une orientation dans le sens opposé à la normal extérieure du tube
    if(ray.getDirection().dot(ext_normal) < 0.0)
      return false;            //orientation opposée, donc intersection venant de l'extérieur
    else
      return true;             //orientation dans le même sens, donc intersection venant de l'intérieur
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_CYLINDER);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SCercleGeometry et ses paramètres hérités
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
