package com.che.demo;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * Created by chetan on 30/04/2017.
 */
public class AkkaMultiThreadCounterMain {
    private static int index;

    private static class ResourceManager extends AbstractLoggingActor {
        ResourceManager() {
            receive(ReceiveBuilder
                    .match(String.class, s -> log().info("Message index " + ++index))
                    .matchAny(m -> unhandled(m))
                    .build()
            );
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem demoSystem = ActorSystem.create("DemoSystem");
        ActorRef manager = demoSystem.actorOf(Props.create(ResourceManager.class), "Manager");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    manager.tell("Message", ActorRef.noSender());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                }
            }).start();
        }
        System.in.read();
    }

}
