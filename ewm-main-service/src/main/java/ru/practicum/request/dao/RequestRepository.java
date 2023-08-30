package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Long countByEvent_IdAndStatus(Long event, RequestStatus status);

    Optional<ParticipationRequest> findByRequester_IdAndId(Long requester, Long request);

    List<ParticipationRequest> findByRequester_Id(Long requester);

    List<ParticipationRequest> findByEvent_Id(Long event);

    List<ParticipationRequest> findByEvent_IdAndStatus(Long event, RequestStatus status);

    Optional<ParticipationRequest> findByRequester_IdAndEvent_Id(Long requester, Long event);
}