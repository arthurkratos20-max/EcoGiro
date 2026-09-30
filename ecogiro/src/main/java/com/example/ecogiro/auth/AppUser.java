package com.example.ecogiro.auth;
import jakarta.persistence.*;
@Entity @Table(name="app_users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String name;
 @Column(nullable=false,length=254) private String email;
 @Column(nullable=false,length=100) private String passwordHash;
 protected AppUser() {}
 public AppUser(String name,String email,String hash) {this.name=name;this.email=email;this.passwordHash=hash;}
 public Long getId(){return id;} public String getName(){return name;}
 public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
}
