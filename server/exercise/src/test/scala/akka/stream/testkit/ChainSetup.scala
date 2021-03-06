package akka.stream.testkit

import akka.actor.{ ActorRefFactory, ActorSystem }
import akka.stream.MaterializerSettings
import akka.stream.scaladsl._
import org.reactivestreams.Publisher
import akka.stream.FlowMaterializer

// TODO: until `akka-stream-testkit-experimental` package is released, we need a copy of this file for testing

class ChainSetup[In, Out](
                           stream: Flow[In, In] ⇒ Flow[In, Out],
                           val settings: MaterializerSettings,
                           materializer: FlowMaterializer,
                           toPublisher: (Source[Out], FlowMaterializer) ⇒ Publisher[Out])(implicit val system: ActorSystem) {

  def this(stream: Flow[In, In] ⇒ Flow[In, Out], settings: MaterializerSettings, toPublisher: (Source[Out], FlowMaterializer) ⇒ Publisher[Out])(implicit system: ActorSystem) =
    this(stream, settings, FlowMaterializer(settings)(system), toPublisher)(system)

  def this(stream: Flow[In, In] ⇒ Flow[In, Out], settings: MaterializerSettings, materializerCreator: (MaterializerSettings, ActorRefFactory) ⇒ FlowMaterializer, toPublisher: (Source[Out], FlowMaterializer) ⇒ Publisher[Out])(implicit system: ActorSystem) =
    this(stream, settings, materializerCreator(settings, system), toPublisher)(system)

  val upstream = StreamTestKit.PublisherProbe[In]()
  val downstream = StreamTestKit.SubscriberProbe[Out]()
  private val s = Source(upstream).via(stream(Flow[In]))
  val publisher = toPublisher(s, materializer)
  val upstreamSubscription = upstream.expectSubscription()
  publisher.subscribe(downstream)
  val downstreamSubscription = downstream.expectSubscription()
}
