package com.swizima.reportengine.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swizima.reportengine.dto.SystemFeedback;
import com.swizima.reportengine.enums.Status;
import com.swizima.reportengine.models.ReportTemplate;
import com.swizima.reportengine.service.FileStorageProperties;
import com.swizima.reportengine.service.FileStorageService;
import com.swizima.reportengine.service.ReportTemplateService;

import jakarta.servlet.ServletException;

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
	public SystemFeedback uploadTemplate(@RequestParam("file") MultipartFile file)
			throws ServletException, IOException {

		logger.info("Uploading Templates");

		SystemFeedback feedback = new SystemFeedback();

		try {

			String fileExtension = getFileExtension(file.getOriginalFilename());

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
				feedback.setId(reportTemplate.getId());
				feedback.setMessage(fileName);
				feedback.setResponse(true);
			} else {
				feedback.setId(refId);
				feedback.setMessage(fileName);
				feedback.setResponse(false);
			}

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();

			feedback.setMessage("An error occured while creating the file ::: " + ex.getMessage());
			feedback.setResponse(false);

			logger.error("An error occured while creating the file ::: " + ex.getMessage());
		}

		return feedback;
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

	private static String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}
}
