package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable
// Si las fechas vienen como Strings, necesitarás un adaptador para convertirlas a Date o Instant
// import kotlinx.datetime.Instant // Si quieres usar Instant (moderno)
// import java.util.Date // Si prefieres java.util.Date (requiere adaptador o serialización como timestamp/String)

@Serializable
data class Cita(
    val idCita: Int? = null, // ID autogenerado
    val servicioId: Int? = null,
    val seccionId: Int? = null,
    val usuarioId: Int? = null,
    // Campos de fecha y hora.
    // Nota: El manejo de Date/Time en JSON puede variar.
    // Asumo que la API los envía como Strings (ej. "2023-10-26T10:00:00Z")
    // o como timestamps numéricos (Long). Usaremos String por ahora.
    // Si necesitas convertirlos a objetos Date o Instant, necesitarás configurar
    // un adaptador de serialización para Kotlinx Serialization.
    val fecha: String, // O Long, o un objeto fecha/hora si configuras el adaptador
    val hora: String // O Long, o un objeto fecha/hora si configuras el adaptador
)