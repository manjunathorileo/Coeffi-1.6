package com.dfq.coeffi.aboutCoeffi;

import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AboutService {
    @Autowired
    AboutRepository aboutRepository;
    @Autowired
    FileStorageService fileStorageService;

    public void saveAbout(AboutDto aboutDto){
        About about  = new About();
        if (about.getId()>0) {
            about = aboutRepository.findOne(aboutDto.getId());
            about.setDescription(aboutDto.getDescription());
            about.setModules(aboutDto.getModules());
            about.setProductName(aboutDto.getProductName());
            about.setVersion(aboutDto.getVersion());
            about.setReleaseDate(aboutDto.getReleaseDate());
            List<Document> documentList = new ArrayList<>();
            for (Long docId : aboutDto.getDocumentIds()) {
                Document document = fileStorageService.getDocument(docId);
                documentList.add(document);
            }
            about.setDocumentIds(documentList);
            aboutRepository.save(about);
        }
        if (about.getId()==0) {
            about.setDescription(aboutDto.getDescription());
            about.setModules(aboutDto.getModules());
            about.setProductName(aboutDto.getProductName());
            about.setVersion(aboutDto.getVersion());
            about.setReleaseDate(aboutDto.getReleaseDate());
            List<Document> documentList = new ArrayList<>();
            for (Long docId : aboutDto.getDocumentIds()) {
                Document document = fileStorageService.getDocument(docId);
                documentList.add(document);
            }
            about.setDocumentIds(documentList);
            aboutRepository.save(about);
        }
    }


    public AboutDto getLatestAbout(){
        List<About> aboutLatest = aboutRepository.findAll();
        AboutDto aboutDto = new AboutDto();
        List<AboutDocumentsDto> aboutDocumentsDtos = new ArrayList<>();
        About about = null;
        if (!aboutLatest.isEmpty()){
            Collections.reverse(aboutLatest);
            about = aboutLatest.get(0);
            aboutDto.setAbout(about);
            for (Document document : about.getDocumentIds()) {
                AboutDocumentsDto aboutDocumentsDto = new AboutDocumentsDto();
                if (document!=null) {
                    aboutDocumentsDto.setDocId(document.getId());
                    aboutDocumentsDto.setFileName(document.getFileName());
                    aboutDocumentsDtos.add(aboutDocumentsDto);
                }
            }
            aboutDto.setAboutDocumentsDtos(aboutDocumentsDtos);
        }
        return aboutDto;
    }


}
