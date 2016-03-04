/**
 * 
 */
package sim.graphics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.math.SImpossibleNormalizationException;
import sim.math.SMath;
import sim.math.SVector3d;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * Classe qui représente une caméra.
 * @author Simon
 * @since 2014-12-26
 * @version 2014-11-28
 */
public class SCamera extends SAbstractReadableWriteable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_UP, SKeyWordDecoder.KW_LOOK_AT,
    SKeyWordDecoder.KW_ANGLE, SKeyWordDecoder.KW_NEAR_CLIPPING_PLANE, SKeyWordDecoder.KW_FAR_CLIPPING_PLANE 
  };
  
	private static final SVector3d DEFAULT_POSITION = new SVector3d(0.0, 0.0, 0.0);
	private static final SVector3d DEFAULT_LOOK_AT = new SVector3d(0.0, 0.0, 1.0);
	private static final SVector3d DEFAULT_UP = new SVector3d(0.0, 1.0, 0.0);
	
	private static final double DEFAULT_VIEW_ANGLE = 60.0;
	private static final double DEFAULT_ZNEAR = 1.0;
	private static final double DEFAULT_ZFAR = 100.0;

	private SVector3d position;	//position de la caméra
	private SVector3d look_at;	//position où regarde la caméra
	private SVector3d front;	//l'orientation devant la caméra
	private SVector3d up;		//l'orientation haut de la caméra

	//Élément que l'on retrouve dans fonction gluPerspective de la librairie OpenGl pour faire la construction de la pyramide de vue
	//La variable 'aspect' sera obtenue par le SViewport
	private double view_angle;		//(fovy) Specifies the field of view angle, in degrees, in the y direction. 
	private double z_near;			//Specifies the distance from the viewer to the near clipping plane (always positive).
	private double z_far;			//Specifies the distance from the viewer to the far clipping plane (always positive).
		
	/**
	 * Constructeur vide de la caméra. Par défaut, la camera sera située à l'origine pointant selon l'axe x avec le haut orienté selon l'axe z.
	 */
	public SCamera()
	{
		this(DEFAULT_POSITION, DEFAULT_LOOK_AT, DEFAULT_UP);
	}
	
	/**
	 * Constructeur de la caméra avec paramètre de positionnement. 
	 * @param position - La position de la caméra.
	 * @param look_at - L'endroit où regarde la caméra. La distance entre position et look_at n'a pas besoin d'être unitaire.
	 * @param up - L'orientation du haut de la caméra
	 * @throws SConstructorException Si les paramètres de la caméra ne permettent une construction complète de celle-ci. 
	 */
	public SCamera(SVector3d position, SVector3d look_at, SVector3d up) throws SConstructorException
	{
		this.position = position;
		this.look_at = look_at;
		this.up = up;
		
		view_angle = DEFAULT_VIEW_ANGLE;
		z_near = DEFAULT_ZNEAR;
		z_far = DEFAULT_ZFAR;
		
		try{
		  initialize();
		}catch(SInitializationException e){
		  throw new SConstructorException("Erreur SCamera 001 : La construction de la caméra n'est pas possible en raison de ses paramètres aux constructeurs." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
	}
	
	/**
	 * Constructeur de la caméra à partir d'information lue dans un fichier de format .txt.
	 * @param br Le BufferedReader cherchant l'information de le fichier .txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si le vecteur position de la caméra et position de regard ne pas pas compatible.
	 * @throws SConstructorException Si l'écran de fond est plus près de la caméra que l'écran de face.
	 * @see SBufferedReader
	 */
	public SCamera(SBufferedReader br)throws IOException, SConstructorException
	{
		this();		//configuration de base s'il y a des paramètres non défini.
		
		try{
		  read(br);
		}catch(SInitializationException e){
		  //Si le vecteur position et look_at sont incompatible ensemble
		  throw new SConstructorException("Erreur SCamera 002 : La position de la caméra " + position + " et la position de regard " + look_at + " ne permet pas de définir un vecteur haut." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
	}
	
	/**
	 * Méthode pour obtenir la position de la camera.
	 * @return La position de la caméra.
	 */
	public SVector3d getPosition()
	{
		return position;
	}
	
	/**
	 * Méthode pour obtenir l'orientation du devant de la caméra. 
	 * En d'autres mots, la camera point dans la direction de ce vecteur.
	 * @return L'orientation du devant de la caméra.
	 */
	public SVector3d getFront()
	{ 
		return front;
	}
	
	/**
	 * Méthode pour obtenir l'orientation du haut de la caméra.
	 * @return L'orientation du haut de la caméra.
	 */
	public SVector3d getUp()
	{ 
		return up;
	}
	
	/**
	 * Méthode pour obtenir l'angle d'ouverture de vue de la caméra dans la direction verticale.
	 * @return L'angle d'ouverture de la caméra (direction verticale).
	 */
	public double getViewAngle()
	{
		return view_angle;
	}
	
	/**
	 * Méthode pour obtenir la distance entre la caméra et la position du devant de la pyramide de vue (near clipping plane). 
	 * @return La distance entre la caméra et le devant de la pyramide de vue.
	 */
	public double getZNear()
	{
		return z_near;
	}
	
	/**
	 * Méthode pour obtenir la distance entre la caméra et la position de l'arrière de la pyramide de vue (far clipping plane). 
	 * @return La distance entre la caméra et l'arrière de la pyramide de vue.
	 */
	public double getZFar()
	{
		return z_far;
	}
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
  {
		try{
		  // Construction du vecteur "front = devant" et "up = haut" qui doivent être normalisés
		  front = (look_at.substract(position)).normalize();
		  up = up.normalize();
		}catch(SImpossibleNormalizationException e){
		  throw new SInitializationException("Erreur SCamera 003 : Un vecteur n'a pas pu être normalisé." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
		
		//Vérifier que l'écran de fond est plus loin que l'écran de face
    if(z_far < z_near)
      throw new SInitializationException("Erreur SCamera 004 : La distance à l'écran de fond '" + z_far + "' ne peut pas être plus près que la distance à l'écran de face '" + z_near + "'.");  
	}
	
	@Override
	public void write(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_CAMERA);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les paramètres de la classe SViewFrustum
		writeSCameraParameter(bw);
				
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SViewFrustum.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writeSCameraParameter(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_POSITION);
		bw.write("\t");
		position.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_LOOK_AT);
		bw.write("\t\t");
		look_at.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_UP);
		bw.write("\t\t");
		up.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_ANGLE);
		bw.write("\t\t\t");
		bw.write(Double.toString(view_angle));
		bw.write(" degrees");
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_NEAR_CLIPPING_PLANE);
		bw.write("\t");
		bw.write(Double.toString(z_near));
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_FAR_CLIPPING_PLANE);
		bw.write("\t");
		bw.write(Double.toString(z_far));
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	@Override
	protected boolean read(SBufferedReader br, int code, String remaining_line)throws SReadingException
	{
		switch(code)
		{
			case SKeyWordDecoder.CODE_POSITION : position = new SVector3d(remaining_line); return true; 
			
			case SKeyWordDecoder.CODE_UP : 			 up = new SVector3d(remaining_line); return true;
			
			case SKeyWordDecoder.CODE_LOOK_AT :	 look_at = new SVector3d(remaining_line); return true;
			
			case SKeyWordDecoder.CODE_ANGLE : view_angle = readAngle(remaining_line); return true;
			
			case SKeyWordDecoder.CODE_NEAR_CLIPPING_PLANE :	z_near = readClippingPlaneDistance(remaining_line); return true;
			
			case SKeyWordDecoder.CODE_FAR_CLIPPING_PLANE : z_far = readClippingPlaneDistance(remaining_line); return true; 
			
			default : return false;
		}	
		
	}
	
	/**
	 * Méthode pour faire l'analyse d'un string afin d'y retourner l'angle d'ouverture de la pyramide de vue.
	 * L'angle de la pyramide de vue doit se situé entre 0 degré et 180 degré.
	 * @param s L'expression en String de l'angle d'ouverture.
	 * @return La valeur de l'angle d'ouverture.
	 * @throws SReadingException Si l'expression du String n'est pas admissible.
	 * @throws SReadingException Si la valeur numérique n'est pas entre 0 et 180 degré.
	 */
	private double readAngle(String remaining_line) throws SReadingException
	{
		StringTokenizer tokens = new StringTokenizer(remaining_line,SStringUtil.REMOVE_CARACTER_TOKENIZER);
		
		if(tokens.countTokens() == 0)
			throw new SReadingException("Erreur SCamera 005 : Il n'y a pas de valeur numérique affectée à l'angle d'ouverture de la pyramide de vue.");
		
		String s = tokens.nextToken();
		double angle = 0;
	
		try{
			angle = Double.valueOf(s);
		}catch(NumberFormatException e){ 
			throw new SReadingException("Erreur SCamera 006 : L'expression '" + s + "' n'est pas un nombre de type double pouvant être affecté à l'angle d'ouverture de la pyramide de vue.");
		}	
	
		if(angle < SMath.EPSILON || angle > 180.0)
			throw new SReadingException("Erreur SCamera 007 : L'angle de la pyramide de vue doit être entre 0 et 180 degrés.");
		
		return angle;
	}
	
	/**
	 * Méthode pour faire l'analyse d'un string afin d'y retourner la distance associé à un clipping plane de la pyramide de vue.
	 * La distance d'un clipping plane doit être positive.
	 * @param remaining_line - L'expression en String de la distance du clipping plane.
	 * @return La valeur de la distance du clipping plane.
	 * @throws SReadingException Si l'expression du String n'est pas admissible.
	 * @throws SReadingException Si la valeur numérique est négative.
	 */
	private double readClippingPlaneDistance(String remaining_line) throws SReadingException
	{
		StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);
		
		if(tokens.countTokens() == 0)
			throw new SReadingException("Erreur SCamera 008 : Il n'y a pas de valeur numérique affectée à la distance du clipping plane de la pyramide de vue.");
		
		String s = tokens.nextToken();
		double distance = 0;
	
		try{
			distance = Double.valueOf(s);
		}catch(NumberFormatException e){ 
			throw new SReadingException("Erreur SCamera 009 : L'expression '" + s + "' n'est pas un nombre de type double pouvant être affecté à la distance du clipping plane de la pyramide de vue.");
		}	
	
		if(distance < SMath.EPSILON)
			throw new SReadingException("Erreur SCamera 010 : La distance du clipping plane doit être supérieure ne peut pas être négative ou nulle.");
		
		return distance;
	}
	
	@Override
	protected void readingInitialization() throws SInitializationException
	{
	  initialize();
	}
	 
	@Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_CAMERA;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin classe SCamera
