/**
 * 
 */
package sim.graphics.shader;

import sim.exception.SRuntimeException;
import sim.geometry.SRay;
import sim.graphics.SColor;
import sim.math.SVector3d;

/**
 * Interface permet de déterminer la couleur associée à l'intersection d'un rayon avec une géométrie en fonction du matériel appliqué sur la géométrie, de l'éclairage de la scène et des primitives environnant (ex : effet d'ombrage).
 * Il existe une très grande variété de "shader" dont leur complexité améliore le réalisme de la couleur calculée.
 * 
 * @author Simon Vézina
 * @since 2015-01-09
 * @version 2015-08-14
 */
public interface SShader {

	/**
	 * Méthode qui détermine la couleur associée à un rayon ayant effectué une intersection avec une géométrie de la scène.
	 * Si le <b>rayon a touché une géométrie</b>, un calcul sera réalisé pour <b>déterminer la couleur</b> en lien avec le <b>point d'intersection</b>.
	 * Si le <b>rayon n'a pas touché de géométrie</b>, un <b>test d'intersection sera réalisé</b> en premier temps pour déterminer la géométrie intersecté et un calcul de couleur sera réalisé par la suite.
	 * Une couleur </b>noire</b> sera retournée s'il n'y a <b>pas d'intersection</b> entre le rayon et une géométrie de la scène.
	 * Il est important de rappeler que chaque géométrie possède un lien vers sa primitive parent qui détient l'information du matériel (ex: couleur de la géométrie).
	 * @param ray - Le rayon ayant réalisé une intersection.
	 * @return La couleur associée à l'intersection. S'il n'y a <b>pas d'intersection</b>, la couleur retournée sera <b>noire</b>.
	 * @throws SRuntimeException Si le rayon a déjà été intersecté péalablement.
	 */
	public SColor shade(SRay ray)throws SRuntimeException;
	
	/**
   * Méthode pour évaluer l'indice de réfraction associé à un point de l'espace.
   * Nous avons trois scénarios possibles :
   * <p>1) Si la position est située à la <b>frontière d'une géométrie</b> (sur la surface), elle sera considérée comme à l'extérieure de la géométrie et l'indice n = 1.0 sera affecté.</p>
   * <p>2) Si la position est située dans <b>une géométrie</b>, il faudra obtenir son indice de réfraction n s'il possède une primitive comme parent, sinon n = 1.0 sera affecté.</p>
   * <p>3) Si la position est située dans <b>plusieurs géométrie</b>, NOUS AVONS PRÉSENTEMENT UN PROBLÈME !!! qui devra être résolu dans le futur.</p>
   * @param position - La position dans l'espace des géométries.
   * @return L'indice de réfraction associé à la position.
   */
  public double evaluateRefractiveIndex(SVector3d position);
  
}//fin interface SShader
