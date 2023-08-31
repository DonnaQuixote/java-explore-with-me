package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;
import ru.practicum.category.model.Category;
import ru.practicum.comment.model.Comment;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;
    @Embedded
    Location location;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    EventState state;
    String title;
    @Formula("(select count(r.id) from participation_requests as r " +
            "where r.event_id = id and r.status like 'CONFIRMED')")
    Long confirmedRequests;
    @OneToMany
    @JoinTable(name = "comments",
    inverseJoinColumns = @JoinColumn(name = "id"))
    List<Comment> comments = new ArrayList<>();
}