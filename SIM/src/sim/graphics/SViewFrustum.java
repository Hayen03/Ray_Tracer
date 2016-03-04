/**
 * 
 */
package sim.graphics;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SCamera;
import sim.graphics.SViewport;
import sim.math.SVector3d;
import sim.math.SVectorPixel;

/**
 * Classe qui représente la pyramide de vue associé au rendu à générer. 
 * L'orientation de la pyramide de vue dépend de la position et l'orientation d'une camera.
 * La géométrie de la pyramide vue est exprimé dans l'espace des primitives de la scène.
 * @author Simon Vézina
 * @since 2014-12-28
 * @version 2015-07-16
 */
public class SViewFrustum {

  //Mode de sélection de la coordonnée interne d'un pixel
  public static final String[] PIXEL_COORDINATE = {"top_left", "top_right", "bottom_left", "bottom_right", "center", "random" };
  public static final int TOP_LEFT_PIXEL = 0;
  public static final int TOP_RIGHT_PIXEL = 1;
  public static final int BOTTOM_LEFT_PIXEL = 2;
  public static final int BOTTOM_RIGHT_PIXEL = 3;
  public static final int CENTER_PIXEL = 4;
  public static final int RANDOM_PIXEL = 5;
  
  private final SVector3d camera_position;         //la position de la camera
  private final int pixel_internal_coordinate;	   //code de coordonnée interne du pixel
  
  //Paramètres en précalculs
	private final SVector3d centre_clipping_plane;   //Position du centre de l'écran de face de la pyramide de vue
	private final SVector3d u1;                      //Vecteur pixel unitaire parallèle à la largeur
	private final SVector3d u2;                      //Vecteur pixel unitaire parallèle à la hauteur
	private final SVector3d r_ini;                   //Vecteur déplacement pour passer du centre à la coordonnée du pixel (0,0)
	
	private final int x_max;                         //Index du pixel maximal en largeur (width)
	private final int y_max;                         //Index du pixel maximal en hauteur (height)
	
	/**
	 * Constructeur d'une pyramide de vue.
	 * @param camera - La camera de la scène.
	 * @param viewport - Le viewport où sera effectué le rendu de la scène.
	 * @throws SConstructorException S'il y a eu une erreur lors de la construction de la pyramide de vue.
	 */
	public SViewFrustum(SCamera camera, SViewport viewport)throws SConstructorException
	{
		this(camera, viewport, TOP_LEFT_PIXEL);
	}
	
	/**
	 * Constructeur d'une pyramide de vue.
	 * @param camera - La camera de la scène.
	 * @param viewport - Le viewport où sera effectué le rendu de la scène.
	 * @param pixel_coordonate_code - Le code identifiant la coordonnée sélectionnée à l'intérieur d'un pixel.
	 * @throws SConstructorException S'il y a eu une erreur lors de la construction de la pyramide de vue.
	 */
	public SViewFrustum(SCamera camera, SViewport viewport, int pixel_coordonate_code)throws SConstructorException
	{
		if(isPixelInternalCoordinateCodeValid(pixel_coordonate_code))
			pixel_internal_coordinate = pixel_coordonate_code;
		else
		  throw new SConstructorException("Ereur SViewFrustum 002 : Le code de coordonnée interne du pixel '" + pixel_coordonate_code +"' n'est pas reconnu.");
	  
	  if(camera == null)
      throw new SConstructorException("Erreur SViewFrustum 003 : La caméra n'a pas été initialisée (valeur null).");
    
    if(viewport == null)
      throw new SConstructorException("Erreur SViewFrustum 004 : Le viewport n'a pas été initialisé (valeur null).");
    
    //Position de la camera
    camera_position = camera.getPosition();
    
    //Relation mathématique pour évaluer H : H = 2n tan(theta/2)
    double H = 2*camera.getZNear()*Math.tan((camera.getViewAngle()/2)/360*(2*Math.PI));  //Hauteur du front clipping plane
    
    //Relation mathématique pour évaluer L : L = aH
    double L = viewport.normalAspect()*H;             //Largeur du front clipping plane
    
    //Position dans l'espace de la pyramide de vue du centre 
    centre_clipping_plane = camera.getPosition().add(camera.getFront().multiply(camera.getZNear()));
    
    //Vecteur en pixel unitaire parallèle à la largueur du front clipping plane 
    SVector3d u1_tmp = (camera.getFront().cross(camera.getUp())).normalize();
    u1 = u1_tmp.multiply(L/(double)viewport.getWidth());
    x_max = viewport.getWidth();
      
    //Vecteur en pixel unitaire parallèle à la largeur du front clipping plane 
    SVector3d u2_tmp = (camera.getFront().cross(u1)).normalize();
    u2 = u2_tmp.multiply(H/(double)viewport.getHeight());
    y_max = viewport.getHeight();
    
    //Vecteur permettant de localiser à partir du centre du front clipping plane le pixel de coordonnée (0,0)
    SVector3d r_ini_1 = u1.multiply((double)(-1*viewport.getWidth()/2));
    SVector3d r_ini_2 = u2.multiply((double)(-1*viewport.getHeight()/2));
    r_ini = r_ini_1.add(r_ini_2);    
	}
	
	/**
   * Méthode pour obtenir la position de la camera associée à cette pyramide de vue (<i>view frustum</i>).
   * @return la pointe de la pyramide de vue correspondant à la position de la caméra.
   */
  public SVector3d getCameraPosition()
  {
    return camera_position;
  }
  
  /**
   * Méthode pour obtenir le code associé à la coordonnée interne du pixel qui sera calculé par la pyramide de vue
   * à partir d'une coordonnée pixel de l'écran de vue.
   * @return le code de référence de la coordonée interne d'un pixel.
   */
  public int getPixelInternalCoordinate()
  {
    return pixel_internal_coordinate; 
  }
  
	/**
   * Méthode pour retourner la position d'un pixel du Viewport dans le référentiel du ViewFrustum en coordonnée xyz. 
   * Ce pixel sera situé sur le front clipping plane.
   * @param p - La coordonnée du pixel.
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue.
   * @throws SRuntimeException Si la caméra n'a pas été initialisée pour la pyramide de vue.
   * @throws SRuntimeException Si le viewport n'a pas été initialisé pour la pyramide de vue. 
   */
  public SVector3d viewportToViewFrustum(SVectorPixel p)throws SRuntimeException
  { 
    return viewportToViewFrustum(p.getX(), p.getY());
  } 
  
  /**
   * Méthode pour retourner la position d'un pixel de coordonnée xy dans le Viewport dans le référentiel du ViewFrustum en coordonnée xyz. 
   * Ce pixel sera situé sur le front clipping plane à la coordonnée déterminé par le code choisi lors de la construction du SViewFrustum.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue.
   */
  public SVector3d viewportToViewFrustum(int x, int y)
  {
    return viewportToViewFrustum(x, y, pixel_internal_coordinate);
  }
  
	/**
   * Méthode pour retourner la position d'un pixel de coordonnée xy dans le Viewport dans le référentiel du ViewFrustum en coordonnée xyz. 
   * Ce pixel sera situé sur le front clipping plane à la coordonnée déterminé par le code choisi.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @param pixel_code_coordinate - Le code de la coordonnée interne du pixel qui sera calculé.
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue.
   * @throws SRunTimeException Si la coordonnée (x,y) est à l'extérieur des limitives du viewport.
   * @throws SRunTimeException Si le code de coordonnée interne du pixel est invalide.
   */
	public SVector3d viewportToViewFrustum(int x, int y, int pixel_code_coordinate)throws SRuntimeException
  {
	  if(x < 0 || x > x_max)
	    throw new SRuntimeException("Erreur SViewFrustum 005 : La coordonnée x = " + x + "est à l'extérieur de l'interval acceptable [0," + x_max + "].");
  
	  if(y < 0 || y > y_max)
      throw new SRuntimeException("Erreur SViewFrustum 006 : La coordonnée y = " + y + "est à l'extérieur de l'interval acceptable [0," + y_max + "].");
	  
	  //Sélection de la coordonnée interne du pixel.
	  switch(pixel_code_coordinate)
	  {
	    case TOP_LEFT_PIXEL : return viewportToViewFrustumTopLeft(x,y);
	    case TOP_RIGHT_PIXEL : return viewportToViewFrustumTopRight(x,y);
	    case BOTTOM_LEFT_PIXEL : return viewportToViewFrustumBottomLeft(x,y);
	    case BOTTOM_RIGHT_PIXEL : return viewportToViewFrustumBottomRight(x,y);
	    case CENTER_PIXEL : return viewportToViewFrustumCenter(x,y);
	    case RANDOM_PIXEL : return viewportToViewFrustumRandom(x,y);
	    
	    default : throw new SRuntimeException("Erreur SViewFrustum 007 : Le code de sélection interne au pixel '" + pixel_internal_coordinate + "' n'est pas bien défini."); 
	  }
  }
	
	/**
	 * <p>Méthode pour définir le mécanisme de localisation de la coordonnée interne à un pixel. Les formats
	 * admissibles sont :</p>
	 * <p>TOP_LEFT_PIXEL = 1 : Sélection d'une coordonnée dans le coin supérieur gauche du pixel.</p>
	 * <p> ... </p>
	 * <p>CENTER_PIXEL = 5 : Sélection d'une coordonnée dans le centre du pixel.</p>
	 * <p>RANDOM_PIXEL = 6: Sélection d'une coordonnée aléatoire à l'intérieur du pixel.</p>
	 * @param code - Le code de localisation.
	 */
	private boolean isPixelInternalCoordinateCodeValid(int code)
	{
	  switch(code)
	  {
	    case TOP_LEFT_PIXEL :
	    case TOP_RIGHT_PIXEL :
	    case BOTTOM_LEFT_PIXEL :
	    case BOTTOM_RIGHT_PIXEL :
	    case CENTER_PIXEL : 
	    case RANDOM_PIXEL : return true;
	    
	    default : return false;
	  }
	}
	
  /**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à la <b>position supérieure gauche du pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin supérieur gauche du pixel.
   */
  private SVector3d viewportToViewFrustumTopLeft(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x)).add(u2.multiply((double)y));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
  
	/**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à la <b>position supérieure droit du pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin supérieur droit du pixel.
   */
  private SVector3d viewportToViewFrustumTopRight(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x+1.0)).add(u2.multiply((double)y));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
  
  /**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à la <b>position inférieur gauche du pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin inférieur gauche du pixel.
   */
  private SVector3d viewportToViewFrustumBottomLeft(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x)).add(u2.multiply((double)y+1.0));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
  
  /**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à la <b>position inférieur droit du pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin inférieur droit du pixel.
   */
  private SVector3d viewportToViewFrustumBottomRight(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x+1.0)).add(u2.multiply((double)y+1.0));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
  
  /**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à la <b>position centrale du pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin supérieur gauche du pixel.
   */
  private SVector3d viewportToViewFrustumCenter(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x+0.5)).add(u2.multiply((double)y+0.5));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
			
  /**
   * Méthode pour retourner la position (x,y,z) d'un pixel de coodonnée (x,y). La position du pixel correspondra à une <b>position aléatoire dans le pixel</b>.
   * @param x - La coordonnée x du pixel en largeur (width).
   * @param y - La coordonnée y du pixel en hauteur (height).
   * @return Le vecteur position du pixel sur le devant de la pyramide de vue situé dans le coin supérieur gauche du pixel.
   */
  private SVector3d viewportToViewFrustumRandom(int x, int y)
  {
    //Vecteur permettant de localiser à partir du pixel (0,0) le pixel (x,y)
    SVector3d r = (u1.multiply((double)x+Math.random())).add(u2.multiply((double)y+Math.random()));
    
    return ((centre_clipping_plane.add(r_ini)).add(r));
  }
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		test1();
		
	}

	/**
	 * Test #1 : Test pour vérifier la fonctionnalité du calcul de la position d'un pixel dans l'espace du view frustum.
	 */
	public static void test1()
	{
	 	SViewport viewport = new SViewport(5, 5);
		
		//Reproduction de l'exemple dans les notes de cours
		SCamera camera = new SCamera(new SVector3d(0.0, 0.0, 0.0),			//camera à l'origine
				                     new SVector3d(1.0, 0.0, 0.0),			//regarde en x
				                     new SVector3d(0.0, 0.0, 1.0));			//up en z
		
		SViewFrustum view_frustum = new SViewFrustum(camera, viewport);
		
		try{
			
			while(viewport.hasNextPixel())
			{
				SVectorPixel p = viewport.nextPixel();
				SVector3d position_pixel = view_frustum.viewportToViewFrustum(p);
				
				System.out.print("Pixel : " + p + "\t Vecteur : " + position_pixel);
				System.out.println();
			}			
		}catch(Exception e){ System.out.println(e.getMessage()); }
	}
	
}//fin classe SViewFrustum
