/**
 * 
 */
package sim.graphics;

import java.io.BufferedWriter;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.geometry.SGeometry;
import sim.geometry.SGeometryReader;
import sim.graphics.material.SDefaultMaterial;
import sim.graphics.material.SMaterial;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SLog;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SPrimitive</b> représente un objet comprenant une géométrie et un matériel.
 * 
 * @author Simon Vézina
 * @since 2015-01-06
 * @version 2015-12-04
 */
public class SPrimitive extends SAbstractReadableWriteable{

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_MATERIAL_NAME };
  
  //-------------
  // VARIABLES //
  //-------------
  
	private SGeometry geometry;				    //la géométrie de la primitive
	private SMaterial material;				    //le matériel de la primitive
	
	private String material_name;			    //nom du matériel si affectation à l'extérieur 
	private boolean isGeometry_selected;	//détermine si la géométrie a été sélectionné lors de la lecture
	
	/**
	 * Constructeur vide d'une primitive.
	 */
	public SPrimitive() 
	{
	  this(null, null);
	}

	/**
	 * Constructeur avec une géométrie et un matériel sans relation préalable.
	 * @param geometry - La géométrie de la primitive.
	 * @param material - Le matériel de la primitive.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 */
	public SPrimitive(SGeometry geometry, SMaterial material) throws SConstructorException
	{
	  // Affectation de la géométrie
	  this.geometry = geometry;
	  
	  if(geometry == null)
	    isGeometry_selected = false;
	  else
	  {
	    isGeometry_selected = true;
	    geometry.setPrimitiveParent(this);
	  }
	  
	  // Affectation du matériel
	  this.material = material;
	  
	  if(material == null)
	    material_name = SDefaultMaterial.DEFAULT_MATERIAL_NAME;
	  else
	    material_name = material.getName();
	  
	  try{
      initialize();
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SPrimitive 001 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
	}
	
	/**
	 * Constructeur d'une primitive à partir d'information lue dans un fichier de format txt.
	 * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si une erreur est survenue à la construction.
	 * @see SBufferedReader
	 */
	public SPrimitive(SBufferedReader sbr)throws IOException, SConstructorException
	{
		this();		
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SPrimitive 002 : Une erreur d'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Méthode pour obtenir le nom du matériel à attribuer à cette primitive.
	 * @return Le nom de du matériel.
	 */
	public String getMaterialName()
	{ 
	  return material_name;
	}
	
	/**
	 * Méthode pour obtenir la géométrie de la primitive. 
	 * Il est important de préciser qu'une primitive <b>sans géométrie</b> retourne <b>null</b> comme référence.
	 * 
	 * @return La géométrie de la primitive ou <b>null</b> si elle n'en a pas.
	 */
	public SGeometry getGeometry()
	{ 
	  return geometry;
	}
	
	/**
	 * Méthode pour obtenir le matériel affecté à la primitive. 
	 * @return Le matériel de la primitive.
	 * @throws SRuntimeException Si l'affectation du matériel à la primitive n'a pas été encore effectuée. 
	 */
	public SMaterial getMaterial()throws SRuntimeException
	{ 
		if(material == null)
			throw new SRuntimeException("Erreur SPrimitive 004 : L'affectation du matériel à cette primitive n'a pas encore été réalisée.");
		else
			return material;
	}
	
	/**
	 * Méthode pour affecter un matériel à une primitive. L'affectation sera réalisée si le nom du matériel est celui défini dans la primitive.
	 * @param new_material - Le matériel à affecter à la primitive.
	 * @throws SRuntimeException Si le nom du matériel n'est pas celui accepté par la primitive.
	 */
	public void setMaterial(SMaterial new_material)throws SRuntimeException
	{
		//Vérification que le nom du matériel accepté par cette primitive est bien celui du nouveau matériel en paramètre
		if(new_material.getName().equals(material_name))
			material = new_material;
		else
			throw new SRuntimeException("Erreur SPrimitive 005 : L'affectation de ce matériel n'est pas possible puisque son nom n'est pas celui accepté par cette primitive.");
	}
	
	@Override
	public void write(BufferedWriter bw) throws IOException 
	{
		bw.write(SKeyWordDecoder.KW_PRIMITIVE);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les paramètres de la classe SPrimitive
		writeSPrimitiveParameter(bw);
		
		bw.write(SKeyWordDecoder.KW_END);
		bw.write("\t#end primitive" + SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}

	/**
	 * Méthode pour écrire les paramètres associés à la classe SPrimitive.
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException S'il y a une erreur d'écriture.
	 * @see IOException
	 */
	protected void writeSPrimitiveParameter(BufferedWriter bw)throws IOException 
	{
		bw.write(SKeyWordDecoder.KW_MATERIAL_NAME);
		bw.write("\t");
		bw.write(material_name);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		if(geometry != null)
			geometry.write(bw);
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
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException, IOException
	{
		//Analyser le code sur la lecture d'une géométrie à l'aide du lecteur de géométrie
	  SGeometryReader reader = new SGeometryReader(sbr, code, this);
    
	  //S'il y a eu lecture d'une géométrie
    if(reader.isRead())
      if(!isGeometry_selected)
      {
        geometry = reader.getGeometry();
        isGeometry_selected = true;
        return true;
      }
      else
        throw new SReadingException("Erreur SPrimitive 006 : Une géométrie a déjà été sélectionnée pour cette primitive.");   
      
	  //Analyser le code sur d'autres paramètres
	  switch(code)
		{
			case SKeyWordDecoder.CODE_MATERIAL_NAME : material_name = readStringNotEmpty(remaining_line, "nom du matériel"); return true;
			
			default : return false;
		}
	}
	
	@Override
	protected void readingInitialization() throws SInitializationException
	{
	  initialize();
	  
	  //Affichage de message si la lecture est incomplète
    if(!isGeometry_selected)
      SLog.logWriteLine("Message SPrimitive : Une primitive a été initialisée sans géométrie après une lecture.");
    
    if(material_name.equals(SDefaultMaterial.DEFAULT_MATERIAL_NAME))
      SLog.logWriteLine("Message SPrimitive : Une primitive a été initialisée sans nom de matériel après une lecture.");
	}
	 
	@Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_PRIMITIVE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    // Mettre les mots clé du lecteur de géométrie (SGeometryReader)
    other_parameters = SStringUtil.merge(other_parameters, SGeometryReader.KEYWORD_PARAMETER);
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin classe SPrimitive
