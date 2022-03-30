package com.search.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

	private Map<String, String> documents;
	private Map<String, Set<String>> index;

	SearchServiceImpl() {
		this.documents = new TreeMap<>();
		this.index = new HashMap<>();
	}

	public Map<String, String> getDocuments() {
		return documents;
	}

	public Map<String, String> deleteAllDocuments() {
		documents.clear();
		index.clear();
		return documents;
	}

	@Override
	public void indexDocument(String id, String content) {
		documents.put(id, content);
		List<String> tokenizedContent = Arrays.asList(content.toLowerCase().trim().split("\\s+"));
		tokenizedContent.forEach(s -> {
			index.computeIfAbsent(s, k -> new HashSet<>()).add(id);
		});
	}

	@Override
	/**
	 * Method will search the term provide in the search
	 * 
	 * @param id
	 * @param term
	 * @return
	 */
	public List<IndexEntry> search(String term, String strategy) {
		if (term == null) {
			throw new IllegalArgumentException("Search Term can not be empty");
		}


		List<IndexEntry> orderedDocuments = new ArrayList<>();
		Set<String> result = index.get(term.toLowerCase());
		if (result == null || result.isEmpty()) {
			return List.of();
		}
		result.forEach(s -> {
			IndexEntry indexEntry = new IndexEntryImpl(s, calculateTfIdfScore(s, term, strategy));
			orderedDocuments.add(indexEntry);
		});

		return orderedDocuments.stream().sorted(Comparator.comparingDouble(IndexEntry::getScore).reversed())
				.collect(Collectors.toList());
	}

	/**
	 * Method will calculate the TF-IDF Score
	 * 
	 * @param id
	 * @param term
	 * @return
	 */
	private double calculateTfIdfScore(String id, String term, String strategy) {

		return tf(id, term, strategy) * idf(term);
	}

	/**
	 * Method calculates the TF score
	 * 
	 * @param id
	 * @param term
	 * @param strategy
	 * @return
	 */
	private double tf(String id, String term, String strategy) {
		List<String> document = Arrays.asList(documents.get(id).trim().split("\\s+"));
		double result = 0;
		Map<String, Integer> totalWordsInDocMap = new HashMap<String, Integer>();

		for (String word : document) {
			if (term.equals(word))
				result++;
		}

		if ("document".equalsIgnoreCase(strategy)) {

			return result / document.size();

		} else {

			for (String w : document) {
				w = w.trim().toLowerCase();
				if (w.length() > 0) {
					if (totalWordsInDocMap.containsKey(w)) {
						totalWordsInDocMap.put(w, totalWordsInDocMap.get(w) + 1);
					} else {
						totalWordsInDocMap.put(w, 1);
					}
				}
			}
			return (0.5 + (0.5 * (result / Collections.max(totalWordsInDocMap.values()))));
		}

	}

	/**
	 * This method calculates the IDF score
	 * 
	 * @param term
	 * @return
	 */
	private double idf(String term) {
		Map<String, String> documentMap = documents;

		long allWordsCount = documentMap.values().stream().map(tokens -> tokens.trim().split("\\s+"))
				.flatMap(Stream::of).count();

		long termCount = documentMap.values().stream().map(tokens -> tokens.trim().split("\\s+")).flatMap(Stream::of)
				.filter(token -> token.equals(term)).count();

		return Math.log((double) allWordsCount / (double) termCount);
	}
}
