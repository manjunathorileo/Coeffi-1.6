package com.dfq.coeffi.i18n;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LanguageDto {
    private long id;
    private List<Language> languages;
    private Language defaultLanguage;
    private Language selectedLanguage;
    private long empId;

}
