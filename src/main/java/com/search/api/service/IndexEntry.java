package com.search.api.service;

public interface IndexEntry {
    String getId();
    void setId(String id);
    double getScore();
    void setScore(double score);
}
