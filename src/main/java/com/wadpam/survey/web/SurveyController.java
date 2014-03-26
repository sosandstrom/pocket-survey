package com.wadpam.survey.web;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wadpam.survey.domain.DResponse;
import com.wadpam.survey.json.JResponse;
import com.wadpam.survey.service.ResponseService;
import net.sf.mardao.core.CursorPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JSurvey;
import com.wadpam.survey.json.JVersion;
import com.wadpam.survey.service.SurveyCrudService;
import com.wadpam.survey.service.SurveyService;

/**
 *
 * @author os
 */
@Controller
@RequestMapping("{domain}/survey")
public class SurveyController extends CrudController<JSurvey, DSurvey, Long, SurveyCrudService> {
    public static final int    ERR_SURVEY_GET_NOT_FOUND = SurveyService.ERR_SURVEY + 1;
    public static final int    ERR_CREATE_CONFLICT      = SurveyService.ERR_SURVEY + 2;

    public static final String NAME_INNER_VERSIONS = "versions";
    public static final String NAME_INNER_PAGESIZE = "innerPageSize";

    private VersionController versionController;

    private ResponseController responseController;


    /**
     * Deep-clone a survey version.
     * @param surveyId
     * @param fromVersionId the version we would like to clone
     * @param description Description of the new version
     * @return the new JVersion object
     */
    @RestReturn(value=JVersion.class, code={
        @RestCode(code=200, description="Version successfully cloned.")
    })
    @RequestMapping(value="v10/{surveyId}", method= RequestMethod.POST, params={"fromVersionId"})
    public JVersion cloneVersion(
            @PathVariable Long surveyId,
            @RequestParam Long fromVersionId,
            @RequestParam String description) {
        DVersion to = service.cloneVersion(surveyId, fromVersionId, description);
        return versionController.convertDomain(to);
    }

    /**
     * Get all existing responses for a particular meeting id
     * @param request the incoming request
     * @param response response
     * @param domain domain name
     * @param extMeetingId external meeting identity
     * @param pageSize the number of responses to return, default 10
     * @param cursorKey optional cursor key
     * @param model model
     * @return all responses for the given external meeting id
     */
    @ResponseBody
    @RequestMapping(value="v10/response", method= RequestMethod.GET, params={"extMeetingId"})
    public JCursorPage<JResponse> getAllResponsesForExternalMeeting(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestParam(required=true) String extMeetingId,
            @RequestParam(defaultValue="10") int pageSize,
            @RequestParam(required=false) String cursorKey,
            Model model) {

        LOG.debug("Get all responses for external meeting id {}", extMeetingId);

        JCursorPage<JResponse> responsePage = responseController.getPageByExtMeetingId(
                request, response, domain, extMeetingId, pageSize, cursorKey, model);
        return responsePage;
    }

    @Override
    public void addInnerObjects(HttpServletRequest request, HttpServletResponse response, 
            String domain, Model model, Iterable<JSurvey> jEntities) {
        for (JSurvey jSurvey : jEntities) {
            LOG.debug("addInnerObjects for {}...", jSurvey);
            if (null != jSurvey && 
                    (null != request.getParameter(NAME_INNER_VERSIONS) || 
                     null != request.getAttribute(NAME_INNER_VERSIONS))) {
                // add versions
                Long surveyId = Long.parseLong(jSurvey.getId());
                model.addAttribute("surveyId", surveyId);

                // default inner page size: 50
                int innerPageSize = 50;
                if (null != request.getParameter(NAME_INNER_PAGESIZE)) {
                    innerPageSize = Integer.parseInt(request.getParameter(NAME_INNER_PAGESIZE));
                }

                final JCursorPage<JVersion> versions = versionController.getPage(request, 
                        response, domain, model, innerPageSize, null);
                LOG.debug("found versions {}", versions.getItems());
                jSurvey.setVersions(versions.getItems());
            }
        }
    }

    @Override
    protected Collection<String> getInnerParameterNames() {
        return Arrays.asList(NAME_INNER_VERSIONS);
    }
    
    // ----------------------- Converter and setters ---------------------------

    public SurveyController() {
        super(JSurvey.class);
    }
    
    @Override
    public void convertDomain(DSurvey from, JSurvey to) {
        convertLongEntity(from, to);
        
        to.setAppArg0(from.getAppArg0());
        to.setState(from.getState());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
    }

    @Override
    public void convertJson(JSurvey from, DSurvey to) {
        convertJLong(from, to);

        to.setAppArg0(from.getAppArg0());
        to.setState(from.getState());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
    }

    @Autowired
    public void setSurveyCrudService(SurveyCrudService surveyCrudService) {
        this.service = surveyCrudService;
    }

    @Autowired
    public void setVersionController(VersionController versionController) {
        this.versionController = versionController;
    }

    @Autowired
    public void setResponseController(ResponseController responseController) {
        this.responseController = responseController;
    }

}
