package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    private CountryGroup group;

    @ManyToOne
    @JoinColumn(name = "countryId", nullable = false)
    private Country country;



}
