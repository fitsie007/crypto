import java.math.BigInteger;

/**
 * Created by FitzRoi on 11/5/14.
 */
class DSA_SK extends SK{

    //public String hash_alg;
    SK sk=null;
    PK pk=null;

    public DSA_SK(){}

    /**
     * Create a new secret key
     */
    public DSA_SK(PK pk2, SK sk2)
    {
        pk=pk2;
        sk=sk2;
    }


    /**
     * The academic version: without padding
     */

    @Override
    public byte[] sign(byte[] message) {
        if(sk!=null)
            return sk.sign(message);
        return null;
    }

    @Override
    public boolean verify(byte[] signature,byte[] message) {
        if(sk!=null)
            return sk.verify(signature,message);
        return false;
    }


    /**
     * Extract the public key
     */
//    //@Override
//    public DSA_PK getPK() {
//        if(pk!=null)
//            return pk.getKey();
//        return null;
//    }


}