/**
 * 
 */
package sim.graphics;

import sim.exception.SRuntimeException;

/**
 * Interface représentant un raytracer permettant de générer des images 2D à partir d'une description d'un environnement 3D.
 * @author Simon Vézina
 * @since 2015-04-07
 * @version 2015-04-07
 */
public interface SRaytracer {

  /**
   * Méthode pour effectuer le calcul du raytracing pour l'ensemble des pixels d'un viewport
   * à l'aide d'une description géométrique d'un environnement 3D.
   */
  public void raytrace();
  
  /**
   * Méthode pour effectuer le raytracing de la scène sur un nombre limité de pixels du viewport.
   * @param nb_pixel - Le nombre de pixels à calculer. Si le nombre est supérieur aux pixels disponibles, ils seronts tous calculés.
   * @throws SRuntimeException Si le nombre de pixels à calculer est négatif.
   */
  public void raytrace(int nb_pixel) throws SRuntimeException;
 
  /**
   * Méthode pour obtenir le nombre total de pixels à calculer.
   * @return Le nombre de pixels à calculer.
   */
  public int nbPixels();
  
}//fin interface SRaytracer
