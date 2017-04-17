/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package caesarcipher;

/**
 *
 * @author FitzRoi
 */
public class CaesarCipher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        char alphabet[]={'A','B','C','D','E','F','G','H','I','J','K','L','M',
                         'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
                        };
        
        String cipherText="GCUA VQ DTGCM";
        int shift=2;
       
        String plainText="";
        for(int i=0; i<cipherText.length(); i++)
        {
            char letter=cipherText.charAt(i);
            int letterInt=(int) letter;
            int plainInt=(letterInt-shift);
            //System.out.println(plainInt);
            plainText+=(char)plainInt;
        }
        System.out.println(plainText);
        
       int K=2;
       int x=0;//when encoded=C
       
       int cipherTxt=(x+K)%25;
       
       int plainTxt=(cipherTxt-K)%25;
       
       System.out.println(alphabet[cipherTxt]);
       System.out.println(alphabet[plainTxt]);
    }
    
}
