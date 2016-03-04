/**
 * 
 */
package sim.geometry.space;

import java.util.List;

import sim.exception.SRuntimeException;
import sim.geometry.SGeometry;
import sim.geometry.SRay;
import sim.math.SVector3d;

/**
 * L'interface <b>SGeometrySpace</b> représentant un espace de rengement des géométries. 
 * Cet espace permet d'organiser les géométries afin d'effectuer le calculs de l'intersection avec les géométries plus efficacement.
 * 
 * @author Simon Vézina
 * @since 2015-01-10
 * @version 2015-12-29
 */
public interface SGeometrySpace {

	/**
	 * Méthode qui ajoute une géométrie à l'espace.
	 * @param geometry - La géométrie à ajouter à l'espace.
	 */
	public void addGeometry(SGeometry geometry);
	
	/**
	 * Méthode qui ajoute une liste de géométries à l'espace.
	 * @param list - La liste de géométries à ajouter à l'espace.
	 */
	public void addGeometry(List<SGeometry> list);
	
	/**
	 * Méthode qui évalue l'intersection la plus près entre un rayon et les différentes géométries de l'espace ne dépassant pas un certain temps maximal.
	 * @param ray - Le rayon à intersecter avec les géométries de l'espace.
	 * @param t_max - Le temps maximal.
	 * @return Un nouveau rayon avec les propriétés de l'intersection la plus près ne dépassant pas le temps maximal. Si <b>aucune intersection</b> respectant ces contraintes n'a peut être réalisée, le <b>rayon d'origine sera retourné</b> étant un rayon sans intersection.
	 * @throws SRuntimeException Si le rayon a déjà intersecté une autre géométrie.
	 * @throws SRuntimeException Si la valeur de t_max est négative.
	 * @throws SRuntimeException Si l'espace des géométries n'a pas été préalablement initialisé.
	 */
	public SRay nearestIntersection(SRay ray, double t_max) throws SRuntimeException;
		
	/**
	 * Méthode qui évalue l'intersection la plus près entre un rayon et les différentes géométries <b>opaque</b> de l'espace ne dépassant pas un certain temps maximal.
	 * Une liste des géométries transparentes sera ordonnée en <b>ordre inverse d'apparition</b> (ordre décroissant) et la <b>liste débutera par une géométrie opaque</b> s'il y a eu intersection avec une géométrie opaque.
	 * @param ray - Le rayon à intersecter avec les géométries de l'espace.
	 * @param t_max - Le temps maximal.
	 * @return Une liste des géométries transparentes intersectées en <b>ordre décroissant</b> débutant par une géométrie opaque s'il y a eu lieu. La liste sera <b>vide</b> s'il n'y a <b>aucune intersection</b> sur le temps maximal.
	 * @throws SRuntimeException Si le rayon a déjà intersecté une autre géométrie.
	 * @throws SRuntimeException Si la valeur de t_max est négative.
	 * @throws SRuntimeException Si l'espace des géométries n'a pas été préalablement initialisé.
	 */
	public List<SRay> nearestOpaqueIntersection(SRay ray, double t_max) throws SRuntimeException;
	
	/**
	 * Méthode pour obtenir la liste des géométries où la position du vecteur <i>v</i> se retrouve à l'intérieur.
	 * Si la liste est vide, c'est que le vecteur <i>v</i> se retrouve à l'intérieur d'aucune géométrie.
	 * @param v - La position d'un point de l'espace.
	 * @return La liste des géométries où le vecteur <i>v</i> se retrouve à l'intérieur.
	 * @throws SRuntimeException Si l'espace des géométries n'a pas été préalablement initialisé.
	 */
	public List<SGeometry> listInsideGeometry(SVector3d v) throws SRuntimeException;
	
	/**
	 * Méthode pour initialiser l'espace des géométries. L'espace ne sera pas opérationnel 
	 * pour effectuer des tests d'intersection si cette méthode n'est pas activée avant.
	 */
	public void initialize();
	
}//fin interface SGeometrySpace
