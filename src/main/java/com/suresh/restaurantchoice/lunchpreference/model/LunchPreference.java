package com.suresh.restaurantchoice.lunchpreference.model;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
public class LunchPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamMember;
    private String location;

    // Fields for session management
    private String sessionName;
    private String initiator;
    private boolean ended;
    private String selectedRestaurant;

    @ElementCollection
    private List<String> invitedUsers;

    @ElementCollection
    private List<String> joinedUsers=new LinkedList<>();

    @ElementCollection
    private List<String> restaurantChoices =new LinkedList<>();

    @ElementCollection
    private Set<String> submittedUsers = new HashSet<>();

    public LunchPreference() {
        invitedUsers = new LinkedList<>();
        invitedUsers = new ArrayList<>();
    }

    // Constructors, getters, and setters
}
