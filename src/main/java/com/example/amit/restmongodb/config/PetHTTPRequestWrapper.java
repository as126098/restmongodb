package com.example.amit.restmongodb.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PetHTTPRequestWrapper extends HttpServletRequestWrapper {
	static final Logger log = LoggerFactory.getLogger(PetHTTPRequestWrapper.class.getName());

	private final String jsonPayload;

	public PetHTTPRequestWrapper(HttpServletRequest request) {

		super(request);

		// read the original payload into the xmlPayload variable
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			// read the payload into the StringBuilder
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				// make an empty string since there is no payload
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			log.error("Error reading the request payload", ex);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException iox) {
					// ignore
				}
			}
		}
		jsonPayload = stringBuilder.toString();
	}

	/**
	 * Override of the getInputStream() method which returns an InputStream that
	 * reads from the stored XML payload string instead of from the request's
	 * actual InputStream.
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {

		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonPayload.getBytes());
		ServletInputStream inputStream = new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener arg0) {
			}
		};
		return inputStream;
	}
}
