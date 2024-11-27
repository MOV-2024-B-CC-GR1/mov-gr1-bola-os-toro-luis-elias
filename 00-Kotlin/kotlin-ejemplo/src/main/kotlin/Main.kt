package org.example

import java.util.Date

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val inmutable: String = "Elias";
    //inmutable = "Luis";
    var mutable: String = "Elias";
    mutable = "Luis";


    val ejemploVariable = "Elias";
    ejemploVariable.trim();
    val edadEjemplo: Int = 12;
    val fechaNacimiento: Date = Date()


    val coqueteo = if (esSoltero) "Si" else "No" // if else chiquito

    fun imprimirNombre(nombre: String): Unit{
        fun otraFuncionAdentro(){
            println("Otra funcion adentro")
        }
        println("Nombre: ${nombre}")// llaves cuando se opera con + y se
        //puede solo con el $
    }

}