package cl.orderflowmanagement.gestionpagosyfacturas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Entity
@Table(name = "pagos") 
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa el registro del flujo financiero y detalles de transacción de un pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    @Schema(description = "Identificador único autoincrementable del registro de pago", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;
    
    @Column(name = "pedido_id", nullable = false)
    @Schema(description = "Identificador único del pedido asociado a la transacción", example = "450")
    private Integer pedidoId; 
    
    @Column(name = "monto_neto", nullable = false)
    @Schema(description = "Valor de la transacción antes de aplicar impuestos", example = "100000.00")
    private Double montoNeto;
    
    @Column(name = "iva", nullable = false)
    @Schema(description = "Monto correspondiente al impuesto al valor agregado calculado", example = "19000.00")
    private Double iva;
    
    @Column(name = "total_pagado", nullable = false)
    @Schema(description = "Suma final cobrada al cliente que incluye el monto neto más el IVA", example = "119000.00")
    private Double totalPagado;
    
    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora exacta en la que se procesó el cobro", example = "2026-06-18T14:30:00.000Z")
    private Date fechaHora;
    
    @Column(name = "metodo_pago", nullable = false)
    @Schema(
        description = "Medio o plataforma utilizada por el cliente para efectuar la transacción", 
        allowableValues = {"tarjeta_credito", "tarjeta_debito", "transferencia", "efectivo"}, 
        example = "tarjeta_debito"
    )
    private String metodoPago;
}