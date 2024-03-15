package com.hrsgroup.hotelbookingapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrsgroup.hotelbookingapi.HotelBookingApi;
import com.hrsgroup.hotelbookingapi.HotelBookingApi.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = HotelBookingApi.class)
@AutoConfigureMockMvc
class HotelBookingApiTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private HotelBookingApi hotelBookingApi;

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Booking> bookings;

    @BeforeEach
    void setUp() {
        bookings = new ArrayList<>();
        bookings.add(new Booking("1", "Single", "John Doe", 1));
        bookings.add(new Booking("2", "Double", "Jane Smith", 2));
    }

    @Test
    void testSearchAvailableRooms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"));
    }

    @Test
    void testCreateBooking() throws Exception {
        Booking newBooking = new Booking("3", "Suite", "Alice Wonderland", 3);
        String jsonRequest = objectMapper.writeValueAsString(newBooking);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomType").value("Suite"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guestName").value("Alice Wonderland"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfGuests").value(3));
    }

    @Test
    void testGetAllBookings() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"));
    }

    @Test
    void testGetBookingById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomType").value("Single"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guestName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfGuests").value(1));
    }

    @Test
    void testUpdateBooking() throws Exception {
        Booking updatedBooking = new Booking("1", "Double", "John Doe", 2);
        String jsonRequest = objectMapper.writeValueAsString(updatedBooking);

        mockMvc.perform(MockMvcRequestBuilders.put("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roomType").value("Double"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guestName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfGuests").value(2));
    }

    @Test
    void testDeleteBooking() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
