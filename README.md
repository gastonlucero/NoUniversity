
La documentación dice que nace de la necesidad de consumir/enviar datos en tiempo real (bueno en casi tiempo real) evitando analizar grandes cantidades de información y en su lugar analizando pequeños bloques de la misma.

Pero también se puede utilizar para streaming de datos batch, como?? por ejemplo en casos en que necesitamos generar agregaciones sobre los datos de una bd de gran tamaño de información(y estemos cortos de RAM sobre todo), entonces podemos hacer que los datos entren como un flujo constante en "tiempo real" en nuestra aplicación, o también el ejemplo común de leer tweets y filtrar por algún hashtag.
<img class=" aligncenter" title="streams" src="http://huntc.github.io/reactive-streams-presentation/images/homer.jpg" width="305" height="229" />
Permite poder realizar la comunicación entre distintos flujos de trabajo para que la información fluya entre ellos sin tener que considerar los problemas que puedan encontrarse en el medio(entornos de red, un consumer lento, overflow de buffers,etc), basicamente ofrece una manera fácil, intuitiva y segura de formular y ejecutar un flujo de streaming dejando de lado cosas como OutOfMemoryErrors, para lograr esto tiene que ser capaz entre otras cosas de
<ul>
	<li>limitar el tamaño del buffer que se emplea para contener información</li>
	<li>frenar o desacelerar a un producer cuando el consumer es lento o no responde
esto se logra con el concepto de <strong>backpressure</strong>,que es uno de los conceptos mas importantes de una aplicación reactiva <a href="http://reactive-streams.org/" target="_blank" rel="noopener">(reactive-streams.org).</a></li>
</ul>
Vamos con un ejemplo,
<strong>SidraStream = Manzanas ~> Transformarlas ~> Sidra!!</strong>

Primero las dependencias:

[code language="scala"]
"com.typesafe.akka" %% "akka-stream" % "2.5.3"
[/code]

Para empezar definimos el <strong>ActorSystem</strong> y el <strong>ActorMaterializer</strong>

[code language="scala"]
implicit val system = ActorSystem("scalera")
implicit val materializer = ActorMaterializer()
[/code]

Definimos nuestra fuente de datos (las manzanas) el **Source**<code></code>

[code language="scala"]
val source: Source[Int, NotUsed] =
Source(1 to 1000) //1000 manzanas
[/code]

Es la entrada de nuestro ETL, es algo que tiene exactamente una salida, posiblemente "infinito" (e.g leyendo tweets), y se puede definir de varias formas, por ejemplo:

[code language="scala"]
Source(List("1","2",3))
Source.fromIterator( () => Iterator.range(1,10))
Source.tick(1 seconds, 10 millisecond, System.currentTimeMillis().toString)
Source.single(10)
[/code]

Ahora definimos la transformación que vamos a realizar sobre los datos del source
<img class=" aligncenter" style="max-width: 100%;" src="https://media.giphy.com/media/3o7btR4jYOEIEjBS8M/giphy.gif" width="231" height="130" />

[code language="scala"]
val transformation : Source[String,NotUsed] =
source.map(int => s"transform apple $int")
[/code]

Y finalmente , la transformación la enviamos a la salida  **Sink*
<img class=" aligncenter" style="max-width: 100%;" src="https://media.giphy.com/media/IjS3IBjqdOt9u/giphy.gif" width="136" height="211" />

[code language="scala"]
transformation.to(Sink.foreach(t => println(s"Cider One Sink $t"))).run()
transformation.runWith(Sink.foreach(t => println(s"Cider two Sink $t")))
[/code]

Sink es algo con exactamente una entrada, y tiene varios métodos para usar

[code language="scala"]
Sink.foreach(println)
Sink.fold("zero")((acc,next) =&amp;amp;amp;gt; acc+next)
Sink.ignore
[/code]

Juntando todo queda nuestro SidraStream:

[code language="scala"]
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer}
import akka.stream.scaladsl.{Sink, Source}

object CiderStream extends App {
   implicit val system = ActorSystem("scalera-cider")
   implicit val materializer = ActorMaterializer()

   val source: Source[Int, NotUsed] = Source(1 to 1000) //Entrada de datos

   val transformation : Source[String,NotUsed] = source.map(int => s"transform apple $int")

   transformation.to(Sink.foreach(t => println(s"Cider One Sink $t"))).run()
   transformation.runWith(Sink.foreach(t => println(s"Cider two Sink $t")))
}
[/code]

En el último paso vemos dos sinks o salidas, este es un caso de reutilización, usamos la transformación para generar dos tipos de salidas, si ejecutamos el código, se verán dos resultados distintos(gracias al materializer definido anteriormente)
El Materializer es el encargado de ejecutar el flujo de tareas y devolver los resultados, así como también el que se ocupa de obtener todos los recursos necesario para poder ejecutarlo.
Una de las mejores características, como vimos en el ejemplo, que tiene AkkaStream es que se pueden reutilizar las piezas para futuros diseños de flujos de trabajo.
Usemos el mismo Source de antes, pero definimos un nuevo componente, algo llamado Flow, que simplemente calcule el factorial de los números ()

[code language="scala"]
val flow: Flow[Int, Int, NotUsed] = Flow[Int].fold(1)((acc, next) => acc * next) //100*99*98*...*1
[/code]

Flow[In,Out,Mat] esta definido como algo con exactamente una entrada y una salida, en nuestro caso, la entrada(In)
es Int, y la salida(Out) es Int ,el ultimo campo Mat es del materializer pero a efectos practicos con NotUsed podemos sobrevivir

Para conectar las piezas simplemente las unimos

[code language="scala"]
val sink: Sink[Int, Future[Done]] = Sink.foreach(println)
source.runWith(flow.to(Sink.foreach(println)))
val graph : RunnableGraph[NotUsed] = source.via(flow).to(sink)
graph.run()
[/code]

Que es esto de <strong>RunnableGraph</strong>?, es nuestro flujo continuo de tareas "empaquetado" listo para ser ejecutado

<img style="max-width: 100%;" src="http://doc.akka.io/docs/akka/current/images/compose_nested_flow.png" />
Para que se puede usar?, bueno así como definimos flujos continuos anteriormente, akka no permite por medio de <strong>Graph</strong>
definir flujos no lineales, con <strong>Graph</strong> podemos expresar flujos complejos de trabajo
Como se crea? fácil, fefinimos otro flow que nos suma todos los valores de source

[code language="scala"]
val sumFlow: Flow[Int, Int, NotUsed] = Flow[Int].fold(0)
((acc, next) => acc + next)
[/code]

Y nuestro Graph, lo que va a hacer es dado un source, ejecuta un broadcast de los datos a 2 salidas, cada flow utiliza los datos y su salida es la entrada del sink.....

[code language="scala"]
val graph = RunnableGraph.fromGraph(GraphDSL.create(){
    implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val broadcast = builder.add(Broadcast[Int](2))
    source ~> broadcast.in
    broadcast.out(0) ~> flow ~> sink
    broadcast.out(1) ~> sumFlow ~> sink
    ClosedShape
})
[/code]

Explicando un poco, con el implicit builder, podemos construir nuestro graph, usando el operador "edge" (~>), conocido
también como "connect", "via" o "to". Este operador lo tenemos gracias al <strong>import GraphDSL.Implicits._</strong>

ClosedShape significa que nuestro graph esta totalmente conectado o cerrado (hay distintos casos de graph que veremos
mas adelante que no son cerrados), es decir que todas las entradas y salidas estan conectadas, por lo que podemos
transformar nuestro graph en un RunnableGraph (con RunnableGraph.fromGraph )para ejecutarlo y materializar el resultado.
Listo ya tenemos un graph y un runnableGraph inmutable, thread-safe y lo mejor reutilizable

La gran ventaja del GraphDSL es que es similar a como uno dibujaria el flujo en papel
<h5><img class=" alignleft" style="max-width: 100%;" src="http://doc.akka.io/docs/akka/current/images/simple-graph-example.png" width="381" height="107" /></h5>
<h5>source --> broadcast ---> flow ---> sink</h5>
<h5>source --> broadcast ---> sumFlow ---> sink</h5>
 

La documentación de Streams, que tiene embebido ScalaJs para editar código y probar
<a href="http://doc.akka.io/docs/akka/current/scala/stream/stream-introduction.html" target="_blank" rel="noopener">http://doc.akka.io/docs/akka/current/scala/stream/stream-introduction.html</a>

(Esto no se si esta bien ponerlo)

En próximos post veremos en acción backpressure y como podemos trabajar con distintas "junctions" de AkkaStream, así como realizar test cases.