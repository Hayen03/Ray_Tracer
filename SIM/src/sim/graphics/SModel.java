/**
 * 
 */
package sim.graphics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import sim.exception.SRuntimeException;
import sim.math.SVector3d;
import sim.util.SKeyWordDecoder;
import sim.util.SStringUtil;
import sim.util.SWriteable;

/**
 * Classe qui représente un model 3d comprenant des primitives.
 * @author Simon Vézina
 * @since 2015-03-16
 * @version 2015-10-31
 */
public class SModel implements SWriteable{

  public static final String DEFAULT_FILE_NAME = "none";  //nom du fichier par défaut étant invalide
  
  //Vecteur définissant les matrices de transformation par défaut étant aucune transformation
  public static final SVector3d DEFAULT_SCALE = new SVector3d(1.0, 1.0, 1.0);
  public static final SVector3d DEFAULT_ROTATION = new SVector3d(0.0, 0.0, 0.0);
  public static final SVector3d DEFAULT_TRANSLATION = new SVector3d(0.0, 0.0, 0.0);
  
  private final String file_name;                 //nom du fichier du modèle
  
  private final SVector3d scale;                  //vecteur d'homothétie
  private final SVector3d rotation;               //vecteur de rotaion
  private final SVector3d translation;            //vecteur de rotation
  
  /**
   * La variable 'uv_format' correspond au format d'interprétation des coordonnées uv de texture que l'on doit appliquer
   * au texture définie dans le modèle afin de bien faire correspondre les coordonnées uv avec les bons texel.
   */
  private final int uv_format;
  
  private final List<SPrimitive> primitive_list;  //liste des primitives appartenant au modèle
  
  /**
   * Constructeur d'un modèle 3d.
   * @param file_name - Le nom de du fichier.
   */
  public SModel(String file_name)
  {
    this(file_name, DEFAULT_SCALE, DEFAULT_ROTATION, DEFAULT_TRANSLATION, STexture.UV_DEFAULT);
  }

  /**
   * Constructeur d'un modèle 3d avec matrice de transformation.
   * @param file_name - Le nom de du fichier.
   * @param scale - Le vecteur d'homothétie (<i>scale</i>).
   * @param rotation - Le vecteur de rotation.
   * @param translation - Le vecteur de rotation.
   * @param uv_format - Le format d'interprétation des coordonnées uv de texture.
   * @throws SRuntimeException Si le format d'interprétation des coordonnées de texture uv n'est pas valide.
   */
  public SModel(String file_name, SVector3d scale, SVector3d rotation, SVector3d translation, int uv_format) throws SRuntimeException
  {
    this.file_name = file_name;
    
    this.scale = scale;
    this.rotation = rotation;
    this.translation = translation;
    
    switch(uv_format)
    {
      case STexture.UV_DEFAULT :
      case STexture.ORIGIN_UV_BOTTOM_LEFT :
      case STexture.ORIGIN_UV_TOP_LEFT :      this.uv_format = uv_format; break;
      
      default : throw new SRuntimeException("Erreur SModel 001 : Le format d'interprétation des coordonnées de texture '" + uv_format + "' n'est pas reconnu par le système.");
    }
    
    primitive_list = new ArrayList<SPrimitive>();
  }
  
  /**
   * Méthode pour obtenir le nom du fichier du modèle.
   * @return Le nom du fichier du modèle.
   */
  public String getFileName()
  {
    return file_name;
  }
  
  /**
   * Méthode pour obtenir la liste des primitives appartenant à ce modèle.
   * @return La liste des primitives du modèle.
   */
  public List<SPrimitive> getPrimitiveList()
  {
    return primitive_list;
  }
  
  /**
   * Méthode pour ajouter une primitive au modèle.
   * @param primitive - La primitive à ajouter au modèle.
   */
  public void addPrimitive(SPrimitive primitive)
  {
    primitive_list.add(primitive);
  }
  
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(SKeyWordDecoder.KW_MODEL);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    //Écrire les paramètres de la classe SPrimitive
    writeSModelParameter(bw);
    
    bw.write(SKeyWordDecoder.KW_END);
    bw.write("\t#end model" + SStringUtil.END_LINE_CARACTER);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }

  /**
   * Méthode pour écrire les paramètres associés à la classe SModel.
   * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
   * @throws IOException S'il y a une erreur d'écriture.
   * @see IOException
   */
  protected void writeSModelParameter(BufferedWriter bw)throws IOException 
  {
    bw.write(SKeyWordDecoder.KW_FILE);
    bw.write("\t\t");
    bw.write(file_name);
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_SCALE);
    bw.write("\t\t");
    scale.write(bw);;
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_ROTATION);
    bw.write("\t");
    rotation.write(bw);;
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_TRANSLATION);
    bw.write("\t");
    translation.write(bw);;
    bw.write(SStringUtil.END_LINE_CARACTER);
    
    bw.write(SKeyWordDecoder.KW_UV_FORMAT);
    bw.write("\t");
    bw.write(STexture.UV_FORMAT[uv_format]);
    bw.write(SStringUtil.END_LINE_CARACTER);
  }
  
}//fin classe SModel
