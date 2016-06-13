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
package ca.viaware.securefiles.editor;

import ca.viaware.securefiles.Utils;
import ca.viaware.securefiles.model.SecureEntry;
import ca.viaware.securefiles.model.entry.SecureString;
import ca.viaware.securefiles.persistence.SecureFile;

import java.util.ArrayList;

public class SecureFileEditor {

    private ArrayList<SecureEntry> entries;
    private SecureFile file;

    public SecureFileEditor(SecureFile file) {
        this.entries = file.getSecurePackage().getEntries();
        this.file = file;
    }

    private String[] usage = {
            "Edit ------- e <id>",
            "Delete ----- d <id>",
            "Rename ----- r <id> <new title>",
            "Quick view - c <id>",
            "New -------- n <type[String (s)]> <title>",
            "Set pass --- p",
            "Usage ------ u",
            "Save ------- s",
            "Quit ------- q"
    };

    void usage() {
        for (String s : usage) {
            System.out.println(s);
        }
    }

    public void run() {
        usage();

        while (true) {
            System.out.println("-------- ENTRIES --------");
            for (int i = 0; i < entries.size(); i++) {
                System.out.println(i + " -> " + entries.get(i).getTitle());
            }

            String cmd = Utils.getInput("CMD>");
            String[] cmdParts = cmd.split("[ ]", 2);
            if (cmdParts.length > 0) {
                cmd = cmdParts[0];
                if (cmdParts.length > 1) {
                    String[] argParts = cmdParts[1].split("[ ]", 2);

                    if (cmd.equalsIgnoreCase("n")) {
                        if (argParts.length > 1) {
                            if (argParts[0].equalsIgnoreCase("s")) {
                                SecureString string = new SecureString();
                                string.setTitle(argParts[1]);
                                entries.add(string);
                            } else {
                                System.out.println("Unknown entry type '" + argParts[0] + "'");
                            }
                        } else {
                            System.out.println("Insufficient arguments");
                        }
                    } else {
                        int id = Integer.parseInt(argParts[0]);
                        SecureEntry entry = entries.get(id);

                        if (cmd.equalsIgnoreCase("e")) {
                            if (entry instanceof SecureString) {
                                new StringEditor((SecureString) entry);
                            }
                        } else if (cmd.equalsIgnoreCase("d")) {
                            entries.remove(entry);
                        } else if (cmd.equalsIgnoreCase("r")) {
                            if (argParts.length > 1) {
                                entry.setTitle(argParts[1]);
                            } else {
                                System.out.println("A new name must be supplied to this operation");
                            }
                        } else if (cmd.equalsIgnoreCase("c")) {
                            if (entry instanceof SecureString) {
                                System.out.println(((SecureString) entry).getString());
                            }
                        } else {
                            System.out.println("Unknown operation '" + cmd + "'");
                        }
                    }
                } else if (cmd.equalsIgnoreCase("u")) {
                    usage();
                } else if (cmd.equalsIgnoreCase("s")) {
                    if (file.save()) {
                        System.out.println("Saved changes to disk");
                    } else {
                        System.out.println("Could not save to disk!!");
                    }
                } else if (cmd.equalsIgnoreCase("p")) {
                    char[] pass = Utils.getPassword();
                    file.setPassword(pass);
                    System.out.println("Password changed, remember to save!");
                } else if (cmd.equalsIgnoreCase("q")) {
                    break;
                } else {
                    System.out.println("Insufficient arguments to operation '" + cmd + "'");
                }
            }
        }
    }

}
