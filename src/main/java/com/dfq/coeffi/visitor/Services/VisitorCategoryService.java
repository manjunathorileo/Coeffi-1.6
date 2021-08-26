package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorCategory;

import java.util.List;

public interface VisitorCategoryService
{
    VisitorCategory saveVisitor(VisitorCategory visitorCategory);

    List<VisitorCategory> getAllVisitor();

    VisitorCategory getVisitor(long id);

    void deleteVisitorById(long id);
}
