package com.dfq.coeffi.SOPDetails.SOPType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SopTypeServiceImpl implements SopTypeService
{
    @Autowired
    SopTypeRepository sopTypeRepository;

    @Override
    public SopType saveSop(SopType sopType) {
        return sopTypeRepository.save(sopType) ;
    }

    @Override
    public List<SopType> getSopList() {
        List<SopType> sopTypes = new ArrayList<>();
        List<SopType> sopTypeList = sopTypeRepository.findAll();
        for (SopType sopTypeObj:sopTypeList) {
            if (sopTypeObj.getStatus().equals(true)){
                sopTypes.add(sopTypeObj);
            }
        }
        return sopTypes;
    }

    @Override
    public Optional<SopType> getSopTypeById(long id) {
        return sopTypeRepository.findById(id);
    }

    @Override
    public SopType deleteSopType(long id) {
        Optional<SopType> sopTypeOptional = sopTypeRepository.findById(id);
        SopType sopType = sopTypeOptional.get();
        sopType.setStatus(false);
        SopType sopTypeDeleted = sopTypeRepository.save(sopType);
        return sopTypeDeleted;
    }
}