package sim.math;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import sim.exception.SRuntimeException;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe SVector4d représente un vecteur en 4 dimension xyzt pouvant effectuer des opérations mathématiques.
 * 
 * @author Simon Vezina
 * @since 2015-02-14
 * @version 2015-10-24
 */
public class SVector4d implements SVector {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante 'DEFAULT_COMPONENT' correspond à la composante par défaut des variables x,y et z étant égale à {@value}.
   */
	final private static double DEFAULT_COMPONENT = 0.0;
	
	/**
	 * La constante 'DEFAULT_T' correspond à la composante par défaut de la variable t étant égale à {@value}.
	 */
	final private static double DEFAULT_T = 1.0;
	
	//-------------
	// VARIABLES //
	//-------------
	
	/**
	 * La variable 'x' correspond à la composante x du vecteur 4d.
	 */
	final private double x;	
	
	/**
   * La variable 'y' correspond à la composante y du vecteur 4d.
   */
	final private double y;	
	
	/**
   * La variable 'z' correspond à la composante z du vecteur 4d.
   */
	final private double z;	
	
	/**
   * La variable 't' correspond à la composante t du vecteur 4d.
   */
	final private double t;	
	
	//-----------------------------
	// CONSTRUCTEURS ET MÉTHODES //
	//-----------------------------
	
	/**
	 * Constructeur d'un vecteur 4d à l'origine d'un système d'axe <i>xyz</i> dont <i>t</i> = 1.0.
	 */
	public SVector4d()
	{
		this(DEFAULT_COMPONENT, DEFAULT_COMPONENT, DEFAULT_COMPONENT);
	}
	
	/**
	 * Constructeur d'un vecteur 4d à partir d'un vecteur 3d dont la 4ième dimension <i>t</i> sera égale à 1.0.
	 * @param v - Le vecteur 3d.
	 */
	public SVector4d(SVector3d v)
	{
	  this(v.getX(), v.getY(), v.getZ());
	}
	
	/**
	 * Constructeur d'un vecteur 4d à la position xyz dont t = 1.0.
	 * @param x - La composante <i>x</i> du vecteur.
	 * @param y - La composante <i>y</i> du vecteur.
	 * @param z - La composante <i>z</i> du vecteur.
	 */
	public SVector4d(double x, double y, double z)
	{
		this(x,y,z,DEFAULT_T);
	}
	
	/**
	 * Constructeur avec composante x,y,z et t du vecteur 4d.
	 * @param x - La composante <i>x</i> du vecteur.
	 * @param y - La composante <i>y</i> du vecteur.
	 * @param z - La composante <i>z</i> du vecteur.
	 * @param t - La composante <i>t</i> du vecteur.
	 */
	public SVector4d(double x, double y, double z, double t)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}
	
	/**
	 * Constructeur d'un vecteur 4d en utilisant un string décrivant les paramètres x, y, z et t du vecteur. 
	 * Une lecture autorisée peut prendre la forme suivante : "double x" "double y" "double z" "double t" (incluant la notation avec , ( ) [ ] comme délimieur dans l'expression du string comme par exemple (2.3, 4.3, 5.7, 1.0) ).
	 * @param string - Le string de l'expression du vecteur en paramètres x, y, z et t.
	 * @throws SReadingException Si le format de la lecture n'est pas adéquat.
	 */
	public SVector4d(String string)throws SReadingException
	{
		double[] tab = read(string);
		
		x = tab[0];
		y = tab[1];
		z = tab[2];
		t = tab[3];
	}
	
	/**
	 * Méthode qui donne accès à la coordonnée x du vecteur.
	 * @return La coordonnée x.
	 */
	public double getX()
	{ 
	  return x;
	}
	
	/**
	 * Méthode qui donne accès à la coordonnée y du vecteur.
	 * @return La coordonnée y.
	 */
	public double getY()
	{ 
	  return y;
	}
	
	/**
	 * Méthode qui donne accès à la coordonnée z du vecteur.
	 * @return La coordonnée z.
	 */
	public double getZ()
	{ 
	  return z;
	}
	
	/**
	 * Méthode qui donne accès à la coordonnée t du vecteur.
	 * @return La coordonnée t.
	 */
	public double getT()
	{ 
	  return t;
	}
	
	/**
	 * Méthode pour obtenir un vecteur 3d à partir des composantes <i>x</i>,<i>y</i> et <i>z</i> du vecteur 4d.
	 * La composante <i>t</i> est alors écartée.
	 * @return
	 */
	public SVector3d getSVector3d()
	{
	  return new SVector3d(x, y, z);
	}
	
	 @Override
	 public SVector add(SVector v) throws SRuntimeException
	 {
	   if(v instanceof SVector4d)
	     return add((SVector4d) v);
	   else
	     throw new SRuntimeException("Erreur SVector4d ??? : L'addition d'un type SVector4d est défini uniquement avec un objet de type SVector4d.");
	 }
	 
	/**
	 * Méthode qui retourne <b>l'addition</b> de deux vecteurs. 
	 * @param v - Le vecteur à ajouter au vecteur présent.
	 * @return La somme des deux vecteurs.
	 */
	public SVector4d add(SVector4d v)
	{	
		return new SVector4d(x + v.x, y + v.y, z + v.z, t + v.t);
	}
	
	/**
	 * Méthode qui fait <b>l'addidion</b> de deux vecteurs en 4 dimensions.
	 * @param v1 - Le première vecteur à additionner.
	 * @param v2 - Le deuxième vecteur à additionner. 
	 * @return La somme des deux vecteurs.
	 */
	public static SVector4d add(SVector4d v1, SVector4d v2)
	{ 
	  return v1.add(v2);
	}
	
	/**
	 * Méthode qui retourne la <b>soustraction</b> de deux vecteurs. 
	 * @param v - Le vecteur à soustraire au vecteur présent.
	 * @return La soustraction des deux vecteurs.
	 */
	public SVector4d substract(SVector4d v)
	{
		return new SVector4d(x - v.x, y - v.y, z - v.z, t - v.t);
	}
	
	/**
	 * Méthode qui fait la <b>soustraction</b> de deux vecteurs en 4 dimensions.
	 * Il est important de respecter l'ordre des vecteurs, car A-B = -(B-A). 
	 * @param v1 - Le première vecteur.
	 * @param v2 - Le deuxième vecteur à soustraire. 
	 * @return La soustraction des deux vecteurs.
	 */
	public static SVector4d substract(SVector4d v1, SVector4d v2){ return v1.substract(v2); }
	
	/**
	 * Méthode qui effectue la <b>multiplication par une scalaire</b> sur un vecteur.
	 * @param m - Le muliplicateur.
	 * @return Le résultat de la multiplication par un scalaire m sur le vecteur.
	 */
	public SVector4d multiply(double m)
	{
		return new SVector4d(m*x, m*y, m*z, m*t);
	}
	
	/**
	 * Méthode qui effectue la <b>multiplication par une scalaire</b> m sur un vecteur v. 
	 * @param v - Le vecteur à multiplier par un scalaire.
	 * @param m - Le multiplicateur.
	 * @return Une référence sur l'objet couleur après une modification de multiplication.
	 */
	public static SVector4d multiply(SVector4d v, double m)
	{ 
	  return v.multiply(m);
	}
	
	/**
	 * Méthode pour obtenir le <b>module</b> d'un vecteur.
	 * @return Le module du vecteur.
	 */
	public double modulus()
	{
		return Math.sqrt((x*x) + (y*y) + (z*z) + (t*t));
	}
	
	/**
	 * Méthode pour obtenir <b>le module</b> d'un vecteur.
	 * @return Le module du vecteur.
	 */
	public double module()
	{ 
	  return modulus();
	}
	
	/**
	 * Méthode pour <b> normaliser </b> un vecteur à trois dimensions. 
	 * Un vecteur normalisé possède la même orientation, mais possède une <b>longeur unitaire</b>.
	 * Si le <b> module du vecteur est nul </b>, le vecteur normalisé sera le <b>vecteur nul</b> (0.0, 0.0, 0.0).
	 * @return Le vecteur normalisé.
	 */
	public SVector4d normalize()
	{
	  double mod = modulus();     //obtenir le module du vecteur
    
    //Vérification du module. S'il est trop petit, nous ne pouvons pas numériquement normaliser ce vecteur
    if(mod < SMath.EPSILON)
      throw new SRuntimeException("Erreur SVector4d 001 : Le vecteur " + this.toString() + " étant nul ou près que nul ne peut pas être numériquement normalisé.");
    else
      return new SVector4d(x/mod, y/mod, z/mod, t/mod);
	}
	
	/**
	 * Méthode pour effectuer le <b>produit scalaire</b> avec un autre vecteur v.
	 * @param v - L'autre vecteur en produit scalaire.
	 * @return Le produit scalaire entre les deux vecteurs.
	 */
	public double dot(SVector4d v)
	{
		return (x*v.getX() + y*v.getY() + z*v.getZ() + t*v.getT());
	}
	
	/**
	 * Méthode pour effectuer le <b>produit scalaire</b> entre un vecteur v1 et un vecteur v2.
	 * @param v1 - Le premier vecteur.
	 * @param v2 - Le deuxième vecteur.
	 * @return Le produit scalaire entre les deux vecteurs.
	 */
	public static double dot(SVector4d v1, SVector4d v2)
	{ 
	  return v1.dot(v2);
	}
		
	/**
	 * Méthode pour effectuer le <b>produit scalaire</b> avec un autre vecteur v.
	 * @param v - L'autre vecteur en produit scalaire.
	 * @return Le produit scalaire entre les deux vecteurs.
	 */
	public double produitScalaire(SVector4d v)
	{ 
	  return dot(v);
	}
	
	/**
	 * Méthode pour effectuer le <b>produit scalaire</b> entre un vecteur v1 et un vecteur v2.
	 * @param v1 - Le premier vecteur.
	 * @param v2 - Le deuxième vecteur.
	 * @return Le produit scalaire entre les deux vecteurs.
	 */
	public static double produitScalaire(SVector4d v1, SVector4d v2)
	{ 
	  return dot(v1, v2);
	}
	
	/**
	 * Méthode utilisant un string comme paramètre pour définir les composantes x, y, z et t du vecteur. 
	 * Une lecture autorisée peut prendre la forme suivante : "double x" "double y" "double z" "double t"
	 *  (en incluant la notation avec , ( ) [ ] < > comme délimieur dans l'expression du string comme par exemple (2.3, 4.3, 5.7, 1.0) ).
	 * @param string - Le string de l'expression du vecteur en paramètres x, y, et z.
	 * @return un tableau de trois éléments tel que x = [0], y = [1], z = [2] et t = [3]. 
	 * @throws SReadingException Si le format de lecture n'est pas adéquat.
	 */
	private double[] read(String string)throws SReadingException
	{
		StringTokenizer tokens = new StringTokenizer(string, SStringUtil.REMOVE_CARACTER_TOKENIZER);
		
		if(tokens.countTokens() != 4)
			throw new SReadingException("Erreur SVector4d 002 : L'expression '" + string + "' ne contient pas exactement 4 paramètres pour les composantes xyzt du vecteur 4d.");
		else
		{
			String s = "";					//String à convertir en double pour les composantes x, y et z.
			String comp = "";				//Nom de la composante en lecture utilisé pour l'envoie d'une Exception s'il y a lieu.
			
			try
			{
				double[] tab = new double[4];	//Tableau des 4 composantes
				
				comp = "x";
				s = tokens.nextToken();
				tab[0] = Double.valueOf(s);
				
				comp = "y";
				s = tokens.nextToken();
				tab[1] = Double.valueOf(s);
				
				comp = "z";
				s = tokens.nextToken();
				tab[2] = Double.valueOf(s);
				
				comp = "t";
				s = tokens.nextToken();
				tab[3] = Double.valueOf(s);
				
				return tab;
				
			}catch(NumberFormatException e){ 
				throw new SReadingException("Erreur SVector4d 003 : L'expression '" + s +"' n'est pas valide pour définir la composante '" + comp + "' du vecteur en cours.");
			}
		}	
	}
	
	@Override
	public void write(BufferedWriter bw)throws IOException
	{
		bw.write(toString());
	}
	
	@Override
	public String toString()
	{
		return "[" + x + ", " + y + ", " + z + ", " + t + "]";		
	}
	
	/* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(t);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    
    if(obj == null)
      return false;
    
    if(!(obj instanceof SVector4d))
      return false;
    
    SVector4d other = (SVector4d) obj;
    
    //Comparaison des valeurs x,y,z et t en utilisant les méthode de la classe SMath
    if(!SMath.nearlyEquals(x, other.x))
      return false;
    
    if(!SMath.nearlyEquals(y, other.y))
      return false;
    
    if(!SMath.nearlyEquals(z, other.z))
      return false;
    
    if(!SMath.nearlyEquals(t, other.t))
      return false;
          
    return true;
  }
  
}//fin de la classe SVector4d
