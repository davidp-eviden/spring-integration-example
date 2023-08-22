# Memoria tecnica Spring Batch y Spring Integration

## Spring Batch
---
### Que es Spring Batch ( Angel )

- [ ] Definicion de Spring batch con referencias
### Como empezar ( David )

Para empezar a usar spring batch se necesita implementar en tu proyecto de spring boot las siguientes dependencias ya sea con maven o con gradle. 

Usando **maven**

```xml
<dependency>
    <groupId>org.springframework.batch</groupId>
    <artifactId>spring-batch-core</artifactId>
    <version>5.0.2</version>
</dependency>
```

Usando **gradle**

```gradle
implementation 'org.springframework.batch:spring-batch-core:5.0.2'
```
### Jobs ( Carlos )

#### Que es un trabajo
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



#### Como escuchar cada chunk
#### Como escuchar cuando se ejecuta un trabajo

#### Como escuchar cuando se ejecuta un paso

## Spring Integration
---
### Que es Spring Integration ( Fernando )

Spring Integration es un framework que facilita la conexión entre sí mediante un paradigma de mensajería. 
Con un diseño cuidadoso, estos flujos se pueden modularizar y también reutilizar a un nivel aún mayor.

### Como empezar ( Angel )
### HTTP ( Angel )

#### Como crear un endpoint
#### Como ejecutar los trabajos a traves de una peticion http

### SFTP ( David )





#### Como enviar un archivo via sftp



## References
---
Processor:

[ItemProcessor example](https://www.baeldung.com/introduction-to-spring-batch)

[Processor teoría](https://www.adictosaltrabajo.com/2016/03/14/aprende-spring-batch-con-ejemplos/)