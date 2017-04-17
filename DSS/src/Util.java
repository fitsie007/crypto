
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by FitzRoi on 11/10/14.
 */
public class Util {
    public static final BigInteger ZERO = new BigInteger("0");
    public static final BigInteger ONE = new BigInteger("1");
    public static final BigInteger TWO = new BigInteger("2");
    public static final boolean INVALID=false;
    public static final boolean VALID=true;
    public static final int INVALID_STRENGTH=0;

    //sizes and strengths based on NIST SP 800-57 and DSS
    public static int L=0;
    public static int N=0;

    public static int lSizes[]        =   {1024, 2048, 3072, 7680, 15360};
    public static int nSizes[]        =   { 160,  224,  256,  384,  512};
    public static int M_R_iterations[]=   {  40,   56,   56,   64,   64};
    public static int security_strengths[]={ 80,  112,  128,  192,  256};
    public static int M_R_Rounds=10;

    public void setBitSizes(int L, int N)
    {
        this.L=L;
        this.N=N;
    }
    public int getL()
    {
        return this.L;
    }
    public int getN()
    {
        return this.N;
    }
    public static int getSecurityStrength(int L, int N){
        for(int i=0;i<lSizes.length;i++)
            if(lSizes[i]==L && nSizes[i]==N)
                return security_strengths[i];

        return INVALID_STRENGTH;
    }


    public static boolean bitSizesValid(int L, int N)
    {
        boolean validL=false;
        boolean validN=false;
        //check valid sizes for L and N based in DSS Standard
        for(int i=0;i<lSizes.length;i++)
            if(L==lSizes[i] && N==nSizes[i])
            {
                validL=true;validN=true;
                M_R_Rounds=M_R_iterations[i];
                break;
            }
        if(validL && validN)
            return true;
        return false;
    }

    public static BigInteger bitStringtoInt(String s) {
        BigInteger C=ZERO;
        int n=s.length();
        for(int i=0;i<n;i++)
        {
            BigInteger bi=new BigInteger(s.charAt(i)+"");
            C=C.add(TWO.pow(n-i).multiply(bi));
        }

        return C;
    }

    public static String byteToBinaryString(byte b[], int leftmost)
    {

        StringBuilder sb = new StringBuilder(leftmost);
        for( int i = 0; i < leftmost; i++ )
            sb.append((b[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public static BigInteger generateNumber(int n, Random r)
    {
        BigInteger p = new BigInteger(n,r);
        p=setEndBits(p, n);//set end bits to 1
        return p;
    }

    public static BigInteger setEndBits(BigInteger p, int n)
    {
        BigInteger p2;
        p2=p.setBit(0);//set the first end bit to 1
        p2=p2.setBit(n-1);//set the last end bit to 1
        return p2;
    }

}
