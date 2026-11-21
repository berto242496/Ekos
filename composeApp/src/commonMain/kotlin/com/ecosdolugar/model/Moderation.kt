package com.ecosdolugar.model

enum class ReportTarget(val code: String) {
    ROUTE("route"),
    REVIEW("review"),
    GUIDE("guide")
}

enum class ReportReason(val code: String) {
    INAPPROPRIATE("inappropriate"),
    HARASSMENT("harassment"),
    SPAM("spam"),
    INACCURATE("inaccurate"),
    OTHER("other")
}
