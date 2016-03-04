/**
 * 
 */
package sim.loader.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SMaterialAffectation;
import sim.graphics.SModel;
import sim.graphics.SModelReader;
import sim.graphics.SPrimitive;
import sim.graphics.material.SBlinnMaterial;
import sim.graphics.material.SBlinnTextureMaterial;
import sim.graphics.material.SMaterial;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.util.SAbstractReadable;
import sim.util.SBufferedReader;
import sim.util.SFileSearch;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SLog;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SModelMSRLoader</b> permet de charger un modèle dans un fichier MSR (Model SIM Renderer) étant le format de l'application de SIMREnderer.
 *  
 * @author Simon Vézina
 * @since 2015-07-30
 * @version 2015-12-04
 */
public class SModelMSRLoader extends SAbstractReadable implements SStringLoader {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_PRIMITIVE, SKeyWordDecoder.KW_BLINN_MATERIAL, 
    SKeyWordDecoder.KW_TEXTURE_MATERIAL, SKeyWordDecoder.KW_MODEL     
  };
  
  public static final String FILE_EXTENSION = "msr";  //extension des fichiers lu par ce loader
  
  public SModel model;                      //modèle qui sera généré par le loader
  
  private List<SPrimitive> primitive_list;  //liste des primitives lues dans le fichier
  private List<SMaterial> material_list;    //liste des matériaux lus dans le fichier
  private List<SModel> model_list;          //liste des modèles lus dans le fichier
  
  /**
   * Constructeur d'un loader de modèle de type .msr. 
   */
  public SModelMSRLoader()
  {
    try{
      initialize();
    }catch(SInitializationException e){
      //throw new SConstructorException("Erreur SModelMSRLoader 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
      throw new SRuntimeException("Erreur SModelMSRLoader 001 : Une erreur dans un constructeur vide de doit jamais arriver !!!" + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  @Override
  public Object load(String string) throws SLoaderException
  {
    //Vérification du nom de l'extension du fichier
    if(!SStringUtil.extensionFileLowerCase(string).equals(FILE_EXTENSION))
      throw new SLoaderException("Erreur SModelMSRLoader 002 : Le fichier '" + string + "' n'a pas l'extension '" + FILE_EXTENSION + "' appropriée.");
    
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", string);
    
    //Si fichier non trouvé
    if(!search.isFileFound())
      throw new SLoaderException("Erreur SModelMSRLoader 003 : Le fichier '" + string + "' n'est pas trouvé.");
    
    //S'il y a plusieurs copies ayant le même nom de fichier
    if(search.isManyFileFound())
      throw new SLoaderException("Erreur SModelMSRLoader 004 : Le fichier '" + string + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    //Lecture du fichier à partir du SFileSearch
    try{
      
      model = new SModel(string);                     //construction du modèle à remplir
      
      primitive_list = new ArrayList<SPrimitive>();   //construction de la liste des primitives
      material_list = new ArrayList<SMaterial>();     //construction de la liste des matériaux
      model_list = new ArrayList<SModel>();           //construction de la liste des modèles
      
      FileReader fr = new FileReader(search.getFileFoundList().get(0));
      SBufferedReader sbr = new SBufferedReader(fr);
    
      try{
        read(sbr);
      }catch(SInitializationException e){
        throw new SLoaderException("Erreur SModelMSRLoader 005 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
      }  
        
      sbr.close();
      fr.close();
      
      return model;
      
    }catch(FileNotFoundException e){
      throw new SLoaderException("Erreur SModelMSRLoader 006 : Le fichier '" + string + "' n'est pas trouvé." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }catch(IOException e){
      throw new SLoaderException("Erreur SModelMSRLoader 007 : Une erreur de type I/O est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
     
  }

  /**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
    // Réaliser l'initialisation si les listes des primitives et des matériaux ont été construite
    if(primitive_list != null && material_list != null)
    {
      //Réaliser l'affectation des matériaux aux primitives
      SMaterialAffectation aff = new SMaterialAffectation(primitive_list, material_list);
      aff.affectation();
      
      //Mettre l'ensemble des primitives de tous les modèles lus dans le fichier dans la liste des primitives
      for(SModel m : model_list)
        for(SPrimitive p : m.getPrimitiveList())
          primitive_list.add(p);
         
      //Message sur le nombre de géométrie dans le modèle
      SLog.logWriteLine("Message SModelMSRLoader : Le modèle '" + model.getFileName() + "' contient " + primitive_list.size() + " primitives.");
      
      //Remplir le modèle final de ses géométries
      for(SPrimitive p : primitive_list)
        model.addPrimitive(p);
    }
  }

  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    try{
      switch(code)
      {  
        case SKeyWordDecoder.CODE_PRIMITIVE :          primitive_list.add(new SPrimitive(sbr)); return true;
    
        case SKeyWordDecoder.CODE_BLINN_MATERIAL:      material_list.add(new SBlinnMaterial(sbr)); return true;
    
        case SKeyWordDecoder.CODE_TEXTURE_MATERIAL :   material_list.add(new SBlinnTextureMaterial(sbr)); return true;
        
        case SKeyWordDecoder.CODE_MODEL :              SModelReader m_reader = new SModelReader(sbr);
        
                                                       if(m_reader.asRead())
                                                         model_list.add((SModel)m_reader.getValue());
                                                       return true;
        default : return false;
      }
    }catch(SConstructorException e){
      throw new SReadingException("Erreur SModelMSRLoader 008 : Une erreur est survenue lors de la construction d'un paramètres du modèle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
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
    return SKeyWordDecoder.KW_MODEL_MSR;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SModelMSRLoader
