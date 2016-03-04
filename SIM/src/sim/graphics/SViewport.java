/**
 * 
 */
package sim.graphics;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import sim.exception.SConstructorException;
import sim.exception.SRuntimeException;
import sim.math.SVectorPixel;
import sim.util.SAbstractReadableWriteable;
import sim.util.SBufferedReader;
import sim.util.SInitializationException;
import sim.util.SKeyWordDecoder;
import sim.util.SReadingException;
import sim.util.SStringUtil;

/**
 * La classe <b>SViewport</b> représente une grille de couleur en deux dimension en espace pixels où sera dessiné la scène du point de vue de le pyramide de vue (ViewFrustum).
 * Les coordonnées sont indexés avec [x][y] où x correspond au numéro de ligne et y correspond au numéro de la colonne du tableau.
 * <ul>- La coordonnée [0][0] correspond au coin supérieur gauche de l'image.</ul>
 * <ul>- La coordonnée [width-1][0] correspond au coin supérieur droit de l'image.</ul>
 * <ul>- La coordonnée [0][height-1] correspond au coin inférieur gauche de l'image.</ul>
 * <ul>- La coordonnée [width-1][height-1] correspond au coint inférieur droit de l'image.</ul>
 *  
 * @author Simon Vézina
 * @since 2014-12-27
 * @version 2016-01-08
 */
public class SViewport extends SAbstractReadableWriteable {

  //--------------
  // CONSTANTES //
  //--------------
  
  /**
   * La constante <b>KEYWORD_PARAMETER</b> correspond à un tableau contenant l'ensemble des mots clés 
   * à utiliser reconnus lors de la définition de l'objet par une lecture en fichier.
   */
  private static final String[] KEYWORD_PARAMETER = {
    SKeyWordDecoder.KW_WIDTH, SKeyWordDecoder.KW_HEIGHT, SKeyWordDecoder.KW_IMAGE_FILE_NAME
  };
  
  /**
   * La constante <b>ORIGIN_PIXEL</b> correspond à l'origine en coordonnée pixel du viewport.
   */
  private static final SVectorPixel ORIGIN_PIXEL = new SVectorPixel(0,0);
  
	private final static int DEFAULT_IMAGE_COUNTER = 0;						        //numéro de l'image initiale par défaut
	private final static int MAX_IMAGE_COUNTER = 999;						          //numéro maximal de l'image
		
	public final SColor BACKGROUND_SCOLOR = new SColor(0.0, 0.0, 0.0);		//Couleur du background (en SColor)
	
	private static final int DEFAULT_WIDTH = 500;							            //largeur du viewport par défaut
	private static final int DEFAULT_HEIGHT = 500;						            //hauteur du viewport par défaut
	
	private static final String DEFAULT_IMAGE_FILE_NAME = "image";			  //nom du fichier de l'écriture du viewport par défaut
	private static final String DEFAULT_IMAGE_EXTENSION = "png";			    //extension du fichier d'écriture du viewport par défaut
	
	private static int image_counter = DEFAULT_IMAGE_COUNTER;				      //numéro de l'image qui sera généré lors de l'écriture du viewport dans un fichier image
	
	private String image_file_name;				//nom du fichier lors de l'écriture du viewport en fichier image
		
	private int width;							      //Nombre de pixels en largeur (selon l'axe x, de 0 à width-1)
	private int height;							      //Nombre de pixels en hauteur (selon l'axe y, de 0 à height-1)
	
	private SColor[][] image;             //l'image généré dans le viewport 
	
	private SVectorPixel current_pixel;		//représente le pixel qui est présentement en analyse
		
	/**
	 * Constructeur d'un viewport par défaut.
	 */
	public SViewport()
	{
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	/**
	 * Constructeur avec définition des paramètres de largeur (width) et de hauteur (height).
	 * 
	 * @param width - La largeur du viewport.
	 * @param height - La hauteur du viewport.
	 */
	public SViewport(int width, int height)
	{
		this(width, height, DEFAULT_IMAGE_FILE_NAME);
	}
	
	/**
	 * Constructeur d'un viewport.
	 * 
	 * @param width - La largeur du viewport.
	 * @param height - La hauteur du viewport.
	 * @param file_name - Le nom du fichier image qui sera généré.
	 * @throws SConstructorException Si une erreur est survenue à la construction.
	 */
	public SViewport(int width, int height, String file_name) throws SConstructorException
	{
		this.width = width;
		this.height = height;
		image_file_name = file_name;
				
		try{
		  initialize();
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SViewport 001 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Constructeur du viewport à partir d'information lue dans un fichier de format txt.
	 * 
	 * @param br Le BufferedReader cherchant l'information de le fichier .txt.
	 * @throws IOException Si une erreur de l'objet SBufferedWriter est lancée.
	 * @throws SConstructorException Si une erreur est survenue à la construction.
	 * @see SBufferedReader
	 */
	public SViewport(SBufferedReader br) throws IOException, SConstructorException
	{
		this();		//configuration de base s'il y a des paramètres non défini.
		
		try{
		  read(br);
		}catch(SInitializationException e){
      throw new SConstructorException("Erreur SViewport 002 : Une erreur à l'initialisation est survenue." + SStringUtil.END_LINE_CARACTER + "\t" + e.getMessage(), e);
    }  
	}
	
	/**
	 * Méthode pour obtenir le nombre de pixels en largeur (width) du Viewport.
	 * 
	 * @return Le nombre de pixels en largeur.
	 */
	public int getWidth()
	{ 
	  return width;
	}
	
	/**
	 * Méthode pour obtenir le nombre de pixels en hauteur (height) du Viewport.
	 * 
	 * @return Le nombre de pixels en hauteur.
	 */
	public int getHeight()
	{ 
	  return height;
	}
	
	/**
	 * Méthode pour avoir une copie du contenu du viewport sous forme d'un BufferedImage. 
	 * 
	 * @return une copie du contenu du buffer d'image du viewport.
	 * @see BufferedImage
	 */
	public BufferedImage getBufferedImage()
	{ 
	  BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	  
	  for(int i=0; i<width; i++)
	    for(int j=0; j<height; j++)
	      buffer.setRGB(i, j, image[i][j].normalizeColor().getRGB());
	  
	  return buffer;
	}
	
	/**
	 * Méthode pour définir le numéro de la prochaine image généré par les viewports.
	 * 
	 * @param nb - Le numéro de l'image.
	 * @throws SRuntimeException Si le numéro n'est pas entre 0 et 999.
	 */
	public static void setImageCounter(int nb)throws SRuntimeException
	{
		if(nb < DEFAULT_IMAGE_COUNTER || nb > MAX_IMAGE_COUNTER)
			throw new SRuntimeException("Erreur SViewport 003 : Le numéro d'image '" + nb +"' n'est pas compris entre '" + DEFAULT_IMAGE_COUNTER + "' et '" + MAX_IMAGE_COUNTER +"'.");
		else
			image_counter = nb;
	}
	
	/**
	 * Méthode pour attribuer une couleur à une coordonnée xy d'un pixel du viewport.
	 * Il est important de rappeler que la coordonnée (0,0) correspond au coin supérieur gauche du viewport.
	 * 
	 * @param x - La coordonnée x en largeur (width) du viewport.
	 * @param y - La coordonnée y en hauteur (height) du viewport.
	 * @param color - La couleur à affecter au pixel.
	 * @throws SRuntimeException Si la coordonnée xy du pixel n'est pas admissible.
	 */
	public void setColor(int x, int y, SColor color)throws SRuntimeException
	{
		if(x<0 || x>= width)
			throw new SRuntimeException("Erreur SViewport 004 : La coordonnée x n'est pas comprise entre 0 et width-1.");
		
		if(y<0 || y>= height)
			throw new SRuntimeException("Erreur SViewport 005 : La coordonnée y n'est pas comprise entre 0 et height-1.");
		
		image[x][y] = color;
	}
	
	/**
	 * Méthode pour attribuer une couleur à une coordonnée xy d'un pixel du viewport.
	 * Il est important de rappeler que la coordonnée (0,0) correspond au coin supérieur gauche du viewport.
	 * 
	 * @param p - La coordonnée du pixel du viewport.
	 * @param color - La couleur à affecter au pixel.
	 * @throws SRuntimeException Si la coordonnée xy du pixel n'est pas admissible.
	 */
	public void setColor(SVectorPixel p, SColor color)
	{ 
		setColor(p.getX(), p.getY(), color);
	}  
	
	/**
	 * Méthode pour effacer le viewport en réinitialisant l'ensemble des pixels à la couleur par défaut BACKGROUND_COLOR.
	 * Il y a également réinitilisation de l'itération des pixels du viewport avec la méthode restartPixelIteration().
	 * 
	 * @see restartPixelIteration
	 * @see BACKGROUND_COLOR
	 */
	public synchronized void clear()
	{
		for(int i=0; i<width; i++)
      for(int j=0; j<height; j++)
        image[i][j] = BACKGROUND_SCOLOR;
		
		restartPixelIteration();
	}
	
	/**
	 * Méthode qui réinitialise l'itération des pixels du viewport à l'origine (de coordonnée (0,0)).
	 */
	public void restartPixelIteration()
	{
		current_pixel = null;
	}
	
	/**
	 * Méthode pour déterminer s'il y a encore des pixels à analyser dans le viewport.
	 * 
	 * @return <b>true</b> s'il y a encore des pixels à analyser et <b>false</b> sinon.
	 */
	public boolean hasNextPixel()
	{
		if(current_pixel == null)
			return true;
		
		//Condition d'arrêt : Si x == width-1 et y == height-1
		if(current_pixel.getX() >= (width-1) && current_pixel.getY() >= (height-1))
			return false;
		else
			return true;
	}
	/**
	 * Méthode qui retourne le pixel suivant à être analysé. Cette méthode a été développée afin de pouvoir itérer sur l'ensemble des pixels du viewport.
	 * L'itération va s'effectuer ligne par ligne ce qui correspond à x=0 jusqu'à x=width-1 et l'on recommence en augmentant y afin qu'il passe de y=0 à y=height-1.
	 * 
	 * @return la coordonnée du prochain pixel à analyser et <b> null </b> s'il n'y a plus de pixel à itérer.
	 */
	public synchronized SVectorPixel nextPixel()
	{
		if(current_pixel == null)
			current_pixel = ORIGIN_PIXEL;	//1ier pixel étant l'origine (0,0)
		else
		{	
			int x = current_pixel.getX();
			int y = current_pixel.getY();
		
			x++;	//incrémenter la colonne
		
			//Vérifier s'il faut changer le ligne
			if(x >= width)
			{
				x = 0;	//retourner à la colonne #0
				y++;	//incrémenter la ligne
			
				//Vérifier si les lignes sont complétées
				if(y >= height)
					return null;	//ne pas mettre à jour le current_pixel, car nous avons atteint le maximum
			}
			
			current_pixel = new SVectorPixel(x,y);	//Nouveau pixel courant
		}
		
		return current_pixel;
	}
	
	/**
	 * Méthode qui retourne le ratio width/height sans distorsion. 
	 * Ce calcul est nécessaire pour la formation de la pyramide de vue (ViewFrustum) afin de déterminer une hauteur de pyramide en fonction de la largueur (déterminée par un angle d'ouverture).
	 *  
	 * @return Le ratio width/height.
	 */
	public double normalAspect()
	{ 
	  return (double)width / (double)height; 
	}
	
	/**
	 * Méthode pour écrire un objet SReadableWriteable dans un fichier txt.
	 * 
	 * @param bw - Le buffer pour l'écriture.
	 * @throws IOException Si une erreur de l'objet BufferedWriter est lancée.
	 * @see BufferedWriter
	 */
	public void write(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_VIEWPORT);
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		//Écrire les propriétés de la classe SViewport et ses paramètres hérités
		writeSViewportParameter(bw);
				
		bw.write(SKeyWordDecoder.KW_END);
		bw.write(SStringUtil.END_LINE_CARACTER);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * Méthode pour écrire les paramètres associés à la classe SViewport et ses paramètres hérités.
	 * 
	 * @param bw - Le BufferedWriter écrivant l'information dans un fichier txt.
	 * @throws IOException Si une erreur I/O s'est produite.
	 */
	protected void writeSViewportParameter(BufferedWriter bw)throws IOException
	{	
		bw.write(SKeyWordDecoder.KW_WIDTH);
		bw.write("\t\t");
		bw.write(Integer.toString(width));
		bw.write(" pixels");
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_HEIGHT);
		bw.write("\t\t");
		bw.write(Integer.toString(height));
		bw.write(" pixels");
		bw.write(SStringUtil.END_LINE_CARACTER);
		
		bw.write(SKeyWordDecoder.KW_IMAGE_FILE_NAME);
		bw.write("\t");
		bw.write(image_file_name);
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
	
	/**
	 * Méthode pour écrire le contenu static de la classe SViewport dans un fichier txt.
	 * 
	 * @param bw - Le buffer pour l'écriture.
	 * @throws IOException Si une erreur de l'objet BufferedWriter est lancée.
	 * @see BufferedWriter
	 */
	public static void staticWrite(BufferedWriter bw)throws IOException
	{
		bw.write(SKeyWordDecoder.KW_VIEWPORT_IMAGE_COUNT);
	  bw.write("\t");
	  bw.write(Integer.toString(image_counter));
		bw.write(SStringUtil.END_LINE_CARACTER);
	}
		
	/**
	 * Méthode pour dessiner le viewport dans un fichier image.
	 * 
	 * @throws IOException S'il y a une erreur lors de l'écriture du fichier image. 
	 */
	public void writeImage()throws IOException
	{
		String name = image_file_name + "_";
		
		//Ajouter le numéro de l'image généré par ce viewport
		String number = Integer.toString(image_counter);
		
		//Afficher le bon nombre de '0' au nombre entre 0 et 1000
		if(image_counter < 10)
			name = name.concat("00");
		else
			if(image_counter < 100)
				name = name.concat("0");
		
		name = name.concat(number);
		
		//Générer le fichier image
		File file = new File(name + "." + DEFAULT_IMAGE_EXTENSION);
		BufferedImage buffer = getBufferedImage();
		ImageIO.write(buffer, DEFAULT_IMAGE_EXTENSION, file);
		
		image_counter++;	//augmenter le compteur
		
		//Réinitialiser le compteur si la valeur maximale est atteinte
		if(image_counter > MAX_IMAGE_COUNTER)
			image_counter = DEFAULT_IMAGE_COUNTER;
	}
	
	
	/**
   * Méthode pour faire l'initialisation de l'objet après sa construction.
   * 
   * @throws SInitializationException Si une erreur est survenue lors de l'initialisation.
   */
  private void initialize() throws SInitializationException
	{
		image = new SColor[width][height];
		
		clear();
	}
	
  @Override
	protected boolean read(SBufferedReader sbr, int code, String remaining_line)throws SReadingException
	{
		switch(code)
		{
			case SKeyWordDecoder.CODE_WIDTH :  width = readIntGreaterThanZero(remaining_line, SKeyWordDecoder.KW_WIDTH); return true; 
														
			case SKeyWordDecoder.CODE_HEIGHT : height = readIntGreaterThanZero(remaining_line, SKeyWordDecoder.KW_HEIGHT); return true;
														
			case SKeyWordDecoder.CODE_IMAGE_FILE_NAME : image_file_name = readStringNotEmpty(remaining_line, SKeyWordDecoder.KW_IMAGE_FILE_NAME); return true;
				
			default : return false;
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
    return SKeyWordDecoder.KW_SPHERE;
  }
  
  @Override
  public String[] getReadableParameterName()
  {
    String[] other_parameters = super.getReadableParameterName();
    
    return SStringUtil.merge(other_parameters, KEYWORD_PARAMETER);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    
    if (obj == null)
      return false;
    
    if (getClass() != obj.getClass())
      return false;
    
    SViewport other = (SViewport) obj;
    
    if (height != other.height)
      return false;
    
    if (width != other.width)
      return false;
    
    // Vérification de l'ensemble des couleurs du viewport
    if (!Arrays.deepEquals(image, other.image))
      return false;
    
    return true;
  }

  
}//fin classe SViewport
