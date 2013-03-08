/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.survey.web;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

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

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.survey.domain.DQuestion;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JOption;
import com.wadpam.survey.json.JQuestion;
import com.wadpam.survey.service.QuestionService;
import com.wadpam.survey.service.SurveyService;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping("{domain}/survey/v10/{surveyId}/version/v10/{versionId}/question")
public class QuestionController extends CrudController<JQuestion, 
        DQuestion, 
        Long, 
        QuestionService> {
    public static final int    ERR_GET_NOT_FOUND    = SurveyService.ERR_QUESTION + 1;
    public static final int    ERR_CREATE_NOT_FOUND = SurveyService.ERR_QUESTION + 2;
    public static final int    ERR_CREATE_CONFLICT  = SurveyService.ERR_QUESTION + 3;

    public static final String NAME_INNER_OPTIONS = "options";

    protected SurveyService surveyService;
    private OptionController optionController;

    @Override
    public void addInnerObjects(HttpServletRequest request, HttpServletResponse response,
            String domain, Model model, Iterable<JQuestion> jEntities) {
        if (null != jEntities
                && (null != request.getParameter(NAME_INNER_OPTIONS) || null != request.getAttribute(NAME_INNER_OPTIONS))) {
            for(JQuestion jEntity : jEntities) {
                // add options
                Long outerId = Long.parseLong(jEntity.getId());
                model.addAttribute("questionId", outerId);
                final JCursorPage<JOption> inners = optionController.getPage(request, response,
                        domain, model, 1000, null);
                LOG.debug("found inners {}", inners.getItems());
                jEntity.setOptions(inners.getItems());
            }
        }
    }

    @ModelAttribute("surveyId")
    public Long addSurveyId(@PathVariable Long surveyId) {
        return surveyId;
    }
    
    @ModelAttribute("versionId")
    public Long addVersionId(@PathVariable Long versionId) {
        return versionId;
    }

    @Override
    protected Collection<String> getInnerParameterNames() {
        return Arrays.asList(NAME_INNER_OPTIONS);
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
    public JCursorPage<JQuestion> getPage(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain, Model model,
            @RequestParam(defaultValue="10") int pageSize,
            @RequestParam(required=false) String cursorKey) {

        Long versionId = (Long) model.asMap().get("versionId");
        CursorPage<DQuestion, Long> page = surveyService.getQuestionsPage(versionId,
                pageSize, cursorKey);
        return convertPageWithInner(request, response, domain, model, page);
    }

    // ---------------- Converter and setters ------------------------------

    public QuestionController() {
        super(JQuestion.class);
    }
    
    @Override
    public void convertDomain(DQuestion from, JQuestion to) {
        convertLongEntity(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setOrdering(from.getOrdering());
        to.setQuestion(from.getQuestion());
        to.setRequired(from.getRequired());
        to.setType(from.getType());
        to.setSurveyId(null != from.getSurvey() ? from.getSurvey().getId() : null);
        to.setVersionId(null != from.getVersion() ? from.getVersion().getId() : null);
    }

    @Override
    public void convertJson(JQuestion from, DQuestion to) {
        convertJLong(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setOrdering(from.getOrdering());
        to.setQuestion(from.getQuestion());
        to.setRequired(from.getRequired());
        to.setType(from.getType());
        if (null != from.getSurveyId()) {
            final DSurvey survey = new DSurvey();
            survey.setId(from.getSurveyId());
            to.setSurvey(survey);
        }
        if (null != from.getVersionId()) {
            final DVersion version = new DVersion();
            version.setId(from.getVersionId());
            to.setVersion(version);
        }
    }

    @Autowired
    public void setQuestionService(QuestionService questionService) {
        this.service = questionService;
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @Autowired
    public void setOptionController(OptionController optionController) {
        this.optionController = optionController;
    }
}
