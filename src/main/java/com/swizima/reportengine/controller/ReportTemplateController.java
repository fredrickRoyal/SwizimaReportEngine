package com.swizima.reportengine.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.swizima.reportengine.dto.ReportTemplateDTO;
import com.swizima.reportengine.dto.SystemResponseDTO;
import com.swizima.reportengine.enums.Status;
import com.swizima.reportengine.models.ReportTemplate;
import com.swizima.reportengine.service.FileStorageProperties;
import com.swizima.reportengine.service.FileStorageService;
import com.swizima.reportengine.service.ReportTemplateService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ReportTemplateController {

	private static final Logger logger = LoggerFactory.getLogger(ReportTemplateController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@PostMapping(value = "/templates/upload")
	public SystemResponseDTO<String> uploadTemplate(@RequestParam("file") MultipartFile file)
			throws ServletException, IOException {

		logger.info("Uploading Templates");

		SystemResponseDTO<String> feedback = new SystemResponseDTO<String>();

		try {

			// String fileExtension = getFileExtension(file.getOriginalFilename());

			String refId = "fkasoma";

			String fileName = fileStorageService.storeFile(file, refId);

			ReportTemplate documentUpload = new ReportTemplate();

			documentUpload.setCreatedDate(LocalDateTime.now());
			documentUpload.setPublishedDate(LocalDateTime.now());
			documentUpload.setUpdatedDate(LocalDateTime.now());
			// documentUpload.setDescription(docType);
			documentUpload.setFileName(fileName);
			documentUpload.setFilePath(fileStorageProperties.getUploadDir() + refId + "/" + fileName);
			documentUpload.setFileSize(getFileSize(file.getSize()));
			documentUpload.setFolder(fileStorageProperties.getUploadDir());
			documentUpload.setPublishedBy(refId);
			documentUpload.setPublishStatus(Status.ACTIVE);
			documentUpload.setRefId(refId);
			documentUpload.setRecordStatus(Status.ACTIVE);
			documentUpload.setFileType(file.getContentType());

			ReportTemplate reportTemplate = reportTemplateService.save(documentUpload);

			// String fileDownloadUri =
			// ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName).toUriString();

			// return new UploadFileDTO(fileName, fileDownloadUri, file.getContentType(),
			// getFileSize(file.getSize()));

			if (reportTemplate != null) {
				feedback.setData(reportTemplate.getId());
				feedback.setMessage(fileName);
				feedback.setStatus(true);
			} else {
				feedback.setMessage(fileName);
				feedback.setStatus(false);
			}

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();

			feedback.setMessage("An error occured while creating the file ::: " + ex.getMessage());
			feedback.setStatus(false);

			logger.error("An error occured while creating the file ::: " + ex.getMessage());
		}

		return feedback;
	}

	@GetMapping("/templates/all")
	public SystemResponseDTO<List<ReportTemplateDTO>> getAllTemplates() {

		SystemResponseDTO<List<ReportTemplateDTO>> dataResponse = new SystemResponseDTO<List<ReportTemplateDTO>>();

		List<ReportTemplateDTO> list = new ArrayList<ReportTemplateDTO>();

		List<ReportTemplate> documentUploads = reportTemplateService.find();

		documentUploads.forEach(d -> {

			ReportTemplateDTO dto = getReportTemplateDTO(d);
			if (dto != null) {
				list.add(dto);
			}

		});

		if (!list.isEmpty()) {
			dataResponse.setData(list);
			dataResponse.setMessage("Data found");
			dataResponse.setStatus(true);
		} else {
			dataResponse.setMessage("No Data found");
			dataResponse.setStatus(false);
		}

		return dataResponse;

	}

	@GetMapping("/download/{fileName:.+}/{refId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @PathVariable String refId,
			HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName, refId);

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

	private ReportTemplateDTO getReportTemplateDTO(ReportTemplate reportTemplate) {
		try {

			ReportTemplateDTO dto = new ReportTemplateDTO();

			dto.setId(reportTemplate.getId());
			dto.setDescription(reportTemplate.getDescription());
			dto.setFileName(reportTemplate.getFileName());

			dto.setFileSize(reportTemplate.getFileSize());
			dto.setFileType(reportTemplate.getFileType());
			dto.setFolder(reportTemplate.getFolder());
			dto.setPublishedBy(reportTemplate.getPublishedBy());
			dto.setPublishedDate(
					reportTemplate.getPublishedDate().format(new DateTimeFormatterBuilder().toFormatter()));

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/")
					.path(reportTemplate.getFileName()).toUriString();

			dto.setFilePath(fileDownloadUri);

			if (reportTemplate.getPublishStatus() != null) {
				dto.setPublishStatus(reportTemplate.getPublishStatus().getStatus());
			} else {
				dto.setPublishStatus("");
			}

			dto.setRefId(reportTemplate.getRefId());

			return dto;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	private String getFileSize(long bytes) {
		try {
			if (bytes < 1000) {
				return bytes + "Bytes";
			} else if (bytes >= 1000 && bytes < 1000000) {
				long kb = (bytes / 1000);
				return kb + " KB";
			} else if (bytes >= 1000000) {
				long mb = (bytes / 1000000);
				return mb + " MB";
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return 0 + " Byte";
	}

	public static String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}
}
