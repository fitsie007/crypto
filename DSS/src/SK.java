import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by FitzRoi on 11/9/14.
 */
public class SK extends Hash
{
    private BigInteger p=null;
    private BigInteger q=null;
    private BigInteger g=null;
    private BigInteger k=null;
    private BigInteger x=null;
    private BigInteger y=null;
    private BigInteger kInv=null;
    private BigInteger s=null;
    private BigInteger r=null;

    PK pk=null;

    public SK(){};

    public SK(PK pk2)
    {
      pk= pk2;
    }


    public void setP(BigInteger p){this.p=p;}
    public void setQ(BigInteger q){this.q=q;}
    public void setG(BigInteger g){this.g=g;}
    public void setK(BigInteger k){this.k=k;}
    public void setS(BigInteger s){this.s=s;}
    public void setR(BigInteger r){this.r=r;}
    public void setY(BigInteger y){this.y=y;}
    public void setKey(BigInteger x){this.x=x;}

    public BigInteger getP(){return this.p;}
    public BigInteger getG(){return this.g;}
    public BigInteger getQ(){return this.q;}
    public BigInteger getK(){return this.k;}
    public BigInteger getS(){return this.s;}
    public BigInteger getR(){return this.r;}
    public BigInteger getY(){return this.y;}
    public BigInteger getKey(){return this.x;}


    public void genK(int L,int N){
        int requested_security_strength=Util.INVALID_STRENGTH;
        BigInteger K=null;

        if(Util.bitSizesValid(L, N))
        {
            requested_security_strength = Util.getSecurityStrength(L, N);
            BigInteger c = new BigInteger(N + 64, requested_security_strength, new Random());
            K = (c.mod(q.subtract(Util.ONE))).add(Util.ONE);//k = (c mod (q–1)) + 1.
        }
        else {
            System.err.println("L and N invalid! Could not generate K");
        }

        this.setK(K);

    }

    public BigInteger genG(){
        BigInteger p=getP();
        BigInteger q=getQ();
        System.out.println(p);
        BigInteger g=Util.ZERO;
        BigInteger e=(p.subtract(Util.ONE).divide(q));
        List<BigInteger> hVals = new ArrayList<>();

        while(g.compareTo(Util.ONE)==0)//keep going if g==1
        {
            BigInteger h=new BigInteger(p.bitLength()-1,new Random());
            if(!hVals.contains(h))
            {
                g=h.modPow(e, p);//g=h^e mod p
            }
            hVals.add(h);
        }

        return g;
    }


    public void genKey(int L,int N) {
        int requested_security_strength=Util.INVALID_STRENGTH;

        if(Util.bitSizesValid(L,N))
        {
            requested_security_strength = Util.getSecurityStrength(L, N);
            BigInteger c = new BigInteger(N + 64, requested_security_strength, new Random());
            BigInteger x = (c.mod(q.subtract(Util.ONE))).add(Util.ONE);//x = (c mod (q–1)) + 1
            this.setKey(x);
            BigInteger y=g.modPow(x,p); //y=g^x mod p
            this.setY(y);
        }
    }

    //sign a message based on DSS 4.6
    //K^-1 computed as per Appendix C-1
    public byte[] sign(byte[] m) {
        int N=0,outLen;
        //System.out.println(m);

        N = q.bitLength();
        outLen=hashLength();
        byte []mHash=null;
        int bits=Math.min(N, outLen);
        try {
            mHash=hash(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("g="+g +"\np=" +p +"\nq=" +q +"\n" +"\nk=" +k );
        //System.out.println("\ny=" +y +"\nx=" +x);

        BigInteger r=(g.modPow(k,p).mod(q));//r=(gk mod p) mod q
        this.setR(r);

        //z = the leftmost min(N, outLen) bits of Hash(M).
        String zStr=Util.byteToBinaryString(mHash, bits);
        BigInteger z=Util.bitStringtoInt(zStr);
        //s = ( k^−1 (z + xr)) mod q.
        kInv=k.modInverse(q);
        BigInteger s=kInv.multiply(z.add(x.multiply(r))).mod(q);

        //System.out.println("s=" +s +"r" +r);
        this.setS(s);


        return mHash;

    }

    public boolean verify(byte[] signature, byte[] message) {
        int N=0,outLen;
        N=q.bitLength();
        outLen=hashLength();
        byte []mHash=null;
        int bits=Math.min(N, outLen);

        if(r.compareTo(Util.ZERO)==1 && r.compareTo(q)==-1)//0<r<q
        {
            if(s.compareTo(Util.ZERO)==1 && r.compareTo(q)==-1)//0<s<q
            {
                BigInteger w = s.modInverse(q);//w = (s′)^–1 mod q.
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
                BigInteger v=((g.modPow(u1,p).multiply(y.modPow(u2,p))).mod(p)).mod(q) ;

                if(v.compareTo(r)==0)//if v=r', message verified
                    return true;
            }
        }

        return false;

    }


}
