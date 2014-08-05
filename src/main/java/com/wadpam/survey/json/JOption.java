package com.wadpam.survey.json;

import com.wadpam.open.json.JBaseObject;


/**
 *
 * @author os
 */
public class JOption extends JBaseObject {
    
    /** The question this answer is for */
    private Long questionId;

    /** The survey this answer is for */
    private Long surveyId;

    /** The survey version this answer is for */
    private Long versionId;

    /** The text label for this option*/
    private String label;

    /** An optional order of this option among all other options for this survey/version combination */
    private Long ordering;

    /**
     * Is this option the default option for the survey/version combination.
     * If non of the options are marked as default value, the app should not apply any default value.
     * If more the one option is marked as default value, this is considered an error and the result is undefined.
     */
    private Boolean defaultOption;
    
    /** Application-specific attribute */
    private String appArg0;

    public String getAppArg0() {
        return appArg0;
    }

    public void setAppArg0(String appArg0) {
        this.appArg0 = appArg0;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getOrdering() {
        return ordering;
    }

    public void setOrdering(Long ordering) {
        this.ordering = ordering;
    }

    public Boolean getDefaultOption() {
        return defaultOption;
    }

    public void setDefaultOption(Boolean defaultOption) {
        this.defaultOption = defaultOption;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
    
    @Override
    protected String subString() {
        return String.format("questionId:%d, label:%s", questionId, label);
    }

}
