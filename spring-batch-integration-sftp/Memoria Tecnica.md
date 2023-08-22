# Memoria tecnica Spring Batch y Spring Integration

## Spring Batch
---
### Qué es Spring Batch ( Angel )
***Spring Batch*** es un *framework* de trabajos ("*jobs*") por lotes ("*batch*") diseñado para el desarrollo de aplicaciones empresariales.
Facilita la creación de trabajos y su despliegue, al ofrecer un sistema de procesamiento, reinicio de trabajos, salto de trabajos, administración de recursos y transacciones, y también herramientas addicionales como registro de informes u optimizaciones.

Información addicional en la [documentación oficial aqui](https://spring.io/projects/spring-batch).

En este projecto, se puede ver la implementación en [BatchConfig.java](BatchConfig.java)

### Cómo empezar ( David )

Para empezar a usar spring batch se necesita implementar en tu proyecto de spring boot las siguientes dependencias ya sea con maven o con gradle. 

Usando **Maven**

```xml
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-core</artifactId>
    <version>5.0.2</version>
</dependency>
```

Usando **Gradle**

```gradle
implementation 'org.springframework.batch:spring-batch-core:5.0.2'
```
### Jobs ( Carlos )

#### Qué es un trabajo
#### Como ejecutar un trabajo
#### Como pasar parametros a un trabajo

### Steps ( Carlos )

#### Que es un step

#### Como configurar un step

#### Readers & writers

### Processors ( Fernando )

#### Que es un processor y que hace

#### Como convertir o procesar los datos

### Listeners ( David )



#### Como escuchar cada chunk
#### Como escuchar cuando se ejecuta un trabajo

#### Como escuchar cuando se ejecuta un paso

## Spring Integration
---
### Qué es Spring Integration ( Fernando )

Información addicional en la [documentación oficial](https://spring.io/projects/spring-integration).

### Cómo empezar ( Angel )

Para empezar a usar *Spring Integration* es necesario añadir las siguientes dependencias.
Hay que notar que estas son las últimas versiones en la fecha que se escribió este documento, y se recomienda actualizarse si es necesario.

Usando **Maven**

```xml
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-core</artifactId>
    <version>6.1.2</version>
</dependency>
```

Usando **Gradle**

```gradle
implementation 'org.springframework.integration:spring-integration-core:6.1.2'
```

### HTTP
Una configuración HTTP nos permite ejecutar trabajos al hacer una petición HTTP. Por ejemplo:
```java
@Bean
public IntegrationFlow inbound() {
	return IntegrationFlow.from(Http.inboundGateway("/launch")
		.requestMapping(m -> m.methods(HttpMethod.POST)).replyTimeout(10000))
		.channel("launchJobsChannel") // Subscription to the launchJobsChannel
		.get();
}
```

Información addicional en la [documentación oficial](https://docs.spring.io/spring-integration/docs/current/reference/html/http.html).

#### Como crear un endpoint
#### Como ejecutar los trabajos a traves de una peticion http

### SFTP ( David )





#### Como enviar un archivo via sftp



## References
---
