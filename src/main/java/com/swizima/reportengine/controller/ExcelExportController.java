package com.swizima.reportengine.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.swizima.reportengine.dto.DataExportRequestDTO;
import com.swizima.reportengine.dto.DataExportResponseDTO;
import com.swizima.reportengine.service.FileStorageProperties;
import com.swizima.reportengine.service.FileStorageService;
 

@RestController
public class ExcelExportController {

	private static final Logger logger = LoggerFactory.getLogger(ExcelExportController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@GetMapping("/test")
	private String test() {
		return "HELLO WORLD";
	}

	@PostMapping("/generatefile")
	private DataExportResponseDTO generateCSVFile(@RequestBody DataExportRequestDTO dto) {

		DataExportResponseDTO responseDTO = new DataExportResponseDTO();
		responseDTO.setResponse(false);
		responseDTO.setMessage("Un known accured while initiating transaction");
		try {

			Files.createDirectories(
					Paths.get(fileStorageProperties.getExportDir() + dto.getRefId()).toAbsolutePath().normalize());

			File outputFile = new File(fileStorageProperties.getExportDir() + dto.getRefId() + "/" + dto.getFileName());

			String fileName = outputFile.getAbsolutePath();
			JsonNode jsonTree = new ObjectMapper().readTree(dto.getJson());

			// JsonNode jsonTree = new ObjectMapper().readTree(new
			// File("src/main/resources/orderLines.json"));
			Builder csvSchemaBuilder = CsvSchema.builder();
			JsonNode firstObject = jsonTree.elements().next();
			firstObject.fieldNames().forEachRemaining(fieldName -> {
				csvSchemaBuilder.addColumn(fieldName);
			});
			CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

			CsvMapper csvMapper = new CsvMapper();
			csvMapper.writerFor(JsonNode.class).with(csvSchema).writeValue(new File(fileName), jsonTree);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
					.path(dto.getRefId() + "/").path(dto.getFileName()).toUriString();

			responseDTO.setResponse(true);
			responseDTO.setMessage(fileDownloadUri);
			responseDTO.setFileDownloadUri(fileDownloadUri);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			responseDTO.setMessage(ex.getMessage());
		}
		return responseDTO;
	}

	 

	@GetMapping("/downloadFile/{refId}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String refId, @PathVariable String fileName,
			HttpServletRequest request) {
		// Load file as Resource

		String rootDirectory = fileStorageProperties.getExportDir();

		Resource resource = fileStorageService.loadFileAsResource(fileName, rootDirectory + refId);

		// Try to determine file's content type
		String contentType = null;

		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

 

}
