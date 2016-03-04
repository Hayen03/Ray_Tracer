/**
 * 
 */
package sim.graphics.light;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.graphics.SColor;
import sim.math.SImpossibleNormalizationException;
import sim.math.SVector3d;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SDirectionalLight</b> représente une source de lumière directionnel. 
 * Contrairement à une source de lumière ambiante, cette source de lumière a une orientation ce qui a un impact sur la réflexion diffuse.
 * Ce type de lumière correspond très bien au <b>comportement du Soleil</b> dont l'orientation dépend de la position du Soleil dans le ciel.
 * Dans cette classe, la position de la source aura un effet uniquement si l'on utilise les facteurs d'atténuations.
 * 
 * @author Simon Vézina
 * @since 2015-01-15
 * @version 2015-12-04
 */
public class SDirectionalLight extends SAbstractLight{

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_ORIENTATION };
  
	private static final SVector3d DEFAULT_DIRECTIONAL_LIGHT_ORIENTATION = new SVector3d(0.0, 0.0, -1.0);
	
	/**
	 * La variable <b>orientation</b> correspond à l'orientation de la source de lumière.
	 */
	private SVector3d orientation;		
	
	/**
	 * Constructeur d'une source de lumière directionnelle blanche positionnée à l'origine et orientée selon l'axe -z.
	 * Si dans une scène, l'axe z représente le haut, cette source de lumière va éclairer vers le bas comme le ferait le Soleil à midi.
	 */
	public SDirectionalLight()
	{
		this(DEFAULT_LIGHT_COLOR, DEFAULT_DIRECTIONAL_LIGHT_ORIENTATION);
	}
	
	/**
	 * Constructeur d'une source de lumière directionnelle. 
	 * 
	 * @param color - La couleur de la source de lumière.
	 * @param orientation - L'orientation de la source de lumière.
	 * @throws SConstructorException Si une erreur est survenue à la construction. 
	 */
	public SDirectionalLight(SColor color, SVector3d orientation) throws SConstructorException
	{
		super(color);
		
		this.orientation = orientation;
		
		try{
		  initialize();
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SDirectionalLight 001 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    } 
	}

	/**
	 * Constructeur d'une source de lumière directionnelle à partir d'information lue dans un fichier de format txt.
	 * 
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si une erreur est survenue à la construction. 
	 * @see SBufferedReader
	 */
	public SDirectionalLight(SBufferedReader sbr) throws IOException, SConstructorException
	{
		this();		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SDirectionalLight 002 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Méthode pour obtenir l'orientation de la source de lumière directionnelle.
	 * 
	 * @return L'orientation de la source de lumière.
	 */
	public SVector3d getOrientation()
	{ 
	  return orientation;
	}
	
	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException 
	{
		//Analyse du code du mot clé dans la classe hérité SAbstractLight
		if(super.read(sbr, code, remaining_line))
			return true;
		else
			switch(code)
			{
				case SKeyWordDecoder.CODE_ORIENTATION :	orientation = new SVector3d(remaining_line); return true;
				
				default : return false;
			}
	}
	
	@Override
	public void write(BufferedWriter bw) throws IOException 
	{
		bw.write(SKeyWordDecoder.KW_DIRECTIONAL_LIGHT);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les paramètres de la classe abstraite SAbstractLight
		writeDirectionalLightParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);	
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SDirectionalLight et ceux qu'il a hérité.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writeDirectionalLightParameter(BufferedWriter bw)throws IOException 
	{
		//Écrire les paramètres hérités de la classe SAbstractLight
		writeAbstractLightParameter(bw);
		
		//Écrire les paramètres de la classe SDirectionalLight
		bw.write(SKeyWordDecoder.KW_ORIENTATION);
		bw.write("\t\t");
		orientation.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
	  try{
	    orientation = orientation.normalize();
	  }catch(SImpossibleNormalizationException e){
	    throw new SInitializationException("Erreur SDirectionalLight 003 : Une erreur est survenue lors de l'initialisation." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
	  }
	}
	
	@Override
  protected void readingInitialization() throws SInitializationException
  {
    super.readingInitialization();
    
    initialize();
  }
	
	@Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_DIRECTIONAL_LIGHT;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin classe SDirectionalLight
