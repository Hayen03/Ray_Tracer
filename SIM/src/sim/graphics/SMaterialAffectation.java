/**
 * 
 */
package sim.graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sim.graphics.material.SDefaultMaterial;
import sim.graphics.material.SMaterial;
import sim.util.SLog;

/**
 * Classe qui permet d'affecter le bon matériel à la bonne primitive en fonction du nom du matériel demandé par la primitive.
 *
 * @author Simon Vézina
 * @since 2015-07-31
 * @version 2015-07-31
 */
public class SMaterialAffectation {

  private final List<SPrimitive> primitive_list;  //liste des primitives que l'on doit affecter un matériel
  private final List<SMaterial> material_list;    //liste des matériaux disponibles pour l'affectation
  
  /**
   * Constructeur de l'assignateur de matériel aux primitives.
   * @param primitive_list - La liste des primitives qui doivent subir une affectation d'un matériel.
   * @param material_list - La liste des matériaux disponibles pour l'affectation.
   */
  public SMaterialAffectation(List<SPrimitive> primitive_list, List<SMaterial> material_list)
  {
    this.primitive_list = primitive_list;
    this.material_list = material_list;
  }
  
  /**
   * Méthode pour réaliser l'affectation des matériaux aux primitives. Si un matériau est demandé (par son nom étant la clé d'identification)
   * et qu'il n'est pas présent dans la liste, il sera créé avec des paramètres par défaut. Un message sera affiché pour signaler cette situation.
   */
  public void affectation()
  {
    //Remplir une carte de matériel avec le nom du matériel en clé d'accès.
    //Un message sera envoyé si deux matériaux ont le même nom.
    Map<String, SMaterial> material_map = new HashMap<String, SMaterial>();
    
    for(SMaterial m : material_list)
      if(material_map.containsKey(m.getName()))
        SLog.logWriteLine("Message SMaterialAffectation : Le nom de matériel '" + m.getName() + "' se retrouve deux fois au plus dans la scène. Seule la première définition sera affectée aux primitives.");
      else
        material_map.put(m.getName(), m);
      
    
    //Affecter les matériels aux primitives qui n'ont pas de matériel (ceux provenant d'un modèle devrait avoir un matériel préalablement affecté)
    //Affecter un matériel à la primitive si le nom du matériel demandé par la primitive se retrouve dans la map.
    //Création d'un matériel par défaut avec le nom demandé par la primitive si une primitive n'a pas de matériel (pour qu'elle soit visible puisqu'elle a une géométrie).
    for(SPrimitive p : primitive_list)
      if(material_map.containsKey(p.getMaterialName()))        //vérifier si le nom du matériel désiré est préalablement disponible
        p.setMaterial(material_map.get(p.getMaterialName()));
      else                                                     
      {
        //Construire un nouveau matériel, car celui recherché n'est pas disponible !
        SLog.logWriteLine("Message SMaterialAffectation : Le matériel '" + p.getMaterialName() + "' demandé par une primitive ne se retrouve pas dans la collection des matériaux. Un nouveau matériel sera créé par défaut.");
          
        //Création d'un nouveau matériel de couleur blanche avec le nom désiré par la primitive
        SMaterial new_material = new SDefaultMaterial(p.getMaterialName()); 
          
        //Ajout à la liste des matériels (nécessaire pour la sauvegarde de la scène)
        material_list.add(new_material);
          
        //Ajout de ce matériel à la carte temporaire (au cas où il y aurait d'autres primitives qui voudraient utiliser ce matériel).
        material_map.put(new_material.getName(), new_material);
          
        p.setMaterial(new_material);  //Affectation du nouveau matériel à la primitive
      }
  }
  
}//fin de la classe SMaterialAffectation
