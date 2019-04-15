package com.example.amit.restmongodb.exception;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.validation.ObjectError;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "error")
public class ErrorJson {
    @ApiModelProperty(name = "status", required = true, dataType = "Integer", notes = "holds status value")
    public Integer status;
    @ApiModelProperty(name = "error", required = true, dataType = "String", notes = "holds error value")
    public String error;
    @ApiModelProperty(name = "message", required = true, dataType = "String", notes = "holds error message")
    public String message;
    @ApiModelProperty(name = "timeStamp", required = true, dataType = "String", notes = "holds error timeStamp")
    public String timeStamp;

    public ErrorJson() {

    }

    public ErrorJson(int status, Map<String, Object> errorAttributes) {

	this.status = status;
	this.error = (String) errorAttributes.get("error");
	this.message = (String) errorAttributes.get("message");
	if (errorAttributes.get("errors") != null) {
	    List<ObjectError> list = (List<ObjectError>) errorAttributes.get("errors");
	    for (ObjectError error : list) {
		this.message += "; " + error.getDefaultMessage();
	    }
	}
	this.timeStamp = errorAttributes.get("timestamp").toString();

    }

}