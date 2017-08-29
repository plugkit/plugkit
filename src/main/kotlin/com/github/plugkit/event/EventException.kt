package com.github.plugkit.event

class EventException(cause: Throwable? = null, message: CharSequence? = null) : Exception(message.toString(), cause)