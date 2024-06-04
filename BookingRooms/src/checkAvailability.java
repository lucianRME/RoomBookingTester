import static org.junit.Assert.*;

import java.io.IOException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

/**
 * @author Lucian Irimie
 * 
 */
public class checkAvailability {

	String url = "http://192.168.0.100:9090/checkAvailability/";
	byte[] responseBody = null;

	/**
	 * Steps to reproduce: 1. HTTP GET with no date.
	 * 
	 * Expected results: 400 - Bad Request. Invalid or missing date message.
	 * 
	 * Actual results: 400 - Bad Request. Invalid or missing date message.
	 */

	@Test
	public void negative_GetAvailabilityNodate() {

		String date = "";
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();

			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());
			assertTrue(
					"Invalid or missing date " + date,
					method.getResponseBodyAsString()
							.contains(
									"Invalid or missing date \''"
											+ date
											+ ". Valid date format is: yyyy-mm-dd, e.g. 2013-04-20."));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent future date
	 * 
	 * Expected results: 200 - OK. Returned date is same as input date.
	 * 
	 * Actual results: 200 - OK. Returned date is same as input date.
	 */

	@Test
	public void positive_GetAvailabilityProperDateFormat() {

		String date = "2013-10-10";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCode);
			assertEquals("OK Expected", "OK", method.getStatusLine()
					.getReasonPhrase());
			assertTrue("Expected date: " + date, method
					.getResponseBodyAsString().contains("\"date\":\"" + date));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent past date
	 * 
	 * Expected results: 400 - Bad request. Date from the past should not be
	 * allowed.
	 * 
	 * Actual results: 200 - OK. Returned date is same as input date.
	 */

	@Test
	public void negative_GetAvailabilityProperDateFormatFromThePast_Bug_PastDate() {

		String date = "2000-10-10";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();

			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent past date year 0000
	 * 
	 * Expected results: 400 - Bad request. Date from the past should not be
	 * allowed. Input date should not be modified by the server.
	 * 
	 * Actual results: 200 - OK. Returned date is different than input date.
	 */

	@Test
	public void negative_GetAvailabilityProperDateFormatFromThePast_Bug_Year0000() {

		String date = "0000-10-10";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();
			String stringResponseBody = method.getResponseBodyAsString();
			String expectedResponseBody = "\"date\":\"" + date;

			assertTrue("Expected date: " + date,
					stringResponseBody.contains(expectedResponseBody));
			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent past date year 0000
	 * 
	 * Expected results: 400 - Bad request. Date from the past should not be
	 * allowed. Input date should not be modified by the server.
	 * 
	 * Actual results: 200 - OK. Returned date is different than input date.
	 */

	@Test
	public void negative_GetAvailabilityProperDateFormatFromThePast_Bug_YearFormat() {

		String date = "20101010";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();
			String stringResponseBody = method.getResponseBodyAsString();
			String expectedResponseBody = "\"date\":\"" + date;

			assertEquals("Expected date: " + date, expectedResponseBody,
					stringResponseBody);
			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent invalid month 20
	 * 
	 * Expected results: 400 - Bad request. Invalid month should not be allowed.
	 * 
	 * Actual results: 200 - OK. Invalid date is accepted.
	 */

	@Test
	public void negative_GetAvailabilityInvalidMonth_Bug_MonthValue() {

		String date = "2013-20-10";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();
			method.getResponseBodyAsString();
			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}
	
	/**
	 * Steps to reproduce: 1. HTTP GET with recent invalid month days e.g. 2013-06-31
	 * 
	 * Expected results: 400 - Bad request. Invalid month days value should not be allowed.
	 * 
	 * Actual results: 200 - OK. Invalid date is accepted.
	 */

	@Test
	public void positive_GetAvailabilityInvalidMonthDays_Bug_InvalidMonthDays() {

		String date = "2013-06-31";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();
			method.getResponseBodyAsString();
			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET with recent invalid string date
	 * 
	 * Expected results: 400 - Bad request. String date should not be allowed.
	 * 
	 * Actual results: 400 - Bad request. Invalid date is not accepted.
	 */

	@Test
	public void negative_GetAvailabilityDateAsString() {

		String date = "2013_June_4";
		String line = url + date;

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(line);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			int statusCode = client.executeMethod(method);

			responseBody = method.getResponseBody();
			method.getResponseBodyAsString();
			assertEquals("Bad request status code expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCode);
			assertEquals("Bad Request Expected", "Bad Request", method
					.getStatusLine().getReasonPhrase());

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

	}
}
