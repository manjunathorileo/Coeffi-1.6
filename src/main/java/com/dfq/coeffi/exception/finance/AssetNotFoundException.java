/**
 * By Kapil Kumar
 * to handle asset not found exception
 */

package com.dfq.coeffi.exception.finance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssetNotFoundException  extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AssetNotFoundException(String exceptionMsg){
        super(exceptionMsg);
        log.warn("[Asset Module]", exceptionMsg);
    }
    public AssetNotFoundException(){}
}