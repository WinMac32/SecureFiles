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

import ca.viaware.api.logging.Log;
import ca.viaware.securefiles.editor.SecureFileEditor;
import ca.viaware.securefiles.persistence.SecureFile;
import ca.viaware.securefiles.security.InvalidPassException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SecureFiles {

    private SecureFile login(File file) {
        try {
            char[] pass = Utils.getPassword();
            SecureFile secureFile = new SecureFile(file, pass, false);
            Utils.clear(pass);
            return secureFile;
        } catch (InvalidPassException e) {
            Log.error("Invalid key");
        }
        return login(file);
    }

    private SecureFile loadSecureFile() {
        String cmd = Utils.getInput("Load (l), New (n), Run (r), Quit (q): CMD>");

        if (cmd.equals("l")) {
            String fileName = Utils.getInput("FILE>");

            File file = new File(fileName);
            if (file.exists()) {
                return login(file);
            } else {
                Log.error("Cannot find specified secure binary file");
            }
        } else if (cmd.equals("n")) {
            String fileName = Utils.getInput("FILE>");
            char[] pass = Utils.getPassword("KEY>");
            char[] confirm = Utils.getPassword("CONFIRM>");
            if (Arrays.equals(pass, confirm)) {
                try {
                    SecureFile file = new SecureFile(new File(fileName), pass, true);
                    Utils.clear(pass, confirm);
                    return file;
                } catch (InvalidPassException e) {
                    e.printStackTrace();
                }
            } else {
                Log.error("Keys are not identical!");
            }
            Utils.clear(pass, confirm);
        } else if (cmd.equals("q")) {
            return null;
        } else if (cmd.equals("r")) {
            String runCmd = Utils.getInput("RUN>");
            try {
                Process p = Runtime.getRuntime().exec((System.getProperty("os.name").contains("Windows") ? "cmd /c " : "") + runCmd);
                Utils.pipe(p.getInputStream(), System.out);
                p.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.info("Unknown command!");
        }
        return loadSecureFile();
    }

    public void start() {
        Log.info("Starting ViaWare SecureFiles...");

        for (UIManager.LookAndFeelInfo look : UIManager.getInstalledLookAndFeels()) {
            if (look.getName().equalsIgnoreCase("nimbus")) {
                try {
                    UIManager.setLookAndFeel(look.getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
            }
        }

        while (true) {
            SecureFile file = loadSecureFile();
            if (file == null) break;
            SecureFileEditor editor = file.getEditor();
            editor.run();
        }
        Log.info("Goodbye.");
    }

    public static void main(String[] args) {
        new SecureFiles().start();
    }
}
