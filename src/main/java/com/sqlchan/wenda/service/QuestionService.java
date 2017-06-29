package com.sqlchan.wenda.service;

import com.sqlchan.wenda.dao.QuestionDAO;
import com.sqlchan.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    public int addquestion(Question question) {
        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public Question getById(int id) {
        return questionDAO.getById(id);
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
