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

import ca.viaware.securefiles.Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Encryptor {

    /*
        This whole thing needs to be reworked since right now
        it might as well just be rot13

        Needs to take a salt with the pass, and use a SecureKeyFactory
        with a crap ton of iterations to create the hash
     */

    private static final String ALGORITHM = "AES";
    private Key key;

    public Encryptor(char[] pass) {
        try {
            byte[] pBytes = Utils.toBytes(pass);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            pBytes = sha.digest(pBytes);
            pBytes = Arrays.copyOf(pBytes, 16);
            key = new SecretKeySpec(pBytes, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(bytes);
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
        }
        return null;
    }

    public byte[] decrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(bytes);
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
        }

        return null;
    }

}
