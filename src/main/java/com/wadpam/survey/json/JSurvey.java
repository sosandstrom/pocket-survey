package com.wadpam.survey.json;

import com.wadpam.open.json.JBaseObject;
import java.util.Collection;

/**
 *
 * @author os
 */
public class JSurvey extends JBaseObject {

    public static final long STATE_DELETED = -1;
    public static final long STATE_IN_DESIGN = 0;
    public static final long STATE_ACTIVE = 1;
    public static final long STATE_CLOSED = 2;
    
    /** The title of this survey */
    private String title;

    /** A description of this survey */
    private String description;
    
    /** The Versions configured for this survey */
    private Collection<JVersion> versions;

    /** Application-specific attribute */
    private String appArg0;

    public String getAppArg0() {
        return appArg0;
    }

    public void setAppArg0(String appArg0) {
        this.appArg0 = appArg0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected String subString() {
        return String.format("title:%s", title);
    }

    public Collection<JVersion> getVersions() {
        return versions;
    }

    public void setVersions(Collection<JVersion> versions) {
        this.versions = versions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}