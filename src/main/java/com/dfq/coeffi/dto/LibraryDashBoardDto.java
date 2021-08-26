package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LibraryDashBoardDto {
    long totalBookCount;
    long issuedCount;
    long overdueCount;
    long idleCount;
}