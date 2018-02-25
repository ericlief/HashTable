
/**
 * Multiply and shift hash function
 * 
 * @author liefe
 *
 */
public class MultShiftHash extends Hash {

	private long a; // odd multiplicative constant
	private long bits;
	private int l;

	public MultShiftHash(long bits, int l) {
		super(bits);
		this.bits = bits;
		this.l = l;

		// This is multiplicative constant
		RandGenerator rand = new RandGenerator(0, u); // init random generator (my class not java.util))
		a = rand.nextLong(); // rand 32-bit unsigned long
		a += (a % 2 == 0 ? 1 : 0); // ensure that a is odd
	}

	@Override
	public long hash(long x) {

		// by shifting bits (see super method)
		return ((a * x) & u) >> (w - l);

	}

	@Override
	public Hash rehash() {
		return new MultShiftHash(bits, l);

	}

	public static void main(String[] args) {
		Hash msh = new MultShiftHash(32, 20);
		long m = 2L << 20 - 1; // size of table, here 2^20-1 bits
		System.out.println(m);
		m = (long) Math.pow(2, 20);
		System.out.println(m);
		System.out.println(msh.log2(32));
		// System.out.println(msh.a);
		System.out.println(msh.u);
		System.out.println(msh.hash(2332));
		System.out.println(msh.hash(22));
		msh = msh.rehash();
		System.out.println(msh.hash(2332));
		System.out.println(msh.hash(22));
		msh = msh.rehash();
		System.out.println(msh.hash(2332));
		System.out.println(msh.hash(22));

	}
}
