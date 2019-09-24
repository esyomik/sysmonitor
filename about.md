## Remote system monitor.
The main purpose is creating applications to demonstrate usage of the Apache Kafka message broker. This application consists from the following parts:
- **sysmonitor** it is a native Windows application that gathers system metrics and sends them to a specific topic; the metrics for gathering are read from the configuration file
- **rmonitor** it is a Java application that reads topic with metrics and displays it.

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
> If you want to run several instance of Kafka, create configuration file for each instance and configure it.

### Run Kafka
- Open console window and run Zookeeper:  
`> zkserver`  
it will start the zookeeper on the defualt port which is 2181; you can change the default port in zoo.cfg file
- Open another instance of console window, change current directory to installed Kafka path and run Kafka  
`> .\bin\windows\kafka-server-start.bat .\config\server.properties`
- In third console window create, if necessary, topic  
`> .\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`
>**Note.** Kafka may work unstable under Windows. Just stop Kafka and Zookeeper, remove logs dir from the Kafka and Zookeeper and re-create topics  

### Compile
- Clone repository:  
`> git clone https://github.com/esyomik/sysmonitor.git`
- Open file *sysmonitor/monitor/sysmonitor/sysmonitor.sln* using Microsoft Visual Studio 2017. Choose needed configuration and platform and build solution.
- Go to *sysmonitor/rmonitor/* folder and build it (all necessary dependencies will be added to the resulting *.jar file):  
`> mvn package`

### Run applications
- Configure sysmonitor application, as described below
- Start sysmonitor64.exe on several machines. Each machine can use different configuration file
- Configure rmonitor application using JSON file
- Run rmonitor application  
```
> cd target
> java -cp rmonitor-1.0-SNAPSHOT-all.jar com.sigma.software.rmonitor.App config.json
```

### Configuration file for sysmonitor
``` JSON
{
    "brokers": "WS329.i.sigmaukraine.com:9092",
    "topic": "test",
    "period": 1,
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
**brokers** the list of adresses with ports of brokers  
**topic** the name of topic to send messages; this name should be match with name of previously created topic  
**period** the period to gather system metrics  
**metrics** array of observing metrics  

_Metrics entries:_  
**name** name of metrics  
**counter** the name of counter; you can determine list of available counters using PowerShell as described here https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.diagnostics/get-counter?view=powershell-5.1  
**kind** type of metrics; one of:  
- PERCENT the value in [0..100] range
- VALUE the value; rmonitor will adapt scale automatically
- ACCUMULATED accumulated value, e.g. number of sent packages  
> If particular metrics is not recognized, then it will be skipped

### Configuration file for rmonitor
``` JSON
{
  "brokers": "WS329.i.sigmaukraine.com:9092",
  "topic": "test",
  "period": 1,
  "groupId": "PerformanceMonitor"
}
```
**brokers** the list of adresses with ports of brokers  
**topic** the name of topic to send messages; this name should be match with name of previously created topic  
**period** the period to update system metrics  
**groupId** the identifier of consumer group; **note:** identifier should be unique for each instance of rmonitor application