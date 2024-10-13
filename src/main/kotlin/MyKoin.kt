package org.example

import kotlin.reflect.KClass

typealias Definition<T> = ServiceLocator.(ParametersHolder) -> T

object ServiceLocator{
    private val _instances = hashMapOf<String, Provider<*>>()
    val instances: Map<String, Provider<*>> = _instances

    inline fun <reified T: Any> get(
        qualifier: String? = null, // Добавелен qualifier что бы различать одинаковые обьекты,
        noinline parameters: (() -> ParametersHolder)? = null // лямда для создания ParametersHolder
    ): T {
        val indexKey = indexKey(qualifier, T::class)

        val definition = instances[indexKey] ?: error("Не найден объект")
        @Suppress("UNCHECKED_CAST")
        return definition.get(parameters) as T
    }


    fun loadModules(vararg modules: Module){
        modules.forEach { module: Module ->
            module.mappings.forEach { (indexKey, factory) ->
                _instances[indexKey] = factory
            }
        }
    }

    fun unLoadModules(vararg modules: Module){
        modules.forEach { module: Module ->
            module.mappings.keys.forEach { key ->
                _instances.remove(key)
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

    val mappings = hashMapOf<String, Provider<*>>()

    inline fun <reified T: Any> factory(
        qualifier: String? = null,
        noinline definition: Definition<T>
    ) {
        val indexKey = indexKey(qualifier, T::class)
        mappings[indexKey] = FactoryProvider(definition)
    }

    inline fun <reified T: Any> single(
        qualifier: String? = null,
        noinline definition: Definition<T>
    ) {
        val indexKey = indexKey(qualifier, T::class)
        mappings[indexKey] = SingletonProvider(definition)
    }
}

fun myModule(block: Module.() -> Unit): Module {
    return Module().apply(block)
}

//В Koin данный класс носит имя InstanceFactory
abstract class Provider<T>(
    private val definition: Definition<T>,
) {
    protected fun create(parameters: (() -> ParametersHolder)?): T {
        val holder = parameters?.invoke() ?: ParametersHolder(emptyList())

        return definition.invoke(ServiceLocator, holder)
    }
    abstract fun get(parameters: (() -> ParametersHolder)?): T
}


class SingletonProvider<T>(definition: Definition<T>): Provider<T>(definition){
    private var instance: T? = null

    override fun get(parameters: (() -> ParametersHolder)?): T {
        synchronized(this){
            if (instance == null) {
                instance = create(parameters)
            }
        }
        return instance!!
    }
}

class FactoryProvider<T>(definition: Definition<T>): Provider<T>(definition){

    override fun get(parameters: (() -> ParametersHolder)?): T = create(parameters)
}

inline fun <reified T> inject(
    qualifier: String? = null, // Добавелен qualifier что бы различать одинаковые обьекты,
    noinline parameters: (() -> ParametersHolder)? = null // лямда для создания ParametersHolder
): Lazy<T> = lazy {
    ServiceLocator.get(qualifier, parameters)
}

class ParametersHolder(
    val _values: List<Any>
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(i: Int) = _values[i] as T

}

fun parameters(vararg values: Any) = ParametersHolder(values.toList())
