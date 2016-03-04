/**
 * 
 */
package sim.math;

import java.util.Arrays;

import sim.exception.SNoImplementationException;

/**
 * La classe <b>SMath</b> contient des m�thodes de calcul qui sont compl�mentaire � la classe java.lang.Math. 
 * Elle perment entre autre de r�soudre des polyn�mes.
 * 
 * @author Simon V�zina
 * @since 2015-02-19
 * @version 2015-12-21
 */
public final class SMath {

	/**
	 * <p>
	 * La constante <b>EPSILON</b> repr�sentante un nombre tr�s petit, mais non nul. Ce chiffre peut �tre utilis� 
	 * pour comparer une valeur de type double avec le chiffre z�ro. Puisqu'un double �gale � z�ro
	 * est difficile � obtenir num�riquement apr�s un calcul (sauf si l'on multiplie par z�ro), il est pr�f�rable de 
	 * comparer un "pseudo z�ro" avec cette constante.
	 * </p>
	 * <p>
	 * Avec une valeur de EPSILON = 1e-10, cette valeur permet de comparer ad�quatement des nombres autour de '1' avec suffisamment de chiffres significatifs.
	 * </p>
	 */
	public static double EPSILON = 1e-10;           
	
	/**
   * La constante <b>NEGATIVE_EPSILON</b> repr�sentante un nombre tr�s petit, mais non nul qui est <b>n�gatif</b>. Ce chiffre peut �tre utilis� 
   * pour comparer une valeur arbiraire de type double avec le chiffre z�ro, mais qui sera n�gatif. Puisqu'un double �gale � z�ro
   * est difficile � obtenir num�riquement apr�s un calcul (sauf si l'on multiplie par z�ro), il est pr�f�rable de 
   * comparer un "pseudo z�ro" avec cette constante.
   */
	public static double NEGATIVE_EPSILON = -1.0*EPSILON;  //suffisamment grand pour avoir encore des chiffres significatifs dans la pr�cision d'un double
	
	/**
	 * La constante <b>INFINITY</b> repr�sente un nombre positif �gale � l'infini. Cette valeur est obtenue �
	 * partir de la classe java.lang.Double.
	 * @see java.lang.Double
	 */
	public static double INFINITY = Double.POSITIVE_INFINITY;
	
	/**
	 * 
	 */
	public static double[] NO_SOLUTION = new double[0];
	
	/**
   * M�thode pour d�terminer si deux nombres de type double sont <b>relativement �gaux</b>. 
   * En utilisant une approche de calcul de diff�rence, on v�rifie si
   * <ul>a - b < EPSILON*ref</ul>  
   * afin de <b>valid� l'�galit�</b> entre a et b (a == b). EPSILON est un seuil de pr�cision 
   * et ref est une base de r�f�rence (la valeur absolue la plus �lev�e parmis a et b). 
   * <p>Cependant, si les deux chiffres sont inf�rieurs � EPSILON, ils seront consid�r�s comme �gaux.</p>
   * 
   * @param a - Le 1ier nombre � comparer.
   * @param b - Le 2i�me nombre � comparer.
   * @return <b>true</b> si les deux nombres sont <b>relativement �gaux</b> et <b>false</b> sinon.
   */
	public static boolean nearlyEquals(double a, double b)
	{
	  return nearlyEquals(a, b, EPSILON);
	}
	
	/**
	 * M�thode pour d�terminer si deux nombres de type double sont <b>relativement �gaux</b>. 
	 * En utilisant une approche de calcul de diff�rence, on v�rifie si
	 * <ul>a - b < EPSILON*ref</ul>  
	 * afin de <b>valid� l'�galit�</b> entre a et b (a == b). EPSILON est un seuil de pr�cision 
	 * et ref est une base de r�f�rence (la valeur absolue la plus �lev�e parmis a et b).
	 * <p>Cenpendant, si les deux chiffres sont inf�rieurs � EPSILON, ils seront consid�r�s comme �gaux.</p>
	 * 
	 * @param a - Le 1ier nombre � comparer.
	 * @param b - Le 2i�me nombre � comparer.
	 * @param epsilon - La pr�cision acceptable.
	 * @return <b>true</b> si les deux nombres sont <b>relativement �gaux</b> et <b>false</b> sinon.
	 */
	public static boolean nearlyEquals(double a, double b, double epsilon)
	{
	  double absA = Math.abs(a);
    double absB = Math.abs(b);
    double diff = Math.abs(a - b);
    
    if(a == b)                            //V�rification du cas particulier : 0 = 0 et infiny = infiny (mais pas certain ...) 
      return true;
    else 
      if(diff < epsilon)                  //Cas des petites chiffres : V�rifier si les deux chiffres sont tr�s pr�s l'un de l'autre
        return true;
      else                                //Cas g�n�ral
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
	 * M�thode permettant d'�valuer la racine r�elle d'un polyn�me de degr� '1' de la forme
	 * <ul>Ax + B = 0.</ul>
	 * <p>Un polyn�me de degr� '1' poss�de au maximum <b>une</b> racine r�elle.</p>
	 *  
	 * @param A - Le coefficient devant le terme de puissance '1' (x).
	 * @param B - Le coefficient devant le terme de puissance '0' (1).
	 * @return La racine r�elle d'un polyn�me de degr� '1' (s'il y en a).
	 */
	public static double[] linearRealRoot(double A, double B)
	{
	  // V�rifier si le polyn�me n'est pas d'un degr� inf�rieur.
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
   * M�thode permettant d'�valuer les racines r�elles d'un polyn�me de degr� '2' de la forme
   * <ul>Ax^2 + Bx + C = 0.</ul>
   * <p>Un polyn�me de degr� '2' poss�de au maximum <b>deux</b> racines r�elles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '2' (x^2).
   * @param B - Le coefficient devant le terme de puissance '1' (x).
   * @param C - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines r�elles de d'un polyn�me de degr� '2' (s'il y en a). Les solutions retourn�es dans un tableau sont <b>tri�es en ordre croissant</b>. 
   */
	public static double[] quadricRealRoot(double A, double B, double C)
	{
	  // V�rifier si le polyn�me n'est pas d'un degr� inf�rieur
	  if(A == 0.0)
	    return linearRealRoot(B,C);
	  else
	  {
	    throw new SNoImplementationException("Erreur SMath : C'est m�thode n'a pas �t� impl�ment�e.");
	  }
	  
	}
	
	/**
   * M�thode permettant d'�valuer les racines r�elles d'un polyn�me de degr� '3' de la forme
   * <ul>Ax^3 + Bx^2 + Cx + D = 0.</ul>
   * <p>Un polyn�me de degr� '3' poss�de au maximum <b>trois</b> racines r�elles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '3' (x^3).
   * @param B - Le coefficient devant le terme de puissance '2' (x^2).
   * @param C - Le coefficient devant le terme de puissance '1' (x).
   * @param D - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines r�elles de d'un polyn�me de degr� '3' (s'il y en a). Les solutions retourn�es dans un tableau sont <b>tri�es en ordre croissant</b>. 
   */
	public static double[] cubicRealRoot(double A, double B, double C, double D)
	{
	  // V�rifier si le polyn�me n'est pas d'un degr� inf�rieur
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

	    // Situation A : trois racines r�elles
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
	    // Situation B : une racine r�elle
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
   * M�thode permettant d'�valuer les racines r�elles d'un polyn�me de degr� '4' de la forme
   * <ul>Ax^4 + Bx^3 + Cx^2 + Dx + E = 0.</ul>
   * <p>Un polyn�me de degr� '4' poss�de au maximum <b>quatre</b> racines r�elles.</p>
   *  
   * @param A - Le coefficient devant le terme de puissance '4' (x^4).
   * @param B - Le coefficient devant le terme de puissance '3' (x^3).
   * @param C - Le coefficient devant le terme de puissance '2' (x^2).
   * @param D - Le coefficient devant le terme de puissance '1' (x).
   * @param E - Le coefficient devant le terme de puissance '0' (1).
   * @return Les racines r�elles de d'un polyn�me de degr� '4' (s'il y en a). Les solutions retourn�es dans un tableau sont <b>tri�es en ordre croissant</b>. 
   */
	public static double[] quarticRealRoot(double A, double B, double C, double D, double E)
	{
	  // V�rifier si le polyn�me n'est pas d'un degr� inf�rieur
	  if(A == 0.0)
	    return cubicRealRoot(B,C,D,E);
	  else
    {
      throw new SNoImplementationException("Erreur SMath 001 : M�thode non impl�ment�e.");
    }
	}
	
}//fin de la classe SMath
