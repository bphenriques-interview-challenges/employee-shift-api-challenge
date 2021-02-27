package com.bphenriques.test

import java.time.Instant
import java.time.format.DateTimeFormatter

fun Instant.toISOFormat(): String = DateTimeFormatter.ISO_INSTANT.format(this)
