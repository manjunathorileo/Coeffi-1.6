package com.dfq.coeffi.auditlog.issue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IssueTrackerService 
{
	IssueTracker createIssueTracker(IssueTracker issueTracker);
	List<IssueTracker> listAllIssueTracker();
	Optional<IssueTracker> getIssueTracker(long id);
	void deleteIssueTracker(long id);
	List<IssueTracker> getIssueTrackerListByDate(Date date);
	IssueTracker recordIssueTracker(String errorStack, String moduleName, Priority priority);
}
