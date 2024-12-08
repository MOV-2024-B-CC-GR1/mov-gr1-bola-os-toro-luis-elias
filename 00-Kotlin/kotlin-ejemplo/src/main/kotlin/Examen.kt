import java.io.File
import java.util.Scanner

data class EquipoFutbol(
    var nombre: String,
    var numJugadores: Int,
    var paisOrigen: String,
    var anioCreacion: Int,
    var presupuesto: Int
)

data class Jugador(
    var nombre: String,
    var posicion: String,
    var edad: Int,
    var numeroCamiseta: Int,
    var contratoVigente: Boolean
)

val equipoFile = File("EquipoFutbol.txt")
val jugadorFile = File("Jugador.txt")
val scanner = Scanner(System.`in`)

// Funciones para manejar archivos
fun guardarEquipo(equipo: EquipoFutbol) {
    equipoFile.writeText("${equipo.nombre},${equipo.numJugadores},${equipo.paisOrigen},${equipo.anioCreacion},${equipo.presupuesto}\n")
}

fun leerEquipo(): EquipoFutbol? {
    return if (equipoFile.exists() && equipoFile.length() > 0) {
        val datos = equipoFile.readLines().first().split(",")
        EquipoFutbol(datos[0], datos[1].toInt(), datos[2], datos[3].toInt(), datos[4].toInt())
    } else null
}

fun guardarJugador(jugador: Jugador) {
    jugadorFile.appendText("${jugador.nombre},${jugador.posicion},${jugador.edad},${jugador.numeroCamiseta},${jugador.contratoVigente}\n")
}

fun leerJugadores(): MutableList<Jugador> {
    if (!jugadorFile.exists()) return mutableListOf()
    return jugadorFile.readLines().map { linea ->
        val datos = linea.split(",")
        Jugador(datos[0], datos[1], datos[2].toInt(), datos[3].toInt(), datos[4].toBoolean())
    }.toMutableList()
}

fun actualizarJugadores(jugadores: List<Jugador>) {
    jugadorFile.writeText(jugadores.joinToString("\n") {
        "${it.nombre},${it.posicion},${it.edad},${it.numeroCamiseta},${it.contratoVigente}"
    })
}

// Menú interactivo
fun main() {
    var equipo = leerEquipo() ?: EquipoFutbol("Sin Nombre", 0, "Desconocido", 0, 0)
    val jugadores = leerJugadores()

    while (true) {
        println("\n*** MENÚ PRINCIPAL ***")
        println("1. Añadir jugador")
        println("2. Eliminar jugador")
        println("3. Modificar jugador")
        println("4. Modificar equipo")
        println("5. Imprimir jugadores")
        println("6. Imprimir equipo")
        println("7. Salir")
        print("Selecciona una opción: ")

        when (scanner.nextInt()) {
            1 -> {
                scanner.nextLine() // Consumir nueva línea pendiente
                print("Nombre del jugador: ")
                val nombre = scanner.nextLine()
                print("Posición: ")
                val posicion = scanner.nextLine()
                print("Edad: ")
                val edad = scanner.nextInt()
                print("Número de camiseta: ")
                val numeroCamiseta = scanner.nextInt()
                scanner.nextLine() // Consumir nueva línea pendiente
                print("¿Contrato vigente? (sí/no): ")
                val contratoInput = scanner.nextLine().lowercase()
                val contratoVigente = contratoInput == "sí" || contratoInput == "si"

                val jugador = Jugador(nombre, posicion, edad, numeroCamiseta, contratoVigente)
                jugadores.add(jugador)
                guardarJugador(jugador)
                println("Jugador añadido con éxito.")
            }
            2 -> {
                scanner.nextLine() // Consumir nueva línea pendiente
                print("Nombre del jugador a eliminar: ")
                val nombre = scanner.nextLine()
                val jugador = jugadores.find { it.nombre == nombre }
                if (jugador != null) {
                    jugadores.remove(jugador)
                    actualizarJugadores(jugadores)
                    println("Jugador eliminado con éxito.")
                } else {
                    println("Jugador no encontrado.")
                }
            }
            3 -> {
                scanner.nextLine() // Consumir nueva línea pendiente
                print("Nombre del jugador a modificar: ")
                val nombre = scanner.nextLine()
                val jugador = jugadores.find { it.nombre == nombre }
                if (jugador != null) {
                    print("Nueva posición: ")
                    jugador.posicion = scanner.nextLine()
                    print("Nueva edad: ")
                    jugador.edad = scanner.nextInt()
                    print("Nuevo número de camiseta: ")
                    jugador.numeroCamiseta = scanner.nextInt()
                    scanner.nextLine() // Consumir nueva línea pendiente
                    print("¿Contrato vigente? (sí/no): ")
                    val contratoInput = scanner.nextLine().lowercase()
                    jugador.contratoVigente = contratoInput == "sí" || contratoInput == "si"

                    actualizarJugadores(jugadores)
                    println("Jugador modificado con éxito.")
                } else {
                    println("Jugador no encontrado.")
                }
            }
            4 -> {
                scanner.nextLine() // Consumir nueva línea pendiente
                println("Modificando el equipo: ${equipo.nombre}")
                print("Nuevo nombre del equipo: ")
                equipo.nombre = scanner.nextLine()
                print("Nuevo número de jugadores: ")
                equipo.numJugadores = scanner.nextInt()
                scanner.nextLine() // Consumir nueva línea pendiente
                print("Nuevo país de origen: ")
                equipo.paisOrigen = scanner.nextLine()
                print("Nuevo año de creación: ")
                equipo.anioCreacion = scanner.nextInt()
                print("Nuevo presupuesto: ")
                equipo.presupuesto = scanner.nextInt()

                guardarEquipo(equipo)
                println("Equipo modificado con éxito.")
            }
            5 -> {
                println("\n*** Jugadores ***")
                if (jugadores.isEmpty()) {
                    println("No hay jugadores registrados.")
                } else {
                    jugadores.forEach { println(it) }
                }
            }
            6 -> {
                println("\n*** Equipo ***")
                println(equipo)
            }
            7 -> {
                println("Saliendo del programa...")
                break
            }
            else -> println("Opción inválida. Intenta de nuevo.")
        }
    }
}

