package com.wadpam.survey.web;

import com.wadpam.survey.service.SurveyService;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.survey.domain.DAnswer;
import com.wadpam.survey.json.JAnswer;
import java.io.Serializable;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import net.sf.mardao.core.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author os
 */
@RestReturn(value=JAnswer.class)
@Controller
@RequestMapping("{domain}/survey/v10/{surveyId}/version/v10/{versionId}/response/v10/{responseId}/answer")
public class AnswerController {
    public static final int ERR_GET_NOT_FOUND = SurveyService.ERR_ANSWER + 1;
    public static final int ERR_CREATE_NOT_FOUND = SurveyService.ERR_ANSWER + 2;
    public static final int ERR_CREATE_CONFLICT = SurveyService.ERR_ANSWER + 3;
    
    public static final String NAME_LOCATION = "Location";
    public static final String NAME_X_REQUESTED_WITH = "X-Requested-With";
    public static final String VALUE_X_REQUESTED_WITH_AJAX = "XMLHttpRequest";
    
    static final Logger LOG = LoggerFactory.getLogger(AnswerController.class);
    
    static final Converter CONVERTER = new Converter();
    
    private SurveyService service;
    
    /**
     * Creates an entity.
     * @return a redirect to the created entity
     */
    @RestReturn(value=URL.class, code={
        @RestCode(code=201, description="The entity was created by AJAX", message="Created"),
        @RestCode(code=302, description="The entity was created", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.POST)
    public RedirectView create(
                        @RequestHeader(value=AnswerController.NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable Long surveyId,
            @PathVariable Long versionId,
            @PathVariable Long responseId,
            @ModelAttribute JAnswer jAnswer,
            @RequestParam(required=false) String[] answers
            ) {
        
        if (null == jAnswer.getQuestionId() || jAnswer.getQuestionId() < 1L) {
            throw new IllegalArgumentException("Invalid questionId " + jAnswer.getQuestionId());
        }
        
        final DAnswer dEntity = service.createAnswer(Converter.convert(jAnswer));

        // AJAX request? Respond with 201 Created + Location header.
        if (AnswerController.VALUE_X_REQUESTED_WITH_AJAX.equals(xRequestedWith)) {
            response.setStatus(HttpStatus.CREATED.value());
            final String path = String.format("v10/%d", 
                    dEntity.getId());
            response.addHeader(NAME_LOCATION, path);
            return null;
        }
        
        final String relative = String.format("v10/%d", dEntity.getId());
        final RedirectView returnValue = new RedirectView(relative, true);
        return returnValue;
    }
    
    /**
     * Loads the specified entity.
     * @param domain the domain (used for Multi-tenancy)
     * @param surveyId the survey's id
     * @param responseId the response's id
     * @param id the id of the entity to retrieve
     * @return the loaded JSON object
     */
    @RestReturn(value=JAnswer.class, code={
        @RestCode(code=200, description="The entity was found", message="OK"),
        @RestCode(code=404, description="The entity was not found", message="Not Found")})
    @RequestMapping(value="v10/{id}", method= RequestMethod.GET)
    @ResponseBody
    public JAnswer get(
            @PathVariable String domain,
            @PathVariable Long surveyId,
            @PathVariable Long versionId,
            @PathVariable Long responseId,
            @PathVariable Long id) {
        final DAnswer entity = service.getAnswer(id);
        if (null == entity) {
            throw new NotFoundException(ERR_GET_NOT_FOUND, 
                    "Not a server error, perhaps a client one",
                    null, 
                    String.format("There is no Entity with id %d", id));
        }
        final JAnswer body = Converter.convert(entity);
        
        return body;
    }
    
    /**
     * Queries for a (next) page of entities
     * @param pageSize default is 10
     * @param cursorKey null to get first page
     * @return a page of entities
     */
    @RestReturn(value=JCursorPage.class, entity=JAnswer.class, code={
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public JCursorPage<JAnswer> getPage(
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) Serializable cursorKey) {
        final CursorPage<DAnswer, Long> page = service.getAnswersPage(pageSize, cursorKey);
        final JCursorPage body = CONVERTER.convertPage(page);

        return body;
    }
    
    /**
     * Updates an entity.
     * @param id the id of the entity to update
     * @param jEntity the JSON object for this updated entity
     * @return a redirect to the updated entity
     */
    @RestReturn(value=URL.class, code={
        @RestCode(code=204, description="The entity was updated by AJAX", message="No Content"),
        @RestCode(code=302, description="The entity was updated", message="OK")})
    @RequestMapping(value="v10/{id}", method= RequestMethod.POST)
    public RedirectView update(
            @RequestHeader(value=AnswerController.NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            HttpServletResponse response,
            @PathVariable Long id,
            @ModelAttribute JAnswer jEntity
            ) {
        
        final DAnswer dEntity = service.upsertAnswer(Converter.convert(jEntity), null);
        
        // AJAX request? Respond with 204 No Content.
        if (AnswerController.VALUE_X_REQUESTED_WITH_AJAX.equals(xRequestedWith)) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        
        final String relative = String.format("%d", dEntity.getId());
        final RedirectView returnValue = new RedirectView(relative, true);
        return returnValue;
    }

    public void setService(SurveyService service) {
        this.service = service;
    }

}