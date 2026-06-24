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

## Documentación de la API (Swagger)

Cada microservicio cuenta con su propia documentación interactiva generada con **OpenAPI (Swagger)**. Esto permite explorar los contratos de la API, revisar los esquemas de datos (DTOs) y probar los endpoints directamente desde el navegador.

- **Ruta de acceso:** Una vez iniciado un microservicio, ingresa en tu navegador a `http://localhost:[PUERTO]/swagger-ui/index.html` (reemplaza `[PUERTO]` por el puerto específico del microservicio que deseas consultar).

## Pruebas Unitarias

El proyecto incluye un conjunto de pruebas unitarias para garantizar la calidad, fiabilidad y el correcto funcionamiento de la lógica de negocio de forma aislada.

- Desarrolladas utilizando el estándar de **Mockito**.
- Enfocadas en validar el comportamiento esperado y el correcto manejo de excepciones en las capas principales (Controllers y Services).
- **Cómo ejecutarlas:** Puedes correrlas directamente desde tu entorno de desarrollo (IDE) o ejecutando el comando `mvn test` en la terminal dentro de la carpeta correspondiente a cada microservicio.

## Pasos para Ejecutar

1. Clonar el repositorio localmente.
2. Configurar las credenciales de base de datos MySQL en el archivo `application.yml` de cada microservicio.
3. Compilar e iniciar cada microservicio.
4. Utilizar Postman o la interfaz web de Swagger para consumir y probar los distintos endpoints del sistema.
