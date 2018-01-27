
public class CuckooHT extends HashTable {

    private long[] keysA;	// table 1 for keys
    private long[] keysB;	// table 2 for keys
    private Hash hashFuncA;	// hash function a
    private Hash hashFuncB;	// hash function b
    private long steps;
    private int maxSteps = 100;		// log2(n), maybe make 100
    private int maxRehashes = 10;	// trial and error

    public CuckooHT(long m, Hash hashA, Hash hashB) {
	super(m);
	this.hashFuncA = hashA;
	this.hashFuncB = hashB;
	n = 0;
	steps = 0;

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

	n++;	// increment size of elements in table
	long nRehashes = 0;
	//	long i = hashFuncA.hash(k, m);	// index used in chain
	long i; // pos in table
	//	long initHash = i; // position of initial k insertion which will be returned

	//	long initKey = k;

	while (nRehashes < maxRehashes) {

	    //	    System.out.println("No rehashes=" + nRehashes);

	    for (int loops = 0; loops < maxSteps; loops++) {

		i = hashFuncA.hash(k, m);	// get new 

		//		System.out.println("hashA=" + i);
		//		System.out.println("loops=" + loops);

		// First table pos vacant, so insert this key and we done
		if (keysA[(int) i] == -1) {
		    keysA[(int) i] = k;
		    //		    System.out.println("inserted " + k);
		    return i;
		}

		// First table full, so insert this key and move other key to second table
		long otherKey = keysA[(int) i];	// swap keys
		keysA[(int) i] = k;
		k = otherKey;
		steps++;
		i = hashFuncB.hash(k, m);	// index for table 2

		//		System.out.println("hashA=" + i);

		if (keysB[(int) i] == -1) {		// first table full, so insert this key and move other key to second table
		    keysB[(int) i] = k;		// second table empty, so just insert key and stop
		    return i;	// negative pos indicates second table
		}

		//		    System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
		//		    System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, localSteps);

		// Second table full, so get its current key and in next iter move other key to second table
		otherKey = keysB[(int) i];
		keysB[(int) i] = k;		// insert current key and swap 
		k = otherKey;
		steps++;
	    }

	    // Loop limit exceeded, need to rehash and reinsert again
	    rehash();
	    nRehashes++;
	    //	    System.out.printf("Post-resize init insertion of %d at %d in %d steps\n", k, initHash, steps);

	}

	//	System.out.println("max rehash limit reached");
	n--;
	return -1;

	//	i = find(initKey);
	//	System.out.println("before " + keysA[(int) i]);
	//	keysA[(int) i] = -1; // delete inserted key for re-insert
	//	System.out.println("after deleted " + keysA[(int) i]);

    }

    // steps now 2, 4, etc..
    //		long otherKeyA = keysA[(int) i];
    //		keysA[(int) i] = k;
    //		System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);

    //		i = hashFuncB.hash(k, m);	// index for table 2
    //		if (keysB[(int) i] != -1) {	// second table full, so insert this key and move other key to first table
    //		    long otherKeyB = keysB[(int) i];
    //		    keysB[(int) i] = k;
    //		    //		    System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
    //		    k = otherKeyB;
    //		    i = hashFuncA.hash(k, m);	// index for table 1
    //		} else {
    //		    keysB[(int) i] = k;		// second table empty, so just insert key and stop
    //		    //		    System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
    //		    //		    System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, localSteps);
    //		    steps += localSteps;
    //		    return initKey;
    //		}
    //
    //	    } else {
    //		keysA[(int) i] = k;	// first table empty so insert and we're done
    //		//		System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
    //		//		System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, localSteps);
    //		steps += localSteps;
    //		return initKey;
    //	    }
    //
    //	}while(localSteps<maxSteps);
    //
    //    if(nRehashes>10)
    //
    //    {
    //	System.out.println();
    //	return -1;
    //    }
    //
    //    i=find(initKey);
    //	//	System.out.println("before " + keysA[(int) i]);
    //	keysA[(int) i] = -1; // delete inserted key for re-insert
    //	//	System.out.println("after deleted " + keysA[(int) i]);
    //	rehash(2 * m);
    //	nRehashes++;
    //	n--;
    //	insert(initKey);
    //	System.out.printf("Post-resize init insertion of %d at %d in %d steps\n", initKey, initHash, steps);
    //	return initKey;
    //
    //    }

    /**
     * Insert a given key at a given hash index. Useful if one wishes to use a
     * specific hash function
     * 
     * @param k
     *            key
     * @return i index
     */
    //    @Override
    public long insert2(long k) {

	//	if (n == m)
	//	    throw new IllegalStateException("Hash table is full!");

	//	System.out.println("hash=" + i);
	//	System.out.println("hash=" + (int) i);
	//	System.out.println(keys.length);

	n++;
	long localSteps = 0;	// count probing length

	long nRehashes = 0;

	//	int maxSteps = (int) (Math.log(n) / Math.log(2));
	int maxSteps = 5; // 2 << n;		// log2(n) ?x
	long i = hashFuncA.hash(k, m);	// index used in chain
	long initHash = i; // position of initial k insertion which will be returned
	long initKey = k;
	do {
	    localSteps++;			// steps now 1, 2, etc..
	    //	    System.out.println("n elements " + n);
	    //	    System.out.println("begin loop steps=" + localSteps);
	    //	    System.out.println("max steps " + maxSteps);
	    //	    System.out.println((int) (Math.log(n) / Math.log(2)));
	    //	    int maxSteps = (int) ((int) Math.log(n) / Math.log(2));	// calc for each insert

	    if (keysA[(int) i] != -1) {		// first table full, so insert this key and move other key to second table
		localSteps++;			// steps now 2, 4, etc..
		long otherKeyA = keysA[(int) i];
		keysA[(int) i] = k;
		//		System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
		k = otherKeyA;
		i = hashFuncB.hash(k, m);	// index for table 2
		if (keysB[(int) i] != -1) {	// second table full, so insert this key and move other key to first table
		    long otherKeyB = keysB[(int) i];
		    keysB[(int) i] = k;
		    //		    System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
		    k = otherKeyB;
		    i = hashFuncA.hash(k, m);	// index for table 1
		} else {
		    keysB[(int) i] = k;		// second table empty, so just insert key and stop
		    //		    System.out.printf("inserted %d at %d in %d steps\n", k, i, localSteps);
		    //		    System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, localSteps);
		    steps += localSteps;
		    return initKey;
		}

	    } else {
		keysA[(int) i] = k;	// first table empty so insert and we're done
		//		System.out.printf("inserted %d at %d in %d steps\n", k, i, nSteps);
		//		System.out.printf("init insertion of %d at %d in %d steps\n", initKey, initHash, localSteps);
		steps += localSteps;
		return initKey;
	    }

	} while (localSteps < maxSteps);

	if (nRehashes > 10) {
	    System.out.println();
	    return -1;
	}

	i = find(initKey);
	//	System.out.println("before " + keysA[(int) i]);
	keysA[(int) i] = -1; // delete inserted key for re-insert
	//	System.out.println("after deleted " + keysA[(int) i]);
	rehash();
	nRehashes++;
	n--;
	insert(initKey);
	System.out.printf("Post-resize init insertion of %d at %d in %d steps\n", initKey, initHash, steps);
	return initKey;

    }

    public void rehash() {
	//	System.out.println("Rehashing");
	// Init new hash functions
	hashFuncA = hashFuncA.rehash();
	hashFuncB = hashFuncB.rehash();

	// Reinsert elements into new table, can be resized if necessary
	long[] tmpA = new long[(int) m];
	for (int i = 0; i < m; i++) {		// init array
	    long k = keysA[i];
	    if (k != -1)
		tmpA[(int) hashFuncA.hash(k, m)] = k;
	}

	long[] tmpB = new long[(int) m];
	for (int i = 0; i < m; i++) {		// init array
	    long k = keysB[i];
	    if (k != -1)
		tmpB[(int) hashFuncB.hash(k, m)] = k;
	}

	keysA = tmpA;
	keysB = tmpB;

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

    public long steps() {
	return steps;
    }

    public void resetSteps() {
	steps = 0;
    }

}
