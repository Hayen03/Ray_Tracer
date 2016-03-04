/**
 * 
 */
package sim.application.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.graphics.STextureComparator;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SFileSearch;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SLog;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * <p>
 * La classe <b>SConsoleComparator</b> représente un comparateur d'image. À partir d'une liste à comparer
 * obtenu après la lecture d'un fichier, cette classe va comparer des fichiers pouvant être interprété
 * comme une image et déterminer si deux images sont identiques. 
 * </p>
 * 
 * <p>
 * Une image peut venir d'un fichier image (comme un jpg, png) ou d'un fichier txt décrivant
 * une scène 3d. Dans ce cas, un calcul de <i>ray tracing</i> sera effectué avant d'obtenir l'image.
 * </p>
 * 
 * @author Simon Vézina
 * @since 2016-01-14
 * @version 2016-01-18
 */
public class SConsoleComparator extends SAbstractReadableWriteable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_TEXTURE_COMPARATOR };
 
  //-------------
  // VARIALBES //
  //-------------
  
  /**
   * La variable <b>texture_comparator_list</b> correspond à la liste des comparateurs de textures qui seront réalisées par l'application.
   */
  private static final List<STextureComparator> texture_comparator_list = new ArrayList<STextureComparator>();
 
  /**
   * Constructeur d'un comparateur d'image à partir du nom d'un fichier texte où se retrouve la liste des éléments à comparer.
   * 
   * @param file_name - Le nom du ficher définissant la liste des éléments à comparer.
   * @throws FileNotFoundException Si le fichier de lecture n'est pas trouvé.
   * @throws IOException Si une erreur de type I/O est survenue.
   * @throws SConstructorException Si une erreur est survenue lors de l'initialisation de l'objet.
   */
  public SConsoleComparator(String file_name) throws FileNotFoundException, IOException, SConstructorException
  {
    //Trouver le fichier à partir du répertoire où l'exécution de l'application est réalisée
    SFileSearch search = new SFileSearch("", file_name);
    
    if(!search.isFileFound())
      throw new FileNotFoundException("Erreur SConsoleComparator 001 : Le fichier '" + file_name + "' n'est pas trouvé.");
    
    if(search.isManyFileFound())
      throw new SConstructorException("Erreur SConsoleComparator : Le fichier '" + file_name + "' a été trouvé plus d'une fois dans les différents sous-répertoires. Veuillez en garder qu'une seule version.");
    
    //Lecture de la scène à partir d'un fichier
    FileReader fr = new FileReader(search.getFileFoundList().get(0));
    SBufferedReader sbr = new SBufferedReader(fr);
    
    try{
      read(sbr);
    }catch(SInitializationException e){
      throw new SConstructorException("Erreur SConsoleComparator 003 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
      
    sbr.close();
    fr.close();
  }

  /**
   * Méthode pour effectuer la comparaison de l'ensemble des éléments de la classe.
   */
  public void compareImage()
  {
    // Tableau des comparaisons
    int[] tab_result = new int[texture_comparator_list.size()];
    
    // Effectuer les comparaisons
    SLog.logWriteLine("Message SIMImageComparator --- Calcul des comparaisons :");
    SLog.logWriteLine();
    
    for(int i = 0; i < texture_comparator_list.size(); i++)
    {
      try{
        
        boolean result = texture_comparator_list.get(i).isTextureEquals();
        
        if(result)
          tab_result[i] = 1;    //code vrai
        else
          tab_result[i] = 0;    //code faux
        
      }catch(SRuntimeException e){
        SLog.logWriteLine("Message SIMImageComparator : Une comparaison n'est pas possible, car certain élément n'ont pas été défini.");
        
        tab_result[i] = 2;      //code indéterminé
      }
    }
    
    //Affichage des résultats
    SLog.logWriteLine();
    SLog.logWriteLine("Message SIMImageComparator --- Résultat des comparaisons :");
    SLog.logWriteLine();
    
    for(int i = 0; i < texture_comparator_list.size(); i++)
    {
      SLog.logWriteLine("Comparaison #" + (i+1) + " :");
      SLog.logWriteLine("Fichier 1 - " + texture_comparator_list.get(i).getFileName1());
      SLog.logWriteLine("Fichier 2 - " + texture_comparator_list.get(i).getFileName2());
      
      // L'affichage d'un résultat
      if(tab_result[i] == 1)
        SLog.logWriteLine("Résultat --> ÉGALES");
      else
        if(tab_result[i] == 0)  
          SLog.logWriteLine("Résultat --> DIFFÉRENTES");
        else
          SLog.logWriteLine("Résultat --> INDÉTERMINÉ !");  
      
      SLog.logWriteLine();
    }
    
  }

  @Override
  public void write(BufferedWriter bw) throws IOException
  {
    this.writeComment(bw, "Liste des comparateurs de texture (image et/ou scène).");
    
    for(STextureComparator comp : texture_comparator_list)
      comp.write(bw);
  }

  @Override
  public String getReadableName()
  {
    return SKeyWordDecoder.KW_CONSOLE_COMPARATOR;
  }
  
  @Override
  protected void readingInitialization() throws SInitializationException
  {
    
  }

  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    try{
      
      switch(code)
      {
        case SKeyWordDecoder.CODE_TEXTURE_COMPARATOR : texture_comparator_list.add(new STextureComparator(sbr)); return true;
        
        default : return false;
      }
      
    }catch(SConstructorException e){
      throw new SReadingException("Erreur SConsoleComparator 004 : Un comparateur de texture n'a pas bien été défini." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }
    
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
}//fin de la classe SConsoleComparator
