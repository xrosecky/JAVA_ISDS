package cz.abclinuxu.datoveschranky.tinyDB.holders;

/**
 * 
 * Interface pro čtení obsahu XML elementu.
 * 
 * @author Vaclav Rosecky <xrosecky 'at' gmail 'dot' com>
 */
public interface OutputHolder<T> {

	public void write(char[] array, int start, int length);

	/**
	 * Vrátí výsledek čtení.
	 */
	public T getResult();
}
