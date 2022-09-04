package com.decagon.decapay.repositories.reference.language;

import com.decagon.decapay.model.reference.language.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository <Language, Integer> {
	
	Language findByCode(String code) ;


	List<Language> findAllByOrderByCode();
}
