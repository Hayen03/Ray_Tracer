/**
 * 
 */
package sim.geometry;

import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.math.SVector3d;
import sim.util.SWriteable;

/**
 * Interface repr�sentant le mandat d'une g�om�trie. 
 * Une g�om�trie doit �tre intersectable par une rayon, d�finir si elle repr�sente une surface ferm�e
 * et identifier si un vecteur position se trouve � l'int�rieur d'elle (possible si elle est une surface ferm�e).
 * @author Simon V�zina
 * @since 2015-01-10
 * @version 2015-07-26
 */
public interface SGeometry extends SWriteable {

	/**
	 * M�thode pour obtenir le num�ro d'identification unique ID de la g�om�trie.
	 * @return Le num�ro d'identification ID.
	 */
	public long getID();
	
	/**
	 * M�thode pour obtenir le num�ro correspondant au nom de la g�om�trie.
	 * @return Le num�ro correspondant au nom de la g�om�trie.
	 */
	public int getCodeName();
	
	/**
   * M�thode pour obtenir la primitive parent dont cette g�om�trie fait partie.
   * @return La primitive parent de cette g�om�trie.
   * @throws SRuntimeException Si la g�om�trie ne poss�de pas de primitive comme parent.
   */
  public SPrimitive getPrimitiveParent()throws SRuntimeException;
  
  /**
   * M�thode pour affecter primitive commet parent � la g�om�trie. 
   * @param parent - La primitive parent de la g�om�trie.
   * @throws SRuntimeException Si la g�om�trie poss�de d�j� un parent (n'est pas pr�alablement 'null').
   */
  public void setPrimitiveParent(SPrimitive parent)throws SRuntimeException;
  
	/**
	 * M�thode qui d�termine si la g�om�trie est transparente.
	 * @return <b> true </b> si la g�om�trie est transparente et <b> false </b> sinon.
	 */
	public boolean isTransparent();
	
	/**
	 * M�thode qui d�termine si la g�om�trie est une surface ferm�e. Ainsi, on peut y d�finir l'int�rieur et l'ext�rieur. 
	 * @return <b> true </b> si la g�om�trie est ferm�e et <b> false </b> sinon.
	 */
	public boolean isClosedGeometry();
	
	/**
	 * M�thode qui d�termine si un vecteur point � l'int�rieur de la g�om�trie. Si la g�om�trie n'est pas ferm�e,
	 * le vecteur point automatiquement � l'ext�rieur de la g�om�trie.
	 * @param v - Le vecteur.
	 * @return <b> true </b> si le vecteur point � l'int�rieur de la g�om�trie et <b> false </b> sinon.
	 */
	public boolean isInside(SVector3d v);
	
	/**
   * M�thode permettant d'effectuer le calcul de l'intersection (s'il y a) entre un rayon et une g�om�trie.
   * @param ray - Le rayon � intersecter avec la g�om�trie.
   * @return Un rayon avec les caract�ristiques de l'intersection avec la g�om�trie. 
   * Une r�f�rence sur le rayon en param�tre sera renvoy� s'il n'y a pas eu d'intersection.
   * @throws SRuntimeException S'il y a d�j� eu intersection avec ce rayon.
   */
  public SRay intersection(SRay ray)throws SRuntimeException;
	
}//fin interface SGeometry
