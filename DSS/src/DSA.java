import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by FitzRoi on 11/5/14.
 */
public class DSA{
    //public final static String type = Cipher.DSA;

    public static int N=0;
    public static int L=0;
    public static int seedLen=0;


    DSA_SK sk = null; //new DSA_SK();
    DSA_PK pk = null; //new DSA_PK();

    public DSA(){}

    public DSA(DSA_SK sk2, DSA_PK pk2) {
        sk = sk2;
        pk = pk2;
    }

    public void init(int L, int N, int seedLen)
    {
        this.L=L;
        this.N=N;
        this.seedLen=seedLen;
    }

    //generate public and private keys x and y
    private DSA_SK genKey() {
        if(sk==null)
            sk=new DSA_SK();
        if(pk==null)
            pk=new DSA_PK();

        //BigInteger p=pk.genP(L,N, seedLen);
        //BigInteger p=sk.getP();

        //BigInteger y=sk.getY();
        //sk.setKey(y);

        return sk;
    }




    public DSA_SK getSK() {
        return sk;
    }


    public byte[] sign(byte[] m) {
        if(this.sk == null) return null;
        return this.sk.sign(m);
    }


    public boolean verify(byte[] signature, byte[] message) {
        //return pk.verify_unpad_hash(signature, message);
//        if ((pk == null) && (sk !=null))
//            pk = sk.getPK();
        if(this.pk == null) return false;
        return this.pk.verify(signature, message);
    }


    public void generateParameters()
    {
        BigInteger p,q,g,k;

        //generate new domain parameters
        this.sk=new DSA_SK();
        this.pk=new DSA_PK();

        this.sk.sk=new SK();
        this.pk.pk=new PK();

        q=this.pk.genQ(L, N, N+1);//L,N,seedlen
        this.pk.genQ(L, N, N+1);
        this.pk.setQ(q);

        p=this.pk.genP(L, N, N+1);//L,N,seedlen
        this.pk.setP(p);
        g=this.pk.genG();
        this.pk.setG(g);

        //store domain parameters in pk
        this.sk.setG(g);
        this.sk.setP(p);
        this.sk.setQ(q);


        this.sk.pk=new PK();
        this.sk.sk=new SK();
        this.sk.sk.setP(p);
        this.sk.sk.setQ(q);
        this.sk.sk.setG(g);

        this.sk.pk.setP(p);
        this.sk.pk.setQ(q);
        this.sk.pk.setG(g);


        this.sk.genK(L, N);
        this.sk.genKey(L, N);

        this.sk.sk.setK(this.sk.getK());
        this.sk.sk.setKey(this.sk.getKey());
        this.sk.sk.setY(this.sk.getY());

    }

    public byte[] signDocument(byte[]message, BigInteger p, BigInteger q, BigInteger g, BigInteger k, BigInteger x, BigInteger y)
    {
        byte[]signature=null;
        boolean verified;

        this.sk=new DSA_SK();
        this.pk=new DSA_PK();

        this.sk.sk=new SK();
        this.pk.pk=new PK();
        this.pk.setQ(q);
        this.pk.setP(p);
        this.pk.setG(g);

        //store domain parameters in pk
        this.sk.setG(g);
        this.sk.setP(p);
        this.sk.setQ(q);


        this.sk.pk=new PK();
        this.sk.sk=new SK();
        this.sk.sk.setP(p);
        this.sk.sk.setQ(q);
        this.sk.sk.setG(g);

        this.sk.pk.setP(p);
        this.sk.pk.setQ(q);
        this.sk.pk.setG(g);


        this.sk.setK(k);
        this.sk.setKey(x);

        this.sk.sk.setK(k);
        this.sk.sk.setKey(x);
        this.sk.sk.setY(y);

        signature=this.sk.sk.sign(message);
        return signature;
    }

    public boolean verifySignature(byte[]signature,byte[]message, BigInteger p, BigInteger q, BigInteger g, BigInteger y, BigInteger r, BigInteger s)
    {
        boolean verified;
        this.sk=new DSA_SK();
        this.pk=new DSA_PK();

        this.sk.sk=new SK();
        this.pk.pk=new PK();
        this.pk.setQ(q);
        this.pk.setP(p);
        this.pk.setG(g);

        //store domain parameters in pk
        this.sk.setG(g);
        this.sk.setP(p);
        this.sk.setQ(q);


        this.sk.pk=new PK();
        this.sk.sk=new SK();
        this.sk.sk.setP(p);
        this.sk.sk.setQ(q);
        this.sk.sk.setG(g);

        this.sk.pk.setP(p);
        this.sk.pk.setQ(q);
        this.sk.pk.setG(g);

        this.sk.sk.setY(y);


        this.sk.sk.setS(s);
        this.sk.setS(s);
        this.sk.sk.setR(r);
        this.sk.setR(r);

        verified=this.sk.verify(signature,message);
        return verified;
    }
}
