/*
Copyright 2016 Seth Traverse

This file is part of SecureFiles.

SecureFiles is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SecureFiles is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SecureFiles.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.viaware.securefiles.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Encryptor {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 800000;
    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    private char[] pass;

    public Encryptor(char[] pass) {
        this.pass = new char[pass.length];
        System.arraycopy(pass, 0, this.pass, 0, pass.length);
    }

    private static byte[] createRandBytes(int length) {
        byte[] salt = new byte[length];
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(salt);
        return salt;
    }

    private static SecretKeySpec getSecretKey(char[] pass, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(pass, salt, ITERATIONS, 128);
        byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public EncryptedData encrypt(byte[] bytes) {
        try {
            byte[] salt = createRandBytes(SALT_LENGTH);
            byte[] iv = createRandBytes(16);

            SecretKeySpec secretKey = getSecretKey(pass, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(bytes);

            return new EncryptedData(cipherText, salt, iv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(EncryptedData data) {
        try {
            SecretKeySpec secretKey = getSecretKey(pass, data.getSalt());

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            if (data.hasIv()) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(data.getIv()));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            }

            return cipher.doFinal(data.getCipherText());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            //e.printStackTrace();
            //Squash this one since it seems to occur when the pass is wrong.
            //No need to clutter the log output with a stack trace.
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

}
