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
    
    public CursorPage<DResponse, Long> getResponsesPageByExtMeetingId(String extMeetingId, int pageSize, String cursorKey) {
        final CursorPage<DResponse, Long> page = dao.queryPageByExtMeetingId(extMeetingId, pageSize, cursorKey);
        return page;
    }
    
    public CursorPage<DResponse, Long> getResponsesPageBySurveyIdVersionIdAndExtMeetingId(Long surveyId, Long versionId, String extMeetingId, int pageSize, String cursorKey) {
        return dao.queryPageBySurveyIdVersionIdAndExtMeetingId(surveyId, versionId, extMeetingId, pageSize, cursorKey);
    }

    @Autowired
    public void setDResponseDao(DResponseDao dResponseDao) {
        this.dao = dResponseDao;
    }
}
