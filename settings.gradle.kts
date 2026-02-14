rootProject.name = "reservation-system"

// Shared modules
include("shared:shared-kernel")

// Reservation Context
include("modules:reservation-context:reservation-domain")
include("modules:reservation-context:reservation-application")
include("modules:reservation-context:reservation-adapter")
include("modules:reservation-context:reservation-bootstrap")
