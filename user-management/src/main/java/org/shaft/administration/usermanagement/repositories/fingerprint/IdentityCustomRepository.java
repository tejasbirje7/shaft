package org.shaft.administration.usermanagement.repositories.fingerprint;


import org.shaft.administration.usermanagement.entity.Identity;

import java.util.List;

public interface IdentityCustomRepository {
    public List<Identity> checkIfFpExistsForI(String fp, int i);
    public List<Identity> checkIfIExistsForFp(String fp, boolean isIdentified);
    public Long updateFp(String fp,int i);
}
