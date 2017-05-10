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
                    .match(String.class, s -> incrementMessage())
                    .matchAny(m -> unhandled(m))
                    .build()
            );
        }

        private void incrementMessage() {
            log().info(AkkaMultiThreadCounterMain.messageWithCounter());
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem demoSystem = ActorSystem.create("DemoSystem");
        ActorRef manager = demoSystem.actorOf(Props.create(ResourceManager.class), "Manager");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    //System.out.println("Thread " + Thread.currentThread().getName() + messageWithCounter());
                    manager.tell("Message", ActorRef.noSender());
                }
            }).start();
        }
        System.in.read();
    }

    private static String messageWithCounter() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        return " Message index " + ++index;
    }

}
