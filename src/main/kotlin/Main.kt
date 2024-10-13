package org.example

// Интерфейс для зависимости
interface Engine {
    fun start()
}

// Реализация зависимости
class ElectricEngine() : Engine {
    override fun start() {
        println("Electric engine started")
    }
}

// Реализация зависимости
class GasolineEngine : Engine {
    override fun start() {
        println("Gasoline engine started")
    }
}

// Класс, использующий зависимость
class Car(private val engine: Engine) {
    fun start() {
        engine.start()
    }
}

fun main() {
    setupMyKoin()

    val gasEngine1 = ServiceLocator.get(qualifier = "Gasoline", clazz = Engine::class)
    val gasEngine2 = ServiceLocator.get(qualifier = "Gasoline", clazz = Engine::class)

    println("reference is ${gasEngine1 === gasEngine2}")

    val engine1 = ServiceLocator.get(clazz = Engine::class)
    val engine2 = ServiceLocator.get(clazz = Engine::class)

    println("reference is ${engine1 === engine2}")
}

fun setupMyKoin(){
    ServiceLocator.loadModules(engineModule)
}

val engineModule = myModule {
    factory<Engine>(qualifier = "Electric") { ElectricEngine() }
    factory<Engine>(qualifier = "Gasoline") { GasolineEngine() }

    single<Engine> { ElectricEngine() }
}