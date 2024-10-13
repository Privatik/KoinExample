package org.example

import kotlin.reflect.KClass

object ServiceLocator{
    private val instances = hashMapOf<KClass<out Any>, () -> Any>()

    fun <T: Any> save(
        сlazz: KClass<out T>,
        definition: () -> T
    ) {
        instances[сlazz] = definition
    }


    fun <T: Any> get(clazz: KClass<T>,): T {
        val definition = instances[clazz] ?: error("Не найден объект")
        @Suppress("UNCHECKED_CAST")
        return definition.invoke() as T
    }
}