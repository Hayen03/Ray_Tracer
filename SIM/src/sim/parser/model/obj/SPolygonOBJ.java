/**
 * 
 */
package sim.parser.model.obj;

import java.util.ArrayList;

import sim.exception.SRuntimeException;

/**
 * Classe qui représente un polygone selon le format OBJ.
 * @author Simon Vézina
 * @since 2015-03-25
 * @version 2015-06-15
 */
public class SPolygonOBJ {

  private int index_error;  //le nombre d'erreur d'indexage entre les indexe demandé par la Face et la disponibilité des listes.
  
  private final String material_name;                    //nom du materiel
  
  private final ArrayList<SVertex> vertex_list;          //liste des Vertex du polygone
  private final ArrayList<SVertex> vertex_texture_list;  //liste des Vertex texture du polygone
  private final ArrayList<SVertex> vertex_normal_list;   //liste des Vertex normal du polygone
  
  /**
   * Constructeur d'un polygone selon le format OBJ à partir de la définition d'une face et de l'ensemble des vertexs disponibles.
   * 
   * @param face - La face du polygone comprenant les indexes des différents Vertex du polygone.
   * @param vertex_list - La liste des Vertex disponibles (ne seront pas tous utilisés).
   * @param texture_list - La liste des Vertex texture disponibles (ne seront pas tous utilisés).
   * @param normal_list - La liste des Vertex normales disponibles (ne seront pas tous utilisés). 
   */
  public SPolygonOBJ(SFace face, ArrayList<SVertex> all_vertex_list,  ArrayList<SVertex> all_texture_list,  ArrayList<SVertex> all_normal_list)
  {
    material_name = face.getMaterialName();
    
    index_error = 0;
    
    vertex_list = buildVertexList(face, all_vertex_list); 
    vertex_texture_list = buildVertexTextureList(face, all_texture_list);
    vertex_normal_list = buildVertexNormalList(face, all_normal_list);
  }

  /**
   * Constructeur d'un polygone selon le format OBJ particulier permettant de copier des informations <b>sans</b> les vertexs de normale ni texture.
   * Cette version peut être pertinante si la définition de ces derniers était erronée dans la définition officiel du SPolygonOBJ.
   * 
   * @param material_name - Le nom du matériel associé au polygone.
   * @param vertex_list - La liste des vertex définissant le polygone.
   */
  private SPolygonOBJ(String material_name, ArrayList<SVertex> vertex_list)
  {
    this.material_name = material_name;
    
    index_error = 0;
    
    this.vertex_list = vertex_list;
    this.vertex_texture_list = new ArrayList<SVertex>();   //liste vide
    
    this.vertex_normal_list = new ArrayList<SVertex>();    //liste vide
  }
  
  /**
   * Méthode pour obtenir une copie d'un polygone en ayant retiré les référence aux normales et aux coordonnées uv.
   * Ce nouveau polygone sera automatiquement définit avec une normale géométrique.
   * 
   * @return Un nouveau polygone moins définit (sans normale ni coordonnée de texture en chaque point).
   */
  public SPolygonOBJ generateBasicSPolygonOBJ()
  {
    return new SPolygonOBJ(material_name, vertex_list);
  }
  
  /**
   * Méthode pour obtenir le nom du matériel associé au polygone.
   * @return Le nom du matériel.
   */
  public String getMaterialName()
  {
    return material_name;
  }
  
  /**
   * Méthode pour obtenir le nombre de Vertex dans le polygone.
   * @return Le nombre de Vertex dans le polygone.
   */
  public int vertexNumber()
  {
    return vertex_list.size();
  }
  
  /**
   * Méthode pour déterminer s'il y a des Vertex dans le polygone.
   * @return <b>true</b> s'il y a des Vertex et <b>false</b> sinon.
   */
  public boolean asVertex()
  {
    if(vertex_list.isEmpty())
      return false;
    else
      return true;
  }
  
  /**
   * Méthode pour déterminer s'il y a des Vertex textures dans le polygone.
   * @return <b>true</b> s'il y a des Vertex textures et <b>false</b> sinon.
   */
  public boolean asVertexTexture()
  {
    if(vertex_texture_list.isEmpty())
      return false;
    else
      return true;
  }
  
  /**
   * Méthode pour déterminer s'il y a des Vertex noramles dans le polygone.
   * @return <b>true</b> s'il y a des Vertex noramles et <b>false</b> sinon.
   */
  public boolean asVertexNormal()
  {
    if(vertex_normal_list.isEmpty())
      return false;
    else
      return true;
  }
  
  /**
   * Méthode pour déterminer s'il y a eu des erreur d'indexage durant l'affectation des Vertex au polygone à partir de la table d'indexage.
   * Si tel est le cas, il sera préférable de ne pas générer de polygone avec ces informations.
   * @return <b>true</b> s'il y a eu des erreur à l'indexage et <b>false</b> sinon.
   */
  public boolean asError()
  {
    if(index_error != 0)
      return true;
    else
      return false;
  }
  
  /**
   * Méthode pour obtenir le Vertex d'indice i dans le polygone.
   * @param i - L'indice du Vertex dans le polygone.
   * @return Le Vertex d'indice i.
   * @throws SRuntimeException Si le polygone ne possède pas de Vertex.
   */
  public SVertex getVertex(int i)throws SRuntimeException
  {
    if(asVertex())
      return vertex_list.get(i);
    else
      throw new SRuntimeException("Erreur SPolygonOBJ 001 : Le polygone ne possède pas de Vertex comme paramètre. Une erreur lors de la lecture a du être rencontrée.");
  }
  
  /**
   * Méthode pour obtenir le Vertex texture d'indice i dans le polygone.
   * @param i - L'indice du Vertex dans le polygone. 
   * @return Le Vertex texture d'indice i.
   * @throws SRuntimeException Si le polygone ne possède pas de Vertex texture.
   */
  public SVertex getVertexTexture(int i)throws SRuntimeException
  {
    if(asVertexTexture())
      return vertex_texture_list.get(i);
    else
      throw new SRuntimeException("Erreur SPolygonOBJ 002 : Le polygone ne possède pas de Vertex texture comme paramètre.");
  }
  
  /**
   * Méthode pour obtenir le Vertex normale d'indice i dans le polygone.
   * @param i - L'indice du Vertex dans le polygone. 
   * @return Le Vertex normale d'indice i.
   * @throws SRuntimeException Si le polygone ne possède pas de Vertex normale.
   */
  public SVertex getVertexNormal(int i)throws SRuntimeException
  {
    if(asVertexNormal())
      return vertex_normal_list.get(i);
    else
      throw new SRuntimeException("Erreur SPolygonOBJ 003 : Le polygone ne possède pas de Vertex normale comme paramètre.");
  }
  
  /**
   * Méthode pour construire la liste des Vertex du polygone à partir d'une liste d'index et l'ensemble des Vertex disponibles triés par numéro d'index.
   * @param face - La Face du polygone.
   * @param vertex_list - La liste des Vertex disponibles.
   * @return La liste des Vertex du polygone.
   */
  private ArrayList<SVertex> buildVertexList(SFace face, ArrayList<SVertex> vertex_list)
  {
    ArrayList<SVertex> list = new ArrayList<SVertex>();
    
    if(face.asVertex())
    {
      int[] index_tab = face.getVertexIndex();
      int size_tab = face.getVertexNumber();
      
      //Itérer sur l'ensemble des index du tableau et ajouter le Vertex correspondant à la liste des Vertex
      for(int i=0; i<size_tab; i++)
      {
        int index = index_tab[i];
        
        if(index < vertex_list.size())		//si l'index demandé est disponible
          list.add(vertex_list.get(index));
        else								//si l'index demandé n'est pas disponible
        {
          index_error++;	//identification d'une erreur		
          list.clear();		//vider la liste et rendre cette liste inacessible
          return list;
        }
      }   
    }
    return list;
  }
  
  /**
   * Méthode pour construire la liste des Vertex textures du polygone à partir d'une liste d'index et l'ensemble des Vertex textures disponibles triés par numéro d'index.
   * @param face - La Face du polygone.
   * @param vertex_list - La liste des Vertex textures disponibles.
   * @return La liste des Vertex textures du polygone.
   */
  private ArrayList<SVertex> buildVertexTextureList(SFace face, ArrayList<SVertex> vertex_list)
  {
    ArrayList<SVertex> list = new ArrayList<SVertex>();
    
    if(face.asVertexTexture())
    {
      int[] index_tab = face.getVertexTextureIndex();
      int size_tab = face.getVertexNumber();
      
      //Itérer sur l'ensemble des index du tableau et ajouter le Vertex correspondant à la liste des Vertex
      for(int i=0; i<size_tab; i++)
      {
        int index = index_tab[i];
        
        if(index < vertex_list.size())			//si l'index demandé est disponible
          list.add(vertex_list.get(index));
        else									//si l'index demandé n'est pas disponible
        {
          index_error++;	//identification d'une erreur		
          list.clear();		//vider la liste et rendre cette liste inacessible
          return list;
        }
      }   
    }
    return list;
  }
  
  /**
   * Méthode pour construire la liste des Vertex normales du polygone à partir d'une liste d'index et l'ensemble des Vertex normales disponibles triés par numéro d'index.
   * @param face - La Face du polygone.
   * @param vertex_list - La liste des Vertex normales disponibles.
   * @return La liste des Vertex normales du polygone.
   */
  private ArrayList<SVertex> buildVertexNormalList(SFace face, ArrayList<SVertex> vertex_list)
  {
    ArrayList<SVertex> list = new ArrayList<SVertex>();
    
    if(face.asVertexNormal())
    {
      int[] index_tab = face.getVertexNormalIndex();
      int size_tab = face.getVertexNumber();
      
      //Itérer sur l'ensemble des index du tableau et ajouter le Vertex correspondant à la liste des Vertex
      for(int i=0; i<size_tab; i++)
      {
        int index = index_tab[i];
        
        if(index < vertex_list.size())			//si l'index demandé est disponible
          list.add(vertex_list.get(index));		
        else									//si l'index demandé n'est pas disponible
        {
          index_error++;	//identification d'une erreur		
          list.clear();		//vider la liste et rendre cette liste inacessible
          return list;
        }
      }   
    }
    return list;
  }
  
}//fin classe SPolygonOBJ
