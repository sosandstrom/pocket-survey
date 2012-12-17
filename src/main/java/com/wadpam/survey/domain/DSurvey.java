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
    
    
}