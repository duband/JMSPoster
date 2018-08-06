JMS client for Weblogic (tested on Weblogic 12.1.3 and 12.2.1) ,ActiveMQ (tested with ActiveMQ 5.11 and 5.15.4), and HornetQ 2.2.11 (embedded in JBoss 7.1)

To install the artefact in your maven repository, RUN:

git clone https://github.com/duband/JMSPoster.git

cd JMSPoster

mvn clean install


Once installed, to create a batch environment, RUN:


mvn com.github.duband:jmsposter:[version]:install -DinstallationDirectory=[your installation directory] -DtargetBrokerType=[activemq|weblogic|hornetq-jboss7.1]

cd [your installation directory]


Make sure the values in the file connection.properties match your broker

RUN :

postMsg.bat


Remarks:

if you have set the targetBrokerType to weblogic, copy the JMS Weblogic client jar wlthint3client.jar from [wlserver directory]/server/lib to [current_jms_poster_installation]/lib

if you have set the targetBrokerType to hornetq-jboss7.1, copy the JMS Weblogic client jar jboss-client-7.1.0.Final.jar from [wlserver directory]/bin/client to [current_jms_poster_installation]/lib


More instructions to follow on :

http://javagoogleappspot.blogspot.com/2018/07/a-client-to-easily-post-messages-to.html
