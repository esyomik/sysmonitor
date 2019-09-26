## Security
This document describes steps to configure Kafka broker and clients to use TLS connection with the development environment. The main idea is creating fake CA (certificate authority) root certificate and add it to both *trust stores*: on a client machine(s) and on a server machine. Client and server certificates should be signed by the generated certificate.

It is convenient to use Cygwin under Windows. In the following steps it is assumed that all actions you will do in the same directory (here *C:/kafka/SSL*).

### Generate CA root certificate
- Generate a fake CA root certificate  
`$ openssl req -new -x509 -keyout ca-key -out ca-cert -days 365`  
- Add it into the client's trust store  
`$ keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert`  
- Add CA root certificate into the server's trust store  
`$ keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert`

### Create keystore for the broker and Java applications
- Create self signed certificate (e.g. *server.keystore.jks* for the Kafka):  
`$ keytool -keystore server.keystore.jks -alias kafka -validity 365 -genkey -keyalg RSA -ext SAN=DNS:WS329.i.sigmaukraine.com`  
> **Note** The RFC-2818 recommends to use Subject Alternative Name (SAN) instead of Common Name (CA). But it looks like that in one of the next steps the SAN field is removed from the certificate. So you have to determine CA matched with host name. Also, you can turn off host name verification. Just create an empty field `ssl.endpoint.identification.algorithm=` in the Kafka's configuration file or in the client configuration file  
- Export certificate from the keystore for signing  
`$ keytool -keystore server.keystore.jks -alias kafka -certreq -file cert-file`  
- Sign it using private key of the CA root certificate  
`$ openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial`  
- Import both certificates (CA root and signed server's) into server's keystore:  
`$ keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert`  
`$ keytool -keystore server.keystore.jks -alias kafka -import -file cert-signed`  
- Check them  
`$ keytool -list -v -keystore server.keystore.jks`  

Create a client keystore in a similar way:  
``` bash
$ keytool -keystore WS329.keystore.jks -alias rmonitor -validity 365 -genkey -keyalg RSA -ext SAN=DNS:WS329.i.sigmaukraine.com  
$ keytool -keystore WS329.keystore.jks -alias rmonitor -certreq -file cert-file  
$ openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial  
$ keytool -keystore WS329.keystore.jks -alias CARoot -import -file ca-cert  
$ keytool -keystore WS329.keystore.jks -alias rmonitor -import -file cert-signed  
```

### Create certificate for the C++ aplications
You can use *gen-ssl-certs.sh* (it is taken from the *librdkafka* project). This script contains several hardcoded values to generate a user certificate (password, most certificate fields), thus you can modify them. Usage:  
`$ ./gen-ssl-certs.sh client CA_CERT USER_CERT_FILE_PREFIX_ CN_FIELD`  
**CA_CERT** - file name of the CA certificate; script supposes, that the CA private key file has a name *{CA_CERT}.key* and file with a serial number has name *{CA_CERT}.srl*  
**USER_CERT_FILE_PREFIX_** - prefix for created file with signed user certificate  
**CN_FIELD** - value for CN field in generated certificate  

### Configure broker
- Copy keystore (here *server.keystore.jks*) and trust store (here *server.truststore.jks*) somewhere  
- Add allowed listeners into *server.properties* file  
``` properties
# allows plain text connection using 9092 port and SSL using 9093 port
listeners=PLAINTEXT://:9092,SSL://:9093
```
- Add next parameters into the *server.properties*:  
``` properties
ssl.keystore.location=server.keystore.jks  
ssl.keystore.password=KEYSTORE_PASSWORD  
ssl.key.password=KEY_PASSWORD  
ssl.keystore.type=JKS  
ssl.truststore.location=server.truststore.jks  
ssl.truststore.password=TRUSTSTORE_PASSWORD  
ssl.truststore.type=JKS  
ssl.secure.random.implementation=SHA1PRNG  
# if you want to use secure connection between brokers, optional  
security.inter.broker.protocol=SSL  
# require client's authentication, optional
ssl.client.auth=required  
```
- Run Kafka  
`$ .\bin\windows\kafka-server-start.bat .\config\server.properties`  
and check:  
`$ openssl s_client -debug -connect localhost:9093 -tls1`

### Configure Java client
- Create keystore for the particular client host  
- Copy keystore and trust store (here *WS329.keystore.jks* and *client.truststore.jks* accordingly) into the clent host (you can use the same trust store for the all Java clients)  
- Add next rows to your configuration file  
``` properties
ssl.keystore.location=WS329.keystore.jks  
ssl.keystore.password=KS_PASSWORD  
ssl.key.password=K_PASSWORD  
ssl.keystore.type=JKS  
security.protocol=SSL  
ssl.truststore.location=client.truststore.jks  
ssl.truststore.password=T_PASSWORD  
ssl.truststore.type=JKS  
```

### Configure C++ client
- Create certificate and private key for the particular client host using *gen-ssl-certs.sh* utilites, e.g. for *UGINPC*:  
`$ ./gen-ssl-certs.sh client ca-cert UGINPC_ UGINPC`   
- Copy user's certificate, user's private key and CA certificate (here *UGINPC_client.pem, UGINPC_client.key, ca-cert* accordingly)  
- Add next rows to your configuration file  
``` properties
security.protocol=ssl
# CA certificate file for verifying the broker's certificate.
ssl.ca.location=ca-cert
# Client's certificate
ssl.certificate.location=UGINPC_client.pem
# Client's key
ssl.key.location=UGINPC_client.key
# Key password, if any.
ssl.key.password=PASSWORD
```
E.g. config for *sysmonitor* can be look like:  
``` JSON
{
    "topic": "test",
    "period": 1,

    "kafka": {
        "bootstrap.servers": "WS329.i.sigmaukraine.com:9093",
        "security.protocol": "ssl",
        "ssl.certificate.location": "UGINPC_client.pem",
        "ssl.ca.location": "ca-cert",
        "ssl.key.location": "UGINPC_client.key",
        "ssl.key.password": "Qr41&Zk"
    },

    "metrics": [
        {
            "name": "CPU total time, percent",
            "counter": "\\Processor(_Total)\\% Processor Time",
            "kind": "PERCENT"
        },
        {
            "name": "IPv4 datagrams, datagramm/sec",
            "counter": "\\\\UGINPC\\IPv4\\Datagrams/sec",
            "kind": "VALUE"
        }
    ]
}
```