package sim.util;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * La classe <b>SKeyWordDecoder</b> permet l'analyse d'un <b>mot cle</b> sous forme d'un string afin de le convertir en un <b>code</b> en nombre entier unique. 
 * Ce code pourra être analysé dans l'usage d'une autre classe afin d'exécuter une tâche bien précise (comme dans la lecture d'un fichier d'initialisation d'un objet).
 * 
 * @author Simon Vézina
 * @since 2014-12-20
 * @version 2016-01-11
 */
public class SKeyWordDecoder {

	//Liste des mots clé (key word) et des codes de lecture associés.
	public static final int CODE_ERROR = -1;
	public static final int CODE_NULL = 0;
	
	public static final String KW_END = "end";
	public static final int CODE_END = 1;
	
	public static final String KW_POSITION = "position";
	public static final int CODE_POSITION = 2;
	
	public static final String KW_LOOK_AT = "look_at";
	public static final int CODE_LOOK_AT = 3;
	
	public static final String KW_UP = "up";
	public static final String KW_HAUT = "haut";
	public static final int CODE_UP = 4;
	
	public static final String KW_WIDTH = "width";
	public static final String KW_LARGEUR = "largeur";
	public static final int CODE_WIDTH = 5;
	
	public static final String KW_HEIGHT = "height";
	public static final String KW_HAUTEUR = "hauteur";
	public static final int CODE_HEIGHT = 6;
	
	public static final String KW_ANGLE = "angle";
	public static final int CODE_ANGLE = 7;
	
	public static final String KW_NEAR_CLIPPING_PLANE = "near_clipping_plane";
	public static final int CODE_NEAR_CLIPPING_PLANE = 8;
	
	public static final String KW_FAR_CLIPPING_PLANE = "far_clipping_plane";
	public static final int CODE_FAR_CLIPPING_PLANE = 9;
	
	public static final String KW_CAMERA = "camera";
	public static final int CODE_CAMERA = 10;
	
	public static final String KW_VIEWPORT = "viewport";
	public static final int CODE_VIEWPORT = 11;
	
	public static final String KW_VIEW_FRUSTUM = "view_frustum";
	public static final String KW_VIEWFRUSTUM = "viewfrustum";
	public static final int CODE_VIEW_FRUSTUM = 12;
	
	public static final String KW_IMAGE_FILE_NAME = "image_file_name";
	public static final String KW_NOM_FICHIER_IMAGE = "nom_fichier_image";
	public static final int CODE_IMAGE_FILE_NAME = 13;
	
	public static final String KW_RAY = "ray";
	public static final String KW_RAYON = "rayon";
	public static final int CODE_RAY = 14;
	
	public static final String KW_SPHERE = "sphere";
	public static final int CODE_SPHERE = 15;
		
	public static final String KW_NAME = "name";
	public static final String KW_NOM = "nom";
	public static final int CODE_NAME = 16;
	
	public static final String KW_PRIMITIVE = "primitive";
	public static final int CODE_PRIMITIVE = 17;
	
	public static final String KW_MATERIAL_NAME = "material_name";
	public static final String KW_NOM_MATERIEL = "nom_materiel";
	public static final int CODE_MATERIAL_NAME = 18;
	
	public static final String KW_DEFAULT_MATERIAL = "default_material";
	public static final int CODE_DEFAULT_MATERIAL = 19;
	
	public static final String KW_COLOR = "color";
	public static final String KW_COULEUR = "couleur";
	public static final int CODE_COLOR = 20;
	
	public static final String KW_AMBIENT_LIGHT = "ambient_light";
	public static final String KW_LUMIERE_AMBIANTE = "lumiere_ambiante";
	public static final int CODE_AMBIENT_LIGHT = 21;
	
	public static final String KW_BLINN_MATERIAL = "blinn_material";
	public static final String KW_COLOR_MATERIAL = "color_material";
	public static final String KW_MATERIAL = "material";
	public static final String KW_MATERIEL = "materiel";
	public static final int CODE_BLINN_MATERIAL = 22;
	
	public static final String KW_TEXTURE_MATERIAL = "texture_material";  
	public static final String KW_TEXTURE_MATERIEL = "texture_materiel";  
  public static final int CODE_TEXTURE_MATERIAL = 23;                                  
  
	public static final String KW_CONSTANT_ATTENUATOR = "constant_attenuator";
	public static final int CODE_CONSTANT_ATTENUATOR = 24;

	public static final String KW_LINEAR_ATTENUATOR = "linear_attenuator";
	public static final int CODE_LINEAR_ATTENUATOR = 25;
	
	public static final String KW_QUADRATIC_ATTENUATOR = "quadratic_attenuator";
	public static final int CODE_QUADRATIC_ATTENUATOR = 26;
	
	public static final String KW_DIRECTIONAL_LIGHT = "directional_light";
	public static final String KW_LUMIERE_DIRECTIONNELLE = "lumiere_directionnelle";
	public static final int CODE_DIRECTIONAL_LIGHT = 27;
	
	public static final String KW_ORIENTATION = "orientation";
	public static final int CODE_ORIENTATION = 28;
	
	public static final String KW_NORMAL = "normal";
	public static final String KW_NORMALE = "normale";
	public static final int CODE_NORMAL = 29;
	
	public static final String KW_PLANE = "plane";
	public static final String KW_PLAN = "plan";
	public static final int CODE_PLANE = 30;
	
	public static final String KW_POINT_LIGHT = "point_light";
	public static final String KW_LUMIERE_PONCTUELLE = "lumiere_ponctuelle";
	public static final int CODE_POINT_LIGHT = 31;
	
	public static final String KW_KA = "ka";
	public static final int CODE_KA = 32;
	
	public static final String KW_KD = "kd";
	public static final int CODE_KD = 33;
	
	public static final String KW_KS = "ks";
	public static final int CODE_KS = 34;
	
	public static final String KW_KR = "kr";
	public static final int CODE_KR = 35;
	
	public static final String KW_KT = "kt";
	public static final int CODE_KT = 36;
	
	public static final String KW_PLASTICITY = "plasticity";
	public static final String KW_P = "p";
	public static final int CODE_PLASTICITY = 37;
	
	public static final String KW_REFRACTIVE_INDEX = "refractive_index";
	public static final String KW_N = "n";
	public static final int CODE_REFRACTIVE_INDEX = 38;
	
	public static final String KW_POINT = "point";
	public static final int CODE_POINT = 39;
	
	public static final String KW_TRIANGLE = "triangle";
	public static final int CODE_TRIANGLE = 40;
	
	public static final String KW_BTRIANGLE = "btriangle";
  public static final int CODE_BTRIANGLE = 41;
	
  public static final String KW_SHININESS = "shininess";
  public static final String KW_BRILLANCE = "brillance";
  public static final int CODE_SHININESS = 42;
  
  public static final String KW_MODEL = "model";
  public static final int CODE_MODEL = 43;
  
  public static final String KW_DISK = "disk";
  public static final String KW_DISQUE = "disque";
  public static final int CODE_DISK = 44;
  
  public static final String KW_TUBE = "tube";
  public static final int CODE_TUBE = 45;
  
  public static final String KW_CYLINDER = "cylinder";
  public static final String KW_CYLINDRE = "cylindre";
  public static final int CODE_CYLINDER = 46;
  
  public static final String KW_TASK = "task";
  public static final int CODE_TASK = 47;
  
  public static final String KW_SAMPLING = "sampling";
  public static final int CODE_SAMPLING = 48;
  
  public static final String KW_RAYTRACER = "raytracer";
  public static final int CODE_RAYTRACER = 49;
  
  public static final String KW_RECURSIVE_LEVEL = "recursive_level";
  public static final int CODE_RECURSIVE_LEVEL = 50;
  
  public static final String KW_SPECULAR_REFLEXION = "specular_reflexion";
  public static final String KW_REFLEXION_SPECULAIRE = "reflexion_speculaire";
  public static final int CODE_SPECULAR_REFLEXION = 51;
  
  public static final String KW_PIXEL_COORDINATE = "pixel_coordinate";
  public static final String KW_COORDONNEE_PIXEL = "coordonnee_pixel";
  public static final int CODE_PIXEL_COORDINATE = 52;
  
  public static final String KW_SCALE = "scale";
  public static final String KW_HOMOTHETIE = "homothetie";
  public static final int CODE_SCALE = 53;
  
  public static final String KW_ROTATION = "rotation";
  public static final int CODE_ROTATION = 54;
  
  public static final String KW_TRANSLATION = "translation";
  public static final int CODE_TRANSLATION = 55;
  
  public static final String KW_GEOMETRY = "geometry";
  public static final String KW_GEOMETRIE = "geometrie";
  public static final int CODE_GEOMETRY = 56;
  
  public static final String KW_FILE = "file";
  public static final String KW_FICHIER = "fichier";
  public static final int CODE_FILE = 57;
  
  public static final String KW_SPACE = "space";
  public static final String KW_ESPACE = "espace";
  public static final int CODE_SPACE = 58;
  
  public static final String KW_CONE = "cone";
  public static final int CODE_CONE = 59;
  
  public static final String KW_READ_SCENE = "read_scene";
  public static final int CODE_READ_SCENE = 60;
  
  public static final String KW_WRITE_SCENE = "write_scene";
  public static final int CODE_WRITE_SCENE = 61;
  
  public static final String KW_LOG_FILE_NAME = "log_file_name";
  public static final int CODE_LOG_FILE_NAME = 62;
  
  public static final String KW_LOG_CONSOLE = "log_console";
  public static final int CODE_LOG_CONSOLE = 63;
  
  public static final String KW_LOG_FILE = "log_file";
  public static final int CODE_LOG_FILE = 64;
  
  public static final String KW_VIEWPORT_IMAGE_COUNT = "viewport_image_count";
  public static final int CODE_VIEWPORT_IMAGE_COUNT = 65;
  
  public static final String KW_APPLICATION = "application";
  public static final int CODE_APPLICATION = 66;
  
  public static final String KW_COLOR_NORMALIZATION = "color_normalization";
  public static final int CODE_COLOR_NORMALIZATION = 67;
  
  public static final String KW_UV = "uv";
  public static final int CODE_UV = 68;
  
  public static final String KW_TEXTURE_KA = "texture_ka";
  public static final int CODE_TEXTURE_KA = 69;
  
  public static final String KW_TEXTURE_KD = "texture_kd";
  public static final int CODE_TEXTURE_KD = 70;
  
  public static final String KW_TEXTURE_KS = "texture_ks";
  public static final int CODE_TEXTURE_KS = 71;
  
  public static final String KW_CUBE = "cube";
  public static final int CODE_CUBE = 72;
  
  public static final String KW_SIZE = "size";
  public static final String KW_TAILLE = "taille";
  public static final int CODE_SIZE = 73;
  
  public static final String KW_UV_FORMAT = "uv_format";
  public static final int CODE_UV_FORMAT = 74;
  
  public static final String KW_SPHERICAL_CAP = "spherical_cap";
  public static final String KW_CALOTTE_SPHERIQUE = "calotte_spherique";
  public static final int CODE_SPHERICAL_CAP = 75;
  
  public static final String KW_CURVATURE = "curvature";
  public static final String KW_COURBURE = "courbure";
  public static final int CODE_CURVATURE = 76;
  
  public static final String KW_LENS = "lens";
  public static final String KW_LENTILLE = "lentille";
  public static final int CODE_LENS = 77;
  
  public static final String KW_SCENE = "scene";
  public static final int CODE_SCENE = 78;
  
  public static final String KW_MODEL_MSR = "model_msr";
  public static final int CODE_MODEL_MSR = 79;
  
  public static final String KW_TEXTURE_COMPARATOR = "texture_comparator";
  public static final String KW_TEXTURE_COMPARATEUR = "texture_comparateur";
  public static final int CODE_TEXTURE_COMPARATOR = 80;
  
  public static final String KW_READ_DATA = "read_data";
  public static final int CODE_READ_DATA = 81;
  
  public static final String KW_WRITE_DATA = "write_data";
  public static final int CODE_WRITE_DATA = 82;
  
  public static final String KW_CONSOLE_COMPARATOR = "console_comparator";
  public static final String KW_COMPARATEUR_CONSOLE = "comparateur_console";
  public static final int CODE_CONSOLE_COMPARATOR = 83;
  
	private static final Map<String, Integer> kw_map = buildDataBase(); 
		
	/**
	 * Méthode pour construire la base de donnée permettant l'analyse du mot clé.
	 * @return La carte de correspondance des mots clé avec code.
	 */
	private static Map<String, Integer> buildDataBase()
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		map.put(KW_END, new Integer(CODE_END));
		
		map.put(KW_POSITION, new Integer(CODE_POSITION));
		
		map.put(KW_LOOK_AT, new Integer(CODE_LOOK_AT));
		
		map.put(KW_UP, new Integer(CODE_UP));
		map.put(KW_HAUT, new Integer(CODE_UP));
		
		map.put(KW_WIDTH, new Integer(CODE_WIDTH));
		map.put(KW_LARGEUR, new Integer(CODE_WIDTH));
		
		map.put(KW_HEIGHT, new Integer(CODE_HEIGHT));
		map.put(KW_HAUTEUR, new Integer(CODE_HEIGHT));
		
		map.put(KW_ANGLE, new Integer(CODE_ANGLE));
		
		map.put(KW_NEAR_CLIPPING_PLANE, new Integer(CODE_NEAR_CLIPPING_PLANE));
		
		map.put(KW_FAR_CLIPPING_PLANE, new Integer(CODE_FAR_CLIPPING_PLANE));
		
		map.put(KW_CAMERA, new Integer(CODE_CAMERA));
		
		map.put(KW_VIEWPORT, new Integer(CODE_VIEWPORT));
		
		map.put(KW_VIEW_FRUSTUM, new Integer(CODE_VIEW_FRUSTUM));
		map.put(KW_VIEWFRUSTUM, new Integer(CODE_VIEW_FRUSTUM));
		
		map.put(KW_IMAGE_FILE_NAME, new Integer(CODE_IMAGE_FILE_NAME));
		map.put(KW_NOM_FICHIER_IMAGE, new Integer(CODE_IMAGE_FILE_NAME));
		
		map.put(KW_RAY, new Integer(CODE_RAY));
		map.put(KW_RAYON, new Integer(CODE_RAY));
		
		map.put(KW_SPHERE, new Integer(CODE_SPHERE));
		
		map.put(KW_NAME, new Integer(CODE_NAME));
		map.put(KW_NOM, new Integer(CODE_NAME));
		
		map.put(KW_PRIMITIVE, new Integer(CODE_PRIMITIVE));
				
		map.put(KW_MATERIAL_NAME, new Integer(CODE_MATERIAL_NAME));
		map.put(KW_NOM_MATERIEL, new Integer(CODE_MATERIAL_NAME));
				
		map.put(KW_DEFAULT_MATERIAL, new Integer(CODE_DEFAULT_MATERIAL));
				
		map.put(KW_COLOR, new Integer(CODE_COLOR));
		map.put(KW_COULEUR, new Integer(CODE_COLOR));
		
		map.put(KW_AMBIENT_LIGHT, new Integer(CODE_AMBIENT_LIGHT));
		map.put(KW_LUMIERE_AMBIANTE, new Integer(CODE_AMBIENT_LIGHT));
		
		map.put(KW_BLINN_MATERIAL, new Integer(CODE_BLINN_MATERIAL));
		map.put(KW_COLOR_MATERIAL, new Integer(CODE_BLINN_MATERIAL));
		map.put(KW_MATERIAL, new Integer(CODE_BLINN_MATERIAL));
		map.put(KW_MATERIEL, new Integer(CODE_BLINN_MATERIAL));
	
		map.put(KW_TEXTURE_MATERIAL, new Integer(CODE_TEXTURE_MATERIAL));
		map.put(KW_TEXTURE_MATERIEL, new Integer(CODE_TEXTURE_MATERIAL));
    
		map.put(KW_CONSTANT_ATTENUATOR, new Integer(CODE_CONSTANT_ATTENUATOR));
		
		map.put(KW_LINEAR_ATTENUATOR, new Integer(CODE_LINEAR_ATTENUATOR));
		
		map.put(KW_QUADRATIC_ATTENUATOR, new Integer(CODE_QUADRATIC_ATTENUATOR));
		
		map.put(KW_DIRECTIONAL_LIGHT, new Integer(CODE_DIRECTIONAL_LIGHT));
		map.put(KW_LUMIERE_DIRECTIONNELLE, new Integer(CODE_DIRECTIONAL_LIGHT));
		
		map.put(KW_ORIENTATION, new Integer(CODE_ORIENTATION));
		
		map.put(KW_NORMAL, new Integer(CODE_NORMAL));
		map.put(KW_NORMALE, new Integer(CODE_NORMAL));
		
		map.put(KW_PLANE, new Integer(CODE_PLANE));
		map.put(KW_PLAN, new Integer(CODE_PLANE));
		
		map.put(KW_POINT_LIGHT, new Integer(CODE_POINT_LIGHT));
		map.put(KW_LUMIERE_PONCTUELLE, new Integer(CODE_POINT_LIGHT));
		
		map.put(KW_KA, new Integer(CODE_KA));
		
		map.put(KW_KD, new Integer(CODE_KD));
		
		map.put(KW_KS, new Integer(CODE_KS));
		
		map.put(KW_KR, new Integer(CODE_KR));
		
		map.put(KW_KT, new Integer(CODE_KT));
		
		map.put(KW_PLASTICITY, new Integer(CODE_PLASTICITY));
		map.put(KW_P, new Integer(CODE_PLASTICITY));
		
		map.put(KW_REFRACTIVE_INDEX, new Integer(CODE_REFRACTIVE_INDEX));
		map.put(KW_N, new Integer(CODE_REFRACTIVE_INDEX));
		
		map.put(KW_POINT, new Integer(CODE_POINT));
		
		map.put(KW_TRIANGLE, new Integer(CODE_TRIANGLE));
		
		map.put(KW_BTRIANGLE, new Integer(CODE_BTRIANGLE));
		
		map.put(KW_SHININESS, new Integer(CODE_SHININESS));
		map.put(KW_BRILLANCE, new Integer(CODE_SHININESS));
		
		map.put(KW_MODEL, new Integer(CODE_MODEL));
		
		map.put(KW_DISK, new Integer(CODE_DISK));
		map.put(KW_DISQUE, new Integer(CODE_DISK));
    	
		map.put(KW_TUBE, new Integer(CODE_TUBE));
		
		map.put(KW_CYLINDER, new Integer(CODE_CYLINDER));
    map.put(KW_CYLINDRE, new Integer(CODE_CYLINDER));
		
    map.put(KW_TASK, new Integer(CODE_TASK));
    
    map.put(KW_SAMPLING, new Integer(CODE_SAMPLING));
    
    map.put(KW_RAYTRACER, new Integer(CODE_RAYTRACER));
    
    map.put(KW_RECURSIVE_LEVEL, new Integer(CODE_RECURSIVE_LEVEL));
    
    map.put(KW_SPECULAR_REFLEXION, new Integer(CODE_SPECULAR_REFLEXION));
    map.put(KW_REFLEXION_SPECULAIRE, new Integer(CODE_SPECULAR_REFLEXION));
    
    map.put(KW_PIXEL_COORDINATE, new Integer(CODE_PIXEL_COORDINATE));
    map.put(KW_COORDONNEE_PIXEL, new Integer(CODE_PIXEL_COORDINATE));
    
    map.put(KW_SCALE, new Integer(CODE_SCALE));
    map.put(KW_HOMOTHETIE, new Integer(CODE_SCALE));
    
    map.put(KW_ROTATION, new Integer(CODE_ROTATION));
    
    map.put(KW_TRANSLATION, new Integer(CODE_TRANSLATION));
    
    map.put(KW_GEOMETRY, new Integer(CODE_GEOMETRY));
    map.put(KW_GEOMETRIE, new Integer(CODE_GEOMETRY));
    
    map.put(KW_FILE, new Integer(CODE_FILE));
    map.put(KW_FICHIER, new Integer(CODE_FILE));
    
    map.put(KW_SPACE, new Integer(CODE_SPACE));
    map.put(KW_ESPACE, new Integer(CODE_SPACE));
    
    map.put(KW_CONE, new Integer(CODE_CONE));
    
    //Pour les "mots clés" du fichier config.cfg
    map.put(KW_READ_SCENE, new Integer(CODE_READ_SCENE));
    
    map.put(KW_WRITE_SCENE, new Integer(CODE_WRITE_SCENE));
    
    map.put(KW_LOG_FILE_NAME, new Integer(CODE_LOG_FILE_NAME));
    
    map.put(KW_LOG_CONSOLE, new Integer(CODE_LOG_CONSOLE));
    
    map.put(KW_LOG_FILE, new Integer(CODE_LOG_FILE));
    
    map.put(KW_VIEWPORT_IMAGE_COUNT, new Integer(CODE_VIEWPORT_IMAGE_COUNT));
    
    map.put(KW_APPLICATION, new Integer(CODE_APPLICATION));    
    //fin pour les mots clés du fichier config.cfg
    
    map.put(KW_COLOR_NORMALIZATION, new Integer(CODE_COLOR_NORMALIZATION));
    
    map.put(KW_UV, new Integer(CODE_UV));
    
    map.put(KW_TEXTURE_KA, new Integer(CODE_TEXTURE_KA));
    
    map.put(KW_TEXTURE_KD, new Integer(CODE_TEXTURE_KD));
    
    map.put(KW_TEXTURE_KS, new Integer(CODE_TEXTURE_KS));
    
    map.put(KW_CUBE, new Integer(CODE_CUBE));
    
    map.put(KW_SIZE, new Integer(CODE_SIZE));
    map.put(KW_TAILLE, new Integer(CODE_SIZE));
    
    map.put(KW_UV_FORMAT, new Integer(CODE_UV_FORMAT));
    
    map.put(KW_SPHERICAL_CAP, new Integer(CODE_SPHERICAL_CAP));
    map.put(KW_CALOTTE_SPHERIQUE, new Integer(CODE_SPHERICAL_CAP));
    
    map.put(KW_CURVATURE, new Integer(CODE_CURVATURE));
    map.put(KW_COURBURE, new Integer(CODE_CURVATURE));
    
    map.put(KW_LENS, new Integer(CODE_LENS));
    map.put(KW_LENTILLE, new Integer(CODE_LENS));
    
    map.put(KW_SCENE, new Integer(CODE_SCENE));
    
    map.put(KW_MODEL_MSR, new Integer(CODE_MODEL_MSR));
    
    map.put(KW_TEXTURE_COMPARATOR, new Integer(CODE_TEXTURE_COMPARATOR));
    map.put(KW_TEXTURE_COMPARATEUR, new Integer(CODE_TEXTURE_COMPARATOR));
    
    map.put(KW_READ_DATA, new Integer(CODE_READ_DATA));
    
    map.put(KW_WRITE_DATA, new Integer(CODE_WRITE_DATA));
    
    map.put(KW_CONSOLE_COMPARATOR, new Integer(CODE_CONSOLE_COMPARATOR));
    map.put(KW_COMPARATEUR_CONSOLE, new Integer(CODE_CONSOLE_COMPARATOR));
        
    return map;
	}
	
	/**
	 * Méthode pour obtenir un code numérique associé à un mot clé.
	 * @param key_word - Le mot clé à convertir en code numérique.
	 * @return le code numérique associé au mot clé.
	 */
	public static int getKeyWordCode(String key_word)
	{
		key_word = key_word.toLowerCase(Locale.ENGLISH);
		
		if(kw_map.containsKey(key_word))
			return ((Integer)kw_map.get(key_word)).intValue();
		else
			return CODE_ERROR;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		test1();
	}

	private static void test1()
	{
		buildDataBase();
		
		//iterating over keys only
		for(String key_word : kw_map.keySet())
		{    
			System.out.print("Key = " + key_word);
			System.out.println("\t\t\t Value = " + kw_map.get(key_word));
		}
	}
	
}//fin classe SKeyWordDecoder
