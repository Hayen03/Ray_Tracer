/**
 * 
 */
package sim.geometry.space;

import java.util.List;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;

/**
 * Classe qui représente un évaluateur de dimension de voxel. À partir d'une liste de boîtes englobantes contenant une géométrie, cette
 * classe propose plusieurs algorithmes évaluant une dimension aux voxels afin de répartir dans un grillage idéal l'ensemble des boîtes englobante.
 * Une dimension optimale permettra à un espace de voxel de faire les tests d'intersections le plus rapidement possible.
 * 
 * @author Simon Vézina
 * @since 2015-08-07
 * @version 2015-08-08
 */
public class SVoxelDimensionEvaluator {

  // Les codes de référence des différents algorithmes disponibles
  public static final int ONE_FOR_ONE_ALGORITHM = 0;
  public static final int BIGGEST_AVERAGE_LENGHT_ALGORITHM = 1;
  public static final int MID_AVERAGE_LENGHT_ALGORITHM = 2;
  public static final int SMALLEST_AVERAGE_LENGHT_ALGORITHM = 3;
  
  private final double dimension;     //la dimension suggérée pour les voxels
  
  /**
   * Constructeur d'un évaluateur de dimension de voxel pour espace de voxels.
   * @param list - La liste des boîtes englobantes.
   * @param algorithm_code - Le code de référence de l'algorithme utilisé pour évaluer la dimension.
   * @throws SConstructorException Si le code de référence de l'algorithme n'est pas reconnu.
   * @throws SConstructorException Si un algorithme particulier est lancé avec une liste de boîte vide. 
   */
  public SVoxelDimensionEvaluator(List<SBoundingBox> list, int algorithm_code) throws SConstructorException
  {
    try{
      
      switch(algorithm_code)
      {
        case ONE_FOR_ONE_ALGORITHM :              dimension = algorithOneForOne(); break;
        
        case BIGGEST_AVERAGE_LENGHT_ALGORITHM :   dimension = algorithBiggestAverageLenght(list); break;
        
        case MID_AVERAGE_LENGHT_ALGORITHM :       dimension = algorithMidAverageLenght(list); break;
        
        case SMALLEST_AVERAGE_LENGHT_ALGORITHM :  dimension = algorithSmallestAverageLenght(list); break;
        
        default : throw new SConstructorException("Erreur SVoxelDimensionEvaluator 001 : Le code de l'algorithme '" + algorithm_code + "' n'est pas reconnu.");
      }
    }catch(SRuntimeException e){
      //Si un algorithme "crash" en raison d'une liste de boîte vide
      throw new SConstructorException("Erreur SVoxelDimensionEvaluator 002 : L'algorithme de code '" + algorithm_code + "' a été lancé avec une liste de boîte englobante vide.");
    }
  }

  /**
   * Méthode pour obtenir la dimension des voxels suggérée par l'algorithme choisi.
   * @return La dimension du voxel.
   */
  public double getDimension()
  {
    return dimension;
  }
  
  /**
   * Méthode pour évaluer la dimension du voxel comme étant <b>unitaire</b> (égale à 1).
   * @return La dimension unitaire du voxel. 
   */
  private double algorithOneForOne()
  {
    return 1.0;   //1 unité voxel = 1 unité monde
  }
  
  /**
   * Méthode pour évaluer la dimension du voxel comme étant de largeur égale <b>à la largeur moyenne de la plus grande boîte</b>.
   * @param list - La liste des boîtes englobantes.
   * @return La dimension.
   * @throws SRuntimeException S'il n'y a pas de boîte dans la liste.
   */
  private double algorithBiggestAverageLenght(List<SBoundingBox> list) throws SRuntimeException
  {
    if(list.isEmpty())
      throw new SRuntimeException("Erreur SVoxelDimensionEvaluator 003 : La list étant vide, il est impossible d'appliquer cet algorithme.");
    
    SBoundingBox biggest = list.get(0);
    
    for(SBoundingBox b : list)
      if(b.getAverageLenght() > biggest.getAverageLenght())
        biggest = b;
     
    return biggest.getAverageLenght();  
  }
  
  /**
   * Méthode pour évaluer la dimension du voxel comme étant de largeur égale <b>racine cubique du volume de la boîte ayant le volume moyen</b>.
   * @param list - La liste des boîtes englobantes.
   * @return La dimension.
   * @throws SRuntimeException S'il n'y a pas de boîte dans la liste.
   */
  private double algorithMidAverageLenght(List<SBoundingBox> list) throws SRuntimeException
  {
    if(list.isEmpty())
      throw new SRuntimeException("Erreur SVoxelDimensionEvaluator 004 : La list étant vide, il est impossible d'appliquer cet algorithme.");
   
    SBoundingBox smallest = list.get(0);
    SBoundingBox biggest = list.get(0);
    
    for(SBoundingBox b : list)
      if(b.getAverageLenght() < smallest.getAverageLenght())
        smallest = b;
      else
        if(b.getAverageLenght() > biggest.getAverageLenght())
          biggest = b;
    
    return (smallest.getAverageLenght() + biggest.getAverageLenght())/2;  
  }
  
  /**
   * Méthode pour évaluer la dimension du voxel comme étant de largeur égale <b>à la largeur moyenne de la plus petite boîte</b>.
   * @param list - La liste des boîtes englobantes.
   * @return La dimension.
   * @throws SRuntimeException S'il n'y a pas de boîte dans la liste.
   */
  private double algorithSmallestAverageLenght(List<SBoundingBox> list) throws SRuntimeException
  {
    if(list.isEmpty())
      throw new SRuntimeException("Erreur SVoxelDimensionEvaluator 005 : La list étant vide, il est impossible d'appliquer cet algorithme.");
    
    SBoundingBox smallest = list.get(0);
    
    for(SBoundingBox b : list)
      if(b.getAverageLenght() < smallest.getAverageLenght())
        smallest = b;
     
    return smallest.getAverageLenght();  
  }
    
}//fin de la classe SVoxelDimensionEvaluator
