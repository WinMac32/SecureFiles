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

import ca.viaware.securefiles.model.entry.SecureString;
import java.util.HashMap;
import java.util.Map;

public abstract class SecureEntry {

    private String title;

    private static HashMap<String, Class<? extends SecureEntry>> entryTypes = new HashMap<String, Class<? extends SecureEntry>>();
    static {
        entryTypes.put("string", SecureString.class);
    }

    public SecureEntry() {
        this("New Entry");
    }

    public SecureEntry(String title) {
        this.title = title;
    }

    public static String getId(SecureEntry entry) {
        for (Map.Entry<String, Class<? extends SecureEntry>> e : entryTypes.entrySet()) {
            if (e.getValue().isInstance(entry)) return e.getKey();
        }
        return "unknown";
    }

    public static SecureEntry initialize(String type) {
        try {
            return entryTypes.get(type).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract void load(String payload);
    public abstract String save();

}
