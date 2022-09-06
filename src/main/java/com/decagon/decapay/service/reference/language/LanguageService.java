package com.decagon.decapay.service.reference.language;

import com.decagon.decapay.model.reference.language.Language;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface LanguageService  {

	Language getByCode(String code) ;

	Map<String, Language> getLanguagesMap() ;

	List<Language> getLanguages();

	Locale toLocale(Language language, String store);

	Language toLanguage(Locale locale);
	
	Language defaultLanguage();

	void create(Language language);

	boolean existLanguages();
}
