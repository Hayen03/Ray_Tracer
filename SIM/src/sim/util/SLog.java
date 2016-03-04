package sim.util;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SRuntimeException;

/**
 * La classe <b>SLog</b> permet d'afficher des messages vers la console ou vers un fichier .txt afin de garder une trace de l'exécution du programme.
 * 
 * @author Simon Vezina
 * @since 2014-12-18
 * @version 2016-01-14
 */
public class SLog {
	
  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>LOG_EXTENSION</b> correspond à l'extension du fichier de log par défaut étant {@value}.
   */
  private static final String LOG_EXTENSION = "txt";             
  
  /**
	 * La constante <b>DEFAULT_LOG_FILE_NAME</b> correspond au nom du fichier de log par défaut étant {@value}.
	 */
  private static final String DEFAULT_LOG_FILE_NAME = "log" + "." + LOG_EXTENSION; 
	
	//-------------
	// VARIABLES //
	//-------------
	
	/**
	 * La variable <b>log_file_name</b> correspond au nom du fichier avec extension utilisé pour écrire le log.
	 */
	private static String log_file_name = DEFAULT_LOG_FILE_NAME + "." + LOG_EXTENSION;   
	
	/**
	 * La variable <b>isConsoleLogOn</b> détermine si les messages de type log seront redirigés vers la console.
	 */
	private static boolean isConsoleLogOn = true;		
	
	/**
	 * La variable <b>isFileLogOn<b> détermine si si les messages de type log seront redirigés vers un fichier texte en écriture.
	 */
	private static boolean isFileLogOn = true;						        
	
	/**
	 * La variable <b>fw</b> correspond à une référence vers un fichier texte en écriture.
	 */
	private static FileWriter fw;									                 
	
	/**
	 * La variable <b>bw</b> correspond à une référence vers un buffer en écriture dans un fichier texte.
	 */
	private static BufferedWriter bw;		
	
	/**
	 * La variable <b>isLogFileReady</b> déterminer s'il y a eu création d'un fichier log pour l'écriture.
	 */
	private static boolean isLogFileReady = false;					       
	
	//------------
	// MÉTHODES //
	//------------
	
	/**
	 * Méthode pour obtenir le nom du fichier d'écriture du log.
	 * 
	 * @return Le nom du fichier log.
	 */
	public static String getLogFileName()
	{ 
	  return log_file_name;
	}
	
	/**
	 * Méthode pour déterminer si la console sera utilisée comme média d'écriture du log.
	 * 
	 * @param value Détermine si la console sera utilisée comme log.
	 */
	public static void setConsoleLog(boolean value)
	{ 
	  isConsoleLogOn = value;
	}
		
	/**
	 * Méthode pour déterminer si un fichier de type .txt sera utilisé comme média d'écriture du log.
	 * 
	 * @param value Détermine si un fichier sera utilisé comme log.
	 */
	public static void setFileLog(boolean value)
	{ 
	  isFileLogOn = value;
	}
	
	/**
	 * Méthode pour initialiser le nom du fichier log.
	 * 
	 * @param file_name Nom du fichier de sortir en format texte (de type .txt).
	 * @throws SRuntimeException Si le nom de l'extension n'est pas de type approprié.
	 * @throws IOException Si une erreur I/O s'est produit.
	 */
	public static void setLogFileName(String file_name) throws SRuntimeException, IOException
	{
		
	  
	  //------------------------------------------------------------------------------------------------------------
	  //IL Y A UN PETIT PROBLÈME ICI
	  
	  //Si je fais ça de cette façon, le choix de ne pas imprimer dans le fichier ne marchera pas
	  //car je vais quand même en créer un ce que je ne veux pas si on choisit l'option de ne pas en écrire !!!!
	  //------------------------------------------------------------------------------------------------------------
	  
	  //TODO
	  
	  
	  //Vérification que le fichier porte un extension admissible (.txt)		
		String extension = SStringUtil.extensionFileLowerCase(file_name);
		    
		//Vérification si extension != LOG_EXTENSION 
		if(!extension.equals(LOG_EXTENSION))
			throw new SRuntimeException("Erreur SLog 001 : L'extension '" + extension + "' pour le fichier de log n'est pas de type '" + LOG_EXTENSION + "'.");
		
		//Vérifier s'il y au présentement un fichier log en écriture. Si oui, le fermer pour ouvrier l'autre.
		if(isLogFileReady)
			closeLogFile();
			
		//Création du nouveau fichier d'écriture
		log_file_name = file_name;
		
		try{
			fw = new FileWriter(log_file_name);
			bw = new BufferedWriter(fw);
			isLogFileReady = true;
		}catch(IOException ioe){ 
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Méthode pour fermer le fichier de log. Le fichier log sera vide si l'appel n'est pas effectué avant la fermeture de l'application.
	 * 
	 * @throws IOException S'il y a une erreur lors de la fermeture du fichier.
	 */
	public static void closeLogFile() throws IOException
	{
		bw.close();     //  fermer celui-ci en premier, sinon, ERROR !!!
		fw.close();
		
		isLogFileReady = false;
	}
	
	/**
   * Méthode pour écrire dans le log un saut de ligne.
   */
	public static void logWriteLine()
	{
	  logWriteLine("");
	}
	
	/**
	 * Méthode pour écrire un String dans le log en incluant un saut de ligne.
	 * 
	 * @param str - Le String a écrire dans le log.
	 */
	public static void logWriteLine(String str)
	{
	  logWrite(str + SStringUtil.END_LINE_CARACTER);
	}
	
	/**
   * Méthode pour écrire un String dans le log.
   * 
   * @param str - Le String a écrire dans le log.
   */
  public static void logWrite(String str)
  {
    if(isConsoleLogOn)
      System.out.print(str);
    
    if(isFileLogOn)
    { 
      //Vérification qu'il y a eu initialisation du FileWriter, sinon création avec le nom par défaut
      if(!isLogFileReady)
        try{
          setLogFileName(DEFAULT_LOG_FILE_NAME);
        }catch(IOException ioe){
          ioe.printStackTrace();
        }
      
      try{
        bw.write(str);  //écrire dans le buffer
        bw.flush();     //transférer le buffer dans le fichier (ne pas attendre la fermeture du fichier pour débuter l'écriture)
      }catch(IOException ioe){ 
        ioe.printStackTrace();
      }
    }
  }
  	  
	/**
	 * Méthode pour écrire le contenu static de la classe SLog dans un fichier txt.
	 * @param bw - Le buffer pour l'écriture.
	 * @throws IOException Si une erreur de l'objet BufferedWriter est lancée.
	 * @see BufferedWriter
	 */
	public static void staticWrite(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_LOG_FILE_NAME);
		bw.write("\t");
		bw.write(log_file_name);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_LOG_CONSOLE);
		bw.write("\t");
		if(isConsoleLogOn == true)
			bw.write(SAbstractReadable.TRUE_EXPRESSION[0]);
		else
			bw.write(SAbstractReadable.FALSE_EXPRESSION[0]);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_LOG_FILE);
		bw.write("\t\t");
		if(isFileLogOn == true)
			bw.write(SAbstractReadable.TRUE_EXPRESSION[0]);
		else
			bw.write(SAbstractReadable.FALSE_EXPRESSION[0]);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	@Override
	public String toString()
	{
		return "log_file_name = " + log_file_name;
	}
	
}//end SLog
