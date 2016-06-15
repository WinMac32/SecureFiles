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

public class EncryptedData {

    private byte[] cipherText;
    private byte[] salt;
    private byte[] iv;

    private boolean hasIv;

    public EncryptedData(byte[] cipherText, byte[] salt, byte[] iv) {
        this.cipherText = cipherText;
        this.salt = salt;
        this.iv = iv;

        this.hasIv = iv != null;
    }

    public EncryptedData(byte[] cipherText, byte[] salt) {
        this(cipherText, salt, null);
    }

    public byte[] getCipherText() {
        return cipherText;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public boolean hasIv() {
        return hasIv;
    }
}
