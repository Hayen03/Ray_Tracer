/**
 * 
 */
package sim.parser.model.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.util.SBufferedReader;
import sim.util.SFileSearch;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * Classe représentant un parser de modèle 3d de format obj (Wavefront).
 * @author Simon Vézina
 * @since 2015-03-19
 * @version 2015-08-03
 */
public class SModelOBJParser {

  private static final String MODEL_EXTENSION = "obj";
  
  private static final int VERTEX_CODE = 1;
  private static final int TEXTURE_CODE = 2;
  private static final int NORMAL_CODE = 3;
  private static final int FACE_CODE = 4;
  private static final int MTL_LIBRARY_CODE = 5;
  private static final int USEMTL_CODE = 6;
  
  private static final Hashtable<String,Integer> keyword_table = initialiseKeywordTable();
  
  private final String file_name;                 //nom du fichier .obj
  
  private final ArrayList<SVertex> vertex_list;   //liste des vertex du modèle 3d(commençant à l'index 1)
  
  private final ArrayList<SVertex> texture_list;  //liste des vertex du modèle 3d(commençant à l'index 1)
  
  private final ArrayList<SVertex> normal_list;   //liste des normale du modèle 3d(commençant à l'index 1)
  
  private final List<SFace> face_list;		        //liste des faces (index des vertex pour décrire les formes géométriques)
  
  private final List<SPolygonOBJ> polygon_list;   //liste des polygones du modèle
  
  private String current_material_name;           //nom du matériel présentement à affecter au Face en lecture.
  
  private final List<SMaterialOBJ> material_list; //liste des matériels
  
  /**
   * Constructeur d'un parser pour objet 3d de de format obj.
   * @param file_name - Le nom du fichier comportant le modèle 3d de format obj.
   * @throws SModelOBJParserException Si le fichier n'est pas adéquat. 
   */
  public SModelOBJParser(String file_name)throws SModelOBJParserException
  {
    this.file_name = file_name;
    
    vertex_list = new ArrayList<SVertex>();
    vertex_list.add(null);  //l'index 0 n'est pas admissible      
    
    texture_list = new ArrayList<SVertex>();
    texture_list.add(null);  //l'index 0 n'est pas admissible
    
    normal_list = new ArrayList<SVertex>();
    normal_list.add(null);  //l'index 0 n'est pas admissible
    
    current_material_name = SMaterialOBJ.DEFAULT_NAME;  //nom par défaut du matériel à affecté à une face
    face_list = new LinkedList<SFace>();
        
    material_list = new LinkedList<SMaterialOBJ>();
    
    parseOBJ(file_name);    //lecture du fichier et construction des trois types de vertex et des faces
    
    polygon_list = new LinkedList<SPolygonOBJ>();
    buildPolygon();         //construction des polygones en affectant les vertex selon la description des faces
  
    //Vérification si le fichier OBJ ne contient pas de référence à un fichier MTL.
    //Si c'est le cas, il faut construire un SMaterialOBJ par défaut afin que la 
    //référence au nom du matériel "default" puisse exister 
    if(material_list.isEmpty())
    {
      SLog.logWriteLine("Message SModelOBJParser : Le modèle présent ne possède pas de matériel. Un matériel par défaut sera généré.");
      material_list.add(new SMaterialOBJ());
    }
  }

  /**
   * Méthode pour obtenir le nom du fichier du modèle 3d de format OBJ.
   * @return Le nom du fichier.
   */
  public String getFileName()
  {
	  return file_name;
  }
  
  /**
   * Méthode pour obtenir la liste des polygones définissant le modèle 3d de format OBJ.
   * @return La liste des polygones du modèle.
   */
  public List<SPolygonOBJ> getListPolygon()
  {
	  return polygon_list;
  }
  
  /**
   * Méthode pour obtenir la liste des matériels du modèle 3d de format OBJ. Dans cette liste,
   * on y retrouve un matériel par défaut (portant le nom "default"). Ce matériel peut se retrouver
   * plusieurs fois dans la liste, car il sera présent pour chaque fichier MTL lu par le modèle.
   * @return La liste des matériaux du modèle.
   */
  public List<SMaterialOBJ> getListMaterial()
  {
    return material_list;
  }
  
  /**
   * Méthode pour faire la table de correspondance entre un mot clé un un code d'action.
   * @return La table de mot clé.
   */
  private static Hashtable<String,Integer> initialiseKeywordTable()
  {
    Hashtable<String,Integer> table = new Hashtable<String,Integer>();
    
    table.put("v", new Integer(VERTEX_CODE));
    table.put("vt", new Integer(TEXTURE_CODE));
    table.put("vn", new Integer(NORMAL_CODE));
    table.put("f", new Integer(FACE_CODE));
    table.put("mtllib", new Integer(MTL_LIBRARY_CODE));
    table.put("usemtl", new Integer(USEMTL_CODE));
    
    return table;
  }
  
  /**
   * Méhode pour faire la lecture d'un fichier obj à partir du nom du fichier.
   * @param file_name - Le nom du fichier obj à lire.
   * @throws SModelOBJParserException Si le fichier n'est pas dans le bon format.
   * @throws SModelOBJParserException Si le fichier n'est pas trouvé.
   * @throws SModelOBJParserException Si le fichier existe sous plusieurs versions dans les différents sous-répertoires.
   */
  private void parseOBJ(String file_name)throws SModelOBJParserException
  {
    //Vérification du format du fichier
    if(!SStringUtil.extensionFileLowerCase(file_name).equals(MODEL_EXTENSION))
    	throw new SModelOBJParserException("Erreur SModelOBJParser 001 : Le fichier '" + file_name + "' n'est pas dans le format '" + MODEL_EXTENSION + "'.");
    
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", file_name);
    
    if(!search.isFileFound())
      throw new SModelOBJParserException("Erreur SModelOBJParser 002 : Le fichier '" + file_name + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SModelOBJParserException("Erreur SModelOBJParser 003 : Le fichier '" + file_name + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    File file = new File(search.getFileFoundList().get(0)); //prendre la 1ier trouvé dans la liste
    
    try
    {
      FileReader fr = new FileReader(file);
      SBufferedReader sbr = new SBufferedReader(fr);
      parseOBJ(sbr);
      
    }catch(FileNotFoundException e){
      throw new SModelOBJParserException("Erreur SModelOBJParser 004 : IMPOSSIBLE! La localisation du fichier a été déjà testé.");     
    }catch(IOException ioe){
      throw new SModelOBJParserException("Erreur SModelOBJParser 005 : Une erreur de type I/O est survenue.");      
    }
  }
  
  /**
   * Méthode pour lire le fichier de format obj à l'aide d'un SBufferedReader.
   * @param sbr - Le buffer de lecture.
   * @throws IOException S'il y a eu une erreur de type I/O lors de la lecture du fichier.
   * @throws SModelOBJParserException S'il y a eu une erreur menant à une incapacité de définir le modèle 3d.
   */
  private void parseOBJ(SBufferedReader sbr)throws SModelOBJParserException, IOException
  {			          
	  boolean end_reading = false;	      //condition de fin de lecture
	  
	  while(!end_reading)
	  {
	    String line = sbr.readLine();    //ligne courante en lecture
		  
	    //Si la ligne lu est "null", la fin du fichier est atteinte
		  if(line == null)
			  end_reading = true;
		  else
		  {
			  StringTokenizer tokens = new StringTokenizer(line);  //fragmenter la ligne lue en éliminant les caractères d'espacement
			  
			  //S'il y a un token à analyser
			  if(tokens.hasMoreTokens())
			  {
			    String keyword = tokens.nextToken();     //obtenir le mot clé étant le 1ier mot de la ligne           
			  			  
			    //Si le mot clé doit être analysé
  			  if(keyword_table.containsKey(keyword))
  			  {
    				int code = (keyword_table.get(keyword)).intValue();       //code de lecture
    				
    				//Obtenir la ligne restante (sans 1ier caractère d'espacement + mot clé + autres caractères d'espacement + ligne restante ...)
    				String remaining_line = SStringUtil.removeAllFirstSpacerCaracter(line);
    				remaining_line = remaining_line.substring(keyword.length());	
    				remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
    				    				
    				try{
    				  parseOBJLine(code, remaining_line);                     //analyse de la ligne restante
    				}catch(SModelOBJParserException e){
    				  throw new SModelOBJParserException("Ligne " + (sbr.atLine()-1) + " - " + e.getMessage());
    				}
  			  }
			  }  
		  }
	  }//fin while end_reading
  }
  
  /**
   * Méthode pour faire l'analyse de l'expression d'un restant de ligne en fonction du code de lecture lu 
   * au début de la ligne.
   * @param code - Le code représentant la définition de l'expression restant à analyser.
   * @param remaining_line - L'expression à analyser sera le code de lecture.
   * @throws SModelOBJParserException S'il y a eu une erreur de lecture menant à un échec de la lecture du modèle 3d.
   */
  private void parseOBJLine(int code, String remaining_line)throws SModelOBJParserException
  {
	  switch(code)
	  {
	  	case VERTEX_CODE :  try{
	  	                    vertex_list.add(new SVertex(remaining_line));
	  	                    }catch(SConstructorException e){
	  	                      throw new SModelOBJParserException("Erreur SModelOBJParser 006 : Un vertex est mal défini. " + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
	  	                    }
	  	                    break;
	  	
	  	case TEXTURE_CODE : try{
	  	                    texture_list.add(new SVertex(remaining_line)); 
	  	                    }catch(SConstructorException e){
                            throw new SModelOBJParserException("Erreur SModelOBJParser 007 : Un vertex texture est mal défini." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
                          }
	  	                    break;
	  	
	  	case NORMAL_CODE :  try{
	  	                    normal_list.add(new SVertex(remaining_line));
	  	                    }catch(SConstructorException e){
                            throw new SModelOBJParserException("Erreur SModelOBJParser 008 : Un vertex normal est mal défini." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
                          }
	  	                    break;
	  	                          
	  	case FACE_CODE :    try{
	  	                    face_list.add(new SFace(current_material_name, remaining_line));
	  	                    }catch(SConstructorException e){
	  	                      SLog.logWriteLine("Message SModelOBJParser : Une face est mal définie." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
	  	                    }
	  	                    break;
	  	
	  	case USEMTL_CODE : current_material_name = remaining_line; 
	  	                   break;
	  	
	  	case MTL_LIBRARY_CODE : try{
	  	                        
	  	                        SLog.logWriteLine("Message SModelOBJParser : Lecture de la librairie de matériaux '" + remaining_line + "'.");
	  	                        
	  	                        SMaterialOBJParser p = new SMaterialOBJParser(remaining_line);  //lire la librairie des matériels
	  	                        List<SMaterialOBJ> l = p.getMaterialList();                     //obtenir la liste des matériels
	  	                        material_list.addAll(l);                                        //ajouter l'ensemble des matériels à la liste du matériel
	  	                        }catch(SMaterialOBJParserException e){
	  	                          SLog.logWriteLine("Message SModelOBJParser : Une librairie de matériaux est mal définie." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage());
	  	                        }
	  	                        break;
	  }
  }
  
  /**
   * Méthode pour construire l'ensemble des polygones du modèle à partir de la liste des faces (descriptions des vertex requis identifiés par indexage dans les listes).
   */
  private void buildPolygon()
  {
    //Construire l'ensemble des polygones du modèle
    for(SFace f : face_list)
      polygon_list.add(new SPolygonOBJ(f, vertex_list, texture_list, normal_list));
  }
     
  @Override
  public String toString()
  {
    String vertex_nb = "Vertex number : " + (vertex_list.size()-1);
    String texture_nb = "Texture number : " + (texture_list.size()-1);
    String normal_nb = "Normal number : " + (normal_list.size()-1);
    String face_nb = "Face number :   " + face_list.size();
    String material_nb = "Material number :   " + material_list.size();
    
    return vertex_nb + SStringUtil.END_LINE_CARACTER + texture_nb + SStringUtil.END_LINE_CARACTER + normal_nb + SStringUtil.END_LINE_CARACTER + face_nb + SStringUtil.END_LINE_CARACTER + material_nb + SStringUtil.END_LINE_CARACTER;
  }
  
  /**
   * Méthode de test pour la classe SFace.
   * @param args
   */
  public static void main(String[] args) throws FileNotFoundException, SModelOBJParserException
  {
    test1();
    test2();
  }
  
  /**
   * Méthode de test #1. 
   */
  private static void test1() throws FileNotFoundException, SModelOBJParserException
  {
    String model_name = "lamp.obj";
    SModelOBJParser parser = new SModelOBJParser(model_name);
    
    System.out.println(model_name);
    System.out.println(parser);
  }
 
  /**
   * Méthode de test #1. 
   */
  private static void test2() throws FileNotFoundException, SModelOBJParserException
  {
    String model_name = "foot.obj";
    SModelOBJParser parser = new SModelOBJParser(model_name);
        
    System.out.println(model_name);
    System.out.println(parser);
  }
}//fin SModelOBJParser
