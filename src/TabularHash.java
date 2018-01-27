
public class TabularHash extends Hash {
    private long[][] table;	// table for tabular hashing
    private int bits;
    private int bitsPerChunk; 	// decimal col size of table (represents 2^l distinct values for each substring)
    private int nChunks;		// number of splits for substrings

    public TabularHash(int bits, int bitsPerChunk, int nChunks) {
	super(bits);
	this.bits = bits;
	//	this.maxVal = Long.decode("0xffffffff"); 	// universe, here 2^32-1 bits

	this.bitsPerChunk = bitsPerChunk;
	this.nChunks = nChunks;
    }

    /**
     * Build table if not built and generate hash value for a key.
     * 
     * @param k
     *            key
     * @param m
     *            size of ht
     * @return hash value of hash function h(x)
     */
    @Override
    public long hash(long k, long m) {
	//	System.out.println(table == null);

	//	if (k < 8)
	//	    return k;

	//	int nVals = (int) Math.pow(2, bitsPerChunk);
	int nVals = 2 << bitsPerChunk - 1;
	if (table == null || (table.length != nChunks && table[0].length != bitsPerChunk))
	    buildTable(nChunks, nVals, m);

	//	String s = Long.toBinaryString(k);
	//	//	System.out.println(s);
	//	String[] substrings = split(s, 4);
	//
	//	//	for (String str : substrings)
	//	//	    System.out.println(str);
	//

	long[] chunks = split(k, nChunks);

	// Now perform an XOR on all substrings to get the value of h(x)
	//	long hash = 0l;
	//	for (int i = 0; i < substrings.length; i++) {
	//	    int j = Integer.parseInt(substrings[i], 2); // convert binary string to int
	//	    hash ^= table[i][j];	// retrieve hash value from table and XOR it
	//	    //	    System.out.println("string " + substrings[i]);
	//	    //	    System.out.println("rand val " + Long.toString(table[i][j], 2));
	//	    //	    System.out.println("hash=" + Long.toString(hash, 2));
	//	}

	long hash = 0l;
	for (long i = 0; i < nChunks; i++) {
	    //	    int j = Integer.parseInt(substrings[i], 2); // convert binary string to int
	    long j = chunks[(int) i]; // value of chunk
	    hash ^= table[(int) i][(int) j];	// retrieve hash value from table and XOR it
	    //	    System.out.println("string " + substrings[i]);
	    //	    System.out.println("rand val " + Long.toString(table[i][j], 2));
	    //	    System.out.println("hash=" + Long.toString(hash, 2));
	}
	return hash;

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
	//	System.out.printf("building table %dx%d\n", nSplits, nVals);
	table = new long[nSplits][nVals];
	RandGenerator rand = new RandGenerator(0, m - 1);

	// Initialize table with random w-bit values
	for (int i = 0; i < nSplits; i++) {
	    for (int j = 0; j < nVals; j++) {
		table[i][j] = rand.nextLong();
		//		System.out.println(table[i][j]);
	    }
	}
    }

    private long[] split(long k, int nChunks) {
	//	long mask = 0L & (2 << bitsPerChunk);
	long mask = 0L;
	for (int i = 0, j = 1; i < bitsPerChunk; i++, j *= 2)
	    mask |= j;
	//	System.out.println("mask=" + mask);

	long[] chunks = new long[nChunks];
	for (int i = 0; i < nChunks; i++) {
	    long x = k & mask;
	    chunks[i] = x;
	    k >>= bitsPerChunk;
	    //	    System.out.println(chunks[i]);
	}
	return chunks;
    }

    //    private static String[] split(String s, int nChunks) {
    //	//	System.out.println("string " + s);
    //
    //	int n = s.length();
    //	int szChunk = n / nChunks;
    //	int r = n % nChunks;
    //	String[] strings = new String[nChunks];
    //	int i = 0;
    //	int k = 0;// number of string/chunk
    //	for (int j = szChunk; j <= n; j += szChunk) {
    //	    //	    if (j % (sz + (r + 1) / nChunks) == 0) {
    //	    if (r > 0) {
    //		j++;
    //		r--;
    //	    }
    //	    //
    //	    //	    System.out.println("chunk size=" + (j - i) + " at " + j);
    //	    //
    //	    String substr = s.substring(i, j);
    //	    //	    System.out.println("adding substr " + substr);
    //	    strings[k] = substr;
    //	    i = j;
    //	    k++;
    //	    //	    r--;
    //	}
    //	//	for (int j = 1; j < n + 1; j++) {
    //	//	    if (j % (sz + (r + 1) / nChunks) == 0) {
    //	//		//		if (r > 0) {
    //	//		//		    j++;
    //	//		//		    r--;
    //	//		//		}
    //	//
    //	//		System.out.println("chunk size=" + (j - i) + " at " + j);
    //	//
    //	//		String substr = s.substring(i, j);
    //	//		System.out.println("adding substr " + substr);
    //	//		strings[k] = substr;
    //	//		i = j;
    //	//		k++;
    //	//		r--;
    //	//	    }
    //
    //	return strings;
    //    }

    @Override
    public Hash rehash() {
	return new TabularHash(bits, bitsPerChunk, nChunks);

    }

    public long[][] table() {
	return table;
    }

    public static void main(String[] args) {
	TabularHash hash = new TabularHash(32, 8, 4);
	System.out.println(hash.hash(0b10101010, 512));
	System.out.println(hash.hash(0b11110000, 256));
	System.out.println(hash.hash(0b11110000111100001111000011111111, 256));

    }

}
