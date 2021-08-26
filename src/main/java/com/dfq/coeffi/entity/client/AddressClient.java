package com.dfq.coeffi.entity.client;

import com.dfq.coeffi.entity.client.refClasses.Address;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//@FeignClient(name = "address-service")
public interface AddressClient
{
    @RequestMapping(method = RequestMethod.GET, value = "/address-service/address/{refName}/{refNumber}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Address getAddress(@PathVariable(value = "refName") String refName, @PathVariable(value = "refNumber") long id);

    @RequestMapping(method = RequestMethod.GET, value = "/address/{refName}/{refNumber}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Address getAddressTwo(@PathVariable(value = "refName") String refName, @PathVariable(value = "refNumber") long id);

}