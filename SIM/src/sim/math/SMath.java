/**
 * 
 */
package sim.math;

import java.util.Arrays;

import sim.exception.SNoImplementationException;

/**
 * La classe <b>SMath</b> contient des méthodes de calcul qui sont complémentaire à la classe java.lang.Math. 
 * Elle perment entre autre de résoudre des polynômes.
 * 
 * @author Simon Vézina
 * @since 2015-02-19
 * @version 2015-12-21
 */
public final class SMath {

	/**
	 * <p>
	 * La constante <b>EPSILON</b> représentante un nombre très petit, mais non nul. Ce chiffre peut être utilisé 
	 * pour comparer une valeur de type double avec le chiffre zéro. Puisqu'un double égale à zéro
	 * est difficile à obtenir numériquement après un calcul (sauf si l'on multiplie par zéro), il est préférable de 
	 * comparer un "pseudo zéro" avec cette constante.
	 * </p>
	 * <p>
	 * Avec une valeur de EPSILON = 1e-10, cette valeur permet de comparer adéquatement des nombres autour de '1' avec suffisamment de chiffres significatifs.
	 * </p>
	 */
	public static double EPSILON = 1e-10;           
	
	/**
   * La constante <b>NEGATIVE_EPSILON</b> représentante un nombre très petit, mais non nul qui est <b>négatif</b>. Ce chiffre peut être utilisé 
   * pour comparer une valeur arbiraire de type double avec le chiffre zéro, mais qui sera négatif. Puisqu'un double égale à zéro
   * est difficile à obtenir numériquement après un calcul (sauf si l'on multiplie par zéro), il est préférable de 
   * comparer un "pseudo zéro" avec cette constante.
   */
	public static double NEGATIVE_EPSILON = -1.0*EPSILON;  //suffisamment grand pour avoir encore des chiffres significatifs dans la précision d'un double
	
	/**
	 * La constante <b>INFINITY</b> représente un nombre positif égale à l'infini. Cette valeur est obtenue à
	 * partir de la classe java.lang.Double.
	 * @see java.lang.Double
	 */
	public static double INFINITY = Double.POSITIVE_INFINITY;
	
	/**
	 * 
	 */
	public static double[] NO_SOLUTION = new double[0];
	
	/**
   * Méthode pour déterminer si deux nombres de type double sont <b>relativement égaux</b>. 
   * En utilisant une approche de calcul de différence, on vérifie si
   * <ul>a - b < EPSILON*ref</ul>  
   * afin de <b>validé l'égalité</b> entre a et b (a == b). EPSILON est un seuil de précision 
   * et ref est une base de référence (la valeur absolue la plus élevée parmis a et b). 
   * <p>Cependant, si les deux chiffres sont inférieurs à EPSILON, ils seront considérés comme égaux.</p>
   * 
   * @param a - Le 1ier nombre à comparer.
   * @param b - Le 2ième nombre à comparer.
   * @return <b>true</b> si les deux nombres sont <b>relativement égaux</b> et <b>false</b> sinon.
   */
	public static boolean nearlyEquals(double a, double b)
	{
	  return nearlyEquals(a, b, EPSILON);
	}
	
	/**
	 * Méthode pour déterminer si deux nombres de type double sont <b>relativement égaux</b>. 
	 * En utilisant une approche de calcul de différence, on vérifie si
	 * <ul>a - b < EPSILON*ref</ul>  
	 * afin de <b>validé l'égalité</b> entre a et b (a == b). EPSILON est un seuil de précision 
	 * et ref est une base de référence (la valeur absolue la plus élevée parmis a et b).
	 * <p>Cenpendant, si les deux chiffres sont inférieurs à EPSILON, ils seront considérés comme égaux.</p>
	 * 
	 * @param a - Le 1ier nombre à comparer.
	 * @param b - Le 2ième nombre à comparer.
	 * @param epsilon - La précision acceptable.
	 * @return <b>true</b> si les deux nombres sont <b>relativement égaux</b> et <b>false</b> sinon.
	 */
	public static boolean nearlyEquals(double a, double b, double epsilon)
	{
	  double absA = Math.abs(a);
    double absB = Math.abs(b);
    double diff = Math.abs(a - b);
    
    if(a == b)                            //Vérification du cas particulier : 0 = 0 et infiny = infiny (mais pas certain ...) 
      return true;
    else 
      if(diff < epsilon)                  //Cas des petites chiffres : Vérifier si les deux chiffres sont très près l'un de l'autre
        return true;
      else                                //Cas général
	    {
        double positive_max;
        
        if(absA > absB)
          positive_max = absA;
        else
          positive_max = absB;
      
        if(diff < positive_max*epsilon)
          return true;
        else
          return false;
      }
	}
	
	/**
	 * Méthode permettant d'évaluer la racine réelle d'un polynôme de degré '1' de la forme
	 * <ul>Ax + B = 0.</ul>
	 * <p>Un polynôme de degré '1' possède au maximum <b>une</b> racine réelle.</p>
	 *  
	 * @param A - Le coefficient devant le terme de puissance '1' (x).
	 * @param B - Le coefficient devant le terme de puissance '0' (1).
	 * @return La racine réelle d'un polynôme de degré '1' (s'il y en a).
	 */
	public static double[] linearRealRoot(double A, double B)
	{
	  // Vérifier si le polynôme n'est pas d'un degré inférieur.
	  // Dans ce cas, la fonction serait constante, donc aucune racine possible.
	  if(A == 0.0)
	    return NO_SOLUTION;
	  else
	  {
	    double s1 = -1*B/A;
	    
	    double[] solution = { s1 };
      
      return solution;
	  }
	}
	
	/**
   * Méthode permettant d'évaluer les racines réelles d'un polynôme de degré '2' de la forme
   * <ul>Ax^2 + Bx + C = 0.</ul>
   * <p>Un polynôme de degré '2' possède au maximum <b>deux</b> racines réelles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '2' (x^2).
   * @param B - Le coefficient devant le terme de puissance '1' (x).
   * @param C - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines réelles de d'un polynôme de degré '2' (s'il y en a). Les solutions retournées dans un tableau sont <b>triées en ordre croissant</b>. 
   */
	public static double[] quadricRealRoot(double A, double B, double C)
	{
	  // Vérifier si le polynôme n'est pas d'un degré inférieur
	  if(A == 0.0)
	    return linearRealRoot(B,C);
	  else
	  {
	    throw new SNoImplementationException("Erreur SMath : C'est méthode n'a pas été implémentée.");
	  }
	  
	}
	
	/**
   * Méthode permettant d'évaluer les racines réelles d'un polynôme de degré '3' de la forme
   * <ul>Ax^3 + Bx^2 + Cx + D = 0.</ul>
   * <p>Un polynôme de degré '3' possède au maximum <b>trois</b> racines réelles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '3' (x^3).
   * @param B - Le coefficient devant le terme de puissance '2' (x^2).
   * @param C - Le coefficient devant le terme de puissance '1' (x).
   * @param D - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines réelles de d'un polynôme de degré '3' (s'il y en a). Les solutions retournées dans un tableau sont <b>triées en ordre croissant</b>. 
   */
	public static double[] cubicRealRoot(double A, double B, double C, double D)
	{
	  // Vérifier si le polynôme n'est pas d'un degré inférieur
	  if(A == 0.0)
	    return quadricRealRoot(B,C,D);
	  else
	  {
	    double a1 = B/A;
	    double a2 = C/A;
	    double a3 = D/A;
	    double Q = (a1*a1 - 3.0*a2)/9.0;
	    double R = (2.0*a1*a1*a1 - 9.0*a1*a2 + 27.0*a3)/54.0;
	    double R2_Q3 = R*R - Q*Q*Q;
	    double theta = 0;

	    // Situation A : trois racines réelles
	    if(R2_Q3 <= 0.0)
	    {
	      theta = Math.acos(R/ Math.sqrt(Q*Q*Q));
	        
	      double s1 = -2.0*Math.sqrt(Q)*Math.cos(theta/3.0) - a1/3.0;
	      double s2 = -2.0*Math.sqrt(Q)*Math.cos((theta+2.0*Math.PI)/3.0) - a1/3.0;
	      double s3 = -2.0*Math.sqrt(Q)*Math.cos((theta+4.0*Math.PI)/3.0) - a1/3.0;
	        
	      double[] solutions = { s1, s2, s3 };
	      
	      // Trier en ordre croissant les solutions
	      Arrays.sort(solutions);
	      	     
	      return solutions;
	    }
	    // Situation B : une racine réelle
	    else
	    {
	      double s1 = Math.pow(Math.sqrt(R2_Q3) + Math.abs(R), 1/3.0);
	      s1 += Q / s1;
	      
	      if(R < 0.0)
	        s1 *= 1;
	      else
	        s1 *= -1;
	        
	      s1 -= a1/3.0;
	        
	      double[] solution = { s1 };
          
	      return solution;
	    }
	  }
	}
	
	/**
   * Méthode permettant d'évaluer les racines réelles d'un polynôme de degré '4' de la forme
   * <ul>Ax^4 + Bx^3 + Cx^2 + Dx + E = 0.</ul>
   * <p>Un polynôme de degré '4' possède au maximum <b>quatre</b> racines réelles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '4' (x^4).
   * @param B - Le coefficient devant le terme de puissance '3' (x^3).
   * @param C - Le coefficient devant le terme de puissance '2' (x^2).
   * @param D - Le coefficient devant le terme de puissance '1' (x).
   * @param E - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines réelles de d'un polynôme de degré '4' (s'il y en a). Les solutions retournées dans un tableau sont <b>triées en ordre croissant</b>. 
   */
	public static double[] quarticRealRoot(double A, double B, double C, double D, double E)
	{
	  // Vérifier si le polynôme n'est pas d'un degré inférieur
	  if(A == 0.0)
	    return cubicRealRoot(B,C,D,E);
	  else
    {
      throw new SNoImplementationException("Erreur SMath 001 : Méthode non implémentée.");
    }
	}
	
}//fin de la classe SMath
