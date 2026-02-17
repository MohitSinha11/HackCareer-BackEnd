# HackCareer Backend

Spring Boot API for HackCareer.

## Run locally

```bash
./mvnw spring-boot:run
```

## Environment variables

See `Backend/backend/.env.example` for all supported variables.

Important for production:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`

## Health check

`GET /api/health`
# HackCareer-BackEnd
