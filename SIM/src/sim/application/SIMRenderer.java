package sim.application;

import java.io.FileNotFoundException;
import java.io.IOException;

import sim.application.util.SConfiguration;
import sim.application.util.SConsoleComparator;
import sim.application.util.SConsoleRenderer;
import sim.application.util.SJFrameRenderer;
import sim.exception.SConstructorException;
import sim.exception.SNoImplementationException;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * L'application <b>SIMRenderer</b> permet de représenter une scène en 3d sur une projection d'un écran 2d.
 * 
 * @author Simon Vézina
 * @since 2014-12-28
 * @version 2016-01-14
 */
public class SIMRenderer {

	/**
	 * Lancement de l'application SIMRenderer.
	 * 
	 * @param args
	 */
  public static void main(String[] args) 
  {
    try
    {
      // Lecture du fichier de configuration
      SConfiguration config = new SConfiguration();
      
      // Lancer l'application appropriée
      switch(config.getApplicationType())
      {
        // Lancer aucune application
        case 0 :  SLog.logWriteLine("Message SIMRenderer : Aucune application n'est lancée.");
                  break;
                  
        // Lancer la version "console" de l'application
        case 1 :  SConsoleRenderer.raytrace(config);
                  break;
        
        // Lancer la version "frame" de l'application         
        case 2 :  SJFrameRenderer frame = new SJFrameRenderer();
                  frame.setVisible(true);
                  frame.raytrace(config);
                  break;
        
        // Lancer l'application de comparaison d'image
        case 3 :  SConsoleComparator comparator = new SConsoleComparator(config.getReadDataFileName());
                  comparator.compareImage();
                  comparator.write(config.getWriteDataFileName());
                  break;
                   
        // L'application n'est pas reconnu
        default : SLog.logWriteLine("Message SIMRenderer : Le code de l'application '" + config.getApplicationType() + "' n'est pas reconnu."); 
      }
               
      //Écriture du fichier de configuration
      config.writeFile();    
     
    }catch(FileNotFoundException e){       
      // Exception lancée si le fichier de scène n'est pas trouvé
      SLog.logWriteLine("Message SIMRenderer : Un fichier n'a pas été trouvé." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(SConstructorException e){
      SLog.logWriteLine("Message SIMRenderer : Une erreur lors de la construction d'un objet est survenue."  + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
      e.printStackTrace();
    }catch(SNoImplementationException e){  
      // Exception lancée s'il y a une méthode non implémentée (possible lors d'un labotatoire !!!)
      SLog.logWriteLine("Message SIMRenderer : Une méthode n'a pas été implémentée."  + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();                                          
    }catch(IOException e){                 
      e.printStackTrace(); 
    }
    
    //Fermeture du fichier log  
    try{
      SLog.closeLogFile();
    }catch(IOException e){
      e.printStackTrace();
    }
    
  }
  
}//fin classe SIMRenderer


