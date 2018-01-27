// import HashTable.RandGenerator;
// import java.util.function.LongSupplier;
// import java.util.stream.LongStream;

/**
 * Base class for implemented hash methods. Holds u, w variables, which
 * represent the size of the universe as a long.
 * 
 * @author liefe
 *
 */
abstract class Hash {

    //private static final long MAX_U32 = Long.decode("0xffffffff"); 	// 2^32-1
    long u;	// max (size) of universe, decimal size of bits for range of h(x), 2^u
    long w;	// size in bits for range 

    Hash(long bits) {
	setBits(bits);
    }

    /**
     * Set the bit-size for the range of h(x)
     * 
     * @param bits
     */
    void setBits(long bits) {
	w = bits;	// number of bits
	//	u = (long) Math.pow(2, bits) - 1;
	u = (2L << w - 1) - 1;	 // decimal value, more efficient way of computing 2^w
    }

    /**
     * Method which outputs range of hash to be implemented by subclasses.
     * 
     * @param k
     *            key to hash
     * @param m
     *            size of table
     * @return value of function
     */
    abstract long hash(long k, long m);

    /**
     * Method to rehash or reset hash to new values.
     * 
     * @return
     */
    abstract Hash rehash();

    /**
     * Helper method to comput log2 using bit shifts
     * 
     * @param x
     * @return log2(x)
     */
    public long log2(long x) {
	int r = 0;
	while (x > 1) {
	    x >>= 1;
	    r++;
	}
	return r;
    }
}