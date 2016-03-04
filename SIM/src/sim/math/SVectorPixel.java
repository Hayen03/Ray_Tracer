/**
 * 
 */
package sim.math;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;

/**
 * La classe SVectorPixel représente un vecteur pour positionner un pixel dans un viewport.
 * 
 * @author Simon Vézina
 * @since 2014-12-27
 * @version 2015-10-24
 */
public class SVectorPixel implements SVector {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante 'DEFAULT_COMPONENT' correspond à la coordonnée par défaut des composantes x et y du pixel qui est égale à {@value}. 
   */
  private final static int DEFAULT_COMPONENT = 0;
  
  //--------------
  // VARIABLES  //
  //--------------
  
	/**
	 * La variable 'x' correspond à la coordonnée x du pixel.
	 */
  private final int x;		
  
  /**
   * La variable 'y' correspond à la coordonnée y du pixel.
   */
	private final int y;		
	
	//-----------------------------
	// CONSTRUCTEURS ET MÉTHODES //
	//-----------------------------
	
	/**
	 * Constructeur d'un pixel par défaut. Ce vecteur correspond à l'origine d'un viewport étant à la coordonnée (0,0).
	 * Dans la grande majorité des viewports, cette coordonnée correspond géométriquement au coint supérieur gauche. 
	 */
	public SVectorPixel()
	{
		this(DEFAULT_COMPONENT, DEFAULT_COMPONENT);
	}
	
	/**
	 * Constructeur d'un vecteur de pixel.
	 * 
	 * @param x - La coordonnée x du pixel.
	 * @param y - La coordonnée y du pixel.
	 * @throws SConstructorException Si le pixel possède une coordonnée négative.
	 */
	public SVectorPixel(int x, int y) throws SConstructorException
	{
	  //Vérification des coordonnées afin qu'elles soient positives
	  if(x < 0)
	    throw new SConstructorException("Erreur SVectorPixel 001 : La coordonnée x = " + x + " ne peut pas être négative.");
		
	  if(y < 0)
	    throw new SConstructorException("Erreur SVectorPixel 002 : La coordonnée y = " + y + " ne peut pas être négative.");
			
	  this.x = x;
	  this.y = y;
	}

	/**
	 * Méthode pour obtenir la coordonnée x du pixel.
	 * 
	 * @return La coordonnée x du pixel.
	 */
	public int getX()
	{ 
	  return x;
	}
	
	/**
	 * Méthode pour obtenir la coordonnée y du pixel.
	 * 
	 * @return La coordonnée y du pixel.
	 */
	public int getY()
	{ 
	  return y;
	}
	
	@Override
	public SVector add(SVector v) throws SRuntimeException
	{
		if(v instanceof SVectorPixel)
		  return add((SVectorPixel)v);
		else
		  throw new SRuntimeException("Erreur SVectorPixel 003 : L'addition d'un type SVectorPixel n'est possible qu'avec un objet de type SVectorPixel.");
	}

	/**
	 * Méthode pour effectuer l'addition d'une coordonnée de pixel avec une autre.
	 * 
	 * @param p - La coordonnée de pixel à additionner.
	 * @return La coordonnée de pixel résultant de l'addition d'une autre coordonnée de pixel.
	 */
	public SVectorPixel add(SVectorPixel p)
	{
	  return new SVectorPixel(x + p.x, y + p.y);
	}
	
	/**
	 * Méthode pour effectuer la multiplication par un scalaire avec une coordonnée de pixel. Si la constante est négative,
	 * une exception de type SConstrutorException sera lancée puisqu'une coordonnée de pixel ne peut pas contenir de composante négative.
	 * 
	 * @param cst - La constante à multiplier avec la coordonnée de pixel.
	 * @return La coordonnée de pixel résultant de la multiplication par un scalaire.
	 * @throws SConstructorException Si la constante à multiplier est négative ne permettant pas de construire une coordonnée de pixel avec des composantes négatives.
	 */
	public SVectorPixel multiply(double cst) throws SConstructorException
	{
	  return new SVectorPixel((int)(x*cst), (int)(y*cst));
	}

	/* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    
    if (obj == null) {
      return false;
    }
    
    if (!(obj instanceof SVectorPixel)) {
      return false;
    }
    
    SVectorPixel other = (SVectorPixel) obj;
    
    if (x != other.x) 
      return false;
    
    if (y != other.y) 
      return false;
    
    return true;
  }

  @Override
  public String toString()
  { 
    return new String("[" + x + ", " + y + "]");
  }
	
  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(toString());
  }
	
}//fin de la classe SVectorPixel
