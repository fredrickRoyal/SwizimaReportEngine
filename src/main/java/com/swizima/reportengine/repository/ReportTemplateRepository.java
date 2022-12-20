package com.swizima.reportengine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.swizima.reportengine.models.ReportTemplate;

@Repository("ReportTemplateRepository")
public interface ReportTemplateRepository extends CrudRepository<ReportTemplate, String> {

}
