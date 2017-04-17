/*
 * Generate Primes using Rabin-Miller Tests and Eratosthenes sieve
 * Cryptology Fall 2014 -- Dr. Silaghi
 * September 23, 2014  
 */



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FitzRoi
 */
public class GeneratePrimes {

    /**
     * @param args the command line arguments
     */
    static final BigInteger ZERO = new BigInteger("0");
    static final BigInteger ONE = new BigInteger("1");
    static final BigInteger TWO = new BigInteger("2");
    static final boolean MAYBE_PRIME=true;
    static final boolean COMPOSITE=false;
    
    static Random r = new Random();
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String userN;
        int n;
        int passEratosthenes=0;
        int rabinRounds=0;
        boolean rabinMillerTest=false;
        double confidence=0;
        
        if(args.length==2)
        {
            userN=args[0];
            n=Integer.parseInt(userN);
            confidence=Double.parseDouble(args[1]);
        }
        else//no correct parameters specified
        {
            n=2048;
            confidence=98.9;
            System.out.println("Bits not set. Using n=" +n +", confidence=" +confidence);
            
        }
            
        List<Integer> primesLT2000 = new ArrayList<>();
        primesLT2000 = erathosthenes(2000);
        BigInteger p=new BigInteger(Integer.toString(n));
        double rounds=Math.log(Math.abs(confidence-1))*Math.abs(Math.log(4))+4;
        
        rabinRounds=(int)rounds;
       
        //make sure p is divisible by primes less than 2000 using Eratosthenes sieve
        while(passEratosthenes!=-1)//test failed
        {
            p=generateNumber(n, r);
            passEratosthenes=divisible(p,primesLT2000);
            if(passEratosthenes!=-1)
            {
                System.out.println("Next tested number failed at division with " + primesLT2000.get(passEratosthenes));
                System.out.println(p);
            }
        }
        
        //Try successive numbers if Rabin Miller Failed
        while(!rabinMillerTest)
        {
           //BigInteger newP=p2.add(ONE);
           p=generateNumber(n, r);
           rabinMillerTest=doRabinMiller(p, primesLT2000, rabinRounds); 
        }
        
        System.out.print("The found candidate is (with " + rabinRounds +" round(s) ");
        for(int i=0;i<rabinRounds;i++)
        {
            System.out.print(" a" + (i+1) + "=" +primesLT2000.get(i));
            if(i<rabinRounds-1)
                System.out.print(",");
        }
        
        System.out.println(")\n" +p);
    }
    
    
    public static BigInteger setEndBits(BigInteger p, int n)
    {
        BigInteger p2;
        p2=p.setBit(0);//set the first end bit to 1
        p2=p2.setBit(n-1);//set the last end bit to 1 
        return p2;
    }

    public static BigInteger generateNumber(int n, Random r)
    {
        BigInteger p = new BigInteger(n,r);
        System.out.println("Generated n-bit number:\n" +p);
        p=setEndBits(p, n);//set end bits to 1
        return p;
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
        {     // check different values of 'a' using certain confidence
                a = BigInteger.valueOf(Integer.parseInt(sieve.get(i).toString()));
                boolean maybePrime = TEST(n, a, k, q);
                if (maybePrime==false)
                {
                    System.out.print("Next tested number failed at Rabin-Miller round " +(i+1) +" with");
                    for(int j=0;j<=i;j++)
                    {
                        System.out.print(" a" + (i+1) + "=" +sieve.get(j));
                        if(i!=j)
                            System.out.print(",");
                    }
                    System.out.println(" and k=" +k +"\n");
                    return COMPOSITE;
                }
        }
        
        return MAYBE_PRIME;

    }
}

