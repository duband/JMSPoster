JMS poster for Weblogic,ActiveMQ, and HornetQ

RUN:

git clone https://github.com/duband/JMSPoster.git

cd JMSPoster

mvn clean install

mvn com.jmsgoodies:JMSPoster:1.0-SNAPSHOT:install -DinstallationDirectory=[your installation directory] -DtargetBrokerType=[activemq|weblogic|hornetq]

postMsg.bat
