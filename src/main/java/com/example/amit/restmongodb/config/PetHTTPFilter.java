package com.example.amit.restmongodb.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.amit.restmongodb.exception.ErrorJson;
import com.example.amit.restmongodb.utils.JSONValidationUtil;
import com.example.amit.restmongodb.utils.Util;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import io.micrometer.core.instrument.util.IOUtils;

public class PetHTTPFilter extends OncePerRequestFilter {
	static final Logger log = LoggerFactory.getLogger(PetHTTPFilter.class.getName());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestJSON = null;
		log.info("Inside Filter for vallidating input JSON before creating contract");
		log.info("request uri "  + request.getRequestURL());
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		PetHTTPRequestWrapper petRequestWrapper;
		try {
			petRequestWrapper = new PetHTTPRequestWrapper(httpServletRequest);
		} catch (Exception ex) {
			log.warn("Unable to wrap the request", ex);
			throw new ServletException("Unable to wrap the request", ex);
		}
		try {
			ServletInputStream is = petRequestWrapper.getInputStream();
			requestJSON = IOUtils.toString(is);
			if(request.getRequestURL().toString().endsWith("create")) {
				JSONParser jsonParser = new JSONParser();
				Object obj = jsonParser.parse(requestJSON);
//						new FileReader(requestJSON));
				JSONObject jsonObject = (JSONObject)obj;
				// Converting object to POJO
				requestJSON = jsonObject.toJSONString();
				logger.info("jsonString " + requestJSON);
			}
			log.info("is : " + requestJSON);
		} catch (Exception ex) {
			log.warn("Invalid request", ex);
			throw new ServletException("Invalid request", ex);
		}
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File schemaFile = new File(classLoader.getResource("json-schema/pet_schema.json").getFile());
			log.info("Start validating Contract creation requesting against the predefined JSON schema");
			ProcessingReport report = JSONValidationUtil.isJsonValidForReport(schemaFile, requestJSON);
			if (!report.isSuccess()) {
				log.error("Bad input request format, below is the validation report.");
				Iterator<ProcessingMessage> itr = report.iterator();
				Map<String, Object> errorAttributes = new HashMap<String, Object>();
				errorAttributes.put("error", "Unprocessable Data");
				errorAttributes.put("message", Util.getErrorsList(report, true));
				errorAttributes.put("timestamp", new Date());
				ErrorJson errorJson = new ErrorJson(422,errorAttributes);
				response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
				PrintWriter pw =  response.getWriter();
				pw.write(Util.convertObjectToJson(errorJson));
				pw.flush();
				pw.close();
				
			} else {
				log.info("Filter : PetHTTPFilter : Incoming HTTP Request is validated against the predefined JSON schema");
				filterChain.doFilter(petRequestWrapper, response);
			}
			
		} catch (IOException | ProcessingException e) {
			e.printStackTrace();
		}
		
		
	}

}
