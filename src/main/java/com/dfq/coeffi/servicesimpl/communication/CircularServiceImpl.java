package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.Circular;
import com.dfq.coeffi.repository.communication.CircularRepository;
import com.dfq.coeffi.service.communication.CircularService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CircularServiceImpl implements CircularService {
	

    private final CircularRepository circularRepository;

	public CircularServiceImpl(CircularRepository circularRepository) {
		this.circularRepository = circularRepository;
	}

	@Override
	public List<Circular> listAllCirculars() {
		return circularRepository.findAll();
	}

	@Override
	public Circular saveCircular(Circular circular) {
		Circular object = circularRepository.saveAndFlush(circular);
		return object;
	}


	@Override
	public void deleteCircular(Long id) {
		circularRepository.delete(id);
	}

	@Override
	public void deleteAllCirculars() {
		circularRepository.deleteAll();
	}

	@Override
	public Circular getCircularById(Long id) {
		return circularRepository.findOne(id);
	}

	/*@Override
	public List<Circular> listAllCircularsByEmployeeId(long employeeId) {
		return circularRepository.findByEmployeesAndApproveStatus(employeeId,true);
	}
*/
	@Override
	public List<Circular> getUnapprovalCircular(long managerId) {
		return circularRepository.findByFirstManagerAndApproveStatus(managerId,false);
	}

	@Override
	public List<Circular> getAllCircularsByStatus(boolean status) {
		return circularRepository.findByApproveStatus(status);
	}

	@Override
	public List<Circular> getAllCircularByApprovedStatus(boolean approveStatus) {
		return circularRepository.findByApproveStatus(approveStatus=true);
	}
}
