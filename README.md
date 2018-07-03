JMS poster for Weblogic,ActiveMQ, and HornetQ

RUN:

git clone https://github.com/duband/JMSPoster.git

cd JMSPoster

mvn clean install

mvn com.jmsgoodies:JMSPoster:1.0-SNAPSHOT:install -DinstallationDirectory=[your installation directory] -DtargetBrokerType=[activemq|weblogic|hornetq]

if weblogic client(tested on Weblogic 12.1.3 and 12.2.1), copy Weblogic jar from [wlserver directory]/server/lib/wlthint3client.jar under /lib
 

postMsg.bat

