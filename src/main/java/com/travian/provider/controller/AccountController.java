package com.travian.provider.controller;

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

import com.travian.provider.request.AccountInfoRequest;
import com.travian.provider.request.GameWorld;
import com.travian.provider.request.InitiateAdventureRequest;
import com.travian.provider.response.AccountInfoResponse;
import com.travian.provider.response.Adventure;
import com.travian.provider.response.Status;
import com.travian.provider.service.AccountService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	private static final Logger Log = LoggerFactory.getLogger(AccountController.class);
	
	@Autowired
	private AccountService service;
	
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = AccountInfoResponse.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/getInfo", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountInfoResponse> getAccountInfo(@RequestBody AccountInfoRequest request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		if(Log.isDebugEnabled())
			Log.debug("AccoiuntInfo Request::"+request);
		AccountInfoResponse response = service.getAccountInfo(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = AccountInfoResponse.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/getInfoOnly", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountInfoResponse> getAccountInfo(@RequestBody GameWorld request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		if(Log.isDebugEnabled())
			Log.debug("AccoiuntInfo Request::"+request);
		AccountInfoResponse response = service.getAccountInfoWL(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = List.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/getAdventureList", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Adventure>> getAdventureList(@RequestBody GameWorld request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		if(Log.isDebugEnabled())
			Log.debug("AccoiuntInfo Request::"+request);
		List<Adventure> adventures = service.getAdventureList(request);
		return new ResponseEntity<>(adventures, HttpStatus.CREATED);
	}
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Status.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/hero/sendToAdventure", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Status> initiateAdventure(@RequestBody InitiateAdventureRequest request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		if(Log.isDebugEnabled())
			Log.debug("AccoiuntInfo Request::"+request);
		Status status = service.initiateAdventure(request);
		return new ResponseEntity<>(status, HttpStatus.CREATED);
	}



}
