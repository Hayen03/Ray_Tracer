/**
 * 
 */
package sim.math;

import java.util.List;

import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;

/**
 * La classe <b>SVectorUtil</b> est une classe utilitaire permettant d'effectuer des opérations sur des objet héritant de <b>SVector</b>.
 * 
 * @author Simon Vézina
 * @since 2015-11-03
 * @version 2015-12-21
 */
public class SVectorUtil {

  /**
   * <p>Méthode effectuant le calcul de l'interpolation linéaire entre deux vecteurs v0 et v1 par le facteur t.
   * L'équation mathématique correspondant à l'interpolation est
   * <ul> v = v0*(1 - t) + v1*t</ul>
   * où v0 est le vecteur de référence et v1 est le vecteur pondéré par le facteur t.</p>
   * <p>De plus, l'interpolation impose la contrainte
   * <ul>0 <= t <= 1</ul>
   * sur le facteur t.</p>
   * 
   * @param v0 - Le 1ier vecteur de référence pondéré par 1 - t.
   * @param v1 - Le 2ième vecteur pondéré par le facteur t.
   * @param t - Le paramètre de pondération.
   * @return Le vecteur interpolé.
   * @throws SRuntimeException Si la contrainte sur t n'est pas respectée (0 <= t <= 1).
   */
  public static SVector linearInterpolation(SVector v0, SVector v1, double t)throws SRuntimeException
  {
    //Doit satisfaire la contrainte sur t
    if(t < 0.0 || t > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 001 : L'interpolation par le paramètre t = " + t + " n'est pas définie,car t doit respecter l'inégalité 0 <= t <= 1");
    
    throw new SNoImplementationException("Erreur SVectorUtil : La méthode n'a pas été implémentée.");
  }
  
  /**
   * <p>Méthode effectant le calcul de l'interpolation linéaire en coordonnée barycentrique entre trois vecteurs v0, v1 et v2 par le facteur t1 et t2.
   * <p>L'équation mathématique correspondant à l'interpolation est
   * <ul> v = v0*(1 - t1 - t2) + v1*t1 + v2*t2</ul>  
   * où v0 est le vecteur de référence et v1 est le vecteur pondéré par le facteur t1 
   * et v2 est le vecteur pondéré par le facteur t2.</p>
   * <p>De plus, l'interpolation impose les contraintes
   * <ul>0 <= t1 <= 1 , 0 <= t2 <= 1 et t1 + t2 <= 1</ul>
   * sur le facteur t1 et t2.</p>
   * 
   * @param v0 - Le 1ier vecteur de référence pondéré par 1 - t1 - t2.
   * @param v1 - Le 2ième vecteur pondéré par t1.
   * @param v2 - Le 3ième vecteur pondéré par t2.
   * @param t1 - Le 1ier paramètre de pondération.
   * @param t2 - Le 2ième paramètre de pondération.
   * @return Le vecteur interpolé.
   * @throws SRuntimeException Si les contraintes sur t1 et t2 ne sont pas respectées (0 <= t1 <= 1, 0 <= t2 <= 1 et t1 + t2 <= 1).  
   */
  public static SVector linearBarycentricInterpolation(SVector v0, SVector v1, SVector v2, double t1, double t2)throws SRuntimeException
  {
    //Doit satisfaire la contrainte sur t1 (0 <= t1 <= 1)
    if(t1 < 0.0 || t1 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 002 : L'interpolation par le paramètre t1 = " + t1 + " n'est pas définie,car t1 doit respecter l'inégalité 0 <= t1 <= 1");
   
    //Doit satisfaire la contrainte sur t2 (0 <= t2 <= 1)
    if(t2 < 0.0 || t2 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 003 : L'interpolation par le paramètre t2 = " + t2 + " n'est pas définie,car t1 doit respecter l'inégalité 0 <= t2 <= 1");
   
    //Doit satisfaire la contrainte sur t1 et t2 (t1 + t2 <= 1)
    if(t1 + t2 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 004 : L'interpolation par les paramètre t1 = " + t1 + " et t2 = " + t2 + " n'est pas définie,car t1 et t2 ne respecter l'inégalité t1 + t2 <= 1");
   
    throw new SNoImplementationException("Erreur SVectorUtil : La méthode n'a pas été implémentée.");
  }
  
  /**
   * <p>Méthode effectant le calcul de l'interpolation linéaire en coordonnée barycentrique entre plusieurs vecteurs v0, v1, ... et v_n par les facteur t1, t2, ..., t_n.
   * <p>L'équation mathématique correspondant à l'interpolation est
   * <ul> v = v0*(1 - t1 - t2 - ... - t_n) + v1*t1 + v2*t2 + ... + v_n*t_n</ul>  
   * où v0 est le vecteur de référence et les vecteur v_i sont les vecteurs pondérés par les facteurs t_i.</p>
   * <p>De plus, l'interpolation impose les contraintes
   * <ul>0 <= t_i <= 1 , t1 + t2 + ... + t_n <= 1</ul>
   * sur tous les facteurs t_i.</p>
   * 
   * @param vector_list - La liste des vecteurs dans l'interpolation. Cette liste contient n+1 vecteur (v0 et les v_i étant de nombre n).
   * @param t_list - La liste des facteurs de pondérations des vecteurs. Cette liste contient n facteurs (les n facteurs des n vecteur v_i).
   * @return Le vecteur interpolé.
   * @throws SRuntimeException Si les contraintes sur t_i ne sont pas respectées (0 <= t_i <= 1 et t1 + t2 + ... + t_n <= 1). 
   * @throws SRuntimeException Si le nombre d'élément des listes n'est pas adéquat (vector_list.size() != t_list.size()+1). 
   
   */
  public static SVector linearBarycentricInterpolation(List<SVector> vector_list, List<Double> t_list) throws SRuntimeException
  {
    //Vérification de la contrainte sur t (0 <= t <= 1) pour l'ensemble des t
    for(Double t : t_list)
      if(t.doubleValue() < 0.0 || t.doubleValue() > 1.0)
        throw new SRuntimeException("Erreur SVectorUtil 005 : L'interpolation par le paramètre t = " + t.doubleValue() + " n'est pas définie,car t doit respecter l'inégalité 0 <= t <= 1");
    
    //Vérification de la contrainte t1 + t2 + ... + tn = 1.0
    double total = 0.0;
    
    for(Double t : t_list)
      total += t.doubleValue();
    
    if(total > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 006 : L'interpolation par des paramètre t n'est pas définie, car la somme des paramètres t est supérieur à 1");
    
    //Vérifier que les deux listes ont la même taille
    if(vector_list.size() != t_list.size()+1)
      throw new SRuntimeException("Erreur SVectorUtil 007 : L'interpolation est impossible puisque les deux listes passées en paramètre n'ont les bonnes tailles.");
    
    //Vérification que la liste des vecteurs n'est pas vide
    if(vector_list.isEmpty())
      throw new SRuntimeException("Erreur SVectorUtil 008 : L'interpolation est impossible puisque la liste des vecteurs est vide.");
    
    throw new SNoImplementationException("Erreur SVectorUtil : La méthode n'a pas été implémentée.");
  }
  
}//fin de la classe SVectorUtil
