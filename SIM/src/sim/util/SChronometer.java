package sim.util;

import sim.exception.SRuntimeException;

/**
 * La classe SChronometer permet d'évaluer le temps écoulé avec une résolution en milliseconde.
 * @author Simon Vezina
 * @version 2014-12-08
 * @version 2014-12-25
 */
public class SChronometer 
{
	private long start_time;	// temps écoulé en milliseconde depuis January 1, 1970, 00:00:00 GMT pour signaler le début du chronomètre.
	private boolean isStarted;
	
	private long stop_time;		// temps écoulé en milliseconde depuis January 1, 1970, 00:00:00 GMT pour signaler l'arrêt du chronomètre.
	private boolean isStoped;
	
	/**
	 * Constructeur du chronometre.
	 */
	public SChronometer()
	{
		reset();
	}
	
	/**
	 * Méthode pour initialiser le chronometre.
	 */
	public void start()
	{	
		start_time = System.currentTimeMillis();	// Temps à partir de January 1, 1970, 00:00:00 GMT
		isStarted = true;
	}
	
	/**
	 * Méthode pour arrêter la mesure de l'écoulement du temps depuis l'appel de la méthode start().
	 */
	public void stop()
	{
		stop_time = System.currentTimeMillis();		// Temps à partir de January 1, 1970, 00:00:00 GMT
		isStoped = true;
	}
	
	/**
	 * Méthode pour réinitialiser le chronomètre.
	 */
	public void reset()
	{
		start_time = 0;
		isStarted = false;
		
		stop_time = 0;
		isStoped = false;
	}
	
	/**
	 * Méthode pour obtenir l'écoulement du temps entre l'appel de la fonction start() et stop().
	 * @return Temps écoulé en seconde.
	 * @throws SRuntimeException S'il l'appel de la méthode start() n'a pas encore initialisée le chronomètre.
	 */
	public double getTime()throws SRuntimeException
	{
		return (double) getTimeMilliseconde() / 1000;
	}
	
	/**
	 * Méthode pour obtenir l'écoulement du temps entre l'appel de la fonction start() et stop().
	 * @return Temps écoulé en milliseconde.
	 * @throws SRuntimeException S'il l'appel de la méthode start() n'a pas encore initialisée le chronomètre.
	 */
	public long getTimeMilliseconde()throws SRuntimeException
	{
		if(!isStarted)
			throw new SRuntimeException("Erreur SChronometer 001 : La méthode start() du chronomètre n'a pas encore initialisée le chronomètre.");
		
		if(isStoped)
			return stop_time - start_time;
		else
		{
			//Si le chronomètre n'a pas été stoppé, retourner le temps écoulé entre le start() et l'appel de la fonction getTimeMilliseconde()
			long current_time = System.currentTimeMillis();
			return current_time - start_time;
		}
	}
	
	/**
	 * Méthode test pour vérifier la classe SChronometer.
	 * @param args
	 */
	public static void main(String[] args)
	{
		Test1();
	}

	/**
	 * Schronometer Test #1 
	 */
	private static void Test1()
	{
		SChronometer c = new SChronometer();
		
		//Écriture du temps écoulé (sera en exception).
		try{
		System.out.println(String.valueOf(c.getTime()) + " s");
		}catch(Exception e){System.out.println(e); }
		
		
		
		//Écoulement du temps débuté avec méthode "boucle bidon"
		c.start();
		System.out.println("Début de la boucle bidon.");
		for(int i=0; i<1000000; i++)
			for(int j=0; j<1000; j++)
				c.stop();
		System.out.println("Fin de la boucle bidon.");
		c.stop();
		
		//Écriture du temps écoulé.
		try{
		System.out.println(String.valueOf(c.getTime()) + " s");
		}catch(Exception e){}
		
		try{
		System.out.println(String.valueOf(c.getTimeMilliseconde()) + " ms");
		}catch(Exception e){}
	}
}//end SChronometer
