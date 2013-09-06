package com.wadpam.survey.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.DaoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.wadpam.open.exceptions.ConflictException;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.survey.dao.DAnswerDao;
import com.wadpam.survey.dao.DOptionDao;
import com.wadpam.survey.dao.DQuestionDao;
import com.wadpam.survey.dao.DResponseDao;
import com.wadpam.survey.dao.DSurveyDao;
import com.wadpam.survey.dao.DVersionDao;
import com.wadpam.survey.dao.Di18nDao;
import com.wadpam.survey.domain.DAnswer;
import com.wadpam.survey.domain.DOption;
import com.wadpam.survey.domain.DQuestion;
import com.wadpam.survey.domain.DResponse;
import com.wadpam.survey.domain.DSurvey;
import com.wadpam.survey.domain.DVersion;
import com.wadpam.survey.json.JAnswer;
import com.wadpam.survey.json.JResponse;
import com.wadpam.survey.json.JSurvey;
import com.wadpam.survey.json.JVersion;
import com.wadpam.survey.web.AnswerController;
import com.wadpam.survey.web.Converter;
import com.wadpam.survey.web.OptionController;
import com.wadpam.survey.web.QuestionController;
import com.wadpam.survey.web.ResponseController;
import com.wadpam.survey.web.SurveyController;

/**
 *
 * @author os
 */
public class SurveyService {

    public static final String DOMAIN_APIDOCS = "apidocs";

    /** Base offset for survey resource errors (101000) */
    public static final int ERR_SURVEY = 101000;
    /** Base offset for response resource errors (102000) */
    public static final int ERR_RESPONSE = 102000;
    /** Base offset for answer resource errors (103000) */
    public static final int ERR_ANSWER = 103000;
    /** Base offset for question resource errors (104000) */
    public static final int ERR_QUESTION = 104000;
    /** Base offset for option resource errors (105000) */
    public static final int ERR_OPTION = 105000;
    /** Base offset for version resource errors (106010) */
    public static final int ERR_VERSION = 106010;

    public static final int ERR_VERSION_CREATE_NOT_FOUND = 106001;

    static final Logger LOG = LoggerFactory.getLogger(SurveyService.class);

    private static final DatastoreService DATA_STORE = DatastoreServiceFactory.getDatastoreService();

    private DAnswerDao answerDao;
    private Di18nDao i18nDao;
    private DOptionDao optionDao;
    private DQuestionDao questionDao;
    private DResponseDao responseDao;
    private DSurveyDao surveyDao;
    private DVersionDao versionDao;

    public void init() {
    }
    
    public DOption cloneOption(DQuestion question, DOption from) {
        final DOption to = new DOption();
        to.setLabel(from.getLabel());
        to.setQuestion(question);
        to.setSurvey(from.getSurvey());
        to.setVersion(from.getVersion());
        
        optionDao.persist(to);
        
        return to;
    }
    
    public DQuestion cloneQuestion(DVersion version, DQuestion from) {
        final DQuestion to = new DQuestion();
        to.setOrdering(from.getOrdering());
        to.setQuestion(from.getQuestion());
        to.setRequired(from.getRequired());
        to.setSurvey(from.getSurvey());
        to.setType(from.getType());
        to.setVersion(version);
        
        questionDao.persist(to);
        
        for (DOption fo : optionDao.queryByQuestion(from)) {
            cloneOption(to, fo);
        }
        
        return to;
    }
    
    public DVersion cloneVersion(Long surveyId, Long fromVersionId, String description) {
        LOG.debug("clone version from {} for surveyId {}", fromVersionId, surveyId);
        final DSurvey survey = new DSurvey();
        survey.setId(surveyId);
        
        final DVersion to = new DVersion();
        to.setDescription(description);
        to.setState(JVersion.STATE_DRAFT);
        to.setSurvey(survey);

        versionDao.persist(to);
        
        // clone version
        if (null != fromVersionId) {
            final DVersion from = new DVersion();
            from.setId(fromVersionId);
            for (DQuestion fq : questionDao.queryByVersion(from)) {
                cloneQuestion(to, fq);
            }
        }
        
        return to;

    }
    
    public DAnswer createAnswer(DAnswer dEntity) {

        // id must be generated
        if (null != dEntity.getId()) {
            throw new ConflictException(AnswerController.ERR_CREATE_CONFLICT,
                    String.format("id {} must not be specified", dEntity.getId()),
                    null,
                    "id must be generated");
        }
        
        // check that question exists
        final DQuestion question = questionDao.findByPrimaryKey(dEntity.getQuestion().getId());
        if (null == question) {
            throw new NotFoundException(AnswerController.ERR_CREATE_NOT_FOUND, 
                    String.format("Question {} not found",  dEntity.getQuestion().getId()), 
                    null, 
                    "Cannot answer non-existing question");
        }
        
        answerDao.persist(dEntity);
        return dEntity;
    }
    
    public DOption createOption(DOption dEntity) {
        
        // id must be generated
        if (null != dEntity.getId()) {
            throw new ConflictException(OptionController.ERR_CREATE_CONFLICT,
                    String.format("id {} must not be specified", dEntity.getId()),
                    null,
                    "id must be generated");
        }
        
        // check that question exists
        final DQuestion question = questionDao.findByPrimaryKey(dEntity.getQuestion().getId());
        if (null == question) {
            throw new NotFoundException(OptionController.ERR_CREATE_NOT_FOUND, 
                    String.format("Question {} not found",  dEntity.getQuestion().getId()), 
                    null, 
                    "Cannot answer non-existing question");
        }
        
        optionDao.persist(dEntity);
        return dEntity;
    }
    
    public DQuestion createQuestion(DQuestion dEntity) {
        
        // id must be generated
        if (null != dEntity.getId()) {
            throw new ConflictException(QuestionController.ERR_CREATE_CONFLICT,
                    String.format("id {} must not be specified", dEntity.getId()),
                    null,
                    "id must be generated");
        }
        
        
        // check that survey exists
        final DSurvey survey = surveyDao.findByPrimaryKey(dEntity.getSurvey().getId());
        if (null == survey) {
            throw new NotFoundException(QuestionController.ERR_CREATE_NOT_FOUND, 
                    String.format("Survey {} not found",  dEntity.getSurvey().getId()), 
                    null, 
                    "Cannot respond to non-existing survey");
        }
        
        questionDao.persist(dEntity);
        return dEntity;
    }
    
    public DSurvey createSurvey(DSurvey dEntity) {
        
        // id must be generated
        if (null != dEntity.getId()) {
            throw new ConflictException(SurveyController.ERR_CREATE_CONFLICT,
                    String.format("id {} must not be specified", dEntity.getId()),
                    null,
                    "id must be generated");
        }
        
        // patch state if missing
        if (null == dEntity.getState()) {
            dEntity.setState(JSurvey.STATE_IN_DESIGN);
        }
        
        surveyDao.persist(dEntity);
        
        // create the first version
        cloneVersion(dEntity.getId(), null, "0.1");
        
        return dEntity;
    }
    
    public DVersion createVersion(Long surveyId, Long fromVersionId, String description) {
        final DSurvey survey = surveyDao.findByPrimaryKey(surveyId);
        if (null == survey) {
            throw new NotFoundException(ERR_VERSION_CREATE_NOT_FOUND, null, null, 
                    String.format("Cannot find survey with id %d", surveyId));
        }
        
        final DVersion fromVersion = versionDao.findByPrimaryKey(fromVersionId);
        if (null == fromVersion) {
            throw new NotFoundException(ERR_VERSION_CREATE_NOT_FOUND, null, null, 
                    String.format("Cannot find from-version with id %d", fromVersionId));
        }
        
        return cloneVersion(surveyId, fromVersionId, description);
    }
    
    public Integer deleteAll() {
        int count = answerDao.deleteAll();
        count += i18nDao.deleteAll();
        count += optionDao.deleteAll();
        count += responseDao.deleteAll();
        count += questionDao.deleteAll();
        count += versionDao.deleteAll();
        count += surveyDao.deleteAll();
        
        // for GAE, will do nothing
        ((DaoImpl) answerDao).dropTable();
        ((DaoImpl) i18nDao).dropTable();
        ((DaoImpl) optionDao).dropTable();
        ((DaoImpl) responseDao).dropTable();
        ((DaoImpl) questionDao).dropTable();
        ((DaoImpl) versionDao).dropTable();
        ((DaoImpl) surveyDao).dropTable();
        
        return count;
    }

    public void deleteResponse(Long id) {
        DResponse response = new DResponse();
        response.setId(id);
        Object key = responseDao.getPrimaryKey(response);
        Iterable<Long> answerKeys = answerDao.queryKeysByResponse(key);

        // Delete Answer of the response
        answerDao.delete(null, answerKeys);
        // Delete Response
        responseDao.delete(id);
    }

    public void deleteQuestion(Long id) {
        DQuestion question = new DQuestion();
        question.setId(id);
        Object key = questionDao.getPrimaryKey(question);

        Iterable<Long> optionKeys = optionDao.queryKeysByQuestion(key);
        Iterable<Long> answerKeys = answerDao.queryKeysByQuestion(key);

        // Delete Answer of the survey
        answerDao.delete(null, answerKeys);
        // Delete Option of the survey
        optionDao.delete(null, optionKeys);
        // Delete question
        questionDao.delete(id);

    }

    public void deleteVersion(Long id) {
        DVersion version = new DVersion();
        version.setId(id);
        Object key = versionDao.getPrimaryKey(version);

        Iterable<Long> questionKeys = questionDao.queryKeysByVersion(key);
        Iterable<Long> optionKeys = optionDao.queryKeysByVersion(key);
        Iterable<Long> responseKeys = responseDao.queryKeysByVersion(key);
        Iterable<Long> answerKeys = answerDao.queryKeysByVersion(key);

        // Delete Answer of the survey
        answerDao.delete(null, answerKeys);
        // Delete Response of the survey
        responseDao.delete(null, responseKeys);
        // Delete Option of the survey
        optionDao.delete(null, optionKeys);
        // Delete Question of the survey
        questionDao.delete(null, questionKeys);
        // Delete version
        versionDao.delete(id);

    }

    public void deleteSurvey(Long id) {
        DSurvey survey = new DSurvey();
        survey.setId(id);
        Object key = surveyDao.getPrimaryKey(survey);

        Iterable<Long> versionKeys = versionDao.queryKeysBySurvey(key);
        Iterable<Long> questionKeys = questionDao.queryKeysBySurvey(key);
        Iterable<Long> optionKeys = optionDao.queryKeysBySurvey(key);
        Iterable<Long> responseKeys = responseDao.queryKeysBySurvey(key);
        Iterable<Long> answerKeys = answerDao.queryKeysBySurvey(key);

        // Delete Option of the survey
        optionDao.delete(null, optionKeys);
        // Delete Answer of the survey
        answerDao.delete(null, answerKeys);
        // Delete Response of the survey
        responseDao.delete(null, responseKeys);
        // Delete Question of the survey
        questionDao.delete(null, questionKeys);
        // Delete version of the survey
        versionDao.delete(null, versionKeys);
        // Delete survey
        surveyDao.delete(id);
    }

    public void deleteAnswersByResponseAndQuestions(Long responseId, Long[] questionId) {
        Object responseKey = responseDao.getPrimaryKey(null, responseId);
        final ArrayList<Object> questionKeys = new ArrayList<Object>();
        for (Long id : questionId) {
            questionKeys.add(questionDao.getPrimaryKey(null, id));
        }

        Iterable<Long> answers = answerDao.queryKeysByResponseAndQuestions(responseKey, questionKeys);
        // Delete answer
        answerDao.delete(null, answers);
    }

    public DAnswer getAnswer(Long id) {
        final DAnswer entity = answerDao.findByPrimaryKey(id);
        return entity;
    }
    
    public Iterable<DAnswer> getAnswersByResponse(Long id) {
        final DResponse response = new DResponse();
        response.setId(id);
        return answerDao.queryByResponse(response);
    }

    public Iterable<DAnswer> getAnswersByResponseIds(Collection<Long> ids) {
        return answerDao.queryByResponseIds(ids);
    }

    public Iterable<DAnswer> getAnswersByVersion(Long versionId) {
        final DVersion version = new DVersion();
        version.setId(versionId);
        return answerDao.queryByVersion(version);
    }

    @Deprecated
    public CursorPage<DAnswer, Long> getAnswersPage(Long responseId, int pageSize, Serializable cursorKey) {
        return getAnswersPage(responseId, pageSize, null != cursorKey ? cursorKey.toString() : (String) null);
    }
    
    public CursorPage<DAnswer, Long> getAnswersPage(Long responseId, int pageSize, String cursorKey) {
        final DResponse response = new DResponse();
        response.setId(responseId);
        final CursorPage<DAnswer, Long> page = answerDao.queryPageByResponse(response, pageSize, cursorKey);
        return page;
    }
    
    public DOption getOption(Long id) {
    final DOption entity = optionDao.findByPrimaryKey(id);
        return entity;
    }
    
    public Iterable<DOption> getOptionsBySurvey(Long surveyId) {
        DSurvey survey = new DSurvey();
        survey.setId(surveyId);
        return optionDao.queryBySurvey(survey);
    }
    
    public Iterable<DOption> getOptionsByVersion(Long versionId) {
        DVersion version = new DVersion();
        version.setId(versionId);
        return optionDao.queryByVersion(version);
    }
    
    @Deprecated
    public CursorPage<DOption, Long> getOptionsPage(Long questionId, int pageSize, Serializable cursorKey) {
        return getOptionsPage(questionId, pageSize, null != cursorKey ? cursorKey.toString() : (String) null);
    }
    
    public CursorPage<DOption, Long> getOptionsPage(Long questionId, int pageSize, String cursorKey) {
        DQuestion question = new DQuestion();
        question.setId(questionId);

        optionDao.queryPage(pageSize, DOptionDao.COLUMN_NAME_CREATEDDATE, true, null, false, cursorKey);
        final CursorPage<DOption, Long> page = optionDao.queryPageByQuestion(question, pageSize, cursorKey);

        // run a quick sort by created date to avoid indexing.
        if (page.getTotalSize() > 1) {
            List<DOption> options = new ArrayList<DOption>(page.getItems());
            Collections.sort(options, new Comparator<DOption>() {
                @Override
                public int compare(DOption o1, DOption o2) {
                    return (int) (o1.getCreatedDate().getTime() - o2.getCreatedDate().getTime());
                }
            });
            page.setItems(options);
        }
        return page;
    }
    
    public DQuestion getQuestion(Long id) {
    final DQuestion entity = questionDao.findByPrimaryKey(id);
        return entity;
    }
    
    public Iterable<DVersion> getVersionsBySurvey(Long surveyId) {
        DSurvey survey = new DSurvey();
        survey.setId(surveyId);
        return versionDao.queryBySurvey(survey);
    }
    
    public Iterable<DQuestion> getQuestionsBySurvey(Long surveyId) {
        DSurvey survey = new DSurvey();
        survey.setId(surveyId);
        return questionDao.queryBySurvey(survey);
    }
    
    public Iterable<DQuestion> getQuestionsByVersion(Long versionId) {
        DVersion version = new DVersion();
        version.setId(versionId);
        return questionDao.queryByVersion(version);
    }
    
    public CursorPage<DQuestion, Long> getQuestionsPage(Long versionId, int pageSize, String cursorKey) {
        final DVersion version = new DVersion();
        version.setId(versionId);
        final CursorPage<DQuestion, Long> page = questionDao.queryPageByVersion(version, pageSize, cursorKey);
        return page;
    }
    
    public DResponse getResponse(Long id) {
        final DResponse entity = responseDao.findByPrimaryKey(id);
        return entity;
    }
    
    public CursorPage<DResponse, Long> getResponsesPage(Long versionId, int pageSize, String cursorKey) {
        DVersion version = new DVersion();
        version.setId(versionId);
        final CursorPage<DResponse, Long> page = responseDao.queryPageByVersion(version, pageSize, cursorKey);
        return page;
    }
    
    public DSurvey getSurvey(Long id) {
        final DSurvey entity = surveyDao.findByPrimaryKey(id);
        return entity;
    }
    
    @Deprecated
    public CursorPage<DSurvey, Long> getSurveysPage(int pageSize, Serializable cursorKey) {
        return getSurveysPage(pageSize, null != cursorKey ? cursorKey.toString() : (String) null);
    }
    
    public CursorPage<DSurvey, Long> getSurveysPage(int pageSize, String cursorKey) {
        final CursorPage<DSurvey, Long> page = surveyDao.queryPage(pageSize, cursorKey);
        return page;
    }
    
    public DVersion getVersion(Long id) {
        final DVersion entity = versionDao.findByPrimaryKey(id);
        return entity;
    }
    
    public CursorPage<DVersion, Long> getVersionsPage(Long surveyId, int pageSize, String cursorKey) {
        DSurvey survey = new DSurvey();
        survey.setId(surveyId);
        final CursorPage<DVersion, Long> page = versionDao.queryPageBySurvey(survey, pageSize, cursorKey);
        return page;
    }
    
    public DOption updateOption(DOption dEntity) {
        optionDao.update(dEntity);
        return dEntity;
    }

    public DQuestion updateQuestion(DQuestion dEntity) {
        questionDao.update(dEntity);
        return dEntity;
    }

    public DSurvey updateSurvey(DSurvey dEntity) {
        surveyDao.update(dEntity);
        return dEntity;
    }

    public DVersion updateVersion(DVersion dEntity) {
        versionDao.update(dEntity);
        return dEntity;
    }

    public DAnswer upsertAnswer(DAnswer dEntity, Iterable<DAnswer> existing) {
        boolean existed = null != dEntity.getId();
        
        // check if found by Question and Response
        if (!existed && null != existing) {
            for (DAnswer da : existing) {
                if (dEntity.getQuestion().getId().equals(
                        da.getQuestion().getId())) {
                    da.setAnswer(dEntity.getAnswer());
                    dEntity = da;
                    existed = true;
                    break;
                }
            }
        }
        
        if (existed) {
            answerDao.update(dEntity);
        }
        else {
            answerDao.persist(dEntity);
        }
        return dEntity;
    }

    public DResponse upsertResponse(JResponse jResponse) {
        final DResponse dEntity = Converter.convert(jResponse);
        return upsertResponse(dEntity, jResponse.getAnswers());
    }
        
    public DResponse upsertResponse(DResponse dEntity, Collection<JAnswer> innerAnswers) {
        // check that survey exists
        final DSurvey survey = surveyDao.findByPrimaryKey(dEntity.getSurvey().getId());
        if (null == survey) {
            throw new NotFoundException(ResponseController.ERR_CREATE_NOT_FOUND, 
                    String.format("Survey {} not found",  dEntity.getSurvey().getId()), 
                    null, 
                    "Cannot respond to non-existing survey");
        }
        
        // patch state if missing
        if (null == dEntity.getState()) {
            dEntity.setState(JResponse.STATE_ACTIVE);
        }
        
        final boolean existed = null != dEntity.getId();
        if (existed) {
            responseDao.update(dEntity);
        }
        else {
            responseDao.persist(dEntity);
        }

        LOG.info("saved response {}, inner answers = {}", dEntity, innerAnswers);
        
        // inner Answers to this response?
        if (null != innerAnswers) {
            final Iterable<DAnswer> existing = answerDao.queryByResponse(dEntity);
            for (JAnswer answer : innerAnswers) {
                // if ID generated above, populate on inner answers
                answer.setResponseId(dEntity.getId());
                upsertAnswer(Converter.convert(answer), existing);
            }
        }
        
        return dEntity;
    }

    public void setAnswerDao(DAnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    public void setI18nDao(Di18nDao i18nDao) {
        this.i18nDao = i18nDao;
    }

    public void setOptionDao(DOptionDao optionDao) {
        this.optionDao = optionDao;
    }

    public void setQuestionDao(DQuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public void setResponseDao(DResponseDao responseDao) {
        this.responseDao = responseDao;
    }

    public void setSurveyDao(DSurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    public void setVersionDao(DVersionDao versionDao) {
        this.versionDao = versionDao;
    }

}
