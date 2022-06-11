package com.hide_and_fps.project.twich;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.hide_and_fps.project.twich.Configuration;

import java.io.InputStream;

public class Bot {

    /**
     * Holds the Bot Configuration
     */
    private Configuration configuration;

    /**
     * Twitch4J API
     */
    private TwitchClient twitchClient;

    private SimpleEventHandler eventHandler;
    
    /**
     * Constructor
     */
    public Bot() {
        // Load Configuration
        loadConfiguration();
        setBot();

        registerFeatures();
        //start();
    }
    
    public Bot setBot() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        //region Auth
        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc")
        );
        //endregion

        //region TwitchClient
        twitchClient = clientBuilder
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * GraphQL has a limited support
                 * Don't expect a bunch of features enabling it
                 */
                .withEnableGraphQL(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();
        //endregion
        return this;
    }
    
    public void newSetClient(String channel) {
    	twitchClient.getChat().joinChannel(channel);
    }
    
    /**
     * Method to register all features
     */
    private void registerFeatures() {
		this.eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
        // Register Event-based features
        /*
		ChannelNotificationOnDonation channelNotificationOnDonation = new ChannelNotificationOnDonation(eventHandler);
        ChannelNotificationOnFollow channelNotificationOnFollow = new ChannelNotificationOnFollow(eventHandler);
        ChannelNotificationOnSubscription channelNotificationOnSubscription = new ChannelNotificationOnSubscription(eventHandler);
		WriteChannelChatToConsole writeChannelChatToConsole = new WriteChannelChatToConsole(eventHandler);
    	*/
    }

    /**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("config.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, Configuration.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }

    public void start() {
        // Connect to all channels
        for (String channel : configuration.getChannels()) {
            twitchClient.getChat().joinChannel(channel);
        }
    }
    
    public SimpleEventHandler getTwichEventHandler() {
    	return this.eventHandler;
    }

}