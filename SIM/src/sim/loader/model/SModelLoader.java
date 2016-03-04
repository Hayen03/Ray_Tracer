/**
 * 
 */
package sim.loader.model;

import java.util.Hashtable;

import sim.graphics.SModel;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.util.SStringUtil;

/**
 * Classe qui représente un constructeur de modèle 3d à partir du nom du fichier. Le loader aura
 * pour fonction de choisir le bon type de loader selon le format du fichier à lire.
 * @author Simon Vézina
 * @since 2015-03-16
 * @version 2015-07-31
 */
public class SModelLoader {

  private final Hashtable<String,SStringLoader> model_loader_chooser;
  
  /**
   *  Constructeur du loader de modèle 3d. 
   */
  public SModelLoader()
  {
    model_loader_chooser = new Hashtable<String,SStringLoader>();
    
    //Insertion des loaders de modèle 3d 
    model_loader_chooser.put(SModelOBJLoader.FILE_EXTENSION, new SModelOBJLoader());
    model_loader_chooser.put(SModelMSRLoader.FILE_EXTENSION, new SModelMSRLoader());
    model_loader_chooser.put(SModelAGPLoader.FILE_EXTENSION, new SModelAGPLoader());
  }

  /**
   * Méthode qui effectue la construction d'un SModel à partir du nom d'un fichier.
   * @param file_name - Le nom du fichier en lecture.
   * @return Le modèle 3d construit.
   * @throws SLoaderException Si le fichier n'a pas pu être chargé correctement.
   */
  public SModel loadModel(String file_name)throws SLoaderException
  {
    String extension = SStringUtil.extensionFileLowerCase(file_name);  //obtenir l'extension du fichier
    
    //Construire le modèle 3d
    if(!model_loader_chooser.containsKey(extension))
      throw new SLoaderException("Erreur SModelLoader 001 : Le fichier '" + file_name +"' de format '" + extension + "' n'est pas un format de modèle pouvant être interprété par ce programme.");
    else
    {
      SStringLoader loader = model_loader_chooser.get(extension);
      return (SModel)loader.load(file_name);
    }
  }
  
}//fin SModelLoader
