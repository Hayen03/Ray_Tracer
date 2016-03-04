/**
 * 
 */
package sim.geometry;

import java.io.IOException;

import sim.exception.SConstructorException;
import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;
import sim.graphics.SPrimitive;
import sim.util.SAbstractReadable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SGeometryReader</b> représente un lecteur de géométrie. 
 * À partir d'un SBufferedReader et d'un code de référence,
 * ce lecteur va construire la bonne géométrie en fonction des paramètres lus dans le fichier txt. 
 * Si le code de référence n'est pas reconnu, rien de plus sera lu dans le fichier txt. 
 * 
 * @author Simon Vézina
 * @since 2015-07-18
 * @version 2015-12-02
 */ 
public class SGeometryReader extends SAbstractReadable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  public static final String[] KEYWORD_PARAMETER = { 
    SKeyWordDecoder.KW_SPHERE, SKeyWordDecoder.KW_PLANE, SKeyWordDecoder.KW_DISK, SKeyWordDecoder.KW_TRIANGLE, 
    SKeyWordDecoder.KW_BTRIANGLE, SKeyWordDecoder.KW_TUBE, SKeyWordDecoder.KW_CYLINDER, SKeyWordDecoder.KW_CONE,
    SKeyWordDecoder.KW_CUBE, SKeyWordDecoder.KW_SPHERICAL_CAP, SKeyWordDecoder.KW_LENS, SKeyWordDecoder.KW_GEOMETRY 
  };
  
  private static final SGeometry DEFAULT_GEOMETRY = null; //géométrie par défaut étant non chargé
  
  //-------------
  // VARIABLES //
  //-------------
  
  SGeometry geometry;           //géométrie lue
  boolean is_read;              //si la géométrie a été lue (n'est plus 'null')
  
  SPrimitive primitive_parent;  //parent primitive de la géométrie
  
  /**
   * Constructeur d'un constructeur de géométrie à partir d'information lue dans un fichier de format txt.
   * @param sbr - Le BufferedReader cherchant l'information dans le fichier txt.
   * @param code - Le code de lecture correspondant à la géométrie à lire dans le fichier txt.
   * @param parent - La primitive parent à la géométrie qui sera construite en lecture.
   * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
   * @throws SReadingException Si une erreur de lecture a été trouvée.
   */
  public SGeometryReader(SBufferedReader sbr, int code, SPrimitive parent)throws IOException, SReadingException
  {
    geometry = DEFAULT_GEOMETRY;
    is_read = false;
    primitive_parent = parent;
    
    read(sbr, code, "");
  }

  /**
   * Méthode pour obtenir la géométrie lue par le lecteur.
   * @return La géométrie lue par le lecteur.
   * @throw SRuntimeException S'il n'y a pas eu de géométrie lue par le lecteur.
   */
  public SGeometry getGeometry()throws SRuntimeException
  {
    if(is_read)
      return geometry;
    else
      throw new SRuntimeException("Erreur SGeometryReader 001 : Il n'y a pas eu de géométrie lu par ce lecteur.");
  }
  
  /**
   * Méthode pour qui détermine si le lecteur de géométrie a lu une géométrie.
   * @return <b>true</b> si une géométrie a été lue et <b>false</b> sinon.
   */
  public boolean isRead()
  {
    return is_read;
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
   * @see sim.util.SAbstractReadableWriteable#read(sim.util.SBufferedReader, int, java.lang.String)
   */
  @Override
  protected boolean read(SBufferedReader sbr, int code, String remaining_line) throws SReadingException, IOException
  {
    if(is_read)
      throw new SReadingException("Erreur SGeometryReader 002 : Une géométrie a déjà été lue par ce lecteur.");
    else
    { 
      
      try{
        
        //Lecture des différentes géométrie reconnues par le lecteur
        switch(code)
        {
          case SKeyWordDecoder.CODE_SPHERE : geometry = new SSphereGeometry(sbr, primitive_parent); break; 
                                             
          case SKeyWordDecoder.CODE_PLANE : geometry = new SPlaneGeometry(sbr, primitive_parent); break; 
                                             
          case SKeyWordDecoder.CODE_DISK : geometry = new SDiskGeometry(sbr, primitive_parent); break;
               
          case SKeyWordDecoder.CODE_TRIANGLE : geometry = new STriangleGeometry(sbr, primitive_parent); break;
            
          case SKeyWordDecoder.CODE_BTRIANGLE : geometry = new SBTriangleGeometry(sbr, primitive_parent); break; 
          
          case SKeyWordDecoder.CODE_TUBE : geometry = new STubeGeometry(sbr, primitive_parent); break;
          
          case SKeyWordDecoder.CODE_CYLINDER : geometry = new SCylinderGeometry(sbr, primitive_parent); break; 
              
          case SKeyWordDecoder.CODE_CONE : geometry = new SConeGeometry(sbr, primitive_parent); break; 
          
          case SKeyWordDecoder.CODE_CUBE : geometry = new SCubeGeometry(sbr, primitive_parent); break; 
          
          case SKeyWordDecoder.CODE_SPHERICAL_CAP : geometry = new SSphericalCapGeometry(sbr, primitive_parent); break;
          
          case SKeyWordDecoder.CODE_LENS : geometry = new SLens(sbr, primitive_parent); break;
          
          //La géométrie transformable
          case SKeyWordDecoder.CODE_GEOMETRY : geometry = new STransformableGeometry(sbr, primitive_parent); break;
          
          default : return false;
        }
        
        is_read = true;
        return true;
        
      }catch(SConstructorException e){
        // Une erreur est survenue lors de la construction d'une géométrie.
        throw new SReadingException("Erreur SGeometryReader 003 : Une géométrie n'a pas été construite avec succès." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
      }
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
    throw new SNoImplementationException("Erreur SGeometryReader 004 : Cette méthode ne retourne pas de mot clé, car cet objet ne peut pas être construit par l'appel d'un mot clé.");
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    return KEYWORD_PARAMETER;
  }
  
}//fin de la classe SGeometryReader
