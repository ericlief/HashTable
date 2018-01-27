
/**
 * Multiply and shift hash function
 * 
 * @author liefe
 *
 */
public class MultShiftHash extends Hash {

    //    private static long[][] table;	// for tabular hashing
    //private static final long MAX_U32 = Long.decode("0xffffffff"); 	// 2^32-1
    //private long u;	// decimal size of bits for range of h(x), 2^u
    // Init random generator (my class not java.util)

    private long a;	// odd multiplicative constant
    private long bits;

    public MultShiftHash(long bits) {
	super(bits);
	this.bits = bits;
	//	System.out.println(u);
	RandGenerator rand = new RandGenerator(0, u); // init random generator (my class not java.util)
	a = rand.nextLong();
	a += (a % 2 == 0 ? 1 : 0);	// ensure that a is odd
    }

    //    /**
    //     * Set the bit-size for the range of h(x)
    //     * 
    //     * @param bits
    //     */
    //    public void setBits(int bits) {
    //	u = (long) Math.pow(2, bits) - 1;
    //    }

    @Override
    public long hash(long x, long m) {
	//	int l = (int) (Math.log(m) / Math.log(2));
	long l = log2(m);	// get bit equivalent of m (size of table)
	// by shifting bits (see super method)
	return ((a * x) & u) >> (w - l);
    }

    @Override
    public Hash rehash() {
	return new MultShiftHash(bits);

    }

    public static void main(String[] args) {
	MultShiftHash msh = new MultShiftHash(32);
	System.out.println(msh.log2(32));
	System.out.println(msh.a);
	System.out.println(msh.u);
	System.out.println(msh.hash(2332, 32));

    }
}
