/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.survey.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.survey.domain.DAnswer;
import com.wadpam.survey.domain.DQuestion;
import com.wadpam.survey.domain.DResponse;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JAnswer;
import com.wadpam.survey.service.AnswerService;
import com.wadpam.survey.service.SurveyService;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.mardao.core.CursorPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping("{domain}/survey/v10/{surveyId}/version/v10/{versionId}/response/v10/{responseId}/answer")
public class AnswerController extends CrudController<JAnswer, 
        DAnswer, 
        Long, 
        AnswerService> {
    public static final int ERR_GET_NOT_FOUND    = SurveyService.ERR_ANSWER + 1;
    public static final int ERR_CREATE_NOT_FOUND = SurveyService.ERR_ANSWER + 2;
    public static final int ERR_CREATE_CONFLICT  = SurveyService.ERR_ANSWER + 3;

    protected SurveyService surveyService;

    @ModelAttribute("surveyId")
    public Long addSurveyId(@PathVariable Long surveyId) {
        return surveyId;
    }
    
    @ModelAttribute("versionId")
    public Long addVersionId(@PathVariable Long versionId) {
        return versionId;
    }
    
    @ModelAttribute("responseId")
    public Long addResponseId(@PathVariable Long responseId) {
        return responseId;
    }
    
    /**
     * Queries for a (next) page of entities
     * @param pageSize default is 10
     * @param cursorKey null to get first page
     * @return a page of entities
     */
    @Override
    @RestReturn(value=JCursorPage.class, code={
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public JCursorPage<JAnswer> getPage(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) String cursorKey) {
        Long responseId = (Long) model.asMap().get("responseId");
        final CursorPage<DAnswer, Long> page = surveyService.getAnswersPage(responseId, pageSize, cursorKey);
        final JCursorPage<JAnswer> body = convertPage(page);

        return body;
    }
    
    // ------------------------- Converter and setters -------------------------

    public AnswerController() {
        super(JAnswer.class);
    }

    @Override
    public void convertDomain(DAnswer from, JAnswer to) {
        convertLongEntity(from, to);
        
        to.setAnswer(from.getAnswer());
        to.setQuestionId(null != from.getQuestion() ? from.getQuestion().getId() : null);
        to.setResponseId(null != from.getResponse() ? from.getResponse().getId() : null);
        to.setSurveyId(null != from.getSurvey() ? from.getSurvey().getId() : null);
        to.setVersionId(null != from.getVersion() ? from.getVersion().getId() : null);
    }

    @Override
    public void convertJson(JAnswer from, DAnswer to) {
        convertJLong(from, to);

        to.setAnswer(from.getAnswer());
        if (null != from.getQuestionId()) {
            final DQuestion foreign = new DQuestion();
            foreign.setId(from.getQuestionId());
            to.setQuestion(foreign);
        }
        if (null != from.getResponseId()) {
            final DResponse foreign = new DResponse();
            foreign.setId(from.getResponseId());
            to.setResponse(foreign);
        }
        if (null != from.getSurveyId()) {
            final DSurvey foreign = new DSurvey();
            foreign.setId(from.getSurveyId());
            to.setSurvey(foreign);
        }
        if (null != from.getVersionId()) {
            final DVersion version = new DVersion();
            version.setId(from.getVersionId());
            to.setVersion(version);
        }
    }

    @Autowired
    public void setAnswerService(AnswerService answerService) {
        this.service = answerService;
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
}
