public class TabularHash extends Hash {
	private long[][] table; // table for tabular hashing
	private int bits; // bits of hash range
	private int bitsPerChunk; // decimal col size of table (represents 2^l distinct values for each substring)
	private int nChunks; // number of splits for substrings
	private int nVals; // e.g. 2^8 possible values for table entry
	private long m; // size of hash table to generate key for

	public TabularHash(int bits, long m) {
		super(bits);
		this.bits = bits;
		this.bitsPerChunk = 8;
		this.nChunks = 4;
		this.nVals = 2 << bitsPerChunk - 1;
		this.m = m;
		// Construct table
		buildTable(nChunks, nVals, m);
	}

	/**
	 * Build table if not built and generate hash value for a key by peforming the
	 * XOR operation on the splits (chunks) for a key.
	 * 
	 * @param k
	 *            key
	 * @param m
	 *            size of ht
	 * @return hash value of hash function h(x)
	 */
	@Override
	public long hash(long k) {
		// Use mask to split key into 4 chunks
		long mask = 0xFF; // for 8 bit chunks
		long k0 = k & mask;
		long k1 = (k >> 8) & mask;
		long k2 = (k >> 16) & mask;
		long k3 = (k >> 24) & mask;
		return table[0][(int) k0] ^ table[1][(int) k1] ^ table[2][(int) k2] ^ table[3][(int) k3];
	}

	/**
	 * Build a table and fill with random numbers of size 2^k bits = m.
	 * 
	 * @param nSplits
	 *            row size
	 * @param nVals
	 *            col size
	 * @param m
	 *            size of hash table
	 */
	public void buildTable(int nSplits, int nVals, long m) {
		table = new long[nSplits][nVals];
		RandGenerator rand = new RandGenerator(0, m - 1); // keys
		// Initialize table with random w-bit values
		for (int i = 0; i < nSplits; i++) {
			for (int j = 0; j < nVals; j++) {
				table[i][j] = rand.nextLong();
			}
		}
	}

	/**
	 * The key is first split into the desired chunks. This is done efficiently
	 * using bit masking with the long
	 * 
	 * 
	 * @param k
	 * @param nChunks
	 * @return
	 */
	private long[] split(long k) {
		// Uncomment for a different mask than that below
		// long mask = 0L;
		// for (int i = 0, j = 1; i < bitsPerChunk; i++, j *= 2)
		// mask |= j;
		long mask = 0xFF;
		long[] chunks = new long[nChunks];
		for (int i = 0; i < nChunks; i++) {
			long x = k & mask;
			chunks[i] = x;
			k >>= bitsPerChunk;
		}
		return chunks;
	}

	@Override
	public Hash rehash() {
		return new TabularHash(bits, m);
	}

	public long[][] table() {
		return table;
	}

	/**
	 * For tests
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Hash hash = new TabularHash(32, 2 << 20 - 1);
		System.out.println(hash.hash(33));
		System.out.println(hash.hash(0b11110000));
		System.out.println(hash.hash(0b11110000111100001111000011111111));
		hash = hash.rehash();
		System.out.println(hash.hash(33));
		System.out.println(hash.hash(0b11110000));
		System.out.println(hash.hash(0b11110000111100001111000011111111));
	}
}
