package com.github.plugkit.plugin

internal val Plugin<*>.fullName
    get() = this.description.fullName