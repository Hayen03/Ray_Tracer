/**
 * 
 */
package sim.loader.texture;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.awt.Color;

import sim.graphics.STexture;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.loader.texture.DDSImage.ImageInfo;
import sim.util.SFileSearch;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe STextureDDSLoader représente un lecteur de texture de format 'dds'.
 * 
 * À utiliser : http://worldwind31.arc.nasa.gov/svn/trunk/WorldWind/src/gov/nasa/worldwind/formats/dds/DDSCompressor.java
 * 
 * À utiliser : http://www.java-gaming.org/index.php?topic=24633.0
 * 
 * @author Simon Vézina
 * @since 2015-09-27
 * @version 2015-10-17
 */


/*
 * 
 * Référence : http://forum.worldwindcentral.com/showthread.php?18530-DDS-gt-BufferedImage
 * 
 */
public class STextureDDSLoader implements SStringLoader {

  public static final String FILE_EXTENSION = "dds";  //extension des fichiers lue par ce loader
  
  /**
   * Constructeur d'un lecteur de texture de format 'dds'. 
   */
  public STextureDDSLoader()
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
      throw new SLoaderException("Erreur STextureDDSLoader 001 : Le fichier '" + string + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SLoaderException("Erreur STextureDDSLoader 002 : Le fichier '" + string + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    
    
    //VERSION 
    //Simplified clone of DxTex tool from the DirectX SDK, written in
    //Java using the DDSImage; tests fetching of texture data
    //Référence : DxTex.java - JogAmp.org
    
    //à utiliser au besoin : http://www.java-gaming.org/index.php?topic=18827.0
    //Voir ceci pour format avec compression : http://www.chriscohnen.de/tutorials/DDStutorial/dds_tutorial.htm
    
    /*
    try{
    
      DDSImage dds_image = DDSImage.read(search.getFileFoundList().get(0));
    
      // Get image data
      int mipMapLevel = 0;
      DDSImage.ImageInfo info = dds_image.getMipMap(mipMapLevel);
      int width = info.getWidth();
      int height = info.getHeight();
      ByteBuffer data = info.getData();
      
      // Build ImageIcon out of image data
      BufferedImage img = new BufferedImage(width, height,
                                            BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster dst = img.getRaster();

      int skipSize;
      if (dds_image.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8) {
        skipSize = 4;
      } else if (dds_image.getPixelFormat() == DDSImage.D3DFMT_R8G8B8) {
        skipSize = 3;
      } else {
        dds_image.close();
        
        if(info.isCompressed())
          throw new SLoaderException("Erreur STextureDDSLoader 006 : Le fichier '" + string + "' est dans un format '" + dds_image.getPixelFormat() + "' qui n'est pas supporté. En plus, il est considéré compressé.");
        else
          throw new SLoaderException("Erreur STextureDDSLoader 006 : Le fichier '" + string + "' est dans un format '" + dds_image.getPixelFormat() + "' qui n'est pas supporté. Il n'est pas compressé.");
      }

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          // NOTE: highly suspicious that A comes fourth in
          // A8R8G8B8...not really ARGB, but RGBA (like OpenGL)
          dst.setSample(x, y, 0, data.get(skipSize * (width * y + x) + 2) & 0xFF);
          dst.setSample(x, y, 1, data.get(skipSize * (width * y + x) + 1) & 0xFF);
          dst.setSample(x, y, 2, data.get(skipSize * (width * y + x) + 0) & 0xFF);
        }
      }
      
      
      return new STexture(search.getFileNameToSearch(), img);
      
    }catch(IOException e){
      throw new SLoaderException("Erreur STextureDDSLoader 006 : Le fichier '" + string + "' une erreur de type I/O est survenu lors de la lecture." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }  
    */
    
    
      //VERSION PERSONNELLE
    
   
    try{
      
      DDSImage dds_image = DDSImage.read(search.getFileFoundList().get(0));
      
      if(dds_image.getNumMipMaps() > 0)
      {
        ImageInfo info = dds_image.getMipMap(0);
        ByteBuffer buffer = info.getData();
        int width = info.getWidth();
        int height = info.getHeight();
        
        if(buffer.capacity() != width*height)
          throw new SLoaderException("Erreur STextureDDSLoader 003 : La texture " + string + " est en erreur d'interprétation car --> width = " + width + ", height = " + height + " et produit = " + width*height + " != buffer capacity = " + buffer.capacity() + ".");
            
        //BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                
        int x = 0;
        int y = 0;
        
        int error_color = 0;
        
        for(int i = 0; i < buffer.capacity(); i++ )
        {
          java.awt.Color c;
          
          int color_value = (int)buffer.get(i);
          color_value += 128;                     //puisque le byte est entre -128 et 128, je vais translater le tout entre 0 et 255
           
          //CETTE APPROCHE SEMBLE OUBLIER TOUT LES AUTRES CANAUX DE LA COULEUR
          //1 byte, c'est bon pour un canal, mais j'en ai besoin de 4 !
          
          try{
            c = new java.awt.Color(color_value);               
          }catch(IllegalArgumentException e){
            c = new java.awt.Color(255, 0, 255);
            error_color++;
          }
          
          //Changement de ligne au besoin
          if(x >= width)
          {
            x = 0;
            y++;
          }
          
          //Si l'on a fait trop de changement de ligne
          if(y >= height)
            throw new SLoaderException("Erreur STextureDDSLoader 004 : Le fichier '" + string + "' contient plus d'informatique de que lignes disponibles.");
          
          result.setRGB(x, y, c.getRGB());
                   
          //Passer à la case suivante
          x++;
          
        }
           
        if(error_color > 0)
          SLog.logWriteLine("Message STextureDDSLoader : La texture " + string + " contient " + error_color + " pixels de couleur en erreur.");
        
        SLog.logWriteLine("Message STextureDDSLoader : La texture " + string + " est présentement chargée, mais avec plusieurs erreurs puisque l'algorithme est mal interprété.");
        
        return new STexture(search.getFileNameToSearch(), result, STexture.ORIGIN_UV_TOP_LEFT);
        
      }
      else
        throw new SLoaderException("Erreur STextureDDSLoader 005 : Le fichier '" + string + "' ne contient pas de MipMap ce qui correspond à aucune texture.");
      
   
    }catch(IOException e){
      throw new SLoaderException("Erreur STextureDDSLoader 006 : Le fichier '" + string + "' une erreur de type I/O est survenu lors de la lecture." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }
   
      
    
    
    
    /*      
      //POUR FAIRE FONCTIONNER AVEC LE CODE INTERNE
      try{
      File f = new File(search.getFileFoundList().get(0));
      byte[] bytes  = new byte[(int)f.length()];
      ByteBuffer buf = ByteBuffer.wrap(bytes);
      
      BufferedImage image = readDxt3(buf);
      
      return new STexture(search.getFileNameToSearch(), image);
      
      }catch(IllegalArgumentException e){
        throw new SLoaderException("Erreur STextureDDSLoader 003 : Le fichier '" + string + "' ne marche pas!" + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
      }
     */  
    
  }

  private static final int DDPF_FOURCC = 0x0004;

  private static final int DDSCAPS_TEXTURE = 0x1000;

  protected static class Color {

    private int r, g, b;

    public Color() {
      this.r = this.g = this.b = 0;
    }

    public Color(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }

    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      final Color color = (Color) o;

      if (b != color.b) {
        return false;
      }
      if (g != color.g) {
        return false;
      }
      //noinspection RedundantIfStatement
      if (r != color.r) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result;
      result = r;
      result = 29 * result + g;
      result = 29 * result + b;
      return result;
    }
  }

  protected static Dimension readHeaderDxt3(ByteBuffer buffer) {
    buffer.rewind();

    byte[] magic = new byte[4];
    buffer.get(magic);
    assert new String(magic).equals("DDS");

    int version = buffer.getInt();
    assert version == 124;

    int flags = buffer.getInt();
    int height = buffer.getInt();
    int width = buffer.getInt();
    int pixels = buffer.getInt(); // ???
    int depth = buffer.getInt();
    int mipmaps = buffer.getInt();

    buffer.position(buffer.position() + 44); // 11 unused double-words

    int pixelFormatSize = buffer.getInt(); // ???
    int fourCC = buffer.getInt();
    assert fourCC == DDPF_FOURCC;

    byte[] format = new byte[4];
    buffer.get(format);
    assert new String(format).equals("DXT3");

    int bpp = buffer.getInt(); // bits per pixel for RGB (non-compressed) formats
    buffer.getInt(); // rgb bit masks for RGB formats
    buffer.getInt(); // rgb bit masks for RGB formats
    buffer.getInt(); // rgb bit masks for RGB formats
    buffer.getInt(); // alpha mask for RGB formats

    int unknown = buffer.getInt();
    assert unknown == DDSCAPS_TEXTURE;
    int ddsCaps = buffer.getInt(); // ???
    buffer.position(buffer.position() + 12);

    return new Dimension(width, height);
  }

  public static BufferedImage readDxt3(ByteBuffer buffer) {
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    Dimension dimension = readHeaderDxt3(buffer);

    return readDxt3Buffer(buffer, dimension.width, dimension.height);
  }

  public static BufferedImage readDxt3Buffer(ByteBuffer buffer, int width, int height) {
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    int[] pixels = new int[16];
    int[] alphas = new int[16];

    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

    int numTilesWide = width / 4;
    int numTilesHigh = height / 4;
    for (int i = 0; i < numTilesHigh; i++) {
      for (int j = 0; j < numTilesWide; j++) {
        // Read the alpha table.
        long alphaData = buffer.getLong();
        for (int k = alphas.length - 1; k >= 0; k--) {
          alphas[k] = (int) (alphaData >>> (k * 4)) & 0xF; // Alphas are just 4 bits per pixel
          alphas[k] <<= 4;
        }

        short minColor = buffer.getShort();
        short maxColor = buffer.getShort();
        Color[] lookupTable = expandLookupTable(minColor, maxColor);

        int colorData = buffer.getInt();

        for (int k = pixels.length - 1; k >= 0; k--) {
          int colorCode = (colorData >>> k * 2) & 0x03;
          pixels[k] = (alphas[k] << 24) | getPixel888(multiplyAlpha(lookupTable[colorCode], alphas[k]));
        }

        result.setRGB(j * 4, i * 4, 4, 4, pixels, 0, 4);
      }
    }
    return result;
  }

  private static Color multiplyAlpha(Color color, int alpha) {
    Color result = new Color();

    double alphaF = alpha / 256.0;

    result.r = (int) (color.r * alphaF);
    result.g = (int) (color.g * alphaF);
    result.b = (int) (color.b * alphaF);
    return result;
  }

  protected static Color getColor565(int pixel) {
    Color color = new Color();

    color.r = (int) (((long) pixel) & 0xf800) >>> 8;
    color.g = (int) (((long) pixel) & 0x07e0) >>> 3;
    color.b = (int) (((long) pixel) & 0x001f) << 3;

    return color;
  }

  private static Color[] expandLookupTable(short minColor, short maxColor) {
    Color[] result = new Color[]{getColor565(minColor), getColor565(maxColor), new Color(), new Color()};

    result[2].r = (2 * result[0].r + result[1].r + 1) / 3;
    result[2].g = (2 * result[0].g + result[1].g + 1) / 3;
    result[2].b = (2 * result[0].b + result[1].b + 1) / 3;

    result[3].r = (result[0].r + 2 * result[1].r + 1) / 3;
    result[3].g = (result[0].g + 2 * result[1].g + 1) / 3;
    result[3].b = (result[0].b + 2 * result[1].b + 1) / 3;

    return result;
  }

  protected static int getPixel888(Color color) {
    int r = color.r;
    int g = color.g;
    int b = color.b;
    return r << 16 | g << 8 | b;
  }

  

}//fin de la classe STextureDDSLoader



