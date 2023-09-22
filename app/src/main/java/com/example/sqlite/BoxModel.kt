package com.example.sqlite
import java.util.*
import kotlin.random.Random
class BoxModel(
    var id:Int =getAutoId(),
    var name:String = "",
    var qr: String= generateRandomNumber(20),
    ){
    companion object{
        fun getAutoId(): Int {
            return (1..100).random()
        }
        fun generateRandomNumber(length: Int): String {
            val randomNumber = StringBuilder(length)
            val digits = "0123456789"

            repeat(length) {
                val randomIndex = Random.nextInt(digits.length)
                val randomDigit = digits[randomIndex]
                randomNumber.append(randomDigit)
            }

            return randomNumber.toString()
        }

    }
    override fun toString(): String{return name}

}


