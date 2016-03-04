/**
 * 
 */
package sim.loader.model;

import sim.exception.SConstructorException;
import sim.geometry.SCylinderGeometry;
import sim.graphics.SColor;
import sim.graphics.SModel;
import sim.graphics.SPrimitive;
import sim.graphics.material.SBlinnMaterial;
import sim.graphics.material.SMaterial;
import sim.loader.SLoaderException;
import sim.loader.SStringLoader;
import sim.math.SVector3d;
import sim.parser.model.agp.SModelAGPParser;
import sim.parser.model.agp.SModelAGPParserException;
import sim.parser.model.agp.SPoint;
import sim.parser.model.agp.SPointSequence;
import sim.util.SLog;
import sim.util.SStringUtil;

/**
 * La classe <b>SModelAGPLoader</b> permet de charge un mod�le de format .agp �tant le format de mod�le du <i>projet Anaglyphe</i> r�alis� par Anik Souli�re.
 * 
 * @author Simon V�zina
 * @since 2015-08-01
 * @version 2015-12-20
 */
public class SModelAGPLoader implements SStringLoader {

  public static final String FILE_EXTENSION = "agp";  //extension des fichiers lue par ce loader
  
  private static final SColor DEFAULT_COLOR = new SColor(0.8, 0.8, 0.2);
  
  private static final double DEFAULT_RAY = 0.1;
  
  /**
   * La variable <b>error</b> correspond au nombre d'erreur lors de lecture du mod�le 3d.
   */
  private int error;
  
  /**
   *  Constructeur d'un chargeur de mod�le 3d de format .agp . 
   */
  public SModelAGPLoader()
  {
    error = 0;
  }

  /* (non-Javadoc)
   * @see sim.loader.SStringLoader#load(java.lang.String)
   */
  @Override
  public Object load(String string) throws SLoaderException
  {
    try{
      
      SModelAGPParser parser = new SModelAGPParser(string);
      
      SModel model = new SModel(string);
      SMaterial material = new SBlinnMaterial(DEFAULT_COLOR);
      
      //Construire des cylindres � partir de la liste de point des s�quences.
      //Puisque les points sont dans l'ordre, il y aura une chaine de cylindre.
      for(SPointSequence s : parser.getListSequence())
      {
        SPoint p1 = s.getPoint(0);  //le 1ier point de la s�quence
        SPoint p2;
        
        //Faire un cylindre avec comme point p2 le suivant, puis p1 devient p2 pour le prochain � faire 
        for(int i=1; i<s.size(); i++)
        {
          p2 = s.getPoint(i);
          
          
          try{
            //Construire le cylindre et la primitive
            SCylinderGeometry cylinder = new SCylinderGeometry(new SVector3d(p1.getX(), p1.getY(), p1.getZ()), new SVector3d(p2.getX(), p2.getY(), p2.getZ()), DEFAULT_RAY);
          
            //Ajouter la primitive au mod�le
            model.addPrimitive(new SPrimitive(cylinder, material));
          }catch(SConstructorException e){
            // Compter le nombre d'erreur dans le mod�le (des points qui se sont r�p�t�s dans la s�quence) 
            error++;
          }
               
          p1 = p2;
        }
      }
      
      // Afficher un message d�nombrant le nombre d'erreurs
      if(error > 0)
        SLog.logWriteLine("Message SModelAGPLoader : Le mod�le " + string + " contient " + error + " erreurs dans la d�finition du mod�le."); 
      
      return model;
      
    }catch(SModelAGPParserException e){
      throw new SLoaderException("Erreur SModelAGPLoader 003 : Une erreur lors de la lecture est survenue ce qui emp�che le chargement du mod�le 3d. " + SStringUtil.END_LINE_CARACTER + e.getMessage());
    }
  }

}//fin de la classe SModelAGPLoader
