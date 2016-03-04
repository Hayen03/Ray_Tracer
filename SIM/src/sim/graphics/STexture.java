/**
 * 
 */
package sim.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.math.SVectorUV;

/**
 * La classe <b>STexture</b> représente une texture de couleur. Cette classe permet de mettre en mémoire une image et accéder aux différents pixels 
 * de couleur de l'image en coordonnée texture (u,v) tel que u et v est compris entre 0.0 et 1.0.
 * 
 * Les formats d'image supportés sont : gif, png, jpeg (jpg), tga
 *  
 * @author Simon Vézina
 * @since 2015-09-13
 * @version 2016-01-08
 */
public class STexture {

  final public static String[] UV_FORMAT = { "default", "origin_uv_top_left", "origin_uv_bottom_left" };
  
  final public static int UV_DEFAULT = 0;
  
  final public static int ORIGIN_UV_TOP_LEFT = 1;
  
  final public static int ORIGIN_UV_BOTTOM_LEFT = 2;
    
  /**
   * La constante 'DEFAULT_FILE_NAME' correspond à un nom de fichier pour une texture par défaut ne pouvant être lu.
   * Une texture portant le nom de {@value} ne peut pas être chargée en mémoire. 
   */
  public static final String DEFAULT_FILE_NAME = "none";  //nom du fichier par défaut étant invalide

  private final String file_name;                         //nom du fichier de la texture
  
  private final BufferedImage image;                      //image en mémoire 
  private final int width;                                //largeur de la texture (en x ou u)
  private final int height;                               //hauteur de la texture (en y ou v)
  
  /**
   * La variable 'uv_coordinate_format' correspond a un code déterminant le format des coordonnée uv des textures. 
   * Le format précise entre autre la localisation de l'origine dans une texture ainsi que le sens des axes uv. 
   */
  private int uv_coordinate_format;
  
  /**
   * Constructeur d'une texture de couleur en prenant le <b>coin supérieur gauche</b> d'une image comme origine de la coordonnée uv de texture.
   * 
   * @param file_name - Le nom du fichier.
   * @param image - ...
   * @throws SConstructorException S'il y a eu une erreur lors de la lecture empêchant la construction de la texture.
   */
  public STexture(String file_name, BufferedImage image)throws SConstructorException
  {
    this(file_name, image, ORIGIN_UV_TOP_LEFT);
  }
  
  /**
   * Constructeur d'une texture de couleur.
   * 
   * @param file_name - Le nom du fichier.
   * @param uv_format - Le code de référence de l'interprétation des coordonnée uv de texture.
   * @throws SConstructorException Si le code de référence de l'interprétation des coordonnées uv de texture n'est pas reconnu par le système.
   * @throws SConstructorException Si à la construction, on utilise le format 'UV_DEFAULT', car il faut obligatoirement spécifier un format.
   */
  public STexture(String file_name, BufferedImage image, int uv_format)throws SConstructorException
  {
    //Initialisation du format d'interprétation des coordonnées de textures
    switch(uv_format)
    {
      case UV_DEFAULT : throw new SConstructorException("Erreur STexture 001 : Erreur de construction de la texture en raison d'un format par défaut. À la construction, il faut absolument spécifier un format.");
      
      case ORIGIN_UV_TOP_LEFT :
      case ORIGIN_UV_BOTTOM_LEFT : uv_coordinate_format = uv_format; break;
      
      default : throw new SConstructorException("Erreur STexture 002 : Erreur de construction de la texture en raison du format d'interprétation '" + uv_format + "' qui n'est pas reconnu par le système.");
    }
    
    this.file_name = file_name;
    this.image = image;
    width = image.getWidth();
    height = image.getHeight();
  }
  
  /**
   * Copie constructeur d'une texture. La copie de cette texture fera une copie de la référence au BufferedImage de la texture à copier. 
   * Ainsi, une modification au BufferedImage influencera également l'ensemble des BufferedImage des autres copies.
   * <p>L'intérêt de cette copie permettra de modifier personnellement le paramètre <i>uv_coordinate_format</i> par la méthode <i>setUVFormat(...)</i>
   * ce qui permettra d'interpréter individuellement les coordonnées des textures pour chaque usage différent d'une même texture.</p>
   * 
   * @param texture - La texture à copier.
   */
  public STexture(STexture texture)
  {
    file_name = texture.file_name;
    image = texture.image;
    width = texture.width;
    height = texture.height;  
    uv_coordinate_format = texture.uv_coordinate_format;
  }
  
  /**
   * Méthode pour obtenir le nom du fichier de la texture.
   * 
   * @return Le nom du fichier de la texture.
   */
  public String getFileName()
  {
    return file_name;
  }
  
  /**
   * Méthode pour obtenir la couleur d'une coordonnée uv de la texture.
   * 
   * @param uv - La coordonnée uv de la texture.
   * @return La couleur en coordonnée uv de la texture.
   * @throws SRuntimeException Si le code d'interprétation de l'origine de la coordonnée de texture uv est mal définie.
   */
  public SColor getSColor(SVectorUV uv) throws SRuntimeException
  {
    //S'assurer que la coordonnée uv pourra accéder une coordonnée dans la texture
    SVectorUV crop_uv = uv.getInBound(SVectorUV.REPEAT);
    
    //Obtenir la couleur dans la texture à partir de la coordonnée uv
    //Puisque l'origine (0,0) du BufferedImage est dans le coin supérieur gauche, nous allons utiliser cette convention pour aller cherche la bonne couleur. 
    //Cependant, il est possible de devoir interpréter l'origine différemment selon le choix pris lors de la création d'un modèle 3d.
    switch(uv_coordinate_format)
    {
      case ORIGIN_UV_TOP_LEFT : return getSColorUVFormated(crop_uv);
      
      case ORIGIN_UV_BOTTOM_LEFT : return getSColorUVFormated(new SVectorUV(crop_uv.getU(), 1.0 - crop_uv.getV()));
      
      default : throw new SRuntimeException("Erreur STexture 002 : L'origine de la coordonnée de texture de code '" + uv_coordinate_format + "' est mal définie.");
    }    
  }
  
  /**
   * Méthode pour modifier le format d'interprétation des coordonnées uv de la texture.
   * Si le format utilisé est STexture.UV_DEFAULT, il n'y aura aucun changement, car le format par défaut est celui désigné lors de la construction de la texture.
   * 
   * @param uv_format - Le code du format d'interprétation des coordonnées uv de la texture.
   * @throws SRuntimeException Si le code du format d'interprétation n'est pas reconnu par le système. 
   */
  public void setUVFormat(int uv_format) throws SRuntimeException
  {
    switch(uv_format)
    {
      case UV_DEFAULT :            break;   //rien changer, car le format par défaut est celui lors de l'initialisation.
      case ORIGIN_UV_TOP_LEFT : 
      case ORIGIN_UV_BOTTOM_LEFT : uv_coordinate_format = uv_format; 
                                   break;
      
      default : throw new SRuntimeException("Erreur STexture 003 : Le code d'interprétation de l'origine de la coordonnée de texture '" + uv_format +"' n'est pas reconnu par le système.");
    }
    
  }
  
  /**
   * Méthode pour obtenir la couleur à partir d'une coordonnée uv interprété dans le format BufferedImage
   * positionnnant la coordonnée de texel (0,0) dans le coin supérieur gauche de la texture.
   * 
   * @param uv - La coordonnée uv du texel.
   * @return La couleur du texel.
   */
  private SColor getSColorUVFormated(SVectorUV uv)
  {
    return new SColor(new Color(image.getRGB( (int)(uv.getU()*(width-1)), (int)(uv.getV()*(height-1)) )));  
  }
  

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    
    if (obj == null)
      return false;
    
    if (getClass() != obj.getClass())
      return false;
    
    STexture other = (STexture) obj;
    
    // Comparer les dimensions des images
    if (height != other.height)
      return false;
    
    if (width != other.width)
      return false;
       
    // Comparer les images uniquement si elles ne sont pas null.
    // Deux images égales à null seront considérées égales à ce point.
    if (image == null)
    {
      if (other.image != null)
        return false;
    }
    else
    {
      if(other.image == null)
        return false;
      else
      {
        // Itérer sur l'ensemble des pixels du BufferedImage
        for (int y = 0; y < height; y++)
          for (int x = 0; x < width; x++)
            // Puisque dans un buffered image, une couleur est convertie en code entier,
            // on peut utiliser l'opérateur == et != pour comparer deux couleurs.
            if (image.getRGB(x, y) != other.image.getRGB(x, y))
              return false; 
      }
    }
          
    return true;
  }
  
}//fin de la classe STexture
