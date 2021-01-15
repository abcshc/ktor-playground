package com.example.user

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val gender = char("gender", 1)
    val phone = varchar("phone", 15)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    var name by Users.name
    var gender by Users.gender
    var phone by Users.phone

    companion object : IntEntityClass<User>(Users)
}