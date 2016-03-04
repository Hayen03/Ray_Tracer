/**
 * 
 */
package sim.parser.model.obj;

import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.util.SStringUtil;

/**
 * Classe qui représente une Face selon le format OBJ.
 * @author Simon Vézina
 * @since 2015-03-21
 * @version 2015-04-02
 */
public class SFace {

  private static final String FACE_CODE = "f";  //code de reconnaissance d'une Face (lettre lue avant le traitement de l'expression)
  
  //Format de lecture d'un vertex d'une Face.
  private static final int FACE_FORMAT_UNDEFINED = 0;
  private static final int FACE_FORMAT_1 = 1;         //#1 Vertex :             v1          v2          v3          ...
  private static final int FACE_FORMAT_2 = 2;         //#2 Vertex et texture :  v1/vt1      v2/vt2      v3/vt3      ...
  private static final int FACE_FORMAT_3 = 3;         //#3 Vertex et normale :  v1//vn1     v2//vn2     v3//vn3     ...
  private static final int FACE_FORMAT_4 = 4;         //#4 Tout :               v1/vt1/vn1  v2/vt2/vn2  v3/vt3/vn3  ...
  
  private final String material_name; //nom du materiel
  
	private final int[] vertex_index;  //tableau des vertex point
	private final int[] texture_index; //tableau des vertex texture
	private final int[] normal_index;  //tableau des vertex normal
	
	private final int format;
	
	/**
	 * Constructeur d'une Face dans le format OBJ avec nom de matériel par défaut.
	 * @param expression - L'expression complète de la Face.
	 */
	public SFace(String expression)
	{
	  this(SMaterialOBJ.DEFAULT_NAME, expression);
	}
	
	/**
	 * Constructeur d'un Face pour le format OBJ (Wavefront).
	 * @param material_name - Le nom du matériel affecté à la Face.
	 * @param expression - L'expression décrivant la Face.
	 * @throws SConstructorException Si le nom du matériel est null.
	 */
	public SFace(String material_name, String expression)throws SConstructorException
	{
	  if(material_name == null)
	    throw new SConstructorException("Erreur SFace 001 : Le nom du materiel ne peut pas être null.");
	  
	  this.material_name = material_name; 
    
    StringTokenizer tokens = new StringTokenizer(expression);
    
    int nb_token = tokens.countTokens();  //nombre de vertex dans la Face
    
    //Face sans expression
    if(nb_token == 0)
    {
      vertex_index = null;
      normal_index = null;
      texture_index = null;
      format = FACE_FORMAT_UNDEFINED;
    }
    else
    {
      vertex_index = new int[nb_token];
      normal_index = new int[nb_token];
      texture_index = new int[nb_token];
      
      String first_token = tokens.nextToken();
      format = evaluateVertexFormat(first_token);
      
      //Reconstruire le tokenizer pour avoir à nouveau au 1ier élément
      tokens = new StringTokenizer(expression);
      int index = 0;
      
      try{
        
        //Itérer sur l'ensemble des vertex de la Face
        while(tokens.hasMoreTokens())
        {
          parseFaceIndexToken(tokens.nextToken(), index);
          index++;
        }
      }catch(SModelOBJParserException e){
        throw new SConstructorException("Erreur SFace 002 : L'expression '" + expression + "' n'est pas adéquate pour définir une face." + SStringUtil.END_LINE_CARACTER + e.getMessage() );
      }
      
    }
	}
	
	/**
	 * Méthode pour obtenir le nom du matériel attribué à la Face.
	 * @return Le nom du matériel.
	 */
	public String getMaterialName()
	{
	  return material_name;
	}
	
	/**
   * Méthode pour déterminer s'il y a des Vertex associées à la Face.
   * @return <b>true</b> s'il y a des Vertex en association et <b>false</b> sinon.
   */
  public boolean asVertex()
  {
    if(getVertexNumber() == 0)
      return false;
    else
      return true;
  }
  
	/**
	 * Méthode pour obtenir le tableau des indexes des Vertex associés à la Face.
	 * @return Le tableau des indexes des Vertex.
	 * @throws SRuntimeException S'il n'y a pas de Vertex associées à la Face.
	 */
	public int[] getVertexIndex()throws SRuntimeException
	{
	  if(asVertex())
	    return vertex_index;
	  else
	    throw new SRuntimeException("Erreur SFace 002 : Cette Face ne possède pas de vertex en index.");
	}
	
	/**
	 * Méthode pour obtenir le nombre de Vertex associés à la Face  (Vertex, Vertex texture et Vertex normale).
	 * @return Le nombre de Vertex dans la Face.
	 */
	public int getVertexNumber()
	{
	  if(vertex_index == null)
	    return 0;
	  else
	    return vertex_index.length;
	}
	
	/**
	 * Méthode pour déterminer s'il y a des normales associées à la Face.
	 * @return <b>true</b> s'il y a des Vertex normales en association et <b>false</b> sinon.
	 */
	public boolean asVertexNormal()
	{
	  switch(format)
	  {
	    case FACE_FORMAT_UNDEFINED : return false; 
	    case FACE_FORMAT_1 : return false; 
      case FACE_FORMAT_2 : return false;
      case FACE_FORMAT_3 : return true;
      case FACE_FORMAT_4 : return true;
	    
	    default : return false;
	  }
	}
	
	/**
	 * Méthode pour obtenir le tableau des indexes des normales associées à la Face.
	 * @return Le tableau des indexes des normales associées à la Face.
	 * @throws SRuntimeException S'il n'y a pas de normales associées à la Face.
	 */
	public int[] getVertexNormalIndex()throws SRuntimeException
	{
	  if(asVertexNormal())
	    return normal_index;
	  else
	    throw new SRuntimeException("Erreur SFace 003 : Cette Face ne possède pas de 'Vertex normal' en indexe.");
	}
	
	/**
   * Méthode pour déterminer s'il y a des normales associées à la Face.
   * @return <b>true</b> s'il y a des Vertex texture en association et <b>false</b> sinon.
   */
  public boolean asVertexTexture()
  {
    switch(format)
    {
      case FACE_FORMAT_UNDEFINED : return false; 
      case FACE_FORMAT_1 : return false; 
      case FACE_FORMAT_2 : return true;
      case FACE_FORMAT_3 : return false;
      case FACE_FORMAT_4 : return true;
      
      default : return false;
    }
  }
  
  /**
   * Méthode pour obtenir le tableau des indexes des texture associées à la Face.
   * @return Le tableau des indexe des textures associées à la Face.
   * @throws SRuntimeException S'il n'y a pas de texture associées à la Face.
   */
  public int[] getVertexTextureIndex()throws SRuntimeException
  {
    if(asVertexTexture())
      return texture_index;
    else
      throw new SRuntimeException("Erreur SFace 004 : Cette Face ne possède pas de 'Vertex texture' en indexe.");
  }
  
	/**
	 * Méthode pour analyser l'expression d'un élément de la Face étant valide pour un vertex d'index spécifié.
	 * Selon le format de l'expression, l'information sera stockée dans les tableaux de la Face. 
	 * @param token_expression - L'expression associée à un vertex.
	 * @param index - L'indexe du vertex en lecture.
	 * @throw SModelOBJParserException S'il est a des erreurs dans le type de formatage de l'expression d'un vertex de la Face.
	 * @throw SModelOBJParserException Si un élément lu dans l'expression ne peut pas être converti en format int.
	 */
	private void parseFaceIndexToken(String token_expression, int index)throws SModelOBJParserException
	{
		//Le caractère de séparation des index des vertex est par défaut "/" ce qui donne une syntaxe comme : 56/34/34
	  //Cependant, j'ai trouvé des modèles avec la syntaxe suivante : -56/-34/-34
	  //Ainsi, je vais utiliser deux caractères de séparateur ("/" et "-")
	  StringTokenizer elements = new StringTokenizer(token_expression, "/-");
		int[] tab = new int[elements.countTokens()];
				
		//Lecture des éléments de l'expression et écriture dans un tableau (maximumde 3)
		int i = 0;
		
		while(elements.hasMoreTokens())
		{
			try{
				tab[i] = Integer.parseInt(elements.nextToken());
				i++;
			}catch(NumberFormatException e){
				throw new SModelOBJParserException("Erreur SFace 005 : Une expression lue ne peut pas être converti en format int.");
			}
		}
		
		switch(format)
		{
		  case FACE_FORMAT_UNDEFINED : throw new SModelOBJParserException("Erreur SFace 006 : Le format de formatage est indéfini."); 
	    
		  case FACE_FORMAT_1 : fillVertexFormat1(tab, index); break; 
			
			case FACE_FORMAT_2 : fillVertexFormat2(tab, index); break;
                
			case FACE_FORMAT_3 : fillVertexFormat3(tab, index); break;
			
			case FACE_FORMAT_4 : fillVertexFormat4(tab, index); break;
			
			default : throw new SModelOBJParserException("Erreur SFace 007 : Le format de formatage n'est pas reconnu."); 
		}
	}
	
	/**
	 * Méthode pour déterminer le format de lecture des vertex de la Face.
	 * @param expression - L'expression d'un vertex.
	 * @return Le code associé au format de lecture.
	 */
	private int evaluateVertexFormat(String expression)
	{
	  StringTokenizer elements = new StringTokenizer(expression, "/");             //tokeniser l'expression
	  
	  int nb_element = elements.countTokens();                                     //compter le nombre de terme dans l'expression
	  int nb_slash_caracter = SStringUtil.countCaracterInString(expression, '/');  //compter le nombre de '/' dans l'expression
	  
	  switch(nb_element)
	  {
	    case 1 : return FACE_FORMAT_1;             //expression : v1
	    
	    case 2 : if(nb_slash_caracter == 1)
	               return FACE_FORMAT_2;           //expression : v1/vt1
	             else
	               if(nb_slash_caracter == 2)
	                 return FACE_FORMAT_3;         //expression : v1//vn1
	               else
	                 return FACE_FORMAT_UNDEFINED;
	    
	    case 3 : return FACE_FORMAT_4;             //expression : v1/vt1/vn1
	    
	    default :  return FACE_FORMAT_UNDEFINED;
	  }
	}
	
	private void fillVertexFormat1(int[] tab, int index)
	{
		vertex_index[index] = tab[0];
	}
	
	private String writeVertexFormat1(int i)
	{
	  return "" + vertex_index[i] + "";
	}
	
	private void fillVertexFormat2(int[] tab, int index)
  {
    vertex_index[index] = tab[0];
    texture_index[index] = tab[1];
  }
	
	private String writeVertexFormat2(int i)
  {
    return "" + vertex_index[i] + "/" + texture_index[i] + "";
  }
	
	private void fillVertexFormat3(int[] tab, int index)
  {
    vertex_index[index] = tab[0];
    normal_index[index] = tab[1];
  }
	
	private String writeVertexFormat3(int i)
  {
    return "" + vertex_index[i] + "//" + normal_index[i] + "";
  }
	
	private void fillVertexFormat4(int[] tab, int index)
  {
    vertex_index[index] = tab[0];
    texture_index[index] = tab[1];
    normal_index[index] = tab[2];
  }
	
	private String writeVertexFormat4(int i)
  {
    return "" + vertex_index[i] + "/" + texture_index[i] + "/"+ normal_index[i] + "";
  }
	
	@Override
  public String toString()
  {
    String expression = FACE_CODE + "  ";
    
    for(int i=0; i<vertex_index.length; i++)
    {
      switch(format)
      {
        case FACE_FORMAT_UNDEFINED : expression = expression + "???"; break;
      
        case FACE_FORMAT_1 : expression = expression + writeVertexFormat1(i); break; 
      
        case FACE_FORMAT_2 : expression = expression + writeVertexFormat2(i); break; 
                
        case FACE_FORMAT_3 : expression = expression + writeVertexFormat3(i); break; 
        
        case FACE_FORMAT_4 : expression = expression + writeVertexFormat4(i); break; 
      
        default : expression = expression + "???"; break;
      }
      
      expression = expression + "  ";
    }
    return expression;
  }
	
	/**
	 * Méthode de test pour la classe SFace.
	 * @param args
	 */
	public static void main(String[] args) 
	{
	  test1();
	}
	
	private static void test1()
	{
	  String expression1 = "5  6  7  8";
	  System.out.println("Expression1 en entré : " + expression1);
	  
	  SFace face1 = new SFace(expression1);
	  
	  System.out.println("Expression en écriture : " + face1);
	  
	  
	  String expression2 = "5/8  6/9  7/3  8/10";
    System.out.println("Expression2 en entré : " + expression2);
    
    SFace face2 = new SFace(expression2);
    
    System.out.println("Expression en écriture : " + face2);
    
    
    String expression3 = "5//8  6//9  7//13  8//10  13//12";
    System.out.println("Expression3 en entré : " + expression3);
    
    SFace face3 = new SFace(expression3);
    
    System.out.println("Expression en écriture : " + face3);
    
    
    String expression4 = "5/8/14  6/9/56  7/13/78  8/10/12  13/12/32";
    System.out.println("Expression4 en entré : " + expression4);
    
    SFace face4 = new SFace(expression4);
    
    System.out.println("Expression en écriture : " + face4);
	}
}//fin classe SFace
