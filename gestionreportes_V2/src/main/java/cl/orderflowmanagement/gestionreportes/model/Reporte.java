package cl.orderflowmanagement.gestionreportes.model;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historial_reportes")
@Schema(description = "Modelo que representa el registro analítico e histórico de los reportes gerenciales generados en el sistema")
public class Reporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único autoincrementable del registro del reporte", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Column(nullable = false)
    @Schema(
        description = "Clasificación o categoría del reporte generado para el análisis del negocio", 
        allowableValues = {"VENTAS_MENSUAL", "GASTOS_PROVEEDORES", "RANKING_PLATOS"}, 
        example = "VENTAS_MENSUAL"
    )
    private String tipoReporte;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora exacta en la que el sistema procesó y consolidó el reporte", example = "2026-06-18T15:00:00.000Z")
    private Date fechaGeneracion; 

    @Column(nullable = false)
    @Schema(description = "Suma monetaria total calculada y consolidada en el periodo de este reporte", example = "4500000.50")
    private Double montoTotal; 

    @Column(length = 500)
    @Schema(description = "Breve sinopsis, observaciones clave o conclusiones automáticas del reporte generado", example = "El reporte mensual de ventas muestra un incremento del 12% con respecto al mes anterior. El plato más vendido fue el 'Lomo Saltado'.")
    private String resumen; 
}