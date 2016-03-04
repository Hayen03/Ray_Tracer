/**
 * 
 */
package sim.graphics;

import sim.exception.SRuntimeException;
import sim.graphics.shader.SShader;
import sim.math.SVectorPixel;

/**
 * Classe représentant un raytracer lançant uniquement <b>une rayon</b> pour chaque pixel du viewport.
 * 
 * @author Simon Vézina
 * @since 2015-04-07
 * @version 2015-07-17
 */
public class SSingleCastRaytracer extends SAbstractRaytracer {

  /**
   * Constructeur d'un raytracer.
   * 
   * @param view_frustum - La pyramide de vue.
   * @param shader - Le calculateur d'illumination (<i>shader</i>).
   * @param viewport - La fenêtre de rendu (<i>viewport</i>).
   */
  public SSingleCastRaytracer(SViewFrustum view_frustum, SShader shader, SViewport viewport)
  {
	  super(view_frustum, shader, viewport);
  }

  /*
   * (non-Javadoc)
   * 
   * @see sim.graphics.SRaytracer#raytrace(int)
   */
  @Override
  public void raytrace(int nb_pixel) throws SRuntimeException
  {
    // Vérification du nombre de pixels demandé en calcul
    if (nb_pixel < 0)
      throw new SRuntimeException("Erreur SSingleCastRaytracer 001 : Le nombre de pixels '" + nb_pixel + "' doit être supérieur à 0.");

    for(int nb = 0; nb < nb_pixel && viewport.hasNextPixel(); nb++)
    {
      final SVectorPixel pixel = viewport.nextPixel();	//le prochain pixel à calculer
      final SColor color = evaluatePixelColor(pixel);	  //déterminer la couleur calculé par le shader pour le pixel	
      viewport.setColor(pixel, color);	                //dessiner le pixel avec la couleur dans le bon pixel
    }
  }

  /**
   * Méthode pour évaluer la couleur à attribuer à un pixel.
   * 
   * @param pixel - La coordonnée du pixel.
   * @return La couleur à attribuer au pixel.
   */
  protected SColor evaluatePixelColor(SVectorPixel pixel)
  {
    return raytracePixel(pixel);
  }

}// fin classe SSingleCastRaytracer
