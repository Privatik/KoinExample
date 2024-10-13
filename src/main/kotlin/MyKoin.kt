package org.example

import kotlin.reflect.KClass

object ServiceLocator{
    private val instances = hashMapOf<String, () -> Any>()

    fun <T: Any> save(
        qualifier: String? = null, // Добавелен qualifier что бы различать одинаковые обьекты
        clazz: KClass<out T>,
        definition: () -> T
    ) {
        val indexKey = indexKey(qualifier, clazz)
        instances[indexKey] = definition
    }


    fun <T: Any> get(
        qualifier: String? = null, // Добавелен qualifier что бы различать одинаковые обьекты
        clazz: KClass<T>
    ): T {
        val indexKey = indexKey(qualifier, clazz)

        val definition = instances[indexKey] ?: error("Не найден объект")
        @Suppress("UNCHECKED_CAST")
        return definition.invoke() as T
    }
}

// Генерация cтроки из qualifier и clazz что бы создавать меньше обьектов
fun indexKey(
    qualifier: String?,
    clazz: KClass<*>,
): String {
    return "${clazz.java.name}:${qualifier.orEmpty()}"
}