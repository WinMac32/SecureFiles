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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class SecureFileEditor {

    private ArrayList<SecureEntry> entries;
    private SecureFile file;

    private ArrayList<Command> commands;
    private static HashMap<Class<? extends SecureEntry>, Class<? extends Editor>> editors = new HashMap<>();
    static {
        editors.put(SecureString.class, StringEditor.class);
    }

    private boolean running;

    public SecureFileEditor(SecureFile file) {
        this.entries = file.getSecurePackage().getEntries();
        this.file = file;

        this.commands = new ArrayList<>();
        initCommands();

        this.running = true;
    }


    private String[] usage = {
            "Edit ------- e <id>",
            "Delete ----- d <id>",
            "Rename ----- r <id> <new title>",
            "Quick view - c <id>",
            "New -------- n <type> <title>",
            "List types - l",
            "Set pass --- p",
            "Usage ------ u",
            "Save ------- s",
            "Quit ------- q"
    };

    private void initCommands() {
        commands.add(new Command("Edit", "e", 1) {
            @Override
            public void run(String[] args, String trailing) {
                int id = Integer.parseInt(args[0]);
                SecureEntry entry = entries.get(id);

                try {
                    editors.get(entry.getClass()).getConstructor(entry.getClass()).newInstance(entry);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        commands.add(new Command("Delete", "d", 1) {
            @Override
            public void run(String[] args, String trailing) {
                entries.remove(Integer.parseInt(args[0]));
            }
        });

        commands.add(new Command("Rename", "r", 1) {
            @Override
            public void run(String[] args, String trailing) {
                if (trailing == null || trailing.length() <= 0) return;
                SecureEntry entry = entries.get(Integer.parseInt(args[0]));
                entry.setTitle(trailing);
            }
        });

        commands.add(new Command("New", "n", 1) {
            @Override
            public void run(String[] args, String trailing) {
                if (trailing == null || trailing.length() <= 0) return;
                SecureEntry entry = SecureEntry.initialize(args[0]);
                if (entry == null) {
                    System.out.println("Unknown entry type: " + args[0]);
                    return;
                }
                entry.setTitle(trailing);
                entries.add(entry);
            }
        });

        commands.add(new Command("List types", "l", 0) {
            @Override
            public void run(String[] args, String trailing) {
                String[] types = SecureEntry.getTypes();
                String line = "Available types: ";
                for (String t : types) line += t + ", ";
                System.out.println(line.substring(0, line.length() - 2));
            }
        });

        commands.add(new Command("Set pass", "p", 0) {
            @Override
            public void run(String[] args, String trailing) {
                char[] pass = Utils.getPassword();
                file.setPassword(pass);
                System.out.println("Password changed, remember to save!");
            }
        });

        commands.add(new Command("Usage", "u", 0) {
            @Override
            public void run(String[] args, String trailing) {
                usage();
            }
        });

        commands.add(new Command("Save", "s", 0) {
            @Override
            public void run(String[] args, String trailing) {
                if (file.save()) {
                    System.out.println("Saved changes to disk");
                } else {
                    System.out.println("Could not save changes!");
                }
            }
        });

        commands.add(new Command("Quit", "q", 0) {
            @Override
            public void run(String[] args, String trailing) {
                running = false;
            }
        });
    }

    private void usage() {
        for (String s : usage) {
            System.out.println(s);
        }
    }

    public void run() {
        usage();

        while (running) {
            System.out.println("-------- ENTRIES --------");
            for (int i = 0; i < entries.size(); i++) {
                System.out.println(i + " -> " + entries.get(i).getTitle());
            }

            String cmd = Utils.getInput("CMD>");
            String[] cmdParts = cmd.split("[ ]", 2);
            if (cmdParts.length > 0) {
                cmd = cmdParts[0];

                commands:
                for (Command c : commands) {
                    if (c.getCommand().equalsIgnoreCase(cmd)) {
                        String[] args = new String[c.getArgLength()];
                        if (c.getArgLength() > 0) {
                            if (cmdParts.length < 2) break;
                            String left = cmdParts[1];
                            for (int i = 0; i < c.getArgLength(); i++) {
                                if (left == null || left.length() <= 0) {
                                    System.out.println("Insufficient arguments to command: " + cmd);
                                    break commands;
                                }
                                String[] aParts = left.split("[ ]", 2);
                                args[i] = aParts[0];
                                left = aParts.length > 1 ? aParts[1] : null;
                            }
                            c.run(args, left);
                        } else {
                            c.run(args, cmdParts.length > 1 ? cmdParts[1] : null);
                        }
                        break;
                    }
                }
            }
        }
    }

    abstract class Command {

        private String name;
        private String command;
        private int argLength;

        public Command(String name, String command, int argLength) {
            this.name = name;
            this.command = command;
            this.argLength = argLength;
        }

        public String getName() {
            return name;
        }

        public String getCommand() {
            return command;
        }

        public int getArgLength() {
            return argLength;
        }

        public abstract void run(String[] args, String trailing);
    }

}
