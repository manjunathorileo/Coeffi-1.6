package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.visitor.Entities.VisitorCategory;
import com.dfq.coeffi.visitor.Repositories.VisitorCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VisitorCategoryServiceImpl implements VisitorCategoryService
{
    @Autowired
    VisitorCategoryRepository visitorCategoryRepository;

    @Override
    public VisitorCategory saveVisitor(VisitorCategory visitorCategory)
    {
        return visitorCategoryRepository.save(visitorCategory) ;
    }

    @Override
    public List<VisitorCategory> getAllVisitor()
    {
        return visitorCategoryRepository.findAll();
    }

    @Override
    public VisitorCategory getVisitor(long id)
    {
        return visitorCategoryRepository.findOne(id) ;
    }

    @Override
    public void deleteVisitorById(long id)
    {
        visitorCategoryRepository.delete(id);

    }
}
