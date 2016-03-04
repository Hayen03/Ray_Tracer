/**
 * 
 */
package sim.graphics;

import sim.geometry.SRay;
import sim.graphics.shader.SShader;
import sim.math.SVector3d;
import sim.math.SVectorPixel;

/**
 * Classe abstraite représentant un raytracer.
 * @author Simon Vézina
 * @since 2015-04-11
 * @version 2015-07-16
 */
public abstract class SAbstractRaytracer implements SRaytracer {

	 protected final SViewFrustum view_frustum; // pyramide de vue du raytracer
	 protected final SShader shader; // shader du raytracer
	 protected final SViewport viewport; // viewport du raytracer
	
	 /**
	   * Constructeur d'un raytracer.
	   * 
	   * @param view_frustum - La pyramide de vue.
	   * @param shader - Le calculateur d'illumination (<i>shader</i>).
	   * @param viewport - La fenêtre de rendu (<i>viewport</i>).
	   */
	  public SAbstractRaytracer(SViewFrustum view_frustum, SShader shader, SViewport viewport)
	  {
	    this.view_frustum = view_frustum;
	    this.shader = shader;
	    this.viewport = viewport;
	  }
	  
	/* (non-Javadoc)
	 * @see sim.graphics.SRaytracer#raytrace()
	 */
	@Override
	public void raytrace()
	{
		raytrace(nbPixels());
	}

	/* (non-Javadoc)
	 * @see sim.graphics.SRaytracer#nbPixels()
	 */
	@Override
	public int nbPixels() 
	{
		return viewport.getHeight() * viewport.getHeight();
	}

	/**
	   * Méthode pour évaluer la couleur à attribuer à un pixel.
	   * 
	   * @param pixel - La coordonnée du pixel.
	   * @return La couleur à attribuer au pixel.
	   */
	abstract protected SColor evaluatePixelColor(SVectorPixel pixel);
	
	/**
	   * Méthode pour calculer la couleur associé à un rayon lancé depuis la position de la caméra et traversant un pixel de la pyramide de vue. La coordonnée du
	   * pixel sera calculée par la pyramide de vue (view frustum) et la couleur sera déterminée par le shader ayant accès à la géométrie de la scène et des
	   * matériaux qui s'y trouvent.
	   * 
	   * @param pixel - La coordonnée du pixel.
	   * @return La couleur calculée par <b>le lancé d'un rayon</b> dans le pixel.
	   */
	  protected SColor raytracePixel(SVectorPixel pixel)
	  {
	    // La coordonnée 3d du pixel dans les coordonnées de la scène
	    SVector3d position_pixel = view_frustum.viewportToViewFrustum(pixel);

	    // Direction du rayon normalisé
	    SVector3d direction = position_pixel.substract(view_frustum.getCameraPosition()).normalize();
	   
	    // Évaluer adéquatement l'indice de réfraction et reconstruction du rayon adéquatement
	    double refractive_index = shader.evaluateRefractiveIndex(position_pixel);
	    
	    // Construire le rayon avec le bon indice de réfraction
	    SRay ray = new SRay(position_pixel, direction, refractive_index); 

	    // Évaluer la couleur attribuée à ce rayon partant de la caméra et traversant un pixel du viewport
	    return shader.shade(ray);
	  }
	  
}//fin de la classe SAbstractRaytracer
