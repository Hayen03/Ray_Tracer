/**
 * 
 */
package sim.geometry;

import sim.exception.SNoImplementationException;
import sim.math.SMath;
import sim.math.SVector3d;

/**
 * La classe <b>SGeometricUtil</b> représente une classe utilitaire permettant d'analyser des géométries.
 * 
 * @author Simon Vézina
 * @since 2015-11-23
 * @version 2015-11-24
 */
public class SGeometricUtil {

  /**
   * <p>Méthode permettant d'évaluer si un vecteur position v est situé sur la suface d'une sphère.</p>
   * <p>Le résultat de cette méthode sera interprétable de la façon suivante :
   * <ul>- Si v est à l'intérieur de la surface de la sphère : La méthode retournera -1.</ul>
   * <ul>- Si v est sur la surface de la sphère : La méthode retournera 0.</ul>
   * <ul>- Si v est à l'extérieur de la surface de la sphère : La méthode retournera 1.</ul>
   * </p>
   * 
   * @param r_s - La position de la sphère.
   * @param R - Le rayon de la sphère (doit être positif).
   * @param v - Le vecteur position à analyser.
   * @return <b>-1</b> si le vecteur est à l'intérieur, <b>0</b> si le vecteur est sur la surface et <b>1</b> si le vecteur est à l'extérieur de la surface.
   */
  public static int isOnSphereSurface(SVector3d r_s, double R, SVector3d v)
  {
    // Évaluer la distance entre le centre de la sphère et le vecteur v
    double distance = v.substract(r_s).modulus();  
    
    double R_plus = (1 + SMath.EPSILON) * R;
    double R_minus = (1 - SMath.EPSILON) * R;
    
    // Si la distance mentionne que le vecteur v est à l'intérieur
    if(distance < R_minus)
      return -1;
    else
      // Si la distance mentionne que le vecteur v est à l'extérieur
      if(distance > R_plus)
        return 1;
      else
        // Le vecteur v est sur la surface
        return 0;
  }
  
  /**
   * ...
   * 
   * @param r_t
   * @param axis
   * @param R
   * @param v
   * @return
   */
  public static int isOnInfiniteTubeSurface(SVector3d r_t, SVector3d axis, double R, SVector3d v)
  {
    throw new SNoImplementationException("Erreur SGeometricUtil 001 : La méthode n'a pas été implémentée.");
  }
  
}//fin de la classe SGeometricUtil
