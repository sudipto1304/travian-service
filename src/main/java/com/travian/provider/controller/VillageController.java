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

import com.travian.provider.request.CelebrationRequest;
import com.travian.provider.request.DeleteTradeRouteRequest;
import com.travian.provider.request.TradeRouteRequest;
import com.travian.provider.request.TroopEvasionRequest;
import com.travian.provider.request.VillageInfoRequest;
import com.travian.provider.response.EvasionResponse;
import com.travian.provider.response.Status;
import com.travian.provider.response.Village;
import com.travian.provider.service.VillageService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/village")
public class VillageController {
	
	private static final Logger Log = LoggerFactory.getLogger(VillageController.class);
	
	@Autowired
	private VillageService service;
	
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = List.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/getInfo", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Village>> getAccountInfo(@RequestBody VillageInfoRequest request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		
		List<Village> response = service.getVillagesInfo(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
	
	
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Status.class),
            @ApiResponse(code = 412, message = "Precondition Failed"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
	@RequestMapping(value="/initiateCelebration", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Status> initiateCelebration(@RequestBody CelebrationRequest request, HttpServletRequest servletRequest, @RequestHeader HttpHeaders headers) throws IOException {
		Status status = service.initiateCelebration(request);
		return new ResponseEntity<>(status, HttpStatus.OK);
	}
	
	
	
	
	

}
