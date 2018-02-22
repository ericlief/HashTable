public class CuckooHT extends HashTable {

	private long[] keysA; // table 1 for keys
	private long[] keysB; // table 2 for keys
	private Hash hashFuncA; // hash function a
	private Hash hashFuncB; // hash function b
	private long steps;
	private int maxRehashes; // to control when to cut off insert
	private int maxSteps; // to determine when to rehash

	public CuckooHT(long m, Hash hashA, Hash hashB) {
		super(m);
		maxRehashes = 20; // l = log2(m)
		maxSteps = 2 * maxRehashes; // here 40

		this.hashFuncA = hashA;
		this.hashFuncB = hashB;
		n = 0;
		steps = 0;

		// Build first table
		keysA = new long[(int) m];
		for (int i = 0; i < m; i++) // init array
			keysA[i] = -1; // since no null for primitives in Java...

		// Build second table
		keysB = new long[(int) m];
		for (int i = 0; i < m; i++) // init array
			keysB[i] = -1; // since no null for primitives in Java...

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

		n++; // increment size of elements in table
		long nRehashes = 0;
		long i; // pos in table

		// Key already in table(s)
		if (keysA[(int) hashFuncA.hash(k)] == k || keysB[(int) hashFuncB.hash(k)] == k)
			return -1;

		while (nRehashes < maxRehashes) {

			for (int loops = 0; loops < maxSteps; loops++) {

				i = hashFuncA.hash(k); // get new

				// First table pos vacant, so insert this key and we done
				if (keysA[(int) i] == -1) {
					keysA[(int) i] = k;
					return i;
				}

				// First table full, so insert this key and move other key to second table
				long otherKey = keysA[(int) i]; // swap keys
				keysA[(int) i] = k;
				k = otherKey;
				steps++;
				i = hashFuncB.hash(k); // index for table 2

				if (keysB[(int) i] == -1) { // first table full, so insert this key and move other key to second table
					keysB[(int) i] = k; // second table empty, so just insert key and stop
					return i; // negative pos indicates second table
				}

				k = otherKey;// Second table full, so get its current key and in next iter move other key to
				// second table
				otherKey = keysB[(int) i];
				keysB[(int) i] = k; // insert current key and swap
				k = otherKey;
				steps++;
			}

			// Loop limit exceeded, need to rehash and reinsert again
			rehash();
			nRehashes++;
		}

		// Failed insert, return
		n--;
		return -1;

	}

	/**
	 * Rehash both hash functions and rebuild table. This is done when a maximum
	 * limit of steps has been reached.
	 */
	public void rehash() {

		// Init new hash functions
		hashFuncA = hashFuncA.rehash();
		hashFuncB = hashFuncB.rehash();

		// Copy tables to temp
		long[] tmpA = new long[(int) m];
		for (int i = 0; i < m; i++) { // init array
			tmpA[i] = keysA[i]; // copy
			keysA[i] = -1; // erase
		}

		// Build second table
		long[] tmpB = new long[(int) m];
		for (int i = 0; i < m; i++) { // init array
			tmpB[i] = keysB[i]; // copy
			keysB[i] = -1; // since no null for primitives in Java...
		}

		// Reinsert elements from temp tables into new (empty) table
		for (int i = 0; i < m; i++) { // init array
			long k = tmpA[i]; // get key to reinsert
			if (k != -1)
				insert(k);
		}
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

		long i = hashFuncA.hash(k); // search in first pos
		System.out.printf("searching for %d at %d\n", k, i);

		if (keysA[(int) i] != k) {
			i = hashFuncB.hash(k); // search in second pos
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
