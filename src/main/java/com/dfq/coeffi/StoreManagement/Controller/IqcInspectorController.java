package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.StoreManagement.Repository.IqcInspectorItemsRepository;
import com.dfq.coeffi.StoreManagement.Repository.IqcInspectorRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class IqcInspectorController extends BaseController {
    @Autowired
    IqcInspectorRepository iqcInspectorRepository;
    @Autowired
    IqcInspectorItemsRepository iqcInspectorItemsRepository;
    @Autowired
    EmployeeService employeeService;

    @PostMapping("goods-receiver-save")
    ResponseEntity<IqcInspector> saveGoodsReceiver(@RequestBody IqcInspector iqcInspector) {
        iqcInspector.setItemStatus(MaterialsEnum.New);
        List<IqcInspectorItems> iqcInspectorItems = iqcInspector.getIqcInspectorItemsList();
        for (IqcInspectorItems i : iqcInspectorItems) {
            i.setStatus(MaterialStatus.New);
        }
        iqcInspectorRepository.save(iqcInspector);

        return new ResponseEntity<>(iqcInspector, HttpStatus.OK);

    }

    @GetMapping("get-goods-receiver")
    ResponseEntity<List<IqcInspector>> getGoodsReceiver() {
        List<IqcInspector> iqcInspectorList = iqcInspectorRepository.findAll();

        return new ResponseEntity<>(iqcInspectorList, HttpStatus.OK);

    }

    @GetMapping("get-iqc-goods-receiver")
    ResponseEntity<List<IqcInspector>> getIqcGoodsReceiver() {
        List<IqcInspector> iqcInspectorList = iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1 = new ArrayList<>();
        for (IqcInspector i : iqcInspectorList) {
            if (i.getItemStatus().equals(MaterialsEnum.New)) {
                iqcInspectorList1.add(i);
            }

        }
        return new ResponseEntity<>(iqcInspectorList1, HttpStatus.OK);
    }

    @PostMapping("iqc-approve")
    public ResponseEntity<IqcInspector> Approve(@RequestBody IqcInspectorItemsDto expensesDto) throws Exception {
        Date date = new Date();
        Optional<Employee> employee = employeeService.getEmployee(expensesDto.getEmployeeId());
        Employee emp = employee.get();
        IqcInspector employeeRequest = new IqcInspector();
        employeeRequest.setItemStatus(MaterialsEnum.Approved);
        employeeRequest.setPo(expensesDto.getPo());
        employeeRequest.setGrn(expensesDto.getGrn());
        employeeRequest.setSupplierName(expensesDto.getSupplierName());
        employeeRequest.setReceivedDate(expensesDto.getReceivedDate());
        employeeRequest.setInspectionDate(expensesDto.getInspectionDate());
        employeeRequest.setItemType(expensesDto.getItemType());
        employeeRequest.setInspectorName(employee.get().getFirstName() + " " + employee.get().getLastName());
        employeeRequest.setMarkedOn(date);
        employeeRequest.setIqcInspectorItemsList(expensesDto.getIqcInspectorItemsList());
        for (IqcInspectorItems i : expensesDto.getIqcInspectorItemsList()) {
            IqcInspectorItems iqcInspectorItems = new IqcInspectorItems();
            iqcInspectorItems.setItemName(i.getItemName());
            iqcInspectorItems.setItemCategory(i.getItemCategory());
            iqcInspectorItems.setItemType(expensesDto.getItemType());
            iqcInspectorItems.setOrderedCount(i.getOrderedCount());
            iqcInspectorItems.setDefectedCount(i.getDefectedCount());
            iqcInspectorItems.setReceivedCount(i.getReceivedCount());
            iqcInspectorItems.setAvailableStock(i.getOrderedCount());
            iqcInspectorItems.setReceivedDate(expensesDto.getReceivedDate());
            iqcInspectorItems.setInspectionDate(expensesDto.getInspectionDate());
            iqcInspectorItems.setInspectorName(employee.get().getFirstName() + " " + employee.get().getLastName());
            iqcInspectorItems.setSupplierName(expensesDto.getSupplierName());
            iqcInspectorItems.setStatus(MaterialStatus.Approved);
            iqcInspectorItemsRepository.save(iqcInspectorItems);
        }
        iqcInspectorRepository.save(employeeRequest);
        return new ResponseEntity<>(employeeRequest, HttpStatus.CREATED);
    }

    @PostMapping("iqc-reject")
    public ResponseEntity<IqcInspector> Reject(@RequestBody IqcInspectorItemsDto expensesDto) throws Exception {
        Date date = new Date();
        Optional<Employee> employee = employeeService.getEmployee(expensesDto.getEmployeeId());
        Employee emp = employee.get();
        IqcInspector employeeRequest = new IqcInspector();
        employeeRequest.setItemStatus(MaterialsEnum.Rejected);
        employeeRequest.setPo(expensesDto.getPo());
        employeeRequest.setGrn(expensesDto.getGrn());
        employeeRequest.setSupplierName(expensesDto.getSupplierName());
        employeeRequest.setReceivedDate(expensesDto.getReceivedDate());
        employeeRequest.setInspectionDate(expensesDto.getInspectionDate());
        employeeRequest.setInspectorName(employee.get().getFirstName() + " " + employee.get().getLastName());
        employeeRequest.setMarkedOn(date);
        employeeRequest.setIqcInspectorItemsList(expensesDto.getIqcInspectorItemsList());
        for (IqcInspectorItems i : expensesDto.getIqcInspectorItemsList()) {
            IqcInspectorItems iqcInspectorItems = new IqcInspectorItems();
            iqcInspectorItems.setItemName(i.getItemName());
            iqcInspectorItems.setItemCategory(i.getItemCategory());
            iqcInspectorItems.setItemType(expensesDto.getItemType());
            iqcInspectorItems.setOrderedCount(i.getOrderedCount());
            iqcInspectorItems.setDefectedCount(i.getDefectedCount());
            iqcInspectorItems.setReceivedCount(i.getReceivedCount());
            iqcInspectorItems.setAvailableStock(i.getOrderedCount());
            iqcInspectorItems.setReceivedDate(expensesDto.getReceivedDate());
            iqcInspectorItems.setInspectionDate(expensesDto.getInspectionDate());
            iqcInspectorItems.setInspectorName(employee.get().getFirstName() + " " + employee.get().getLastName());
            iqcInspectorItems.setSupplierName(expensesDto.getSupplierName());
            iqcInspectorItems.setStatus(MaterialStatus.Rejected);
            iqcInspectorItemsRepository.save(iqcInspectorItems);
        }
        iqcInspectorRepository.save(employeeRequest);
        return new ResponseEntity<>(employeeRequest, HttpStatus.CREATED);
    }

    @GetMapping("get-iqc-approved-items")
    public ResponseEntity<List<IqcInspector>> getApprovedIqc() {
        List<IqcInspector> iqcInspectorList = iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1 = new ArrayList<>();

        for (IqcInspector i : iqcInspectorList) {
            if (i.getItemStatus().equals(MaterialsEnum.Approved)) {
                iqcInspectorList1.add(i);
            }
        }
        return new ResponseEntity<>(iqcInspectorList, HttpStatus.OK);
    }

    @GetMapping("get-iqc-rejected-items")
    public ResponseEntity<List<IqcInspector>> getRejectedIqc() {
        List<IqcInspector> iqcInspectorList = iqcInspectorRepository.findAll();
        List<IqcInspector> iqcInspectorList1 = new ArrayList<>();

        for (IqcInspector i : iqcInspectorList) {
            if (i.getItemStatus().equals(MaterialsEnum.Rejected)) {
                iqcInspectorList1.add(i);
            }
        }
        return new ResponseEntity<>(iqcInspectorList1, HttpStatus.OK);
    }

    @GetMapping("previous-ordered-items-list")
    public ResponseEntity<List<IqcInspector>> getPreviousIqc() {
        List<IqcInspector> iqcInspectorList = iqcInspectorRepository.findAll();
        return new ResponseEntity<>(iqcInspectorList, HttpStatus.OK);
    }

    @GetMapping("current-stock")
    public ResponseEntity<List<IqcInspectorItems>> getCurrentStock() {
        List<IqcInspectorItems> iqcInspectorItemsList = iqcInspectorItemsRepository.findAll();
        return new ResponseEntity<>(iqcInspectorItemsList, HttpStatus.OK);
    }

    @GetMapping("get-stock")
    public ResponseEntity<List<IqcInspectorItems>> getItem() {
        List<IqcInspectorItems> iqcInspectorItemsList = iqcInspectorItemsRepository.findAll();
        return new ResponseEntity<>(iqcInspectorItemsList, HttpStatus.OK);
    }

    @GetMapping("get-iqc-approve-items-details/{id}")
    public ResponseEntity<ApproveItemsDto> getApproveItems(@PathVariable("id") long id) {
        ApproveItemsDto approveItemsDto = new ApproveItemsDto();
        Date date = new Date();
        approveItemsDto.setInspectionDate(date);
        Optional<Employee> employee = employeeService.getEmployee(id);
        approveItemsDto.setInspectorName(employee.get().getFirstName() + " " + employee.get().getLastName());

        return new ResponseEntity<>(approveItemsDto, HttpStatus.OK);
    }


}
