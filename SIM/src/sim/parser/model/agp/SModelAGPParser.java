/**
 * 
 */
package sim.parser.model.agp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import sim.parser.model.obj.SModelOBJParserException;
import sim.util.SBufferedReader;
import sim.util.SFileSearch;
import sim.util.SStringUtil;


/**
 * Classe représentant un lecteur de fichier .agp correspondant à un modèle 3d dans le format du <i>projet Anaglyphe</i> réalisé par Anik Soulière.
 * 
 * @author Simon Vézina
 * @since 2015-08-01
 * @version 2015-08-02
 */
public class SModelAGPParser {

  private static final String MODEL_EXTENSION = "agp";
  
  private final String file_name;                     //nom du fichier .agp
  private final List<SPointSequence> sequence_list;   //liste des séquences de points
  
  /**
   * Constructeur d'un parser pour objet 3d de de format agp.
   * @param file_name - Le nom du fichier comportant le modèle 3d de format obj.
   * @throws SModelOBJParserException Si le fichier n'est pas adéquat. 
   */
  public SModelAGPParser(String file_name)throws SModelAGPParserException
  {
    this.file_name = file_name;
    sequence_list = new ArrayList<SPointSequence>();
    
    parseAGP(file_name);
  }
  
  /**
   * Méthode pour obtenir le nom du fichier du modèle 3d de format AGP.
   * @return Le nom du fichier.
   */
  public String getFileName()
  {
    return file_name;
  }
  
  /**
   * Méthode pour obtenir la liste des séquences de points du modèle 3d de format AGP.
   * @return La liste des séquences.
   */
  public List<SPointSequence> getListSequence()
  {
    return sequence_list;
  }
  
  /**
   * Méhode pour faire la lecture d'un fichier agp à partir du nom du fichier.
   * @param file_name - Le nom du fichier agp à lire.
   * @throws SModelAGPParserException Si le fichier n'est pas dans le bon format.
   * @throws SModelAGPParserException Si le fichier n'est pas trouvé.
   * @throws SModelAGPParserException Si le fichier existe sous plusieurs versions dans les différents sous-répertoires.
   */
  private void parseAGP(String file_name)throws SModelAGPParserException
  {
    //Vérification du format du fichier
    if(!SStringUtil.extensionFileLowerCase(file_name).equals(MODEL_EXTENSION))
      throw new SModelAGPParserException("Erreur SModelAGPParser 001 : Le fichier '" + file_name + "' n'est pas dans le format '" + MODEL_EXTENSION + "'.");
    
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", file_name);
    
    if(!search.isFileFound())
      throw new SModelAGPParserException("Erreur SModelAGPParser 002 : Le fichier '" + file_name + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SModelAGPParserException("Erreur SModelAGPParser 003 : Le fichier '" + file_name + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    File file = new File(search.getFileFoundList().get(0)); //prendre la 1ier trouvé dans la liste
    
    try
    {
      FileReader fr = new FileReader(file);
      SBufferedReader sbr = new SBufferedReader(fr);
      parseAGP(sbr);
      
    }catch(FileNotFoundException e){
      throw new SModelAGPParserException("Erreur SModelAGPParser 004 : IMPOSSIBLE! La localisation du fichier a été déjà testé.");     
    }catch(IOException ioe){
      throw new SModelAGPParserException("Erreur SModelAGPParser 005 : Une erreur de type I/O est survenue.");      
    }
  }
  
  /**
   * Méthode pour lire le fichier de format agp à l'aide d'un SBufferedReader.
   * 
   * @param sbr - Le buffer de lecture.
   * @throws IOException S'il y a eu une erreur de type I/O lors de la lecture du fichier.
   * @throws SModelAGPParserException S'il y a eu une erreur menant à une incapacité de définir le modèle 3d.
   */
  private void parseAGP(SBufferedReader sbr)throws SModelAGPParserException, IOException
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
          String first_token = tokens.nextToken();        //obtenir le 1ier token à analyser
          first_token = first_token.replaceAll(",",".");  //remplacer les ',' par des '.' car c'est le format employé par java
          
          //Si le 1ier token peut être converti en double, c'est que l'on commence la définition d'une séquence.
          //Si la conversion n'est pas possible, c'est que nous avons une exception correspondant à une ligne de commentaire.
          //Nous allons alors analyser :
          //1ière ligne - Coordonnée x de la séquence de points
          //2ième ligne - Coordonnée y de la séquence de points
          //3ième ligne - Coordonnée z de la séquence de points
          try{
            
            Double.parseDouble(first_token);  //vérifier si le 1ier token est un double, une exception sera lancée si ce n'est pas le cas
             
            String line_x = line;                 //ligne déjà lue
            line_x = line_x.replaceAll(",",".");  //remplacement des ',' par des '.' pour les nombres décimaux
                        
            String line_y = sbr.readLine();       //ligne suivante lue
            line_y = line_y.replaceAll(",",".");  //remplacement des ',' par des '.' pour les nombres décimaux
                                    
            String line_z = sbr.readLine();       //ligne suivante lue
            line_z = line_z.replaceAll(",",".");  //remplacement des ',' par des '.' pour les nombres décimaux
                        
            parsePointSequence(line_x, line_y, line_z, sbr.atLine() - 3);
            
          }catch(NumberFormatException e){
            //rien faire, car c'est une ligne de commentaire ...
          }
        }
      }  
    }//fin while
  }
  
  /**
   * Méthode pour analyser trois string définissant les coordonnées x,y et z d'une séquence de points.
   * @param line_x - La ligne définissant les coordonnées x.
   * @param line_y - La ligne définissant les coordonnées y.
   * @param line_z - La ligne définissant les coordonnées z.
   * @throws SModelAGPParserException Si les trois string n'ont pas le même nombre d'élément en définition.
   * @throws SModelAGPParserException Si une ligne n'est pas bien défini.
   */
  public void parsePointSequence(String line_x, String line_y, String line_z, long reading_line)throws SModelAGPParserException
  {
    //Vérification si une ligne est 'null'. Dans ce cas, il y a une dernière séquence de point qui est alors mal défini
    if(line_x == null || line_y == null || line_z == null)
      throw new SModelAGPParserException("Erreur SModelAGPParseur 006 : À la séquence de ligne " + reading_line + ", une ligne est manquante ce qui rend la séquence de points mal définie.");
    
    //Fragmenter les lignes
    StringTokenizer tokens_x = new StringTokenizer(line_x);  //fragmenter la ligne définissant x
    StringTokenizer tokens_y = new StringTokenizer(line_y);  //fragmenter la ligne définissant y
    StringTokenizer tokens_z = new StringTokenizer(line_z);  //fragmenter la ligne définissant z
    
    //Vérification sur le nombre d'élément par ligne
    if(tokens_x.countTokens() != tokens_y.countTokens() || tokens_x.countTokens() != tokens_z.countTokens())
      throw new SModelAGPParserException("Erreur SModelAGPParseur 007 : À la séquence de ligne " + reading_line + ", il y a " +  tokens_x.countTokens() + " coordonnées x, " + tokens_y.countTokens() + " coordonnées y et " + tokens_z.countTokens() + " coordonnées z. La séquence est mal définie.");
  
    try{
      
      //Construire la séquence de point avec le nombre de point correspondant au nombre de tokens
      SPointSequence sequence = new SPointSequence(tokens_x.countTokens());
      
      int index;  //l'indexage des points
      
      //Remplir la séquence de coordonnée x
      index = 0;
      
      while(tokens_x.hasMoreTokens())
      {
        sequence.setX(index, Double.parseDouble(tokens_x.nextToken()));
        index++;
      }
      
      //Remplir la séquence de coordonnée y
      index = 0;
      
      while(tokens_y.hasMoreTokens())
      {
        sequence.setY(index, Double.parseDouble(tokens_y.nextToken()));
        index++;
      }
      
      //Remplir la séquence de coordonnée z
      index = 0;
      
      while(tokens_z.hasMoreTokens())
      {
        sequence.setZ(index, Double.parseDouble(tokens_z.nextToken()));
        index++;
      }
      
      //La séquence étant terminée, nous pouvons l'ajouter à la liste des séquences
      sequence_list.add(sequence);
      
    }catch(NumberFormatException e){
      throw new SModelAGPParserException("Erreur SModelAGPParseur 008 : À la séquence de ligne " + reading_line + ", un élément lu n'a pas été convertir en valeur numérique. La séquence est ainsi invalide.");
    }
  }
  
  /**
   * Test de la classe SModelAGPParser
   * @param arg
   */
  public static void main(String[] arg)
  {
    test("shuriken.agp");
    test("pyramide.agp");
  }
  
  private static void test(String file_name)
  {
    try{
      
      System.out.println("Fichier en test : '" + file_name + "'.");
      
      SModelAGPParser parser = new SModelAGPParser(file_name);
    
      //Faire l'affichage du contenu
      List<SPointSequence> sequences = parser.getListSequence();
      
      for(SPointSequence s : sequences)
      { 
        for(int i=0; i<s.size(); i++)
          System.out.print(s.getPoint(i).getX() + "\t");
        System.out.println();
        
        for(int i=0; i<s.size(); i++)
          System.out.print(s.getPoint(i).getY() + "\t");
        System.out.println();
        
        for(int i=0; i<s.size(); i++)
          System.out.print(s.getPoint(i).getZ() + "\t");
        System.out.println();
        
        System.out.println();
      }
    }catch(SModelAGPParserException e){
      e.printStackTrace();
      System.out.println("test en échec !!!");
    }
  }
}//fin de la classe SModelAGPParser
