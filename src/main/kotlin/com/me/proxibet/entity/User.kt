package com.me.proxibet.entity

import com.me.proxibet.enum.Role
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "\"user\"")
class User (
    @UuidGenerator
    @Id
    val id: UUID,
    var username: String,
    val firstName: String,
    val lastName : String,
    var password: String,
    var salt: String,
    var email: String,
    var role: Role
) {
}