# Order Flow Management - Arquitectura de Microservicios

## Descripción del Proyecto

Proyecto de backend para administrar el flujo completo de un restaurante. El sistema está construido con una arquitectura de 10 microservicios usando Java y Spring Boot. Se encarga de automatizar todo el proceso: desde que un cliente reserva una mesa, el garzón toma el pedido, la cocina recibe el ticket, se descuenta el stock de los ingredientes exactos, hasta el pago final y la generación de reportes.

## Equipo de Desarrollo

- Vicente Céspedes
- Gabriel González
- Vicente Ponce

## Funcionalidades Implementadas

- **Core:** Gestión de Pedidos, Monitor de Cocina y Facturación/Pagos.
- **Operaciones:** Gestión de Mesas y Reservas con transición de estados síncrona.
- **Inventario:** Gestión de Menú y control transaccional de stock de Insumos/Recetas.
- **Administración:** Control de Usuarios/Empleados, Proveedores y generación de Reportes agregados.

## Pasos para Ejecutar

1. Clonar el repositorio localmente.
2. Configurar las credenciales de base de datos MySQL en el archivo `application.properties` de cada microservicio.
3. Compilar e iniciar cada microservicio .
4. Utilizar Postman para manejar ENDPOINTS.
