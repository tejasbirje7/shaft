package org.shaft.administration.usermanagement.repositories.fingerprint;


import org.shaft.administration.usermanagement.entity.Identity;

import java.util.List;

public interface IdentityCustomRepository {
    public List<Identity> checkIfFpExistsForI(String fp, int i, boolean isIdentified);
    public List<Identity> checkIfIExistsForFp(String fp);
    public Long updateFp(String fp,int i);
}
