package com.dfq.coeffi.service.finance;


import com.dfq.coeffi.entity.finance.expense.Asset;
import com.dfq.coeffi.entity.user.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AssetService 
{
	Asset createAsset(Asset asset, User loggedUser);
	List<Asset> listAllAsset();
	List<Asset> listAllAsset(int page, int size);
	Optional<Asset> getAsset(long id);
	void deleteAsset(long id);
	List<Asset> getAssetBetweenDate(Date startDate, Date endDate);
	
}
