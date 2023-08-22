# Memoria tecnica Spring Batch y Spring Integration

## Spring Batch
---
### Qué es Spring Batch
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

El processor es el elemento responsable tratar la información obtenida por el reader, su uso no es obligatorio. Es donde se aplica lógica de negocio si se requiere de ella.

#### Como convertir o procesar los datos

Para ello tienes que crear un método de tipo ItemProcessor, o crear una clase que lo implemente.

Esto sería un ejemplo de una implementación de un ItemProcessor, a través de una clase.
```java
public class StudentItemProcessor implements ItemProcessor<Student, Student> {
    @Override
    public Student process(Student student) throws Exception {
        //some business logic
    }
}
```

Y este otro a través de un método de tipo ItemProcessor.

```java
@Configuration
public class SpringBatchConfig {
    @Bean
    public ItemProcessor<Student, Student> itemProcessor() {
        return new itemProcessor();
        //some business logic
    }
}
```

### Listeners ( David )



#### Cómo escuchar cada chunk
#### Cómo escuchar cuando se ejecuta un trabajo

#### Cómo escuchar cuando se ejecuta un paso

## Spring Integration

### Qué es Spring Integration ( Fernando )

Spring Integration es un framework que facilita la conexión entre sí mediante un paradigma de mensajería. Con un diseño cuidadoso, estos flujos se pueden modularizar y también reutilizar a un nivel aún mayor.

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

### SFTP ( David )





#### Cómo enviar un archivo via sftp



## References
---
Processor:

[ItemProcessor example](https://www.baeldung.com/introduction-to-spring-batch)

[Processor teoría](https://www.adictosaltrabajo.com/2016/03/14/aprende-spring-batch-con-ejemplos/)