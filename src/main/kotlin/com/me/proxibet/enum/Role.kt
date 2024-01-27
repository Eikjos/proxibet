package com.me.proxibet.enum

enum class Role(name: String) {
    USER("User"),
    ADMIN("Admin");

    fun getName() : String {
        return this.name;
    }
}