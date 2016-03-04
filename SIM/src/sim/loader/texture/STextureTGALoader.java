/**
 * 
 */
package sim.loader.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import sim.graphics.STexture;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.util.SFileSearch;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe STextureTGALoader représente un lecteur de texture de format 'tga' ne supportant pas de compression.
 * Référence : http://stackoverflow.com/questions/1514035/java-tga-loader
 * 
 * Pour une version supportant des textures compressées, voir le lien suivant : (pas en BufferedImage)
 * http://www.java-tips.org/other-api-tips-100035/112-jogl/1706-loading-compressed-and-uncompressed-tgas-nehe-tutorial-jogl-port.html
 * 
 * Autre lien : (LE MEILLEUR !!!)
 *
 * Windows TGA file loader.
 * @author Abdul Bezrati
 * @author Pepijn Van Eeckhoudt
 * http://xith.org/archive/JavaCoolDude/showsrc/showsrc.php?src=../JWS/Xith3D/Quake3/source/org/xith3d/loaders/md3/md3util/TGALoader.java
 *
 * @author Simon Vézina
 * @since 2015-09-27
 * @version 2015-10-28
 */
public class STextureTGALoader implements SStringLoader {

  public static final String FILE_EXTENSION = "tga";  //extension des fichiers lue par ce loader
  
  private static int offset;
  
  /**
   * Constructeur d'un lecteur de texture de format 'tga'. 
   */
  public STextureTGALoader()
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
      throw new SLoaderException("Erreur STextureTGALoader 001 : Le fichier '" + string + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SLoaderException("Erreur STextureTGALoader 002 : Le fichier '" + string + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
       
    //Méthode #1
    //-----------
    // Référence : http://stackoverflow.com/questions/1514035/java-tga-loader
    //-----------
    //La méthode #1 semble être la meilleur pour évaluer une texture TGA sans compression.
    //Le résultat obtenu par l'algorithme donne les couleurs dans le format accepté par BufferedImage,
    //mais l'origine semble être dans le coin inférieur gauche (au lieu du standare coin supérieur gauche).
    //La texture construite avec cette méthode devra réajuster l'origine uv de la texture.
    //Cependant, si la texture est compressée, l'algorithme tente sa chance et génère une exception (ArrayIndexOutOfBoundsException).
    
    try{
      
      File f = new File(search.getFileFoundList().get(0));
      byte[] buf = new byte[(int)f.length()];
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
      bis.read(buf);
      bis.close();
      BufferedImage image = decode(buf);
      
      return new STexture(search.getFileNameToSearch(), image, STexture.ORIGIN_UV_TOP_LEFT);
      
    }catch(IOException e){
      throw new SLoaderException("Erreur STextureTGALoader 003 : Une erreur de type I/O est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
    }catch(ArrayIndexOutOfBoundsException e){
      // tester les autres algorithes ...
    }
    
    
    //Méthode #2
    //----------
    // Référence : http://www.java-tips.org/other-api-tips-100035/112-jogl/1706-loading-compressed-and-uncompressed-tgas-nehe-tutorial-jogl-port.html
    //----------
    //La méthode #2 semble pas fonctionné. Elle a été mise de côté.
    
    /*
      try{
      Texture t = new Texture();
      TGALoader.loadTGA(t, string);
      
      return TGALoader.textureToBufferedImage(t);
      
      }catch(IOException e2){
        // tester les autres algorithmes ...
      }
    */
    
    //Méthode #3
    //----------
    // @author Abdul Bezrati
    // @author Pepijn Van Eeckhoudt
    // Référence : http://xith.org/archive/JavaCoolDude/showsrc/showsrc.php?src=../JWS/Xith3D/Quake3/source/org/xith3d/loaders/md3/md3util/TGALoader.java
    //----------
    //Cette méthode marche, mais seulement pour les textures avec compression (étrange). 
    //Les textures sans compression sont 'trop bleu' ce qui prouve qu'un canal de couleur est mal interprété (peut-être en raison du canal alpha).
    //Puisque je n'ai pas poussé mes recherches plus loin, je me résigne à l'utiliser seulement si la méthode #1 ne fonction pas
    //ce qui semble donner des bons résultats jusqu'à présent (2015-10-28).
    //Encore une fois, le résultat est bon, mais l'origine uv de la texture semble respecter la convension coin inférieur gauche.
    //Je dois ainsi en informer le constructeur de la STexture afin qu'elle donne les bonnes couleurs.
    
    try{
    
      BufferedImage image = loadTGA(search.getFileFoundList().get(0));
      
      return new STexture(search.getFileNameToSearch(), image, STexture.ORIGIN_UV_TOP_LEFT);
      
    }catch(IOException ioe){
        // tester les autres algorithmes ...
    }
      
    // finalement ... rien ne passe !
    throw new SLoaderException("Erreur STextureTGALoader 004 : Une erreur de lecture est survenue. Ce fichier ne peut pas être interprété par cet algorithme ... ce qui est un gros problème !");   
      
   }
    

  /**
   * ...
   * 
   * @param b
   * @return
   */
  private int btoi(byte b)
  {
    int a = b;
    return (a<0?256+a:a); 
  }

  /**
   * ...
   * 
   * @param buf
   * @return
   */
  private int read(byte[] buf)
  {
    return btoi(buf[offset++]);
  }

  /**
   * ...
   * 
   * @param buf
   * @return
   * @throws IOException
   */
  public BufferedImage decode(byte[] buf) throws IOException 
  {
    offset = 0;

    // Reading header bytes
    // buf[2]=image type code 0x02=uncompressed BGR or BGRA
    // buf[12]+[13]=width
    // buf[14]+[15]=height
    // buf[16]=image pixel size 0x20=32bit, 0x18=24bit 
    // buf{17]=Image Descriptor Byte=0x28 (00101000)=32bit/origin upperleft/non-interleaved
    for (int i=0;i<12;i++)
            read(buf);
    int width = read(buf)+(read(buf)<<8);   // 00,04=1024
    int height = read(buf)+(read(buf)<<8);  // 40,02=576
    read(buf);
    read(buf);

    int n = width*height;
    int[] pixels = new int[n];
    int idx=0;

    if (buf[2]==0x02 && buf[16]==0x20) { // uncompressed BGRA
        while(n>0) {
            int b = read(buf);
            int g = read(buf);
            int r = read(buf);
            int a = read(buf);
            int v = (a<<24) | (r<<16) | (g<<8) | b;
            pixels[idx++] = v;
            n-=1;
        }
    } else if (buf[2]==0x02 && buf[16]==0x18) {  // uncompressed BGR
        while(n>0) {
            int b = read(buf);
            int g = read(buf);
            int r = read(buf);
            int a = 255; // opaque pixel
            int v = (a<<24) | (r<<16) | (g<<8) | b;
            pixels[idx++] = v;
            n-=1;
        }
    } else {
        // RLE compressed
        while (n>0) {
            int nb = read(buf); // num of pixels
            if ((nb&0x80)==0) { // 0x80=dec 128, bits 10000000
                for (int i=0;i<=nb;i++) {
                    int b = read(buf);
                    int g = read(buf);
                    int r = read(buf);
                    pixels[idx++] = 0xff000000 | (r<<16) | (g<<8) | b;
                }
            } else {
                nb &= 0x7f;
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                int v = 0xff000000 | (r<<16) | (g<<8) | b;
                for (int i=0;i<=nb;i++)
                    pixels[idx++] = v;
            }
            n-=nb+1;
        }
    }

    BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    bimg.setRGB(0, 0, width,height, pixels, 0,width);
    return bimg;
  }

  private static final byte[] uTGAcompare = new byte[]{0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Uncompressed TGA Header

  private static final byte[] cTGAcompare = new byte[]{0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Compressed TGA Header


  public static BufferedImage loadTGA(String filename) throws IOException{        // Load a TGA file

   ClassLoader  fileLoader  = TGALoader.class.getClassLoader();
   InputStream  in          = fileLoader.getResourceAsStream( filename);
    byte[]      header = new byte[12];
 
    if(in == null)
      in = new FileInputStream(filename);

     readBuffer(in, header);

     if(Arrays.equals(uTGAcompare, header)){// See if header matches the predefined header of

       return loadUncompressedTGA(in);          // If so, jump to Uncompressed TGA loading code

     }
     else if (Arrays.equals(cTGAcompare, header)){    // See if header matches the predefined header of

       return loadCompressedTGA( in);         // If so, jump to Compressed TGA loading code

     }
     else{                        // If header matches neither type

       in.close();
       throw new IOException("TGA file be type 2 or type 10 "); // Display an error

     }
  }

  private static void readBuffer(InputStream in, byte[] buffer) throws IOException {
    int bytesRead = 0;
    int bytesToRead = buffer.length;
    while (bytesToRead > 0) {
      int read = in.read(buffer, bytesRead, bytesToRead);
      bytesRead  += read;
      bytesToRead -= read;
    }
  }

  private static BufferedImage loadUncompressedTGA(InputStream in) throws IOException{ // Load an uncompressed TGA (note, much of this code is based on NeHe's

                                                                                       // TGA Loading code nehe.gamedev.net)

    byte[] header = new byte[6];
    readBuffer(in, header);

    int  imageHeight = (unsignedByteToInt(header[3]) << 8) + unsignedByteToInt(header[2]), // Determine The TGA height  (highbyte*256+lowbyte)

         imageWidth  = (unsignedByteToInt(header[1]) << 8) + unsignedByteToInt(header[0]), // Determine The TGA width (highbyte*256+lowbyte)

         bpp         = unsignedByteToInt(header[4]);                                       // Determine the bits per pixel


    if ((imageWidth  <= 0) ||
 (imageHeight <= 0) ||
 ((bpp != 24) && (bpp!= 32))){ // Make sure all information is valid

      throw new IOException("Invalid texture information"); // Display Error

    }
    int  bytesPerPixel = (bpp / 8),                                   // Compute the number of BYTES per pixel

         imageSize     = (bytesPerPixel * imageWidth * imageHeight);  // Compute the total amout ofmemory needed to store data

    byte imageData[]   = new byte[imageSize];                         // Allocate that much memory


    readBuffer(in, imageData);

    BufferedImage  bufferedImage  = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

    for(int j = 0; j < imageHeight; j++)
      for(int i = 0; i < imageWidth; i++) {
        int  index = ( (imageHeight - 1 -j)* imageWidth + i) * bytesPerPixel,
             value = (    255              & 0xFF) << 24|
 (imageData[index + 0] & 0xFF) << 16|
 (imageData[index + 1] & 0xFF) <<  8|
 (imageData[index + 2] & 0xFF) ;
        bufferedImage.setRGB(i, j,value);
      }
    return bufferedImage;
  }

  private static BufferedImage loadCompressedTGA(InputStream fTGA) throws IOException   // Load COMPRESSED TGAs

  {

    byte[] header = new byte[6];
    readBuffer(fTGA, header);

    int  imageHeight = (unsignedByteToInt(header[3]) << 8) + unsignedByteToInt(header[2]), // Determine The TGA height  (highbyte*256+lowbyte)

         imageWidth  = (unsignedByteToInt(header[1]) << 8) + unsignedByteToInt(header[0]), // Determine The TGA width (highbyte*256+lowbyte)

         bpp         =  unsignedByteToInt(header[4]);                                       // Determine the bits per pixel


    if ((imageWidth  <= 0) ||
 (imageHeight <= 0) ||
 ((bpp != 24) && (bpp!= 32))){ // Make sure all information is valid

      throw new IOException("Invalid texture information"); // Display Error

    }
    int  bytesPerPixel = (bpp / 8),                                   // Compute the number of BYTES per pixel

         imageSize     = (bytesPerPixel * imageWidth * imageHeight);  // Compute the total amout ofmemory needed to store data

    byte imageData[]   = new byte[imageSize];                         // Allocate that much memory


    int pixelcount     = imageHeight * imageWidth,                    // Nuber of pixels in the image

        currentbyte    = 0,                                           // Current byte

        currentpixel   = 0;                                           // Current pixel being read


    byte[] colorbuffer = new byte[bytesPerPixel];                     // Storage for 1 pixel


    do {
      int chunkheader = 0;                      // Storage for "chunk" header
      try{
        chunkheader = unsignedByteToInt((byte) fTGA.read());
      }
      catch (IOException e) {
        throw new IOException("Could not read RLE header"); // Display Error

      }

      if(chunkheader < 128){                                            // If the ehader is < 128, it means the that is the number of RAW color packets minus 1

        chunkheader++;                                                  // add 1 to get number of following color values

        for(short counter = 0; counter < chunkheader; counter++){       // Read RAW color values

          readBuffer(fTGA, colorbuffer);
          // write to memory

          imageData[currentbyte   ]  = colorbuffer[2];                  // Flip R and B vcolor values around in the process

          imageData[currentbyte + 1] = colorbuffer[1];
          imageData[currentbyte + 2] = colorbuffer[0];

          if(bytesPerPixel == 4)                                        // if its a 32 bpp image

             imageData[currentbyte + 3] = colorbuffer[3];               // copy the 4th byte


          currentbyte += bytesPerPixel;                                 // Increase thecurrent byte by the number of bytes per pixel

          currentpixel++;                                               // Increase current pixel by 1


          if(currentpixel > pixelcount){                                // Make sure we havent read too many pixels

            throw new IOException("Too many pixels read");              // if there is too many... Display an error!

          }
        }
      }
      else{                                                             // chunkheader > 128 RLE data, next color reapeated chunkheader - 127 times

        chunkheader -= 127;                                             // Subteact 127 to get rid of the ID bit

        readBuffer(fTGA, colorbuffer);
        for(short counter = 0; counter < chunkheader; counter++){       // copy the color into the image data as many times as dictated

          imageData[currentbyte    ] = colorbuffer[2];                  // switch R and B bytes areound while copying

          imageData[currentbyte + 1] = colorbuffer[1];
          imageData[currentbyte + 2] = colorbuffer[0];

          if(bytesPerPixel == 4)                                        // If TGA images is 32 bpp

            imageData[currentbyte + 3] = colorbuffer[3];                // Copy 4th byte

            currentbyte +=  bytesPerPixel;                              // Increase current byte by the number of bytes per pixel

            currentpixel++;                                             // Increase pixel count by 1

          if(currentpixel > pixelcount){                                // Make sure we havent written too many pixels

            throw new IOException("Too many pixels read");              // if there is too many... Display an error!

          }
        }
      }
    } while (currentpixel < pixelcount);                                // Loop while there are still pixels left


    BufferedImage  bufferedImage  = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

    for(int j = 0; j < imageHeight; j++)
      for(int i = 0; i < imageWidth; i++) {
        int  index = ( (imageHeight - 1 -j)* imageWidth + i) * bytesPerPixel,
             value = (      255            & 0xFF) << 24|
 (imageData[index + 0] & 0xFF) << 16|
 (imageData[index + 1] & 0xFF) <<  8|
 (imageData[index + 2] & 0xFF) ;
        bufferedImage.setRGB(i, j,value);
      }
    return bufferedImage;
  }

  private static int unsignedByteToInt(byte b) {
    return (int) b & 0xFF;
  }

  
  
  
  
  
}//fin de la classe STextureTGALoader

class TGA {
  // First 6 Useful Bytes From The Header
  //ByteBuffer header = BufferUtil.newByteBuffer(6);                
  ByteBuffer header = ByteBuffer.allocate(6);
  // Holds Number Of Bytes Per Pixel Used In The TGA File
  int bytesPerPixel;              
  // Used To Store The Image Size When Setting Aside Ram
  int imageSize;                
  int temp;    // Temporary Variable
  int type;           
  int height;    // height of Image
  int width;    // width ofImage
  int bpp;    // Bits Per Pixel
}

class TGAHeader {
  byte[] Header = new byte[12];  // TGA File Header
}

class TGALoader {
  private static final ByteBuffer uTGAcompare;  // Uncompressed TGA Header
  private static final ByteBuffer cTGAcompare;  // Compressed TGA Header

  private static final int BPP_24_BYTES = 24;
  private static final int BPP_32_BYTES = 32;
  
  static {
      byte[] uncompressedTgaHeader = new byte[]{0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      byte[] compressedTgaHeader = new byte[]{0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0};

      //uTGAcompare = BufferUtil.newByteBuffer(uncompressedTgaHeader.length);
      uTGAcompare = ByteBuffer.allocate(uncompressedTgaHeader.length);
      uTGAcompare.put(uncompressedTgaHeader);  // Uncompressed TGA Header
      uTGAcompare.flip();

      //cTGAcompare = BufferUtil.newByteBuffer(compressedTgaHeader.length);
      cTGAcompare = ByteBuffer.allocate(compressedTgaHeader.length);
      cTGAcompare.put(compressedTgaHeader);  // Compressed TGA Header
      cTGAcompare.flip();
  }

  // Load a TGA file
  public static void loadTGA(Texture texture, String filename) throws IOException, SLoaderException         
  {
      //ByteBuffer header = BufferUtil.newByteBuffer(12);
      ByteBuffer header = ByteBuffer.allocate(12);
      
      //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
      SFileSearch search = new SFileSearch("", filename);
      
      if(!search.isFileFound())
        throw new SLoaderException("Erreur STextureTGALoader 005 : Le fichier '" + filename + "' n'est pas trouvé.");
      
      if(search.isManyFileFound())
        throw new SLoaderException("Erreur STextureTGALoader 006 : Le fichier '" + filename + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
         
        File f = new File(search.getFileFoundList().get(0));
        
        InputStream in_stream = new FileInputStream(f);
        //ReadableByteChannel in = Channels.newChannel(ResourceRetriever.getResourceAsStream(filename));
        ReadableByteChannel in = Channels.newChannel(in_stream);
        readBuffer(in, header);

      // See if header matches the predefined header of
      if (uTGAcompare.equals(header))  
      {   // an Uncompressed TGA image
          // If so, jump to Uncompressed TGA loading code
          loadUncompressedTGA(texture, in);  
      } else if (cTGAcompare.equals(header))    
      {            
          // See if header matches the predefined header of
          // an RLE compressed TGA image
          // If so, jump to Compressed TGA loading code
          loadCompressedTGA(texture, in); 
      } else        // If header matches neither type
      {
          in.close();
          // Display an error
          throw new IOException("TGA file be type 2 or type 10 ");  

      }
      
      
  }

  public static BufferedImage textureToBufferedImage(Texture texture)
  {
    int height = texture.height;
    int width = texture.width;
    
    int nb_pixels = height*width;
    
    BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    //BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE); 
        
    byte[] bytes = texture.imageData.array();
    int[] pixels = new int[nb_pixels];
    
    if(texture.type == BPP_24_BYTES)
    {
      SLog.logWriteLine("Message STextureTGALoader - Lecture d'une texture de type 24 bytes.");
      
      for(int i = 0; i < nb_pixels; i++)
      {
        //Array de type RGB (on suppose que la convertion BGR est déjà effectuée par la librairie) 
        //int r = btoi(bytes[i]);
        //int g = btoi(bytes[i+1]);
        //int b = btoi(bytes[i+2]);
        int r = unsignedByteToInt(bytes[i]);
        int g = unsignedByteToInt(bytes[i+1]);
        int b = unsignedByteToInt(bytes[i+2]);
        int a = 255;                    // opaque pixel
        
        //Color c = new Color(r,g,b);
        //int v = c.getRGB();
        
        int v = (a<<24) | (r<<16) | (g<<8) | b; // pour ARGB
        
        pixels[i] = v;
      }
    }
    else
    {
      SLog.logWriteLine("Message STextureTGALoader - Lecture d'une texture de type 32 bytes.");
      
      for(int i = 0; i < nb_pixels; i++)
      {      
        //Array de type RGBA (on suppose la convertion BGRA est déjà effectuée par la librairie) 
        //int r = btoi(bytes[i]);
        //int g = btoi(bytes[i+1]);
        //int b = btoi(bytes[i+2]);
        //int a = btoi(bytes[i+3]);
        int r = unsignedByteToInt(bytes[i]);
        int g = unsignedByteToInt(bytes[i+1]);
        int b = unsignedByteToInt(bytes[i+2]);
        int a = unsignedByteToInt(bytes[i+3]);
        
        Color c = new Color(r,g,b);
        int v = c.getRGB();
        //int v = (a<<24) | (r<<16) | (g<<8) | b; // pour ARGB
        pixels[i] = v; 
      }
    }
    
    bimg.setRGB(0, 0, width, height, pixels, 0, width);
    
    return bimg;
  }
  
  /**
   * ...
   * 
   * @param b
   * @return
   */
  private static int btoi(byte b)
  {
    int a = b;
    return (a<0?256+a:a); 
  }
  
  private static void readBuffer(ReadableByteChannel in, ByteBuffer buffer) 
          throws IOException {
      while (buffer.hasRemaining()) {
          in.read(buffer);
      }
      buffer.flip();
  }

  private static void loadUncompressedTGA(Texture texture, ReadableByteChannel in) 
          throws IOException  
          // Load an uncompressed TGA (note, much of this code is based on NeHe's
  {  // TGA Loading code nehe.gamedev.net)
      TGA tga = new TGA();
      readBuffer(in, tga.header);

      // Determine The TGA width  (highbyte*256+lowbyte)
      texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + 
              unsignedByteToInt(tga.header.get(0));  
      // Determine The TGA height  (highbyte*256+lowbyte)
      texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + 
              unsignedByteToInt(tga.header.get(2));          
      // Determine the bits per pixel
      texture.bpp = unsignedByteToInt(tga.header.get(4));
      tga.width = texture.width;  // Copy width into local structure
      tga.height = texture.height;  // Copy height into local structure
      tga.bpp = texture.bpp;    // Copy BPP into local structure

      if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && 
              (texture.bpp != 32)))  // Make sure all information is valid
      {
          throw new IOException("Invalid texture information"); // Display Error
      }

      if (texture.bpp == 24)    // If the BPP of the image is 24...
          //texture.type = GL.GL_RGB;  // Set Image type to GL_RGB
          texture.type = BPP_24_BYTES;
      else        // Else if its 32 BPP
          //texture.type = GL.GL_RGBA;  // Set image type to GL_RGBA
        texture.type = BPP_32_BYTES;
      
      tga.bytesPerPixel = (tga.bpp / 8); // Compute the number of BYTES per pixel
       
      // Compute the total amout ofmemory needed to store data
      tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);    
      // Allocate that much memory
      //texture.imageData = BufferUtil.newByteBuffer(tga.imageSize);  
      texture.imageData = ByteBuffer.allocate(tga.imageSize);
      readBuffer(in, texture.imageData);

      for (int cswap = 0; cswap < tga.imageSize; cswap += tga.bytesPerPixel) {
          byte temp = texture.imageData.get(cswap);
          texture.imageData.put(cswap, texture.imageData.get(cswap + 2));
          texture.imageData.put(cswap + 2, temp);
      }
  }

  private static void loadCompressedTGA(Texture texture, ReadableByteChannel fTGA) 
          throws IOException    // Load COMPRESSED TGAs
  {
      TGA tga = new TGA();
      readBuffer(fTGA, tga.header);

      // Determine The TGA width  (highbyte*256+lowbyte)
      texture.width = (unsignedByteToInt(tga.header.get(1)) << 8) + 
              unsignedByteToInt(tga.header.get(0));          
      // Determine The TGA height  (highbyte*256+lowbyte)        
      texture.height = (unsignedByteToInt(tga.header.get(3)) << 8) + 
              unsignedByteToInt(tga.header.get(2));          
      texture.bpp = unsignedByteToInt(tga.header.get(4)); // Determine Bits Per Pixel
      tga.width = texture.width;  // Copy width to local structure
      tga.height = texture.height;  // Copy width to local structure
      tga.bpp = texture.bpp;    // Copy width to local structure

      if ((texture.width <= 0) || (texture.height <= 0) || ((texture.bpp != 24) && 
              (texture.bpp != 32)))  //Make sure all texture info is ok
      {
          // If it isnt...Display error
          throw new IOException("Invalid texture information");  
      }

      if (texture.bpp == 24)    // If the BPP of the image is 24...
          //texture.type = GL.GL_RGB;  // Set Image type to GL_RGB
          texture.type = BPP_24_BYTES;
      else        // Else if its 32 BPP
          //texture.type = GL.GL_RGBA;  // Set image type to GL_RGBA
          texture.type = BPP_32_BYTES;
      tga.bytesPerPixel = (tga.bpp / 8); // Compute BYTES per pixel
      // Compute amout of memory needed to store image
      tga.imageSize = (tga.bytesPerPixel * tga.width * tga.height);    
      // Allocate that much memory
      //texture.imageData = BufferUtil.newByteBuffer(tga.imageSize); 
      texture.imageData = ByteBuffer.allocate(tga.imageSize);
      texture.imageData.position(0);
      texture.imageData.limit(texture.imageData.capacity());

      int pixelcount = tga.height * tga.width; // Nuber of pixels in the image
      int currentpixel = 0;  // Current pixel being read
      int currentbyte = 0;  // Current byte
      // Storage for 1 pixel
      //ByteBuffer colorbuffer = BufferUtil.newByteBuffer(tga.bytesPerPixel);      
      ByteBuffer colorbuffer = ByteBuffer.allocate(tga.bytesPerPixel);
      do {
          int chunkheader;  // Storage for "chunk" header
          try {
              ByteBuffer chunkHeaderBuffer = ByteBuffer.allocate(1);
              fTGA.read(chunkHeaderBuffer);
              chunkHeaderBuffer.flip();
              chunkheader = unsignedByteToInt(chunkHeaderBuffer.get());
          } catch (IOException e) {
              throw new IOException("Could not read RLE header");  // Display Error
          }

          // If the ehader is < 128, it means the that is the number of 
          // RAW color packets minus 1
          if (chunkheader < 128)    
          {      // that follow the header
              chunkheader++;  // add 1 to get number of following color values
              // Read RAW color values
              for (short counter = 0; counter < chunkheader; counter++)    
              {
                  readBuffer(fTGA, colorbuffer);
                  // write to memory
                  // Flip R and B vcolor values around in the process
                  texture.imageData.put(currentbyte, colorbuffer.get(2));  
                  texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
                  texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

                  if (tga.bytesPerPixel == 4)  // if its a 32 bpp image
                  {
                      // copy the 4th byte
                      texture.imageData.put(currentbyte + 3, colorbuffer.get(3));  
                  }

                  // Increase thecurrent byte by the number of bytes per pixel
                  currentbyte += tga.bytesPerPixel;  
                  currentpixel++;    // Increase current pixel by 1

                  // Make sure we havent read too many pixels
                  if (currentpixel > pixelcount) 
                  {
                      // if there is too many... Display an error!
                      throw new IOException("Too many pixels read");      
                  }
              }
          } else
          {
              // chunkheader > 128 RLE data, next color reapeated chunkheader - 127 times
              chunkheader -= 127;  // Subteact 127 to get rid of the ID bit
              readBuffer(fTGA, colorbuffer);

              // copy the color into the image data as many times as dictated
              for (short counter = 0; counter < chunkheader; counter++)          
              {  // by the header
                  texture.imageData.put(currentbyte, colorbuffer.get(2));  
                  texture.imageData.put(currentbyte + 1, colorbuffer.get(1));
                  texture.imageData.put(currentbyte + 2, colorbuffer.get(0));

                  if (tga.bytesPerPixel == 4) // if its a 32 bpp image
                  {
                      // copy the 4th byte
                      texture.imageData.put(currentbyte + 3, colorbuffer.get(3));  
                  }

                  // Increase current byte by the number of bytes per pixel
                  currentbyte += tga.bytesPerPixel;  
                  currentpixel++;  // Increase pixel count by 1

                  // Make sure we havent written too many pixels
                  if (currentpixel > pixelcount) 
                  {
                      // if there is too many... Display an error!
                      throw new IOException("Too many pixels read");      
                  }
              }
          }
      } while (currentpixel < pixelcount); // Loop while there are still pixels left
  }

  private static int unsignedByteToInt(byte b) {
      return (int) b & 0xFF;
  }
}

class Texture {
  ByteBuffer imageData;  // Image Data (Up To 32 Bits)
  int bpp;      // Image Color Depth In Bits Per Pixel
  int width;      // Image width
  int height;      // Image height
  int[] texID = new int[1];  // Texture ID Used To Select A Texture
  int type;      // Image Type (GL_RGB, GL_RGBA)
}

