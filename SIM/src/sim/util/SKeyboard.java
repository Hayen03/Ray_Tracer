package sim.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * La classe <b>SKeyboard</b> permet la lecture d'une donnée à l'aide du clavier.
 * 
 * @author Simon Vezina
 * @version 2014-12-06
 * @version 2016-01-13
 */
public class SKeyboard
{
  /**
   * La constante <b>ERROR_MESSAGE</b> correspond au message d'erreur associé à une erreur de l'utilisation de la classe.
   */
  private static final String ERROR_MESSAGE = "Erreur de lecture! Recommencez! ";

  /**
   *  Méthode pour lire un String au clavier a partir d'une console.
   *  
   *  @return La valeur lue au clavier.
   */
  public static String readString()
  {
    String  entree = null;
    boolean done = false;
    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    while( !done )
    {
      try{
      entree = keyboard.readLine();
      done = true;
      }catch(Exception e){ System.out.print(ERROR_MESSAGE); }
    }
    return entree;
  }//fin readString

  /**
   *  Méthode pour lire un int au clavier a partir d'une console.
   *  
   *  @return La valeur lue au clavier.
   */
  public static int readInt()
  {
    String  entree = null;
    int sortie = 0;
    boolean done = false;
    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    while( !done )
    {
      try{
      entree = keyboard.readLine();
      sortie = Integer.parseInt(entree);
      done = true;
      }catch(Exception e){ System.out.print(ERROR_MESSAGE); }
    }

    return sortie;
  }//fin readInt

  /**
   *  Méthode pour lire un float au clavier a partir d'une console.
   *  
   *  @return La valeur lue au clavier.
   */
  public static float readFloat()
  {
    String  entree = null;
    float sortie = 0;
    boolean done = false;
    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    while( !done )
    {
      try{
      entree = keyboard.readLine();
      sortie = Float.parseFloat(entree);
      done = true;
      }catch(Exception e){ System.out.print(ERROR_MESSAGE); }
    }

    return sortie;
  }//fin readFloat

  
 
}//end SKeyboard