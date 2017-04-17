import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by FitzRoi on 11/9/14.
 */
public class Hash {
    public static String hash_alg="SHA-256";
    int hash_length=256;
    private byte[] message=null;

    //use SHA-256 to hash values as approved by DSS
    public Hash(){}

    public Hash(byte[]message){this.message=message;}

    public static byte[] hash(byte[] message) throws Exception
    {
        //String strVal=val.toString();

        MessageDigest digest = MessageDigest.getInstance(hash_alg);
        byte[] hashedBytes = digest.digest(message);

        /*StringBuffer hexString = new StringBuffer();

        for (int i=0;i<hashedBytes.length;i++)
            hexString.append(Integer.toHexString(0xFF & hashedBytes[i]));
        return hexString.toString();
        */
        return hashedBytes;
    }

    public int hashLength()
    {
        return this.hash_length;
    }
}
