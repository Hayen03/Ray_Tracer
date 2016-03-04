/**
 * 
 */
package sim.graphics;

import java.util.List;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

import sim.exception.SConstructorException;
import sim.geometry.SGeometry;
import sim.graphics.light.SAmbientLight;
import sim.graphics.light.SDirectionalLight;
import sim.graphics.light.SLight;
import sim.graphics.light.SPointLight;
import sim.graphics.material.SBlinnMaterial;
import sim.graphics.material.SBlinnTextureMaterial;
import sim.graphics.material.SMaterial;
import sim.graphics.SModel;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SFileSearch;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SLog;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SScene</b> représente une scène en trois dimensions. 
 * Une scène comprend des primitives, des matériaux, des lumières, ainsi que des paramètres de visionnement comme une caméra, un viewport et le ray tracer.
 *  
 * @author Simon Vézina
 * @since 2014-12-26
 * @version 2015-12-03
 */
public class SScene extends SAbstractReadableWriteable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_CAMERA, SKeyWordDecoder.KW_VIEWPORT, SKeyWordDecoder.KW_RAYTRACER,
    SKeyWordDecoder.KW_PRIMITIVE, SKeyWordDecoder.KW_BLINN_MATERIAL, SKeyWordDecoder.KW_TEXTURE_MATERIAL, 
    SKeyWordDecoder.KW_AMBIENT_LIGHT, SKeyWordDecoder.KW_DIRECTIONAL_LIGHT, SKeyWordDecoder.KW_POINT_LIGHT, 
    SKeyWordDecoder.KW_MODEL     
  };
  
  //-------------
  // VARIABLES //
  //-------------
  
  /**
   * La variable 'camera' correspond à la caméra qui sera utilisée pour visualiser la scène.
   */
	private SCamera camera;					                 
	
	/**
	 * La variable 'viewport' correspond à l'écran de vue où seront écrit les différents pixels de couleur calculés par le ray tracer.
	 */
	private SViewport viewport;		
	
	/**
	 * La variable 'raytracer_builder' correspond à un constructeur du raytracer. Selon ses paramètres, un ray tracer particulier sera éventuellement construit.
	 */
	private SRaytracerBuilder raytracer_builder;     
			
	/**
	 * La variable 'primitive_list' correspond à la liste des primitives situées dans la scène. Une primitive est une géométrie affectée à un matériel.
	 */
	private final List<SPrimitive> primitive_list;   
	
	/**
	 * La variable 'material_list' correspond à la liste des différents matériaux disponibles lors du calcul du rendu.
	 */
	private final List<SMaterial> material_list;		 
	
	/**
	 * La variable 'light_list' correspond à la liste des sources de lumières disponibles pour faire les calculs d'illumination de la scène lors du ray tracing.
	 */
	private final List<SLight> light_list;				   
	
	/**
	 * La variable 'model_list' correspond à la liste des différents modèles situés dans la scène. Un modèle comprend plusieurs primitives (géométrie + matériel).
	 */
	private final List<SModel> model_list;           
		
	/**
	 * La variable 'geometry_list' correspond à la liste des différentes géométries situés dans la scène.
	 */
	private final List<SGeometry> geometry_list;     
	
	//----------------
	// CONSTRUCTEUR //
	//----------------
	
	/**
	 * Constructeur avec lecture d'un fichier txt.
	 * 
	 * @param file_name - Le nom du fichier en lecture.
	 * @throws FileNotFoundException Si le fichier de lecture n'a pas été trouvé.
	 * @throws IOException Si une erreur de type I/O est survenue.
	 * @throws SConstructorException Si une erreur est survenue lors de la construction.
	 */
	public SScene(String file_name) throws FileNotFoundException, IOException, SConstructorException
	{
	  camera = new SCamera();                             
    viewport = new SViewport();                         
    raytracer_builder = new SRaytracerBuilder();        
        
    primitive_list = new LinkedList<SPrimitive>();  
    material_list = new LinkedList<SMaterial>();     
    light_list = new LinkedList<SLight>();              
    model_list = new LinkedList<SModel>();              
      
    geometry_list = new LinkedList<SGeometry>();   
		
		//Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", file_name);
    
    if(!search.isFileFound())
      throw new FileNotFoundException("Erreur SScene 001 : Le fichier '" + file_name + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SConstructorException("Erreur SScene 002 : Le fichier '" + file_name + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
		//Lecture de la scène à partir d'un fichier
		FileReader fr = new FileReader(search.getFileFoundList().get(0));
		SBufferedReader sbr = new SBufferedReader(fr);
		
		try{
		  read(sbr);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SScene 003 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
		  
		sbr.close();
		fr.close();
	}
		
	//------------
	// MÉTHODES //
	//------------
	
	/**
	 * Méthode pour obtenir la caméra de la scene.
	 * 
	 * @return La caméra de la scene.
	 */
	public SCamera getCamera()
	{ 
	  return camera;
	}
	
	/**
	 * Méthode pour obtenir le viewport de la scene.
	 * 
	 * @return Le viewport de la scene.
	 */
	public SViewport getViewport()
	{ 
	  return viewport;
	}
	
	/**
	 * Méthode pour obtenir la liste des primitives de la scène.
	 * 
	 * @return La liste des primitives.
	 */
	public List<SPrimitive> getPrimitiveList(){ return primitive_list; }
	
	/**
	 * Méthode pour obtenir la liste des sources de lumière de la scène.
	 * 
	 * @return La liste des sources de lumière.
	 */
	public List<SLight> getLightList(){ return light_list; }
	
	/**
	 * Méthode pour faire la construction du raytracer associé à la scène en fonction de ses paramètres comme le viewport, la caméra, les géométries et les lumières.
	 * 
	 * @return Le ray tracer de la scène.
	 */
	public SRaytracer buildRaytracer()
	{
	  return raytracer_builder.buildRaytracer(viewport, camera, geometry_list, light_list);
	}

	@Override
	public void write(BufferedWriter bw) throws IOException
	{
		writeComment(bw, "Paramètres du rendu");
		
	  camera.write(bw);
		viewport.write(bw);
		raytracer_builder.write(bw);
			
		writeComment(bw, "Modèles de la scène");
		    
		//Écrire les modèle
    for(SModel m : model_list)
      m.write(bw);
    
    writeComment(bw, "Primitives de la scène");
       
    //Écrire les primitives
    for(SPrimitive p : primitive_list)
      p.write(bw);
    
    writeComment(bw, "Matériaux de la scène");
    
   	//Écrire les matériaux
		for(SMaterial m : material_list)
			m.write(bw);
		
		writeComment(bw, "Sources de lumière de la scène");
				
		//Écrire les lumières
		for(SLight l : light_list)
			l.write(bw);		
	}
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
		int primitive_count = 0;
		
		//Remplir l'espace de géométrie avec les géométries des primitives disponibles.
		//Les primitives sans géométrie (qui sont à null) ne seront pas ajoutées à l'espace des géométries.
		for(SPrimitive p : primitive_list)
			if(p.getGeometry() != null)							         //Vérification de la présence d'une géométrie
			{
			  geometry_list.add(p.getGeometry());	//Ajout de la géométrie
			  primitive_count++;
			}
	
		//Mettre l'ensemble des géométries des modèle dans l'espace des géométries
		for(SModel m : model_list)
		  for(SPrimitive p : m.getPrimitiveList())
		    if(p.getGeometry() != null)                     //Vérification de la présence d'une géométrie
		    {
		      geometry_list.add(p.getGeometry());  //Ajout de la géométrie
		      primitive_count++;
		    }
		
		//Faire l'assignation des matériaux aux primitives
		SMaterialAffectation m_affectation = new SMaterialAffectation(primitive_list, material_list);
		m_affectation.affectation();
		
		//Message du comptage des primitives
    SLog.logWriteLine("Message SScene : La scène comprend " + primitive_count + " primitives.");
	}
	
	@Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException, IOException
	{
		try{
  	  switch(code)
  		{
  			case SKeyWordDecoder.CODE_CAMERA :				     camera = new SCamera(sbr); return true;
  															
  			case SKeyWordDecoder.CODE_VIEWPORT :			     viewport = new SViewport(sbr); return true;
  											
  			case SKeyWordDecoder.CODE_RAYTRACER :          raytracer_builder = new SRaytracerBuilder(sbr); return true;
  			
  			case SKeyWordDecoder.CODE_PRIMITIVE :			     primitive_list.add(new SPrimitive(sbr)); return true;
  															
  			case SKeyWordDecoder.CODE_BLINN_MATERIAL:		   material_list.add(new SBlinnMaterial(sbr)); return true;
  			
  			case SKeyWordDecoder.CODE_TEXTURE_MATERIAL :   material_list.add(new SBlinnTextureMaterial(sbr)); return true;
  			
  			case SKeyWordDecoder.CODE_AMBIENT_LIGHT :		   light_list.add(new SAmbientLight(sbr));	return true;
  															
  			case SKeyWordDecoder.CODE_DIRECTIONAL_LIGHT :	 light_list.add(new SDirectionalLight(sbr));	return true;
  															
  			case SKeyWordDecoder.CODE_POINT_LIGHT :			   light_list.add(new SPointLight(sbr));	return true;
  			
  			case SKeyWordDecoder.CODE_MODEL :              SModelReader m_reader = new SModelReader(sbr);
  			
  			                                               if(m_reader.asRead())
  			                                                 model_list.add((SModel)m_reader.getValue());
  			                                               return true;
  			
  			default : return false;
  		}
		}catch(SConstructorException e){
		  throw new SReadingException("Erreur SScene 004 : La construction d'un élément de la scène est impossible." + SStringUtil.END_LINE_CARACTER + "\n" + e.getMessage());
		}
	}
			
	@Override
  protected void readingInitialization() throws SInitializationException
  {
    initialize();
  }
	
	@Override
  public String getReadableName()
  {
	  return SKeyWordDecoder.KW_SCENE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SScene
