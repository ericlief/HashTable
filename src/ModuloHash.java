
public class ModuloHash extends Hash {

    private int bits;

    public ModuloHash(int bits) {
	super(bits);
	this.bits = bits;
    }

    @Override
    /**
     * Perform modulo hash
     */
    public long hash(long k, long m) {
	//	System.out.printf("mod %d mod %d=%d\n", k, m, k % m);
	return k % m;
    }

    @Override
    public Hash rehash() {
	return new ModuloHash(bits);
    }
}
