
abstract class HashTable {

	long m; // size of table
	long n; // number of elements

	public HashTable(long m) {
		this.m = m; // capacity of table
		n = 0;

	}

	/**
	 * Insert a given key at a given hash index. Useful if one wishes to use a
	 * specific hash function
	 * 
	 * @param k
	 *            key
	 * @param j
	 *            hashed index
	 * @return i index
	 */
	abstract boolean insert(long k);

	/**
	 * Find a given key at a given hash index. Useful if one wishes to use a
	 * specific hash function
	 * 
	 * @param k
	 *            key
	 *
	 * @return i index or -1 if not found
	 */
	abstract boolean find(long k);

	/**
	 * Helper method to comput log2 using bit shifts
	 * 
	 * @param x
	 * @return log2(x)
	 */
	public static long log2(long x) {
		int r = 0;
		while (x > 1) {
			x >>= 1;
			r++;
		}
		return r;
	}

}
