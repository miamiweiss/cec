# cec-cdi

This is a forked version of [Edubits's cec-cdi](https://github.com/Edubits/cec-cdi/).
It has been rewritten so that is uses **Spring Framework** instead of **EJBs beans**. 

It is a CDI extension to interface with [libcec](https://github.com/Pulse-Eight/libcec) 
through cec-client. It can be used to remote control TVs and other media devices.

# Install
1. Install cec-client in `/usr/local/bin/cec-client` (for Raspbian a 
[premade deb](https://drgeoffathome.wordpress.com/2015/08/09/a-premade-libcec-deb/) is available)
2. Add cec-cdi to pom.xml:
```xml
<dependency>
    <groupId>com.manuelweiss</groupId>
    <artifactId>cec</artifactId>
    <version>1.0</version>
</dependency>
```

# Usage examples

## Observe CEC messages
```java
@Component
public class MyListener {
  
    @EventListener
    public void handleCecEvent(Message message) {
        //... 
    }

}
```

## Send CEC message
```java
public class SendExample {
    
    @Autowired
    private CecConnection connection;
    
    public void send() {
        // Send message from RECORDER1 (by default the device running this code) to the TV to turn on
        connection.sendMessage(new Message(RECORDER1, TV, IMAGE_VIEW_ON, Collections.emptyList(), ""));
        
        // Send message from RECORDER1 (by default the device running this code) to the TV to turn off
        connection.sendMessage(new Message(RECORDER1, TV, STANDBY, Collections.emptyList(), ""));
	}
}
```

# Resources

- [Edubits's cec-cdi](https://github.com/Edubits/cec-cdi/)
- [libcec](https://github.com/Pulse-Eight/libcec)
- [CEC-O-MATIC](http://www.cec-o-matic.com) – Translate CEC messages