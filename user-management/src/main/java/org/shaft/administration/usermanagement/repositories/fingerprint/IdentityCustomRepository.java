package org.shaft.administration.usermanagement.repositories.fingerprint;


import org.shaft.administration.usermanagement.entity.Identity;

import java.util.List;
import java.util.Map;

public interface IdentityCustomRepository {
    List<Identity> checkIfFpExistsForI(String fp, int i, boolean isIdentified);
    List<Identity> checkIfIExistsForFp(String fp);
    Long updateFp(String fp,int i);
    Long upsertFpAndIPair(int account, String fp, int i);
}
