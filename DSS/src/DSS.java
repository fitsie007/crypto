import java.io.*;
import java.math.BigInteger;

/**
 * Created by FitzRoi on 11/10/14.
 */
public class DSS {
    public static void main(String[] args) {
//        java DSS -p <size_in_bits_of_p> -q <size_in_bits_q> -S <secret_key_file> -P <public_key_file>
//        java DSS -M messagefile -S secret_key_file -s signature_file
//        java DSS -M messagefile -P public_key_file -s signature_file

        int L = 0, N = 0;
        File skFile = null;
        File pkFile = null;
        File sigFile = null;
        File msgFile = null;

        // get parameters from args
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                L = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-q")) {
                N = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-S")) {
                skFile = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("-P")) {
                pkFile = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("-s")) {
                sigFile = new File(args[i + 1]);
                i++;
            } else if (args[i].equals("-M")) {
                msgFile = new File(args[i + 1]);
                i++;
            }

        }

        //valid bits provided, create secret and public key files
        if (L != 0 && N != 0 && skFile != null && pkFile != null) {
            try {
                boolean verified;
                BufferedWriter pkOutput = new BufferedWriter(new FileWriter(pkFile));
                BufferedWriter skOutput = new BufferedWriter(new FileWriter(skFile));
                DSA dsa = new DSA();
                dsa.init(L, N, N + 1);
                dsa.generateParameters();
//                if(verified)
//                    System.out.println("Signature verified successfully");
//                else
//                    System.out.println("Signature not verified");

                //create a secret key file
                skOutput.write("p=" + dsa.sk.getP() + "\n");
                skOutput.write("q=" + dsa.sk.getQ() + "\n");
                skOutput.write("g=" + dsa.sk.getG() + "\n");
                skOutput.write("k=" + dsa.sk.getK() + "\n");
                skOutput.write("x=" + dsa.sk.getKey() + "\n");
                skOutput.write("y=" + dsa.sk.getY() + "\n");
                skOutput.close();

                //create a public key file
                pkOutput.write("p=" + dsa.pk.getP() + "\n");
                pkOutput.write("q=" + dsa.pk.getQ() + "\n");
                pkOutput.write("g=" + dsa.pk.getG() + "\n");
                pkOutput.write("y=" + dsa.sk.getY() + "\n");
                pkOutput.close();

                System.out.println("Secret file(" + skFile + ") and public file(" + pkFile + ") created successfully");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //sign a document
        if (msgFile != null && sigFile != null && skFile!=null)
        {
            byte[]message=null;
            byte[]signature=null;
            BigInteger p=null,q=null,g=null,k=null,x=null,y=null;

            //try to read the message from file
            try (BufferedReader msgFileReader = new BufferedReader(new FileReader(msgFile))) {
                StringBuilder sb = new StringBuilder();
                String line = msgFileReader.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = msgFileReader.readLine();
                }
                String messageStr = sb.toString();
                message=messageStr.getBytes();
                msgFileReader.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //try to read the secret key file
            try  {
                BufferedReader skFileReader = new BufferedReader(new FileReader(skFile));
                BufferedWriter sigOutput = new BufferedWriter(new FileWriter(sigFile));

                String line = skFileReader.readLine();
                while (line != null) {
                    String var=line.substring(0,2);
                    switch(var) {
                        case "p=":
                            p=new BigInteger(line.substring(2,line.length()));
                            //System.out.println("p=" +p);
                         break;
                        case "q=":
                            q=new BigInteger(line.substring(2,line.length()));
                            //System.out.println("q=" +q);
                        break;
                        case "g=":
                            g=new BigInteger(line.substring(2,line.length()));
                        break;
                        case "k=":
                            k=new BigInteger(line.substring(2,line.length()));
                        break;
                        case "x=":
                            x=new BigInteger(line.substring(2,line.length()));
                        break;
                        case "y=":
                            y=new BigInteger(line.substring(2,line.length()));
                        break;
                    }

                    line = skFileReader.readLine();
                }
                DSA dsa = new DSA();
                dsa.init(L, N, N + 1);
                signature=dsa.signDocument(message, p, q, g,  k,  x, y);
                //System.out.println(signature);
                sigOutput.write("*="+signature.toString()+"\n");
                sigOutput.write("r=" +dsa.sk.sk.getR().toString() +"\n");
                sigOutput.write("s=" +dsa.sk.sk.getS().toString() +"\n");
                sigOutput.close();
                skFileReader.close();
                System.out.println("Document signed successfully!");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //verify a signature
        if (msgFile != null && sigFile != null && pkFile!=null)
        {
            byte[]message=null;
            byte[]signature=null;
            boolean verified;
            BigInteger p=null,q=null,g=null,k=null,x=null,y=null,r=null,s=null;
            try  {
                BufferedReader msgFileReader = new BufferedReader(new FileReader(msgFile));
                BufferedReader pkFileReader = new BufferedReader(new FileReader(pkFile));
                BufferedReader sigFileReader = new BufferedReader(new FileReader(sigFile));

                String pkLine = pkFileReader.readLine();
                String msgLine = msgFileReader.readLine();
                String sigLine = sigFileReader.readLine();

                while (pkLine != null) {
                    String var=pkLine.substring(0,2);
                    switch(var) {
                        case "p=":
                            p=new BigInteger(pkLine.substring(2,pkLine.length()));
                            //System.out.println("p=" +p);
                            break;
                        case "q=":
                            q=new BigInteger(pkLine.substring(2,pkLine.length()));
                            //System.out.println("q=" +q);
                            break;
                        case "g=":
                            g=new BigInteger(pkLine.substring(2,pkLine.length()));
                            break;
                        case "y=":
                            y=new BigInteger(pkLine.substring(2,pkLine.length()));
                            break;
                    }

                    pkLine = pkFileReader.readLine();
                }
                pkFileReader.close();

                //read and parse the message file
                StringBuilder sb = new StringBuilder();
                while (msgLine != null) {
                    sb.append(msgLine);
                    sb.append(System.lineSeparator());
                    msgLine = msgFileReader.readLine();
                }
                String messageStr = sb.toString();
                message=messageStr.getBytes();
                System.out.println(message);
                msgFileReader.close();

                //read and parse the signature file
                while (sigLine != null) {
                    String var = sigLine.substring(0, 2);
                    switch (var) {
                        case "*=":
                            signature = sigLine.substring(2, sigLine.length()).getBytes();
                            //System.out.println("sig=" +signature);
                            break;
                        case "r=":
                            r = new BigInteger(sigLine.substring(2, sigLine.length()));
                            //System.out.println("r=" +r);
                            break;
                        case "s=":
                            s = new BigInteger(sigLine.substring(2, sigLine.length()));
                            //System.out.println("s=" +s);
                            break;
                    }
                    sigLine = sigFileReader.readLine();
                }
                sigFileReader.close();
                DSA dsa = new DSA();
                verified=dsa.verifySignature(signature,message, p, q, g, y, r, s);
                if(verified)
                    System.out.println("Signature verified successfully!");
                else
                    System.err.println("Signature could not be verified!");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}