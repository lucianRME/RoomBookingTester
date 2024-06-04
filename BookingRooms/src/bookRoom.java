import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.junit.Ignore;
import org.junit.Test;

import com.eclipsesource.json.JsonObject;

/**
 * @author Lucian Irimie
 * 
 */
public class bookRoom {

	String urlAvailability = "http://192.168.0.100:9090/checkAvailability/";
	String urlBookRoom = "http://192.168.0.100:9090/bookRoom";
	byte[] responseBodyGet = null;
	HttpResponse responseBodyPost;

	/**
	 * Steps to reproduce: 1. HTTP GET the rooms available and Book a Room using
	 * POST
	 * 
	 * Expected results: 200 - OK. Available room is booked. The number of
	 * booked rooms should decrease from the number of available rooms.
	 * 
	 * 
	 * Actual results: 200 - OK. Available room is booked. The number of
	 * available rooms decreases with a different value than the number of
	 * booked rooms
	 */

	@Test
	@Ignore
	// Tests should be independent but can't be run at the same time because of
	// the server lack of capability
	// to reset the number of available rooms
	// Server must be reseted after each test run!
	public void positive_BookOneAvailableRoom_BugNumberOfRoomsAvailableAfterBooking() {

		String date = "2013-12-12";
		String line = urlAvailability + date;
		String numberOfDays = "1";
		int numberOfAvailableRooms = -1;
		int numberOfRoomsAvailableAfterBooking = -1;

		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(line);
		PostMethod post = new PostMethod(urlBookRoom);

		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// GET number of available rooms
			int statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject2 = JsonObject.readFrom(decoded);
			numberOfAvailableRooms = jsonObject2.get("rooms_available").asInt();

			// POST - book a room
			JsonObject jsonObject = new JsonObject().add("numOfDays",
					numberOfDays).add("checkInDate", date);
			post.setRequestBody(jsonObject.toString());

			client = new HttpClient();
			client.setTimeout(3000);

			int statusCodePost = client.executeMethod(post);
			String myPostResponse = post.getResponseBodyAsString();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodePost);

			// GET the number of available rooms after booking
			get = new GetMethod(line);
			statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			decoded = new String(responseBodyGet, "UTF-8");

			jsonObject2 = JsonObject.readFrom(decoded);
			numberOfRoomsAvailableAfterBooking = jsonObject2.get(
					"rooms_available").asInt();

			if (numberOfAvailableRooms != numberOfRoomsAvailableAfterBooking + 1) {
				fail("Booked rooms available missmatched! "
						+ " RoomsBeforeBooking: " + numberOfAvailableRooms
						+ " RoomsAfterBooking: "
						+ numberOfRoomsAvailableAfterBooking);
			}

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			post.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. HTTP GET the rooms available and Book more Rooms
	 * than available using POST
	 * 
	 * Expected results: 200 - OK. Available room is booked. The number of
	 * booked rooms should decrease from the number of available rooms.
	 * 
	 * 
	 * Actual results: 200 - OK. Available room is booked. The number of
	 * available rooms decreases with a different value than the number of
	 * booked rooms
	 */

	@Test
	@Ignore
	// Tests should be independent but can't be run at the same time because of
	// the server lack of capability
	// to reset the number of available rooms
	// Server must be reseted after each test run!
	public void negative_BookMoreRoomsThanAvailable_BugNumberOfRoomsAvailableBellow0() {

		String date = "2013-12-12";
		String line = urlAvailability + date;
		String numberOfDays = "1";
		int numberOfAvailableRooms = -1;
		int numberOfRoomsAvailableAfterBooking = -1;
		int i = 0;

		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(line);
		PostMethod post = new PostMethod(urlBookRoom);

		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// GET number of available rooms
			int statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject2 = JsonObject.readFrom(decoded);
			numberOfAvailableRooms = jsonObject2.get("rooms_available").asInt();

			for (i = 0; i < 11; i++) {

				// POST - book a room
				JsonObject jsonObject = new JsonObject().add("numOfDays",
						numberOfDays).add("checkInDate", date);
				post.setRequestBody(jsonObject.toString());

				client = new HttpClient();
				client.setTimeout(3000);

				int statusCodePost = client.executeMethod(post);
				String myPostResponse = post.getResponseBodyAsString();

				assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
						statusCodePost);
			}

			// GET the number of available rooms after booking
			statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			decoded = new String(responseBodyGet, "UTF-8");

			jsonObject2 = JsonObject.readFrom(decoded);
			numberOfRoomsAvailableAfterBooking = jsonObject2.get(
					"rooms_available").asInt();

			if (numberOfRoomsAvailableAfterBooking < 0) {
				fail("Number of rooms available bellow 0! "
						+ " RoomsAfterBooking: "
						+ numberOfRoomsAvailableAfterBooking);
			}

			System.out.println(numberOfRoomsAvailableAfterBooking);

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			post.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. Book a room for a period of time then verify it
	 * has been un-booked after that period of time
	 * 
	 * Expected results: 200 - OK. Room becomes available again.
	 * 
	 * 
	 * Actual results: 200 - OK. Room becomes available again.
	 */

	@Test
	@Ignore
	// Tests should be independent but can't be run at the same time because of
	// the server lack of capability
	// to reset the number of available rooms
	// Server must be reseted after each test run!
	public void positive_VerifyCheckOutDate() {

		String date = "2013-05-30";
		String dateToVerifyAfterBooking = "2013-06-01";
		String line = urlAvailability + date;
		String line2 = urlAvailability + dateToVerifyAfterBooking;
		String numberOfDays = "2";
		int numberOfAvailableRooms = -1;
		int numberOfRoomsAvailableAfterBooking = -1;
		int i = 0;

		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(line);
		PostMethod post = new PostMethod(urlBookRoom);

		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// GET number of available rooms
			int statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded1 = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject2 = JsonObject.readFrom(decoded1);
			numberOfAvailableRooms = jsonObject2.get("rooms_available").asInt();

			// POST - book a room
			JsonObject jsonObject = new JsonObject().add("numOfDays",
					numberOfDays).add("checkInDate", date);
			post.setRequestBody(jsonObject.toString());

			client = new HttpClient();
			client.setTimeout(3000);

			int statusCodePost = client.executeMethod(post);
			String myPostResponse = post.getResponseBodyAsString();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodePost);

			// GET the number of available rooms after booking
			get = new GetMethod(line2);
			statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded2 = new String(responseBodyGet, "UTF-8");

			jsonObject2 = JsonObject.readFrom(decoded2);
			numberOfRoomsAvailableAfterBooking = jsonObject2.get(
					"rooms_available").asInt();

			if (numberOfAvailableRooms != numberOfRoomsAvailableAfterBooking) {
				fail("Rooms should have been checkedout! "
						+ " RoomsBeforeBooking: " + numberOfAvailableRooms
						+ " RoomsAfterBooking: "
						+ numberOfRoomsAvailableAfterBooking);
			}

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			post.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. Book a room with invalid JSON
	 * 
	 * Expected results: 400 - Bad request.
	 * 
	 * 
	 * Actual results: 400 - Bad request.
	 */

	@Test
	// @Ignore
	// Tests should be independent but can't be run at the same time because of
	// the server lack of capability
	// to reset the number of available rooms
	// Server must be reseted after each test run!
	public void negative_BookRoomInvalidJSON() {

		String date = "2013-05-30";
		String dateToVerifyAfterBooking = "2013-06-01";
		String line = urlAvailability + date;
		String line2 = urlAvailability + dateToVerifyAfterBooking;
		String numberOfDays = "2";
		int numberOfAvailableRooms = -1;

		int i = 0;

		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(line);
		PostMethod post = new PostMethod(urlBookRoom);

		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// GET number of available rooms
			int statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded1 = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject2 = JsonObject.readFrom(decoded1);
			numberOfAvailableRooms = jsonObject2.get("rooms_available").asInt();

			// POST - book a room
			JsonObject jsonObject = new JsonObject().add("numOfDaysABCD",
					numberOfDays).add("checkInDate", date);
			post.setRequestBody(jsonObject.toString());

			client = new HttpClient();
			client.setTimeout(3000);

			int statusCodePost = client.executeMethod(post);
			String myPostResponse = post.getResponseBodyAsString();

			assertEquals("400 Bad request status expected! ",
					HttpStatus.SC_BAD_REQUEST, statusCodePost);

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			post.releaseConnection();
		}

	}

	/**
	 * Steps to reproduce: 1. Verify the price for a two different days for
	 * which the user will book then calculate the price for both and compare it
	 * with the price from the server.
	 * 
	 * Expected results: 200 - OK. The total price is based on the price of the
	 * room for each night during the booking.
	 * 
	 * 
	 * Actual results: 200 - OK. Total price is not calulated correctly
	 */

	@Test
	// @Ignore
	// Tests should be independent but can't be run at the same time because of
	// the server lack of capability
	// to reset the number of available rooms
	// Server must be reseted after each test run!
	public void positive_VerifyPrice_BugTotalPrice() {

		String date1 = "2013-05-30";
		String date2 = "2013-05-31";
		String dateToVerifyAfterBooking = "2013-06-01";
		String line_d1 = urlAvailability + date1;
		String line_d2 = urlAvailability + date2;
		String line2 = urlAvailability + dateToVerifyAfterBooking;
		String numberOfDays = "2";
		int numberOfAvailableRooms = -1;
		int numberOfRoomsAvailableAfterBooking = -1;
		int i = 0;
		int sum1 = 0;
		int sum2 = 0;

		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(line_d1);
		PostMethod post = new PostMethod(urlBookRoom);

		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// GET price 1
			int statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			String decoded1 = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject2 = JsonObject.readFrom(decoded1);
			int price1 = jsonObject2.get("price").asInt();

			// GET price 1
			statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			decoded1 = new String(responseBodyGet, "UTF-8");

			JsonObject jsonObject3 = JsonObject.readFrom(decoded1);
			int price2 = jsonObject3.get("price").asInt();

			sum1 = price1 + price2;

			// POST - book a room
			JsonObject jsonObject = new JsonObject().add("numOfDays",
					numberOfDays).add("checkInDate", dateToVerifyAfterBooking);
			post.setRequestBody(jsonObject.toString());

			client = new HttpClient();
			client.setTimeout(3000);

			int statusCodePost = client.executeMethod(post);
			String myPostResponse = post.getResponseBodyAsString();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodePost);

			String myPostResponse2 = post.getResponseBodyAsString();
			decoded1 = new String(myPostResponse2);

			JsonObject jsonObject4 = JsonObject.readFrom(decoded1);
			int totalPrice = jsonObject4.get("totalPrice").asInt();

			// GET the number of available rooms after booking
			get = new GetMethod(line2);
			statusCodeGet = client.executeMethod(get);
			responseBodyGet = get.getResponseBody();

			assertEquals("200 OK status expected! ", HttpStatus.SC_OK,
					statusCodeGet);

			if (sum1 != totalPrice) {
				fail("Calculated price mismatch! " + " Sum1: " + sum1
						+ " Sum2: " + totalPrice);
			}

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			get.releaseConnection();
			post.releaseConnection();
		}

	}
}