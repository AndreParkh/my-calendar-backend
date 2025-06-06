# Используем базовый образ с JDK
FROM openjdk:23-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR-файл в контейнер
COPY build/libs/*.jar my-calendar-backend.jar

# Команда для запуска приложения
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "my-calendar-backend.jar"]

#  "-Dspring.profiles.active=prod",