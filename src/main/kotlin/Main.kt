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

    val gasEngine1: Engine = ServiceLocator.get(qualifier = "Gasoline")
    val gasEngine2: Engine = ServiceLocator.get(qualifier = "Gasoline")

    println("reference is ${gasEngine1 === gasEngine2}")

    val engine1: Engine = ServiceLocator.get()
    val engine2: Engine = ServiceLocator.get()

    println("reference is ${engine1 === engine2}")

    val car: Car = ServiceLocator.get()
    car.start()
}

fun setupMyKoin(){
    ServiceLocator.loadModules(engineModule, carModule)
}

val engineModule = myModule {
    factory<Engine>(qualifier = "Electric") { ElectricEngine() }
    factory<Engine>(qualifier = "Gasoline") { GasolineEngine() }

    single<Engine> { ElectricEngine() }
}

val carModule = myModule {
    single { Car(engine = get()) }
}