package sim.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

import sim.exception.SNoImplementationException;

/**
 * La classe <b>SBufferedReader</b> représente un objet héritant de la classe BufferedReader avec l'option de connaître le numéro de la ligne déjà lu par le buffer.
 *
 * @author Simon Vézina
 * @see BufferedReader
 * @since 2014-12-21
 * @version 2015-12-05
 */
public class SBufferedReader extends BufferedReader {

  /**
   * La variable <b>At_line</b> correspond au numéro de la ligne en présente lecture.
   */
	private long at_line;
	
	/**
	 * La variable <b>mark_line</b> correspond à ??????????.
	 */
	private long mark_line;

	/**
	 * Creates a buffering character-input stream that uses a default-sized input buffer.
	 * @param in - A Reader
	 */
	public SBufferedReader(Reader in)
	{
		super(in);
		at_line = 1;
		mark_line = 0;
	}
	
	/**
	 * Creates a buffering character-input stream that uses an input buffer of the specified size. 
	 * @param in - A Reader
	 * @param sz - Input-buffer size
	 */
	public SBufferedReader(Reader in, int sz)
	{
		super(in, sz);
		at_line = 1;
		mark_line = 0;
	}
	
	/**
	 * Méthode pour obtenir le numéro de la ligne en présente lecture par le buffer.
	 * Si un appel de la méthode readLine() est effectuée, le buffer se positionnera sur la ligne suivante et augmentera le comptage de la ligne de 1.
	 * Ainsi, le numéro de la ligne associé au String retourné par la méthode readLine() sera getAtLine() - 1.  
	 * @return Le numéro de la ligne où le buffer est rendu dans sa lecture.
	 */
	public long atLine(){ return at_line;	}
	
	@Override
	public String readLine()throws IOException
	{
		at_line++;
		return super.readLine();
	}
	
	@Override
	public void mark(int readAheadLimit)throws IOException
	{
		super.mark(readAheadLimit);
		mark_line = at_line;
	}
	
	@Override
	public void reset()throws IOException
	{
		super.reset();
		at_line = mark_line;
	}

	@Override
	public long skip(long n)throws IOException
	{
		throw new SNoImplementationException("public long skip(long n)throws IOException - Méthode non implémentée.");
	}
    
	@Override
	public int read(char[] cbuf, int off, int len)throws IOException
	{
		throw new SNoImplementationException("public int read(char[] cbuf, int off, int len)throws IOException - Méthode non implémentée.");
	}
	
	@Override
	public int read()throws IOException
	{
		throw new SNoImplementationException("public int read()throws IOException - Méthode non implémentée.");
	}
	
}//fin classe SBufferedReader
