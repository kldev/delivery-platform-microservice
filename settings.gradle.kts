rootProject.name = "delivery-platform"

include(
    ":services:delivery-service",
    ":services:ledger-service",
    ":services:settlement-service",    
    ":services:reconciliation-service",

    ":libraries:common"    
)