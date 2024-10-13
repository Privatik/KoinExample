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

    Car(engine = ServiceLocator.get(qualifier = "Gasoline", clazz = Engine::class)).start()
    Car(engine = ServiceLocator.get(qualifier = "Electric", clazz = Engine::class)).start()
}

fun setupMyKoin(){
    ServiceLocator.save(
        qualifier = "Electric",
        Engine::class
    ) { ElectricEngine() }

    ServiceLocator.save(
        qualifier = "Gasoline",
        Engine::class
    ) { GasolineEngine() }
}