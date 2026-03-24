package com.texas.developers.ams.entity;


import com.texas.developers.ams.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "username", columnNames = "username")
})
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "mobile_number", length = 10, nullable = false)
    private String mobileNumber;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "role", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}