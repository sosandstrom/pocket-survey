/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.survey.service;

import net.sf.mardao.core.CursorPage;

import org.springframework.beans.factory.annotation.Autowired;

import com.wadpam.open.mvc.MardaoCrudService;
import com.wadpam.survey.dao.DResponseDao;
import com.wadpam.survey.domain.DResponse;

/**
 *
 * @author sosandstrom
 */
public class ResponseService extends MardaoCrudService<
        DResponse, 
        Long,
        DResponseDao> {

    protected SurveyService surveyService;

    public CursorPage<DResponse> getResponsesPageByExtMeetingId(String extMeetingId, int pageSize, String cursorKey) {
        final CursorPage<DResponse> page = dao.queryPageByExtMeetingId(extMeetingId, pageSize, cursorKey);
        return page;
    }

    public CursorPage<DResponse> getResponsesPageBySurveyIdVersionIdAndExtMeetingId(Long surveyId, Long versionId, String extMeetingId, int pageSize, String cursorKey) {
        return dao.queryPageBySurveyIdVersionIdAndExtMeetingId(surveyId, versionId, extMeetingId, pageSize, cursorKey);
    }

    public CursorPage<DResponse> getResponsesPageByVersionIdCreatedBy(Long versionId, String createdById, int pageSize, String cursorKey) {
        return dao.queryPageByVersionIdCreatedBy(versionId, createdById, pageSize, cursorKey);
    }

    @Autowired
    public void setDResponseDao(DResponseDao dResponseDao) {
        this.dao = dResponseDao;
    }

    @Override
    public void delete(String parentKeyString, Long id) {
        surveyService.deleteResponse(id);
    }

    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
}
