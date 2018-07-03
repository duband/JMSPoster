JMS poster for Weblogic (tested on Weblogic 12.1.3 and 12.2.1) ,ActiveMQ, and HornetQ

RUN:

git clone https://github.com/duband/JMSPoster.git

cd JMSPoster

mvn clean install

mvn com.jmsgoodies:JMSPoster:1.0-SNAPSHOT:install -DinstallationDirectory=[your installation directory] -DtargetBrokerType=[activemq|weblogic|hornetq]

if you have set the targetBrokerType to weblogic, copy the JMS Weblogic client jar wlthint3client.jar from [wlserver directory]/server/lib to [current_jms_poster_intallation]/lib
 
postMsg.bat

