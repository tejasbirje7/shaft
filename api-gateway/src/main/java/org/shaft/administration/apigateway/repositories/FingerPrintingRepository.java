package org.shaft.administration.apigateway.repositories;

import org.shaft.administration.apigateway.entity.FingerPrint.Fingerprinting;
import org.shaft.administration.apigateway.repositories.fingerprint.FingerPrintingCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FingerPrintingRepository extends ElasticsearchRepository<Fingerprinting,String>, FingerPrintingCustomRepository {
}
