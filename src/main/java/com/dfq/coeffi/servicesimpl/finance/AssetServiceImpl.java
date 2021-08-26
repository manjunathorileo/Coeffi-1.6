package com.dfq.coeffi.servicesimpl.finance;

import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.entity.finance.expense.Asset;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.finance.AssetRepository;
import com.dfq.coeffi.service.finance.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final ApplicationLogService applicationLogService;

	@Autowired
	public AssetServiceImpl(AssetRepository assetRepository, ApplicationLogService applicationLogService)
	{
        this.assetRepository = assetRepository;
        this.applicationLogService = applicationLogService;
	}

	@Override
	public Asset createAsset(Asset asset, User loggedUser)
	{
        Asset persistedObject = assetRepository.save(asset);
        if(persistedObject != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), " Asset "+ asset.getTitle()+" created "+persistedObject.getId(),
                    "POST", loggedUser.getId());
        }
		return persistedObject;
	}

	@Override
	public List<Asset> listAllAsset() 
	{
		return assetRepository.findAll();
	}

	@Override
	public List<Asset> listAllAsset(int page, int size) {
		Page<Asset> assetPage = assetRepository.findAll(new PageRequest(page, size));

		System.out.print("TOTAL ELEMENTS : " + assetPage.getTotalElements());

		List<Asset> assets = assetPage.getContent();
		return assets;
	}

	@Override
	public Optional<Asset> getAsset(long id) 
	{
		return ofNullable(assetRepository.findOne(id));
	}

	@Override
	public void deleteAsset(long id) 
	{
		assetRepository.delete(id);
	}
	@Override
	public List<Asset> getAssetBetweenDate(Date startDate, Date endDate) {
		return assetRepository.getAssetBetweenDate(startDate,endDate);
	}

}
