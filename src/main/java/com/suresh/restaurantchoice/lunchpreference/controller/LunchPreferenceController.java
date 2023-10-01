package com.suresh.restaurantchoice.lunchpreference.controller;

import com.suresh.restaurantchoice.lunchpreference.model.LunchPreference;
import com.suresh.restaurantchoice.lunchpreference.repository.LunchPreferenceRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/lunch")
@Api(tags = "Lunch Preference Controller", description = "Endpoints related to lunch preferences")
public class LunchPreferenceController {
    private final LunchPreferenceRepository preferenceRepository;

    @Autowired
    public LunchPreferenceController(LunchPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @PostMapping("/create-session")
    @ApiOperation(value = "Create a lunch session", notes = "Creates a new lunch session with the given preferences.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Session created successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> createSession(@RequestBody LunchPreference preference) {
        preferenceRepository.save(preference);
        return ResponseEntity.ok("Session created successfully");
    }

    @PostMapping("/invite")
    @ApiOperation(value = "invites users to a session", notes = "invite the users to join the session and submit their choice")
    @ApiResponses({
            @ApiResponse(code = 200, message = "invited successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> inviteToSession(
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("users") List<String> users) {
        Optional<LunchPreference> optionalPreference = preferenceRepository.findById(sessionId);

        if (optionalPreference.isPresent()) {
            LunchPreference preference = optionalPreference.get();
            for (String user : users) {
                preference.getInvitedUsers().add(user);
            }
            preferenceRepository.save(preference);
            String invitedUsers = String.join(", ", users);
            return ResponseEntity.ok(invitedUsers + " invited to the session.");
        } else {
            return ResponseEntity.badRequest().body("Session not found.");
        }
    }


    @PostMapping("/submit-restaurant")
    @ApiOperation(value = "submits a restaurant choice", notes = "user submits their choice of restaurant")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Restaurant submitted successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> submitRestaurantChoice(
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("user") String user,
            @RequestParam("restaurant") String restaurant) {
        Optional<LunchPreference> optionalPreference = preferenceRepository.findById(sessionId);

        if (optionalPreference.isPresent()) {
            LunchPreference preference = optionalPreference.get();

            if (preference.isEnded()) {
                return ResponseEntity.badRequest().body("Session has ended, and restaurant choices cannot be submitted."+" Restaurant selected during the random pick is "+preference.getSelectedRestaurant());
            }

            if (!preference.getInvitedUsers().contains(user)) {
                return ResponseEntity.badRequest().body(user + " is not part of the session and cannot submit a restaurant choice.");
            }

            if (!preference.getJoinedUsers().contains(user)) {
                return ResponseEntity.badRequest().body(user + " must join the session before submitting a restaurant choice.");
            }
            if (preference.getSubmittedUsers().contains(user)) {
                return ResponseEntity.badRequest().body(user + " You have already submitted a restaurant choice for this session.");
            }

            preference.getRestaurantChoices().add(restaurant);
            preference.getSubmittedUsers().add(user);
            preferenceRepository.save(preference);
            return ResponseEntity.ok(user + " submitted restaurant choice: " + restaurant);
        } else {
            return ResponseEntity.badRequest().body("Session not found.");
        }
    }


    @GetMapping("/get-restaurants")
    @ApiOperation(value = "list all the restaurants", notes = "lists the restaurants that have been submitted by all users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Listed the restaurants successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<List<String>> getSubmittedRestaurants(@RequestParam("sessionId") Long sessionId) {
        Optional<LunchPreference> optionalPreference = preferenceRepository.findById(sessionId);

        if (optionalPreference.isPresent()) {
            LunchPreference preference = optionalPreference.get();
            List<String> submittedRestaurants = preference.getRestaurantChoices();
            return ResponseEntity.ok(submittedRestaurants);
        } else {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PostMapping("/end-session")
    @ApiOperation(value = "ends the session", notes = "ends the session initiated by the initiator")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ended the session successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> endSession(@RequestParam("sessionId") Long sessionId, @RequestParam("user") String user) {
        Optional<LunchPreference> optionalPreference = preferenceRepository.findById(sessionId);

        if (optionalPreference.isPresent()) {
            LunchPreference preference = optionalPreference.get();
            if (preference.getInitiator().equals(user)) {
                preference.setEnded(true);
                // Implement logic to randomly select a restaurant from submitted choices
                Random random = new Random();
                int randomIndex = random.nextInt(preference.getRestaurantChoices().size());
                String selectedRestaurant = preference.getRestaurantChoices().get(randomIndex);
                preference.setSelectedRestaurant(selectedRestaurant);
                preferenceRepository.save(preference);
                return ResponseEntity.ok("Session ended. Selected restaurant: " + selectedRestaurant);
            } else {
                return ResponseEntity.badRequest().body("Only the session initiator can end the session.");
            }
        } else {
            return ResponseEntity.badRequest().body("Session not found.");
        }
    }

    @PostMapping("/join-session")
    @ApiOperation(value = "joins the session", notes = "user joins the session only after invited , uninvited users cannot join")
    @ApiResponses({
            @ApiResponse(code = 200, message = "joined the session successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> joinSession(@RequestParam("sessionId") Long sessionId, @RequestParam("user") String user) {
        Optional<LunchPreference> optionalPreference = preferenceRepository.findById(sessionId);

        if (optionalPreference.isPresent()) {
            LunchPreference preference = optionalPreference.get();
            if (preference.isEnded()) {
                return ResponseEntity.badRequest().body("Session has ended and cannot be joined. "+" Restaurant selected during the random pick is "+preference.getSelectedRestaurant());
            }
            if (preference.getInvitedUsers().contains(user)) {
                //  preference.getInvitedUsers().add(user);
                preference.getJoinedUsers().add(user);
                preferenceRepository.save(preference);
                return ResponseEntity.ok(user + " joined the session."+"restaurants submitted by other users "+preference.getRestaurantChoices());
            } else {
                //   return ResponseEntity.badRequest().body("Session has ended and cannot be joined.");
                return ResponseEntity.badRequest().body(user + " is not invited to the session.");
            }
        } else {
            return ResponseEntity.badRequest().body("Session not found.");
        }
    }

    @GetMapping("/{sessionId}/picked-restaurant")
    @ApiOperation(value = "randomly selected restaurant", notes = "restaurant picked randomly when initiator ends the session ")
    @ApiResponses({
            @ApiResponse(code = 200, message = "random selection is successful"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<String> getPickedRestaurant(@PathVariable Long sessionId) {

        Optional<LunchPreference> pickedRestaurant = preferenceRepository.findById(sessionId);
        if (pickedRestaurant.isPresent()) {
           return ResponseEntity.ok("restaurant " + pickedRestaurant.get().getSelectedRestaurant() + " has been picked randomly");
        } else {
            return ResponseEntity.ok("no restaurants found");
        }
    }

}
