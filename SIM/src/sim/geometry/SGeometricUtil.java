/**
 * 
 */
package sim.geometry;

import sim.exception.SNoImplementationException;
import sim.math.SMath;
import sim.math.SVector3d;

/**
 * La classe <b>SGeometricUtil</b> repr�sente une classe utilitaire permettant d'analyser des g�om�tries.
 * 
 * @author Simon V�zina
 * @since 2015-11-23
 * @version 2015-11-24
 */
public class SGeometricUtil {

  /**
   * <p>M�thode permettant d'�valuer si un vecteur position v est situ� sur la suface d'une sph�re.</p>
   * <p>Le r�sultat de cette m�thode sera interpr�table de la fa�on suivante :
   * <ul>- Si v est � l'int�rieur de la surface de la sph�re : La m�thode retournera -1.</ul>
   * <ul>- Si v est sur la surface de la sph�re : La m�thode retournera 0.</ul>
   * <ul>- Si v est � l'ext�rieur de la surface de la sph�re : La m�thode retournera 1.</ul>
   * </p>
   * 
   * @param r_s - La position de la sph�re.
   * @param R - Le rayon de la sph�re (doit �tre positif).
   * @param v - Le vecteur position � analyser.
   * @return <b>-1</b> si le vecteur est � l'int�rieur, <b>0</b> si le vecteur est sur la surface et <b>1</b> si le vecteur est � l'ext�rieur de la surface.
   */
  public static int isOnSphereSurface(SVector3d r_s, double R, SVector3d v)
  {
    // �valuer la distance entre le centre de la sph�re et le vecteur v
    double distance = v.substract(r_s).modulus();  
    
    double R_plus = (1 + SMath.EPSILON) * R;
    double R_minus = (1 - SMath.EPSILON) * R;
    
    // Si la distance mentionne que le vecteur v est � l'int�rieur
    if(distance < R_minus)
      return -1;
    else
      // Si la distance mentionne que le vecteur v est � l'ext�rieur
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
    throw new SNoImplementationException("Erreur SGeometricUtil 001 : La m�thode n'a pas �t� impl�ment�e.");
  }
  
}//fin de la classe SGeometricUtil
