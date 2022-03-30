package com.search.api.service;

import java.util.List;

interface SearchService {

    void indexDocument(String id, String content);
 
    List<IndexEntry> search(String term, String strategy);
}
