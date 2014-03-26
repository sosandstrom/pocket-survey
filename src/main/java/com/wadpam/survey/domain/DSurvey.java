package com.wadpam.survey.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class DSurvey extends AbstractLongEntity {
    
    /** Launched, In design, Closed, Deleted */
    @Basic
    private Long state;
    
    /** The title of this survey */
    @Basic
    private String title;

    /** A description of this survey */
    @Basic
    private String description;

    /** Application-specific attribute */
    @Basic
    private String appArg0;

    public String getAppArg0() {
        return appArg0;
    }

    public void setAppArg0(String appArg0) {
        this.appArg0 = appArg0;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
