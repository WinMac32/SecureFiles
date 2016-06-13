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
package ca.viaware.securefiles.model;

import java.util.ArrayList;
import java.util.Iterator;

public class SecurePackage {

    private ArrayList<SecureEntry> entries;

    public SecurePackage(ArrayList<SecureEntry> entries) {
        this.entries = entries;
    }

    public SecurePackage() {
        this(new ArrayList<SecureEntry>());
    }

    public void addEntry(SecureEntry entry) {
        this.entries.add(entry);
    }

    public void removeEntry(SecureEntry entry) {
        this.entries.remove(entry);
    }

    public ArrayList<SecureEntry> getEntries() {
        return entries;
    }

}
