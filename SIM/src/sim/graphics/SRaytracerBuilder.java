/**
 * 
 */
package sim.graphics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.geometry.SGeometry;
import sim.geometry.space.SAbstractGeometrySpace;
import sim.geometry.space.SGeometrySpace;
import sim.geometry.space.SLinearSpace;
import sim.geometry.space.SMultiVoxelSpace;
import sim.geometry.space.SVoxelSpace;
import sim.graphics.light.SLight;
import sim.graphics.shader.SAbstractShader;
import sim.graphics.shader.SPhongReflexionShader;
import sim.graphics.shader.SRecursiveShader;
import sim.graphics.shader.SShader;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SRaytracerBuilder</b> représente un <b>constructeur de raytracer</b> à partir d'information lue dans un fichier txt.
 * La lecture permettra de définir les propriétés du <b>shader</b> ainsi que du <b>raytracer</b>. 
 * 
 * @author Simon Vézina
 * @since 2015-07-08
 * @version 2015-12-04
 */
public class SRaytracerBuilder extends SAbstractReadableWriteable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_TASK, SKeyWordDecoder.KW_SAMPLING, SKeyWordDecoder.KW_RECURSIVE_LEVEL,
    SKeyWordDecoder.KW_SPECULAR_REFLEXION, SKeyWordDecoder.KW_PIXEL_COORDINATE,
    SKeyWordDecoder.KW_SPACE, SKeyWordDecoder.KW_COLOR_NORMALIZATION
  };
  
  private static int DEFAULT_NB_TASK = 1;       //nombre de tâches en multiprocesseur
  private static int DEFAULT_NB_SAMPLING = 1;   //nombre de rayon par pixel
  
  //Paramètres pour la définition du shader
  private int specular_reflexion_algorithm;   
  private int recursive_level;
  
  //Paramètre pour la définition du view frustum
  private int pixel_internal_coordinate;
  
  //Paramètre pour la définition du ray tracer
  private int nb_task;                          //le nombre de tâches simultanées qui seront effectuées durant le calcul de l'image (multi-processeur)
  private int nb_sampling;                      //le nombre de rayon dans le calcul de la couleur d'un pixel
  
  //Paramètre pour la définition de l'espace des géométrie
  private int type_of_space;
  
  /**
   * Constructeur d'un constructeur à raytracer par défaut. 
   */
  public SRaytracerBuilder()
  {
    specular_reflexion_algorithm = SAbstractShader.BLINN_SPECULAR_REFLEXION;
    recursive_level = 1;
    
    pixel_internal_coordinate = SViewFrustum.TOP_LEFT_PIXEL;
    
    nb_task = DEFAULT_NB_TASK;
    nb_sampling = DEFAULT_NB_SAMPLING;
    
    type_of_space = SAbstractGeometrySpace.LINEAR;
    
    try{
      initialize();
    }catch(SInitializationException e){
      //throw new SConstructorException("Erreur SRaytracerBuilder 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
      throw new SRuntimeException("Erreur SRaytracerBuilder 001 : Une erreur dans un constructeur vide ne doit jamais se produire !!!" + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  /**
   * Constructeur d'un constructeur à raytracer à partir d'information lue dans un fichier de format .txt.
   * @param sbr - Le BufferedReader cherchant l'information de le fichier .txt.
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SConstructorException Si une erreur est survenue à la construction.
   * @see SBufferedReader
   */
  public SRaytracerBuilder(SBufferedReader sbr)throws IOException, SConstructorException
  {
    this();   //configuration de base s'il y a des paramètres non défini.
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SRaytracerBuilder 002 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }  
  }
  
  /**
   * Méthode pour faire la construction d'un raytracer à partir de plusieurs paramètres.
   * @param viewport - Le viewport.
   * @param camera - La caméra.
   * @param geometry_space - L'espace des géométries de la scène. 
   * @param light_list - La liste des lumières de la scène.
   * @return Le raytracer pouvant faire le rendu de la scène.
   * @throws SRuntimeException Si le code associé à l'espace des géométries n'est pas reconnu par le système.
   */
  public SRaytracer buildRaytracer(SViewport viewport, SCamera camera, List<SGeometry> geometry_list, List<SLight> light_list) throws SRuntimeException
  {
    //Construction de l'espace des géométries
    SGeometrySpace geometry_space;
    
    switch(type_of_space)
    {
      case SAbstractGeometrySpace.LINEAR : geometry_space = new SLinearSpace(); break;
      
      case SAbstractGeometrySpace.VOXEL : geometry_space = new SVoxelSpace(); break;
         
      case SAbstractGeometrySpace.MULTI_VOXEL : geometry_space = new SMultiVoxelSpace(); break;
      
      default : throw new SRuntimeException("Erreur SRaytracerBuilder 003 : Le type d'espace de code '" + type_of_space + "' n'est pas reconnu par le système.");
    }
    
    geometry_space.addGeometry(geometry_list);  //ajouter les géométries à l'espace des géométries
    geometry_space.initialize();                //faire l'initialisation de l'espace (précalcul pour accélérer les calculs d'intersection)
    
    //Construction du shader
    SShader shader;
    
    if(recursive_level == 1)
      shader = new SPhongReflexionShader(geometry_space, camera.getZFar(), light_list, specular_reflexion_algorithm);
    else
      shader = new SRecursiveShader(geometry_space, camera.getZFar(), light_list, specular_reflexion_algorithm, recursive_level);
    
    //Construction du view frustum
    SViewFrustum view_frustum = new SViewFrustum(camera, viewport, pixel_internal_coordinate);
    
    //Construction du raytracer adéquat
    if(nb_sampling == 1 && nb_task == 1)
      return new SSingleCastRaytracer(view_frustum, shader, viewport);
    else
      //return new SThreadPoolCastRaytracer(view_frustum, shader, viewport, nb_sampling, nb_task);
      return new SMultiCastRaytracer(view_frustum, shader, viewport, nb_sampling, nb_task);
  }
  
  /* (non-Javadoc)
   * @see sim.util.SWriteable#write(java.io.BufferedWriter)
   */
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_RAYTRACER);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les paramètres de la classe SPrimitive
    writeSRaytracerBuilderParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write(SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SRaytracerBuilder et ses paramètres hérités.
   * 
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeSRaytracerBuilderParameter(BufferedWriter bw)throws IOException
  {
    bw.write(SKeyWordDecoder.KW_TASK);
    bw.write("\t\t\t");
    bw.write(Integer.toString(nb_task));
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_SAMPLING);
    bw.write("\t\t");
    bw.write(Integer.toString(nb_sampling));
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_RECURSIVE_LEVEL);
    bw.write("\t\t");
    bw.write(Integer.toString(recursive_level));
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_SPECULAR_REFLEXION);
    bw.write("\t");
    bw.write(SAbstractShader.SPECULAR_REFLEXION[specular_reflexion_algorithm]);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_PIXEL_COORDINATE);
    bw.write("\t");
    bw.write(SViewFrustum.PIXEL_COORDINATE[pixel_internal_coordinate]);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_SPACE);
    bw.write("\t\t\t");
    bw.write(SAbstractGeometrySpace.TYPE_OF_SPACE[type_of_space]);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_COLOR_NORMALIZATION);
    bw.write("\t");
    bw.write(SColor.COLOR_NORMALIZATION[SColor.getColorNormalization()]);
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
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    switch(code)
    {
      case SKeyWordDecoder.CODE_TASK :                nb_task = readIntGreaterThanZero(remaining_line, SKeyWordDecoder.KW_TASK); return true;
      
      case SKeyWordDecoder.CODE_SAMPLING :            nb_sampling = readIntGreaterThanZero(remaining_line, SKeyWordDecoder.KW_SAMPLING); return true;
                         
      case SKeyWordDecoder.CODE_RECURSIVE_LEVEL :     recursive_level = readIntGreaterThanZero(remaining_line, SKeyWordDecoder.KW_RECURSIVE_LEVEL); return true;
      
      case SKeyWordDecoder.CODE_SPECULAR_REFLEXION :  specular_reflexion_algorithm = readIntOrExpression(remaining_line, SKeyWordDecoder.KW_SPECULAR_REFLEXION, SAbstractShader.SPECULAR_REFLEXION); return true;
      
      case SKeyWordDecoder.CODE_PIXEL_COORDINATE :    pixel_internal_coordinate = readIntOrExpression(remaining_line, SKeyWordDecoder.KW_PIXEL_COORDINATE, SViewFrustum.PIXEL_COORDINATE); return true;
      
      case SKeyWordDecoder.CODE_SPACE :               type_of_space = readIntOrExpression(remaining_line, SKeyWordDecoder.KW_SPACE, SAbstractGeometrySpace.TYPE_OF_SPACE); return true;
        
      case SKeyWordDecoder.CODE_COLOR_NORMALIZATION : SColor.setColorNormalization(readIntOrExpression(remaining_line, SKeyWordDecoder.KW_COLOR_NORMALIZATION, SColor.COLOR_NORMALIZATION)); return true;
      
      default : return false;
    }
  }
  
  @Override
  protected void readingInitialization() throws SInitializationException
  {
    initialize();
  }
  
  @Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_RAYTRACER;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin classe SRaytracerBuilder
