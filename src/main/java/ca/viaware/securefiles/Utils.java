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
package ca.viaware.securefiles;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

public class Utils {

    public static void printIndent(int level) {
        System.out.print(getIndent(level));
    }

    public static String getIndent(int level) {
        String s = "";
        for (int i = 0; i < level; i++) {
            s += "    ";
        }
        return s;
    }

    public static String getInput(String prompt) {
        System.out.print(prompt + " ");
        return new Scanner(System.in).nextLine();
    }

    public static String getInput() {
        return getInput(">");
    }

    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);
        return bytes;
    }

    private static String readLine(String format, Object... args) {
        System.out.print(String.format(format, args));
        return new Scanner(System.in).nextLine();
    }

    private static char[] readPassword(String format, Object... args) {
        if (System.console() != null) return System.console().readPassword(format, args);
        System.out.println("WARNING -> Console password input unsupported, password entry in insecure mode!");
        return readLine(format, args).toCharArray();
    }

    public static char[] getPassword(String prompt) {
        return readPassword(prompt + " ");
    }

    public static char[] getPassword() {
        return getPassword("PASS>");
    }

    public static void clear(char[]... pass) {
        for (char[] p : pass) {
            Arrays.fill(p, '0');
        }
    }

    public static void pipe(final InputStream in, final OutputStream out) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int read;
                try {
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void bufferedRead(byte[] dest, InputStream input) throws IOException {
        byte[] buffer = new byte[1024];
        int read, total = 0;

        while ((read = input.read(buffer, 0, dest.length - total)) != -1 && total < dest.length) {
            System.arraycopy(buffer, 0, dest, total, read);
            total += read;
        }
    }

    public static String toHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
