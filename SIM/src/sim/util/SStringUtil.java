/**
 * 
 */
package sim.util;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * La classe <b>SStringUtil</b> permet d'effectuer quelques opérations pratiques sur un String.
 * 
 * @author Simon Vézina
 * @since 2015-03-21
 * @version 2015-12-02
 */
public class SStringUtil {
  
  /**
   * La constante <b>END_LINE_CARACTER</b> correspond à un caractère de changement de ligne.
   * Il est déterminé à l'aide de l'instruction <i>System.getProperty("line.separator")</i> ce qui permet d'avoir le bon caractère pour la bonne plateforme d'exécution. 
   */
  public static final String END_LINE_CARACTER = System.getProperty("line.separator"); 	
  
  /**
   * La constante <b>REMOVE_CARACTER_TOKENIZER</b> correspond à l'ensemble des caractères à retirer lorsque l'on désire séparé en sous-mot une longue chaîne de caractère.
   * Les caractères sont les suivants : {@value}.
   */
  public static final String REMOVE_CARACTER_TOKENIZER = " \t\n\r\f,()[]<>";
		
  /**
	 * Méthode pour obtenir l'extension du nom du fichier. Ceci correspond aux derniers caractères après le dernier "." dans le nom du fichier.
	 * La valeur retournée sera en caratère minuscule.
	 * 
	 * @param file_name - Le nom du fichier
	 * @return L'extension du fichier en caratères minuscules.
	 */
	public static String extensionFileLowerCase(String file_name)
	{
	  String extension = "";
	  int i = file_name.lastIndexOf('.');
	    
	  if (i > 0)
	    extension = file_name.substring(i+1);
	    
	  return extension.toLowerCase(Locale.ENGLISH);
	}
	
	/**
	 * Méthode pour obtenir le nom d'une fichier sans la référence à des répertoires de localisation inclus dans le nom du fichier.
	 * 
	 * @param file_name - Le nom du fichier (incluant peut-être des répertoires de localisation).
	 * @return Le nom du fichier uniquement.
	 */
	public static String getFileNameWithoutDirectory(String file_name)
	{
	  StringTokenizer tokens = new StringTokenizer(file_name, "/\\");  //faire token avec séparateur '/' et '\'
	  
	  String last_token = null;
	  
	  while(tokens.hasMoreTokens())
	    last_token = tokens.nextToken();
	  
	  return last_token;
	}
	
	/**
	 * Méthode pour compter le nombre de fois qu'un caractère particulier se retrouve dans un string.
	 * @param string - Le string en analyse.
	 * @param caracter - Le caractère à identifier et à compter.
	 * @return Le nombre de fois qu'un caractère précis a été compté dans le string.
	 */
	public static int countCaracterInString(String string, char caracter)
	{
	  int count = 0;
		for(int i = 0; i < string.length(); i++)
		  if(string.charAt(i) == caracter)
		    count++;
		  
		return count;
	}
	  
	/**
	 * Méthode pour faire la jonction entre plusieurs String contenu dans un tableau de String. 
	 * L'expression finale comprendra un caractère d'espacement entre les String. 
	 * @param strings - Le tableau de String.
	 * @param spacer - L'expression d'espacement.
	 * @return L'expression comprenant tous les String du tableau de String.
	 */
	public static String join(String[] strings, String spacer)
	{
	  String expression = "";
	  
	  for(int i = 0; i < strings.length; i++)
	  {
	    if(!strings[i].equals(""))
	      expression = expression + strings[i];
	      
	    if(i < strings.length-1)
	      expression = expression + spacer;
	  }
	    
	  return expression;
	}

	/**
	 * Méthode effectuant la fusions de deux tableaux de String. 
	 * L'ordre des éléments sera la même quand dans les tableaux précédent 
	 * où le dernier élément du tableau 1 sera suivit par le 1ier élément du tableau 2.
	 *
	 * @param strings1 - Le 1ier tableau de String à fusionner.
	 * @param strings2 - Le 2ième tableau de String à fusionner.
	 * @return Un tableau de String fusionné.
	 */
	public static String[] merge(String[] strings1, String[] strings2)
	{
	  String[] result = new String[strings1.length + strings2.length];
    
    // Mettre les éléments du 1ier tableau dans le tableau résultant
    for(int i = 0; i < strings1.length; i++)
      result[i] = strings1[i];
    
    // Mettre les éléments du 2ier tableau dans le tableau résultant
    for(int i = 0; i < strings2.length; i++)
      result[strings1.length + i] = strings2[i];
    
    return result;
	}
	
	/**
	 * Méthode pour générer un String en retirant l'ensemble des caratères d'espacement
	 * situés au début du String passé en paramètre.
	 *   
	 * @param string - Le mot dont l'on désirer retirer les premiers caractères d'espacement.
	 * @return Le mot sans les premiers caractères d'espacement.
	 */
  public static String removeAllFirstSpacerCaracter(String string)
	{
	  if(string == null)
	    return string;
	  else
	  {
	    // Retirer les 1ier caractères uniquement faisant parti de la liste
	    while(string.length() > 0)
	      switch(string.charAt(0))
	      {
	        case ' '  : 
	        case '\t' : 
	        case '\n' :
	        case '\r' : 
	        case '\f' : string = string.substring(1); break;
	          	          
	        default : return string;
	      }
	      
	    return string;   //le caractère sera alors ""
	  }
	}
	  
}//fin de la classe SStringUtil
