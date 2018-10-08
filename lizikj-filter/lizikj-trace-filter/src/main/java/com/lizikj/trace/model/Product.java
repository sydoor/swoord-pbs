package com.lizikj.trace.model;

import java.io.Serializable;
import java.util.List;

/**
 * 产品对象
 * @auth zone
 * @date 2017-10-14
 */
public class Product implements Serializable {
    private Long id;

    private String name;

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
