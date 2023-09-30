package com.suresh.restaurantchoice.lunchpreference.controller;

import com.suresh.restaurantchoice.lunchpreference.model.LunchPreference;
import com.suresh.restaurantchoice.lunchpreference.repository.LunchPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/lunch")
public class LunchPreferenceController {
    private final LunchPreferenceRepository preferenceRepository;

    @Autowired
    public LunchPreferenceController(LunchPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @GetMapping("/submit")
    public String showLunchPreferenceForm(Model model) {
        model.addAttribute("preference", new LunchPreference());
        return "restaurant-choice";
    }

    @PostMapping("/submit")
    public String submitLunchPreference(@ModelAttribute("preference") LunchPreference preference) {
        // Process and save the lunch preference in the back-end

        return "redirect:/submit"; // Redirect to the form page after submission
    }

    @PostMapping("/create-session")
    public ResponseEntity<String> createSession(@RequestBody LunchPreference preference) {
        preferenceRepository.save(preference);
        return ResponseEntity.ok("Session created successfully");
    }

    @PostMapping("/invite")
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
    public ResponseEntity<String> getPickedRestaurant(@PathVariable Long sessionId) {

        Optional<LunchPreference> pickedRestaurant = preferenceRepository.findById(sessionId);
        if (pickedRestaurant.isPresent()) {
           return ResponseEntity.ok("restaurant " + pickedRestaurant.get().getSelectedRestaurant() + " has been picked randomly");
        } else {
            return ResponseEntity.ok("no restaurants found");
        }
    }

}
