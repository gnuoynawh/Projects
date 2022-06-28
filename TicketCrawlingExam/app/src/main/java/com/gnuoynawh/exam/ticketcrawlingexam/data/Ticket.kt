package com.gnuoynawh.exam.ticketcrawlingexam

import java.io.Serializable

data class Ticket(
    var title: String,
    var thumb: String,
    var place: String,
    var date: String,
    var number: String
) : Serializable {
    constructor(): this("", "", "", "", "")
}
