/**
 * 
 */
package sim.graphics;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.geometry.SAbstractGeometry;
import sim.geometry.SBTriangleGeometry;
import sim.geometry.SGeometry;
import sim.geometry.STransformableGeometry;
import sim.geometry.STriangleGeometry;
import sim.graphics.material.SMaterial;
import sim.graphics.material.STextureMaterial;
import sim.loader.SLoaderException;
import sim.loader.model.SModelLoader;
import sim.math.SImpossibleNormalizationException;
import sim.math.SVector3d;
import sim.util.SAbstractReadable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SLog;
import sim.util.SReader;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SModelReader</b> représente un lecteur de modèle 3d. 
 * L'objectif de ce lecteur sera la lecture des propriétés du modèle dans le fichier de scène comme le nom du fichier principalement.
 * 
 * @author Simon Vézina
 * @since 2015-07-22
 * @version 2015-12-21
 */
public class SModelReader extends SAbstractReadable implements SReader {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { 
    SKeyWordDecoder.KW_FILE, SKeyWordDecoder.KW_SCALE, SKeyWordDecoder.KW_ROTATION,
    SKeyWordDecoder.KW_TRANSLATION, SKeyWordDecoder.KW_UV_FORMAT  
  };
  
  /**
   * La constante <b>DEFAULT_MODEL</b> correspond au modèle par défaut étant inexistant.
   * Sa valeur est alors <b>null</b>.
   */
  private static final SModel DEFAULT_MODEL = null;    //modèle par défaut étant non chargé
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable <b>model_map</b> correspond à la carte des modèles déjà lu et chargé en mémoire.
   * On peut ainsi les réutiliser et leur appliquer de nouvelles transformation pour les visualiser différemment.
   * La <b>clé de recherche</b> est le <b>nom du fichier</b>.
   */
  private static final Map<String, SModel> model_map = new HashMap<String, SModel>();
  
  /**
   * La variable <b>file_name</b> correspond au nom du fichier comprenant les informations définissant le modèle.
   */
  private String file_name;                
  
  /**
   * La variable <b>scale</b> correspond au vecteur comprenant les informations de la transformation d'hométhétie (<i>scale</i>).
   */
  private SVector3d scale;                  
  
  /**
   * La variable <b>rotation</b> correspond au vecteur comprenant les informations de la transformation de rotation.
   */
  private SVector3d rotation;               
  
  /**
   * La variable <b>translation</b> correspond au vecteur comprenant les informations de la transformation de translation.
   */
  private SVector3d translation;           
  
  /**
   * La variable <b>uv_format</b> correspond au code d'interprétation des coordonnées uv que l'on doit appliquer au texture
   * afin que chaque coordonnée uv corresponde au bon texel des textures.
   */
  private int uv_format;
  
  /**
   * La variable <b>model</b> correspond au modèle lu par le lecteur.
   */
  SModel model;
  
  /**
   * La varaible <b>is_read</b> détermine si un modèle a été lu avec succès par le lecteur.
   */
  boolean is_read;
  
  //-----------------
  // CONSTRUCTEURS //
  //-----------------
  
  /**
   * Constructeur d'un lecteur de modèle par défaut. 
   */
  public SModelReader()
  {
    file_name = SModel.DEFAULT_FILE_NAME;
    
    scale = SModel.DEFAULT_SCALE;
    rotation = SModel.DEFAULT_ROTATION;
    translation = SModel.DEFAULT_TRANSLATION;
    
    uv_format = STexture.UV_DEFAULT;
    
    model = DEFAULT_MODEL;
    is_read = false;
  }

  /**
   * Constructeur d'un constructeur de géométrie à partir d'information lue dans un fichier de format txt.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   */
  public SModelReader(SBufferedReader sbr)throws IOException, SConstructorException
  {
    this();
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SModelReader 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }
  
  @Override
  public SModel getValue() throws SRuntimeException
  {
    if(is_read)
      return model;
    else
      throw new SRuntimeException("Erreur SModelReader 002 : Le modèle '" + file_name + "' n'a pas été lu.");
  }

  @Override
  public boolean asRead()
  {
    return is_read;
  }
    
  /**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
    try{
      
      //Vérifier si le modèle a déjà été lu
      if(model_map.containsKey(file_name))
        model = model_map.get(file_name);
      else
      {
        SModelLoader model_loader = new SModelLoader();
      
        SLog.logWriteLine("Message SModelReader : Lecture du modèle '" + file_name + "'.");
        
        model = model_loader.loadModel(file_name);  //lecture du modèle (exception lancée s'il y a eu erreur)
        
        model_map.put(file_name, model);            //mettre le modèle lu dans la carte des modèles
      }
      
      //Exécuter la transformation du modèle par la construction d'un nouveau
      model = transformModel(model);
      
      is_read = true;
      
    }catch(SLoaderException e){
      SLog.logWriteLine("Erreur SModelReader 003 : Le chargement du modèle '" + file_name + "' est impossible. " + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
  }

  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    //Lecture des différentes géométrie reconnues par le lecteur
    switch(code)
    {
      case SKeyWordDecoder.CODE_FILE : file_name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_FILE); return true;
    
      case SKeyWordDecoder.CODE_SCALE : scale = new SVector3d(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_ROTATION : rotation = new SVector3d(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_TRANSLATION : translation = new SVector3d(remaining_line); return true;
      
      case SKeyWordDecoder.CODE_UV_FORMAT : uv_format = readIntOrExpression(remaining_line, SKeyWordDecoder.KW_UV_FORMAT, STexture.UV_FORMAT); return true;
      
      default : return false;
    }
  }

  /**
   * Méthode pour transformer l'intégralité des géométries contenues dans le modèle en fonction des matrices de transformation.
   * Un nouveau modèle avec l'applications des matrices de transformation sera généré.
   * @param model - Le modèle à transformer.
   * @return Un nouveau modèle où l'application des matrices de transformation a été réalisée.
   */
  private SModel transformModel(SModel model)
  {
    SModel transformed_model = new SModel(file_name, scale, rotation, translation, uv_format);
    
    List<SPrimitive> list = model.getPrimitiveList();
    
    //On transforme toutes les primitives 
    int geometry_error = 0;                     //géométrie en erreur de transformation
    
    for(SPrimitive p : list)
    {
      SGeometry geometry;                       //la primitive transformée à ajouter à la liste
      SMaterial material = p.getMaterial();     //le matériel de la géométrie  
      
      //Modification des géométries en fonction du type qu'elle est qui est identifié par un code de référence
      try{
        
        switch(p.getGeometry().getCodeName())
        {
          case SAbstractGeometry.TRIANGLE_CODE :  geometry = transformTriangleGeometry((STriangleGeometry)p.getGeometry()); break;
          
          case SAbstractGeometry.BTRIANGLE_CODE : geometry = transformBTriangleGeometry((SBTriangleGeometry)p.getGeometry()); break;
          
          default :                               geometry = new STransformableGeometry(p.getGeometry(), scale, rotation, translation); break;
        }
        
        // Modifier le format d'interprétation des coordonnées uv de texture pour un matériel avec texture
        // Cependant, plusieurs instance du modèle peuvent être construire, mais une seule interprétation des
        // coordonnée uv sera possible. Ce sera la première définition qui sera retenue.
        if(material.asTexture())
        {
          STextureMaterial texture_material = (STextureMaterial) material;
          if(!texture_material.isUVFormatSelected())
            texture_material.setUVFormat(uv_format);
        }
        
        transformed_model.addPrimitive(new SPrimitive(geometry, material));      //ajouter la primitive transformée au nouveau modèle
        
      }catch(SConstructorException e){
        //S'il y a des géométries en erreur de construction après l'application des transformations
        geometry_error++;
      }catch(SImpossibleNormalizationException e){
        geometry_error++;
      }   
    }//fin for
      
    //Afficher un message sur le nombre de géométrie en erreur de transformation
    if(geometry_error > 0)
      SLog.logWriteLine("Message SModelReader : Il y a '" + geometry_error + "' géométries en erreur de transformation. Ils ne seront pas disponibles pour l'affichage.");
    
    return transformed_model;
  }
  
  /**
   * Méthode pour faire la transformation d'un STriangleGeometry dans l'espace unitaire vers l'espace transformé à partir des vecteurs de transformation lus par le lecteur.
   * @param triangle - Le triangle à transformé
   * @return La géométrie d'un triangle unitaire transformé.
   */
  private SGeometry transformTriangleGeometry(STriangleGeometry triangle)
  {
    //Version sans transformation des points du triangles
    return new STriangleGeometry(triangle.getP0(), triangle.getP1(), triangle.getP2());
  }
  
  /**
   * Méthode pour faire la transformation d'un SBTriangleGeometry dans l'espace unitaire vers l'espace transformé à partir des vecteurs de transformation lus par le lecteur.
   * @param triangle - Le triangle à transformé
   * @return La géométrie d'un triangle barycentrique unitaire transformé.
   * @throws SImpossibleNormalizationException Si un vecteur normale n'a pas pu être normalisé adéquatement.
   */
  private SGeometry transformBTriangleGeometry(SBTriangleGeometry triangle) throws SImpossibleNormalizationException
  {
    //Version sans transformation des triangles barycentrique
    return new SBTriangleGeometry(triangle.getP0(), triangle.getP1(), triangle.getP2(),
                                  triangle.getN0(), triangle.getN1(), triangle.getN2(), 
                                  triangle.getUV0(), triangle.getUV1(), triangle.getUV2());
  }
  
  @Override
  protected void readingInitialization() throws SInitializationException
  {
    initialize();
  }
  
  @Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_MODEL;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SModelReader
