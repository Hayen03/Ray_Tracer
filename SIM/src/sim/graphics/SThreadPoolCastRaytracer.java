/**
 * 
 */
package sim.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.shader.SShader;
import sim.graphics.SAbstractRaytracer;
import sim.math.SVectorPixel;
import sim.util.SLog;

/**
 * La classe <b>SThreadPoolCastRaytracer</b> représentant un raytracer lançant <b>plusieurs rayons</b> dans chaque pixel du viewport. 
 * Cette méthode permet de réduire l'aliassage dans l'image puisque qu'une moyenne de la couleur 
 * obtenue par chaque rayon lancé dans chaque pixel sera utilisé pour définir la couleur de chaque pixel.
 * 
 * @author Simon Vézina et Robin Vézina
 * @since 2015-04-07
 * @version 2016-01-13
 */
public class SThreadPoolCastRaytracer extends SAbstractRaytracer {

  private static final int DEFAULT_TASK = 2;			  //nombre de tâches par excécution par défaut (typiquement le nombre de processeur que l'on veut exploiter)
  private static final int DEFAULT_SAMPLING = 1;		//nombre de rayon lancé par pixel par défaut
  
  private final int task;                           //nombre de tâches par exécution (nombre de thread en exécution simultanée)
  private final int sampling;	                      //nombre de rayon lancé dans chaque pixel
  
  private final ThreadPoolExecutor executor;        //exécuteur des tâches de calcul de la couleur de chaque pixel 
  
  /**
   * Sous-classe représentant une tâche à réaliser par un thread.
   * La tâche à réaliser sera de calculer une SColor et prendra comme paramètre un SVectorPixel.
   * @author Simon Vézina et Robin Vézina
   * @since 2015-04-07
   * @version 2015-04-10
   */
  protected final class FutureSColor implements Callable<SColor>
  {
    private SVectorPixel pixel;	//le pixel dont la couleur doit être évalué

    /**
     * Constructeur de la futur tâche à réaliser.
     * @param pixel - La coordonnée du pixel dont la couleur sera à évaluer.
     */
    public FutureSColor(SVectorPixel pixel) 
    {
      this.pixel = pixel;
    }

    @Override
    public SColor call() throws Exception
    {
      //La tâche a réaliser sera d'évaluer la couleur du pixel (méthode protected de la classe présente).
      return evaluatePixelColor(pixel);
    }
  }

  
  /**
   * Constructeur d'un raytracer de type <i>multi cast</i> avec <b>un rayon</b> par défaut. 
   * 
   * @param view_frustum - La pyramide de vue.
   * @param shader - Le calculateur d'illumination (<i>shader</i>).
   * @param viewport - La fenêtre de rendu (<i>viewport</i>).
   */
  public SThreadPoolCastRaytracer(SViewFrustum view_frustum, SShader shader, SViewport viewport)
  {
    this(view_frustum, shader, viewport, DEFAULT_SAMPLING, DEFAULT_TASK);
  }

  /**
   * Constructeur d'un raytracer de type <i>multi cast</i>.
   * 
   * @param view_frustum - La pyramide de vue.
   * @param shader - Le calculateur d'illumination (<i>shader</i>).
   * @param viewport - La fenêtre de rendu (<i>viewport</i>).
   * @param sampling - Le nombre de rayon lancé par pixel afin de réduire le crénelage (<i>anti aliasing>/i>). 
   * @param task - Le nombre de tâches simultanés en calcul (pour le <i>multithreading</i>).
   * @throws SConstructorException Si le nombre de rayons par pixel (<i>sampling</i>) n'est pas supérieur à 0.
   * @throws SConstructorException Si le nombre de tâches (<i>task</i>) n'est pas supérieur à 0.
   */
  public SThreadPoolCastRaytracer(SViewFrustum view_frustum, SShader shader, SViewport viewport, int sampling, int task)throws SConstructorException
  {
    super(view_frustum, shader, viewport);
    
    //Vérification que le sampling soit supérieur à 0
    if(sampling > 0)
    	this.sampling = sampling;
    else
    	throw new SConstructorException("Erreur SMultiCastRaytracer 001 : Le nombre de rayon par pixel '" + sampling + "' n'est pas supérieur à 0.");
    
    //Vérification que le sampling soit supérieur à 0
    if(task > 0)
      this.task = task;
    else
      throw new SConstructorException("Erreur SMultiCastRaytracer 002 : Le nombre de rayon par pixel '" + task + "' n'est pas supérieur à 0.");
      
    //Message de mauvaise fonctionnalité si la coordonée interne d'un pixel n'est pas aléatoire lors de super sampling
    if(sampling > 1 && view_frustum.getPixelInternalCoordinate() != SViewFrustum.RANDOM_PIXEL)
      SLog.logWriteLine("Message SMultiCastRaytracer : Puisque le sampling est à '" + sampling + "' et que la coordonnée interne d'un pixel est '" + SViewFrustum.PIXEL_COORDINATE[view_frustum.getPixelInternalCoordinate()] + "'(code = " + view_frustum.getPixelInternalCoordinate() + ") et non pas '" + SViewFrustum.PIXEL_COORDINATE[SViewFrustum.RANDOM_PIXEL] + "'(code = " + SViewFrustum.RANDOM_PIXEL + "), l'antialiasing ne peut pas être applicable.");
    
    //Construction de l'exécuteur du threadPool
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(task);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see sim.graphics.SRaytracer#raytrace(int)
   */
  @Override
  public void raytrace(int nb_pixel) throws SRuntimeException
  {
    //Vérification du nombre de pixels demandé en calcul
    if(nb_pixel < 0)
      throw new SRuntimeException("Erreur SMultiCastRaytracer 003 : Le nombre de pixels '" + nb_pixel + "' doit être supérieur à 0.");

    int count = 0; // compter le nombre de pixel calculé

    //Exécuter des calculs tant que le nombre de pixel désiré n'est pas atteint
    while(count < nb_pixel)
    {
      // S'il y a encore des pixels disponibles à être dessiné
      if (viewport.hasNextPixel()) 
      {
        // le prochain pixel à calculer
        List<SVectorPixel> listSVectorPixel = new ArrayList<SVectorPixel>();
        for(int i = 0; i < task && viewport.hasNextPixel(); i++)
        {
          SVectorPixel nextPixel = viewport.nextPixel();
          listSVectorPixel.add(nextPixel);
        }

        
        List<FutureTask<SColor>> list = new ArrayList<FutureTask<SColor>>();
        for(SVectorPixel sVectorPixel : listSVectorPixel)
          list.add(new FutureTask<SColor>(new FutureSColor(sVectorPixel)));

        //Chargement des tâches à faire dans l'exécuteur et lancement de la thread
        for (FutureTask<SColor> f : list)
          executor.execute(f);

        List<SColor> listSColor = new ArrayList<SColor>();

        // Déterminer la couleur calculée par le shader pour le pixel
        try{
          
          for(FutureTask<SColor> f : list)
            listSColor.add(f.get());
            
        } catch (InterruptedException e) {
          throw new SRuntimeException("Erreur SThreadPoolCastRaytracer 004 : Raytracer interropu.", e);
        } catch (ExecutionException e) {
          throw new SRuntimeException("Erreur SThreadPoolCastRaytracer 005 : Erreur d'exécution.", e);
        }

        // Dessiner les pixels calculés dans le viewport
        for(int i = 0; i < listSColor.size(); i++) 
          viewport.setColor(listSVectorPixel.get(i), listSColor.get(i));
        
        count += listSColor.size(); // augmenter le compteur
      } 
      else                         
        count = nb_pixel;           // nombre de pixel à calculé atteint, donc on arrête tout
    }
  }

  @Override
  protected SColor evaluatePixelColor(SVectorPixel pixel)
  {
    //--------------------------------------------------------------------
    //Évaluer la couleur d'un pixel selon le nombre de sampling à évaluer
    //--------------------------------------------------------------------
    
    if(sampling == 1)                         //à un rayon par pixel
      return raytracePixel(pixel);
    else                                      //à plusieurs rayons par pixel
    {
      SColor[] tab = new SColor[sampling];
      
      //Calcul la couleur pour chaque sampling
      for(int i=0; i<tab.length; i++)
      	tab[i] = raytracePixel(pixel);
      
      //Faire la somme des couleurs
      SColor sum = new SColor(0.0, 0.0, 0.0);
      
      for(int i=0; i<tab.length; i++)
      	sum = sum.add(tab[i]);
      
      //Retourner la couleur après avoir divisé par le nombre de sampling
      return sum.multiply( 1.0 / (double)sampling);
    }
  }
  
}// fin classe SSingleCastRaytracer
