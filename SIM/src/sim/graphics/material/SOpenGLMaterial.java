/**
 * 
 */
package sim.graphics.material;

import sim.exception.SConstructorException;
import sim.exception.SNoImplementationException;
import sim.graphics.SColor;
import sim.util.SInitializationException;
import sim.util.SStringUtil;

/**
 * Classe qui représente un matériel respectant les standards de la librairie OpenGl. Par défaut, la couleur du
 * matériel est blanc (1.0, 1.0, 1.0) ou la couleur de la texture et on lui appliquer un vecteur de coefficient
 * de réflexion (ka, kd, ks) qui aura pour effet de filtrer la couleur de base du matériel selon le modèle d'illumination.
 * 
 * @author Simon Vézina
 * @since 2015-01-13
 * @version 2015-12-05
 */
public class SOpenGLMaterial extends SDefaultMaterial {

  //Couleur de base d'un matériel OpenGl (couleur qui devra être multipliée par les coefficients de réflexion)
  protected static final SColor COLOR_BASE = new SColor(1.0, 1.0, 1.0);           
  
  //Paramètre par défaut
  private static final SColor DEFAULT_AMBIENT_COEFFICIENT = new SColor(0.1, 0.1, 0.1);  
  private static final SColor DEFAULT_DIFFUSE_COEFFICIENT = new SColor(0.3, 0.7, 0.3);      //sera vert !!!
  private static final SColor DEFAULT_SPECULAR_COEFFICIENT = new SColor(0.05, 0.05, 0.05);
  private static final double DEFAULT_SHININESS = 5;
  
  private static final String DEFAULT_NAME = "default_openGL_material"; //nom par défaut
  
  private final SColor coefficient_ambient;   //vecteur des coefficients de réflexion ambiant   
  private final SColor coefficient_diffuse;   //vecteur des coefficients de réflexion diffus
  private final SColor coefficient_specular;  //vecteur des coefficients de réflexion spéculaire
  private final double shininess;             //Niveau de brillance du matériel
  
  /**
   * Constructeur d'un matériel respectant les standards de la librairie OpenGL blanc avec nom par défaut.
   */
  public SOpenGLMaterial()
  {
    this(DEFAULT_NAME);
  }
  
  /**
   * Constructeur d'un matériel respectant les standards de la librairie OpenGL blanc.
   * @param name - Le nom du matériel.
   */
  public SOpenGLMaterial(String name)
  {
    this(name, DEFAULT_AMBIENT_COEFFICIENT, DEFAULT_DIFFUSE_COEFFICIENT, DEFAULT_SPECULAR_COEFFICIENT, DEFAULT_SHININESS);
  }
  
  /**
   * Constructeur d'un matériel respectant les standards de la librairie OpenGL.
   * @param name - Le nom du matériel.
   * @param ka - Le vecteur des coefficients de réflexion ambiant.
   * @param kd - Le vecteur des coefficients de réflexion diffuse.
   * @param ks - Le vecteur des coefficients de réflexion spéculaire.
   * @param shininess - La brillance du matériel.
   * @throws SConstructorException Si la brillance du matériel est négative.
   * @throws SConstructorException Si une erreur est survenue lors de la construction. 
   */
  public SOpenGLMaterial(String name, SColor ka, SColor kd, SColor ks, double shininess) throws SConstructorException
  {
    super(name);
    
    if(shininess < 0)
      throw new SConstructorException("Erreur SOpenGLMaterial 001 : La brillance (shininess) '" + shininess + "' est inférieur à zéro.");
    
    coefficient_ambient = ka;
    coefficient_diffuse = kd;
    coefficient_specular = ks;
    this.shininess = shininess;
    
    try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SOpenGLMaterial 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
  }
  
  /* (non-Javadoc)
   * @see simGraphic.SMaterial#ambientColor()
   */
  @Override
  public SColor ambientColor()
  { 
    return COLOR_BASE.multiply(coefficient_ambient);
  }
  
  /* (non-Javadoc)
   * @see simGraphic.SMaterial#diffuseColor()
   */
  @Override
  public SColor diffuseColor()
  { 
    return COLOR_BASE.multiply(coefficient_diffuse);
  }

  /* (non-Javadoc)
   * @see simGraphic.SMaterial#specularColor()
   */
  @Override
  public SColor specularColor()
  { 
    return COLOR_BASE.multiply(coefficient_specular);
  }
  
  /* (non-Javadoc)
	 * @see simGraphic.SMaterial#shininess()
	 */
	@Override
	public double getShininess()
	{ 
	  return shininess;
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
    throw new SNoImplementationException("Erreur SOpenGLMaterial 003 : La méthode n'a pas été implémentée.");
  }
	
}//fin classe SOpenGLMaterial
