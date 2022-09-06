package com.decagon.decapay.service.reference.language;

import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.model.reference.language.Language;
import com.decagon.decapay.repositories.reference.language.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service("languageService")
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

	private final LanguageRepository languageRepository;

	@Override
	@Cacheable("languageByCode")
	public Language getByCode(String code)  {
		return languageRepository.findByCode(code);
	}
	
	@Override
	public Locale toLocale(Language language, String countryCode) {

		if(countryCode != null) {

			//try to build valid language
			if("CA".equals(countryCode) && language.getCode().equals("en")) {
				countryCode = "US";
			}
			
			return new Locale(language.getCode(), countryCode);
		
		} else {
			
			return new Locale(language.getCode());
		}
	}
	
	@Override
	public Language toLanguage(Locale locale) {
		Language language = null;
		try {
			language = getLanguagesMap().get(locale.getLanguage());
		} catch (Exception e) {
			log.error("Cannot convert locale " + locale.getLanguage() + " to language");
		}
		if(language == null) {
			language = new Language(AppConstants.DEFAULT_LANGUAGE);
		}
		return language;

	}
	
	@Override
	public Map<String,Language> getLanguagesMap()  {
		
		List<Language> langs = this.getLanguages();
		Map<String,Language> returnMap = new LinkedHashMap<String,Language>();
		for(Language lang : langs) {
			returnMap.put(lang.getCode(), lang);
		}
		return returnMap;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Language> getLanguages()  {

		List<Language> langs = null;
		try {
			//langs = (List<Language>) cache.getFromCache("LANGUAGES");
			if(langs==null) {
				langs = this.languageRepository.findAllByOrderByCode();
				//cache.putInCache(langs, "LANGUAGES");
			}
		} catch (Exception e) {
			log.error("getLanguages()", e);
		}
		return langs;
	}
	
	@Override
	public Language defaultLanguage() {
		return toLanguage(Locale.ENGLISH);
	}

	@Override
	public void create(Language language) {
		languageRepository.save(language);
	}

	@Override
	public boolean existLanguages() {
		return languageRepository.count()>0;
	}

}
