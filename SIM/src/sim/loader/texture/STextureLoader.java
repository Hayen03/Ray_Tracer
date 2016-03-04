/**
 * 
 */
package sim.loader.texture;

import java.util.Hashtable;

import sim.graphics.STexture;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * Classe qui représente un constructeur de texture à partir du nom du fichier. Le loader aura
 * pour fonction de choisir le bon type de loader selon le format du fichier à lire.
 * 
 * @author Simon Vézina
 * @since 2015-09-27
 * @version 2015-10-28
 */
public class STextureLoader {

  //Construire la table de correspondant entre le format du fichier en lecture et le bon lecteur 
  private static final Hashtable<String,SStringLoader> texture_loader_chooser = buildHashtable();
  
  /**
   *  Constructeur du loader de texture. 
   */
  public STextureLoader()
  {
    
  }

  /**
   * Méthode qui effectue la construction d'un BufferedImage à partir du nom d'un fichier.
   * 
   * @param file_name - Le nom du fichier en lecture.
   * @return Le BufferedImage.
   * @throws SLoaderException Si le fichier n'a pas pu être chargé correctement.
   */
  public STexture loadTexture(String file_name)throws SLoaderException
  {
    String extension = SStringUtil.extensionFileLowerCase(file_name);  //obtenir l'extension du fichier
    
    //Construire le modèle 3d
    if(!texture_loader_chooser.containsKey(extension))
      throw new SLoaderException("Erreur STextureLoader 001 : Le fichier '" + file_name +"' de format '" + extension + "' n'est pas un format de texture pouvant être interprété par ce programme.");
    else
    {
      SStringLoader loader = texture_loader_chooser.get(extension);
      STexture texture = (STexture) loader.load(file_name);
      
      SLog.logWriteLine("Message STextureLoader : La texture '" + texture.getFileName() + "' a été chargée.");
      
      return texture;
    }
  }
  
  /**
   * Méthode pour construire la table de correspondance entre le format du fichier en lecture et le bon lecteur de fichier.
   * @return La table de correspondance.
   */
  private static Hashtable<String,SStringLoader> buildHashtable()
  {
    Hashtable<String,SStringLoader> table = new Hashtable<String,SStringLoader>();
    
    //Insertion du loader de texture de format : TGA
    table.put(STextureTGALoader.FILE_EXTENSION, new STextureTGALoader());
    
    //Insertion du loader de texture de format : DDS
    table.put(STextureDDSLoader.FILE_EXTENSION, new STextureDDSLoader());
    
    //Insertion du loader de texture de format : GIF, PNG, JPG, JPEG
    for(int i = 0; i < STextureImageIOLoader.FILE_EXTENSIONS.length; i++)
      table.put(STextureImageIOLoader.FILE_EXTENSIONS[i], new STextureImageIOLoader());
    
    return table;
  }
  
}//fin STextureLoader
