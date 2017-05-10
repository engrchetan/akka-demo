package com.che.demo;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * Created by chetan on 30/04/2017.
 */
public class AkkaProducerConsumerMain {
    private static String[] PRODUCTS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"};

    private static class Producer extends AbstractLoggingActor {
        private final ActorRef consumer;
        Producer() {
            consumer = getContext().system().actorOf(Props.create(Consumer.class), "Consumer");
            receive(ReceiveBuilder
                    .match(Integer.class, i -> produce(i))
                    .matchAny(m -> unhandled(m))
                    .build()
            );
        }

        private void produce(Integer i) throws InterruptedException {
            if(++i >= PRODUCTS.length) return;
            log().info("Producing product " + PRODUCTS[i]);
            Thread.sleep(200);
            consumer.tell(i, self());
        }
    }

    private static class Consumer extends AbstractLoggingActor {
        Consumer() {
            receive(ReceiveBuilder
                            .match(Integer.class, i -> consume(i))
                            .matchAny(m -> unhandled(m))
                            .build()
            );
        }

        private void consume(Integer i) throws InterruptedException {
            log().info("Consuming product " + PRODUCTS[i]);
            sender().tell(i, self());
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem demoSystem = ActorSystem.create("DemoSystem");
        ActorRef producer = demoSystem.actorOf(Props.create(Producer.class), "Producer");
        producer.tell(-1, ActorRef.noSender());
        System.in.read();
    }

}
