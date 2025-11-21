Auth Service starter
Run:
  docker compose up -d postgres rabbitmq redis
  ./gradlew bootRun


## Next actions implemented
- Authorization Server (JDBC RegisteredClient + JWKS)
- Nimbus JWT signing (JwtService)
- Flyway migrations for app tables and oauth2 tables
- Audit consumer persisting audit logs
- Actuator + Prometheus
- GitHub Actions CI workflow
# authservice_full_with_k8s
