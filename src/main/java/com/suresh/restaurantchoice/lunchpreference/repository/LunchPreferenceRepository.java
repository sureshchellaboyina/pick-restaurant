package com.suresh.restaurantchoice.lunchpreference.repository;

import com.suresh.restaurantchoice.lunchpreference.model.LunchPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LunchPreferenceRepository extends JpaRepository<LunchPreference, Long> {
    Optional<LunchPreference> findBySessionNameAndTeamMember(String sessionName, String teamMember);
}
