./gradlew clean bootJar
docker build -t crypto-balance-tracker .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e DEMO_COINGECKO_API_KEY=CG-XvaxnBABUHntYUvEohUfEUer \
  crypto-balance-tracker
