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
            title = "Faltante crítico de sobres — Punto de venta Escazú",
            description = "El punto de venta Escazú reporta faltante de 500 sobres de la Serie 1 del álbum FIFA 2026. El pedido #ESC-2026-014 fue recibido incompleto desde la distribuidora central. Se requiere reposición urgente para evitar pérdida de ventas en temporada alta.",
            priority = Priority.HIGH,
            status = TicketStatus.OPEN,
            supplier = "Distribuidora Central S.A.",
            createdAt = "2026-05-28T09:15:00Z",
            category = TicketCategory.INVENTORY,
            affectedQuantity = 500
        ),
        Ticket(
            id = "TKT-002",
            title = "Proveedor no realizó entrega programada — Zona Norte",
            description = "LogisPack CR no se presentó en la ventana de entrega acordada (08:00–10:00) para el pedido #ZN-2026-089 de 200 álbumes base con destino Zona Norte. El transportista no tiene registro de la cita. Se debe reprogramar y escalar al coordinador logístico.",
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
            title = "Error en conteo de stickers por caja — Lote 2026-A",
            description = "Auditoría interna detectó inconsistencia en el lote 2026-A: las cajas están etiquetadas con 100 sobres pero contienen entre 92 y 97 unidades. Afecta 50 cajas (lote completo). Se requiere revisión con Ediciones Coleccionables S.A. y nota de crédito.",
            priority = Priority.MEDIUM,
            status = TicketStatus.OPEN,
            supplier = "Ediciones Coleccionables S.A.",
            createdAt = "2026-05-27T11:00:00Z",
            category = TicketCategory.QUALITY,
            affectedQuantity = 50
        ),
        Ticket(
            id = "TKT-004",
            title = "Retraso en entrega — Distribuidora Pérez Zeledón",
            description = "El pedido #PZ-2026-089 lleva 3 días de retraso. SportDist Internacional reporta bloqueo vial en Ruta 2 por derrumbe. El cliente en Pérez Zeledón tiene inventario para 2 días más. Se solicitó ruta alterna vía San Isidro.",
            priority = Priority.MEDIUM,
            status = TicketStatus.RESOLVED,
            supplier = "SportDist Internacional",
            createdAt = "2026-05-25T08:00:00Z",
            category = TicketCategory.LOGISTICS,
            assignedTo = "Ana Vargas"
        ),
        Ticket(
            id = "TKT-005",
            title = "Daño en empaque durante transporte — Lote Especial UEFA",
            description = "Se recibieron 30 cajas del lote especial UEFA con empaque primario dañado. Los sobres presentan exposición a humedad. Control de calidad determinó que el 60% es apto para venta, el 40% debe ser reemplazado bajo garantía del proveedor.",
            priority = Priority.LOW,
            status = TicketStatus.CLOSED,
            supplier = "FutbolMania Import",
            createdAt = "2026-05-20T16:45:00Z",
            category = TicketCategory.QUALITY,
            affectedQuantity = 30
        ),
        Ticket(
            id = "TKT-006",
            title = "Pedido duplicado — Punto de venta San José Centro",
            description = "El sistema ERP registró y despachó dos veces el pedido #SJC-2026-124. El punto de venta recibió 300 cajas en lugar de 150. Se debe coordinar devolución y nota de crédito. El transportista está disponible para recogida el próximo miércoles.",
            priority = Priority.HIGH,
            status = TicketStatus.OPEN,
            supplier = "Distribuidora Central S.A.",
            createdAt = "2026-05-28T17:20:00Z",
            category = TicketCategory.SUPPLIER,
            affectedQuantity = 150
        ),
        Ticket(
            id = "TKT-007",
            title = "Faltante de stickers exclusivos FIFA — Colección Brillante",
            description = "La colección de stickers holográficos exclusivos FIFA 2026 presenta faltante del 40% del catálogo (18 de 45 referencias) en todos los puntos de venta de la GAM. La alta demanda superó el pronóstico inicial. Se requiere orden de reposición urgente con Ediciones Coleccionables S.A.",
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
            title = "Código QR inválido en sobres Serie Especial 2026",
            description = "Múltiples clientes reportan que los códigos QR impresos en los sobres de la Serie Especial 2026 arrojan error 404 en la aplicación oficial de Panini. El problema afecta el lote FI-2026-B completo. Se escaló al equipo de producción en Italia.",
            priority = Priority.MEDIUM,
            status = TicketStatus.OPEN,
            supplier = "FutbolMania Import",
            createdAt = "2026-05-29T13:30:00Z",
            category = TicketCategory.QUALITY
        ),
        Ticket(
            id = "TKT-009",
            title = "Inventario no registrado — Bodega Central",
            description = "Se recibió un palé con 1 000 cajas de sobres en bodega central sin orden de compra ni registro en el sistema de inventario. Se desconoce el proveedor y el número de lote. Posible despacho erróneo de otro cliente. En cuarentena hasta identificación.",
            priority = Priority.LOW,
            status = TicketStatus.OPEN,
            supplier = "Desconocido",
            createdAt = "2026-05-26T09:00:00Z",
            category = TicketCategory.INVENTORY,
            affectedQuantity = 1000
        ),
        Ticket(
            id = "TKT-010",
            title = "Falla en sistema de rastreo — LogisPack CR",
            description = "El portal de seguimiento de LogisPack CR no actualiza el estado de 45 envíos activos desde hace 18 horas. Imposible confirmar si los pedidos están en tránsito, retenidos o entregados. Los puntos de venta están generando reclamaciones. Se solicitó reporte manual al proveedor.",
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
