package com.hide_and_fps.project.twich;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.DonationEvent;

public abstract class ChannelNotificationOnDonation {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public ChannelNotificationOnDonation(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(DonationEvent.class, event -> onDonation(event));
    }

    public abstract void onDonation(DonationEvent event);
    /**
     * Subscribe to the Donation Event
     */
    /*
    public void onDonation(DonationEvent event) {
        String message = String.format(
                "%s just donated %s using %s!",
                event.getUser().getName(),
                event.getAmount(),
                event.getSource()
        );

        event.getTwitchChat().sendMessage(event.getChannel().getName(), message);
    }
    */

}