
public class LinearProbeHT extends HashTable {

    private long[] keys;
    // private long[] values;
    private Hash hashFunc;	// chosen h(x)

    public LinearProbeHT(long m, Hash hashFunc) {
	super(m);			// instantiate super class
	this.hashFunc = hashFunc;	// specified h(x)

	// Init array to hold table
	keys = new long[(int) m];
	for (int i = 0; i < m; i++)
	    keys[i] = -1;			// since no null for primitives in Java...

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
    @Override
    public long insert(long k) {
	int nSteps = 1;	// count probing length

	if (n == m)
	    throw new IllegalStateException("Hash table is full!");

	long i = hashFunc.hash(k, m);
	//	System.out.println("hash=" + i);
	//	System.out.println("hash=" + (int) i);
	//	System.out.println(keys.length);
	while (keys[(int) i] != -1) {
	    //	    System.out.println("i=" + i);
	    i = (i + 1) % m;
	    nSteps++;
	}
	keys[(int) i] = k;
	System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
	n++;
	return i;
    }

    /**
     * Find a given key at a given hash index. Useful if one wishes to use a
     * specific hash function
     * 
     * @param k
     *            key
     * @param j
     *            hashed index or
     * @return i index or -1 if not found
     */
    @Override
    public long find(long k) {

	long i = hashFunc.hash(k, m);
	while (keys[(int) i] != k) {
	    i = (i + 1) % m;
	}
	if (keys[(int) i] == k)
	    return i;
	else
	    return -1;
    }

    //    public static void main(String[] args) {
    ////	LinearProbeHT ht = new LinearProbHT(m)
    //		
    //    }
}
