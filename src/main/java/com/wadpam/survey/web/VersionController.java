package com.wadpam.survey.web;

import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JVersion;
import com.wadpam.survey.service.SurveyService;
import com.wadpam.survey.service.VersionService;
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
 * @author os
 */
@Controller
@RequestMapping("{domain}/survey/{surveyVersion}/{surveyId}/version")
public class VersionController extends CrudController<JVersion, DVersion, Long, VersionService> {
    
    private SurveyService surveyService;
    
    @ModelAttribute(value="surveyId")
    public Long addSurveyId(@PathVariable Long surveyId) {
        return surveyId;
    }

    @Override
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public JCursorPage<JVersion> getPage(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) String cursorKey) {

        Long surveyId = (Long) model.asMap().get("surveyId");
        final CursorPage<DVersion, Long> page = surveyService.getVersionsPage(surveyId, pageSize, cursorKey);
        final JCursorPage body = convertPage(page);

        return body;
    }

    // --------------------- Converter and setters -----------------------------

    public VersionController() {
        super(JVersion.class);
    }

    @Override
    public void convertDomain(DVersion from, JVersion to) {
        convertLongEntity(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setDescription(from.getDescription());
        to.setState(from.getState());
        to.setSurveyId(null != from.getSurvey() ? from.getSurvey().getId() : null);
    }

    @Override
    public void convertJson(JVersion from, DVersion to) {
        convertJLong(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setDescription(from.getDescription());
        to.setState(from.getState());
        if (null != from.getSurveyId()) {
            final DSurvey survey = new DSurvey();
            survey.setId(from.getSurveyId());
            to.setSurvey(survey);
        }
    }
    
    @Autowired
    public void setVersionService(VersionService versionService) {
        this.service = versionService;
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
}
