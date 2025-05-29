package com.example.practico3.observer

interface Observable {
    fun addObserver(observer: Observer)
    fun notifyObservers()
}