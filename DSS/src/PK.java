import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by FitzRoi on 11/10/14.
 */
public class PK extends Hash {


    private BigInteger p=null;
    private BigInteger q=null;
    private BigInteger g=null;
    private BigInteger y=null;
    private BigInteger r=null;
    private BigInteger s=null;


    public PK(){}

    public void setP(BigInteger p){this.p=p;}
    public void setQ(BigInteger q){this.q=q;}
    public void setG(BigInteger g){this.g=g;}
    public void setS(BigInteger s){this.s=s;}
    public void setR(BigInteger r){this.r=r;}
    public void setKey(BigInteger y){this.y=y;}

    public BigInteger getP(){return this.p;}
    public BigInteger getG(){return this.g;}
    public BigInteger getQ(){return this.q;}
    public BigInteger getS(){return this.s;}
    public BigInteger getR(){return this.r;}
    public BigInteger getKey(){return this.y;}


    public boolean verify(byte[] signature, byte[] message) {
        int N=0,outLen;
        BigInteger w=null;
        outLen=hashLength();
        byte []mHash=null;
        int bits=Math.min(N, outLen);

        if(r.compareTo(Util.ZERO)==1 && r.compareTo(q)==1)//0<r<q
        {
            if(s.compareTo(Util.ZERO)==1 && r.compareTo(q)==1)//0<s<q
            {
                w = s.modInverse(q);//w = (s′)^–1 mod q.
                //z = the leftmost min(N, outlen) bits of Hash(M′ )
                outLen=hashLength();
                try {
                    mHash=hash(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String zStr=Util.byteToBinaryString(mHash,bits);
                BigInteger z=Util.bitStringtoInt(zStr);
                BigInteger u1=z.multiply(w).mod(q);//u1 = (zw) mod q.
                BigInteger u2=((r.multiply(w).mod(q)));//u2 = ((r′)w) mod q.
                // v = (((g)u1 (y)u2) mod p) mod q.
                BigInteger v=( g.modPow(u1,p).multiply(y.modPow(u2,p)).mod(q) );

                if(v.compareTo(r)==0)//if v=r', message verified
                    return true;
            }
        }

        return false;

    }


    public BigInteger genQ(int L, int N, int seedLen) {
        int outLen = N - 1;
        int n = (L / outLen) - 1;
        int b = L - 1 - (n * outLen);
        BigInteger U = null;
        BigInteger Q = null;
        boolean qPrime = false;
        BigInteger domain_parameter_seed = null;

        if (Util.bitSizesValid(L, N) && seedLen > N) {
            while (!qPrime) {
                domain_parameter_seed = Util.generateNumber(N, new Random());

                //hash the domain_parameter_seed
                try {
                    U = new BigInteger(Hash.hash(domain_parameter_seed.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //U = Hash(domain_parameter_seed) mod 2^N–1
                //q=2^N–1 +U+1–(U mod 2).
                BigInteger TWO_POW_N_MINUS_ONE = Util.TWO.pow(N - 1);
                U = U.mod(TWO_POW_N_MINUS_ONE);
                Q = TWO_POW_N_MINUS_ONE.add(U.add(Util.ONE)).subtract(U.mod(Util.TWO));
                //check if q is prime using Miller-Rabin
                M_R MR_Test = new M_R(Util.M_R_Rounds, Q);
                qPrime = MR_Test.runTest();

            }
        }
        return Q;

    }

    public BigInteger genP(int L, int N, int seedLen) {
        //generate a prime p
        BigInteger P = null;
        boolean pPrime=false;
        int outLen=N-1;
        int n = (L / outLen)-1;
        int b = L - 1 - (n * outLen);
        BigInteger domain_parameter_seed=new BigInteger(seedLen,new Random());
        int offset = 1;
        for (int i = 0; i <= (4 * L - 1); i++) {
            for (int j = 0; i <= n; j++) {
                BigInteger vj = Util.ZERO;
                BigInteger W = Util.ZERO;
                BigInteger X = Util.ZERO;
                BigInteger C = Util.ZERO;
                byte[] hashVal;
                //Vj = Hash ((domain_parameter_seed + offset + j) mod 2^seedlen).
                try {
                    BigInteger tempVj=domain_parameter_seed.add(new BigInteger(Integer.toString(offset)).add(new BigInteger(Integer.toString(j))));
                    tempVj=tempVj.mod(Util.TWO.pow(j * seedLen));
                    byte[] bytesToHash=tempVj.toByteArray();
                    hashVal = Hash.hash(bytesToHash);
                    vj = new BigInteger(hashVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //W = V0 + (V1 ∗ 2^outlen)+ ...+ (V^n–1 ∗ 2^(n–1) ∗ outlen) + ((Vn mod 2^b) ∗2^n ∗ outlen)
                W = W.add(vj);
                if (j == n)
                    W = W.add(vj.mod(Util.TWO.pow(b)).multiply(Util.TWO.pow(n * outLen)));//+ ((Vn mod 2^b) ∗2^n ∗ outlen)
                X = W.add(Util.TWO.pow(L - 1));
                C = X.mod(Util.TWO.multiply(q));//get q from pk

                P = X.subtract(C.subtract(Util.ONE)); //p ≡ 1 (mod 2q).
                if (P.compareTo(Util.TWO.pow(L - 1)) == -1)//If (p < 2L–1)
                {
                    offset=offset+n+1;
                    //i++;
                }

                //test if p is prime
                M_R MRTest = new M_R(Util.M_R_Rounds, P);
                pPrime = MRTest.runTest();

                if (pPrime) {
                    return P;
                }

            }
        }
        return P;
    }

    public BigInteger genG(){
        BigInteger p=getP();
        BigInteger q=getQ();
        BigInteger G=Util.ONE;
        BigInteger e=(p.subtract(Util.ONE).divide(q));
        List<BigInteger> hVals = new ArrayList<>();

        while(G.compareTo(Util.ONE)==0)//keep going if g==1
        {
            BigInteger h=new BigInteger(p.bitLength()-1,new Random());
            if(!hVals.contains(h))
            {
                G=h.modPow(e, p);//g=h^e mod p
            }
            hVals.add(h);
        }

        return G;
    }

}
