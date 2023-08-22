package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ViewStats(app,uri, count(distinct ip)) from EndpointHit " +
            "where timestamp between ?1 and ?2 " +
            "and uri in ?3 " +
            "group by app, uri " +
            "order by count(distinct ip) desc")
    List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select new ru.practicum.ViewStats(app, uri, count(distinct ip)) from EndpointHit " +
            "where timestamp between ?1 and ?2 " +
            "group by app, uri " +
            "order by count(distinct ip) desc")
    List<ViewStats> getUniqueWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where timestamp between ?1 and ?2 " +
            "group by app, uri " +
            "order by count(distinct ip) desc")
    List<ViewStats> getWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStats(app, uri, count(ip)) from EndpointHit " +
            "where timestamp between ?1 and ?2 " +
            "and uri in ?3 " +
            "group by app, uri " +
            "order by count(ip) desc")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris);
}