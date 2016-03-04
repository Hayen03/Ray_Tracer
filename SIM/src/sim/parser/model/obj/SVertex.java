/**
 * 
 */
package sim.parser.model.obj;

import java.util.StringTokenizer;

import sim.exception.SConstructorException;
import sim.util.SStringUtil;

/**
 * Classe qui correspond à un vertex comprenant 4 composantes de type float. On peut attribuer au vertex 
 * la coordonnée xyz pour une point ou la coordonnée rgba pour une couleur.  
 * @author Simon Vézina
 * @since 2015-03-19
 * @version 2015-04-03
 */
public class SVertex {

  final float[] vertex; //tableau des composantes   
  
  /**
   * Constructeur d'un vertex à trois composantes pour position (x,y,z) ou pour couleur (r,g,b).
   * @param x - La 1ière composante du vertex. 
   * @param y - La 2ième composante du vertex.
   * @param z - La 3ième composante du vertex.
   */
  public SVertex(float x, float y, float z)
  {
      vertex = new float[3];
      vertex[0] = x;
      vertex[1] = y;
      vertex[2] = z;
  }
  
  /**
   * Constructeur d'un vertex.
   * @param expression - L'expression définisant les composantes du vertex.
   * @throws SConstructorException S'il y a eu une erreur dans l'expression du vertex.
   */
  public SVertex(String expression)throws SConstructorException
  {
    try{
      float[] tab = readVertexExpression(expression);
      
      vertex = new float[tab.length];
      for(int i=0; i<vertex.length; i++)
        vertex[i] = tab[i];
    }catch(SModelOBJParserException e){
      throw new SConstructorException("Erreur SVertex 001 : L'expression '" + expression + "' n'est pas adéquat pour définir un vertex." + SStringUtil.END_LINE_CARACTER + e.getMessage());
    }
  }

  /**
   * ...
   * 
   * @return
   */
  public float getX()
  {
	  return vertex[0];
  }
  
  /**
   * ...
   *  
   * @return
   */
  public float getY()
  {
	  return vertex[1];
  }
  
  /**
   *  ...
   *  
   * @return
   */
  public float getZ()
  {
	  return vertex[2];
  }
  
  /**
   *  ...
   *  
   * @return
   */
  public float getU()
  {
	  return vertex[0];
  }
  
  /**
   *  ...
   *  
   * @return
   */
  public float getV()
  {
	  return vertex[1];
  }
  
  /**
   *  ...
   *  
   * @param index
   * @return
   */
  public float get(int index)
  {
	  return vertex[index];
  }
  
  /**
   * Méthode pour faire l'analyse de l'expression d'un vertex à N composantes.
   * @param expression - L'expression du vertex.
   * @return Un tableau à N composantes du vertex.
   * @throws SModelOBJParserException S'il y a eu une erreur dans l'expression du vertex.
   */
  private float[] readVertexExpression(String expression)throws SModelOBJParserException
  {
    String s = "";          //String à convertir en double pour les composantes x, y et z.
    int nb = 0;             //numéro de la composante et index dans le tableau
    String comp = "";       //Nom de la composante en lecture utilisé pour l'envoie d'une Exception s'il y a lieu.
   
    StringTokenizer tokens = new StringTokenizer(expression);
    
    //Vérifier que l'expression n'est pas vide
    if(tokens.countTokens() == 0)
      throw new SModelOBJParserException("Erreur SVertex 001 : L'expression est vide, donc ne contient pas de composante pour définir le vertex.");
      
    float[] tab = new float[tokens.countTokens()]; 
    
    try
    {
      //Analyser l'ensemble des composantes de l'expression représentant le vertex
      while(tokens.hasMoreTokens())
      {
        comp = "x" + nb;
        s = tokens.nextToken();
        tab[nb] = Float.valueOf(s);
        
        nb++;
      }
            
    }catch(NumberFormatException e){ 
      throw new SModelOBJParserException("Erreur SVertex 002 : L'expression '" + s +"' n'est pas valide pour définir la composante '" + comp + "' du vertex.");
    }
    
    return tab;
  }
      
  @Override
  public String toString()
  {
    String expression = "";
    
    for(int i=0; i<vertex.length; i++)
      if(i==0)
        expression = expression + vertex[i];
      else
        expression = expression + "  " + vertex[i];
    
    return expression;
  }
  
  /**
   * Méthode de test pour la classe SFace.
   * @param args
   */
  public static void main(String[] args) throws SModelOBJParserException
  {
    test1();
    test2();
  }
  
  /**
   * Méthode de test #1. 
   */
  private static void test1() throws SModelOBJParserException
  {
    String expression1 = "5.6  6.8  7.7  8.8";
    System.out.println("Expression1 en entré : " + expression1);
    
    SVertex vertex1 = new SVertex(expression1);
    
    System.out.println("Expression en écriture : " + vertex1);
    
    
    String expression2 = "5.6  a";
    System.out.println("Expression1 en entré : " + expression2);
    
    SVertex vertex2 = new SVertex(expression2);
    
    System.out.println("Expression en écriture : " + vertex2);   
  }
  
  private static void test2() throws SModelOBJParserException
  {
    String expression1 = "";
    System.out.println("Expression1 en entré : " + expression1);
    
    SVertex vertex1 = new SVertex(expression1);
    
    System.out.println("Expression en écriture : " + vertex1);  
  }
  
}//fin SVertex
