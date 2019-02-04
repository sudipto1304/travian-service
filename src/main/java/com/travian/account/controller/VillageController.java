package com.travian.account.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.travian.account.request.VillageInfoRequest;
import com.travian.account.response.AccountInfoResponse;
import com.travian.account.response.Village;
import com.travian.account.service.VillageService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/village")
public class VillageController {
	
	private static final Logger Log = LoggerFactory.getLogger(VillageController.class);
	
	@Autowired
	private VillageService service;
	
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = AccountInfoResponse.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/getInfo", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Village>> getAccountInfo(@RequestBody VillageInfoRequest request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		if(Log.isDebugEnabled())
			Log.debug("AccoiuntInfo Request::"+request);
		List<Village> response = service.getVillagesInfo(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
