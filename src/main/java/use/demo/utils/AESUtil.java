package use.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class AESUtil {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM_NAME = "AES";

    private static final String TRANSFORMATION2 = "AES/CBC/PKCS7Padding";
    public static final String TRANSFORMATION3 = "AES/CBC/NoPadding";

    /**
     * @param key
     * @param secretKey
     * @param data
     *
     * @return
     *
     * @throws Exception
     */
    public static byte[] encrypt(String key, String secretKey, byte[] data) throws Throwable {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    /**
     * @param key
     * @param secretKey
     * @param data
     *
     * @return
     *
     * @throws Exception
     */
    public static byte[] decrypt(String key, String secretKey, byte[] data) throws Throwable {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, localSecretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(String key, String secretKey, byte[] data, String mode) throws Throwable {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(mode);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, localSecretKeySpec, ivParameterSpec);
        return cipher.doFinal(data);
    }

    public static int decryptFile(File srcFile, File destFile, byte[] key) {
        int res = -1;

        if (srcFile == null || !srcFile.exists()) {
            return res;
        }

        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);

            byte[] buffer = new byte[8 * 1024];
            int size = -1;
            while ((size = fis.read(buffer)) != -1) {
                System.out.println("l: " + size);
                byte[] input = null;
                if (size < buffer.length) {
                    input = new byte[size];
                    System.arraycopy(buffer, 0, input, 0, size);
                } else {
                    input = buffer;
                }

                byte[] decryData = decrypt2(new String(key,"UTF-8"), input, false);

                System.out.println("l2: " + decryData.length);
                fos.write(decryData);
            }

            fis.close();
            fos.close();

            res = 0;

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return res;
    }

    public static byte[] decrypt2(String secretKey, byte[] data, boolean needMd5) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec localSecretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), ALGORITHM_NAME);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION2);
            byte[] aesSource = new byte[16];
            for (int i = 0; i < 16; i++) {
                aesSource[i] = (byte) 0x00;
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec(aesSource);
            cipher.init(Cipher.DECRYPT_MODE, localSecretKeySpec , ivParameterSpec);

            byte[] input = null;
            if (needMd5) {
                input = new byte[data.length - 16];
                System.arraycopy(data, 0, input, 0, data.length - 16);
            } else {
                input = data;
            }
            byte[] ret = cipher.doFinal(input);
            return ret;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) throws UnsupportedEncodingException {
        File f = new File("hutest4-log.apk");
        File f_new = new File("new.apk");
        decryptFile(f, f_new, "6b363efb42eb0c64".getBytes("UTF-8"));
    }



}
