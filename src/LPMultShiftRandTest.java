import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LPMultShiftRandTest {
	public static void main(String[] args) {
		String fout = "lp-multshift-randx.csv";
		Path pathOut = Paths.get(System.getProperty("user.home")).resolve("code/ds/HashTable/output/" + fout);
		Hash hash;
		long key;
		int w = 32; // 32-bit unsigned
		int l = 20; // log2 of size of table
		long m = 2L << l - 1; // size of table, here 2^20-1 bits
		long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
		// RANDOM TEST
		// Init random generator (my class not java.util)
		RandGenerator rand = new RandGenerator(0, MAX_U32);
		// Tabular
		hash = new MultShiftHash(w, l); // init hash function with w-bit output
		LinearProbeHT ht = new LinearProbeHT(m, hash);
		int j = 0;
		long totalSteps = 0;
		while (j < m) {
			try (BufferedWriter out = Files.newBufferedWriter(pathOut, StandardOpenOption.APPEND,
					StandardOpenOption.CREATE)) {
				key = rand.nextLong(); // generate random element
				long startTime = System.nanoTime();
				boolean idx = ht.insert(key);
				long timePerInsert = System.nanoTime() - startTime;
				long stepsPerInsert = ht.steps(); // total steps for the run
				totalSteps += stepsPerInsert;
				double a = (double) j / (double) m; // load
				// write
				out.write(a + "," + totalSteps + "," + timePerInsert + "\n");
				System.out.println(a + "," + totalSteps + "," + timePerInsert + "\n");
				ht.resetSteps();
				j++; // next insert segment in sequence
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Alernate test
			// final int MAX_RUNS = 100;
			// Double[] alphas = { .1, .15, .2, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7,
			// .75, .8, .85, .9, .95 };
			// for (Double a : alphas) {
			// try (BufferedWriter out = Files.newBufferedWriter(pathOut,
			// StandardOpenOption.APPEND,
			// StandardOpenOption.CREATE)) {
			// double meanStepsPerInsert = 0;
			// double meanTimePerInsert = 0;
			// int failedRehashes = 0;
			// // For each a, do 10 runs
			// for (int run = 0; run < MAX_RUNS; run++) {
			// hash = new MultShiftHash(w, l); // init hash function with w-bit output
			// LinearProbeHT ht = new LinearProbeHT(m, hash);
			// // Fill each table to desired load a
			// long elemPerLoad = (long) Math.ceil(a * m);
			// System.out.println("filling to load " + a + " with " + elemPerLoad);
			// for (int j = 0; j < elemPerLoad; j++) {
			// key = rand.nextLong(); // generate random element
			// ht.insert(key);
			// }
			// // Clear steps class field
			// ht.resetSteps();
			// // Now insert sequence
			// long startTime = System.nanoTime();
			// long k = 4096; // number of segments to insert
			// // System.out.println("out loop");
			// int j;
			// for (j = 0; j < k && ht.n < m; j++) {
			// // System.out.println("in loop");
			// key = rand.nextLong(); // generate random element
			// boolean idx = ht.insert(key);
			// }
			// long totalTime = System.nanoTime() - startTime;
			// long steps = ht.steps(); // total steps for the run
			// // double aveTime = (double) endTime / (double) runs;
			// meanStepsPerInsert += (double) steps / (double) j; // number of pairs
			// meanTimePerInsert += (double) totalTime / (double) j;
			// }
			// // Get averages over all runs
			// meanStepsPerInsert /= MAX_RUNS;
			// meanTimePerInsert /= MAX_RUNS;
			// // Uncomment to write
			// out.write(a + "," + meanStepsPerInsert + "," + meanTimePerInsert + "\n");
			// System.out.println("alpha " + a + " steps " + meanStepsPerInsert + " time " +
			// meanTimePerInsert);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
	}
}
