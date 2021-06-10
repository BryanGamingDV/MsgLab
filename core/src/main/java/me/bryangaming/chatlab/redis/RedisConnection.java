package me.bryangaming.chatlab.redis;

import me.bryangaming.chatlab.PluginService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection {

    private final PluginService pluginService;
    private JedisPool jedisPool;
    private final String password;
    private final String host;
    private final int port;

    public RedisConnection(PluginService pluginService, String password, String host, int port) {
            this.password = password;
            this.pluginService = pluginService;
            this.host = host;
            this.port = port;
    }

    public void redisConnect() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(8);

        if (password != null && !password.trim().isEmpty()) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 2000);
        }
    }

    public void sendMessage(String channel, MessageType messageType, String... messages) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String message : messages){
            stringBuilder.append(message.replace(";", "//,")).append(";");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, messageType.getMessageType() + ";" + stringBuilder.toString());
        }
    }

    public void subscribeChannel(String channel){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(new RedisSubscriber(pluginService), channel);
        }
    }

    public JedisPool getJedisPool(){
        return jedisPool;
    }
}
