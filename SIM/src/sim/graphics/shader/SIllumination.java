/**
 * 
 */
package sim.graphics.shader;

import java.util.List;

import sim.graphics.SColor;
import sim.graphics.material.SMaterial;
import sim.math.SVector3d;

/**
 * La classe <b>SIllumination</b> représente une classe utilitaire qui effectue les calculs d'illuminations selon le type d'algorithme utilisé. 
 * On y retrouve des modèles d'illumination à réflexion (ambiante, diffuse et spéculaire) et à transmission (filtrage
 * d'une source de lumière au travers des matériaux transparents). 
 * 
 * @author Simon Vézina
 * @since 2015-01-16
 * @version 2015-12-21
 */
public class SIllumination {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>NO_ILLUMINATION</b> correspond à la couleur par défaut sans calcul d'illumination.
   * Cette couleur est <b>noire</b>.
   */
	public static final SColor NO_ILLUMINATION = new SColor(0.0, 0.0, 0.0);
	
	//------------
	// MÉTHODES //
	//------------
	
	/**
	 * <p>Méthode qui effectue le calcul de la <u>réflexion ambiante</u> d'une source de lumière <b><i>L</i>a</b> sur une surface
	 * dont la couleur réfléchie de façon ambiante est <b><i>S</i>a</b>.</p> 
	 * <p>La formule calculée est <b><i>L</i>amb = <i>L</i>a*<i>S</i>a</b>.</p> 
	 * 
	 * @param La - La couleur ambiante <b><i>L</i>a</b> de la source de lumière.
	 * @param Sa - La couleur réfléchie de façon ambiante <b><i>S</i>a</b> par la surface.
	 * @return La couleur ambient <b><i>L</i>amb</b> de la lumière réfléchie par la surface.
	 */
	public static SColor ambientReflexion(SColor La, SColor Sa)
	{
		return La.multiply(Sa);
	}
	
	/**
	 * <p>Méthode qui effectue le calcul de la <u>réflexion diffuse</u> selon le <b>modèle de réfexion Lambertienne</b> d'une source de lumière <b><i>L</i>d</b> sur une surface
	 * dont la couleur réfléchie de façon diffuse est <b><i>S</i>d</b></b>.</p> 
	 * <p>La formule calculée est <ul><b><i>L</i>dif = <i>L</i>d*<i>S</i>d*<i>N</i>dot<i>L</i></b></ul></p>
	 * <p>où <b><i>N</i></b> est la normale à la surface et <b><i>L</i></b> est l'orientation inverse de la source de lumière.</p>
	 * 
	 * @param Ld - La couleur ambiante <b><i>L</i>d</b> de la source de lumière.
	 * @param Sd - La couleur réfléchie de façon diffuse <b><i>S</i>d</b> par la surface.
	 * @param N - La normale <b><i>N</i></b> à la surface (unitaire).
	 * @param d - L'orientation <b><i>d</i></b> de la source de lumière (unitaire).
	 * @return La couleur diffuse <b><i>L</i>dif</b> de la lumière réfléchie par la surface.
	 */
	public static SColor lambertianReflexion(SColor Ld, SColor Sd, SVector3d N, SVector3d d)
	{
	  return Ld.multiply(Sd.multiply(N.dot(d.multiply(-1)))).normalize();
	}
	
	/**
	 * <p>Méthode qui effectue le calcul de la <u>réflexion spéculaire</u> selon le <b>modèle spéculaire de <i>Phong</i></b> d'une source de lumière <b><i>L</i>s</b> sur une surface
	 * dont la couleur réfléchie de façon spéculaire est <b><i>S</i>s</b></b>.</p> 
	 * <p>La formule calculée est <ul><b><i>L</i>spe = <i>L</i>s*<i>S</i>s*(<i>R</i>dot<i>E</i>)^<i>n</i></b></ul></p>
	 * <p>où <b><i>R</i></b> est l'orientation de la réflexion de la lumière, <b><i>E</i></b> est l'orientation vers l'oeil (l'inverse de l'orientation du rayon) et <b><i>n</i></b> est le niveau de brillance.</p>
	 * 
	 * @param Ls - La couleur spéculaire <b><i>L</i>s</b> de la source de lumière.
	 * @param Ss - La couleur réfléchie de façon spéculaire <b><i>S</i>s</b> par la surface.
	 * @param N - La normale <b><i>N</i></b> à la surface (unitaire).
	 * @param v - L'orientation <b><i>v</i></b> du rayon  (unitaire).
	 * @param d - L'orientation <b><i>d</i></b> de la source de lumière (unitaire).
	 * @param n - Le niveau de brillance <b><i>n</i></b> (à quel point la surface est polie).
	 * @return La couleur spéculaire <b><i>L</i>spe</b> de la lumière réfléchie par la surface.  
	 */
	public static SColor phongSpecularReflexion(SColor Ls, SColor Ss, SVector3d N, SVector3d v, SVector3d d, double n)
	{
	  return NO_ILLUMINATION;
	}
	
	/**
	 * <p>Méthode qui effectue le calcul de la <u>réflexion spéculaire</u> selon le <b>modèle spéculaire de <i>Blinn</i></b> d'une source de lumière <b><i>L</i>s</b> sur une surface
	 * dont la couleur réfléchie de façon spéculaire est <b><i>S</i>s</b></b>.</p> 
	 * <p>La formule calculée est <ul><b><i>L</i>spe = <i>L</i>s*<i>S</i>s*(<i>N</i>dot<i>H</i>)^<i>n</i></b></ul></p>
	 * <p>où <b><i>N</i></b> est l'orientation de la normale à la surface, <b><i>H</i></b> est l'orientation du vecteur bisecteur entre <b><i>E</i></b> et <b><i>L</i></b> et <b><i>n</i></b> est le niveau de brillance.</p>
	 * 
	 * @param Ls - La couleur spéculaire <b><i>L</i>s</b> de la source de lumière.
	 * @param Ss - La couleur réfléchie de façon spéculaire <b><i>S</i>s</b> par la surface.
	 * @param N - La normale <b><i>N</i></b> à la surface (unitaire).
	 * @param v - L'orientation <b><i>v</i></b> du rayon  (unitaire).
	 * @param d - L'orientation <b><i>d</i></b> de la source de lumière (unitaire).
	 * @param n - Le niveau de brillance <b><i>n</i></b> (à quel point la surface est polie).
	 * @return La couleur spéculaire <b><i>L</i>spe</b> de la lumière réfléchie par la surface.  
	 */
	public static SColor blinnSpecularReflexion(SColor Ls, SColor Ss, SVector3d N, SVector3d v, SVector3d d, double n)
	{
	  return NO_ILLUMINATION;
	}
	
	/**
	 * Méthode pour évaluer la couleur d'une source de lumière après son passage au travers plusieurs matériaux transparents.
	 * 
	 * @param transparent_material_list - La liste des matériaux transparents traversés par la lumière.
	 * @param light_color - La couleur de la source de lumière.
	 * @return La couleur de la source de la lumière filtré par son passage au travers les matériaux transparents.
	 */
	public static SColor filteredTransparencyLight(List<SMaterial> transparent_material_list, SColor light_color)
	{
		SColor filtered_light = light_color;	//couleur de la lumière filtrée par la transparence des géométries traversées
		
		//Nous allons utiliser la couleur diffuse pour déterminer la couleur pouvant traverser un matériel.
		//Les couleurs des matériaux vont jouer le rôle de filtre laissant passer uniquement la couleur du matériel
		//et pondéré par leur niveau de transparence.
		for(SMaterial m : transparent_material_list)
			filtered_light = filtered_light.multiply(m.transparencyColor());
		
		
		return filtered_light;
	}
	
}//fin de la classe SIllumination
