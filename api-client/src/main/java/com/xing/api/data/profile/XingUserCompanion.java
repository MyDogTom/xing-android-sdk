package com.xing.api.data.profile;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class XingUserCompanion {

    Set<String> fields = new LinkedHashSet<>();

    public void requestAcademicTitle(boolean request) {
        if (request) {
            fields.add("academic_title");
        } else {
            fields.remove("academic_title");
        }
    }

    public String getFields() {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> it = fields.iterator(); it.hasNext();){
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(it.next());
        }
        return builder.toString();

    }

}
