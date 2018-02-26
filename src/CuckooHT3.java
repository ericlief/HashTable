public class CuckooHT3 extends HashTable {
	long[][] table = new long[2][];
	long[][] tableCopy = new long[2][];
	// private long[] keysA; // table 1 for keys
	// private long[] keysB; // table 2 for keys
	// private long[] tempKeySet; // set of keys already inserted
	// private Hash hashFuncA; // hash function a
	// private Hash hashFuncB; // hash function b
	private Hash[] hashFuncs;
	private long swaps; // swaps per insertion attempt
	private long steps; // total steps including rehash insertions
	private long maxRehashes; // to control when to cut off insert
	private int maxSwaps; // to determine when to rehash
	private int nRehashes; // counter for each insert of a new key

	public CuckooHT3(long m, Hash hashA, Hash hashB) {
		super(m);
		// maxRehashes = 20; // l = log2(m)
		// maxSwaps = 2 * maxRehashes; // here 40
		// this.hashFuncA = hashA;int
		// this.hashFuncB = hashB;
		this.hashFuncs = new Hash[] { hashA, hashB };
		this.maxRehashes = log2(m) / 2;
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
		// nRehashes = 0; // reset rehashes
		maxSwaps = (int) log2(n) + 1; // max loops, only computed once at time of initial insertion
		// maxRehashes = (int) 1.5 * maxSwaps;
		// nRehashes = 0;
		// System.out.println("trying to insert " + k);
		// System.out.println("els in table = " + n + " max = " + maxSwaps);
		// System.out.println(nRehashes + "/" + maxRehashes);
		// swaps = 0;
		// boolean hasInserted;
		// long[][] tableCopy = deepCopyHashTable(); // a spare HT to revert to if
		// needed
		// while (!(hasInserted = insert(k, 0))) {
		// if (nRehashes > maxRehashes)
		//// break;
		// rehash(k);
		// // status = insert(k, 0);
		// }
		// steps += swaps;
		// if (!hasInserted) {
		// table = tableCopy; // revert
		// return false;
		// }
		// n++;
		// return true;
		tableCopy = deepCopyHashTable(table); // copy tables once
		boolean status = put(k); // call internal method put()
		// steps += swaps; // update total steps
		if (status) {
			n++;
			return true;
		}
		table = tableCopy; // revert back to old tables
		// System.out.println("failed with rehashes = " + nRehashes + "/" +
		// maxRehashes);
		return false;
	}

	private boolean put(long k) {
		if (nRehashes >= maxRehashes)
			return false;
		// System.out.println("put key " + k);
		// System.out.println("rehashes: " + nRehashes + "/" + maxRehashes);
		// print();
		int whichTable = 0;
		swaps = 0;
		while (swaps <= maxSwaps) {
			// System.out.println("swaps: " + swaps + "/" + maxSwaps);
			// System.out.println("table " + whichTable);
			long i = hashFuncs[whichTable].hash(k); // smoke new hash
			if (table[whichTable][(int) i] != -1) {
				swaps++;
				steps++;
				// System.out.println("swaps " + swaps);
				long ejectedKey = table[whichTable][(int) i]; // eject key
				// System.out.println("ejecting " + ejectedKey + " from " + "table " +
				// whichTable);
				table[whichTable][(int) i] = k; // insert new key
				k = ejectedKey; // swap
				whichTable = Math.abs(whichTable - 1); // goto other table for next iter
				// print();
			} else {
				// This table pos vacant, so insert this key and we done
				// System.out.println("inserted " + k + " in " + "table " + whichTable);
				table[whichTable][(int) i] = k; // insert
				return true;
			}
		}
		// Need to rehash, but first need to restore to previous state
		// table = deepCopyHashTable(tableCopy); // revert back to old tables
		if (nRehashes <= maxRehashes) {
			rehash();
			put(k);
		}
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
		// if (nRehashes > maxRehashes) {
		// System.out.println("num rehashes " + nRehashes);
		// return;
		// }
		// Init new hash functions
		for (int hash = 0; hash < 2; hash++)
			hashFuncs[hash] = hashFuncs[hash].rehash();
		// // Store inserted keys in a set for further rehashes
		// if (nRehashes == 0) {
		//
		// // Copy first table to temp set
		// int j = 1; // to hold place in set (already occupied by at least first
		// expelled key)
		// tempKeySet = new long[2 * (int) m];
		// for (int i = 0; i < m; i++) { // init array
		// if (keysA[i] != -1) {
		// tempKeySet[j++] = keysA[i]; // copy to set
		// keysA[i] = -1; // erase
		// }
		// }
		// for (int i = 0; i < m; i++) { // init array
		// if (keysB[i] != -1) {
		// tempKeySet[j++] = keysB[i]; // copy to set
		// keysA[i] = -1; // erase
		// }
		// }
		//
		// // Copy second table
		// tmpB = new long[(int) m];
		// for (int i = 0; i < m; i++) { // init array
		// tmpB[i] = keysB[i]; // copinserting 3274633494 y
		// keysB[i] = -1; // since no null for primitives in Java...
		// }
		// }
		//
		// // Reinsert last ejected key
		// insert(k, nRehashes);
		// long[][] copy = deepCopyHashTable(); // a spare HT to revert to if needed
		// Check tables and if keys are no in correct places the
		// reinsert
		for (int tableNumber = 0; tableNumber < 2; tableNumber++) {
			for (int i = 0; i < m / 2; i++) { // init array
				// long k = tempKeySet[i]; // get key to reinsert
				// if (k != -1)
				long k = table[tableNumber][i];
				// System.out.println("looking for " + k);
				// if (!find(k)) {
				if (k != -1 && !find(k)) {
					table[tableNumber][i] = -1;
					// swaps = 0;
					// System.out.println("didnt find " + k + " swaps " + swaps);
					put(k);
				}
			}
		}
		// insert(keyToReinsert, 0);
	}

	// for (int i = 0; i < m / 2; i++) { // init array
	// // long} k = tempKeySet[i]; // get key to reinsert
	// // if (k != -1)
	// k = keysB[i];
	// if (!find(k)) {inserting 3274633494
	// keysB[i] = -1;
	// insert(k, nRehashes);
	//
	// }
	// }
	//
	// // Reinsert} last ejected key
	// insert(k, nRehashes);
	//
	// }
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
	public boolean find(long k) {
		// long i = hashFuncA.hash(k); // search in first pos
		// System.out.printf("searching for %d at %d\n", k, i);
		//
		// if (keysA[(int) i] != k) {
		// i = hashFuncB.hash(k); // search in second pos
		// System.out.printf("searching for %d at %d\n", k, i);
		//
		// if (keysB[(int) i] != k)
		// throw new IllegalStateException("item not found");
		// }
		// System.out.printf("found %d at %d\n", k, i);
		//
		// return i;
		// Key already in table(s)
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
		// Unit test
		int w = 32; // 32-bit unsigned
		long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
		int l = 2; // bits, m=2^l
		long m = 2L << l - 1; // size of table, hereg 2^20-1 bits
		// Init random generator (my class not java.util)
		RandGenerator rand = new RandGenerator(0, MAX_U32);
		Hash hashA, hashB; // two hash funcs for table
		long key;
		// Cuckoo with mult shift
		// hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		// hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		hashA = new TabularHash(w, m / 2);
		hashB = new TabularHash(w, m / 2);
		CuckooHT3 ht = new CuckooHT3(m, hashA, hashB);
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
