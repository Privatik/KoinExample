package org.example

import kotlin.reflect.KClass

object ServiceLocator{
    private val instances = hashMapOf<String, () -> Any>()

    fun <T: Any> get(
        qualifier: String? = null, // Добавелен qualifier что бы различать одинаковые обьекты
        clazz: KClass<T>
    ): T {
        val indexKey = indexKey(qualifier, clazz)

        val definition = instances[indexKey] ?: error("Не найден объект")
        @Suppress("UNCHECKED_CAST")
        return definition.invoke() as T
    }


    fun loadModules(vararg modules: Module){
        modules.forEach { module: Module ->
            module.mappings.forEach { (indexKey, factory) ->
                instances[indexKey] = factory
            }
        }
    }

    fun unLoadModules(vararg modules: Module){
        modules.forEach { module: Module ->
            module.mappings.keys.forEach { key ->
                instances.remove(key)
            }
        }
    }
}

// Генерация cтроки из qualifier и clazz что бы создавать меньше обьектов
fun indexKey(
    qualifier: String?,
    clazz: KClass<*>,
): String {
    return "${clazz.java.name}:${qualifier.orEmpty()}"
}

class Module {

    val mappings = hashMapOf<String, () -> Any>()

    fun save(
        qualifier: String? = null,
        clazz: KClass<out Any>,
        definition: () -> Any
    ) {
        val indexKey = indexKey(qualifier, clazz)
        mappings[indexKey] = definition
    }
}

fun myModule(block: Module.() -> Unit): Module {
    return Module().apply(block)
}
