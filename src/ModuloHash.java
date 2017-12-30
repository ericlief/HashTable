
public class ModuloHash extends Hash {

    public ModuloHash(int bits) {
	super(bits);
    }

    @Override
    /**
     * Perform modulo hash
     */
    public long hash(long k, long m) {
	System.out.printf("mod %d mod %d=%d\n", k, m, k % m);
	return k % m;
    }
}
