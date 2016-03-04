/**
 * 
 */
package sim.graphics.material;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.graphics.SColor;
import sim.math.SVectorUV;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SDefaultMaterial</b> représente un matériel par défaut.
 * Ce matériel sera blanc, complètement opaque avec des attribues physiques par défaut.
 *
 * @author Simon Vézina
 * @since 2015-01-13
 * @version 2015-12-14
 */
public class SDefaultMaterial extends SAbstractReadableWriteable implements SMaterial {

  /**
   * La constante 'DEFAULT_MATERIAL_NAME' correspond au nom par défaut d'un matériel égal à {@value}.
   */
	public static final String DEFAULT_MATERIAL_NAME = "none";
	
	//Couleur réfléchie par un matériel opaque par défaut
	private static final SColor DEFAULT_AMBIENT_COLOR = new SColor(0.2, 0.2, 0.2);
	private static final SColor DEFAULT_DIFFUSE_COLOR = new SColor(0.6, 0.6, 0.6);
	private static final SColor DEFAULT_SPECULAR_COLOR = new SColor(0.7, 0.7, 0.7);
	private static final double DEFAULT_SHININESS = 20.0;
	private static final double DEFAULT_REFLECTIVITY = 0.0;
	private static final double DEFAULT_TRANSPARENCY = 0.0;
	private static final double DEFAULT_REFRACTIVE_INDEX = 1.00;
		
	/**
	 * La variable 'name' correspond au nom du matériel.
	 */
	private String name;			
		
	/**
	 * Constructeur d'un matérial par défaut avec un nom par défaut.
	 */
	public SDefaultMaterial()
	{ 
		this(DEFAULT_MATERIAL_NAME);
	}
	
	/**
	 * Constructeur d'un matériel avec un nom de référence. Ce nom sera utilisé comme référence
	 * lors d'une affectation d'un matériel à une primitive.
	 * @param material_name - Le nom du matériel.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 */
	public SDefaultMaterial(String material_name) throws SConstructorException
	{
		name = material_name;
		
		try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SDefaultMaterial 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
		
	/**
	 * Constructeur d'un matériel à partir d'information lue dans un fichier de format txt.
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 * @see SBufferedReader
	 */
	public SDefaultMaterial(SBufferedReader sbr)throws IOException, SConstructorException
	{
		this();		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SDefaultMaterial 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	/* (non-Javadoc)
   * @see simGraphic.SMaterial#getName()
   */
	@Override
	public String getName()
	{ 
	  return name; 
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#ambientColor()
	 */
	@Override
	public SColor ambientColor()
	{ 
	  return DEFAULT_AMBIENT_COLOR;
	}
	
	@Override
	public SColor ambientColor(SVectorUV uv)
	{
	  return ambientColor();
	}
	  
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#diffuseColor()
	 */
	@Override
	public SColor diffuseColor()
	{ 
	  return DEFAULT_DIFFUSE_COLOR;
	}

	@Override
  public SColor diffuseColor(SVectorUV uv)
  {
	  return diffuseColor();
  }
   
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#specularColor()
	 */
	@Override
	public SColor specularColor()
	{ 
	  return DEFAULT_SPECULAR_COLOR;
	}
	
	@Override
  public SColor specularColor(SVectorUV uv)
  {
	  return specularColor();
  }
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#transparencyColor()
	 */
	@Override
	public SColor transparencyColor()
	{ 
		//Couleur diffuse de l'objet pondérée par le facteur de transparence
		return DEFAULT_DIFFUSE_COLOR.multiply(DEFAULT_TRANSPARENCY);
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#isReflective()
	 */
	@Override
	public boolean isReflective()
	{ 
		if(reflectivity() == 0.0)
			return false;
		else
			return true;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#reflexivity()
	 */
	@Override
	public double reflectivity()
	{ 
	  return DEFAULT_REFLECTIVITY;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#shininess()
	 */
	@Override
	public double getShininess()
	{ 
	  return DEFAULT_SHININESS;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#isTransparent()
	 */
	@Override
	public boolean isTransparent()
	{ 
		if(transparency() == 0.0)
			return false;
		else
			return true;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#transparency()
	 */
	@Override
	public double transparency()
	{ 
	  return DEFAULT_TRANSPARENCY;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#refractiveIndex()
	 */
	@Override
	public double refractiveIndex()
	{
	  return DEFAULT_REFRACTIVE_INDEX;
	}
	
	/* (non-Javadoc)
	 * @see simGraphic.SMaterial#getOpacity()
	 */
	@Override
	public double opacity()
	{ 
		return 1.0-transparency();
	}
	
	@Override
  public boolean asTexture()
  {
    return false;
  }
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
				
	}

	/* (non-Javadoc)
	 * @see simTools.SReadableWriteable#read(SBufferedReader sbr, int code, String remaining_line) throws Exception
	 */
	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException
	{
		switch(code)
		{
			case SKeyWordDecoder.CODE_NAME : name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_NAME); return true; 			
			  
			default : return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see simTools.SWriteable#write(java.io.BufferedWriter)
	 */
	@Override
	public void write(BufferedWriter bw) throws IOException 
	{
		bw.write(SKeyWordDecoder.KW_DEFAULT_MATERIAL);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les propriétés particuliers de la classe SDefaultMaterial
		writeDefaultMaterialParameter(bw);
				
		bw.write(SKeyWordDecoder.KW_END);
		bw.write("\t#end material" + SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);	
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SDefaultMaterial.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writeDefaultMaterialParameter(BufferedWriter bw)throws IOException 
	{
		bw.write(SKeyWordDecoder.KW_NAME);
		bw.write("\t");
		bw.write(name);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null) 							//Test du null
			return false;
		    
		if (other == this)							//Test de la même référence 
			return true;
		    
		if (!(other instanceof SDefaultMaterial))	//Test d'un type différent
		    	return false;
		    
		//Vérification du nom du matériel (si identique)
		SDefaultMaterial o = (SDefaultMaterial)other;
		
		if(name.equals(o.getName()))
			return true;
		else
			return false;    
	}
	
	@Override
  protected void readingInitialization() throws SInitializationException
  {
	  initialize();
  }
	
	@Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_DEFAULT_MATERIAL;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    return super.getReadableParameterName();
  }
  
}//fin classe SDefaultMaterial
