/**
 * 
 */
package sim.geometry;

import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SVector3d;
import sim.util.SWriteable;

/**
 * Interface représentant le mandat d'une géométrie. 
 * Une géométrie doit être intersectable par une rayon, définir si elle représente une surface fermée
 * et identifier si un vecteur position se trouve à l'intérieur d'elle (possible si elle est une surface fermée).
 * @author Simon Vézina
 * @since 2015-01-10
 * @version 2015-07-26
 */
public interface SGeometry extends SWriteable {

	/**
	 * Méthode pour obtenir le numéro d'identification unique ID de la géométrie.
	 * @return Le numéro d'identification ID.
	 */
	public long getID();
	
	/**
	 * Méthode pour obtenir le numéro correspondant au nom de la géométrie.
	 * @return Le numéro correspondant au nom de la géométrie.
	 */
	public int getCodeName();
	
	/**
   * Méthode pour obtenir la primitive parent dont cette géométrie fait partie.
   * @return La primitive parent de cette géométrie.
   * @throws SRuntimeException Si la géométrie ne possède pas de primitive comme parent.
   */
  public SPrimitive getPrimitiveParent()throws SRuntimeException;
  
  /**
   * Méthode pour affecter primitive commet parent à la géométrie. 
   * @param parent - La primitive parent de la géométrie.
   * @throws SRuntimeException Si la géométrie possède déjà un parent (n'est pas préalablement 'null').
   */
  public void setPrimitiveParent(SPrimitive parent)throws SRuntimeException;
  
	/**
	 * Méthode qui détermine si la géométrie est transparente.
	 * @return <b> true </b> si la géométrie est transparente et <b> false </b> sinon.
	 */
	public boolean isTransparent();
	
	/**
	 * Méthode qui détermine si la géométrie est une surface fermée. Ainsi, on peut y définir l'intérieur et l'extérieur. 
	 * @return <b> true </b> si la géométrie est fermée et <b> false </b> sinon.
	 */
	public boolean isClosedGeometry();
	
	/**
	 * Méthode qui détermine si un vecteur point à l'intérieur de la géométrie. Si la géométrie n'est pas fermée,
	 * le vecteur point automatiquement à l'extérieur de la géométrie.
	 * @param v - Le vecteur.
	 * @return <b> true </b> si le vecteur point à l'intérieur de la géométrie et <b> false </b> sinon.
	 */
	public boolean isInside(SVector3d v);
	
	/**
   * Méthode permettant d'effectuer le calcul de l'intersection (s'il y a) entre un rayon et une géométrie.
   * @param ray - Le rayon à intersecter avec la géométrie.
   * @return Un rayon avec les caractéristiques de l'intersection avec la géométrie. 
   * Une référence sur le rayon en paramètre sera renvoyé s'il n'y a pas eu d'intersection.
   * @throws SRuntimeException S'il y a déjà eu intersection avec ce rayon.
   */
  public SRay intersection(SRay ray)throws SRuntimeException;
	
}//fin interface SGeometry
