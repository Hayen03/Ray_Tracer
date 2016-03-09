/**
 * 
 */
package sim.physics;

import sim.exception.SNoImplementationException;
import sim.exception.SRuntimeException;
import sim.math.SVector3d;

/**
 * La classe <b>SOptics</b> repr�sente une classe utilisaire pouvante effectuer des calculs en lien avec la <b>physique optique</b>.
 * 
 * @author Simon V�zina
 * @since 2015-01-16
 * @version 2015-12-21
 */
public class SOptics {

	/**
	 * M�thode qui �value la <b>r�flexion</b> d'un rayon <b><i>v</i></b> sur une normale � la surface <b><i>N</i></b>.
	 * La solution � la r�flexion est un rayon r�fl�chi <b><i>R</i></b>. Ce calcul respecte la <u>loi de la r�flexion</u>.
	 * 
	 * @param v - Le rayon <b><i>v</i></b> incident.  
	 * @param N - La normale � la surface <b><i>N</i></b>.
	 * @return Le rayon r�fl�chi <b><i>R</i></b>.
	 */
	public static SVector3d reflexion(SVector3d v, SVector3d N)
	{
		return v.add(N.multiply(v.multiply(-1).dot(N)*2));
//		throw new SNoImplementationException("Cette m�thode doit �tre impl�ment�e dans le cadre d'un laboratoire.");
	}
	
	/**
	 * M�thode qui �value la <b>r�fraction</b> d'un rayon <b><i>v</i></b> par rapport � une normale � la surface <b><i>N</i></b>.
	 * La solution � la r�fraction est un rayon transmis <b><i>T</i></b>. Ce calcul respecte la <u>loi de la r�fraction</u>.
	 * 
	 * @param v - Le rayon <b><i>v</i></b> incident.  
	 * @param N - La normale � la surface <b><i>N</i></b>.
	 * @param n1 - L'indice de r�fraction du milieu incident.
	 * @param n2 - L'indice de r�fraction du milieu r�fract�.
	 * @return Le rayon transmis <b><i>T</i></b>.
	 * @throws SRuntimeException S'il y a r�flexion totale interne ce qui interdit la transmission d'un rayon selon la loi de la r�fraction.
	 */
	public static SVector3d refraction(SVector3d v, SVector3d N, double n1, double n2)throws SRuntimeException
	{
		SVector3d E = v.multiply(-1);
		double n = n1/n2;
		double dot = E.dot(N);
		return v.multiply(n).add(N.multiply(n*dot - Math.sqrt(1-n*n*(1-dot*dot))));
		
//	  throw new SNoImplementationException("Cette m�thode doit �tre impl�ment�e dans le cadre d'un laboratoire.");
	}
	
	/**
	 * M�thode qui d�termine s'il y aura r�flexion totale interne
	 * 
	 * @param v - Le rayon <b><i>v</i></b> incident.  
	 * @param N - La normale � la surface <b><i>N</i></b>.
	 * @param n1 - L'indice de r�fraction du milieu incident.
	 * @param n2 - L'indice de r�fraction du milieu r�fract�.
	 * @return
	 */
	public static boolean isTotalInternalReflection(SVector3d v, SVector3d N, double n1, double n2)
	{
		double dot = v.dot(N), n = n2/n1;
		return dot*dot + n*n <= 1;
		//throw new SNoImplementationException("Cette m�thode doit �tre impl�ment�e dans le cadre d'un laboratoire.");
	}

}//fin classe SOptics
