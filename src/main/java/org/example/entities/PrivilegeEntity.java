package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private List<RoleEntity> roles =  new ArrayList<>();

    public PrivilegeEntity(String name) {
        this.name = name;
    }
}
