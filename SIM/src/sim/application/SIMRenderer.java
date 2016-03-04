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
 * L'application <b>SIMRenderer</b> permet de repr�senter une sc�ne en 3d sur une projection d'un �cran 2d.
 * 
 * @author Simon V�zina
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
      
      // Lancer l'application appropri�e
      switch(config.getApplicationType())
      {
        // Lancer aucune application
        case 0 :  SLog.logWriteLine("Message SIMRenderer : Aucune application n'est lanc�e.");
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
               
      //�criture du fichier de configuration
      config.writeFile();    
     
    }catch(FileNotFoundException e){       
      // Exception lanc�e si le fichier de sc�ne n'est pas trouv�
      SLog.logWriteLine("Message SIMRenderer : Un fichier n'a pas �t� trouv�." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(SConstructorException e){
      SLog.logWriteLine("Message SIMRenderer : Une erreur lors de la construction d'un objet est survenue."  + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
      e.printStackTrace();
    }catch(SNoImplementationException e){  
      // Exception lanc�e s'il y a une m�thode non impl�ment�e (possible lors d'un labotatoire !!!)
      SLog.logWriteLine("Message SIMRenderer : Une m�thode n'a pas �t� impl�ment�e."  + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
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


