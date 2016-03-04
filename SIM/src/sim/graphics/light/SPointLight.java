/**
 * 
 */
package sim.graphics.light;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.SColor;
import sim.math.SImpossibleNormalizationException;
import sim.math.SVector3d;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SStringUtil;

/**
 * La classe <b>SPointLight</b> représente une source de lumière ponctuelle.
 * 
 * @author Simon Vézina
 * @since 2015-01-21
 * @version 2016-01-14
 */
public class SPointLight extends SAbstractAttenuatedLight {

	/**
	 * Constructeur d'une source de lumière ponctuelle blanche située à l'origine sans facteur d'atténuation.
	 */
	public SPointLight()
	{
		this(DEFAULT_LIGHT_COLOR, DEFAULT_POSITION);
	}

	/**
	 * Constructeur d'une source ponctuelle de lumière sans atténuation.
	 * 
	 * @param color - La couleur de la source de lumière.
	 * @param position - La position de la source de lumière.
	 */
	public SPointLight(SColor color, SVector3d position)
	{
		this(color, position, DEFAULT_CONSTANT_ATTENUATOR, DEFAULT_LINEAR_ATTENUATOR, DEFAULT_QUADRATIC_ATTENUATOR);
	}

	/**
	 * Constructeur d'une source de lumière blanche avec facteur d'atténuation.
	 * 
	 * @param color - La couleur de la source de lumière.
	 * @param position - La positon de la source de lumière.
	 * @param cst_att - La constante d'atténuation à taux constant.
	 * @param lin_att - La constante d'atténuation à taux linéaire.
	 * @param quad_att - La constante d'atténuation à taux quadratique.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 */
	public SPointLight(SColor color, SVector3d position, double cst_att, double lin_att, double quad_att) throws SConstructorException
	{
		super(color, position, cst_att, lin_att, quad_att);
		
		try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SPointLight 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}

	/**
	 * Constructeur d'une source de lumière ponctuelle à partir d'information lue dans un fichier de format txt.
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SException Si des facteurs d'atténuation sont initialisés avec des valeurs erronées.
	 * @throws SConstructorException Si une erreur est survenue à la construction. 
	 * @see SBufferedReader
	 */
	public SPointLight(SBufferedReader sbr)throws IOException, SConstructorException
	{
		this();		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SPointLight 002 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	@Override
	public SVector3d getOrientation(SVector3d position_to_illuminate) throws SRuntimeException
	{ 
		SVector3d orientation = position_to_illuminate.substract(position);
		
		try{
		  return orientation.normalize(); 
		}catch(SImpossibleNormalizationException e){
		  // ce cas sera observé si le point à illuminer est situé sur la source de lumière
		  throw new SRuntimeException("Erreur SPointLight 003 : La cible à illuminer est située sur la source de lumière ponctuelle." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
		}
	}
	
	@Override
	public double attenuation(SVector3d position_to_illuminate)
	{
		double distance = (position.substract(position_to_illuminate)).modulus();
		
		return 1.0 / (constant_attenuator + linear_attenuator*distance + quadratic_attenuator*distance*distance);
	}
	
	/* (non-Javadoc)
	 * @see simTools.SWriteable#write(java.io.BufferedWriter)
	 */
	@Override
	public void write(BufferedWriter bw) throws IOException
	{
		bw.write(SKeyWordDecoder.KW_POINT_LIGHT);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		writePointLightParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}

	/**
	 * Méthode pour écrire les paramètres associés à la classe SPointLight et ceux qu'il a hérité.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writePointLightParameter(BufferedWriter bw)throws IOException
	{
		//Écrire les paramètres hérités de la classe SAbstractLight
	  writeAbstractAttenuatedLightParameter(bw);
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
  protected void readingInitialization() throws SInitializationException
  {
    super.readingInitialization();
    
    initialize();
  }
	
	@Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_POINT_LIGHT;
  }
	
}//fin de la classe SPointLight
