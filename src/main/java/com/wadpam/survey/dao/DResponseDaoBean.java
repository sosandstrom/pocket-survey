package com.wadpam.survey.dao;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;

import com.wadpam.survey.domain.DResponse;

/**
 * Implementation of Business Methods related to entity DResponse.
 * This (empty) class is generated by mardao, but edited by developers.
 * It is not overwritten by the generator once it exists.
 * 
 * Generated on 2012-10-19T08:40:22.845+0700.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class DResponseDaoBean extends GeneratedDResponseDaoImpl implements DResponseDao {

    @Override
    public CursorPage<DResponse, Long> queryPageBySurveyIdVersionIdAndExtMeetingId(Long surveyId, Long versionId,
            String extMeetingId, int pageSize, String cursorKey) {
        final Object surveyForeignKey = getSurveyDao().getPrimaryKey(null, surveyId);
        final Filter surveyFilter = createEqualsFilter(COLUMN_NAME_SURVEY, surveyForeignKey);

        final Object versionForeignKey = getVersionDao().getPrimaryKey(null, versionId);
        final Filter versionFilter = createEqualsFilter(COLUMN_NAME_VERSION, versionForeignKey);

        final Filter meetingFilter = createEqualsFilter(COLUMN_NAME_EXTMEETINGID, extMeetingId);

        return queryPage(false, pageSize, null, null, null, false, null, false, cursorKey, surveyFilter, versionFilter,
                meetingFilter);
    }

    @Override
    public CursorPage<DResponse, Long> queryPageByVersionIdCreatedBy(Long versionId, String createdById, int pageSize,
            String cursorKey) {
        final Object versionForeignKey = getVersionDao().getPrimaryKey(null, versionId);
        final Filter versionFilter = createEqualsFilter(COLUMN_NAME_VERSION, versionForeignKey);

        final Filter createdByFilter = createEqualsFilter(COLUMN_NAME_CREATEDBY, createdById);

        return queryPage(false, pageSize, null, null, null, false, null, false, cursorKey, versionFilter,
                createdByFilter);
    }
    
    @Override
    public Iterable<Long> queryKeysBySurvey(Object surveyKey) {
        Filter filter = createEqualsFilter(COLUMN_NAME_SURVEY, surveyKey);
        return queryIterableKeys(0, -1, null, null, null, false, null, false, filter);
    }

    @Override
    public Iterable<Long> queryKeysByVersion(Object versionKey) {
        Filter filter = createEqualsFilter(COLUMN_NAME_VERSION, versionKey);
        return queryIterableKeys(0, -1, null, null, null, false, null, false, filter);
    }

    @Override
    public Iterable<Long> queryKeysBySurveyVersionExtMeeting(Object surveyKey, Object versionKey, String meetingId) {
        Filter surveyFilter = createEqualsFilter(COLUMN_NAME_SURVEY, surveyKey);
        Filter versionFilter = createEqualsFilter(COLUMN_NAME_VERSION, versionKey);
        Filter meetingFilter = createEqualsFilter(COLUMN_NAME_EXTMEETINGID, meetingId);
        return queryIterableKeys(0, -1, null, null, null, false, null, false, surveyFilter, versionFilter, meetingFilter);
    }
}
