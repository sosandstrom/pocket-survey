package com.wadpam.survey.json;

import com.wadpam.open.json.JBaseObject;
import java.util.Collection;

/**
 *
 * @author os
 */
public class JQuestion extends JBaseObject {

    /** An answer is optional */
    public static final long REQUIRED_OPTIONAL = 0;
    
    /** An answer is required */
    public static final long REQUIRED_REQUIRED = 1;
    
    /** Remind user if not answered */
    public static final long REQUIRED_REMINDER = 2;
    
    /**
     * Free-text input.
     * Normally not validation should be done by the app.
     */
    public static final long TYPE_TEXT = 1;
    
    /**
     * Numeric input
     * App should validate that the user enter a number.
     */
    public static final long TYPE_NUMBER = 2;
    
    /**
     * Percentage input.
     * The app should only allow number input within the range 0-100
     */
    public static final long TYPE_PERCENT = 3;
    
    /**
     * Checkbox input
     * true or false (checkbox) input
     */
    public static final long TYPE_BOOLEAN = 4;
    
    /**
     * Acquire an image with camera, and upload.
     * TODO Where to provide the url to the uploaded image?
     */
    public static final long TYPE_IMAGE = 5;
    
    /**
     * Any file to upload
     * TODO Where to provide the url to the uploaded file?
     */
    public static final long TYPE_FILE = 6;
    
    /**
     * Radio buttons (single select)
     * The options are provided as Options entities.
     */
    public static final long TYPE_RADIO = 7;
    
    /**
     * Multi-select input
     * The options are provided as Options entities.
     */
    public static final long TYPE_MULTI = 8;
    
    /**
     * Drop-down select input (single select
     * The options are provided as Options entities.
     */
    public static final long TYPE_DROPDOWN = 9;
    
    /**
     * Date (and time) input.
     * The app must check that the input is a valid Date
     * TODO How is this value provided, as timestamp or date string?
     */
    public static final long TYPE_DATE = 10;

    /**
     * This type acts as a section/group.
     * All questions until the next section type or the end of the survey should be given a grouped presentation.
     * The question string is optional and if provided should be presented as a header for the section.
     */
    public static final long TYPE_SECTION = 11;

    /**
     * Email input type.
     * The app must check that the input is a valid email
     */
    public static final long TYPE_EMAIL = 12;

    /**
     * Address input type.
     * The app can use this type to perform reverse geocoding and/or input validation.
     */
    public static final long TYPE_ADDRESS = 13;

    /**
     * Phone number input type
     * The app can use this type to support tap to call and/or input validation
     */
    public static final long TYPE_PHONE_NUMBER = 14;

    /**
     * This types acts as a text presentation only, e.g. to provide additional text and context to a survey.
     */
    public static final long TYPE_DESCRIPTION = 15;
    
    /** The survey this question is for */
    private Long surveyId;

    /** The version this question is for */
    private Long versionId;

    /** The order of this question within the Survey */
    private Long ordering;

    /** The default localization of the question */
    private String question;
    
    /** Indicates if an answer is required: REQUIRED, OPTIONAL, REMINDER. */
    private Long required;
    
    /** The answer type: TEXT, NUMBER, IMAGE, ... */
    private Long type;

    /**
     * This availability of this question depends on a list of other questions
     * It should only be enabled once all of these questions have received valid input from the users.
     */
    private Collection<Long> dependsOnQuestions;

    /** The options configured for this question */
    private Collection<JOption> options;
    
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

    public Long getOrdering() {
        return ordering;
    }

    public void setOrdering(Long order) {
        this.ordering = order;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getRequired() {
        return required;
    }

    public void setRequired(Long required) {
        this.required = required;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Collection<JOption> getOptions() {
        return options;
    }

    public void setOptions(Collection<JOption> options) {
        this.options = options;
    }

    @Override
    protected String subString() {
        return String.format("surveyId:%d, versionId:%d, order:%d, type:%d, question:%s", 
                surveyId, versionId, ordering, type, question);
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Collection<Long> getDependsOnQuestions() {
        return dependsOnQuestions;
    }

    public void setDependsOnQuestions(Collection<Long> dependsOnQuestions) {
        this.dependsOnQuestions = dependsOnQuestions;
    }
}
