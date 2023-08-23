# Memoria tecnica Spring Batch y Spring Integration

Spring Batch
---
### Qué es Spring Batch
***Spring Batch*** es un *framework* de trabajos ("*jobs*") por lotes ("*batch*") diseñado para el desarrollo de aplicaciones empresariales.
Facilita la creación de trabajos y su despliegue, al ofrecer un sistema de procesamiento, reinicio de trabajos, salto de trabajos, administración de recursos y transacciones, y también herramientas addicionales como registro de informes u optimizaciones.

Información adicional en la [documentación oficial aqui](https://spring.io/projects/spring-batch).


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
Para ejecutar un trabajo, debes configurar el contexto de la aplicación y crear el metodo con el Job. 
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

Finalmente, para ejecutar el trabajo debemos llamar al JobLauncher y pasarle el job que queremos ejecutar con sus parametros.

```java
    try {
        jobLauncher.run(job, new JobParameters());
    } catch (JobExecutionException e){
        ...
    }
```

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
public class ContractItemProcessor implements ItemProcessor<Contract, Contract> {
    @Override
    public Contract process(Contract contract) throws Exception {
        //some business logic
    }
}
```

Y este otro a través de un método de tipo ItemProcessor.

```java
@Configuration
public class SpringBatchConfig {
    @Bean
    public ItemProcessor<Contract, Contract> ProcessorContract() {
        return new itemProcessor();
        //some business logic
    }
}
```
Se implemntaría llamandole desde el propio step.
```java
public Step moveToOtherTableStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomChunkListener customChunkListener, CustomStepExecutionListener customStepExecutionListener) {
        return new StepBuilder("moveToOtherTableStep", jobRepository)
        .<Contract, ContractProcess>chunk(5, transactionManager)
        ...
        .processor(ProcessorContract())
        .build();
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
Una configuración HTTP nos permite ejecutar trabajos al hacer una petición HTTP a un gateway.
Es necesario que este en una clase marcada con ``@EnableIntegration``

Información adicional en la [documentación oficial](https://docs.spring.io/spring-integration/docs/current/reference/html/http.html).

#### Como crear un endpoint

Se necesita un ``@Bean`` de ``IntegrationFlow``. Este se subscribirá a un canal, que tiene que estar definido.
```java
@Bean
public IntegrationFlow inbound() {
	return IntegrationFlow.from(Http.inboundGateway("/launch")
		.requestMapping(m -> m.methods(HttpMethod.POST)).replyTimeout(10000))
		.get();
}
```
#### Como ejecutar los trabajos a traves de una peticion HTTP

Una vez creado el *endpoint* hay que conectarlo con un canal ("*channel*"). 
Para esto, modificamos el *endpoint* anterior para especificar el canal.
```java
    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlow.from(Http.inboundGateway("/launch")
                        .requestMapping(m -> m.methods(HttpMethod.POST)).replyTimeout(10000))
                .channel("launchJobsChannel") // Subscription to the launchJobsChannel
                .get();
    }
```

El tipo de dato que el método devuelve puede ser distinto al especificado, pero es necesario tener la anotación ``@ServiceActivator`` con ``inputChannel`` con un nombre descriptivo que queramos.

Los nombres de los canales **deben de ser únicos**.
```java
    @ServiceActivator(inputChannel = "launchJobsChannel")
    public Message<?> launchJobs() {
        try {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                    .addDate("date", new Date());
            JobExecution jobExecution  = this.jobLauncher.run(this.job, jobParametersBuilder.toJobParameters());

            // Si el trabajo se ejecuta correctamente, se devuelve una respuesta exitosa.
            return MessageBuilder
                    .withPayload(String.format("The job was launched successfully with the following details: \n %s", jobExecution.toString()))
                    .build();
        } catch (Exception e) {
            // Si se produce una excepción al ejecutar el trabajo, manejarla aquí.
            // Puedes devolver un mensaje de error o cualquier respuesta apropiada.
            return MessageBuilder.withPayload("Error launching the job with the following error: " + e.getMessage()).build();
        }
    }
```

### SFTP ( David )

#### Que permite hacer spring integration SFTP

Spring integration sftp te permite el envio de ficheros y mensajes a traves del protocolo [SFTP](https://en.wikipedia.org/wiki/SSH_File_Transfer_Protocol)
#### Como enviar un archivo via sftp

Los siguiente pasos muestran como enviar un fichero sftp y encapsularlo en un paso para ser ejecutado a traves de un trabajo.

##### Paso 1: Definimos una interfaz que funcionara como puerta de enlace y se utilizara para definir los metodos que se llamaran a traves de un determinado canal

```java
@MessagingGateway
public interface CustomGateway{
	@Gateway(requestChannel = "sendToSftpChannel")
	void sendToSftp(File file);
}
```

En este paso estamos definiendo que una vez el metodo sea implementado funcionara a traves del canal `sendToSftpChannel`.

##### Paso 2: Definimos la sesion

```java
public class IntegrationConfig{
	@Bean
	public SessionFactory<SftpClient.DirEntry> sessionFactory(){
		DefaultSessionFactory factory = new DefaultSessionFactory();
		factory.setHost("192..."); 
		factory.setPort(22); 
		factory.setUser(""); 
		factory.setPassword("");
		factory.setAllowUknownKeys(true);
		return new CachingSessionFactory<>(factory);
	}
}
```

##### Paso 3: Definimos una funcion que en caso de que un servicio se suscriba al canal `sendToSftpChannel` dicho metodo sera ejecutado.

```java
@Bean
@ServiceActivator(inputChannel = "sendToSftpChannel")
public MessageHandler handler(){
	SftpMessageHandler sftpMessageHandler = new SftpMessageHandler();
	sftpMessageHandler.setRemoteDirectoryExpression(new LiteralExpression("/directorioDestino"));

	return sftpMessageHandler;
}
```

##### Paso 4: Definimos el envio del archivo en un Step utilizando **tasklet**

```java
@Bean
public Step sendToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, CustomGateway customGateway){
	return new StepBuilder("sendToSftpStep", jobRepository)
		.tasklet( (contribution, chunkContext) -> {
			customGateway.sendToSftp(new FileSystemResource("filePath").getFile())
			return RepeatStatus.FINISHED;
		}, transactionManager)
		.build();
}
```

* `tasklet()` permite crear pasos para aquellas funciones en las que no se tenga que hacer uso de reader o writers. En este caso esta tarea solo llamaria al metodo que se definio en la interfaz **CustomGateway** y se le pasa por parametro un archivo. Para mas informacion acerca de las **tasklets** consulte el siguiente [enlace](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#taskletStep)


## References
---
Processor:

[ItemProcessor example](https://www.baeldung.com/introduction-to-spring-batch)

[Processor teoría](https://www.adictosaltrabajo.com/2016/03/14/aprende-spring-batch-con-ejemplos/)