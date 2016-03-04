package sim.graphics.light;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.graphics.SColor;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SStringUtil;

/**
 * La classe <b>SAmbientLight</b> représente une source de lumière ambiante. 
 * Une source ambiante simule les multilples réflexions interne entre les primitives.
 * Elle est traditionnellement très faible dans une scène. 
 * Dans cette classe, la position de la source aura un effet uniquement si l'on utilise les facteurs d'atténuations.
 * 
 * @author Simon Vézina
 * @since 2015-01-09
 * @version 2015-11-28
 */
public class SAmbientLight extends SAbstractLight {

	/**
	 * Constructeur d'une source de lumière ambiante blanche à faible intensité située à l'origine sans facteur d'atténuation. 
	 */
	public SAmbientLight()
	{
		this(DEFAULT_LIGHT_COLOR);
	}

	/**
	 * Constructeur d'une source de lumière ambiante sans facteur d'atténuation.
	 * 
	 * @param color - La couleur de la source de lumière.
	 * @param position - La position de la source de lumière.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 */
	public SAmbientLight(SColor color) throws SConstructorException
	{
		super(color);
		
		try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SAmbientLight 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	/**
	 * Constructeur d'une source de lumière ambiante à partir d'information lue dans un fichier de format txt.
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si une erreur est survenue à la construction. 
	 * @see SBufferedReader
	 */
	public SAmbientLight(SBufferedReader sbr) throws IOException, SConstructorException
	{
		this();		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SAmbientLight 002 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
				
	}

	@Override
	public void write(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_AMBIENT_LIGHT);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les paramètres de la classe SAmbientLight et ceux qu'il hérite
		writeAmbientLightParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SAmbientLight et ceux qu'il a hérité.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writeAmbientLightParameter(BufferedWriter bw)throws IOException 
	{
		//Écrire les paramètres hérités de la classe SAbstractLight
		writeAbstractLightParameter(bw);
		
		// Puisque cette classe n'a pas de paramètre, il n'y a rien a ajouter ...
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
    return SKeyWordDecoder.KW_AMBIENT_LIGHT;
  }
	
}//fin SAmbientLight
