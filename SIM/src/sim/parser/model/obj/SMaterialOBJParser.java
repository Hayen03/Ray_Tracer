/**
 * 
 */
package sim.parser.model.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
 * Classe qui permet la lecture d'une librairie de matériel pour un format de modèle 3d de type OBJ (Wavefront).
 * Le parser produira toujours au moins un matériel étant le matériel par défaut portant le nom de "default".
 * Ainsi, un fichier vide permettra d'avoir au moins un matériel pouvant ne pas être affecté si le modèle 3d
 * est bien défini dans son ensemble.
 * @author Simon Vézina
 * @since 2015-03-28
 * @version 2015-09-14
 */
public class SMaterialOBJParser {

  private static final String MATERIAL_LIBRAIRY_OBJ_EXTENSION = "mtl";
  
  private static final int KA_CODE = 1;
  private static final int KD_CODE = 2;
  private static final int KS_CODE = 3;
  private static final int NS_CODE = 4;
  private static final int NEWMTL_CODE = 5;
  private static final int TEXTURE_KA_CODE = 6;
  private static final int TEXTURE_KD_CODE = 7;
  private static final int TEXTURE_KS_CODE = 8;
  
  private static final Hashtable<String,Integer> keyword_table = initialiseKeywordTable();
  
  private final String file_name; //nom du fichier .mtl
  
  private String current_material_name;
  private SVertex current_Ka;           //valeur Ka à affecter lors de la fin de la lecture du matériel présentement en lecture
  private SVertex current_Kd;           //valeur Kd à affecter lors de la fin de la lecture du matériel présentement en lecture
  private SVertex current_Ks;           //valeur Ks à affecter lors de la fin de la lecture du matériel présentement en lecture
  private float current_Ns;             //valeur Ns à affecter lors de la fin de la lecture du matériel présentement en lecture
  
  //Nom des textures s'il y a usage
  private String current_texture_Ka;    //propriété de réflexion ambiante de la texture
  private String current_texture_Kd;    //propriété de réflexion diffuse de la texture
  private String current_texture_Ks;    //propriété de réflexion spéculaire de la texture
  
  private List<SMaterialOBJ> material_list; //liste des matériels construit par la parser
  
  /**
   * Constructeur pour parser de librairie de matériel pour format de modèle OBJ (Wavefront).
   * @param file_name - Le nom du fichier en lecture.
   */
  public SMaterialOBJParser(String file_name) throws SMaterialOBJParserException
  {
    this.file_name = file_name; //nom du fichier MTL en lecture
    
    material_list = new LinkedList<SMaterialOBJ>(); //construction de la liste contenant les futurs matériaux
    
    initializeCurrent();  //initialisation par défaut des coefficients si un matériel est créé sans préciser l'ensemble de ces valeurs

    parseMTL(file_name);
  }

  /**
   * Méthode pour obtenir le nom du fichier en lecture.
   * @return Le nom du fichier en lecture.
   */
  public String getFileName()
  {
    return file_name;
  }
  
  /**
   * Méthode pour obtenir la liste des matériels associés à la lecture du fichier MTL.
   * Un matériel par défaut (portant le nom "default) sera automatiquement généré lors de la lecture.
   * Si le fichier est sans erreur, ce matériel ne devrait par être utilisé.
   * @return La liste des matériels du fichier MTL.
   */
  public List<SMaterialOBJ> getMaterialList()
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
    
    table.put("Ka", new Integer(KA_CODE));
    table.put("Kd", new Integer(KD_CODE));
    table.put("Ks", new Integer(KS_CODE));
    table.put("Ns", new Integer(NS_CODE));
    
    table.put("newmtl", new Integer(NEWMTL_CODE));
    
    table.put("map_Ka", new Integer(TEXTURE_KA_CODE));
    table.put("map_Kd", new Integer(TEXTURE_KD_CODE));
    table.put("map_Ks", new Integer(TEXTURE_KS_CODE));
    
    //Autres termes à inclure éventuellement :
    // d : "dissolve" (probablement l'opacité) où 1.0 est complètement opaque
    // Tr : l'inverse de d
    // map_bump : bump mapping
    // Ni : optical_density (range from 0.001 to 10)
    // Tf : transmission filter using RGB values
    // illum : ???
    
    return table;
  }
  
  /**
   * Méhode pour faire la lecture d'un fichier mtl à partir du nom du fichier.
   * @param file_name - Le nom du fichier obj à lire.
   */
  private void parseMTL(String file_name) throws SMaterialOBJParserException
  {
    //Vérification du format du fichier
    if(!SStringUtil.extensionFileLowerCase(file_name).equals(MATERIAL_LIBRAIRY_OBJ_EXTENSION))
      throw new SMaterialOBJParserException("Erreur SMaterialOBJParser 001 : Le fichier '" + file_name + "' n'est pas dans le format '" + MATERIAL_LIBRAIRY_OBJ_EXTENSION + "'.");
    
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", file_name);
    
    if(!search.isFileFound())
      throw new SMaterialOBJParserException("Erreur SMaterialOBJParser 002 : Le fichier '" + file_name + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SMaterialOBJParserException("Erreur SMaterialOBJParser 003 : Le fichier '" + file_name + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    File file = new File(search.getFileFoundList().get(0)); //prendre la 1ier trouvé dans la liste
    
    try
    {
      FileReader fr = new FileReader(file);
      SBufferedReader sbr = new SBufferedReader(fr);
      parseMTL(sbr);
      
    }catch(FileNotFoundException e){
      throw new SMaterialOBJParserException("Erreur SMaterialOBJParser 004 : IMPOSSIBLE! La localisation du fichier a été déjà testé.");     
    }catch(IOException ioe){
      throw new SMaterialOBJParserException("Erreur SMaterialOBJParser 005 : Une erreur de type I/O est survenue.");      
    }
  }
  
  /**
   * Méthode pour lire le fichier de format mtl à l'aide d'un SBufferedReader.
   * @param sbr - Le buffer de lecture.
   */
  private void parseMTL(SBufferedReader sbr)throws IOException
  {               
    boolean end_reading = false;        //condition de fin de lecture
    
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
            
            //Obtenir la ligne restante (sans 1ier caractère d'espacement + mot clé + autres caractères d'espacement)
            String remaining_line = SStringUtil.removeAllFirstSpacerCaracter(line);
            remaining_line = remaining_line.substring(keyword.length());  
            remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
          
            try{
            parseMTLLine(code, remaining_line);                       //analyse de la ligne restante
            }catch(SConstructorException e){
              SLog.logWriteLine("Ligne " + (sbr.atLine()-1) + " - " + e.getMessage());
            }catch(NumberFormatException e){
              SLog.logWriteLine("Ligne " + (sbr.atLine()-1) + " - " + e.getMessage());
            }
          }
        }  
      }
    }//fin while end_reading
    
    //Puisque le format du fichier .mtl ne possède pas un mot clé de fin,
    //lorsque la fin du fichier est atteint, il faut construire le dernier
    //matériel en définition. S'il n'y pas de nom affecté, il sera "default".
    buildMaterial();
  }
  
  /**
   * Méthode pour faire l'analyse de l'expression d'un restant de ligne en fonction du code de lecture lu 
   * au début de la ligne.
   * @param code - Le code représentant la définition de l'expression restant à analyser.
   * @param remaining_line - L'expression à analyser sera le code de lecture.
   * @throws SConstructorException Si un vertex est mal lu.
   * @throws NumberFormatException Si un nombre float est mal lu.
   */
  private void parseMTLLine(int code, String remaining_line)throws SConstructorException, NumberFormatException
  {
    switch(code)
    {
      case KA_CODE : current_Ka = new SVertex(remaining_line); break;
      
      case KD_CODE : current_Kd = new SVertex(remaining_line); break;
      
      case KS_CODE : current_Ks = new SVertex(remaining_line); break;
      
      case NS_CODE : current_Ns = Float.valueOf(remaining_line); break;
      
      case TEXTURE_KA_CODE : remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
                             
                             if(!remaining_line.equals(""))
                               current_texture_Ka = remaining_line;
                             break;
                             
      case TEXTURE_KD_CODE : remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
                             
                             if(!remaining_line.equals(""))
                               current_texture_Kd = remaining_line;
                             
                             break;
      
      case TEXTURE_KS_CODE : remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
                             
                             if(!remaining_line.equals(""))
                               current_texture_Ks = remaining_line;
                             
                             break;
      
      case NEWMTL_CODE :  buildMaterial();                                                            //construire un matériel avec les informations précédentes recueillies
                          initializeCurrent();                                                        //réinitialiser les coefficients pour la prochaines lecture
                          remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);
                          current_material_name = remaining_line;                                     //affectation du prochaine nom du matériel
                          break;
    }
  }
  
  /**
   * Méthode pour construire un nouveau matériel à partir des données courantes lues précédemment.
   * Ce matériel est par la suite ajouté à la liste.
   */
  private void buildMaterial()
  {
    material_list.add(new SMaterialOBJ(current_material_name, current_Ka, current_Kd, current_Ks, current_Ns, current_texture_Ka, current_texture_Kd, current_texture_Ks));
  }
  
  /**
   * Méthode pour faire l'initialisation des coefficients par défaut d'un matériel (s'ils ne sont pas affectés lors de la lecture d'un matériel). 
   */
  private void initializeCurrent()
  {
    current_material_name = SMaterialOBJ.DEFAULT_NAME;
    current_Ka = SMaterialOBJ.DEFAULT_AMBIENT_COEFFICIENT;
    current_Kd = SMaterialOBJ.DEFAULT_DIFFUSE_COEFFICIENT;
    current_Ks = SMaterialOBJ.DEFAULT_SPECULAR_COEFFICIENT;
    current_Ns = SMaterialOBJ.DEFAULT_SHININESS;
    
    current_texture_Ka = null;
    current_texture_Kd = null;
    current_texture_Ks = null;
  }
  
}//fin de la classe SMaterialOBJParser
