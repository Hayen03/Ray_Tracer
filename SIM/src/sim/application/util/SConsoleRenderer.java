/**
 * 
 */
package sim.application.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.geometry.space.SAbstractGeometrySpace;
import sim.graphics.SRaytracer;
import sim.graphics.SScene;
import sim.util.SChronometer;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe <b>SConsoleRenderer</b> représente une console permettant de générer une image par 
 * calcul de <i>ray tracing</i> à partir d'un fichier de configuration. 
 * 
 * @author Simon Vézina
 * @since 2015-08-23
 * @version 2016-01-01
 */
public class SConsoleRenderer {

  /**
   * Méthode pour lancer l'application du ray tracer en format "console".
   * @param config - La configuration de l'application.
   */
  public static void raytrace(SConfiguration config)
  {
    try
    {
      SChronometer chrono = new SChronometer();
      
      //Lecture de la scène
      chrono.start();
      SLog.logWriteLine("Message SConsoleRenderer : Lecture de la scène '" + config.getReadDataFileName() + "'.");
      
      SScene scene = new SScene(config.getReadDataFileName());
        
      chrono.stop();
      SLog.logWriteLine("Message SConsoleRenderer : Fin de la lecture de la scène.");
      SLog.logWriteLine("Message SConsoleRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("");
      
      // Début du ray tracing
      SLog.logWriteLine("Message SConsoleRenderer : Début du raytracing.");
      
      chrono.start();
      
      // Construction du raytracer
      SRaytracer raytracer = scene.buildRaytracer();
      
      // Afficher une ligne de "-" afin de mieux visualiser la progression du ray tracing
      for(int i=0; i<101; i++)
        SLog.logWrite("_");
      SLog.logWriteLine();
      
      // Effectuer le ray tracing avec affichage "." à chaque 1% complété
      int pixels = raytracer.nbPixels();
      int pack = pixels / 100;
      
      for(int i=0; i<101; i++)
      {
        //Calcul du ray tracing
        raytracer.raytrace(pack);  //calcul pour une groupe de 1% des pixels
        
        SLog.logWrite(".");        //écriture du caractère "." à chaque 1% de complété
      }
      
      chrono.stop();
      
      SLog.logWriteLine();
      SLog.logWriteLine();
      SLog.logWriteLine("Message SConsoleRenderer : Fin du raytracing.");
      SLog.logWriteLine("Message SConsoleRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("Message SConsoleRenderer : Nombre de tests d'intersection réalisés : " + SAbstractGeometrySpace.getIntersectionTestCount() + " tests.");
      SLog.logWriteLine();
      
      //Écriture de l'image
      chrono.start();
      SLog.logWriteLine("Message SConsoleRenderer : Écriture de l'image.");
      
      scene.getViewport().writeImage();
      
      chrono.stop();
      SLog.logWriteLine("Message SConsoleRenderer : Fin de l'écriture de l'image.");
      SLog.logWriteLine("Message SConsoleRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("");
      
      //Écriture de la scène
      scene.write(config.getWriteDataFileName());
    
    }catch(FileNotFoundException e){       
      SLog.logWriteLine("Message SConsoleRenderer : Une erreur de type FileNotFoundException est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(SConstructorException e){
      SLog.logWriteLine("Message SConsoleRenderer : Une erreur de type SConstructorException est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(IOException e){                 
      // Exception lancée s'il y a un problème en mémoire lors de la lecture/écriture d'un fichier             
      e.printStackTrace();                                               
    }
    
    SLog.logWriteLine("Message SConsoleRenderer : Fin de l'application."); 
  }

}//fin de la classe SConsoleRenderer
