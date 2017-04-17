import java.math.BigInteger;

/**
 * Created by FitzRoi on 11/5/14.
 */


class DSA_PK extends PK{
    private BigInteger y=null;
    //String hash_alg=Cipher.SHA256;
    PK pk=null;

    public DSA_PK() {}
    public DSA_PK(PK pk2)
    {
        pk=pk2;

    }



    public void setKey(BigInteger y){this.y=y;}

    public BigInteger getKey(){return this.y;}
    /**
     * This is the "academic" version without hashing/padding of messages
     */
    //@Override
    public boolean verify(byte[] signature, byte[] hashed) {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * To test if the keys have the same components
     */
    //@Override
    public boolean __equals(DSA_PK o) {
        // TODO Auto-generated method stub
        return false;
    }

}