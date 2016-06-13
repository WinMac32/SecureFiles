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
package ca.viaware.securefiles.persistence;

import ca.viaware.api.logging.Log;
import ca.viaware.securefiles.Utils;
import ca.viaware.securefiles.editor.SecureFileEditor;
import ca.viaware.securefiles.model.SecurePackage;
import ca.viaware.securefiles.security.Encryptor;
import ca.viaware.securefiles.security.InvalidPassException;

import java.io.*;

public class SecureFile {

    public static byte[] HEADER;
    static {
        try {
            HEADER = "!SECUREFILE!".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            HEADER = "!SECUREFILE!".getBytes();
        }
    }

    private File file;
    private SecurePackage securePackage;
    private Encryptor encryptor;

    public SecureFile(File file, char[] pass, boolean isNew) throws InvalidPassException {
        this.file = file;
        this.encryptor = new Encryptor(pass);
        if (isNew) securePackage = new SecurePackage();
        else securePackage = load(file);
    }

    public SecureFileEditor getEditor() {
        return new SecureFileEditor(this);
    }

    public SecurePackage getSecurePackage() {
        return securePackage;
    }

    public boolean save() {
        return save(securePackage, file);
    }

    private SecurePackage load(File file) throws InvalidPassException {
        Log.info("Loading %0...", file.getAbsolutePath());
        try {
            InputStream input = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            byte[] buffer = new byte[1024];
            int read, total = 0;
            while ((read = input.read(buffer)) != -1) {
                System.arraycopy(buffer, 0, bytes, total, read);
                total += read;
            }
            byte[] decrypted = encryptor.decrypt(bytes);
            if (decrypted == null) throw new InvalidPassException();
            ByteArrayInputStream data = new ByteArrayInputStream(decrypted);
            for (byte b : HEADER) {
                if (data.read() != b) throw new InvalidPassException();
            }
            Log.info("Done.");
            return new Serializer().deserialize(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.error("Unable to load!");
        return null;
    }

    private boolean save(SecurePackage securePackage, File file) {
        Log.info("Saving %0...", file.getAbsolutePath());
        try {
            byte[] serialized = new Serializer().serialize(securePackage);
            byte[] bytes = new byte[serialized.length + HEADER.length];
            System.arraycopy(HEADER, 0, bytes, 0, HEADER.length);
            System.arraycopy(serialized, 0, bytes, HEADER.length, serialized.length);
            byte[] encrypted = encryptor.encrypt(bytes);
            OutputStream out = new FileOutputStream(file);
            out.write(encrypted);
            out.close();
            Log.info("Done.");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.error("Save failed!");
        return false;
    }

    public void setPassword(char[] newPassword) {
        this.encryptor = new Encryptor(newPassword);
        Utils.clear(newPassword);
    }

}
