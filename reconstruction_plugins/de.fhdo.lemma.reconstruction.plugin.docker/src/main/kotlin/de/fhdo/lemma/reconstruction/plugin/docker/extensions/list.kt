package de.fhdo.lemma.reconstruction.plugin.docker.extensions

inline fun <reified T> List<T>.containsAnyElementOf(list: List<T>): Boolean {
    for (element in this) {
        if (list.contains(element)) {
            return true
        }
    }

    return false
}