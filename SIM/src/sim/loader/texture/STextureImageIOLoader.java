/**
 * 
 */
package sim.loader.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sim.graphics.STexture;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.util.SFileSearch;
import sim.util.SStringUtil;

/**
 * La classe STextureImageIOLoader représente un lecteur de texture de format 'gif', 'png' et 'jpeg' ('jpg') dont l'implémentation
 * provient de la classe javax.imageio.ImageIO.
 * 
 * @author Simon Vézina
 * @since 2015-09-27
 * @version 2015-10-28
 */
public class STextureImageIOLoader implements SStringLoader {

  /**
   * La constante 'FILE_EXTENSIONS' correspond aux différents type de fichier image pouvant être interprété par ce chargeur d'image.
   * La liste est la suivante : gif, png, jpg (jpeg)
   */
  public static final String[] FILE_EXTENSIONS = {"gif", "png", "jpeg", "jpg"};  //extensions des fichiers lues par ce loader
  
  /**
   * Constructeur d'un lecteur de texture selon l'implémentation de la classe javax.imageio.ImageIO supportant les formats
   * 'gif', 'png' et 'jpeg' ('jpg'). 
   */
  public STextureImageIOLoader()
  {
    
  }

  /* (non-Javadoc)
   * @see sim.loader.SStringLoader#load(java.lang.String)
   */
  @Override
  public Object load(String string) throws SLoaderException
  {
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", string);
    
    if(!search.isFileFound())
      throw new SLoaderException("Erreur STextureImageIOLoader 001 : Le fichier '" + string + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SLoaderException("Erreur STextureImageIOLoader 002 : Le fichier '" + string + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    File file = new File(search.getFileFoundList().get(0)); //prendre la 1ier trouvé dans la liste
    
    try{
      
      BufferedImage result = ImageIO.read(file);  //chargement de la texture en mémoire 
      
      //Vérification si la texture est bien chargée
      if(result == null)
        throw new SLoaderException("Erreur STextureImageIOLoader 003 : Le fichier '" + string + "' de format '" + SStringUtil.extensionFileLowerCase(string) + "' n'a pas été adéquatement interprété par ce lecteur.");
      else
        return new STexture(search.getFileNameToSearch(), result, STexture.ORIGIN_UV_TOP_LEFT);
      
    }catch(IOException e){
      throw new SLoaderException("Erreur STextureImageIOLoader 004 : Le chargement de la texture '" + string + "' est impossible. " + SStringUtil.END_LINE_CARACTER + e.getMessage());
    }
  }

}//fin de la classe STextureImageIOLoader
