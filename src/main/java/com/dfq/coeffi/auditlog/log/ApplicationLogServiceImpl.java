package com.dfq.coeffi.auditlog.log;

import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class ApplicationLogServiceImpl implements ApplicationLogService {

	private final ApplicationLogRepository applicationLogRepository;

	@Autowired
	public ApplicationLogServiceImpl(ApplicationLogRepository applicationLogRepository)
	{
		this.applicationLogRepository = applicationLogRepository;
		
	}

    @Override
    public List<ApplicationLog> listAllApplicationLogs() {
        return applicationLogRepository.findAll();
    }

    @Override
    public Optional<ApplicationLog> getApplicationLog(long id) {
        return ofNullable(applicationLogRepository.findOne(id));
    }

    @Override
    public List<ApplicationLog> getApplicationLogsByDate(Date date) {
        return applicationLogRepository.getIApplicationLogsByDate(date);
    }

    @Override
    public List<ApplicationLog> getLoggedUserLogs(long loggedUserId) {
        return applicationLogRepository.loggedUserLogs(loggedUserId);
    }

    @Override
    public ApplicationLog recordApplicationLog(String operationDoneBy, String description, String operationType, long loggedUserId) {
        ApplicationLog applicationLog = new ApplicationLog();
        applicationLog.setDescription(description);
        applicationLog.setLoggedOn(DateUtil.getTodayDate());
        applicationLog.setOperationDoneBy(operationDoneBy);
        applicationLog.setOperationType(operationType);
        applicationLog.setLoggedUserId(loggedUserId);
        return applicationLogRepository.save(applicationLog);
    }
}
