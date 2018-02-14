import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LPTabSeqTest {

    public static void main(String[] args) {

	String fout = "lp-tabular-seq.csv";
	Path pathOut = Paths.get(System.getProperty("user.home")).resolve("code/ds/HashTable/output/" + fout);
	Hash hash;
	long key;
	int w = 32;	// 32-bit unsigned
	long m = 2L << 20 - 1; 	// size of table, here 2^20-1 bits
	int l = 20;	// log2 of size of table
	long MAX_U32 = (2L << 32 - 1) - 1; 	// universe, here 2^32-1 bits

	try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.WRITE,
		StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {

	    // Run tests for every size of m, 2^l
	    for (l = 0; l <= 20; l++) {
		if (l == 0)
		    m = 1;
		else
		    m = 2L << l - 1; 	// size of table, here 2^l-1 bits

		double meanStepsPerInsert = 0;
		int MAX_RUNS = 1000;
		for (int run = 0; run < MAX_RUNS; run++) {	// 1000 runs for each 

		    //	 Tabular
		    hash = new TabularHash(w, m);
		    LinearProbeHT ht = new LinearProbeHT(m, hash);

		    // Fill each table to a = .89 
		    double a = .89;
		    long elemPerLoad = (long) Math.ceil(a * m);
		    //		    System.out.println("filling to load " + a + " with " + elemPerLoad);
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
		    //		    System.out.println("load after inserting to .89=" + a);
		    for (j = 1; a <= .91; a = (double) ht.n / (double) m, j++) {
			key = j + i - 1; // continue sequence
			ht.insert(key);
		    }
		    long steps = ht.steps(); // total steps for the run
		    //		    meanStepsPerInsert += (double) steps / (double) j;
		    meanStepsPerInsert = (double) steps / (double) j;
		    out.write(l + "," + run + "," + meanStepsPerInsert + "\n");

		}
		//		// Get averages over all runs
		//		meanStepsPerInsert /= MAX_RUNS;
		//		// Uncomment to write 
		//		out.write(l + "," + meanStepsPerInsert + "\n");
		//		System.out.println("l " + l + " steps " + meanStepsPerInsert);
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
