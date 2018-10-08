package com.lizikj.trace.service;

import java.util.List;

import com.lizikj.trace.model.Comment;

/**
 * 评论服务
 * @auth zone
 * @date 2017-10-14
 */
public interface CommentService {

    List<Comment> getCommentsByProductId(Long productId);

}
