package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorDocAdmin;

import java.util.List;

public interface VisitorDocumentService
{
    VisitorDocAdmin saveDocument(VisitorDocAdmin visitorDocAdmin);

    List<VisitorDocAdmin> getAllDocument();

    VisitorDocAdmin getDocument(long id);

    void deleteDocumentById(long id);
}
