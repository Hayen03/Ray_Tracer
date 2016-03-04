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
import sim.util.SLog;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>STransformableGeometry</b> qui représente une géométrie pouvant être transformée à l'aide de matrices de transformations linéaires
 * comme la <b>translation</b>, la <b>rotation</b> et <b>l'homothétie</b> (<i>scale</i>). Cette géométrie devra contenir une géométrie interne
 * donnant la forme de base (sans transformation) à la géométrie transformable.
 * 
 * @author Simon Vézina
 * @since 2015-07-17
 * @version 2015-12-21
 */
public class STransformableGeometry extends SAbstractGeometry {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { 
    SKeyWordDecoder.KW_SCALE, SKeyWordDecoder.KW_ROTATION, SKeyWordDecoder.KW_TRANSLATION
  };
  
  /**
   * La constante <b>DEFAULT_GEOMETRY</b> correspond à la géométrie par défaut étant <b>null</b>.
   * Cela signifie qu'il n'y a pas de géométrie interne à la géométrie transformable.
   */
  private static final SGeometry DEFAULT_GEOMETRY = null;
  
  /**
   * La constante <b>DEFAULT_SCALE</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation d'homothétie (<i>scale</i>).
   * Par défaut, le vecteur sera (1.0, 1.0, 1.0) ce qui correspond à aucune déformation.
   */
  private static final SVector3d DEFAULT_SCALE = new SVector3d(1.0, 1.0, 1.0);
  
  /**
   * La constante <b>DEFAULT_ROTATION</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation de rotation.
   * Par défaut, le vecteur sera (0.0, 0.0, 0.0) ce qui correspond à aucune rotation.
   */
  private static final SVector3d DEFAULT_ROTATION = new SVector3d(0.0, 0.0, 0.0);
  
  /**
   * La constante <b>DEFAULT_TRANSLATION</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation de translation.
   * Par défaut, le vecteur sera (0.0, 0.0, 0.0) ce qui correspond à aucune translation.
   */
  private static final SVector3d DEFAULT_TRANSLATION = new SVector3d(0.0, 0.0, 0.0);
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>geometry</b> correspond à la géométrie interne à la géométrie transformable.
   */
  protected SGeometry geometry;       
  
  /**
   * La variable <b>isGeometry_selected</b> détermine si la géométrie interne a été sélectionnée.
   * Si tel est le cas, la variable <b>geometry</b> ne sera pas 'null'.
   */
  private boolean isGeometry_selected;  
  
  /**
   * La variable <b>scale</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation d'homothétie (<i>scale</i>).
   */
  private SVector3d scale;              
  
  /**
   * La variable <b>rotation</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation de rotation.
   */
  private SVector3d rotation;           
  
  /**
   * La variable <b>translation</b> correspond au vecteur comprenant les informations à attribuer à une matrice de transformation de translation.
   */
  private SVector3d translation;        
  
  //--------------------------------------------------
  //Paramètres de précalculs pour les transformations
  //--------------------------------------------------
  
  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur d'une géométrie transformable à l'aide d'une géométrie interne.
   * @param geometry - La géométrie interne.
   */
  public STransformableGeometry(SGeometry geometry)
  {
    this(geometry, DEFAULT_SCALE, DEFAULT_ROTATION, DEFAULT_TRANSLATION);
    
  }

  /**
   * Constructeur d'une géométrie transformable à l'aide d'une géométrie interne et des
   * matrices de transformations linéaires comme <b>l'homothéthie</b> (<i>scale</i>), <b>la rotation</b> et <b>la translation</b>.
   * @param geometry - La géométrie transformée.
   * @param scale - Le vecteur définissant la matrice <b>d'homothéthie</b> (<i>scale</i>).
   * @param rotation - Le vecteur définissant la matrice <b>de rotation</b> (en degré).
   * @param translate - Le vecteur définissant la matrice <b>de translation</b>.
   */
  public STransformableGeometry(SGeometry geometry, SVector3d scale, SVector3d rotation, SVector3d translation)
  {
    this(geometry, scale, rotation, translation, null);
  }
  
  /**
   * Constructeur d'une géométrie transformable à l'aide d'une géométrie interne relié à une primitive parent et des
   * matrices de transformations linéaires comme <b>l'homothéthie</b> (<i>scale</i>), <b>la rotation</b> et <b>la translation</b>.
   * @param geometry - La géométrie transformée.
   * @param scale - Le vecteur définissant la matrice <b>d'homothéthie</b> (<i>scale</i>).
   * @param rotation - Le vecteur définissant la matrice <b>de rotation</b> (en degré).
   * @param translate - Le vecteur définissant la matrice <b>de translation</b>.
   * @param parent - La primitive parent à cette géométrie.
   * @throws SConstructorException Si une erreur est survenue lors de la construction de la géométrie.
   */
  public STransformableGeometry(SGeometry geometry, SVector3d scale, SVector3d rotation, SVector3d translation, SPrimitive parent) throws SConstructorException
  {
    super(parent);
    
    this.geometry = geometry;
    
    //Vérifier si la géométrie a été définie, donc non nulle
    if(geometry != null)            
      isGeometry_selected = true;
    else
      isGeometry_selected = false;
    
    this.scale = scale;
    this.rotation = rotation;
    this.translation = translation;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur STransformableGeometry 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }
  
  /**
   * Constructeur d'une géométrie transformable à partir d'information lue dans un fichier de format txt.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param parent - La primitive qui fait la construction de cette géométrie (qui est le parent).
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SConstructorException Si une erreur est survenue lors de la construction de la géométrie.
   * @see SBufferedReader
   */
  public STransformableGeometry(SBufferedReader sbr, SPrimitive parent) throws IOException, SConstructorException
  {
    this(DEFAULT_GEOMETRY, DEFAULT_SCALE, DEFAULT_ROTATION, DEFAULT_TRANSLATION, parent);   
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur STransformableGeometry 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }
  
  @Override
  public int getCodeName()
  {
    return SAbstractGeometry.TRANSFORMABLE_CODE;
  }
  
  @Override
  public boolean isClosedGeometry()
  {
    // S'il y a une géométrie interne à la géométrie transformable
    if(geometry != null)
      return geometry.isClosedGeometry();
    else
      return false;
  }

  @Override
  public boolean isInside(SVector3d v)
  {
    // S'il y a une géométrie interne à la géométrie transformable
    if(geometry != null)
    {
      return false;
    }
    else
      return false;
  }

  @Override
  public SRay intersection(SRay ray) throws SRuntimeException
  {
    if(ray.asIntersected())
      throw new SRuntimeException("Erreur STransformableGeometry 003 : Le rayon a déjà intersecté une autre géométrie.");
    
    // S'il y a une géométrie interne à la géométrie transformable
    if(geometry != null)
    {
      return ray;
    }
    else
      return ray;
  }

  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_GEOMETRY);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SSphereGeometry et ses paramètres hérités
    writeSTransformableGeometryParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe STransformableGeometry et ses paramètres hérités.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSTransformableGeometryParameter(BufferedWriter bw)throws IOException
  {
    bw.write(SKeyWordDecoder.KW_SCALE);
    bw.write("\t");
    scale.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_ROTATION);
    bw.write("\t");
    rotation.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_TRANSLATION);
    bw.write("\t");
    translation.write(bw);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    if(geometry != null)
      geometry.write(bw);
    
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
  @Override
  protected boolean isInsideIntersection(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur STransformableGeometry 004 : Cette méthode n'est pas implémentée, car elle n'est pas utile au fonctionnement de la classe.");
  }

  @Override
  protected SVector3d evaluateIntersectionNormal(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur STransformableGeometry 005 : Cette méthode n'est pas implémentée, car elle n'est pas utile au fonctionnement de la classe.");
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
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    //Analyser le code sur la lecture d'une géométrie à l'aide du lecteur de géométrie
    SGeometryReader reader = new SGeometryReader(sbr, code, null);
    
    //S'il y a eu lecture d'une géométrie
    if(reader.isRead())
      if(!isGeometry_selected)
      {
        geometry = reader.getGeometry();
        isGeometry_selected = true;
        return true;
      }
      else
        throw new SReadingException("Erreur STranformableGeometry 006 : Une géométrie a déjà été sélectionnée pour cette géométrie transformable.");   
      
    //Analyser le code sur d'autres paramètres
    switch(code)
    {
      case SKeyWordDecoder.CODE_SCALE : scale = new SVector3d(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_ROTATION : rotation = new SVector3d(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_TRANSLATION : translation = new SVector3d(remaining_line); return true;
      
      default : return false;
    }
  }

  @Override
  protected SVectorUV evaluateIntersectionUV(SRay ray, double intersection_t)
  {
    throw new SNoImplementationException("Erreur STransformableGeometry 007 : La méthode n'a pas été implémentée.");
  }
  
  @Override
  protected void readingInitialization() throws SInitializationException
  {
    super.readingInitialization();
    
    initialize();
    
    //Affichage de message si la lecture est incomplète
    if(!isGeometry_selected)
      SLog.logWriteLine("Message STransformableGeometry : Une géométrie transformable (Geometry) a été lue mais initialisée sans géométrie interne.");
  }
  
  @Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_GEOMETRY;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    // Mettre les mots clé du lecteur de géométrie (SGeometryReader)
    other_parameters = SStringUtil.merge(other_parameters, SGeometryReader.KEYWORD_PARAMETER);
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe STransformableGeometry
