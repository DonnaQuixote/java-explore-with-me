package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiator(User user, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long event, Long user);

    @Query("select e from Event e " +
            "where e.state = 'PUBLISHED' " +
            "and (:text is null or upper(e.annotation) like upper(concat('%', :text, '%')) or upper(e.description) " +
            "like upper(concat('%', :text, '%')))" +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate > :rangeStart " +
            "and e.eventDate < :rangeEnd " +
            "and (:onlyAvailable is null or e.confirmedRequests < e.participantLimit or e.participantLimit = 0)")
    List<Event> search(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                       LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    @Query("select e from Event e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and e.eventDate > :rangeStart " +
            "and e.eventDate < :rangeEnd")
    List<Event> adminSearch(List<Long> users, List<EventState> states, List<Long> categories,
                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);
}