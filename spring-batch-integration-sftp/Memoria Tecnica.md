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

#### Como convertir o procesar los datos

### Listeners ( David )



#### Como escuchar cada chunk
#### Como escuchar cuando se ejecuta un trabajo

#### Como escuchar cuando se ejecuta un paso

## Spring Integration
---
### Que es Spring Integration ( Fernando )
### Como empezar ( Angel )
### HTTP ( Angel )

#### Como crear un endpoint
#### Como ejecutar los trabajos a traves de una peticion http

### SFTP ( David )





#### Como enviar un archivo via sftp



## References
---
