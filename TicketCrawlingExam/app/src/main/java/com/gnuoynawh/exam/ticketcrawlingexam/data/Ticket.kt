package com.gnuoynawh.exam.ticketcrawlingexam.data

import java.io.Serializable

data class Ticket(
    var number: String,
    var title: String,
    var thumb: String,
    var place: String,
    var date: String,
    var count: String,
    var state: String
) : Serializable {
    constructor(): this("", "", "", "", "", "", "")
}
