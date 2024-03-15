package com.hrsgroup.hotelbookingapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
@RequestMapping("/bookings")
public class HotelBookingApi {

  private static final Logger LOGGER = LogManager.getLogger(HotelBookingApi.class);

  private List < Booking > bookings = new ArrayList < > ();

  public static void main(String[] args) {
    SpringApplication.run(HotelBookingApi.class, args);
  }

  @GetMapping("/search")
  public ResponseEntity < List < Booking >> searchAvailableRooms() {
    LOGGER.info("Searching available rooms.");
    return new ResponseEntity < > (bookings, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity < Booking > createBooking(@RequestBody Map < String, Object > bookingMap) {
    if (bookingMap == null || bookingMap.isEmpty()) {
      return new ResponseEntity < > (HttpStatus.BAD_REQUEST);
    }

    /*better logic will be to add a condition to check if room is available from the DB before processing.
    This can be due to multiple session trying for same room
    */
    Booking booking = new Booking();
    // Set ID
    booking.setId(UUID.randomUUID().toString());
    booking.setRoomType((String) bookingMap.get("roomType"));
    booking.setGuestName((String) bookingMap.get("guestName"));

    //optional parameter
    if (bookingMap.containsKey("numberOfGuests")) {
      booking.setNumberOfGuests((Integer) bookingMap.get("numberOfGuests"));
    }

    bookings.add(booking);
    LOGGER.info("New booking created with ID: {}", booking.getId());
    return new ResponseEntity < > (booking, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity < List < Booking >> getAllBookings() {
    LOGGER.info("Fetching all bookings.");
    return new ResponseEntity < > (bookings, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity < Booking > getBookingById(@PathVariable String id) {
    LOGGER.info("Fetching booking with ID: {}", id);
    for (Booking booking: bookings) {
      if (booking.getId().equals(id)) {
        return new ResponseEntity < > (booking, HttpStatus.OK);
      }
    }
    LOGGER.error("Booking with ID: {} not found.", id);
    return new ResponseEntity < > (HttpStatus.NOT_FOUND);
  }

  @PutMapping("/{id}")
  public ResponseEntity < Booking > updateBooking(@PathVariable String id, @RequestBody Booking updatedBooking) {
    LOGGER.info("Updating booking with ID: {}", id);
    for (Booking booking: bookings) {
      if (booking.getId().equals(id)) {
        booking.setRoomType(updatedBooking.getRoomType());
        booking.setGuestName(updatedBooking.getGuestName());
        booking.setNumberOfGuests(updatedBooking.getNumberOfGuests());
        LOGGER.info("Booking with ID: {} updated successfully.", id);
        return new ResponseEntity < > (booking, HttpStatus.OK);
      }
    }
    LOGGER.error("Booking with ID: {} not found for updating.", id);
    return new ResponseEntity < > (HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity < Void > deleteBooking(@PathVariable String id) {
    LOGGER.info("Deleting booking with ID: {}", id);
    bookings.removeIf(booking -> booking.getId().equals(id));
    LOGGER.info("Booking with ID: {} deleted successfully.", id);
    return new ResponseEntity < > (HttpStatus.NO_CONTENT);
  }

  /*Added Booking class in the same file, however better approach is to add separate Booking class
   * This is done as per requirement to not use multiple classes
   */
  static class Booking {
    private String id;
    private String roomType;
    private String guestName;
    private int numberOfGuests;

    public void setGuestName(String newName) {
      this.guestName = newName;
    }

    public void setRoomType(String newRoomType) {
      this.roomType = newRoomType;
    }

    public void setId(String newString) {
      this.id = newString;
    }

    public void setNumberOfGuests(int totalNumberOfGuest) {
      this.numberOfGuests = totalNumberOfGuest;
    }

    public String getId() {
      return id;
    }

    public String getRoomType() {
      return roomType;
    }

    public String getGuestName() {
      return guestName;
    }

    public int getNumberOfGuests() {
      return numberOfGuests;
    }

    public Booking() {}

    public Booking(String id, String roomType, String guestName, int numberOfGuests) {
      this.id = id;
      this.roomType = roomType;
      this.guestName = guestName;
      this.numberOfGuests = numberOfGuests;
    }
  }
}
