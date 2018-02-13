import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

// import HashTable.RandGenerator;
// import java.util.function.LongSupplier;
// import java.util.stream.LongStream;

public class TestLP {

    public static void main(String[] args) {

	String fout = "lp-mult-seq.csv";
	Path pathOut = Paths.get(System.getProperty("user.home")).resolve("code/ds/HashTable/output/" + fout);
	Hash hash;
	long key;

	// Test all LP hash table variants (my class not java.util)
	int w = 32;	// 32-bit unsigned
	//	long MAX_U32 = (2L << 32 - 1) - 1; 	// universe, here 2^32-1 bits = 1048576;
	long m = 2L << 20 - 1; 	// size of table, here 2^20-1 bits
	int l = 20;	// log2 of size of table
	//	// RANDOM TEST
	//	// Init random generator (my class not java.util)
	//	RandGenerator rand = new RandGenerator(0, MAX_U32);
	//
	//	//	hash = new MultShiftHash(w);	// init hash function with w-bit output
	//	//	hash = new ModuloHash(w);	// init hash class with w-bit output
	//
	//	// Tabular
	//	int bitsPerSubstring = 8;	// size of chunks, r-bits
	//	int nSplits = 4;		// number of splits, c
	//	hash = new TabularHash(w, bitsPerSubstring, nSplits);
	//	final int MAX_RUNS = 100;
	//	Double[] alphas = { .1, .15, .2, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7, .75, .8, .85, .9, .95 };
	//	for (Double a : alphas) {
	//	    try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.APPEND,
	//		    StandardOpenOption.CREATE)) {
	//
	//		double meanStepsPerInsert = 0;
	//		double meanTimePerInsert = 0;
	//		int failedRehashes = 0;
	//		// For each a, do 10 runs 
	//		for (int run = 0; run < 10; run++) {
	//		    LinearProbeHT ht = new LinearProbeHT(m, hash);
	//
	//		    // Fill each table to desired load a
	//		    long elemPerLoad = (long) Math.ceil(a * m);
	//		    System.out.println("filling to load " + a + " with " + elemPerLoad);
	//		    for (int j = 0; j < elemPerLoad; j++) {
	//			key = rand.nextLong();	// generate random element
	//			ht.insert(key);
	//		    }
	//
	//		    // Clear steps class field
	//		    ht.resetSteps();
	//
	//		    // Now insert sequence
	//		    long startTime = System.nanoTime();
	//		    long k = 4096;		// number of segments to insert
	//		    //		    System.out.println("out loop");
	//		    int j;
	//		    for (j = 0; j < k && ht.n < m; j++) {
	//			//			System.out.println("in loop");
	//			key = rand.nextLong();	// generate random element
	//			long idx = ht.insert(key);
	//		    }
	//		    long totalTime = System.nanoTime() - startTime;
	//		    long steps = ht.steps(); // total steps for the run
	//
	//		    //		    double aveTime = (double) endTime / (double) runs;
	//		    meanStepsPerInsert += (double) steps / (double) j; // number of pairs 
	//		    meanTimePerInsert += (double) totalTime / (double) j;
	//		}
	//		// Get averages over all runs
	//		meanStepsPerInsert /= MAX_RUNS;
	//		meanTimePerInsert /= MAX_RUNS;
	//
	//		// Uncomment to write 
	//		out.write(a + "," + meanStepsPerInsert + "," + meanTimePerInsert + "\n");
	//		System.out.println("alpha " + a + " steps " + meanStepsPerInsert + " time " + meanTimePerInsert);
	//	    } catch (IOException e) {
	//		e.printStackTrace();
	//	    }
	//	}

	// SEQUENTIAL TEST
	//		hash = new MultShiftHash(w);	// init hash function with w-bit output
	//	 hash = new ModuloHash(w);	// init hash class with w-bit output
	//	
	//		//	 Tabular
	//		int bitsPerSubstring = 8;	// size of chunks, r-bits
	//		int nSplits = 4;		// number of splits, c
	//		hash = new TabularHash(w, bitsPerSubstring, nSplits);
	// Run tests for every size of m, 2^l
	for (l = 0; l <= 20; l++) {
	    m = 2L << l - 1; 	// size of table, here 2^l-1 bits

	    try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.APPEND,
		    StandardOpenOption.CREATE)) {

		for (int run = 0; run < 1000; run++) {	// 1000 runs for each 
		    // Multshift
		    hash = new MultShiftHash(w, l);	// init hash function with w-bit output

		    //		    //	 Tabular
		    //		    int bitsPerSubstring = 8;	// size of chunks, r-bits
		    //		    int nSplits = 4;		// number of splits, c
		    //		    hash = new TabularHash(w, bitsPerSubstring, nSplits);

		    LinearProbeHT ht = new LinearProbeHT(m, hash);

		    // Fill each table to a = .89 
		    double a = .89;
		    long elemPerLoad = (long) Math.ceil(a * m);
		    System.out.println("filling to load " + a + " with " + elemPerLoad);
		    long i;	// sequential keys
		    for (i = 1; i < elemPerLoad; i++) {
			// insert seq keys
			key = i;
			ht.insert(key);
		    }
		    ht.resetSteps();

		    // Insert from .89 to .91 and measure mean
		    long j;	// keys 
		    a = (double) ht.n / (double) m;	// check load
		    System.out.println("load after inserting to .89=" + a);
		    for (j = 1; a <= .91; a = (double) ht.n / (double) m, j++) {
			key = j + i - 1; // continue sequence
			ht.insert(key);
		    }
		    long steps = ht.steps(); // total steps for the run
		    double meanStepsPerInsert = (double) steps / (double) j;

		    // Uncomment to write 
		    out.write(l + "," + run + "," + meanStepsPerInsert + "\n");
		    System.out.println("l " + l + " run " + run + " steps " + meanStepsPerInsert);
		    System.out.println("n steps " + (double) steps);

		}

	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

}
