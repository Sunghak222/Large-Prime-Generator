import java.io.FileWriter;
import java.io.IOException;

/*
* I do not use any external libraries or data structures such as bitset or arraylist in this program.
* */
public class LargePrimeGenerator {
    /*
    * PARTITION: set the range of numbers to get primes step by step.
    * ARRAY_SIZE: set the size of arrays.
    * ARBITRARY_INT_THRESHOLD: Instead of using Integer.MAX_VALUE, I used this to be safer from exceptions
    * */
    static final int PARTITION = 1000000;
    static final int ARRAY_SIZE = 100000000;
    static final int ARBITRARY_INT_THRESHOLD = 2000000000; // 2b

    /*
     * intCounter: number of integer primes
     * longCounter: number of long primes
     * smallPrimes: array that stores integer primes
     * */
    static int intCounter = 0;
    static long longCounter = 0;
    static long limit = PARTITION;

    static int[] smallPrimes = new int[ARRAY_SIZE];

    static long currMaxPrime = 0;
    /*
    * get first PARTITION primes up to 1 million
    * @param fileName file name to store primes
    * */
    static void getFirstPrimes(String fileName) throws IOException {
        boolean[] isNotPrime = new boolean[PARTITION+1]; //false = prime, true = not prime.

        isNotPrime[0] = isNotPrime[1] = true;

        for (int i = 2; i*i <= limit; i++) {
            if (!isNotPrime[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    isNotPrime[j] = true;
                }
            }
        }

        FileWriter writer = new FileWriter(fileName);

        // Write primes to the file
        for (int i = 2; i <= limit; i++) {
            if (!isNotPrime[i]) {
                smallPrimes[intCounter++] = i;
                writer.write(i + "\n");
            }
        }
        writer.close();
    }

    /*
    * get primes in the range of integer from lo to hi.
    * Technically less than Integer.MAX_VALUE to avoid Out of bounds exception.
    *
    *
    * @param fileName file name to store primes.
    * @param lo lower range of partition.
    * @param hi upper range of partition.
    * */
    static void getIntPrimes(String fileName, int lo, int hi) throws IOException {
        int n = (hi-lo+1);
        boolean[] isNotPrime = new boolean[n];

        for (int i = 0; i < intCounter; i++) {
            int p = smallPrimes[i];
            if ((long)p*p > hi) break;
            long start = (long)p*p;
            if (start < lo) {
                start = ((lo + (p - 1)) / p) * (long) p;
            }
            int j = (int)(start-lo);
            for (; j < n; j+=p) {
                isNotPrime[j] = true;
            }
        }

        FileWriter writer = new FileWriter(fileName, true);

        for (int i = 0; i < n; i++) {
            if (!isNotPrime[i]) {
                smallPrimes[intCounter++] = lo+i;
                writer.write(lo+i + "\n");
                currMaxPrime = lo+i;
            }
        }
        writer.close();
    }
    /*
     * get primes in the range of long from lo to hi and write to file.
     * Technically less than Integer.MAX_VALUE to avoid Out of bounds exception.
     *
     *
     * @param fileName file name to store primes.
     * @param lo lower range of partition.
     * @param hi upper range of partition.
     * */
    static void writePrimesToFile(String fileName, long lo, long hi) throws IOException {
        int n = (int)(hi-lo+1);
        boolean[] isNotPrime = new boolean[n];

        for (int i = 0; i < intCounter; i++) { //sieve with int primes
            int p = smallPrimes[i];
            if ((long)p*p > hi) break;
            long start = (long) p*p;
            if (start < lo) start = ((lo + (p - 1)) / p) * (long) p;
            int j = (int)(start-lo);
            for (; j < n; j+=p) {
                isNotPrime[j] = true;
            }
        }

        // write to file
        FileWriter writer = new FileWriter(fileName, true);

        for (int i = 0; i < n; i++) {
            if (!isNotPrime[i]) {
                longCounter++;
                writer.write(lo+i + "\n");
                currMaxPrime = lo+i;
            }
        }
        writer.close();
    }
    static void smallestPrimes(String fileName) throws IOException {
        getFirstPrimes(fileName);
        while (true) {
            limit += PARTITION;
            if (limit < ARBITRARY_INT_THRESHOLD) {
                //After testing, this method gets around 98175707 primes.
                // 98175707^2 is larger than the range of long.
                // It is enough unless I am allowed to use BigInteger.
                getIntPrimes(fileName, (int)limit-PARTITION+1, (int)limit);
            }
            else {
                writePrimesToFile(fileName, limit - PARTITION + 1, limit);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        try {
            smallestPrimes("21097305d_SunghakHeo.txt");
        } catch (OutOfMemoryError outOfMemoryError) {
            System.err.println("Array size too large");
            System.err.println("Max JVM memory: " + Runtime.getRuntime().maxMemory());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            System.err.print("Index out of bounds ");
            indexOutOfBoundsException.printStackTrace();
            System.err.println("Max JVM memory: " + Runtime.getRuntime().maxMemory());
        }
        long end = System.currentTimeMillis();
        System.out.println("I found " + (intCounter+longCounter) + " primes below " + limit + " in " + (end - start) / 1000. + " seconds.");
    }
}

