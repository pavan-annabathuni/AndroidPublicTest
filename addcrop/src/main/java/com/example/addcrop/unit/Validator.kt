package com.example.addcrop.unit

object Validator {
    fun validateInput( name: String,area: Int): Boolean {
        return !(area <= 0 || name.isEmpty())
    }
}