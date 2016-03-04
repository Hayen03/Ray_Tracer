package sim.application.util;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import sim.exception.SConstructorException;
import sim.geometry.space.SAbstractGeometrySpace;
import sim.graphics.SRaytracer;
import sim.graphics.SScene;
import sim.util.SChronometer;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe <b>SJFrameRenderer</b> représentant un JFrame permettant de lancer l'application du <i>ray tracer</i>.
 * 
 * @author Simon Vézina
 * @since 2015-08-22
 * @version 2016-01-01
 */
public class SJFrameRenderer extends JFrame {

  /**
   * La variable <b>NB_LINE_BEFORE_REPAINT</b> représente le nombre de lignes calculées par le <i>ray tracer</i> avant d'effectuer un repaint du jpanel.
   */
  private final int NB_LINE_BEFORE_REPAINT = 4;   
  
  /**
   * 
   */
  private static final long serialVersionUID = 1663616456131809383L;
  
  private SJPanelRenderer contentPane;

  //----------------
  // CONSTRUCTEUR //
  //----------------
  
  /**
   * Constructeur de l'application <i>ray tracer</i> dans un format "JFrame".
   */
  public SJFrameRenderer()
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    
    //Le panel du renderer
    contentPane = new SJPanelRenderer();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.setPreferredSize(getMaximumSize());       //permettre au jpanel d'occuper le maximum d'espace dans le jframe
    setContentPane(contentPane);
  }

  //------------
  // MÉTHODES //
  //------------
  
  /**
   * Méthode pour lancer l'application du <i>ray tracer</i> en format "JFrame".
   * 
   * @param config - La configuration de l'application.
   */
  public void raytrace(SConfiguration config)
  {
    try
    {
      SChronometer chrono = new SChronometer();
      
      //Lecture de la scène
      chrono.start();
      SLog.logWriteLine("Message SJFrameRenderer : Lecture de la scène '" + config.getReadDataFileName() + "'.");
      
      SScene scene = new SScene(config.getReadDataFileName());
        
      chrono.stop();
      SLog.logWriteLine("Message SJFrameRenderer : Fin de la lecture de la scène.");
      SLog.logWriteLine("Message SJFrameRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("");
      
      //Ray tracing
      chrono.start();
      SLog.logWriteLine("Message SJFrameRenderer : Début du raytracing.");
      
      //Construction du raytracer
      SRaytracer raytracer = scene.buildRaytracer();
      
      //Mettre à jour la taille de la fenêtre
      setBounds(100, 100, scene.getViewport().getWidth(), scene.getViewport().getHeight());
      
      //Effectuer le ray tracing ligne par ligne
      int nb_pixels = scene.getViewport().getWidth()*NB_LINE_BEFORE_REPAINT;
      
      while(scene.getViewport().hasNextPixel())
      {
        raytracer.raytrace(nb_pixels);
        contentPane.setBufferedImage(scene.getViewport().getBufferedImage());
        repaint();
      }
                       
      chrono.stop();
      
      SLog.logWriteLine("Message SJFrameRenderer : Fin du raytracing.");
      SLog.logWriteLine("Message SJFrameRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("Message SJFrameRenderer : Nombre de tests d'intersection réalisés : " + SAbstractGeometrySpace.getIntersectionTestCount() + " tests.");
      
      SLog.logWriteLine("");
      
      //Écriture de l'image
      chrono.start();
      SLog.logWriteLine("Message SJFrameRenderer : Écriture de l'image.");
      
      //Écriture du fichier png
      scene.getViewport().writeImage();
      
      chrono.stop();
      SLog.logWriteLine("Message SJFrameRenderer : Fin de l'écriture de l'image.");
      SLog.logWriteLine("Message SJFrameRenderer : Durée : " + chrono.getTime() + " s.");
      SLog.logWriteLine("");
      
      //Écriture de la scène
      scene.write(config.getWriteDataFileName());
 
    }catch(FileNotFoundException e){       
      SLog.logWriteLine("Message SJFrameRenderer : Une erreur de type FileNotFoundException est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(SConstructorException e){
      SLog.logWriteLine("Message SJFrameRenderer : Une erreur de type SConstructorException est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage()); 
      e.printStackTrace();
    }catch(IOException e){                 
      // Exception lancée s'il y a un problème en mémoire lors de la lecture/écriture d'un fichier             
      e.printStackTrace();                                              
    }
      
    SLog.logWriteLine("Message SJFrameRenderer : Fin de l'application."); 
  }
  
}//fin de la classe SJFrameRenderer
