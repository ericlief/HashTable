import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

// import HashTable.RandGenerator;
// import java.util.function.LongSupplier;
// import java.util.stream.LongStream;

abstract class Hash {

    //private static final long MAX_U32 = Long.decode("0xffffffff"); 	// 2^32-1
    long u;	// decimal size of bits for range of h(x), 2^u
    long w;	// size in bits for range 

    Hash(int bits) {
	setBits(bits);
    }

    /**
     * Set the bit-size for the range of h(x)
     * 
     * @param bits
     */
    void setBits(int bits) {
	w = bits;
	u = (long) Math.pow(2, bits) - 1;
    }

    abstract long hash(long k, long m);

    abstract Hash rehash();

    public static void main(String[] args) {

	int runs = 0; 			// iterations to average over
	String fout = "lp-multshift.csv";
	Path pathOut = Paths.get(System.getProperty("user.home"))
		.resolve("Dropbox/DataStructs/HashTable/output/" + fout);

	try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.WRITE,
		StandardOpenOption.CREATE)) {
	    int w = 32;	// 32-bit unsigned
	    long MAX_U32 = Long.decode("0xffffffff"); 	// universe, here 2^32-1 bits
	    long m = Long.decode("0xfffff");		// size of ht, here 2^20

	    // Init random generator (my class not java.util)
	    RandGenerator rand = new RandGenerator(0, MAX_U32);

	    HashTable ht;
	    long key;

	    // Test all LP hash table variants (my class not java.util)

	    //	    // Mult shift with LP
	    //	    m = 195;
	    //	    Hash msh = new MultShiftHash(w);	// init hash function with w-bit output
	    //	    ht = new LinearProbeHT(m, msh);
	    //	    for (int i = 0; i < m; i++) {
	    //		key = rand.nextLong();	// generate random element
	    //		ht.insert(key);
	    //		System.out.println("found " + ht.find(key));
	    //	    }

	    //	    // Mult shift with Cuckoo
	    //	    m = 8;
	    //	    Hash msh = new MultShiftHash(w);	// init hash function with w-bit output
	    //	    int bitsPerSubstring = 8;	// size of chunks, r-bits
	    //	    int nSplits = 4;		// number of splits, c
	    //	    Hash tab = new TabularHash(w, bitsPerSubstring, nSplits);
	    //	    ht = new CuckooHT(m, msh, tab);
	    //	    for (int i = 0; i < 2 * m; i++) {
	    //		key = rand.nextLong();	// generate random element
	    //		ht.insert(key);
	    //		System.out.println("found " + ht.find(key));
	    //	    }

	    //	    // Tabular hash function
	    //	    m = 64;
	    //	    int bitsPerSubstring = 8;	// size of chunks, r-bits
	    //	    int nSplits = 4;		// number of splits, c
	    //	    Hash tab = new TabularHash(w, bitsPerSubstring, nSplits);
	    //	    ht = new LinearProbeHT(m, tab);
	    //	    //	    idx = tab.hash(key, m);	// get hash code from table
	    //	    for (int i = 0; i < m; i++) {
	    //		key = rand.nextLong();	// generate random element
	    //		ht.insert(key);
	    //		System.out.println("found " + ht.find(key));
	    //	    }

	    //	    // Simple mod hash
	    //	    m = 20;
	    //	    Hash mod = new ModuloHash(w);	// init hash class with w-bit output
	    //	    ht = new LinearProbeHT(m, mod);
	    //	    for (int i = 0; i < m; i++) {
	    //		key = rand.nextLong();	// generate random element
	    //		ht.insert(key);
	    //		System.out.println("found " + ht.find(key));
	    //	    }
	    //	    // insert key
	    //	    //	    ht = new LinearProbeHT(m);
	    //	    ht.insert(key, idx);

	    //	    for (long l = 2; l < w; l *= 2) {
	    //		//		    long a = stream.
	    //		long a = rand.nextLong();
	    //		a += (a % 2 == 0 ? 1 : 0);	// ensure that a is odd
	    //		System.out.printf("x=%d, a=%d, l=%d, hash=%d\n", x, a, l, multShift(x, a, w, l));
	    //
	    //	    }

	    // Insert m elements into T filling until load factor a = 1

	    //	    long a = rand.nextLong();	// odd multiplicative constant
	    //	    a += (a % 2 == 0 ? 1 : 0);	// ensure that a is odd
	    //	    long l = 20;		// fixed size of table T = 2^l = 2^20

	    Double[] alphas = { .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0 };
	    for (Double a : alphas) {
		for (int i = 0; i < 10; i++) {	// ten runs for each 
		    Hash msh = new MultShiftHash(w);	// init hash function with w-bit output
		    ht = new LinearProbeHT(m, msh);

		    // Fill each table to desired load a
		    long elemPerLoad = (long) Math.ceil(a * m);
		    System.out.println("filling to load " + a + " with " + elemPerLoad);
		    for (int j = 0; j < elemPerLoad; j++) {
			key = rand.nextLong();	// generate random element
			ht.insert(key);
		    }
		    // Now insert sequence
		    long startTime = System.nanoTime();
		    long k = 128;		// number of segments to insert
		    for (int j = 0; j < k; j++) {
			key = rand.nextLong();	// generate random element
			ht.insert(key);
		    }
		}
	    }
	    //	    while (k < m) {
	    //		k += 128; //next batch, increment 2^7
	    //		long x = rand.nextLong();
	    //		System.out.println("x=" + x);
	    //		//		long hash = multShift(x, a, w, l);
	    //		//		System.out.printf("x=%d, a=%d, l=%d, hash=%d\n", x, a, l, hash);
	    //
	    //		long time = System.nanoTime() - startTime;
	    //		double aveTime = time / (double) k;
	    //		//		int nInserts += 128; // number of insertion subsequence 
	    //		//		double meanSwapTime = aveTime / nSwaps;
	    //
	    //		//System.out.println(n + " elap time  " + time);
	    //		//System.out.println("ave time " + aveTime);
	    //		//System.out.println("n swaps " +nSwaps);
	    //		//System.out.println("mean swap time " +meanSwapTime);
	    //
	    //		// Uncomment to write 
	    //		//		out.write(n + "," + aveTime + "," + meanSwapTime + "\n");
	    //
	    //	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
//	long w = 64;
//    long w = 32;
//    long MAX_U32 = Long.decode("0xffffffff"); 	// 2^32-1
//    long m = Long.decode("0xfffff");		// 2^20
//    //	System.out.println(m);
//    //	long MAX_U32 = Long.MAX_VALUE;
//
//    //	System.out.println(MAX_U32);
//    RandGenerator rand = new RandGenerator(0, MAX_U32);
//    //	LongStream stream = rand.longs(0, MAX_U32);
//    //LongStream keys = rand.longs(10, 0, MAX_U32);
//
//    // Insert m elements into T filling until load factor a = 1
//    for(
//    long i = 0;i<m;i++)
//    {
//	long x = rand.nextLong();
//	//System.out.println("x=" + x);
//
//	//	    LongStream A = rand.longs(w, 0, MAX_U32);
//	//	    LongStream ls = LongStream.generate((LongSupplier) A);
//	//	    ls.limit(10).forEach(System.out::println);
//
//	//	    LongStream stream = rand.longs(10, 0, MAX_U32);
//	long a = rand.nextLong();
//	long l = 20;
//	a += (a % 2 == 0 ? 1 : 0);	// ensure that a is odd
//	long hash = multShift(x, a, w, l);
//	System.out.printf("x=%d, a=%d, l=%d, hash=%d\n", x, a, l, hash);
//
//	// insert
//
//	ht.insert(x, hash);
//	//	    for (long l = 2; l < w; l *= 2) {
//	//		//		    long a = stream.
//	//		long a = rand.nextLong();
//	//		a += (a % 2 == 0 ? 1 : 0);	// ensure that a is odd
//	//		System.out.printf("x=%d, a=%d, l=%d, hash=%d\n", x, a, l, multShift(x, a, w, l));
//	//
//	//	    }
//    }
