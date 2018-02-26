import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CuckooMultShiftRandTest {
	public static void main(String[] args) {
		String fout = "cuckoo-multshift-randx.csv";
		Path pathOut = Paths.get(System.getProperty("user.home")).resolve("code/ds/HashTable/output/" + fout);
		int w = 32; // 32-bit unsigned
		long MAX_U32 = (2L << 32 - 1) - 1; // universe, here 2^32-1 bits
		int l = 20; // bits, m=2^l
		long m = 2L << l - 1; // size of table, here 2^20-1 bits
		// Init random generator (my class not java.util)
		RandGenerator rand = new RandGenerator(0, MAX_U32);
		Hash hashA, hashB; // two hash funcs for table
		long key;
		final int MAX_REHASHES = l / 2; // 10%
		final int MAX_RUNS = 100;
		// long nRehashes = 0;
		// Double[] alphas = { .1, .15, .2, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7,
		// .75, .8, .85, .9, .95 };
		// Double[] alphas = { .1, .15, .2, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7,
		// .75, .8, .85, .9, .95 };
		// Double[] alphas = { .5 };
		// for (Double a : alphas) {
		// Too many failures
		int j = 0;
		long totalSteps = 0;
		hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit output
		CuckooHT3 ht = new CuckooHT3(m, hashA, hashB);
		while (j < m && ht.getNRehashes() < MAX_REHASHES) {
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
			//
			// Double[] alphas = { .5 };
			// for (Double a : alphas) {
			// // Too many failures
			// if (failedRehashes > MAX_FAILED_REHASHES)
			// break;
			// try (BufferedWriter out = Files.newBufferedWriter(pathOut,
			// StandardOpenOption.APPEND,
			// StandardOpenOption.CREATE)) {
			// double meanStepsPerInsert = 0;
			// double meanTimePerInsert = 0;
			// for (int run = 0; run < MAX_RUNS; run++) { // ten runs for each
			// // Cuckoo with mult shift
			// hashA = new MultShiftHash(w, l - 1); // init hash function with w-bit output
			// hashB = new MultShiftHash(w, l - 1); // init hash function with w-bit output
			// CuckooHT3 ht = new CuckooHT3(m, hashA, hashB);
			// // Fill each table to desired load a
			// long elemPerLoad = (long) Math.ceil(a * m);
			// System.out.println("filling to load " + a + " with " + elemPerLoad);
			// for (int j = 0; j < elemPerLoad; j++) {
			// key = rand.nextLong(); // generate random element
			// boolean idx = ht.insert(key);
			// }
			// ht.resetSteps();
			// // Now insert sequence
			// long startTime = System.nanoTime();
			// long k = 4096; // number of segments to insert
			// int j;
			// for (j = 1; j <= k && ht.n <= m; j++) {
			// key = rand.nextLong(); // generate random element
			// boolean idx = ht.insert(key);
			// if (!idx)
			// failedRehashes++;
			// }
			// long totalTime = System.nanoTime() - startTime;
			// long steps = ht.steps(); // total steps for the run
			// meanStepsPerInsert += (double) steps / (double) j;
			// meanTimePerInsert += (double) totalTime / (double) j;
			// }
			// // // Too many failures
			// // if (failedRehashes > MAX_FAILED_REHASHES)
			// // break;
			// // Get averages over all runs
			// meanStepsPerInsert /= MAX_RUNS;
			// meanTimePerInsert /= MAX_RUNS;
			// // Uncomment to write
			// out.write(a + "," + meanStepsPerInsert + "," + meanTimePerInsert + "\n");
			// System.out.println("alpha " + a + " steps " + meanStepsPerInsert + " time " +
			// meanTimePerInsert);
			// } catch (IOException e) {
			// e.printStackTrace();
			//
		}
	}
}
