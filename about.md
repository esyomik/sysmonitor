## Remote system monitor.
The main purpose is creating applications to demonstrate usage of the Apache Kafka message broker. This application consists from the following parts:
- **sysmonitor** it is a native Windows application that gathers system metrics and sends them to a specific topic; the metrics for gathering are read from the configuration file
- **rmonitor** it is a Java application that reads topic with metrics and displays them in runtime  
- **statservice** it is a service for gathering performance metrics, consolidating them to the statistical data and sending statistical data to various types of consumers, like BI systems, fraud detectors and so on  

This document describes only unsecure connection. You can add authorization using certificates as described in [security.md](security.md).

### Install environment (Windows)
#### Install and configure *Zookeeper*
Download from the http://zookeeper.apache.org/releases.html#download and unzip it anywhere. e.g. C:\Tools.
 - Copy and Rename “zoo_sample.cfg” to “zoo.cfg” in C:\Tools\zookeeper-3.4.9\conf
 - Find & edit dataDir=/tmp/zookeeper to \zookeeper-3.4.9\data 
 - Add in System Variables ZOOKEEPER_HOME = C:\Tools\zookeeper-3.4.9
 - Edit System Variable named “Path” and append this in the last ;%ZOOKEEPER_HOME%\bin;

#### Install and configure *Kafka*
- Go to config folder in Apache Kafka and edit “server.properties” using any text editor.
 - edit most used configuration:
   - **broker.id** identifier of broker; should be unique for each instance
   - **listeners**  uncomment it if you want to use another port (e.g. if you run several instances of the Kafka on the same machine
   - **log.dirs** set to point to folder with logs ${InstallDir}/kafka-logs; each instance of Kafka should have a different folder
   - **num.partitions** default number of partitions for each topic
   - **zookeeper.connect** If your Apache Zookeeper on different server then change the “zookeeper.connect” property. By default Apache Zookeeper will run on port 2181.  
   - **delete.topic.enable = true** add this option
   - **auto.create.topics.enable = true** add this option if you want to allow creating topics automatically
> If you want to run the several instances of Kafka, create configuration file for each instance and configure it.

### Run Kafka
- Open console window and run Zookeeper:  
`> zkserver`  
it will start the *zookeeper* on the defualt port which is 2181; you can change the default port in the *zoo.cfg* file
- Open another instance of console window, change current directory to installed Kafka path and run Kafka  
`> .\bin\windows\kafka-server-start.bat .\config\server.properties`
- In third console window create, if necessary, topic(s)  
`> .\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`
>**Note.** Kafka may work unstable under Windows. Just stop Kafka and Zookeeper, remove logs dir from the Kafka and Zookeeper and re-create topics  

### Compile
- Clone repository:  
`> git clone https://github.com/esyomik/sysmonitor.git`
- Open file *sysmonitor/monitor/sysmonitor/sysmonitor.sln* using Microsoft Visual Studio 2017. Choose needed configuration and platform and build the solution.
- Go to *sysmonitor/rmonitor/* folder and build a *Remote Monitor* application (all necessary dependencies will be added to the resulting *.jar file):  
`> mvn package`  
- Go to *sysmonitor/statservice/* folder and build it (all necessary dependencies will be added to the resulting *.jar file):  
`> mvn package`

### Run applications
- Configure sysmonitor application, as described below
- Start sysmonitor64.exe on several machines. Each machine can use different configuration file
- Configure rmonitor application using JSON file
- Run rmonitor application  
``` bash
$ cd rmonitor/target
$ java -cp rmonitor-1.0-SNAPSHOT-all.jar com.sigma.software.rmonitor.App config.json
```  
- Run statservice if is necessary  
``` bash
$ cd statservice/target
$ java -cp statservice-1.0-SNAPSHOT-all.jar com.sigma.software.statservice.StatService config.json
```  
- If you configure statservice to output data into topic, you can see results using *kafka-console-consumer.bat* tool:
``` bash
$ cd ${KafkaDirectory}
$ .\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic statistics --from-beginning
```  
Starting application order isn't important.

### Configuration file for sysmonitor
``` JSON
{
    "topic": "test",
    "period": 1,
    "kafka" : {
        "bootstrap.servers": "WS329.i.sigmaukraine.com:9092"
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
**kafka** configuration parameters for kafka client, see [CONFIGURATION.md](CONFIGURATION.md) for detailed description and [security.md](security.md) to configure secure connection  
**topic** the name of topic to send messages; this name should be match with name of previously created topic  
**period** the period in seconds to gather system metrics  
**metrics** array of observing metrics  

_Metrics entries:_  
**name** name of the metrics  
**counter** name of the counter; you can determine list of available counters using PowerShell as described here https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.diagnostics/get-counter?view=powershell-5.1  
**kind** type of metrics; one of:  
- PERCENT the value in [0..100] range
- VALUE the value; *rmonitor* will adapt a chart scale automatically
- ACCUMULATED accumulated value, e.g. number of sent packages  
> If particular metrics is not recognized, then it will be skipped

### Configuration file for rmonitor
Rmonitor is configured using *properties* file. See also [security.md](security.md) to configure secure connection
``` properties
# the list of adresses with ports of brokers
bootstrap.servers=WS329.i.sigmaukraine.com:9092
# the identifier of consumer group
group.id=PerformanceMonitor
# disable auto commits for better performance
enable.auto.commit=false
# the name of topic to receive messages
topic.name=test
# the period to update system metrics
update.period.seconds=2
```  
> **Note.** The group identifier should be unique for each instance of the *rmonitor* application  

### Configuration file for statservice
``` JSON
{
  "kafkaPerfProperties" : {
    "bootstrap.servers": "WS329.i.sigmaukraine.com:9093",
    "topic.name": "test",
    "group.id": "StatService",
    "enable.auto.commit": "true",
    "auto.commit.interval.ms": "1000"
  },
  "recorders" : [
    {
      "type": "CONSOLIDATED_STATISTIC",
      "duration": 300,
      "inputName": ".*",
      "output": [
        {
          "serializer": "FLAT",
          "destination": "STDOUT"
        }
      ]
    },
    {
      "type": "STATISTIC",
      "duration": 300,
      "inputName": ".*",
      "output": [
        {
          "serializer": "XML",
          "destination": "FILE",
          "properties": {
            "filename": "D:/statistics.txt",
            "charset": "UTF-8",
            "format": "#%s\n%s\n\n"
          }
        },
        {
          "serializer": "JSON",
          "destination": "TOPIC",
          "properties": {
            "bootstrap.servers": "WS329.i.sigmaukraine.com:9092",
            "topic.name": "statistics",
            "group.id": "StatService",
          }
        }
      ]
    }
  ]
}
```  
**kafkaPerfProperties** the preferences for the Kafka consumer for reading from the topic where *sysmonitor* writes performance counters to, similar to the #rmonitor application; see also [security.md](security.md) to configure secure connection  
**recorders** the array of channels to gather various statistics, fields: 
- **type** the type of recorder; one of:
  - CONSOLIDATED_STATISTIC united statistical data from the several computers
  - STATISTIC statistical data for each host
  - HISTOGRAM (isn't implemented)
  - CONSOLIDATED_HISTORAM (isn't implemented)
- **duration** period for gathering statistics  
- **inputName** regexp to filter input metrics by host names
- **output** array of targets where the gathered statistics send to, each item has the next format:  
  - **serializer** the serializer type, one of:
    - PLAIN the plain text
    - XML the xml
    - JSON the json document
    - BINARY serialized using Java native mechanism
  - **destination** the destination where the serialized data send to, one of:  
    - FILE local file  
    - STDOUT standard output stream
    - TOPIC a Kafka topic (create topic: `.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic statistics`)  
    - DATABASE (isn't implemented)  
    - SERVICE (isn't implemented)  
  - **properties** the preferences depending from the type of the *destination* value