package com.panini.supportapp.data.mock

import com.panini.supportapp.domain.model.Priority
import com.panini.supportapp.domain.model.Ticket
import com.panini.supportapp.domain.model.TicketCategory
import com.panini.supportapp.domain.model.TicketStatus

/**
 * Realistic mock dataset based on actual Panini/FIFA 2026 distribution scenarios.
 * All tickets represent plausible operational incidents for the local market.
 */
object MockData {

    val tickets = mutableListOf(
        Ticket(
            id = "TKT-001",
            title = "Critical pack shortage — Escazú point of sale",
            description = "The Escazú point of sale reports a shortage of 500 packs from Series 1 of the FIFA 2026 album. Order #ESC-2026-014 was received incomplete from the central distributor. Urgent replenishment is required to avoid lost sales during peak season.",
            priority = Priority.HIGH,
            status = TicketStatus.OPEN,
            supplier = "Central Distributor S.A.",
            createdAt = "2026-05-28T09:15:00Z",
            category = TicketCategory.INVENTORY,
            affectedQuantity = 500
        ),
        Ticket(
            id = "TKT-002",
            title = "Supplier missed scheduled delivery — North Zone",
            description = "LogisPack CR did not show up during the agreed delivery window (08:00–10:00) for order #ZN-2026-089 of 200 base albums bound for the North Zone. The carrier has no record of the appointment. Rescheduling and escalation to the logistics coordinator are required.",
            priority = Priority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            supplier = "LogisPack CR",
            createdAt = "2026-05-27T14:30:00Z",
            category = TicketCategory.DISTRIBUTION,
            assignedTo = "Carlos Méndez",
            affectedQuantity = 200
        ),
        Ticket(
            id = "TKT-003",
            title = "Sticker count error per box — Batch 2026-A",
            description = "An internal audit found inconsistencies in batch 2026-A: boxes are labeled with 100 packs but contain between 92 and 97 units. Affects 50 boxes (full batch). Review with Ediciones Coleccionables S.A. and a credit note are required.",
            priority = Priority.MEDIUM,
            status = TicketStatus.OPEN,
            supplier = "Ediciones Coleccionables S.A.",
            createdAt = "2026-05-27T11:00:00Z",
            category = TicketCategory.QUALITY,
            affectedQuantity = 50
        ),
        Ticket(
            id = "TKT-004",
            title = "Delivery delay — Pérez Zeledón distributor",
            description = "Order #PZ-2026-089 is 3 days overdue. SportDist Internacional reports a roadblock on Route 2 due to a landslide. The Pérez Zeledón client has inventory for 2 more days. An alternate route via San Isidro was requested.",
            priority = Priority.MEDIUM,
            status = TicketStatus.RESOLVED,
            supplier = "SportDist Internacional",
            createdAt = "2026-05-25T08:00:00Z",
            category = TicketCategory.LOGISTICS,
            assignedTo = "Ana Vargas"
        ),
        Ticket(
            id = "TKT-005",
            title = "Packaging damage during transport — UEFA Special Batch",
            description = "30 boxes from the UEFA special batch arrived with damaged primary packaging. Packs show moisture exposure. Quality control determined 60% is fit for sale; 40% must be replaced under supplier warranty.",
            priority = Priority.LOW,
            status = TicketStatus.CLOSED,
            supplier = "FutbolMania Import",
            createdAt = "2026-05-20T16:45:00Z",
            category = TicketCategory.QUALITY,
            affectedQuantity = 30
        ),
        Ticket(
            id = "TKT-006",
            title = "Duplicate order — San José Centro point of sale",
            description = "The ERP system registered and dispatched order #SJC-2026-124 twice. The point of sale received 300 boxes instead of 150. Return coordination and a credit note are required. The carrier is available for pickup next Wednesday.",
            priority = Priority.HIGH,
            status = TicketStatus.OPEN,
            supplier = "Central Distributor S.A.",
            createdAt = "2026-05-28T17:20:00Z",
            category = TicketCategory.SUPPLIER,
            affectedQuantity = 150
        ),
        Ticket(
            id = "TKT-007",
            title = "Missing exclusive FIFA stickers — Shiny Collection",
            description = "The exclusive FIFA 2026 holographic sticker collection is missing 40% of the catalog (18 of 45 SKUs) across all GAM points of sale. Demand exceeded the initial forecast. An urgent replenishment order with Ediciones Coleccionables S.A. is required.",
            priority = Priority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            supplier = "Ediciones Coleccionables S.A.",
            createdAt = "2026-05-29T10:00:00Z",
            category = TicketCategory.INVENTORY,
            assignedTo = "Luis Rojas",
            affectedQuantity = 800
        ),
        Ticket(
            id = "TKT-008",
            title = "Invalid QR code on 2026 Special Series packs",
            description = "Multiple customers report that QR codes printed on 2026 Special Series packs return a 404 error in the official Panini app. The issue affects the entire FI-2026-B batch. Escalated to the production team in Italy.",
            priority = Priority.MEDIUM,
            status = TicketStatus.OPEN,
            supplier = "FutbolMania Import",
            createdAt = "2026-05-29T13:30:00Z",
            category = TicketCategory.QUALITY
        ),
        Ticket(
            id = "TKT-009",
            title = "Unregistered inventory — Central warehouse",
            description = "A pallet with 1,000 boxes of packs arrived at the central warehouse with no purchase order or inventory record. Supplier and batch number are unknown. Possible mis-shipment from another client. Held in quarantine until identification.",
            priority = Priority.LOW,
            status = TicketStatus.OPEN,
            supplier = "Unknown",
            createdAt = "2026-05-26T09:00:00Z",
            category = TicketCategory.INVENTORY,
            affectedQuantity = 1000
        ),
        Ticket(
            id = "TKT-010",
            title = "Tracking system failure — LogisPack CR",
            description = "The LogisPack CR tracking portal has not updated the status of 45 active shipments for 18 hours. Unable to confirm whether orders are in transit, held, or delivered. Points of sale are filing complaints. A manual report from the supplier was requested.",
            priority = Priority.MEDIUM,
            status = TicketStatus.IN_PROGRESS,
            supplier = "LogisPack CR",
            createdAt = "2026-05-28T08:00:00Z",
            category = TicketCategory.LOGISTICS,
            assignedTo = "María González",
            affectedQuantity = 45
        )
    )
}
