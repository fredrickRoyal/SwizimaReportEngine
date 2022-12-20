package com.swizima.reportengine.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swizima.reportengine.enums.Status;
import com.swizima.reportengine.models.ReportTemplate;
import com.swizima.reportengine.repository.ReportTemplateRepository;

@Service("ReportTemplateService")
@Transactional
public class ReportTemplateService {

	@Autowired
	private ReportTemplateRepository reportTemplateRepository;

	private final Logger log = LoggerFactory.getLogger(ReportTemplateService.class);

	public ReportTemplate save(ReportTemplate reportTemplate) {
		try {
			return reportTemplateRepository.save(reportTemplate);

		} catch (Exception ex) {
			log.info(ex.getMessage());
		}
		return null;
	}

	public List<ReportTemplate> find() {

		List<ReportTemplate> list = new ArrayList<ReportTemplate>();

		try {

			Iterable<ReportTemplate> templates = reportTemplateRepository.findAll();

			templates.forEach(template -> {

				list.add(template);
			});

		} catch (Exception ex) {
			log.info(ex.getMessage());
		}
		return list;
	}

	public ReportTemplate delete(ReportTemplate reportTemplate) {

		try {

			reportTemplate.setRecordStatus(Status.DELETED);

			return reportTemplateRepository.save(reportTemplate);

		} catch (Exception ex) {

			log.info(ex.getMessage());
		}

		return null;

	}

}
