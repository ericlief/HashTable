public class CuckooHT extends HashTable {
	long[][] table = new long[2][]; // the ht
	long[][] tableCopy = new long[2][]; // used to restore to prev state
	private Hash[] hashFuncs;
	private long swaps; // swaps per insertion attempt
	private long steps; // total steps including rehash insertions
	private long maxRehashes; // to control when to cut off insert
	private long maxSwaps; // to determine when to rehash
	private int nRehashes; // counter for each insert of a new key

	public CuckooHT(long m, Hash hashA, Hash hashB, long maxSwaps) {
		super(m);
		this.hashFuncs = new Hash[] { hashA, hashB };
		this.maxRehashes = 10; // = log2(m) / 2;
		this.maxSwaps = maxSwaps; // max loops only depends on max number of items in hash
									// source (Dr. Mares' class notes, 6-12)
		this.nRehashes = 0;
		n = 0;
		// Build first table
		long[] keysA = new long[(int) m / 2];
		for (int i = 0; i < m / 2; i++) // init array
			keysA[i] = -1; // since no null for primitives in Java...
		// Build second table / 2
		long[] keysB = new long[(int) m / 2];
		for (int i = 0; i < m / 2; i++) // init array
			keysB[i] = -1; // since no null for primitives in Java...
		this.table[0] = keysA;
		this.table[1] = keysB;
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
	public boolean insert(long k) {
		if (find(k))
			return false;
		// initial insertion
		tableCopy = deepCopyHashTable(table); // copy tables once
		boolean status = put(k); // call internal method put()
		if (status) {
			n++;
			return true;
		}
		table = tableCopy; // revert back to old tables
		return false;
	}

	/**
	 * Internal insertion method
	 * 
	 * @param k
	 * @return true if insertion is successful or else false, for failure rehashing
	 */
	private boolean put(long k) {
		if (nRehashes >= maxRehashes)
			return false;
		int whichTable = 0;
		swaps = 0;
		while (swaps <= maxSwaps) {
			long i = hashFuncs[whichTable].hash(k); // smoke new hash
			if (table[whichTable][(int) i] != -1) {
				swaps++;
				steps++;
				long ejectedKey = table[whichTable][(int) i]; // eject key
				table[whichTable][(int) i] = k; // insert new key
				k = ejectedKey; // swap
				whichTable = Math.abs(whichTable - 1); // goto other table for next iter
			} else {
				// This table pos vacant, so insert this key and we done
				table[whichTable][(int) i] = k; // insert
				return true;
			}
		}
		// Need to rehash and then insert uninserted element after
		if (nRehashes <= maxRehashes) {
			rehash();
			put(k);
		}
		// Failed (max limit for) rehash
		return false;
	}

	/**
	 * Rehash both hash functions and rebuild table. This is done when a maximum
	 * limit of steps has been reached. int nRehashes,
	 * 
	 * @param nRehashes
	 * @param k
	 *            table[tableNumber][i] = -1; this is the last key that needs to be
	 *            re-inserted
	 */
	public void rehash() {
		if (nRehashes >= maxRehashes)
			return;
		// Keep track of rehashes
		nRehashes++;
		// Init new hash functions
		for (int hash = 0; hash < 2; hash++)
			hashFuncs[hash] = hashFuncs[hash].rehash();
		// Check tables and if keys are no in correct places the
		// reinsert
		for (int tableNumber = 0; tableNumber < 2; tableNumber++) {
			for (int i = 0; i < m / 2; i++) { // init array
				long k = table[tableNumber][i];
				if (k != -1 && !find(k)) {
					table[tableNumber][i] = -1;
					put(k);
				}
			}
		}
	}

	/**
	 * Find a given key in one of its two positions.
	 * 
	 * @param k
	 *            key
	 * @param j
	 *            hashed index or
	 * @return i index or -1 if not found
	 */
	@Override
	public boolean find(long k) {
		return (table[0][(int) hashFuncs[0].hash(k)] == k || table[1][(int) hashFuncs[1].hash(k)] == k);
	}

	public long steps() {
		return steps;
	}

	public long getNRehashes() {
		return nRehashes;
	}

	public void resetSteps() {
		steps = 0;
	}

	public void print() {
		System.out.println("printing table a");
		for (int i = 0; i < m / 2; i++)
			System.out.print(table[0][i] + " ");
		System.out.println();
		System.out.println("printing table b");
		for (int i = 0; i < m / 2; i++)
			System.out.print(table[1][i] + " ");
		System.out.println();
	}

	/**
	 * Deep copy a table (used to backup and restore to another state)
	 * 
	 * @param src
	 * @return new table
	 */
	public long[][] deepCopyHashTable(long[][] src) {
		long[][] newTable = new long[2][];
		// Copy first table to temp set
		for (int tableX = 0; tableX < 2; tableX++) {
			long[] keys = new long[(int) m / 2];
			for (int i = 0; i < m / 2; i++)
				keys[i] = src[tableX][i];
			newTable[tableX] = keys;
		}
		return newTable;
	}

	public static void main(String[] args) {
		// Unit tests
		int w = 32; // 32-bit unsigned
		long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
		int l = 3; // bits, m=2^l
		long m = 2L << l - 1; // size of table, here 2^20-1 bits
		System.out.println("size of table =" + m);
		// Init random generator (my class not java.util)
		RandGenerator rand = new RandGenerator(0, MAX_U32);
		Hash hashA, hashB; // two hash funcs for table
		long key;
		// Cuckoo with mult shift
		// hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		// hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		hashA = new TabularHash(w, m / 2);
		hashB = new TabularHash(w, m / 2);
//		long maxSwaps = Hash.log2(m);
		long maxSwaps = 6*l;
		CuckooHT ht = new CuckooHT(m, hashA, hashB, maxSwaps);
		// System.out.println("inserting");
		for (int j = 0; j < m; j++) {
			key = rand.nextLong(); // generate random element
			// System.out.println("inserting " + key);
			boolean idx = ht.insert(key);
			if (!idx)
				System.out.println("failed insert with rehashes for key " + key);
		}
		System.out.println("printing table a");
		for (int i = 0; i < m / 2; i++)
			System.out.println(ht.table[0][i]);
		System.out.println("printing table b");
		for (int i = 0; i < m / 2; i++)
			System.out.println(ht.table[1][i]);
	}
}
