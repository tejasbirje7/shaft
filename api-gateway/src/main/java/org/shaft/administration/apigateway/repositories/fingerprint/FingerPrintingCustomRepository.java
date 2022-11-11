package org.shaft.administration.apigateway.repositories.fingerprint;

import org.shaft.administration.apigateway.entity.Fingerprinting;

import java.util.List;

public interface FingerPrintingCustomRepository {
    public List<Fingerprinting> checkIfFpExistsForI(String fp, int i);
    public List<Fingerprinting> checkIfIExistsForFp(String fp);
    public Long updateFp(String fp,int i);
}
