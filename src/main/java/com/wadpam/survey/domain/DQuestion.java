package com.wadpam.survey.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import net.sf.mardao.core.domain.AbstractLongEntity;

import java.util.Collection;

/**
 *
 * @author os
 */
@Entity
public class DQuestion extends AbstractLongEntity {
    
    /** The order of this question within the Survey */
    @Basic
    private Long ordering;

    /** The default localization of the question */
    @Basic
    private String question;
    
    /** Indicates if an answer is required: REQUIRED, OPTIONAL, REMINDER. */
    @Basic
    private Long required;
    
    /** The answer type: TEXT, NUMBER, IMAGE, ... */
    @Basic
    private Long type;

    /**
     * This availability of this question depends on a list of other questions
     * It should only be enabled once all of these questions have received valid input from the users.
     */
    @Basic
    private Collection<Long> dependsOnQuestions;

    /** The survey this question is for */
    @ManyToOne
    private DSurvey survey;
    
    /** The survey version this question is for */
    @ManyToOne
    private DVersion version;
    
    /** Application-specific attribute */
    @Basic
    private String appArg0;

    public String getAppArg0() {
        return appArg0;
    }

    public void setAppArg0(String appArg0) {
        this.appArg0 = appArg0;
    }

    public Long getOrdering() {
        return ordering;
    }

    public void setOrdering(Long order) {
        this.ordering = order;
    }
    
    public DSurvey getSurvey() {
        return survey;
    }

    public void setSurvey(DSurvey survey) {
        this.survey = survey;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getRequired() {
        return required;
    }

    public void setRequired(Long required) {
        this.required = required;
    }

    public DVersion getVersion() {
        return version;
    }

    public void setVersion(DVersion version) {
        this.version = version;
    }

    public Collection<Long> getDependsOnQuestions() {
        return dependsOnQuestions;
    }

    public void setDependsOnQuestions(Collection<Long> dependsOnQuestions) {
        this.dependsOnQuestions = dependsOnQuestions;
    }
}
