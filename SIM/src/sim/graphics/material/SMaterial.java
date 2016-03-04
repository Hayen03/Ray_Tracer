/**
 * 
 */
package sim.graphics.material;

import sim.graphics.SColor;
import sim.math.SVectorUV;
import sim.util.SWriteable;

/**
 * L'interface SMaterial représente un matériel avec différentes propriétés. 
 * 
 * @author Simon Vézina
 * @since 2015-01-13
 * @version 2015-10-31
 */
public interface SMaterial extends SWriteable {

	/**
	 * Méthode pour obtenir le nom du matériel.
	 * 
	 * @return Le nom du matériel.
	 */
	public String getName();
	
	/**
	 * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>ambiante</u> par la surface du matériel.
	 * Cette couleur peut être calculée à partir du produit de la couleur de la surface <b><i>S</i></b> avec la constant
	 * de réflexion ambiant <b><i>k</i>a</b> tel que <ul><b><i>S</i>a = <i>k</i>a*<i>S</i></b>.</ul>
	 * 
	 * @return La couleur de la lumière <b><i>S</i>a</b> réfléchie de façon ambiante par la surface.
	 */
	public SColor ambientColor();
	
	/**
   * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>ambiante</u> par la surface du matériel
   * à partir d'une <b>coordonnée <i>uv</i></b> d'une <b>texture</b>. S'il n'y a <b>pas de texture en mémoire</b> ou 
   * que cette <b>méthode est non définie</b> par le matériel, la couleur retournée sera celle de la méthode <b>ambiantColor()</b>.
   * 
   * @param uv - La coordonnée <i>uv</i> de la texture. 
   * @return La couleur de réflexion ambiante de la texture (si elle est disponible).
   */
  public SColor ambientColor(SVectorUV uv);
    
	/**
	 * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>diffuse</u> par la surface du matériel.
	 * Cette couleur peut être calculée à partir du produit de la couleur de la surface <b><i>S</i></b> avec la constant
	 * de réflexion diffuse <b><i>k</i>b</b> tel que <ul><b><i>S</i>d = <i>k</i>d*<i>S</i></b>.</ul>
	 * 
	 * @return La couleur de la lumière <b><i>S</i>d</b> réfléchie de façon diffuse par la surface.
	 */
	public SColor diffuseColor();
	
	/**
   * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>diffuse</u> par la surface du matériel
   * à partir d'une <b>coordonnée <i>uv</i></b> d'une <b>texture</b>. S'il n'y a <b>pas de texture en mémoire</b> ou 
   * que cette <b>méthode est non définie</b> par le matériel, la couleur retournée sera celle de la méthode <b>diffuseColor()</b>.
   * 
   * @param uv - La coordonnée <i>uv</i> de la texture. 
   * @return La couleur de réflexion diffuse de la texture (si elle est disponible).
   */
  public SColor diffuseColor(SVectorUV uv);
  
	/**
	 * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>spéculaire</u> par la surface du matériel.
	 * Cette couleur peut être calculée à partir du produit de la couleur de la surface <b><i>S</i></b> avec la constant
	 * de réflexion spéculaire <b><i>k</i>s</b> tel que <ul><b><i>S</i>s = <i>k</i>s*<i>S</i></b>.</ul>
	 * 
	 * @return La couleur de la lumière <b><i>S</i>s</b> réfléchie de façon spéculaire par la surface.
	 */
	public SColor specularColor();
	
	/**
   * Méthode pour obtenir la <b>couleur réfléchie</b> de façon <u>spéculaire</u> par la surface du matériel
   * à partir d'une <b>coordonnée <i>uv</i></b> d'une <b>texture</b>. S'il n'y a <b>pas de texture en mémoire</b> ou 
   * que cette <b>méthode est non définie</b> par le matériel, la couleur retournée sera celle de la méthode <b>specularColor()</b>.
   * 
   * @param uv - La coordonnée <i>uv</i> de la texture. 
   * @return La couleur de réflexion speculaire de la texture (si elle est disponible).
   */
  public SColor specularColor(SVectorUV uv);
  
	/**
	 * <p>Méthode pour obtenir la <b>couleur transmise</b> (filtrée) par la surface du matériel. Elle permet de faire des calculs
	 * de filtrage de la lumière.</p> <p>Par exemple, une couleur de transparence (1,1,1) fait tout traverser, une couleur de 
	 * transparence (0.8, 0.8, 0.8) fait tout traverser avec atténuation et une couleur de transparence (1,0,0)
	 * fait uniquement traverser la couleur rouge.</p> Cette couleur est calculée à partir du produit de la couleur de la surface <b><i>S</i></b> avec la constant
	 * de transmission <b><i>k</i>t</b> tel que <ul><b><i>S</i>t = <i>k</i>t*<i>S</i></b>.</ul>
	 * 
	 * @return La couleur de la lumière <b><i>S</i>t</b> transmise par la surface.
	 */
	public SColor transparencyColor();
	
	/**
	 * Méthode pour obtenir le niveau de brillance de la surface du matériel.
	 * @return le niveau de brillance du matériel.
	 */
	public double getShininess();
	
	/**
	 * Méthode qui détermine si le matériel réfléchi la lumière comme un miroir.
	 * @return <b>true</b> si le matériel est réfléchissant et <b>false</b> sinon.
	 */
	public boolean isReflective();
	
	/**
	 * Méthode pour obtenir le niveau de réflexion d'une source de lumière par la surface du matériel.
	 * @return le niveau de réflexion du matériel (0 = 0% réfléchie, 1 = 100% réfléchie).
	 */
	public double reflectivity();
	
	/**
	 * Méthode qui détermine si le matériel est transparent.
	 * @return <b>true</b> si le matériel est transparent et <b>false</b> sinon.
	 */
	public boolean isTransparent();
	
	/**
	 * Méthode pour obtenir le niveau de transparence de la surface du matériel.
	 * @return le niveau de transparence du matériel (0 = 0% transparent, 1 = 100% transparent).
	 */
	public double transparency();
	
	/**
	 * Méthode pour obtenir le niveau d'opacité de la surface du matériel. Cette valeur correspond à l'inverse de la transparence.
	 * La somme de la transparence <b><i>k</i>t</b> avec l'opacité <b><i>k</i>o</b> donne 1 (kt + ko = 1).
	 * @return le niveau d'opacité du matériel (0 = 0% opaque, 1 = 100% opaque).
	 */
	public double opacity();
	
	/**
	 * Méthode pour obtenir l'indice de réfraction du matériel.
	 * @return L'indice de réfraction du matériel. Cependant, si le matériel est <b>opaque</b>, l'indice de réfraction sera automatiquement égale à <b>1.0</b>.
	 * @throws SRuntimeException Si le matériel n'est pas transparent.
	 */
	public double refractiveIndex();
	
	/**
	 * Méthode permettant de préciser si le matétiel contient des textures dans sa définition.
	 * 
	 * @return <b>true</b> si le matériel est défini avec des textures et <b>false</b> sinon.
	 */
	public boolean asTexture();
	
}//fin interface SMaterial
