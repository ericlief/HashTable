
public class CuckooHT extends HashTable {

    private long[] keysA;	// table 1 for keys
    private long[] keysB;	// table 2 for keys
    private Hash hashFuncA;	// hash function a
    private Hash hashFuncB;	// hash function b

    public CuckooHT(long m, Hash hashA, Hash hashB) {
	super(m);
	this.hashFuncA = hashA;
	this.hashFuncB = hashB;
	n = 0;

	// Build first table
	keysA = new long[(int) m];
	for (int i = 0; i < m; i++)		// init array
	    keysA[i] = -1;			// since no null for primitives in Java...

	// Build second table
	keysB = new long[(int) m];
	for (int i = 0; i < m; i++)		// init array
	    keysB[i] = -1;			// since no null for primitives in Java...

    }

    /**
     * Insert a given key at a given hash index. Useful if one wishes to use a
     * specific hash function
     * 
     * @param k
     *            key
     * @return i index
     */
    @Override
    public long insert(long k) {

	//	if (n == m)
	//	    throw new IllegalStateException("Hash table is full!");

	//	System.out.println("hash=" + i);
	//	System.out.println("hash=" + (int) i);
	//	System.out.println(keys.length);

	n++;
	int nSteps = 0;	// count probing length
	int maxSteps = (int) (Math.log(n) / Math.log(2));
	long i = hashFuncA.hash(k, m);	// index used in chain
	long initHash = i; // position of initial k insertion which will be returned
	long initKey = k;
	do {
	    nSteps++;			// steps now 1, 2, etc..
	    System.out.println("n elements " + n);
	    System.out.println("begin loop steps=" + nSteps);
	    System.out.println("max steps " + maxSteps);
	    System.out.println((int) (Math.log(n) / Math.log(2)));
	    //	    int maxSteps = (int) ((int) Math.log(n) / Math.log(2));	// calc for each insert

	    if (keysA[(int) i] != -1) {		// first table full, so insert this key and move other key to second table
		nSteps++;			// steps now 2, 4, etc..
		long otherKeyA = keysA[(int) i];
		keysA[(int) i] = k;
		System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
		k = otherKeyA;
		i = hashFuncB.hash(k, m);	// index for table 2
		if (keysB[(int) i] != -1) {	// second table full, so insert this key and move other key to first table
		    long otherKeyB = keysB[(int) i];
		    keysB[(int) i] = k;
		    System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
		    k = otherKeyB;
		    i = hashFuncA.hash(k, m);	// index for table 1
		} else {
		    keysB[(int) i] = k;		// second table empty, so just insert key and stop
		    System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
		    System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, nSteps);
		    return initKey;
		}

	    } else {
		keysA[(int) i] = k;	// first table empty so insert and we're done
		//		System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
		System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, nSteps);
		return initKey;
	    }

	} while (nSteps < maxSteps);
	i = find(initKey);
	System.out.println("before " + keysA[(int) i]);
	keysA[(int) i] = -1; // delete inserted key for re-insert
	System.out.println("after deleted " + keysA[(int) i]);
	rehash(2 * m);
	n--;
	insert(initKey);
	System.out.printf("Post-resize init insertion of %d at %d in %d steps\n", initKey, initHash, nSteps);
	return initKey;

    }

    public void rehash(long capacity) {
	System.out.println("Resizing");

	long[] tmpA = new long[(int) capacity];
	for (int i = 0; i < m; i++)		// init array
	    tmpA[i] = -1;			// since no null for primitives in Java...

	long[] tmpB = new long[(int) capacity];
	for (int i = 0; i < m; i++)		// init array
	    tmpB[i] = -1;			// since no null for primitives in Java...

	// Copy first table
	for (int i = 0; i < m; i++) {
	    if (keysA[i] != -1)
		tmpA[i] = keysA[i];
	}

	// Copy second table
	for (int i = 0; i < m; i++) {
	    if (keysB[i] != -1)
		tmpB[i] = keysB[i];
	}

	keysA = tmpA;
	keysB = tmpB;
	m = capacity;
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

	long i = hashFuncA.hash(k, m);	// search in first pos
	System.out.printf("searching for %d at %d\n", k, i);

	if (keysA[(int) i] != k) {
	    i = hashFuncB.hash(k, m);	// search in second pos
	    System.out.printf("searching for %d at %d\n", k, i);

	    if (keysB[(int) i] != k)
		throw new IllegalStateException("item not found");
	}
	System.out.printf("found %d at %d\n", k, i);

	return i;
    }

    //    public static void main(String[] args) {
    ////	LinearProbeHT ht = new LinearProbHT(m)
    //		
    //    }
}
