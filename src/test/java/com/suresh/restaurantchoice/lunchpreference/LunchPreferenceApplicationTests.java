package com.suresh.restaurantchoice.lunchpreference;

import com.suresh.restaurantchoice.lunchpreference.controller.LunchPreferenceController;
import com.suresh.restaurantchoice.lunchpreference.model.LunchPreference;
import com.suresh.restaurantchoice.lunchpreference.repository.LunchPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LunchPreferenceApplicationTests {

	@Test
	void contextLoads() {
	}
	@Mock
	private LunchPreferenceRepository preferenceRepository;

	@Mock
	private Model model;

	private LunchPreferenceController controller;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		controller = new LunchPreferenceController(preferenceRepository);
	}

	@Test
	void testShowLunchPreferenceForm() {
		String viewName = controller.showLunchPreferenceForm(model);
		assertEquals("restaurant-choice", viewName);
	}

	@Test
	void testSubmitLunchPreference() {
		LunchPreference preference = new LunchPreference();
		String viewName = controller.submitLunchPreference(preference);
		assertEquals("redirect:/submit", viewName);
	}

	@Test
	void testCreateSession() {
		LunchPreference preference = new LunchPreference();
		when(preferenceRepository.save(any(LunchPreference.class))).thenReturn(preference);

		ResponseEntity<String> response = controller.createSession(preference);
		assertEquals("Session created successfully", response.getBody());
	}

	@Test
	void testInviteToSession() {
		Long sessionId = 1L;
		String user = "Suresh";
		LunchPreference preference = new LunchPreference();
		preference.setId(sessionId);
		when(preferenceRepository.findById(sessionId)).thenReturn(Optional.of(preference));

		ResponseEntity<String> response = controller.inviteToSession(sessionId, user);
		assertEquals("Suresh invited to the session.", response.getBody());
	}

	@Test
	void testSubmitRestaurantChoice() {
		Long sessionId = 1L;
		String user = "Suresh";
		String restaurant = "Saizeriya";
		LunchPreference preference = new LunchPreference();
		preference.setId(sessionId);
		preference.getInvitedUsers().add(user);
		when(preferenceRepository.findById(sessionId)).thenReturn(Optional.of(preference));

		ResponseEntity<String> response = controller.submitRestaurantChoice(sessionId, user, restaurant);
		assertEquals("Suresh must join the session before submitting a restaurant choice.", response.getBody());
	}

	@Test
	void testEndSession() {
		Long sessionId = 1L;
		String user = "Initiator";
		LunchPreference preference = new LunchPreference();
		preference.setId(sessionId);
		preference.setInitiator(user);
		preference.getRestaurantChoices().add("Saizeriya"); // Add at least one choice to avoid the "bound must be positive" error
		when(preferenceRepository.findById(sessionId)).thenReturn(Optional.of(preference));

		ResponseEntity<String> response = controller.endSession(sessionId, user);
		assertEquals("Session ended. Selected restaurant: Saizeriya", response.getBody());
	}

	@Test
	void testJoinSession() {
		Long sessionId = 1L;
		String user = "Suresh";
		LunchPreference preference = new LunchPreference();
		preference.setId(sessionId);
		preference.getInvitedUsers().add(user);
		when(preferenceRepository.findById(sessionId)).thenReturn(Optional.of(preference));

		ResponseEntity<String> response = controller.joinSession(sessionId, user);
		assertEquals("Suresh joined the session.", response.getBody());
	}

}
