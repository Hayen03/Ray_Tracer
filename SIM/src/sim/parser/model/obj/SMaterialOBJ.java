/**
 * 
 */
package sim.parser.model.obj;

/**
 * Classe qui représente la description d'un matériel pour un objet 3d de format OBJ (Wavefront).
 * @author Simon Vézina
 * @since 2015-03-28
 * @version 2015-11-05
 */
public class SMaterialOBJ {

  //Paramètres par défaut accessibles publiquement
  public static final String DEFAULT_NAME = "default";
  public static final SVertex DEFAULT_AMBIENT_COEFFICIENT = new SVertex(0.2f, 0.2f, 0.2f);  
  public static final SVertex DEFAULT_DIFFUSE_COEFFICIENT = new SVertex(0.3f, 0.7f, 0.3f);
  public static final SVertex DEFAULT_SPECULAR_COEFFICIENT = new SVertex(0.2f, 0.2f, 0.2f);
  public static final float DEFAULT_SHININESS = 5.0f;
  
  private final String material_name; //nom du matériel
  
  private final SVertex Ka; //coefficient de réflexion ambiante
  private final SVertex Kd; //coefficient de réflexion diffuse
  private final SVertex Ks; //coefficient de réflexion spéculaire
  private final float Ns;   //constante de brillance (shininess)
  
  private final String texture_Ka;  //nom de la texture de couleur ambiante
  private final String texture_Kd;  //nom de la texture de couleur diffuse
  private final String texture_Ks;  //nom de la texture de couleur spéculaire
  
  /**
   *  Constructeur d'un matériel par défaut pour objet 3d de format OBJ.
   */
  public SMaterialOBJ()
  {
    this(DEFAULT_NAME, DEFAULT_AMBIENT_COEFFICIENT, DEFAULT_DIFFUSE_COEFFICIENT, DEFAULT_SPECULAR_COEFFICIENT, DEFAULT_SHININESS);
  }
  
  /**
   * Constructeur d'un matériel pour objet 3d de format OBJ.
   * 
   * @param material_name - Le nom du matériel.
   * @param Ka - Le coefficient de réflexion ambiante pour les composantes rgb.
   * @param Kd - Le coefficient de réflexion diffuse pour les composantes rgb.
   * @param Ks - Le coefficient de réflexion spéculaire pour les composantes rgb.
   * @param Ns - La constance de brillance (shininess).
   */
  public SMaterialOBJ(String material_name, SVertex Ka, SVertex Kd, SVertex Ks, float Ns)
  {
    this(material_name, Ka, Kd, Ks, Ns, null, null, null);
  }
  
  /**
   * Constructeur d'un matériel pour objet 3d de format OBJ avec texture.
   * 
   * @param material_name - Le nom du matériel.
   * @param Ka - Le coefficient de réflexion ambiante pour les composantes rgb.
   * @param Kd - Le coefficient de réflexion diffuse pour les composantes rgb.
   * @param Ks - Le coefficient de réflexion spéculaire pour les composantes rgb.
   * @param Ns - La constance de brillance (shininess).
   * @param texture_Ka - Le nom du fichier comprenant l'information de la texture pour la réflexion ambiante.
   * @param texture_Kd - Le nom du fichier comprenant l'information de la texture pour la réflexion diffuse.
   * @param texture_Ks - Le nom du fichier comprenant l'information de la texture pour la réflexion spéculaire.
   */
  public SMaterialOBJ(String material_name, SVertex Ka, SVertex Kd, SVertex Ks, float Ns, String texture_Ka, String texture_Kd, String texture_Ks)
  {
    this.material_name = material_name;
    
    this.Ka = Ka;
    this.Kd = Kd;
    this.Ks = Ks;
    this.Ns = Ns;
    
    this.texture_Ka = texture_Ka;
    this.texture_Kd = texture_Kd;
    this.texture_Ks = texture_Ks;
  }
  
  /**
   * Méthode pour obtenir le nom du matériel. Le nom par défaut sera "default" si le matériel
   * a été construit sans nom particulier.
   * @return Le nom du matériel.
   */
  public String getMaterialName()
  {
    return material_name;
  }
  
  /**
   * Méthode pour obtenir les coefficients de réflexion ambiante du matériel.
   * @return Le vertex des coefficients de réflexion ambiante.
   */
  public SVertex getKa()
  {
    return Ka;
  }
  
  /**
   * Méthode pour obtenir les coefficients de réflexion diffuse du matériel.
   * @return Le vertex des coefficients de réflexion diffuse.
   */
  public SVertex getKd()
  {
    return Kd;
  }
  
  /**
   * Méthode pour obtenir les coefficients de réflexion spéculaire du matériel.
   * @return Le vertex des coefficients de réflexion spéculaire.
   */
  public SVertex getKs()
  {
    return Ks;
  }
  
  /**
   * Méthode pour obtenir le coefficient de brillance (shininess) du matériel.
   * @return Le coefficient de brillance (shininess).
   */
  public float getNs()
  {
    return Ns;
  }
  
  /**
   * Méthode pour obtenir le nom du fichier de la texture pour la réflexion ambiante.
   * @return Le nom du fichier comprenant l'information de la texture pour la réflexion ambiante. 
   * Le nom du fichier sera <b>null</b> s'il n'y a pas de texture affectée.
   */
  public String getTextureKaFileName()
  {
    return texture_Ka;
  }
  
  /**
   * Méthode pour obtenir le nom du fichier de la texture pour la réflexion diffuse.
   * @return Le nom du fichier comprenant l'information de la texture pour la réflexion diffuse.
   * Le nom du fichier sera <b>null</b> s'il n'y a pas de texture affectée.
   */
  public String getTextureKdFileName()
  {
    return texture_Kd;
  }
  
  /**
   * Méthode pour obtenir le nom du fichier de la texture pour la réflexion spéculaire.
   * @return Le nom du fichier comprenant l'information de la texture pour la réflexion spéculaire.
   * Le nom du fichier sera <b>null</b> s'il n'y a pas de texture affectée.
   */
  public String getTextureKsFileName()
  {
    return texture_Ks;
  }
  
  /**
   * Méthode pour déterminer si le matériel contient une référence à une texture. 
   * @return <b>true</b> si la matériel contient une texture et <b>false</b> sinon.
   */
  public boolean asTexture()
  {
    if(texture_Ka != null)
      return true;
    
    if(texture_Kd != null)
      return true;
    
    if(texture_Ks != null)
      return true;
    
    return false;
  }
  
}//fin SMaterialOBJ
