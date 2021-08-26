package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorDocAdmin;
import com.dfq.coeffi.visitor.Repositories.VisitorDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VisitorDocumentServiceImpl implements VisitorDocumentService
{
    @Autowired
    VisitorDocumentRepository visitorDocumentRepository;


    @Override
    public VisitorDocAdmin saveDocument(VisitorDocAdmin visitorDocAdmin)
    {
        return visitorDocumentRepository.save(visitorDocAdmin);
    }

    @Override
    public List<VisitorDocAdmin> getAllDocument()
    {
        return visitorDocumentRepository.findAll();
    }

    @Override
    public VisitorDocAdmin getDocument(long id)
    {
        return visitorDocumentRepository.findOne(id);
    }

    @Override
    public void deleteDocumentById(long id)
    {
        visitorDocumentRepository.delete(id);

    }
}
