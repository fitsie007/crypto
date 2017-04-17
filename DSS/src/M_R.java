/**
 * Created by FitzRoi on 11/8/14.
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class M_R {

    static final BigInteger ZERO = new BigInteger("0");
    static final BigInteger ONE = new BigInteger("1");
    static final BigInteger TWO = new BigInteger("2");
    static final boolean MAYBE_PRIME=true;
    static final boolean COMPOSITE=false;
    public int rounds;
    private BigInteger p;

    public M_R(int rounds, BigInteger p)
    {
        this.rounds=rounds;
        this.p=p;
    }

    public boolean runTest() {
        List<Integer> primesLT2000 = new ArrayList<>();
        int passEratosthenes=0;
        boolean rabinMillerTest=false;
        primesLT2000 = erathosthenes(2000);


        //make sure p is divisible by primes less than 2000 using Eratosthenes sieve
        passEratosthenes = divisible(p, primesLT2000);
        if (passEratosthenes != -1)
            return COMPOSITE;
        rabinMillerTest=doRabinMiller(p, primesLT2000, rounds);
       return rabinMillerTest;
    }


    //This function returns an arrayList (sieve) of primes less than n
    public static List erathosthenes(int n)
    {
        List<Integer> sieve = new ArrayList<>();
        boolean[] isPrime = new boolean[n + 1];

        for(int i=1; i<n; i++)
        {
            isPrime[i]=true;

        }

        for (int i = 2; i*i <= n; i++)
        {
            if (isPrime[i]) {
                for (int j = i; i*j <= n; j++) {
                    isPrime[i*j] = false;
                }
            }
        }

        for(int i=0; i<n;i++)
        {
            if(isPrime[i])
                sieve.add(i);
        }

        return sieve;
    }

    //This function accepts a BigInteger and a sieve of primes
    //and checks if it is divisible by a prime.
    //It returns the index of the value that fails the division
    //or -1 if not divisible
    public static int divisible(BigInteger p, List primes)
    {
        BigInteger rem;
        int retVal=-1;

        for(int i=1;i<primes.size(); i++)
        {
            BigInteger prime=new BigInteger(primes.get(i).toString());
            rem=p.remainder(prime);
            //System.out.println("Checking if " +p +" is divisible by " +prime);

            if(rem.compareTo(ZERO)==0)//equal to
            {
                retVal=i;//division breaks here
                break;
            }
        }
        return retVal;
    }
    //This function performs Miller's test to check if a number Maybe prime
    //or composite
    public static boolean TEST(BigInteger n, BigInteger a, int k, BigInteger q)
    {
        for (int i = 0; i < k; i++)
        {
            BigInteger a2jq = TWO.pow(i);
            a2jq = a2jq.multiply(q);
            BigInteger x = a.modPow(a2jq, n);
            if (x.equals(n.subtract(ONE)) || x.equals(ONE))
            {
                return MAYBE_PRIME;
            }
        }
        return COMPOSITE;

    }

    //This function finds a, k, and q and calls Miller's test
    //to check if a number maybe prime or composite
    //It prints the list of a values that fails the test
    public static boolean doRabinMiller(BigInteger n, List sieve, int rounds)
    {
        BigInteger q = n.subtract(ONE);
        BigInteger a;
        int k = 1;

        while (q.mod(TWO).equals(ZERO))
        {
            k++;
            q = q.divide(TWO);
        }

        for (int i=0; i<rounds; i++)
        {   // check different values of 'a' using certain confidence
            a = BigInteger.valueOf(Integer.parseInt(sieve.get(i).toString()));
            boolean maybePrime = TEST(n, a, k, q);
            if (maybePrime==false)
            {
                return COMPOSITE;
            }
        }

        return MAYBE_PRIME;

    }

}
