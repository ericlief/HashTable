
public class ModuloHash extends Hash {

    private int bits;	// bits for hash value, e.g. 32
    private long m; 	// size of table

    public ModuloHash(int bits, long m) {
	super(bits);
	this.bits = bits;
	this.m = m;
    }

    @Override
    /**
     * Perform modulo hash.
     * 
     * @param k
     *            key
     * @return
     */
    public long hash(long k) {
	//	System.out.printf("mod %d mod %d=%d\n", k, m, k % m);
	return k % m;
    }

    @Override
    public Hash rehash() {
	return new ModuloHash(bits, m);
    }
}
