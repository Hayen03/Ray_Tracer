/**
 * 
 */
package sim.graphics;

import java.util.HashMap;
import java.util.Map;

import sim.exception.SRuntimeException;
import sim.loader.SLoaderException;
import sim.loader.texture.STextureLoader;
import sim.util.SLog;
import sim.util.SReader;
import sim.util.SStringUtil;

/**
 * La classe <b>STextureReader</b> représente un lecteur de texture.
 * 
 * @author Simon Vézina
 * @since 2015-11-07
 * @version 2016-01-08
 */
public class STextureReader implements SReader {

  private static final STexture DEFAULT_TEXTURE = null;    
  
  //Carte des textures déjà lues (pour ne pas refaire de chargement)
  //Clé de recherche : Nom du fichier
  //Information : construction de la texture à partir du nom du fichier
  private static final Map<String, STexture> texture_map = new HashMap<String, STexture>();
  
  private String file_name;                 //nom du fichier du modèle
  
  STexture texture;
  
  boolean is_read;
  
  /**
   * Constructeur d'un lecteur de texture.
   * 
   * @param file_name - Le nom du fichier de la texture.
   */
  public STextureReader(String file_name)
  {
    this.file_name = file_name;
    texture = DEFAULT_TEXTURE;
    is_read = false;
    
    initialize();
  }

  /**
   * Méthode pour obtenir le nom du fichier en lecture.
   * 
   * @return Le nom du fichier en lecture.
   */
  public String getFileName()
  {
    return file_name;
  }
  
  @Override
  public STexture getValue() throws SRuntimeException
  {
    if(is_read)
      return texture;
    else
      throw new SRuntimeException("Erreur STextureReader 001 : La texture '" + file_name + "' n'a pas été lu.");
  }
  
  @Override
  public boolean asRead()
  {
    return is_read;
  }
  
  /**
   * Méthode pour faire l'initialisation du lecteur de texture.
   */
  private void initialize()
  {
    try{
      
      //Vérifier si la texture a déjà été lue
      if(texture_map.containsKey(file_name))
        texture = texture_map.get(file_name);
      else
      {
        STextureLoader texture_loader = new STextureLoader();
      
        SLog.logWriteLine("Message STextureReader : Lecture de la texture '" + file_name + "'.");
        
        texture = texture_loader.loadTexture(file_name);  //lecture de la texture (exception lancée s'il y a eu erreur)
        
        texture_map.put(file_name, texture);            //mettre le modèle lu dans la carte des modèles
      }
      
      //Construire une nouvelle texture à partir du même tableau des couleurs,
      //car il est possible que cette texture nécessite une autre interprétations
      //des coordonnées uv lors de son usage dans un matériel
      texture = new STexture(texture);
      
      is_read = true;
      
    }catch(SLoaderException e){
      SLog.logWriteLine("Erreur STextureReader 002 : Le chargement de la texture '" + file_name + "' est impossible. " + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
  }
  
}//fin de la classe STextureReader
