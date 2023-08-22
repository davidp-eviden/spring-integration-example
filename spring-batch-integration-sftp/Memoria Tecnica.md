# Memoria tecnica Spring Batch y Spring Integration

Spring Batch
---
### Qué es Spring Batch
***Spring Batch*** es un *framework* de trabajos ("*jobs*") por lotes ("*batch*") diseñado para el desarrollo de aplicaciones empresariales.
Facilita la creación de trabajos y su despliegue, al ofrecer un sistema de procesamiento, reinicio de trabajos, salto de trabajos, administración de recursos y transacciones, y también herramientas addicionales como registro de informes u optimizaciones.

Información adicional en la [documentación oficial aqui](https://spring.io/projects/spring-batch).

En este proyecto, se puede ver la implementación en [BatchConfig.java](BatchConfig.java)

### Cómo empezar

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
### Jobs
#### Qué es un trabajo
Un trabajo o Job es un proceso que encapsula una tarea o un conjunto de tareas relacionadas que deben ejecutarse en lotes. 
Generalmente consta de uno o más pasos (Steps)que deben ejecutarse en un orden específico.
#### Como ejecutar un trabajo
Para ejecutar un trabajo en Spring Batch, debes configurar el contexto de la aplicación y crear el metodo con el Job. 
El JobBuilder o JobLauncher es responsable de iniciar la ejecución del trabajo. 
Una vez configurado, puedes invocar el Job con el nombre del trabajo que deseas ejecutar.
```java
public class BatchConfig {
    
    public Job moveToOtherTableAndWriteInCsvJob(Step moveToOtherTableStep, JobRepository jobRepository, CustomJobExecutionListener listener, Step convertToCsvStep, Step fileToSftpStep, Step newFileCompleted) {
        return new JobBuilder("moveToOtherTableAndWriteInCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener) // job listener
                .start(moveToOtherTableStep).on("FAILED").fail() // If the moveToOtherTableStep fails - stop  the job
                .from(moveToOtherTableStep).on("COMPLETED").to(convertToCsvStep) // Otherwise continue to the next step
                .from(convertToCsvStep).on("FAILED").fail()
                .from(convertToCsvStep).on("COMPLETED").to(newFileCompleted)
                .from(newFileCompleted).end()
                .build();
    }
}
```
Una vez llamado el Job procederemos a empezar el trabajo, indicar los pasos en el orden adecuado y cerrar el trabajo.

#### Como pasar parametros a un trabajo
Como en el ejemplo anterior, Spring Batch te permite pasar parámetros a un trabajo al momento de su ejecución. Puedes definir parámetros en la configuración del trabajo y luego proporcionar valores concretos al ejecutarlo.

Más información de como configurar el Job en la [documentación oficial](https://docs.spring.io/spring-batch/docs/current/reference/html/job.html)
### Steps
#### Que es un step
Un paso o Step es una unidad de trabajo individual que forma parte de un trabajo o Job. 
Cada step tiene una función específica, como leer datos de una fuente externa, procesarlos y escribir el resultado en una ubicación deseada.
Un Step podrá ser tan simple o complejo o de la tipología que el desarrollador determine oportuno.

#### Como configurar un step
Un step puede estar compuesto de tres elementos: reader, writer y processor.
Los readers son responsables de obtener datos, los processors realizan transformaciones en los datos y los writers escriben los resultados en una fuente de datos.
```java
public Step moveToOtherTableStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener, CustomStepExecutionListener customStepExecutionListener) {
        return new StepBuilder("moveToOtherTableStep", jobRepository)
        .<Contract, ContractProcessed>chunk(5, transactionManager)
        .listener(customChunkListener) // chunk listener
        .listener(customStepExecutionListener) // step listener
        .reader(reader()) // read from the contract table using repositoryItemReader
        .processor(contract -> new ContractProcessed(contract.getPolicyId(), contract.getPolicy(), contract.getPolicySituation(), contract.getPolicyBrand(), contract.getPolicyDate(), contract.isExpired(), contract.isDisabled()))
        .writer(writer()) // write  to the contract_processed table using repositoryItemWriter
        .build();
        }
```
Cabe destacar que un Chunk en los Step es una forma de procesar datos en bloques o fragmentos, en lugar de procesar cada registro individualmente. En lugar de leer, procesar y escribir cada registro uno por uno, se procesa un grupo (chunk) de registros a la vez que nosotros predeterminamos.

Más información de como configurar el Step en la [documentación oficial](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html)
#### Readers & writers
Los readers son componentes que se utilizan en un Step para leer datos de una fuente externa, como una base de datos, un archivo CSV o un servicio web. Proporcionan una interfaz para acceder a los datos y transformarlos en objetos que puedan ser procesados.
```java
public RepositoryItemReader<Contract> reader() {
    return new RepositoryItemReaderBuilder<Contract>()
    .name("reader")
    .repository(contractRepository)
    .methodName("findAll")
    .sorts(Collections.singletonMap("policy", Sort.Direction.ASC))
    .build();
}
```
Los writers son componentes que se utilizan en un Step para escribir los resultados del proceso en una fuente de datos. Pueden ser utilizados para escribir en bases de datos, archivos, servicios web, entre otros.
```java
public RepositoryItemWriter<ContractProcessed> writer() {
        return new RepositoryItemWriterBuilder<ContractProcessed>()
                .repository(contractProcessedRepository)
                .methodName("save")
                .build();
    }
```
### Processors

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

---

## Spring Integration

### Qué es Spring Integration 

Spring Integration es un framework que facilita la conexión entre sí mediante un paradigma de mensajería. 
Con un diseño cuidadoso, estos flujos se pueden modularizar y también reutilizar a un nivel aún mayor.

Información adicional en la [documentación oficial](https://spring.io/projects/spring-integration).

### Cómo empezar

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

Información adicional en la [documentación oficial](https://docs.spring.io/spring-integration/docs/current/reference/html/http.html).

### SFTP ( David )





#### Cómo enviar un archivo via sftp



## References
---
Processor:

[ItemProcessor example](https://www.baeldung.com/introduction-to-spring-batch)

[Processor teoría](https://www.adictosaltrabajo.com/2016/03/14/aprende-spring-batch-con-ejemplos/)