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

    Car(engine = ServiceLocator.get(Engine::class)).start()
}

fun setupMyKoin(){
    ServiceLocator.save(Engine::class) { ElectricEngine() }
}