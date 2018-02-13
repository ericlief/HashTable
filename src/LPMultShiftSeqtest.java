import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LPMultShiftSeqtest {

    public static void main(String[] args) {

	String fout = "lp-multshift-seq.csv";
	Path pathOut = Paths.get(System.getProperty("user.home")).resolve("code/ds/HashTable/output/" + fout);
	Hash hash;
	long key;
	int w = 32;	// 32-bit unsigned
	long m = 2L << 20 - 1; 	// size of table, here 2^20-1 bits
	int l = 20;	// log2 of size of table
	long MAX_U32 = (2L << 32 - 1) - 1; 	// universe, here 2^32-1 bits

	// Run tests for every size of m, 2^l
	for (l = 0; l <= 20; l++) {
	    m = 2L << l - 1; 	// size of table, here 2^l-1 bits

	    try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.APPEND,
		    StandardOpenOption.CREATE)) {

		for (int run = 0; run < 1000; run++) {	// 1000 runs for each 

		    // Multshift
		    hash = new MultShiftHash(w, l);	// init hash function with w-bit output
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
