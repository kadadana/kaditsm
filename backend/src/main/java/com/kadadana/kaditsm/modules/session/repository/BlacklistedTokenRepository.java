package com.kadadana.kaditsm.modules.session.repository;

//JAVA IMPORTS
import org.springframework.data.repository.CrudRepository;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.session.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, String> {
}