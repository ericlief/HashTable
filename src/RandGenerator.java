import java.util.Random;

public class RandGenerator {

    long a, b;	// start and end of range
    Random rand;

    public RandGenerator(long a, long b) {
	this.a = a;
	this.b = b;
	rand = new Random();
	if (b < a)
	    throw new IllegalArgumentException("origin a must be less than b");

    }

    public long nextLong() {
	return (long) (a + (b - a + 1) * rand.nextDouble());
    }

    public int nextInt() {
	return (int) (a + (b - a + 1) * rand.nextDouble());
    }

    public double nextDouble() {
	return a + (b - a + 1) * rand.nextDouble();
    }

    public static void main(String[] args) {
	long a, b;
	final long MAX_U32 = Long.decode("0xffffffff"); 	// 2^32-1
	a = 0;
	b = MAX_U32;
	RandGenerator longs = new RandGenerator(a, b);
	for (long i = a; i < b; i = i * i) {
	    //System.out.println(longs.nextDouble());
	    System.out.println(longs.nextLong());
	    //System.out.println(longs.nextInt());
	}

    }
}
