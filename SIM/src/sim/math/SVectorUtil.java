/**
 * 
 */
package sim.math;

import java.util.List;

import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;

/**
 * La classe <b>SVectorUtil</b> est une classe utilitaire permettant d'effectuer des op�rations sur des objet h�ritant de <b>SVector</b>.
 * 
 * @author Simon V�zina
 * @since 2015-11-03
 * @version 2015-12-21
 */
public class SVectorUtil {

  /**
   * <p>M�thode effectuant le calcul de l'interpolation lin�aire entre deux vecteurs v0 et v1 par le facteur t.
   * L'�quation math�matique correspondant � l'interpolation est
   * <ul> v = v0*(1 - t) + v1*t</ul>
   * o� v0 est le vecteur de r�f�rence et v1 est le vecteur pond�r� par le facteur t.</p>
   * <p>De plus, l'interpolation impose la contrainte
   * <ul>0 <= t <= 1</ul>
   * sur le facteur t.</p>
   * 
   * @param v0 - Le 1ier vecteur de r�f�rence pond�r� par 1 - t.
   * @param v1 - Le 2i�me vecteur pond�r� par le facteur t.
   * @param t - Le param�tre de pond�ration.
   * @return Le vecteur interpol�.
   * @throws SRuntimeException Si la contrainte sur t n'est pas respect�e (0 <= t <= 1).
   */
  public static SVector linearInterpolation(SVector v0, SVector v1, double t)throws SRuntimeException
  {
    //Doit satisfaire la contrainte sur t
    if(t < 0.0 || t > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 001 : L'interpolation par le param�tre t = " + t + " n'est pas d�finie,car t doit respecter l'in�galit� 0 <= t <= 1");
    
    throw new SNoImplementationException("Erreur SVectorUtil : La m�thode n'a pas �t� impl�ment�e.");
  }
  
  /**
   * <p>M�thode effectant le calcul de l'interpolation lin�aire en coordonn�e barycentrique entre trois vecteurs v0, v1 et v2 par le facteur t1 et t2.
   * <p>L'�quation math�matique correspondant � l'interpolation est
   * <ul> v = v0*(1 - t1 - t2) + v1*t1 + v2*t2</ul>  
   * o� v0 est le vecteur de r�f�rence et v1 est le vecteur pond�r� par le facteur t1 
   * et v2 est le vecteur pond�r� par le facteur t2.</p>
   * <p>De plus, l'interpolation impose les contraintes
   * <ul>0 <= t1 <= 1 , 0 <= t2 <= 1 et t1 + t2 <= 1</ul>
   * sur le facteur t1 et t2.</p>
   * 
   * @param v0 - Le 1ier vecteur de r�f�rence pond�r� par 1 - t1 - t2.
   * @param v1 - Le 2i�me vecteur pond�r� par t1.
   * @param v2 - Le 3i�me vecteur pond�r� par t2.
   * @param t1 - Le 1ier param�tre de pond�ration.
   * @param t2 - Le 2i�me param�tre de pond�ration.
   * @return Le vecteur interpol�.
   * @throws SRuntimeException Si les contraintes sur t1 et t2 ne sont pas respect�es (0 <= t1 <= 1, 0 <= t2 <= 1 et t1 + t2 <= 1).  
   */
  public static SVector linearBarycentricInterpolation(SVector v0, SVector v1, SVector v2, double t1, double t2)throws SRuntimeException
  {
    //Doit satisfaire la contrainte sur t1 (0 <= t1 <= 1)
    if(t1 < 0.0 || t1 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 002 : L'interpolation par le param�tre t1 = " + t1 + " n'est pas d�finie,car t1 doit respecter l'in�galit� 0 <= t1 <= 1");
   
    //Doit satisfaire la contrainte sur t2 (0 <= t2 <= 1)
    if(t2 < 0.0 || t2 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 003 : L'interpolation par le param�tre t2 = " + t2 + " n'est pas d�finie,car t1 doit respecter l'in�galit� 0 <= t2 <= 1");
   
    //Doit satisfaire la contrainte sur t1 et t2 (t1 + t2 <= 1)
    if(t1 + t2 > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 004 : L'interpolation par les param�tre t1 = " + t1 + " et t2 = " + t2 + " n'est pas d�finie,car t1 et t2 ne respecter l'in�galit� t1 + t2 <= 1");
   
    throw new SNoImplementationException("Erreur SVectorUtil : La m�thode n'a pas �t� impl�ment�e.");
  }
  
  /**
   * <p>M�thode effectant le calcul de l'interpolation lin�aire en coordonn�e barycentrique entre plusieurs vecteurs v0, v1, ... et v_n par les facteur t1, t2, ..., t_n.
   * <p>L'�quation math�matique correspondant � l'interpolation est
   * <ul> v = v0*(1 - t1 - t2 - ... - t_n) + v1*t1 + v2*t2 + ... + v_n*t_n</ul>  
   * o� v0 est le vecteur de r�f�rence et les vecteur v_i sont les vecteurs pond�r�s par les facteurs t_i.</p>
   * <p>De plus, l'interpolation impose les contraintes
   * <ul>0 <= t_i <= 1 , t1 + t2 + ... + t_n <= 1</ul>
   * sur tous les facteurs t_i.</p>
   * 
   * @param vector_list - La liste des vecteurs dans l'interpolation. Cette liste contient n+1 vecteur (v0 et les v_i �tant de nombre n).
   * @param t_list - La liste des facteurs de pond�rations des vecteurs. Cette liste contient n facteurs (les n facteurs des n vecteur v_i).
   * @return Le vecteur interpol�.
   * @throws SRuntimeException Si les contraintes sur t_i ne sont pas respect�es (0 <= t_i <= 1 et t1 + t2 + ... + t_n <= 1). 
   * @throws SRuntimeException Si le nombre d'�l�ment des listes n'est pas ad�quat (vector_list.size() != t_list.size()+1). 
   
   */
  public static SVector linearBarycentricInterpolation(List<SVector> vector_list, List<Double> t_list) throws SRuntimeException
  {
    //V�rification de la contrainte sur t (0 <= t <= 1) pour l'ensemble des t
    for(Double t : t_list)
      if(t.doubleValue() < 0.0 || t.doubleValue() > 1.0)
        throw new SRuntimeException("Erreur SVectorUtil 005 : L'interpolation par le param�tre t = " + t.doubleValue() + " n'est pas d�finie,car t doit respecter l'in�galit� 0 <= t <= 1");
    
    //V�rification de la contrainte t1 + t2 + ... + tn = 1.0
    double total = 0.0;
    
    for(Double t : t_list)
      total += t.doubleValue();
    
    if(total > 1.0)
      throw new SRuntimeException("Erreur SVectorUtil 006 : L'interpolation par des param�tre t n'est pas d�finie, car la somme des param�tres t est sup�rieur � 1");
    
    //V�rifier que les deux listes ont la m�me taille
    if(vector_list.size() != t_list.size()+1)
      throw new SRuntimeException("Erreur SVectorUtil 007 : L'interpolation est impossible puisque les deux listes pass�es en param�tre n'ont les bonnes tailles.");
    
    //V�rification que la liste des vecteurs n'est pas vide
    if(vector_list.isEmpty())
      throw new SRuntimeException("Erreur SVectorUtil 008 : L'interpolation est impossible puisque la liste des vecteurs est vide.");
    
    throw new SNoImplementationException("Erreur SVectorUtil : La m�thode n'a pas �t� impl�ment�e.");
  }
  
}//fin de la classe SVectorUtil
