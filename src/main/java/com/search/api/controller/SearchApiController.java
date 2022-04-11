package com.search.api.controller;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.search.api.service.IndexEntry;
import com.search.api.service.SearchServiceImpl;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@OpenAPIDefinition(tags = {
		@Tag(name = "Search API")
})
@RequestMapping("/searchapi")
class SearchApiController {

	private SearchServiceImpl searchService;
	private int counter = 1;

	public static final String RESPONSE_CODE_OK = "200";
	public static final String RESPONSE_CODE_BAD_REQUEST = "400";
	public static final String RESPONSE_CODE_NOT_FOUND = "404";

	SearchApiController(SearchServiceImpl searchService) {
		this.searchService = searchService;

	}

	@Operation(tags = {
			"Search" }, summary = "Search Text", description = "Get All Documents matched with the search input. Provide Strategy values as document or augment", responses = {
					@ApiResponse(responseCode = RESPONSE_CODE_OK, description = "List of Document Results") })
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Object> search(@RequestParam String term,@RequestParam String strategy) {
		
		if(!("document".equalsIgnoreCase(strategy) || "augment".equalsIgnoreCase(strategy))) {
			return new ResponseEntity<Object>("Strategy should be either doc or aug entered strategy value : "+strategy, HttpStatus.NOT_ACCEPTABLE);
		}
		List<IndexEntry> searchResults = searchService.search(term, strategy);
		return new ResponseEntity<Object>(searchResults, HttpStatus.OK);
		 
	}

	@Operation(tags = {
			"List Of Documents" }, summary = "Search Text", description = "Get All Documents Stored", responses = {
					@ApiResponse(responseCode = RESPONSE_CODE_OK, description = "Get All Documents Stored") })
	@GetMapping(value = "/displayAllDocs", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> displayAllDocs() {
		return searchService.getDocuments();
	}

	@Operation(tags = {
			"Delete All Docs" }, summary = "Delete All Docs", description = "Delete All Docs", responses = {
					@ApiResponse(responseCode = RESPONSE_CODE_OK, description = "Delete All Docs") })
	@GetMapping(value = "/deleteAllDocs")
	ResponseEntity<Object> deleteAllDocs() {
		
		String documentsDeleted="";
		
		if(searchService.getDocuments() == null || searchService.getDocuments().isEmpty()) {
			documentsDeleted = "No Documents to delete";
		} else {
			searchService.deleteAllDocuments();
			counter=1;
			documentsDeleted = "All documents Deleted";
		}
		
		return new ResponseEntity<Object>(documentsDeleted, HttpStatus.OK);

		
	}

	@Operation(tags = {
			"Documents" }, summary = "Insert a new document", description = "Insert a new document.", responses = {
					@ApiResponse(responseCode = RESPONSE_CODE_OK, description = "Document inserted"),
					@ApiResponse(responseCode = RESPONSE_CODE_BAD_REQUEST, description = "Required field was empty", content = @Content(mediaType = "application/json")) }

	)
	@PostMapping(value = "/documents")
	public Map<String, String> addDocuments(@RequestBody List<String> documentList) {

		Map<String, String> documents = new TreeMap<>();
		documentList.forEach(content -> {
			String id = String.join("", "document", Integer.toString(counter++));
			documents.put(id, content);
			searchService.indexDocument(id, content);
		});
		return documents;
	}
}
