FROM adoptopenjdk/openjdk17

WORKDIR /app
ADD *.jar app.jar
COPY packgopay-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=dev
ENV TZ=Asia/Shanghai
# 远程调试接口
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=8002,server=y,suspend=n"
EXPOSE 8002
EXPOSE 8090
VOLUME /tmp

ENTRYPOINT ["exec","java","-jar","-Xmx512m","app.jar"]