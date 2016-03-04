/**
 * 
 */
package sim.geometry;

import sim.exception.SNoImplementationException;
import sim.math.SVector3d;
import sim.math.SVectorUV;

/**
 * La classe <b>SGeometricUVMapping</b> est une classe utilitaire permettant de faire la correspondance
 * entre des coordonnées sur une géométrie et une coordonnée de texture uv.
 * 
 * @author Simon Vézina
 * @since 2015-11-10
 * @version 2015-12-21
 */
public class SGeometricUVMapping {

  /**
   * ...
   * 
   * @param r_sphere - La position de la sphère.
   * @param R - Rayon de la sphère (doit être positif).
   * @param P - Le point P sur la sphère (doit être sur la sphère).
   * @return La coordonnée de texture uv.
   */
  public static SVectorUV sphereUVMapping(SVector3d r_sphere, double R, SVector3d P) 
  {
    throw new SNoImplementationException("La méthode doit être implémentée dans le cadre d'un laboratoire.");
  }
  
  
}//fin de la classe SGeometricUVMapping
