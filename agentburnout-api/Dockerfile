FROM 244386282524.dkr.ecr.us-west-2.amazonaws.com/ccbu/base/amazon_jre17_base:17.0.10-24.02.16

EXPOSE 8080

ADD target/abo-1.0.0.jar abo-1.0.0.jar

ENTRYPOINT ["sh", "-c", "java -DSpring.datasource.username=\"$(cat /vault/secrets/aurora-username.txt)\" -DSpring.datasource.password=\"$(cat /vault/secrets/aurora-password.txt)\" -jar abo-1.0.0.jar"]

