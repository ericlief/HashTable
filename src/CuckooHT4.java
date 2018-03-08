public class CuckooHT4 extends HashTable {

	private long[] keysA; // table 1 for keys
	private long[] keysB; // table 2 for keys
	// private long[] tempKeySet; // set of keys already inserted
	private Hash hashFuncA; // hash function a
	private Hash hashFuncB; // hash function b
	private long swaps; // swaps per insertion attempt
	private long steps; // mean steps
	private int maxRehashes; // to control when to cut off insert
	private int maxSwaps; // to determine when to rehash
	// private int nRehashes; // counter for each insert of a new key

	public CuckooHT4(long m, Hash hashA, Hash hashB) {
		super(m);
		maxRehashes = 20; // l = log2(m)
		// maxSwaps = 2 * maxRehashes; // here 40

		this.hashFuncA = hashA;
		this.hashFuncB = hashB;
		n = 0;

		// Build first table
		keysA = new long[(int) m / 2];
		for (int i = 0; i < m / 2; i++) // init array
			keysA[i] = -1; // since no null for primitives in Java...

		// Build second table / 2
		keysB = new long[(int) m / 2];
		for (int i = 0; i < m / 2; i++) // init array
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
	public boolean insert(long k) {
		// nRehashes = 0; // reset rehashes
		long maxSwaps = log2(n); // max loops, only computed once at time of initial insertion
		long code = insert(k, 0);
		if (code == -1)
			return false;
		n++;
		return true;
	}

	/**
	 * Insert a given key at a given hash index. Useful if one wishes to use a
	 * specific hash function
	 * 
	 * @param k
	 *            key
	 * @return i index
	 */
	private long insert(long k, int nRehashes) {

		// if (nRehashes == 0)
		// n++; // increment size of elements in table for first insert only
		// /long nRehashes = 0;

		// Key already in table(s)
		// if (keysA[(int) hashFuncA.hash(k)] == k || keysB[(int) hashFuncB.hash(k)] ==
		// k)
		if (find(k))
			return -1;

		if (nRehashes > maxRehashes)
			return -1;

		long initKey = k;
		long initInsertPos = hashFuncA.hash(k); // init the initial pos we start with to avoid a cycle
		long i = -1; // pos in tables
		// while (nRehas khes < maxRehashes) {

		// for (int swaps = 0; swaps < maxSwaps; swaps++) {
		swaps = 0;
		while (swaps <= maxSwaps) {

			// if (k == initKey && i == initInsertPos) // a cycle
			// break;

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
			swaps++;
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
			swaps++;
			steps++;
		}

		// Loop limit exceeded, need to rehash and reinsert again
		// if (nRehashes == 0)
		// tempKeySet[0] = k;
		rehash(k, nRehashes);
		// nRehashes++;

		// Failed insert, return
		// n--;
		return -1;

	}

	/**
	 * Rehash both hash functions and rebuild table. This is done when a maximum
	 * limit of steps has been reached. int nRehashes,
	 * 
	 * @param nRehashes
	 * @param k
	 *            this is the last key that needs to be re-inserted
	 */
	public void rehash(long k, int nRehashes) {

		// Keep track of rehashes
		nRehashes++;

		if (nRehashes > maxRehashes) {
			// System.out.println("max rehashes " + nRehashes);
			return;
		}

		// Init new hash functions
		hashFuncA = hashFuncA.rehash();
		hashFuncB = hashFuncB.rehash();

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
		// tmpB[i] = keysB[i]; // copy
		// keysB[i] = -1; // since no null for primitives in Java...
		// }
		// }
		//
		// // Reinsert last ejected key
		// insert(k, nRehashes);

		// Check tables and if keys are no in correct places the
		// reinsert
		for (int i = 0; i < m / 2; i++) { // init array
			// long k = tempKeySet[i]; // get key to reinsert
			// if (k != -1)
			k = keysA[i];
			if (!find(k)) {
				keysA[i] = -1;
				insert(k, nRehashes);
			}

		}
		for (int i = 0; i < m / 2; i++) { // init array
			// long} k = tempKeySet[i]; // get key to reinsert
			// if (k != -1)
			k = keysB[i];
			if (!find(k)) {
				keysB[i] = -1;
				insert(k, nRehashes);

			}
		}

		// Reinsert} last ejected key
		insert(k, nRehashes);

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
		return (keysA[(int) hashFuncA.hash(k)] == k || keysB[(int) hashFuncB.hash(k)] == k);
	}

	public long steps() {
		return steps;
	}

	public void resetSteps() {
		steps = 0;
	}

	// public long[][] deepCopyHashTable() {
	//
	// // Copy first table to temp set
	// for (int tableX = 0; tableX<2; tableX++) {
	// long[] keys = new long[m/2];
	// for (int i = 0; i < m / 2; i++) {
	// if (tables[] != -1) {
	// keysA[i]
	//
	//
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
	// tmpB[i] = keysB[i]; // copy
	// keysB[i] = -1; // since no null for primitives in Java...
	// }
	// }

	public static void main(String[] args) {

		// Unit test
		int w = 32; // 32-bit unsigned
		long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
		long m = 2L << 4 - 1; // size of table, here 2^20-1 bits
		int l = 4; // bits, m=2^l

		// Init random generator (my class not java.util)
		RandGenerator rand = new RandGenerator(0, MAX_U32);
		Hash hashA, hashB; // two hash funcs for table
		long key;

		// Cuckoo with mult shift
		hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		CuckooHT4 ht = new CuckooHT4(m, hashA, hashB);

		System.out.println("inserting");
		for (int j = 0; j < m; j++) {
			key = rand.nextLong(); // generate random element
			System.out.println("inserting " + key);
			boolean idx = ht.insert(key);
			if (!idx)
				System.out.println("failed insert with rehashes");
		}
		System.out.println("printing table a");
		for (int i = 0; i < m / 2; i++)
			System.out.println(ht.keysA[i]);
		System.out.println("printing table b");
		for (int i = 0; i < m / 2; i++)
			System.out.println(ht.keysB[i]);
	}

}

// public class CuckooHT2 extends HashTable {
//
// long[][] table = new long[2][];
// long[][] tableCopy = new long[2][];
// // private long[] keysA; // table 1 for keys
// // private long[] keysB; // table 2 for keys
// // private long[] tempKeySet; // set of keys already inserted
// // private Hash hashFuncA; // hash function a
// // private Hash hashFuncB; // hash function b
// private Hash[] hashFuncs;
// private long swaps; // swaps per insertion attempt
// private long steps; // mean steps
// private int maxRehashes; // to control when to cut off insert
// private int maxSwaps; // to determine when to rehash
// private int nRehashes; // counter for each insert of a new key
//
// public CuckooHT2(long m, Hash hashA, Hash hashB) {
// super(m);
// // maxRehashes = 20; // l = log2(m)
// // maxSwaps = 2 * maxRehashes; // here 40
//
// // this.hashFuncA = hashA;int
// // this.hashFuncB = hashB;
// this.hashFuncs = new Hash[] { hashA, hashB };
// n = 0;
//
// // Build first table
// long[] keysA = new long[(int) m / 2];
// for (int i = 0; i < m / 2; i++) // init array
// keysA[i] = -1; // since no null for primitives in Java...
//
// // Build second table / 2
// long[] keysB = new long[(int) m / 2];
// for (int i = 0; i < m / 2; i++) // init array
// keysB[i] = -1; // since no null for primitives in Java...
//
// this.table[0] = keysA;
// this.table[1] = keysB;
// }
//
// /**
// * Insert a given key at a given hash index. Useful if one wishes to use a
// * specific hash function
// *
// * @param k
// * key
// * @return i index
// */
// @Override
// public boolean insert(long k) {
// System.out.println("trying to insert " + k);
// System.out.println("size of table = " + n + " max = " + maxSwaps);
//
// if (find(k))
// return false;
//
// // nRehashes = 0; // reset rehashes
// maxSwaps = (int) log2(n); // max loops, only computed once at time of initial
// insertion
// maxRehashes = (int) .5 * maxSwaps;
// nRehashes = 0;
// // swaps = 0;
// // boolean hasInserted;
// // long[][] tableCopy = deepCopyHashTable(); // a spare HT to revert to if
// // needed
// // while (!(hasInserted = insert(k, 0))) {
// // if (nRehashes > maxRehashes)
// //// break;
// // rehash(k);
// // // status = insert(k, 0);
// // }
// // steps += swaps;
// // if (!hasInserted) {
// // table = tableCopy; // revert
// // return false;
// // }
// // n++;
// // return true;
// tableCopy = deepCopyHashTable(table); // copy tables once
//
// return put(k);
// }
//
// private boolean put(long k) {
//
// // long[][] oldTable = deepCopyHashTable();
// boolean hasInserted;
// // long[][] tableCopy = deepCopyHashTable(); // a spare HT to revert to if
// // needed
// while (!(hasInserted = put(k, 0))) {
// table = deepCopyHashTable(tableCopy); // revert back to old tables
// if (nRehashes > maxRehashes)
// break;
// rehash();
// // status = insert(k, 0);
// }
// steps += swaps;
// if (!hasInserted) {
// return false;
// }
// n++;
// return true;
// }
//
// /**
// * Insert a given key at a given hash index. Useful if one wishes to use a
// * specific hash function table[tableNumber][i] = -1;
// *
// * @param k
// * key
// * @return i index2
// */
// private boolean put(long k, int whichTable) {
// System.out.println("inserting " + k + " in " + whichTable + " num swaps " +
// swaps);
// print();
// // if (nRehashes == 0)
// // n++; // increment size of elements in table for first insert only
// // /long nRehashes = 0;
//
// // Key already in table(s)
// // if (keysA[(int) hashFuncA.hash(k)] == k || keysB[(int) hashFuncB.hash(k)]
// ==
// // k)
// // if (find(k))
// // return false;
//
// if (swaps > maxSwaps)
// return false;
//
// // long initKey = k;
// // long initInsertPos = hashFuncA.hash(k); // init the initial pos we start
// with
// // to avoid a cycle
// // long i ; // pos in tables
// // while (nRehas khes < maxRehashes) {
//
// // for (int swaps = 0; swaps < maxSwaps; swaps++) {
// // swaps = 0;
// // while (swaps <= maxSwaps) {
//
// // if (k == initKey && i == initInsertPos) // a cycle
// // break;
//
// long i = hashFuncs[whichTable].hash(k); // get new
// boolean hasInserted = true;
//
// if (table[whichTable][(int) i] != -1) {
// swaps++;
// long ejectedKey = table[whichTable][(int) i];
// System.out.println("ejecting " + ejectedKey + " from " + "table " +
// whichTable);
// hasInserted = put(ejectedKey, Math.abs(whichTable - 1)); // get other table
//
// }
// if (hasInserted) {
// // This table pos vacant, so insert this key and we done
// System.out.println("inserted " + k + " in " + "table " + whichTable);
// table[whichTable][(int) i] = k;
// }
//
// return hasInserted;
//
// // // First table full, so insert this key and move other key to second table
// // long otherKey = keysA[(int) i]; // swap keys
// // keysA[(int) i] = k;
// // k = otherKey;(long keyToReinsert
// // swaps++;
// // steps++;
// // i = hashFuncB.hash(k); // index for table 2
// //
// // if (keysB[(int) i] == -1) { // first table full, so insert this key and
// move
// // other key to second table
// // keysB[(int) i] = k; // second table empty, so just insert key and stop
// // return i; // negative pos indicates second table
// // }
// //
// // k = otherKey;// Second table full, so get its current key and in next iter
// // move other key to
// //
// // // second table
// // otherKey = keysB[(int) i];
// // keysB[(int) i] = k; // insert current key and swap
// // k = otherKey;
// // swaps++;
// // steps++;
// }
//
// // Loop limit exceeded, need to rehash and reinsert again
// // if (nRehashes == 0)
// // tempKeySet[0] = k;
// // rehash(k);insert
// // // nRehashes++;
// //
// // // Failed insert, return
// // // n--;
// // return -1;
// //
// // }
//
// /**
// * Rehash both hash functions and rebuild table. This is done when a maximum
// * limit of steps has been reached. int nRehashes,
// *
// * @param nRehashes
// * @param k
// * table[tableNumber][i] = -1; this is the last key that needs to be
// * re-inserted
// */
// public void rehash() {
//
// // Keep track of rehashes
// nRehashes++;
//
// // if (nRehashes > maxRehashes) {
// System.out.println("num rehashes " + nRehashes);
// // return;
// // }
//
// // Init new hash functions
// for (int hash = 0; hash < 2; hash++)
// hashFuncs[hash] = hashFuncs[hash].rehash();
//
// // // Store inserted keys in a set for further rehashes
// // if (nRehashes == 0) {
// //
// // // Copy first table to temp set
// // int j = 1; // to hold place in set (already occupied by at least first
// // expelled key)
// // tempKeySet = new long[2 * (int) m];
// // for (int i = 0; i < m; i++) { // init array
// // if (keysA[i] != -1) {
// // tempKeySet[j++] = keysA[i]; // copy to set
// // keysA[i] = -1; // erase
// // }
// // }
// // for (int i = 0; i < m; i++) { // init array
// // if (keysB[i] != -1) {
// // tempKeySet[j++] = keysB[i]; // copy to set
// // keysA[i] = -1; // erase
// // }
// // }
// //
// // // Copy second table
// // tmpB = new long[(int) m];
// // for (int i = 0; i < m; i++) { // init array
// // tmpB[i] = keysB[i]; // copinserting 3274633494 y
// // keysB[i] = -1; // since no null for primitives in Java...
// // }
// // }
// //
// // // Reinsert last ejected key
// // insert(k, nRehashes);
//
// // long[][] copy = deepCopyHashTable(); // a spare HT to revert to if needed
//
// // Check tables and if keys are no in correct places the
// // reinsert
// for (int tableNumber = 0; tableNumber < 2; tableNumber++) {
//
// for (int i = 0; i < m / 2; i++) { // init array
// // long k = tempKeySet[i]; // get key to reinsert
// // if (k != -1)
// long k = table[tableNumber][i];
// System.out.println("looking for " + k);
// // if (!find(k)) {
// if (k != -1 && !find(k)) {
// table[tableNumber][i] = -1;
// swaps = 0;
// System.out.println("didnt find " + k + " swaps " + swaps);
//
// put(k);
// }
// }
//
// }
// // insert(keyToReinsert, 0);
// }
// // for (int i = 0; i < m / 2; i++) { // init array
// // // long} k = tempKeySet[i]; // get key to reinsert
// // // if (k != -1)
// // k = keysB[i];
// // if (!find(k)) {inserting 3274633494
// // keysB[i] = -1;
// // insert(k, nRehashes);
// //
// // }
// // }
// //
// // // Reinsert} last ejected key
// // insert(k, nRehashes);
// //
// // }
//
// /**
// * Find a given key at a given hash index. Useful if one wishes to use a
// * specific hash function
// *
// * @param k
// * key
// * @param j
// * hashed index or
// * @return i index or -1 if not found
// */
// @Override
// public boolean find(long k) {
//
// // long i = hashFuncA.hash(k); // search in first pos
// // System.out.printf("searching for %d at %d\n", k, i);
// //
// // if (keysA[(int) i] != k) {
// // i = hashFuncB.hash(k); // search in second pos
// // System.out.printf("searching for %d at %d\n", k, i);
// //
// // if (keysB[(int) i] != k)
// // throw new IllegalStateException("item not found");
// // }
// // System.out.printf("found %d at %d\n", k, i);
// //
// // return i;
//
// // Key already in table(s)
// return (table[0][(int) hashFuncs[0].hash(k)] == k || table[1][(int)
// hashFuncs[1].hash(k)] == k);
// }
//
// public long steps() {
// return steps;
// }
//
// public void resetSteps() {
// steps = 0;
// }
//
// public void print() {
// System.out.println("printing table a");
// for (int i = 0; i < m / 2; i++)
// System.out.print(table[0][i] + " ");
// System.out.println();
// System.out.println("printing table b");
// for (int i = 0; i < m / 2; i++)
// System.out.print(table[1][i] + " ");
// System.out.println();
// }
//
// public long[][] deepCopyHashTable(long[][] src) {
//
// long[][] newTable = new long[2][];
// // Copy first table to temp set
// for (int tableX = 0; tableX < 2; tableX++) {
// long[] keys = new long[(int) m / 2];
// for (int i = 0; i < m / 2; i++)
// keys[i] = src[tableX][i];
// newTable[tableX] = keys;
// }
//
// return newTable;
//
// }
//
// public static void main(String[] args) {
//
// // Unit test
// int w = 32; // 32-bit unsigned
// long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
// int l = 8; // bits, m=2^l
// long m = 2L << l - 1; // size of table, here 2^20-1 bits
//
// // Init random generator (my class not java.util)
// RandGenerator rand = new RandGenerator(0, MAX_U32);
// Hash hashA, hashB; // two hash funcs for table
// long key;
//
// // Cuckoo with mult shift
// // hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit
// output
// // hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit
// output
// hashA = new TabularHash(w, m / 2);
// hashB = new TabularHash(w, m / 2);
// CuckooHT2 ht = new CuckooHT2(m, hashA, hashB);
//
// // System.out.println("inserting");
// for (int j = 0; j < m; j++) {
// key = rand.nextLong(); // generate random element
// // System.out.println("inserting " + key);
// boolean idx = ht.insert(key);
// if (!idx)
// System.out.println("failed insert with rehashes for key " + key);
// }
// System.out.println("printing table a");
// for (int i = 0; i < m / 2; i++)
// System.out.println(ht.table[0][i]);
// System.out.println("printing table b");
// for (int i = 0; i < m / 2; i++)
// System.out.println(ht.table[1][i]);
// }
//
// }
