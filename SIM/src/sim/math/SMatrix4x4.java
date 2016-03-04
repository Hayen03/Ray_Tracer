/**
 * 
 */
package sim.math;

import java.util.Arrays;

import sim.exception.SNoImplementationException;

/**
 * La classe <b>SMatrix4x4</b> représentant une matrice de '4' lignes et '4' colonnes. 
 * Les composantes de la matrice respectent la notation suivante :
 * <p>M[i][j] = </p> 
 * <p>| m00  m01  m02  m03 |</p>
 * <p>| m10  m11  m12  m13 |</p>
 * <p>| m20  m21  m22  m23 |</p>
 * <p>| m30  m31  m32  m33 |</p>
 *  
 * Puisque l'implémentation de cette matrice se fera à l'aide d'un tableau à une dimension, on retrouve l'indexation suivante :
 * <p>M[i][j] = </p> 
 * <p>|  0    1    2    3  |</p>
 * <p>|  4    5    6    7  |</p>
 * <p>|  8    9   10   11  |</p>
 * <p>| 12   13   14   15  |</p>
 * 
 * @author Simon Vézina
 * @since 2015-05-27
 * @version 2015-12-21
 */
public class SMatrix4x4 {

  //-------------
  // VARIABLES //
  //-------------
  
	/**
	 * La variable 'matrix' correspond à la matrice 4x4 contenant les 16 éléments.
	 */
	private final double[] matrix;
	
	//-----------------
	// CONSTRUCTEURS //
	//-----------------
	
	/**
	 * Constructeur de la matrice nulle (tous les éléments M[i][j] seront nuls.)  
	 */
	public SMatrix4x4()
	{
		this(
			
			0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0
			
			);
	}

	/**
	 * Constructeur d'une matrice 4x4 avec paramètres en coordonnée M[i][j] tel que i correspond à la ligne
	 * et j correspond à la colonne. Visuellement, l'organisation des données correspond à la matrice suivante :
	 * <p>M = | m00  m01  m02  m03 |</p>
	 * <ul>| m10  m11  m12  m13 |</ul>
	 * <ul>| m20  m21  m22  m23 |</ul>
	 * <ul>| m30  m31  m32  m33 |</ul>
	 * 
	 * @param m00 - L'élément 00 de la matrice (colonne 1 : ligne 1)
	 * @param m01 - L'élément 01 de la matrice (colonne 2 : ligne 1)
	 * @param m02 - L'élément 02 de la matrice (colonne 3 : ligne 1)
	 * @param m03 - L'élément 03 de la matrice (colonne 4 : ligne 1)
	 * @param m10 - L'élément 10 de la matrice (colonne 1 : ligne 2)
   * @param m11 - L'élément 11 de la matrice (colonne 2 : ligne 2)
   * @param m12 - L'élément 12 de la matrice (colonne 3 : ligne 2)
   * @param m13 - L'élément 13 de la matrice (colonne 4 : ligne 2)
   * @param m20 - L'élément 20 de la matrice (colonne 1 : ligne 3)
   * @param m21 - L'élément 21 de la matrice (colonne 2 : ligne 3)
   * @param m22 - L'élément 22 de la matrice (colonne 3 : ligne 3)
   * @param m23 - L'élément 23 de la matrice (colonne 4 : ligne 3)
   * @param m30 - L'élément 30 de la matrice (colonne 1 : ligne 4)
   * @param m31 - L'élément 31 de la matrice (colonne 2 : ligne 4)
   * @param m32 - L'élément 32 de la matrice (colonne 3 : ligne 4)
   * @param m33 - L'élément 33 de la matrice (colonne 4 : ligne 4) 
	 */
	public SMatrix4x4(double m00, double m01, double m02, double m03,
        			      double m10, double m11, double m12, double m13,
        			      double m20, double m21, double m22, double m23,
        			      double m30, double m31, double m32, double m33)
	{
	  matrix = new double[]{
      m00,	  m01,	  m02,	 m03,
      m10,	  m11,	  m12,	 m13,
      m20,	  m21,	  m22,	 m23,
      m30,	  m31,	  m32,	 m33  };
	}
	
	/**
	 * Constructeur d'une matrice avec tableau des 16 composantes de la matrice.
	 * 
	 * @param m - Le tableau des 16 composantes de la matrice.
	 */
  private SMatrix4x4(double[] m)
  {
    matrix = m;
  }
	
  //------------
  // MÉTHODES //
  //------------
  
	/**
	 * Méthode qui effectue le produit matriciel entre deux matrices.
	 * La <b>matrice M1</b> qui lance l'appel de cette méthode sera la matrice de <b>gauche</b> et la <b>matrice M2</b>
	 * passée en paramètre sera la matrice de <b>droite</b>. Cette précision est nécessaire étant donné
	 * que le <b>produit matriciel n'est pas commutatif</b>.
	 * 
	 * <p>Le résutat du produit matriciel M = M1*M2 correspond au calcul</p>
	 * <ul>M[i][j] = SOMME sur k : M1[i][k]*M2[k][j]</ul>
	 * <p>où M[i][j] est une composantes de la matrice M.</p>
	 * 
	 * @param M - La matrice M2 de droite à multiplier avec la matrice M1 lançant l'appel de la méthode.
	 * @return Le résultat M du produit matriciel.
	 */
	public SMatrix4x4 multiply(SMatrix4x4 M) 
	{
		double[] m = M.matrix;
		
		return new SMatrix4x4(	
				
				// Premiere ligne
				matrix[0]*m[0] + matrix[1]*m[4] + matrix[2]*m[8] + matrix[3]*m[12],         
				matrix[0]*m[1] + matrix[1]*m[5] + matrix[2]*m[9] + matrix[3]*m[13],
				matrix[0]*m[2] + matrix[1]*m[6] + matrix[2]*m[10] + matrix[3]*m[14],
				matrix[0]*m[3] + matrix[1]*m[7] + matrix[2]*m[11] + matrix[3]*m[15],

				// Deuxieme ligne
				matrix[4]*m[0] + matrix[5]*m[4] + matrix[6]*m[8] + matrix[7]*m[12],         
				matrix[4]*m[1] + matrix[5]*m[5] + matrix[6]*m[9] + matrix[7]*m[13],
				matrix[4]*m[2] + matrix[5]*m[6] + matrix[6]*m[10] + matrix[7]*m[14],
				matrix[4]*m[3] + matrix[5]*m[7] + matrix[6]*m[11] + matrix[7]*m[15],

				// Troisieme ligne
				matrix[8]*m[0]	+ matrix[9]*m[4] + matrix[10]*m[8] + matrix[11]*m[12],        
				matrix[8]*m[1]	+ matrix[9]*m[5] + matrix[10]*m[9] + matrix[11]*m[13],
				matrix[8]*m[2]	+ matrix[9]*m[6] + matrix[10]*m[10] + matrix[11]*m[14],
				matrix[8]*m[3]	+ matrix[9]*m[7] + matrix[10]*m[11] + matrix[11]*m[15],

				// Quatrieme ligne
				matrix[12]*m[0]	+ matrix[13]*m[4] + matrix[14]*m[8]	+ matrix[15]*m[12], 				
				matrix[12]*m[1]	+ matrix[13]*m[5] + matrix[14]*m[9]	+ matrix[15]*m[13],
				matrix[12]*m[2]	+ matrix[13]*m[6] + matrix[14]*m[10] + matrix[15]*m[14],
				matrix[12]*m[3]	+ matrix[13]*m[7] + matrix[14]*m[11] + matrix[15]*m[15]
						
				);
	}

	/**
	 * Méthode qui effectue le produit entre une matrice 4x4 et un vecteur colonne en 4d.
	 * La <b>matrice M</b> qui lance l'appel de cette méthode sera à <b>gauche</b> et le <b>vecteur colonne</b> passé
	 * en paramètre sera à <b>droite</b>. Cette précision est nécessaire étant donné
	 * que le <b>produit entre une matrice et un vecteur n'est pas commutatif</b>.
	 * 
	 * <p>Le résutat du produit matriciel u = M*v correspond au calcul</p>
	 * <ul>u[i] = SOMME sur k : M[i][k]*v[k]</ul>
	 * <p>où u[i] est une composantes du vecteur colonne u.</p>
	 * 
	 * @param v - Le vecteur de droite à multiplier avec la matrice qui lance l'appel de la méthode.
	 * @return Le vecteur colonne u étant le résultat du produit de la matrice M avec le vecteur colonne v.
	 */
	public SVector4d multiply(SVector4d v)
	{
		return new SVector4d(	
				
				matrix[0]*v.getX() + matrix[1]*v.getY() + matrix[2]*v.getZ() + matrix[3]*v.getT(),
				matrix[4]*v.getX() + matrix[5]*v.getY() + matrix[6]*v.getZ() + matrix[7]*v.getT(),
				matrix[8]*v.getX() + matrix[9]*v.getY() + matrix[10]*v.getZ() + matrix[11]*v.getT(),
				matrix[12]*v.getX() + matrix[13]*v.getY() + matrix[14]*v.getZ() + matrix[15]*v.getT()
				
				);
	}
	
	/**
	 * Méthode qui effectue le produit entre une matrice 4x4 et un vecteur colonne en 3d dont une 4ième dimension t = 1.0 est ajoutée.
	 * La <b>matrice M</b> qui lance l'appel de cette méthode sera à <b>gauche</b> et le <b>vecteur colonne</b> passé
	 * en paramètre sera à <b>droite</b>. Cette précision est nécessaire étant donné
	 * que le <b>produit entre une matrice et un vecteur n'est pas commutatif</b>.
	 * 
	 * <p>Le résutat du produit matriciel u = M*v correspond au calcul</p>
	 * <ul>u[i] = SOMME sur k : M[i][k]*v[k]</ul>
	 * <p>où u[i] est une composantes du vecteur colonne u.</p>
	 * 
	 * @param v - Le vecteur de droite à multiplier avec la matrice qui lance l'appel de la méthode.
	 * @return Le vecteur colonne u étant le résultat du produit de la matrice M avec le vecteur colonne v.
	 */
  public SVector4d multiply(SVector3d v)
  {
    return multiply(new SVector4d(v));
  }
  
	/**
	 * <p>Méthode pour obtenir la matrice identité I.</p> 
	 * <p>Soit une matrice identité I et une
	 * matrice A quelconque, alors</p> 
	 * 
	 *  <ul> A = A*I = I*A </ul>
	 *  <p>ce qui rend la <b>matrice identité commutative</b>.</p>
	 * 
	 * @return La matrice identité I.
	 */
	public static SMatrix4x4 identity()
	{
		return new SMatrix4x4(
				
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
				
				);
	}
	
	/**
	 * Méthode pour obtenir la matrice transposée de la matrice qui lance l'appel de cette méthode.
	 * La matrice transposée B s'obtient à partir d'une matrice A grâce au calcul
   *
   * <ul> B[j][i] = A[i][j]</ul>
   * 
	 * @return La matrice transposée.
	 */
	public SMatrix4x4 transpose()
	{
		return new SMatrix4x4(
		    
		    matrix[0],  matrix[4],   matrix[8], matrix[12],
        matrix[1],  matrix[5],   matrix[9], matrix[13],
        matrix[2],  matrix[6],  matrix[10], matrix[14],
        matrix[3],  matrix[7],  matrix[11], matrix[15]
      
		    );
	}
	
	/**
	 * Méthode pour obtenir une matrice de transformation linéaire de translation Tr <b>par rapport
	 * à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Tr * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>
	 * 
	 * @param v - Le vecteur de translation en coordonnée cartésienne.
	 * @return La matrice de translation T.
	 */
	public static SMatrix4x4 translation(SVector3d v)
	{
		return SMatrix4x4.translation(v.getX(), v.getY(), v.getZ());
	}
	
	/**
	 * Méthode pour obtenir une matrice de transformation linéaire de translation Tr <b>par rapport
	 * à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Tr * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>
	 * 
	 * @param tr_x - La translation selon l'axe <i>x</i>.
	 * @param tr_y - La translation selon l'axe <i>y</i>.
	 * @param tr_z - La translation selon l'axe <i>z</i>.
	 * @return La matrice de translation T.
	 */
	public static SMatrix4x4 translation(double tr_x, double tr_y, double tr_z)
	{
		return new SMatrix4x4(
				
				1, 0, 0, tr_x,
			  0, 1, 0, tr_y,
			  0, 0, 1, tr_z,
			  0, 0, 0,  1
	  
				);
	}
	
	/**
	 * Méthode pour obtenir une matrice de transformation linéaire d'homothétie Sc (scale) <b>par rapport
	 * à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Sc * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>
	 * 
	 * @param v - Le vecteur d'homothétie en coordonnée cartésienne.
	 * @return La matrice d'homothétie Sc (scale).
	 */
	public static SMatrix4x4 scale(SVector3d v)
	{ 
		return SMatrix4x4.scale(v.getX(), v.getY(), v.getZ());
	}

	/**
	 * Méthode pour obtenir une matrice de transformation linéaire d'homothétie Sc (scale) <b>par rapport
	 * à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Sc * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>
	 * 
	 * @param sx - L'homothétie selon l'axe <i>x</i>.
	 * @param sy - L'homothétie selon l'axe <i>y</i>.
	 * @param sz - L'homothétie selon l'axe <i>z</i>.
	 * @return La matrice de transformation d'homothétie Sc (scale).
	 */
	public static SMatrix4x4 scale(double sc_x,double sc_y,double sc_z)
	{
	  return new SMatrix4x4(	
			  
			  sc_x,    0,    0,   0,
			     0, sc_y,    0,   0,
			     0,    0, sc_z,   0,
			     0,    0,    0,   1
			   
			  );
	}

	/**
	 * Méthode pour obtenir une matrice de transformation linéaire de rotation Rx autour de l'axe <i>x</i> 
	 * <b>par rapport à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Rx * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>  
	 * 
	 * @param degree - L'angle de rotation en degré.
	 * @return La matrice de rotation Rx autour de l'axe <i>x</i>.
	 */
	public static SMatrix4x4 rotationX(double degree)
	{
	  double angle = Math.toRadians(degree);
	  double sinO = Math.sin(angle);
	  double cosO = Math.cos(angle);

	  return new SMatrix4x4(	
			  
			  1,    0,     0, 0,
			  0, cosO, -sinO, 0,
			  0, sinO,  cosO, 0,
			  0,    0,     0, 1
			  
			  );
	}
	
	/**
	 * Méthode pour obtenir une matrice de transformation linéaire de rotation Ry autour de l'axe <i>y</i> 
	 * <b>par rapport à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Ry * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p>  
	 * 
	 * @param degree - L'angle de rotation en degré.
	 * @return La matrice de rotation Ry autour de l'axe <i>y</i>.
	 */
	public static SMatrix4x4 rotationY(double degree)
	{
	  double angle = Math.toRadians(degree);
	  double sinO = Math.sin(angle);
	  double cosO = Math.cos(angle);

	  return new SMatrix4x4(	
			  
          cosO,	  0, sinO,   0,
				     0, 	1,    0,   0,
				 -sinO, 	0, cosO,   0,
             0, 	0,    0,   1

	      );
	}
	
	/**
	 * Méthode pour obtenir une matrice de transformation linéaire de rotation Rz autour de l'axe <i>z</i> 
	 * <b>par rapport à l'origine</b> d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Rz * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p> 
	 *    
	 * @param degree - L'angle de rotation en degré.
	 * @return La matrice de rotation Rz autour de l'axe <i>z</i>.
	 */
	public static SMatrix4x4 rotationZ(double degree)
	{
	  double angle = Math.toRadians(degree);
	  double sinO = Math.sin(angle);
	  double cosO = Math.cos(angle);

	  return new SMatrix4x4(	
			 
			  cosO, -sinO,   0,   0,
			  sinO,  cosO,   0,   0,
			     0,     0,   1,   0,
			     0,     0,   0,   1

			  );
	}
	
	/**
	 * <p>Méthode pour obtenir une matrice de transformation linéaire de rotation Rzyx autour des 
	 * axes successifs <i>x</i>, <i>y</i> et <i>z</i> <b>par rapport à l'origine</b> 
	 * d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
	 * <ul>u = Rzyx * v</ul>
	 * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p> 
	 * <p>La matrice Rzyx est le résultat de l'opération</p>
	 * <ul> Rzyx = Rz*Ry*Rx</ul>
	 * <p>où Rx est la matrice de rotation autour de l'axe <i>x</i>, 
	 *       Ry est la matrice de rotation autour de l'axe <i>y</i> et
	 *       Rz est la matrice de rotation autour de l'axe <i>z</i> et
	 * 
	 * @param v - Le vecteur de rotation selon l'axe x,y et z en <b>degré</b>.
	 * @return La matrice de rotation Rzyx dans l'ordre <i>x</i>, <i>y</i> et <i>z</i> si elle est utilisée tel que u = Rzyx*v.
	 */
	public static SMatrix4x4 Rzyx(SVector3d v)
	{
	  throw new SNoImplementationException("Erreur SMatrix4x4 : La méthode n'est pas implémentée.");
	}
	
	/**
   * <p>Méthode pour obtenir une matrice de transformation linéaire de rotation Rxyz autour des 
   * axes successifs <i>z</i>, <i>y</i> et <i>x</i> <b>par rapport à l'origine</b> 
   * d'un système d'axe cartésien <i>xyz</i> si la matrice est utilisée dans l'ordre
   * <ul>u = Rxyz * v</ul>
   * <p>où v est le vecteur à transformer et u est le vecteur transformé.</p> 
   * <p>La matrice Rxyz est le résultat de l'opération</p>
   * <ul> Rxyz = Rx*Ry*Rz</ul>
   * <p>où Rx est la matrice de rotation autour de l'axe <i>x</i>, 
   *       Ry est la matrice de rotation autour de l'axe <i>y</i> et
   *       Rz est la matrice de rotation autour de l'axe <i>z</i> et
   * 
   * @param v - Le vecteur de rotation selon l'axe x,y et z en <b>degré</b>.
   * @return La matrice de rotation Rxyz dans l'ordre <i>z</i>, <i>y</i> et <i>x</i> si elle est utilisée tel que u = Rxyz*v.
   */
  public static SMatrix4x4 Rxyz(SVector3d v)
  {
    throw new SNoImplementationException("Erreur SMatrix4x4 : La méthode n'est pas implémentée.");
  }
  
	/**
	 * <p>Méthode pour obtenir une matrice de transformation linéaire d'homothétie Sc (<i>scale</i>), de rotation Rzyx autour des 
   * axes successifs <i>x</i>, <i>y</i> et <i>z</i> puis de translation Tr <b>par rapport à l'origine</b> 
   * d'un système d'axe cartésien <i>xyz</i>. La matrice TRzyxS est le résultat de l'opération
   * <ul> TrRzyxSc = Tr*Rz*Ry*Rx*Sc</ul>
   * et elle doit être utilisée sous la représentation
   * <ul> u = TrRzyxSc*v </ul>
   * où v est le vecteur à transformer et u est le vecteur transformé.</p>
	 * @param translation - Le vecteur représentant la translation selon l'axe <i>x</i>, <i>y</i> et <i>z</i>.
	 * @param rotation - Le vecteur rotation autour des avec <i>x</i>, <i>y</i> et <i>z</i> successivement.
	 * @param scale - Le vecteur d'homothétie (<i>scale</i>) à appliquer.
	 * @return La matrice de transformation linéaire TrRzyxSc.
	 */
	public static SMatrix4x4 TrRzyxSc(SVector3d translation, SVector3d rotation, SVector3d scale)
	{
	  throw new SNoImplementationException("Erreur SMatrix4x4 : La méthode n'est pas implémentée.");
	}
	
	/**
   * <p>Méthode pour obtenir une matrice de transformation linéaire de translation Tr, de rotation Rxyz autour des 
   * axes successifs <i>z</i>, <i>y</i> et <i>x</i> puis d'homothétie Sc (<i>scale</i>) <b>par rapport à l'origine</b> 
   * d'un système d'axe cartésien <i>xyz</i>. La matrice ScRxyzTr est le résultat de l'opération
   * <ul> ScRxyzTr = Sc*Rx*Ry*Rz*Tr</ul>
   * et elle doit être utilisée sous la représentation
   * <ul> u = ScRxyzTr*v .</ul>
   * où v est le vecteur à transformer et u est le vecteur transformé.</p>
   * @param scale - Le vecteur d'homothétie (<i>scale</i>) à appliquer.
   * @param rotation - Le vecteur rotation autour des avec <i>x</i>, <i>y</i> et <i>z</i> successivement.
   * @param translation - Le vecteur représentant la translation selon l'axe <i>x</i>, <i>y</i> et <i>z</i>.
   * @return La matrice de transformation linéaire ScRxyzTr.
   */
  public static SMatrix4x4 ScRxyzTr(SVector3d scale, SVector3d rotation, SVector3d translation)
  {
    throw new SNoImplementationException("Erreur SMatrix4x4 : La méthode n'est pas implémentée.");
  }
  
	
	@Override
  public String toString()
  {
    return  "|" + matrix[0] + "\t" +  matrix[1] + "\t" +  matrix[2] + "\t" +  matrix[3] + "|"  + "\n" +  
            "|" + matrix[4] + "\t" +  matrix[5] + "\t" +  matrix[6] + "\t" +  matrix[7] + "|"  + "\n" +
            "|" + matrix[8] + "\t" +  matrix[9] + "\t" +  matrix[10] + "\t" +  matrix[11] + "|"  + "\n" +
            "|" + matrix[12] + "\t" +  matrix[13] + "\t" +  matrix[14] + "\t" +  matrix[15] + "|"  + "\n";
  }
	
	@Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(matrix);
    return result;
  }

  @Override
  public boolean equals(Object obj) 
  {
    if(this == obj)
      return true;
    
    if(obj == null)
      return false;
    
    if(!(obj instanceof SMatrix4x4))
      return false;
    
    SMatrix4x4 other = (SMatrix4x4) obj;

    //Adressage des tableaux en mémoire
    if(matrix == other.matrix)
      return true;

    //Comparaison des valeurs dans le tableau en utilisant un calcul de comparaison SMath.nearlyEquals(a,b)
    for(int i = 0; i < 16; i++)
      if(!SMath.nearlyEquals(matrix[i], other.matrix[i]))
          return false;
    
    return true;
  }
  
}//fin de la classe SMatrix4x4
