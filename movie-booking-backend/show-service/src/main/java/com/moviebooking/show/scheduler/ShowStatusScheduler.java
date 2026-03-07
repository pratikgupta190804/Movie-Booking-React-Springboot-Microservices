package com.moviebooking.show.scheduler;

import com.moviebooking.show.repo.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ShowStatusScheduler {

    private final ShowRepository showRepository;

    @Scheduled(cron = "0 * * * * *") // every minute
    public void updateShowStatuses() {
        LocalDateTime now = LocalDateTime.now();
        int updated = showRepository.updateShowsToRunning(now);
        showRepository.updateShowsToCompleted(now);
        System.out.println("Shows moved to RUNNING: " + updated);
    }
}
