package run.brief.secure;


import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import run.brief.util.Base64;


public final class Encrypt {

    private static final String TYPE_256="SHA-256";
    private final static String TYPE_512="SHA-512";
    
    public static final int KEYLENGTH_32=32;
    public static final int KEYLENGTH_256=256;
    public static final int KEYLENGTH_512=512;
    //public static final int KEYLENGTH_1024=1024;
    //public static final int KEYLENGTH_2048=2048;
    
	private final Cipher cipher;
	private final SecretKeySpec key;
    private final IvParameterSpec ivspec;
    private static String useType=TYPE_512;
    //private static int useKeyLength=KEYLENGTH_512;
    
	// this should never change....
    //private String ENC_PSSWD="bR13FLY11KEY22bYrUNPLAYg0f1GUR399s3CUR3k3Y88lAUNCH3D2013";
    
    
	public Encrypt(String keyString) throws Exception {
		// hash password with SHA-256 and crop the output to 128-bit for key
		if(keyString==null)
			throw new Exception();
		MessageDigest digest = MessageDigest.getInstance(useType);
		//MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.update(keyString.getBytes("UTF-8"));
		byte[] keyBytes = new byte[16];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		key = new SecretKeySpec(keyBytes, "AES");
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ivspec= new IvParameterSpec(iv);
	}

	public static String getRandomKey(int keyLength) {
		
		return csRandomAlphaNumericString(keyLength);
	}

	
public byte[] encrypt(byte[] message) throws Exception
{
    //AlgorithmParameterSpec spec = new IvParameterSpec(cipher.getIV());
    cipher.init(Cipher.ENCRYPT_MODE, key,ivspec);

    return cipher.doFinal(message);
}
public byte[] decrypt(byte[] encMessage) throws Exception
{
    
    cipher.init(Cipher.DECRYPT_MODE, key,ivspec);
     //IvParameterSpec iv = new IvParameterSpec(cipher.getIV());
    return cipher.doFinal(encMessage);
}
public static byte[] fromHexString(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}
	public static String encodeForEncription(String stringToEncode) {
		String encoded=null;
		try {
			encoded=Base64.encodeToString(stringToEncode.getBytes(), true);
		} catch(Exception e) {
            //BLog.e("ENC64","Error Dencodeing: "+e.getMessage());
		}
		return encoded;
	}

    public static String encodeForEncriptionRaw(byte[] encodeBytes) {
        String encoded=null;
        try {
            encoded=Base64.encodeToString(encodeBytes, true);
        } catch(Exception e) {
            //BLog.e("ENC64","Error Dencodeing: "+e.getMessage());
        }
        return encoded;
    }
    public static byte[] encodeForEncriptionRawBtye(byte[] encodeBytes) {
        byte[] encoded=null;
        try {
            encoded=Base64.encodeToByte(encodeBytes, true);
        } catch(Exception e) {
            //BLog.e("ENC64","Error Dencodeing: "+e.getMessage());
        }
        return encoded;
    }
	public static String decodeFromEncription(String stringToDecode) {
		String dencoded=null;
		try {
			dencoded=new String(Base64.decode(stringToDecode.toCharArray()));
		} catch(Exception e) {
            //BLog.e("ENC64","Error Dencodeing: "+e.getMessage());
		}
		return dencoded;
	}

    public static byte[] decodeFromEncriptionRaw(String stringToDecode) {
        try {
            return Base64.decode(stringToDecode.toCharArray());
        } catch(Exception e) {
            //BLog.e("ENC64","Error Dencodeing: "+e.getMessage());
        }
        return null;
    }

	public OutputStream encryptStream(OutputStream out) throws Exception {
		// create a randomly generated IV (initialization vector)
		byte[] iv = new byte[cipher.getBlockSize()];
		new SecureRandom().nextBytes(iv);
		AlgorithmParameterSpec spec = new IvParameterSpec(iv);

		// write the IV to the beginning of the stream
		out.write(iv);

		// setup cipher for encryption and wrap original stream
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		return new CipherOutputStream(out, cipher);
	}

	public InputStream decryptStream(InputStream in) throws Exception {
		// read the IV at the beginning of the stream
		byte[] iv = new byte[cipher.getBlockSize()];
		in.read(iv);
		AlgorithmParameterSpec spec = new IvParameterSpec(iv);

		// setup cipher for decryption and wrap original stream
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		return new CipherInputStream(in, cipher);
	}

	
	private static final char[] VALID_CHARACTERS =
            "ABC23DEFGghijklmnopqHIJKLMNOdefrsVWXYZabct68uvwxyz014PQRSTU579_".toCharArray();
		    //"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456879_".toCharArray();

		// cs = cryptographically secure
		private static String csRandomAlphaNumericString(int numChars) {
		    SecureRandom srand = new SecureRandom();
		    char[] buff = new char[numChars];

		    for (int i = 0; i < numChars; ++i) {
		      // reseed random once you've used up all available entropy bits
		      if ((i % 10) == 0) {
		          srand.setSeed(srand.nextLong()); // 64 bits of random!
		      }
		      buff[i] = VALID_CHARACTERS[srand.nextInt(VALID_CHARACTERS.length)];
		    }
		    return new String(buff);
		}

}

