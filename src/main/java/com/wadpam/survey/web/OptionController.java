/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.survey.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.survey.domain.DOption;
import com.wadpam.survey.domain.DQuestion;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JOption;
import com.wadpam.survey.service.OptionService;
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
@RequestMapping("{domain}/survey/v10/{surveyId}/version/v10/{versionId}/question/v10/{questionId}/option")
public class OptionController extends CrudController<JOption, 
        DOption, 
        Long, 
        OptionService> {
    public static final int ERR_GET_NOT_FOUND    = SurveyService.ERR_OPTION + 1;
    public static final int ERR_CREATE_NOT_FOUND = SurveyService.ERR_OPTION + 2;
    public static final int ERR_CREATE_CONFLICT  = SurveyService.ERR_OPTION + 3;

    protected SurveyService surveyService;

    @ModelAttribute("surveyId")
    public Long addSurveyId(@PathVariable Long surveyId) {
        return surveyId;
    }
    
    @ModelAttribute("versionId")
    public Long addVersionId(@PathVariable Long versionId) {
        return versionId;
    }
    
    @ModelAttribute("questionId")
    public Long addQuestionId(@PathVariable Long questionId) {
        return questionId;
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
    public JCursorPage<JOption> getPage(HttpServletRequest request, 
            HttpServletResponse response,
            @PathVariable String domain, 
            Model model, 
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) String cursorKey) {
        Long questionId = (Long) model.asMap().get("questionId");
        CursorPage<DOption, Long> page = surveyService.getOptionsPage(questionId, pageSize, cursorKey);
        return convertPage(page);
    }

    // ----------------------------- Converter and setters ---------------------

    public OptionController() {
        super(JOption.class);
    }
    
    @Override
    public void convertDomain(DOption from, JOption to) {
        convertLongEntity(from, to);
        
        to.setAppArg0(from.getAppArg0());
        to.setLabel(from.getLabel());
        to.setQuestionId(null != from.getQuestion() ? from.getQuestion().getId() : null);
        to.setSurveyId(null != from.getSurvey() ? from.getSurvey().getId() : null);
        to.setVersionId(null != from.getVersion() ? from.getVersion().getId() : null);
    }

    @Override
    public void convertJson(JOption from, DOption to) {
        convertJLong(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setLabel(from.getLabel());
        if (null != from.getQuestionId()) {
            final DQuestion foreign = new DQuestion();
            foreign.setId(from.getQuestionId());
            to.setQuestion(foreign);
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
    public void setOptionService(OptionService optionService) {
        this.service = optionService;
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    
}
