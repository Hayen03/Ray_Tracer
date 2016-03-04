/**
 * 
 */
package sim.graphics.light;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.graphics.SColor;
import sim.math.SVector3d;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe abstraite <b>SAbstractAttenuatedLight</b> repr�sente une source de lumi�re avec attribue de base comme la couleur et les facteurs d'att�nuation.
 * 
 * @author Simon V�zina
 * @since 2015-01-09
 * @version 2015-11-28
 */
public abstract class SAbstractAttenuatedLight extends SAbstractLight implements SAttenuatedLight{

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_POSITION, SKeyWordDecoder.KW_CONSTANT_ATTENUATOR,
    SKeyWordDecoder.KW_LINEAR_ATTENUATOR, SKeyWordDecoder.KW_QUADRATIC_ATTENUATOR
  };
  
	protected static final SVector3d DEFAULT_POSITION = new SVector3d(0.0, 0.0, 0.0);	//position � l'origine par d�faut
	
	//Par d�faut, il n'y a pas d'att�nuation
	protected static final double DEFAULT_CONSTANT_ATTENUATOR = 1;						
	protected static final double DEFAULT_LINEAR_ATTENUATOR = 0.0;
	protected static final double DEFAULT_QUADRATIC_ATTENUATOR = 0.0;
	
	protected SVector3d position;			//la position de la source de lumi�re
	
	protected double constant_attenuator;	//constante d'att�nuation constant
	protected double linear_attenuator;		//constante d'att�nuation lin�aire
	protected double quadratic_attenuator;	//constante d'att�nuation quadratique
	
	/**
	 * Constructeur d'une source de lumi�re blanche sans facteur d'att�nuation positionn� � l'origine.
	 */
	public SAbstractAttenuatedLight()
	{
		this(DEFAULT_LIGHT_COLOR, DEFAULT_POSITION);
	}
	
	/**
	 * Constucteur d'une source de lumi�re avec une couleur particuli�re sans facteur d'att�nuation.
	 * @param color - La couleur de la source de lumi�re.
	 */
	public SAbstractAttenuatedLight(SColor color, SVector3d position)
	{
		this(color, position,DEFAULT_CONSTANT_ATTENUATOR, DEFAULT_LINEAR_ATTENUATOR, DEFAULT_QUADRATIC_ATTENUATOR);
	}
	
	/**
	 * Constructeur d'une source de lumi�re.
	 * @param color - La couleur de la source de lumi�re.
	 * @param position - La positon de la source de lumi�re.
	 * @param cst_att - La constante d'att�nuation � taux constant.
	 * @param lin_att - La constante d'att�nuation � taux lin�aire.
	 * @param quad_att - La constante d'att�nuation � taux quadratique.
	 * @throws SConstructorException Si des facteurs d'att�nuation sont initialis�s avec des valeurs erron�es.
	 */
	public SAbstractAttenuatedLight(SColor color, SVector3d position, double cst_att, double lin_att, double quad_att) throws SConstructorException
	{
		super(color);
		
		this.position = position;
		
		if(cst_att < 1)
			throw new SConstructorException("Erreur SAbstractAttenuatedLight 001 : La constante d'att�nuation constante doit �tre sup�rieure � 1.");
		else
			constant_attenuator = cst_att;
		
		if(lin_att < 0)
			throw new SConstructorException("Erreur SAbstractAttenuatedLight 002 : La constante d'att�nuation lin�aire doit �tre positive (C_lin�aire > 0).");
		else
			linear_attenuator = lin_att;
		
		if(quad_att < 0)
			throw new SConstructorException("Erreur SAbstractAttenuatedLight 003 : La constante d'att�nuation quadratique doit �tre positive (C_quad > 0).");
		else
			quadratic_attenuator = quad_att;
		
		try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SAbstractAttenuatedLight 004 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	@Override
	public SVector3d getPosition()
	{ 
	  return position;
	}
	
	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException 
	{
		//Lecture des param�tres de la classe h�rit� SAbstractLight 
		if(super.read(sbr, code, remaining_line))
			return true;
		else
			switch(code)
			{
				case SKeyWordDecoder.CODE_POSITION : position = new SVector3d(remaining_line); return true;
																	
				case SKeyWordDecoder.CODE_CONSTANT_ATTENUATOR : 	constant_attenuator = readAttenuationConstantWithMinValue(remaining_line, 1.0); return true;
																	
				case SKeyWordDecoder.CODE_LINEAR_ATTENUATOR :		linear_attenuator = readAttenuationConstantWithMinValue(remaining_line, 0.0); return true;
																	
				case SKeyWordDecoder.CODE_QUADRATIC_ATTENUATOR :	quadratic_attenuator = readAttenuationConstantWithMinValue(remaining_line, 0.0); return true;
				
				default : return false;
			}	
	}
	
	/**
	 * M�thode qui fait la lecture d'une constante d'att�nuation avec une restriction sur sa valeur minimale. 
	 * Si la valeur lue est inf�rieure � la valeur minimale, une exception sera retourn�e. 
	 * @param remaining_line - L'expression en String de la constante d'att�nuation.
	 * @param min_value - La valeur minimale acceptable pour la constante d'att�nuation.
	 * @return La constante d'att�nuation (sup�rieure � la valeur minimale).
	 * @throws SReadingException S'il y a une erreur de lecture.
	 */
	private double readAttenuationConstantWithMinValue(String remaining_line, double min_value)throws SReadingException
	{
		StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);
		
		if(tokens.countTokens() == 0)
			throw new SReadingException("Erreur SAbstractAttenuatedLight 005 : Il n'y a pas de valeur num�rique affect�e � cette constante d'att�nuation.");
		
		String s = tokens.nextToken();
		double cst = 0;
	
		try{
			cst = Double.valueOf(s);
		}catch(NumberFormatException e){ 
			throw new SReadingException("Erreur SAbstractAttenuatedLight 006 : L'expression '" + s + "' n'est pas un nombre de type double pouvant �tre affect� � une constante d'att�nuation.", e);
		}	
	
		if(cst < min_value)
			throw new SReadingException("Erreur SAbstractAttenuatedLight 006 : La constante d'att�nuation '" + cst +"' est inf�rieure � la valeur minimale acceptable �tant '" + min_value +"'.");
		else
			return cst;
	}
	
	/**
	 * M�thode pour �crire les param�tres associ�s � la classe SAbstractAttenuatedLight et ceux qu'il h�rite.
	 * @param bw - Le BufferedWriter �crivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'�criture.
	 * @see IOException
	 */
	protected void writeAbstractAttenuatedLightParameter(BufferedWriter bw)throws IOException
	{
		//�crire les param�tres h�rit�s de la classe SAbstractLight
		writeAbstractLightParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_POSITION);
		bw.write("\t\t");
		position.write(bw);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_CONSTANT_ATTENUATOR);
		bw.write("\t");
		bw.write(Double.toString(constant_attenuator));
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_LINEAR_ATTENUATOR);
		bw.write("\t");
		bw.write(Double.toString(linear_attenuator));
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_QUADRATIC_ATTENUATOR);
		bw.write("\t");
		bw.write(Double.toString(quadratic_attenuator));
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
   * M�thode pour faire l'initialisation de l'objet apr�s sa construction.
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
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
	
}//fin classe abstraite SAbstractLight
