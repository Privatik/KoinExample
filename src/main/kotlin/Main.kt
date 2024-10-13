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
class Car(
    private val engine: Engine,
    private val ownerName: String
) {
    fun start() {
        engine.start()
        println("The name of the owner is $ownerName")
    }
}

fun main() {
    setupMyKoin()

    val gasEngine1: Engine by inject(qualifier = "Gasoline")
    val gasEngine2: Engine by inject(qualifier = "Gasoline")

    println("reference is ${gasEngine1 === gasEngine2}")

    val engine1: Engine by inject()
    val engine2: Engine by inject()

    println("reference is ${engine1 === engine2}")

    val car: Car by inject(parameters = { parameters("Ivan") })
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
    single { parameters -> Car(engine = get(), ownerName = parameters[0]) }
}