
abstract class HashTable {

    long m;	// size of table
    long n;	// number of elements

    public HashTable(long m) {
	this.m = m;			// capacity of table
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
    abstract long insert(long k);

    /**
     * Find a given key at a given hash index. Useful if one wishes to use a
     * specific hash function
     * 
     * @param k
     *            key
     *
     * @return i index or -1 if not found
     */
    abstract long find(long k);
}
