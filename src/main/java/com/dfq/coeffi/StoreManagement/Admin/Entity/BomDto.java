package com.dfq.coeffi.StoreManagement.Admin.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BomDto {
    private String bomNumber;
    private String bomName;
    private List<BomItems> bomItemsList;
}
