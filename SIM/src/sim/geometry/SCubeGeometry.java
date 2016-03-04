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
import sim.math.SMath;
import sim.math.SVector3d;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SCubeGeometry</b> représente un cube aligné sur les axes x,y et z qui est positionné par rapport à son centre. 
 * 
 * @author Simon Vézina
 * @since 2015-10-19
 * @version 2015-12-02
 */
public class SCubeGeometry extends SAbstractGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_SIZE };
  
  private static final SVector3d PLANE_XY_PLUS_NORMAL = new SVector3d(0.0, 0.0, 1.0);
  private static final SVector3d PLANE_XY_NEG_NORMAL = new SVector3d(0.0, 0.0, -1.0);
  
  private static final SVector3d PLANE_XZ_PLUS_NORMAL = new SVector3d(0.0, 1.0, 0.0);
  private static final SVector3d PLANE_XZ_NEG_NORMAL = new SVector3d(0.0, -1.0, 0.0);
  
  private static final SVector3d PLANE_YZ_PLUS_NORMAL = new SVector3d(1.0, 0.0, 0.0);
  private static final SVector3d PLANE_YZ_NEG_NORMAL = new SVector3d(-1.0, 0.0, 0.0);
   
  /**
   * La constante 'DEFAULT_POSITION' correspond à la position par défaut d'un cube étant égale à l'origine (0,0,0).
   */
  private static final SVector3d DEFAULT_POSITION = new SVector3d(0.0, 0.0, 0.0);
  
  /**
   * La consante 'DEFAULT_SIZE' correspond à la taille par défaut d'un cube étant égale à {@value}.
   */
  private static final double DEFAULT_SIZE = 1.0;
  
  /**
   * La variable 'position' correspond à la position du cube (par rapport à son centre).
   */
  private SVector3d position;
  
  /**
   * La variable 'size' correspond à la taille du cube.
   */
  private double size;
  
  //-----------------------
  //Paramètre utilitaire
  //-----------------------
  
  /**
   * La variable 'positive_half_size' correspond à la moitié de la valeur <b>positive</b> de la taille (<i>size</i>) du cube . 
   * Cette variable est définie à l'initialisation et est utilisée pour accélérer certains calculs. 
   */
  private double positive_half_size;
  
  /**
   * La variable 'negative_half_size' correspond à la moitié de la valeur <b>négative</b> de la taille (<i>size</i>) du cube . 
   * Cette variable est définie à l'initialisation et est utilisée pour accélérer certains calculs. 
   */
  private double negative_half_size;
  
  private SPlaneGeometry planeXYplus;
  private SPlaneGeometry planeXYneg;
  
  private SPlaneGeometry planeXZplus;
  private SPlaneGeometry planeXZneg;
  
  private SPlaneGeometry planeYZplus;
  private SPlaneGeometry planeYZneg;
  
  /**
   *  Constructeur d'un cube par défaut. 
   */
  public SCubeGeometry()
  {
    this(DEFAULT_POSITION, DEFAULT_SIZE);
  }

  /**
   * Constructeur d'un cube sans parent primitive.
   * 
   * @param position - La position du cube (par rapport à son centre).
   * @param size - La dimension du cube.
   */
  public SCubeGeometry(SVector3d position, double size)
  {
    this(position, size, null);
  }
  
  /**
   * Constructeur d'un cube.
   * 
   * @param position - La position du cube (par rapport à son centre).
   * @param size - La dimension du cube.
   * @param parent - Le primitive parent au cube.
   * @throws SConstructorException Si la taille (<i>size</i>) du cube est négative.
   */
  public SCubeGeometry(SVector3d position, double size, SPrimitive parent)throws SConstructorException
  {
    super(parent);
    
    //Vérification que le rayon soit positif
    if(size < 0.0)
      throw new SConstructorException("Erreur SCubeGeometry 001 : Un cube de dimension size = " + size + " qui est négatif n'est pas une définition valide.");
    
    this.position = position;
    this.size = size;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SCubeGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }
  
  /**
   * Constructeur d'une sphère à partir d'information lue dans un fichier de format txt.
   * Puisqu'une géométrie est construite à l'intérieure d'une primitive, une référence à celle-ci doit être intégrée au constructeur pour y a voir accès.
   * 
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de de type I/O est lancée.
   * @see SBufferedReader
   * @see SPrimitive
   */
  public SCubeGeometry(SBufferedReader sbr, SPrimitive parent)throws IOException, SConstructorException
  {
    this(DEFAULT_POSITION, DEFAULT_SIZE, parent);    
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SCubeGeometry 003 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.CUBE_CODE;
  }

  @Override
  public boolean isClosedGeometry()
  {
    return true;
  }

  @Override
  public boolean isInside(SVector3d v)
  {
    //Évaluer la position du vecteur v par rapport au centre du cube.
    SVector3d p = v.substract(position);
    
    //Modifier les conditions de validation afin qu'un point sur le cube ne soit pas à l'intérieur
    double positive_value = (1 - SMath.EPSILON) * positive_half_size;
    double negative_value = (1 - SMath.EPSILON) * negative_half_size;
    
    //Vérifier si la positive du vecteur p se trouve à l'intérieur des dimensions 'modifiées' du cube selon l'axe x, y et z.
    if(p.getX() > positive_value)
      return false;
    
    if(p.getX() < negative_value)
      return false;
    
    if(p.getY() > positive_value)
      return false;
    
    if(p.getY() < negative_value)
      return false;
    
    if(p.getZ() > positive_value)
      return false;
    
    if(p.getZ() < negative_value)
      return false;
    
    return true;
  }

  /* (non-Javadoc)
   * @see sim.geometry.SGeometry#intersection(sim.geometry.SRay)
   */
  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    //S'assurer que le rayon n'a rien intersecté auparavant
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur SCubeGeometry 004 : Le rayon a déjà intersecté une autre géométrie.");
    
    //Considérons que l'intersection la plus près est un rayon sans intersection
    SRay closer_intersection_ray = ray;
    
    //Effectuons la mise à jour du rayon d'intersection le plus près en testant avec les 6 plans du cube.
    //Une affectation avec l'intersection le plus près sera déterminé dans l'appel des méthodes qui suivent.
    closer_intersection_ray = intersectionPlaneXY(ray, closer_intersection_ray, planeXYplus, PLANE_XY_PLUS_NORMAL);
    closer_intersection_ray = intersectionPlaneXY(ray, closer_intersection_ray, planeXYneg, PLANE_XY_NEG_NORMAL);
    
    closer_intersection_ray = intersectionPlaneXZ(ray, closer_intersection_ray, planeXZplus, PLANE_XZ_PLUS_NORMAL);
    closer_intersection_ray = intersectionPlaneXZ(ray, closer_intersection_ray, planeXZneg, PLANE_XZ_NEG_NORMAL);
    
    closer_intersection_ray = intersectionPlaneYZ(ray, closer_intersection_ray, planeYZplus, PLANE_YZ_PLUS_NORMAL);
    closer_intersection_ray = intersectionPlaneYZ(ray, closer_intersection_ray, planeYZneg, PLANE_YZ_NEG_NORMAL);
    
    return closer_intersection_ray;
  }

  /**
   * Méthode qui effectue l'intersection d'un ray avec un plan xy. Si l'intersection est réalisée, on compare le temps de l'intersection
   * avec le temps d'intersection d'un autre rayon pour connaître la plus rapprochée. Si la nouvelle intersection est plus près,
   * on évalue ses caractéristiques et l'on retourne un nouveau rayon avec la nouvelle intersection, sinon on retourne l'ancien rayon. 
   *  
   * @param ray - Le rayon réalisant l'intersection avec le plan xy.
   * @param closer_intersection_ray - Un rayon ayant préalablement déjà réalisé une intersection avec une autre face du cube.
   * @param plane - Le plan xy à intersecté dont la position z peut différer d'un test à un autre.
   * @param outside_normal - La normale à la surface du plan xy pointant vers l'extérieur du cube.
   * @return Un nouveau rayon intersecté si la nouvelle intersection est plus près que l'ancienne intersection, sinon on retourne le rayon <i>closer_intersection_ray</i>. 
   */
  private SRay intersectionPlaneXY(SRay ray, SRay closer_intersection_ray, SPlaneGeometry plane, SVector3d outside_normal)
  {
    //Réaliser le test de l'intersection sur le plan XY plus
    SRay intersection_ray = plane.intersection(ray);
    
    if(!intersection_ray.asIntersected())
      return closer_intersection_ray;
    
    //Évaluons les caractéristiques de l'intersection uniquement si elle est plus près que l'intersection précédente
    if(intersection_ray.getT() < closer_intersection_ray.getT())
    {
      //Vérifions si l'intersection est à l'intérieur du carré en utilisant une position d'intersection par rapport au centre du cube
      SVector3d p = intersection_ray.getIntersectionPosition().substract(position);
      
      if(    p.getX() < positive_half_size && p.getX() > negative_half_size
          && p.getY() < positive_half_size && p.getY() > negative_half_size)
      {
        //Puisque l'intersection se réalise à l'intérieur du carré, 
        //il ne reste plus qu'à déterminer si l'intersection se fait par l'intérieur ou l'extérieur du cube
        if(intersection_ray.getNormal().dot(outside_normal) > 0.0)
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), false);
        else
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), true);
      }
      else
        return closer_intersection_ray;
    }
    else
      return closer_intersection_ray;
  }
  
  /**
    * Méthode qui effectue l'intersection d'un ray avec un plan xz. Si l'intersection est réalisée, on compare le temps de l'intersection
   * avec le temps d'intersection d'un autre rayon pour connaître la plus rapprochée. Si la nouvelle intersection est plus près,
   * on évalue ses caractéristiques et l'on retourne un nouveau rayon avec la nouvelle intersection, sinon on retourne l'ancien rayon. 
   *  
   * @param ray - Le rayon réalisant l'intersection avec le plan xz.
   * @param closer_intersection_ray - Un rayon ayant préalablement déjà réalisé une intersection avec une autre face du cube.
   * @param plane - Le plan xz à intersecté dont la position y peut différer d'un test à un autre.
   * @param outside_normal - La normale à la surface du plan xz pointant vers l'extérieur du cube.
   * @return Un nouveau rayon intersecté si la nouvelle intersection est plus près que l'ancienne intersection, sinon on retourne le rayon <i>closer_intersection_ray</i>. 
   */
  private SRay intersectionPlaneXZ(SRay ray, SRay closer_intersection_ray, SPlaneGeometry plane, SVector3d outside_normal)
  {
    //Réaliser le test de l'intersection sur le plan XZ plus
    SRay intersection_ray = plane.intersection(ray);
    
    if(!intersection_ray.asIntersected())
      return closer_intersection_ray;
    
    //Évaluons les caractéristiques de l'intersection uniquement si elle est plus près que l'intersection précédente
    if(intersection_ray.getT() < closer_intersection_ray.getT())
    {
      //Vérifions si l'intersection est à l'intérieur du carré en utilisant une position d'intersection par rapport au centre du cube
      SVector3d p = intersection_ray.getIntersectionPosition().substract(position);
      
      if(    p.getX() < positive_half_size && p.getX() > negative_half_size
          && p.getZ() < positive_half_size && p.getZ() > negative_half_size)
      {
        //Puisque l'intersection se réalise à l'intérieur du carré, 
        //il ne reste plus qu'à déterminer si l'intersection se fait par l'intérieur ou l'extérieur du cube
        if(intersection_ray.getNormal().dot(outside_normal) > 0.0)
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), false);
        else
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), true);
      }
      else
        return closer_intersection_ray;
    }
    else
      return closer_intersection_ray;
  }
  
  /**
   * Méthode qui effectue l'intersection d'un ray avec un plan yz. Si l'intersection est réalisée, on compare le temps de l'intersection
   * avec le temps d'intersection d'un autre rayon pour connaître la plus rapprochée. Si la nouvelle intersection est plus près,
   * on évalue ses caractéristiques et l'on retourne un nouveau rayon avec la nouvelle intersection, sinon on retourne l'ancien rayon. 
   *  
   * @param ray - Le rayon réalisant l'intersection avec le plan yz.
   * @param closer_intersection_ray - Un rayon ayant préalablement déjà réalisé une intersection avec une autre face du cube.
   * @param plane - Le plan xz à intersecté dont la position x peut différer d'un test à un autre.
   * @param outside_normal - La normale à la surface du plan yz pointant vers l'extérieur du cube.
   * @return Un nouveau rayon intersecté si la nouvelle intersection est plus près que l'ancienne intersection, sinon on retourne le rayon <i>closer_intersection_ray</i>. 
   */
  private SRay intersectionPlaneYZ(SRay ray, SRay closer_intersection_ray, SPlaneGeometry plane, SVector3d outside_normal)
  {
    //Réaliser le test de l'intersection sur le plan YZ plus
    SRay intersection_ray = plane.intersection(ray);
    
    if(!intersection_ray.asIntersected())
      return closer_intersection_ray;
    
    //Évaluons les caractéristiques de l'intersection uniquement si elle est plus près que l'intersection précédente
    if(intersection_ray.getT() < closer_intersection_ray.getT())
    {
      //Vérifions si l'intersection est à l'intérieur du carré en utilisant une position d'intersection par rapport au centre du cube
      SVector3d p = intersection_ray.getIntersectionPosition().substract(position);
      
      
      if(    p.getY() < positive_half_size && p.getY() > negative_half_size
          && p.getZ() < positive_half_size && p.getZ() > negative_half_size)
      {
        //Puisque l'intersection se réalise à l'intérieur du carré, 
        //il ne reste plus qu'à déterminer si l'intersection se fait par l'intérieur ou l'extérieur du cube
        if(intersection_ray.getNormal().dot(outside_normal) > 0.0)
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), false);
        else
          return ray.intersection(this, intersection_ray.getNormal(), intersection_ray.getT(), true);
      }
      else
        return closer_intersection_ray;
    }
    else
      return closer_intersection_ray;
  }
  
  /* (non-Javadoc)
   * @see sim.util.SWriteable#write(java.io.BufferedWriter)
   */
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_CUBE);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SSphereGeometry et ses paramètres hérités
    writeSCubeGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SCubeGeometry et ses paramètres hérités.
   * 
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSCubeGeometryParameter(BufferedWriter bw)throws IOException
  {
    bw.write(SKeyWordDecoder.KW_POSITION);
    bw.write("\t");
    position.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_SIZE);
    bw.write("\t\t");
    bw.write(Double.toString(size));
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SCubeGeometry 005 : La méthode n'est pas définie.");
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SCubeGeometry 006 : La méthode n'est pas définie.");
  }

  /**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
    positive_half_size = size/2;
    negative_half_size = -1*size/2;
    
    planeXYplus = new SPlaneGeometry(position.add(new SVector3d(0.0, 0.0, positive_half_size)), PLANE_XY_PLUS_NORMAL);
    planeXYneg = new SPlaneGeometry(position.add(new SVector3d(0.0, 0.0, negative_half_size)), PLANE_XY_NEG_NORMAL);
    
    planeXZplus = new SPlaneGeometry(position.add(new SVector3d(0.0, positive_half_size, 0.0)), PLANE_XZ_PLUS_NORMAL);
    planeXZneg = new SPlaneGeometry(position.add(new SVector3d(0.0, negative_half_size, 0.0)), PLANE_XZ_NEG_NORMAL);
    
    planeYZplus = new SPlaneGeometry(position.add(new SVector3d(positive_half_size, 0.0, 0.0)), PLANE_YZ_PLUS_NORMAL);
    planeYZneg = new SPlaneGeometry(position.add(new SVector3d(negative_half_size, 0.0, 0.0)), PLANE_YZ_NEG_NORMAL); 
  }

  /* (non-Javadoc)
   * @see sim.util.SAbstractReadable#read(sim.util.SBufferedReader, int, java.lang.String)
   */
  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    switch(code)
    {
      case SKeyWordDecoder.CODE_POSITION : position = new SVector3d(remaining_line); return true;
                          
      case SKeyWordDecoder.CODE_SIZE :     size = readDoubleGreaterThanZero(remaining_line, SKeyWordDecoder.KW_SIZE); return true;
          
      default : return false;
    }
  }

  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur SCubeGeometry 007 : La méthode n'a pas encore été implémentée.");
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
    return SKeyWordDecoder.KW_CUBE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SCubeGeometry
