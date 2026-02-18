package org.example;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
    @SequenceGenerator(name = "user_id", sequenceName = "user_id_seq", allocationSize = 1)
    public long id;

    @Column(name = "name")
    public String name;

    @Column(name = "second_name")
    public String secondName;


    @Column(name = "surname")
    public String surname;

    public User() {

    }
}
