/**
 * 
 */
package sim.graphics.material;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SColor;
import sim.graphics.STexture;
import sim.graphics.STextureReader;
import sim.math.SVectorUV;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * <p>La classe <b>STextureMaterial</b> représente un <b>matériel de Blinn</b> dont les coefficients de rélfexion ambiant, diffus et spéculaire
 * peuvent être déterminé par la présence d'une <b>texture de couleur</b>. Une texture distincte peut être utilisée pour les trois types de réflexion.</p>
 * <p>Lorsqu'une texture est utilisée, le coefficient de réflexion sera multipliée par une couleur de base blanche (1,1,1) ce qui correspond
 * à utiliser la couleur de la texture comme étant la couleur de la réflexion du matériel (pas usage de la couleur de base du matériel).</p>
 * 
 * @see SBlinnMaterial
 * @author Simon Vézina
 * @since 2015-10-19
 * @version 2015-12-08
 */
public class SBlinnTextureMaterial extends SBlinnMaterial implements STextureMaterial {
  
  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { 
    SKeyWordDecoder.KW_TEXTURE_KA, SKeyWordDecoder.KW_TEXTURE_KD, SKeyWordDecoder.KW_TEXTURE_KS
  };
  
  /**
   * La variable 'ambiente_texture' correspond à la texture de réflexion ambiante du matériel.
   */
  private STexture ambiente_texture;    
  
  /**
   * La variable 'diffuse_texture' correspond à la texture de réflexion diffuse du matériel.
   */
  private STexture diffuse_texture;    
  
  /**
   * La variable 'specular_texture' correspond à la texture de réflexion speculaire du matériel.
   */
  private STexture specular_texture;    
    
  /**
   * La variable <b>is_uv_format_selected</b> permet de définir si le code d'interprétation de la coordonnée uv de la texture a été défini.
   */
  private boolean is_uv_format_selected;
  
  //-----------------
  // CONSTRUCTEURS //
  //-----------------
  
  /**
   * Construction d'un matériel avec une couleur par défaut. 
   */
  public SBlinnTextureMaterial()
  {
    this(SBlinnMaterial.DEFAULT_COLOR);
  }

  /**
   * Constructeur d'un matériel avec une couleur.
   * 
   * @param color - La couleur du matériel.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   */
  public SBlinnTextureMaterial(SColor color) throws SConstructorException
  {
    super(color);
    
    ambiente_texture = null;
    diffuse_texture = null;
    specular_texture = null;
    
    is_uv_format_selected = false;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SBlinnTextureMaterial 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }

  /**
   * Constructeur d'un matériel à partir d'information lue dans un fichier de format txt.
   * 
   * @param br - Le BufferedReader cherchant l'information dans le fichier txt.
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SConstructorException Si une erreur est survenue lors de la construction.
   * @see SBufferedReader
   */
  public SBlinnTextureMaterial(SBufferedReader br) throws IOException, SConstructorException
  {
    this();   
        
    try{
      read(br);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SBlinnTextureMaterial 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
  }
  
  @Override
  public SColor ambientColor(SVectorUV uv)
  {
    if(ambiente_texture == null)
      return ambientColor();
    else
      return SBlinnMaterial.DEFAULT_COLOR.multiply(ambiente_texture.getSColor(uv));
  }
  
  @Override
  public SColor diffuseColor(SVectorUV uv)
  {
    if(diffuse_texture == null)
      return diffuseColor();
    else
      return SBlinnMaterial.DEFAULT_COLOR.multiply(diffuse_texture.getSColor(uv));
  }
  
  @Override
  public SColor specularColor(SVectorUV uv)
  {
    if(specular_texture == null)
      return specularColor();
    else
      return SBlinnMaterial.DEFAULT_COLOR.multiply(specular_texture.getSColor(uv));
  }
  
  @Override
  public boolean asTexture()
  {
    return true;
  }
  
  @Override
  public void setUVFormat(int uv_format) throws SRuntimeException
  {
    if(is_uv_format_selected)
      throw new SRuntimeException("Erreur STextureOpenGLMaterial 002 : Le format d'interprétation de coordonnée uv a déjà été défini pour cette texture.");
    
    if(ambiente_texture != null)
      ambiente_texture.setUVFormat(uv_format);
    
    if(diffuse_texture != null)
      diffuse_texture.setUVFormat(uv_format);
    
    if(specular_texture != null)
      specular_texture.setUVFormat(uv_format);
    
    is_uv_format_selected = true;
  }
  
  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException
  {
    //Faire la vérification du code pour la classe parent
    if(super.read(sbr, code, remaining_line))
      return true;
    else
      switch(code)
      {
        case SKeyWordDecoder.CODE_TEXTURE_KA :  String ambiente_texture_file_name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_TEXTURE_KA);
                                                
                                                // Lecture de la texture seulement si le nom du fichier n'est pas 'STexture.DEFAULT_FILE_NAME == none'
                                                if(!ambiente_texture_file_name.equals(STexture.DEFAULT_FILE_NAME))
                                                {
                                                  STextureReader texture_reader_KA = new STextureReader(ambiente_texture_file_name);
                                                  
                                                  if(texture_reader_KA.asRead())
                                                    ambiente_texture = texture_reader_KA.getValue();
                                                  else
                                                    throw new SReadingException("Erreur SBlinnTextureMaterial 003 - La texture de réflexion ambiante '" + ambiente_texture_file_name + "' n'a pas été chargée.");
                                                }   
                                                return true;
                                                
        case SKeyWordDecoder.CODE_TEXTURE_KD :  String diffuse_texture_file_name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_TEXTURE_KD);
                                                
                                                // Lecture de la texture seulement si le nom du fichier n'est pas 'STexture.DEFAULT_FILE_NAME == none'
                                                if(!diffuse_texture_file_name.equals(STexture.DEFAULT_FILE_NAME))
                                                {
                                                  STextureReader texture_reader_KD = new STextureReader(diffuse_texture_file_name);
                                                
                                                  if(texture_reader_KD.asRead())
                                                    diffuse_texture = texture_reader_KD.getValue();
                                                  else
                                                    throw new SReadingException("Erreur SBlinnTextureMaterial 004 - La texture de réflexion diffuse '" + diffuse_texture_file_name + "' n'a pas été chargée.");
                                                }
                                                
                                                return true;
                                                
         case SKeyWordDecoder.CODE_TEXTURE_KS : String specular_texture_file_name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_TEXTURE_KS);
                                                
                                                // Lecture de la texture seulement si le nom du fichier n'est pas 'STexture.DEFAULT_FILE_NAME == none'
                                                if(!specular_texture_file_name.equals(STexture.DEFAULT_FILE_NAME))
                                                {
                                                  STextureReader texture_reader_KS = new STextureReader(specular_texture_file_name);
                                                
                                                  if(texture_reader_KS.asRead())
                                                    specular_texture = texture_reader_KS.getValue();
                                                  else
                                                    throw new SReadingException("Erreur SBlinnTextureMaterial 005 - La texture de réflexion spéculaire '" + specular_texture_file_name + "' n'a pas été chargée.");
                                                }
                                                
                                                return true;
                                                
        default : return false;
      }
    
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException 
  {
    bw.write(SKeyWordDecoder.KW_TEXTURE_MATERIAL);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les propriétés de la classe SColorMaterial et ses paramètres hérités
    writeTextureMaterialParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write("\t#end material" + SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);  
  }
  
  /**
   * Méthode pour écrire les paramètres associés à la classe STextureMaterial et ses paramètres hérités.
   * 
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException Si une erreur I/O s'est produite.
   * @see IOException
   */
  protected void writeTextureMaterialParameter(BufferedWriter bw)throws IOException 
  {
    //Écrire les propriétés de la classe SBlinnMaterial héritées
    writeBlinnMaterialParameter(bw);
    
    //Texture ambiante
    bw.write(SKeyWordDecoder.KW_TEXTURE_KA);
    bw.write("\t");
    
    if(ambiente_texture != null)
      bw.write(ambiente_texture.getFileName());
    else
      bw.write(STexture.DEFAULT_FILE_NAME);
    
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Texture diffuse
    bw.write(SKeyWordDecoder.KW_TEXTURE_KD);
    bw.write("\t");
    
    if(diffuse_texture != null)
      bw.write(diffuse_texture.getFileName());
    else
      bw.write(STexture.DEFAULT_FILE_NAME);
    
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Texture spéculaire
    bw.write(SKeyWordDecoder.KW_TEXTURE_KS);
    bw.write("\t");
    
    if(specular_texture != null)
      bw.write(specular_texture.getFileName());
    else
      bw.write(STexture.DEFAULT_FILE_NAME);
    
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
  protected void readingInitialization() throws SInitializationException
  {
    super.readingInitialization();
    
    initialize();
  }
  
  @Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_TEXTURE_MATERIAL;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
  @Override
  public boolean isUVFormatSelected()
  {
    return is_uv_format_selected;
  }
  
}//fin de la classe STextureMaterial
