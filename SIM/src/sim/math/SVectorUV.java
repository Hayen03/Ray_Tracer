package sim.math;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe SVectorUV représentante une coordonnée uv associée habituellement à une texture. 
 * Ce vecteur possède une composante u et v et sont <b>habituellement</b> situé dans l'intervalle [0.0 , 1.0].
 * Si ce n'est pas le cas, il est important de les recadrer les coordonnées uv à une valeur dans l'intervalle [0,1].
 * <p>Pour ce faire, il y a deux algorithmes possibles :
 * <ul>Repeat : Effectue une répétition de la coordonnée de droite à gauche et de bas à haut (si dépassement positif).</ul>
 * <ul> Clamp : Coupe la coordonnée après dépassement (u > 1 ==> u = 1, u < 0 ==> u = 0).</ul></p> 
 *  
 * @author Simon Vezina
 * @since 2015-09-15
 * @version 2015-10-24
 */
public class SVectorUV implements SVector {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante 'DEFAULT_COMPONENT' correspond à la coordonnée par défaut d'une composante u et v qui est égale à {@value}.
   */
  private static final double DEFAULT_COMPONENT = 0.0; 
  
  /**
   * La constante 'MIN_VALUE' correspond à la coordonnée minimale d'une composante u et v qui est égale à {@value}.
   * <p>Cependant, une coordonnée uv peut être quand même inférieure à {@value}, mais devra être reconvertie à une valeur supérieure à {@value}
   * avant d'être utilisée pour chercher une information dans une texture. Si tel est le cas, le SVectorUV sera considéré <b>out_of_bound</b>.</p>
   */
  private static final double MIN_VALUE = 0.0; // coordonnée minimale
  
  /**
   * La constante 'MAX_VALUE' correspond à la coordonnée maximale d'une composante u et v qui est égale à {@value}.
   * <p>Cependant, une coordonnée uv peut être quand même supérieure à {@value}, mais devra être reconvertie à une valeur inférieure à {@value}
   * avant d'être utilisée pour chercher une information dans une texture. Si tel est le cas, le SVectorUV sera considéré <b>out_of_bound</b>.</p>
   */
  private static final double MAX_VALUE = 1.0; // coordonnée maximale
  
  /**
   * La constante 'REPEAT' définit le choix de recadrer une coordonnée uv à l'extérieur de l'intervalle [0,1] à l'aide d'une répétition des coordonnées uv.
   * <p>Par exemple : 
   * <ul>si u = 1.23, alors la coordonnée recadrée par répétition donnera u = 0.23.</ul>
   * <ul>si u = -2.8, alors la coordonnée recadrée par répétition donnera u = 0.2.</ul></p>
   */
  public static final int REPEAT = 0;
  
  /**
   * La constante 'CLAMP' définit le choix de recadrer une coordonnée uv à l'extérieur de l'intervalle [0,1] à l'aide d'un arrêt des coordonnées uv.
   * <p>Par exemple : 
   * <ul>si u = 1.23, alors la coordonnée recadrée par répétition donnera u = 1.0</ul>
   * <ul>si u = -2.8, alors la coordonnée recadrée par répétition donnera u = 0.0.</ul></p>
   */
  public static final int CLAMP = 1;
  
  //--------------
  // VARIABLES  //
  //--------------
  
  /**
   * La variable 'u' correspond à la composante u du vecteur uv. Cette coordonnée est selon l'axe x (largeur, width) d'une texture où le (0,0) est situé dans le coin supérieur gauche.
   */
  private final double u;
  
  /**
   * La variable 'v' correspond à la composante v du vecteur uv. Cette coordonnée est selon l'axe y (hauteur, height) d'une texture où le (0,0) est situé dans le coin supérieur gauche.
   */
  private final double v; 

  /**
   * La variable 'is_out_of_bound' détermine si l'une des composantes uv du vecteur est à l'extérieur de l'intervalle [0,1]. 
   * Si tel est le cas, ces coordonnées devront être recadrées.  
   */
  private final boolean is_out_of_bound;
  
  //-----------------------------
  // CONSTRUCTEURS ET MÉTHODES //
  //-----------------------------
  
  /**
   * Constructeur représentant une coordonnée uv à l'origine.
   */
  public SVectorUV()
  {
    this(DEFAULT_COMPONENT, DEFAULT_COMPONENT);
  }

  /**
   * Constructeur d'une coordonnée uv quelconque.
   * 
   * @param u - La composante u de la coordonnée uv.
   * @param v - La composante v de la coordonnée uv.
   */
  public SVectorUV(double u, double v) throws SConstructorException
  {
    if (u < MIN_VALUE || u > MAX_VALUE || v < MIN_VALUE || v > MAX_VALUE)
      is_out_of_bound = true;
    else
      is_out_of_bound = false;
    
    this.u = u;
    this.v = v;
  }

  /**
   * Constructeur d'un vecteur uv en utilisant un string décrivant les paramètres u et v du vecteur uv. 
   * Une lecture autorisée peut prendre la forme suivante : "double u" "double v" (incluant la notation avec , ( ) [ ] comme délimieur dans l'expression du string comme par exemple (2.3, 4.3) ).
   * 
   * @param string - Le string de l'expression du vecteur en paramètres u et v.
   * @throws SReadingException Si le format de la lecture n'est pas adéquat.
   */
  public SVectorUV(String string)throws SReadingException
  {
    double[] tab = read(string);
    
    //Vérification du respect de la contrainte 0 <= u,v <= 1
    if(tab[0] < MIN_VALUE || tab[0] > MAX_VALUE || tab[1] < MIN_VALUE || tab[1] > MAX_VALUE)
      is_out_of_bound = true;
    else
      is_out_of_bound = false;
    
    u = tab[0];
    v = tab[1];
  }
  
  /**
   * Méthode pour déterminer si la coordonnée uv possède une composante dont la valeur est à l'extérieur de l'intervalle [0,1].
   * 
   * @return <b>true</b> s'il y a une composante à l'extérieur de l'intervalle et <b>false</b> sinon.
   */
  public boolean isOutOfBound()
  {
    return is_out_of_bound;
  }
  
  /**
   * Méthode pour obtenir la composante u de la coordonnée uv.
   * 
   * @return La composante u de la coordonnée uv.
   */
  public double getU()
  {
    return u;
  }

  /**
   * Méthode pour obtenir la composante v de la coordonnée uv.
   * 
   * @return La composante v de la coordonnée uv.
   */
  public double getV()
  {
    return v;
  }

  /**
   * Méthode qui permet d'obtenir un vecteur uv cadré dans l'intervalle [0,1] à l'aide de l'algoritme de répétition (REPEAT).
   *
   * @return Le vecteur uv recadré (si nécessaire) par l'algorithme de répétition.
   */
  public SVectorUV getInBound()
  {
    return getInBound(REPEAT);
  }
  
  
  /**
   * Méthode qui permet d'obtenir un vecteur uv cadré dans l'intervalle [0,1] à l'aide d'un algoritme particulier.
   * 
   * @param crop_code - Le code de l'algorithme
   * @return Le vecteur uv recadré (si nécessaire). 
   * @throws SRuntimeException Si le code de recadrement n'est pas reconnu par le système.
   */
  public SVectorUV getInBound(int crop_code) throws SRuntimeException
  {
    if(!is_out_of_bound)
      return this;
    else
      switch(crop_code)
      {
        case REPEAT : return cropRepeat(); 
        
        case CLAMP : return cropClamp();
        
        default : throw new SRuntimeException("Erreur SVectorUV 001 : Le code de recadrement '" + crop_code + "' n'est pas reconnu par le système.");
      }
  }
  
  /**
   * Méthode pour recadrer une coordonnée uv par l'algoritme de répétition (REPEAT).
   * 
   * @return Le vecteur uv après recadrement par coupure (REPEAT).
   */
  private SVectorUV cropRepeat()
  {
    //la coordonnée uv sorte de l'intervale [0,1] positivement ou négativement. 
    //Nous allons rectifier la situation ... par répétition
    //Une coordonnée supérieur à 1 revient dans la texture par répétition par 0
    //Une coordonnée intérieur à 0 revient dans la texture par répétition par 1 
    double repeat_u = u;
    if(repeat_u > 0)
      while(repeat_u > 1.0)
        repeat_u -= 1.0;
    else
      while(repeat_u < 0)
        repeat_u += 1.0;
    
    double repeat_v = v;
    if(repeat_v > 0)
      while(repeat_v > 1.0)
        repeat_v -= 1.0;
    else
      while(repeat_v < 0)
        repeat_v += 1.0;
    
    return new SVectorUV(repeat_u, repeat_v);
  }
  
  /**
   * Méthode pour recadrer une coordonnée uv par l'algoritme de coupure (CLAMP).
   * 
   * @return Le vecteur uv après recadrement par coupure (CLAMP).
   */
  private SVectorUV cropClamp()
  {
    double clamp_u = u;
    
    if(clamp_u < 0)
      clamp_u = 0.0;
    else
      if(clamp_u > 1)
        clamp_u = 1.0;
    
    double clamp_v = v;
    
    if(clamp_v < 0)
      clamp_v = 0.0;
    else
      if(clamp_v > 1)
        clamp_v = 1.0;
    
    return new SVectorUV(clamp_u, clamp_v);
  }
  
  @Override
  public SVector add(SVector v) throws SRuntimeException
  {
    if(v instanceof SVectorUV)
      return add((SVectorUV) v);
    else
      throw new SRuntimeException("Erreur SVectorUV 003 : L'addition du type SVectorUV est défini uniquement avec un objet de type SVectorUV.");
  }
  
  /**
   * Méthode pour effectuer l'addition avec une coordonnée uv.
   * 
   * @param uv - La coordonnée uv à additionner.
   * @return La coordonnée uv résultant de l'addition.
   */
  public SVectorUV add(SVectorUV uv)
  {
    return new SVectorUV(u + uv.u, v + uv.v);
  }
  
  /**
   * Méthode pour effectuer la multiplication par un scalaire d'une constante avec la coordonnée uv lançant l'appel de la méthode.
   * 
   * @param cst - La constante à multiplier.
   * @return La coordonnée uv résultant de la multiplication par un scalaire.
   */
  public SVectorUV multiply(double cst)
  {
    return new SVectorUV(u*cst, v*cst);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "[" + u + ", " + v + "]";    
  }

  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    bw.write(toString());
  }
  
  /**
   * Méthode utilisant un string comme paramètre pour définir les composantes u et v vecteur uv. 
   * Une lecture autorisée peut prendre la forme suivante : "double u" "double v" 
   *  (en incluant la notation avec , ( ) [ ] < > comme délimieur dans l'expression du string comme par exemple (2.3, 4.3) ).
   *  
   * @param string - Le string de l'expression du vecteur en paramètres u et v.
   * @return un tableau de deux éléments tel que u = [0] et v = [1]. 
   * @throws SReadingException Si le format de lecture n'est pas adéquat.
   */
  private double[] read(String string)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(string, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() != 2)
      throw new SReadingException("Erreur SVectorUV 003 : L'expression '" + string + "' ne contient pas exactement 2 paramètres pour les composantes uv du vecteur uv.");
    else
    {
      String s = "";          //String à convertir en double pour les composantes u et v.
      String comp = "";       //Nom de la composante en lecture utilisée pour l'envoie d'une Exception s'il y a lieu.
      
      try
      {
        double[] tab = new double[2]; //Tableau des 2 composantes
        
        comp = "u";
        s = tokens.nextToken();
        tab[0] = Double.valueOf(s);
        
        comp = "v";
        s = tokens.nextToken();
        tab[1] = Double.valueOf(s);
        
        return tab;
        
      }catch(NumberFormatException e){ 
        throw new SReadingException("Erreur SVectorUV 004 : L'expression '" + s +"' n'est pas valide pour définir la composante '" + comp + "' du vecteur en cours.");
      }
    } 
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (is_out_of_bound ? 1231 : 1237);
    long temp;
    temp = Double.doubleToLongBits(u);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(v);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    if (!(obj instanceof SVectorUV)) {
      return false;
    }
    SVectorUV other = (SVectorUV) obj;
    
    if(!SMath.nearlyEquals(u, other.u))
      return false;

    if(!SMath.nearlyEquals(v, other.v))
      return false;
    
    return true;
  }

}// fin de la classe SVectorUV
