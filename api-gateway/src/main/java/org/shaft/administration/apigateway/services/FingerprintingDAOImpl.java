package org.shaft.administration.apigateway.services;

import org.shaft.administration.apigateway.dao.FingerPrintingDAO;
import org.shaft.administration.apigateway.entity.Fingerprinting;
import org.shaft.administration.apigateway.repositories.FingerPrintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FingerprintingDAOImpl implements FingerPrintingDAO {

    FingerPrintingRepository fingerPrintingRepository;

    @Autowired
    public FingerprintingDAOImpl(FingerPrintingRepository fingerPrintingRepository) {
        this.fingerPrintingRepository = fingerPrintingRepository;
    }

    @Override
    public Map<String, Integer> checkIdentity(Map<String,Object> details) {
        // #TODO Handle all exceptions and provide different response code for each exception
        String fp = (String) details.get("fp");
        if (details.containsKey("i")) {
            int i = (int) details.get("i");
            List<Fingerprinting> fpDetails = fingerPrintingRepository.checkIfFpExistsForI(fp,i);
            if (fpDetails.isEmpty()) {
                Long totalUpdated = fingerPrintingRepository.updateFp(fp,i);
                if (totalUpdated < 0) {
                    return (Map<String, Integer>) new HashMap<>().put("i",i);
                } else {
                    // #TODO Raise Exception
                }
            } else {
                return (Map<String, Integer>) new HashMap<>().put("i",i);
            }
        } else {
            List<Fingerprinting> fpDetails = fingerPrintingRepository.checkIfIExistsForFp(fp);
            if(fpDetails.isEmpty()) {
                String idx = "1600_1659262362";
                Fingerprinting newFpToI = new Fingerprinting();
                if (details.containsKey("requestTime")) {
                    newFpToI.setIdentity((Integer) details.get("requestTime"));
                    newFpToI.setIdentified(false);
                    List<Map<String,String>> fpArray = new ArrayList<>();
                    fpArray.add((Map<String, String>) new HashMap<>().put("g",fp));
                    newFpToI.setFingerPrint(fpArray);
                    fingerPrintingRepository.save(newFpToI);
                } else {
                    // #TODO Raise Exception BAD REQUEST
                }
            } else {
                return (Map<String, Integer>) new HashMap<>().put("i",fpDetails.get(0).getIdentity());
            }
        }
        return null;
    }
}
