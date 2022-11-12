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
        // Check if `i` exists in request
        if (details.containsKey("i")) {
            // `i` exists in request
            int i = (int) details.get("i");
            List<Fingerprinting> fpDetails = fingerPrintingRepository.checkIfFpExistsForI(fp,i);
            // Check if `fp` exists for `i` received in request
            if (fpDetails.isEmpty()) {
                // Update `fp` for this `i`
                Long totalUpdated = fingerPrintingRepository.updateFp(fp,i);
                if (totalUpdated > 0) {
                    return (Map<String, Integer>) new HashMap<>().put("i",i);
                } else {
                    // #TODO Raise Exception
                }
            } else {
                // `fp` exists return `i`
                return (Map<String, Integer>) new HashMap<>().put("i",i);
            }
        } else {
            // `i` doesn't exist in request
            List<Fingerprinting> fpDetails = fingerPrintingRepository.checkIfIExistsForFp(fp);
            // Check if `i` exists for received `fp`
            if(fpDetails.isEmpty()) {
                // insert `fp` i.e. new `i` case
                // #TODO Retrieve this `idx` variable from account meta service
                String idx = "1600_1659262362";
                Fingerprinting newFpToI = new Fingerprinting();
                if (details.containsKey("requestTime")) {
                    newFpToI.setIdentity((Integer) details.get("requestTime"));
                    newFpToI.setIdentified(false);
                    List<Map<String,String>> fpArray = new ArrayList<>();
                    fpArray.add((Map<String, String>) new HashMap<>().put("g",fp));
                    newFpToI.setFingerPrint(fpArray);
                    fingerPrintingRepository.save(newFpToI);
                    // #TODO  Insert the event schema into `idx` index fetched above to track events
                } else {
                    // #TODO Raise Exception BAD REQUEST
                }
            } else {
                // `i` exists return `fp`
                return (Map<String, Integer>) new HashMap<>().put("i",fpDetails.get(0).getIdentity());
            }
        }
        return null;
    }
}
