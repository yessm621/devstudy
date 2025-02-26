package me.devstudy.study;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @OneToMany(mappedBy = "account")
    private Set<StudyAccount> managers = new HashSet<>();

    @OneToMany(mappedBy = "account")
    private Set<StudyAccount> members = new HashSet<>();

    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    @OneToMany(mappedBy = "tag")
    private Set<StudyTag> studyTags = new HashSet<>();

    @OneToMany(mappedBy = "zone")
    private Set<StudyZone> studyZones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;
}
