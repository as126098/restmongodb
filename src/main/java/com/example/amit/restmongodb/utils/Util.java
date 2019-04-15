package com.example.amit.restmongodb.utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.amit.restmongodb.exception.InvalidRequestDataFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

public class Util {
	static final Logger log = LoggerFactory.getLogger(Util.class.getName());
	public long startTime;
	public long endTime;

	public static void printExecutionTime(String msg, long startTime, long endTime) {
		log.debug(msg + ":" + ((endTime - startTime) / 1000) + " seconds.");
	}
	
	public static boolean verifyJSONSchema(String jsonString) throws InvalidRequestDataFormatException {
		try {
			// log.info("jsonString " + jsonString);
			ClassLoader classLoader = Util.class.getClassLoader();
			File schemaFile = new File(classLoader.getResource("json-schema/contractrequest_schema.json").getFile());
			log.info("Start validating Contract creation requesting against the predefined JSON schema");
			ProcessingReport report = JSONValidationUtil.isJsonValidForReport(schemaFile, jsonString);
			if (!report.isSuccess()) {
				log.error("Bad input request format, below is the validation report.");
				Iterator<ProcessingMessage> itr = report.iterator();
				throw new InvalidRequestDataFormatException(Util.getErrorsList(report, true));
			}
			log.info("Contract creation request is validated against the predefined JSON schema");
		} catch (IOException | ProcessingException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static String getErrorsList(ProcessingReport report, boolean onlyErrors) {
		StringBuilder jsonValidationErrors = new StringBuilder();
		String messageText = null;
		for (ProcessingMessage processingMessage : report) {
			
			/*messageNode = (ObjectNode) processingMessage.asJson();
			dataInstance = messageNode.get("instance").get("pointer").asText();
			
			if (dataInstance.contains("/"))
				dataInstance = dataInstance.substring(1, dataInstance.length()).replaceAll("/", " -> ");
			messageText = "Validation failed for " + dataInstance + " : " + messageNode.get("message").asText();
*/
			messageText = processingMessage.getMessage();
			if (onlyErrors && LogLevel.ERROR.equals(processingMessage.getLogLevel())) {
				jsonValidationErrors.append(messageText).append(" || ");
			}
			else if (!onlyErrors) {
				jsonValidationErrors.append(messageText).append(" || ");
			}
		}

		if (jsonValidationErrors.toString().contains("||")) {
			return jsonValidationErrors.toString().trim().substring(0,
					jsonValidationErrors.toString().trim().length() - 2);
		}

		return jsonValidationErrors.toString();
	}
	
	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		if (object == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}

}
