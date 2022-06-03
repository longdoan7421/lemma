package de.fhdo.lemma.model_processing.code_generation.keycloak.operation.model.jsonconfig

data class Group(
    val groupName:String,
){
    val members: List<User> = mutableListOf()
    val roles: List<Role> = mutableListOf()
}