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

import ca.viaware.securefiles.model.SecureEntry;
import ca.viaware.securefiles.model.SecurePackage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Scanner;

public class Serializer {

    public byte[] serialize(SecurePackage securePackage) {
        JSONObject root = new JSONObject();

        JSONArray entries = new JSONArray();
        for (SecureEntry e : securePackage.getEntries()) {
            JSONObject entry = new JSONObject();
            entry.put("title", e.getTitle());
            entry.put("type", SecureEntry.getId(e));
            entry.put("payload", e.save());
            entries.put(entry);
        }

        root.put("entries", entries);

        String json = root.toString();
        //System.out.println(json);
        try {
            return json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json.getBytes();
    }

    public SecurePackage deserialize(ByteArrayInputStream data) {
        String json = new Scanner(data).useDelimiter("\\Z").next();
        //System.out.println(json);
        JSONObject root = new JSONObject(json);

        SecurePackage securePackage = new SecurePackage();

        JSONArray entries = root.getJSONArray("entries");
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            String type = entry.getString("type");
            SecureEntry secureEntry = SecureEntry.initialize(type);
            if (secureEntry != null) {
                secureEntry.setTitle(entry.getString("title"));
                if (entry.has("payload")) {
                    secureEntry.load(entry.getString("payload"));
                }
                securePackage.addEntry(secureEntry);
            }
        }

        return securePackage;
    }
}
