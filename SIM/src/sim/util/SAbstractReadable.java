/**
 * 
 */
package sim.util;

import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

import sim.math.SMath;

/**
 * La classe abstraite <b>SAbstractReadable</b> repr�sente un objet pouvant �tre initialis� par une lecture dans un fichier txt. 
 * 
 * @author Simon V�zina
 * @since 2015-01-10
 * @version 2015-12-05
 */
public abstract class SAbstractReadable implements SReadable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond � un tableau contenant l'ensemble des mots cl�s 
   * � utiliser reconnus lors de la d�finition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = { SKeyWordDecoder.KW_END };
  
  /**
   * La constante <b>TRUE_EXPRESSION</b> corresond � la liste des mots reconnus par le syst�me comme �tant une expression <b>vrai</b>.
   */
  protected static final String[] TRUE_EXPRESSION = { "true", "vrai", "yes", "oui", "on" };   
  
  /**
   * La constante <b>FALSE_EXPRESSION</b> correspond � la liste des mots reconnus par le syst�me comme �tant une expression <b>fausse</b>.
   */
  protected static final String[] FALSE_EXPRESSION = { "false", "faux", "no", "non", "off" }; 
    
  /**
   * La constante <b>COMMENT_LETTER</b> correspond aux premi�res lettres d'une ligne lues qui seront consid�r�es comme une ligne de commentaire.
   */
  private static final String COMMENT_LETTER = "#/*-!@";
  
  /**
   * La constante <b>OPEN_COMMENT_MODE</b> correspond au mot cl� d�signant l'ouveture d'un bloc de commentaire.
   */
  private static final String OPEN_COMMENT_MODE = "/*";
  
  /**
   * La constante <b>CLOSE_COMMENT_MODE</b> correspond au mot cl� d�signant la fermeture d'un bloc de commentaire.
   */
  private static final String CLOSE_COMMENT_MODE = "*/";
  
  //-----------------------
  // VARIABLES STATIQUES //
  //-----------------------
  
  /**
   * La variable static <b>comment_mode</b> d�termine si la lecture est en mode commentaire. 
   * En mode commentaire, toute les lignes lues ne sont pas trait�es tant que la variable <i>comment_mode</i> n'est pas <b>false</b>.
   */
  private static boolean comment_mode = false;
  
  //------------
  // M�THODES //
  //------------
  
  @Override
  public String[] getReadableParameterName()
  {
    return KEYWORD_PARAMETER;
  }
  
  @Override
  public void read(SBufferedReader sbr) throws IOException, SInitializationException 
  {
    int code;     //code du mot cl� � analyser
    String line;  //ligne lu qui sera analys�e dont le premier mot correspond au mot cl� qui sera analys�
    
    do
    {
      //initialisation de la lecture d'une ligne
      code = SKeyWordDecoder.CODE_NULL;   
      line = sbr.readLine();
      
      //V�rification si le fichier est � sa derni�re ligne sans avoir termin� la lecture de l'objet courant.
      if(line == null)
      {
        // forcer la pr�sence du keyWord de fin de lecture de l'objet (oubli� dans le fichier)
        code = SKeyWordDecoder.CODE_END;  
        
        // Si la lecture est en mode 'commentaire', il faudra retirer cette option
        // pour ne pas affecter la lecture d'un prochain fichier puisque ce param�tre est statique
        comment_mode = false;
      }
      else
      {
        StringTokenizer tokens = new StringTokenizer(line);   //obtenir les tokens de la ligne a analyser
        
        //S'il y a des tokens � analyser
        if(tokens.countTokens() > 0)
        {
          String key_word = tokens.nextToken();       //obtenir le 1ier mot cl� de la ligne
        
          //V�rifier si le mot d�but par un carat�re signalant une ligne de commentaire
          if(!isCommentExpression(key_word))
          {
            code = SKeyWordDecoder.getKeyWordCode(key_word);  //g�n�r� le code de r�f�rence
                  
            //V�rifier si la lecture de l'objet n'est pas compl�t�
            if(code != SKeyWordDecoder.CODE_END)    
              //V�rifier si la lecture est un code d'erreur
              if(code == SKeyWordDecoder.CODE_ERROR)  
              {  
                // Afficher un message signalant que le "keyword" n'est pas reconnu
                SLog.logWriteLine("Ligne " + (sbr.atLine()-1) + " --- " + "Le mot cl� '" + key_word + "' n'est pas reconnu par le syst�me.");
                
                // Afficher un message signalant une suggestion de mot cl� valide dans la lecture courante de l'objet
                SLog.logWriteLine("Lecture en cours : " + getReadableName());
                SLog.logWriteLine("Mots cl�s admissibles en param�tres : " + SStringUtil.join(getReadableParameterName(), ", "));
              }
              else
              {
                //Retirer la pr�sence du mot cl� � la ligne restante
                String remaining_line = SStringUtil.removeAllFirstSpacerCaracter(line);     //Retirer les 1ier caract�res d'espacement de la ligne (s'il y en a) pour obtenir la ligne restante
                remaining_line = remaining_line.substring(key_word.length());               //Retirer le mot cl� de la ligne restante  
                remaining_line = SStringUtil.removeAllFirstSpacerCaracter(remaining_line);  //Retirer les caract�res d'espacement suivant le mot cl�
                
                //Faire le traitement du mot cl� :
                try{
                  
                  //Afficher un message d'erreur si le code ne fait pas parti de la d�finition de l'objet.
                  if(!read(sbr,code,remaining_line))  
                  {
                    SLog.logWriteLine("Ligne " + (sbr.atLine()-1) + " --- " + "Le mot cl� '" + key_word + "' ne fait pas parti de la d�finition de l'objet '" + getReadableName() + "'."); 
                    SLog.logWriteLine("Mots cl�s admissibles en param�tres : " + SStringUtil.join(getReadableParameterName(), ", "));
                  }
                
                }catch(SReadingException sre){
                  //Afficher un message si le code est interpr�t� sans avoir les bonnes configuration de lecture
                  SLog.logWriteLine("Message SAbstractReadable - Ligne " + (sbr.atLine()-1) + " - " + sre.getMessage());
                }
              }
          }//fin if !iscommentWord
        }//fin if tokens > 0
      }//fin else line != null
    }while(code != SKeyWordDecoder.CODE_END);
    
    readingInitialization(); 
  } 

  /**
   * M�thode pour initialiser l'objet apr�s avoir compl�t� la lecture. 
   * Cette m�thode sera vide s'il n'y a pas de traitement de donn�e � effectuer sur les param�tres de lecture pour rendre l'objet op�rationnel.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  abstract protected void readingInitialization() throws SInitializationException;
  
  /**
   * M�thode pour analyser le code du le mot cl� lu dans le fichier txt et ex�cuter la bonne action 
   * sur les param�tres de l'objet en consid�rant l'information contenue dans le restant 
   * d'une ligne lue ou en continuant la lecture dans le buffer.
   * @param sbr - Le buffer de lecture.
   * @param code - Le code du mot cl� a ex�cuter.
   * @param remaining_line - Le restant de la ligne suivant le dernier mot cl� lu.
   * @throws SReadingException Si une erreur de lecture a �t� d�tect�e dans le fichier.
   * @throws IOException Si une erreur de type I/O a �t� lanc�e.
   * @return <b>true</b> si le code a �t� trait� par l'objet et <b>false</b> sinon.
   */
  abstract protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException, IOException;
    
  /**
   * M�thode pour d�terminer si le mot lue d�signe une ligne de commentaire.
   * Un mot de commentaire est <b>vide</b> ou d�bute par un <b>caract�re particulier</b> (#/*-!@)
   * ou bien la lecture est en mode commentaire (<i>comment_mode</i> = <b>true</b>).
   * @param word - Le mot � analyser.
   * @return <b>true</b> si la ligne est consid�r�e comme un commentaire et <b>false</b> sinon.
   */
  private boolean isCommentExpression(String word)
  {
    //V�rification du mot vide (pour ne pas lire un premier caract�re inexistant)
    if(word.equals(""))
      return true;
    
    //Recherche du mot cl� "/*" ou "*/" pour changer l'�tat de lecture (mode commentaire on/off)
    if(word.length() == 2)
      if(word.equals(OPEN_COMMENT_MODE))
      {      
        SLog.logWriteLine("Message SAbstractReadable - Ouverture d'un bloc de commentaire.");
        comment_mode = true;
        return true;
      }
      else
        if(word.equals(CLOSE_COMMENT_MODE))
        {
          SLog.logWriteLine("Message SAbstractReadable - Fermeture d'un bloc de commentaire.");
          comment_mode = false;
          return true;
        }
    
    // Analyse du premier caract�re de la ligne pour v�rifier si seulement cette ligne est retir�e de l'analyse
    // seulement si la lecture n'est pas en mode commentaire. Dans ce cas, la ligne sera consid�r�e comme un commentaire.
    if(comment_mode)
      return true;
    else
    {
      char first_letter = word.charAt(0);
        
      for(int i = 0; i < COMMENT_LETTER.length(); i++)
        if(first_letter == COMMENT_LETTER.charAt(i))
          return true;
    }
        
    return false;
  }
  
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner une valeur de type int sup�rieure ou �gale � 0.
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette valeur sera utilis�e s'il y a une exception de lanc�e.
   * @return La valeur num�rique.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected int readIntEqualOrGreaterThanZero(String remaining_line, String value_name)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 001 : Il n'y a pas de valeur num�rique affect�e au " + value_name +".");
    
    String s = tokens.nextToken();
        
    try{
      int value = Integer.valueOf(s);
      
      if(value < 0)
        throw new SReadingException("Erreur SAbstractReadable 002 : Le " + value_name + " '" + value + "' doit �tre �gal ou sup�rieur � 0.");
      else
        return value;
    }catch(NumberFormatException e){ 
      throw new SReadingException("Erreur SAbstractReadable 003 : L'expression '" + s + "' n'est pas un nombre de type int pouvant �tre affect� � la d�finition d'un " + value_name + ".");
    } 
  }
  
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner une valeur de type int sup�rieure � 0.
   * 
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @return La valeur num�rique.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected int readIntGreaterThanZero(String remaining_line, String value_name)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 004 : Il n'y a pas de valeur num�rique affect�e au '" + value_name +"'.");
    
    String s = tokens.nextToken();
        
    try{
      int value = Integer.valueOf(s);
      
      if(value < 1)
        throw new SReadingException("Erreur SAbstractReadable 005 : Le '" + value_name + "' de valeur '" + value + "' doit �tre sup�rieur � 0.");
      else
        return value;
      
    }catch(NumberFormatException e){ 
      throw new SReadingException("Erreur SAbstractReadable 006 : L'expression '" + s + "' n'est pas un nombre de type int pouvant �tre affect� � la d�finition d'un '" + value_name + "'.");
    } 
  }
  
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner une valeur de type int �gale ou sup�rieure � 0. 
   * Une expression en String peut �tre �galement reconnue pour d�finir la valeur de type int d�sir�e.
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @param valid_expression - Le tableau des String pouvant �tre reconnue. Leur position dans le tableau d�finisse �galement leur valeur attitr�e.
   * @return La valeur num�rique.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected int readIntOrExpression(String remaining_line, String value_name, String[] valid_expression)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 007 : Il n'y a pas de valeur num�rique affect�e au '" + value_name + "'.");
   
    String s = tokens.nextToken();
      
    try{ 
      
      //Analyse par une valeur num�rique :
      int value = Integer.valueOf(s);
      
      if(value < 0)
        throw new SReadingException("Erreur SAbstractReadable 008 : Le " + value_name + " '" + value + "' doit �tre �gal ou sup�rieur � 0.");
      else
        if(value >= valid_expression.length) //ne pas d�passer le nombre d'expression valide
          throw new SReadingException("Erreur SAbstractReadable 008 : Le " + value_name + " '" + value + "' doit �tre inf�rieur � '" + valid_expression.length + "'. Les expressions suivantes sont �galements valides : " + SStringUtil.join(valid_expression, ", "));
        else
          return value;
      
    }catch(NumberFormatException e){ 
      
      //Analyse par mot cl� : Recherche du mot cl� dans la liste des expressions valides dont l'index correspond � la valeur num�rique attitr�e
      for(int i=0; i<valid_expression.length; i++)
        if(s.toLowerCase(Locale.ENGLISH).equals(valid_expression[i]))
          return i;
      
      throw new SReadingException("Erreur SAbstractReadable 009 : L'expression '" + s + "' n'est pas reconnue pour d�finir la valeur '" + value_name + "'. Les expressions valides sont les suivantes : " + SStringUtil.join(valid_expression, ", "));
    }
  }
      
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner une valeur de type double sup�rieure � 0 (et non nul).
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @return La valeur num�rique.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected double readDoubleGreaterThanZero(String remaining_line, String value_name)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 010 : Il n'y a pas de valeur num�rique affect�e au '" + value_name + "'.");
    
    String s = tokens.nextToken();
      
    try{
      double value = Double.valueOf(s);
      
      if(value < SMath.EPSILON)
        throw new SReadingException("Erreur SAbstractReadable 011 : Le '" + value_name + "' de valeur '" + value + "' doit �tre sup�rieur � '" + SMath.EPSILON + "' pour �tre valide.");
      else
        return value;
    }catch(NumberFormatException e){ 
      throw new SReadingException("Erreur SAbstractReadable 012 : L'expression '" + s + "' n'est pas un nombre de type double pouvant �tre affect� � la d�finition d'un '" + value_name + "'.");
    } 
  }
  
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner un string <b>non vide</b>.
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @return Le string non vide.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected String readStringNotEmpty(String remaining_line, String value_name)throws SReadingException
  {
    if(remaining_line.length() == 0)
      throw new SReadingException("Erreur SAbstractReadable 013 : Le string � analyser est '" + remaining_line + "' et n'est pas une expression ad�quate � affecteur � la valeur '" + value_name + "'.");
    else
      return remaining_line;
  }
  
  /**
   * M�thode pour faire l'analyse d'un string d�signant <b>vrai</b> ou <b>faux</b>. 
   * Un nombre entier peut �galement �tre lu o� <b>0</b> d�signe <b>faux</b> et les <b>autres entiers positifs</b> d�signe <b>vrai</b>.
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @return <b>true</b> si la lecture est interpr�t�e comme <b>vrai</b> et <b>false</b> si la lecture est interpr�t�e comme �tant <b>faux</b>.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected boolean readTrueFalseExpressionOrInt(String remaining_line, String value_name) throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 014 : Il n'y a pas de valeur num�rique affect�e au '" + value_name + "'.");
    
    String s = tokens.nextToken();
    
    try{ 
      
      //Analyse par une valeur num�rique :
      int value = Integer.valueOf(s);
      
      if(value < 0)
        throw new SReadingException("Erreur SAbstractReadable 015 : Le " + value_name + " '" + value + "' doit �tre �gal ou sup�rieur � 0.");
      else
        if(value == 0)
          return false;
        else
          return true;
      
    }catch(NumberFormatException e){ 
      
      //Analyse par mot cl� : true/vrai/yes/oui et false/faux/no/non
      s = s.toLowerCase(Locale.ENGLISH);
      
      for(int i = 0; i < TRUE_EXPRESSION.length; i++)
        if(s.equals(TRUE_EXPRESSION[i]))
          return true;
      
      for(int i = 0; i < FALSE_EXPRESSION.length; i++)
        if(s.equals(FALSE_EXPRESSION[i]))
          return false;
      
      throw new SReadingException("Erreur SAbstractReadable 016 : L'expression '" + s + "' n'est pas reconnue pour d�finir la valeur '" + value_name + "'. Les expressions valides sont les suivantes : " + SStringUtil.join(TRUE_EXPRESSION, ", ") + " et " + SStringUtil.join(FALSE_EXPRESSION, ", "));
    }
  }
  
  /**
   * M�thode pour faire l'analyse d'un string afin d'y retourner une valeur de type double.
   * @param remaining_line - L'expression en String de la valeur.
   * @param value_name - Le nom en mots correspondant � la d�finition de la valeur. Cette information sera utilis�e s'il y a une exception de lanc�e.
   * @return La valeur num�rique.
   * @throws SReadingException S'il y a une erreur de lecture.
   */
  protected double readDouble(String remaining_line, String value_name)throws SReadingException
  {
    StringTokenizer tokens = new StringTokenizer(remaining_line, SStringUtil.REMOVE_CARACTER_TOKENIZER);  
    
    if(tokens.countTokens() == 0)
      throw new SReadingException("Erreur SAbstractReadable 017 : Il n'y a pas de valeur num�rique affect�e au '" + value_name + "'.");
    
    String s = tokens.nextToken();
      
    try{

      return Double.valueOf(s);

    }catch(NumberFormatException e){ 
      throw new SReadingException("Erreur SAbstractReadable 018 : L'expression '" + s + "' n'est pas un nombre de type double pouvant �tre affect� � la d�finition d'un '" + value_name + "'.");
    } 
  }
  
}//fin classe SAbstractReadable
