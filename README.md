# Food Ordering Microservices

He thong gom nhieu Spring Boot microservices cho bai toan dat mon an: dang ky/dang nhap, quan ly nha hang va menu, gio hang/dat hang, thanh toan, thong bao, API Gateway va Service Discovery.

## Tong Quan Kien Truc

```text
Client
  |
  v
API Gateway :8080
  |
  +-- auth-service         :8081
  +-- restaurant-service   :8082
  +-- order-service        :8083
  +-- payment-service      :8084
  +-- notification-service :8086
  |
  v
Eureka Server :8761

Ha tang phu tro:
- PostgreSQL: database rieng cho tung service
- Redis: cache/token support cho auth-service
- Kafka: giao tiep bat dong bo giua cac service
- Kafka UI: http://localhost:8085
```

## Cac Service

| Service | Port | Vai tro |
| --- | ---: | --- |
| `eureka-server` | `8761` | Service Discovery, noi cac service dang ky va tim nhau |
| `api-gateway` | `8080` | Cong vao duy nhat cho client, route request den service phu hop |
| `auth-service` | `8081` | Dang ky, dang nhap, logout, refresh token, JWT |
| `restaurant-service` | `8082` | Quan ly nha hang va menu item |
| `order-service` | `8083` | Gio hang, checkout, quan ly don hang |
| `payment-service` | `8084` | Thanh toan, tich hop VNPay sandbox |
| `notification-service` | `8086` | Luu/lay thong bao, xu ly event tu Kafka |

## Cong Nghe Chinh

- Java 17
- Spring Boot 2.5.15
- Spring Cloud 2020.0.6
- Spring Cloud Gateway
- Netflix Eureka
- Spring Data JPA
- PostgreSQL
- Redis
- Apache Kafka
- Spring Kafka
- OpenFeign
- Lombok
- Springdoc OpenAPI UI

## Dieu Kien Can Co

Can cai san:

- JDK 17
- Maven
- Docker va Docker Compose
- PostgreSQL local

Mac dinh cac service dang ket noi PostgreSQL tai:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<database>
spring.datasource.username=postgres
spring.datasource.password=
```

Neu may cua ban dung username/password khac, hay sua trong file:

- `auth-service/src/main/resources/application.properties`
- `restaurant-service/src/main/resources/application.properties`
- `order-service/src/main/resources/application.properties`
- `payment-service/src/main/resources/application.properties`
- `notification-service/src/main/resources/application.properties`

## Chuan Bi Database

Tao cac database PostgreSQL sau:

```sql
CREATE DATABASE authdb;
CREATE DATABASE restaurantdb;
CREATE DATABASE orderdb;
CREATE DATABASE paymentdb;
CREATE DATABASE notificationdb;
```

Cac service dang de:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Nen bang se duoc Hibernate tu dong tao/cap nhat khi service khoi dong.

## Chay Redis, Kafka Va Kafka UI

Tai thu muc root project:

```bash
docker compose up -d
```

Lenh nay se chay:

- Redis: `localhost:6379`
- Kafka: `localhost:29092`
- Kafka UI: `http://localhost:8085`

Kiem tra container:

```bash
docker compose ps
```

Dung ha tang:

```bash
docker compose down
```

## Thu Tu Chay He Thong

Nen chay theo thu tu sau:

1. PostgreSQL
2. Redis/Kafka bang `docker compose up -d`
3. `eureka-server`
4. Cac business service: `auth-service`, `restaurant-service`, `order-service`, `payment-service`, `notification-service`
5. `api-gateway`

Sau khi chay, mo Eureka Dashboard:

```text
http://localhost:8761
```

Neu thanh cong, cac service se hien trong danh sach instance tren Eureka.

## Cach Chay Tung Service

Mo moi terminal rieng cho tung service.

### Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

### Auth Service

```bash
cd auth-service
mvn spring-boot:run
```

### Restaurant Service

```bash
cd restaurant-service
mvn spring-boot:run
```

### Order Service

```bash
cd order-service
mvn spring-boot:run
```

### Payment Service

```bash
cd payment-service
mvn spring-boot:run
```

### Notification Service

```bash
cd notification-service
mvn spring-boot:run
```

### API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

## Build Tat Ca Service

Do project khong co parent Maven o root, build tung service:

```bash
cd eureka-server && mvn clean package
cd ../auth-service && mvn clean package
cd ../restaurant-service && mvn clean package
cd ../order-service && mvn clean package
cd ../payment-service && mvn clean package
cd ../notification-service && mvn clean package
cd ../api-gateway && mvn clean package
```

Tren PowerShell co the chay tung lenh rieng:

```powershell
cd eureka-server
mvn clean package
```

Sau khi build, co the chay file jar:

```bash
java -jar target/<ten-file>.jar
```

## Route Qua API Gateway

Tat ca request tu client nen di qua:

```text
http://localhost:8080
```

Gateway dang route cac path sau:

| Path | Service dich |
| --- | --- |
| `/api/auth/**` | `auth-service` |
| `/api/restaurants/**` | `restaurant-service` |
| `/api/menu-items/**` | `restaurant-service` |
| `/api/cart/**` | `order-service` |
| `/api/orders/**` | `order-service` |
| `/api/customer-orders/**` | `order-service` |
| `/api/restaurant-orders/**` | `order-service` |
| `/api/payments/**` | `payment-service` |
| `/api/notifications/**` | `notification-service` |

Vi du:

```text
POST http://localhost:8080/api/auth/register
POST http://localhost:8080/api/auth/login
GET  http://localhost:8080/api/restaurants
GET  http://localhost:8080/api/cart/{restaurantId}
POST http://localhost:8080/api/orders/checkout
POST http://localhost:8080/api/payments
GET  http://localhost:8080/api/notifications
```

## Endpoint Tong Quan

### Auth Service

Base path: `/api/auth`

- `POST /register`
- `POST /login`
- `POST /logout`
- `POST /refresh`

### Restaurant Service

Base path: `/api/restaurants`

- `GET /`
- `GET /{restaurantId}`
- `PATCH /{restaurantId}`
- `GET /{restaurantId}/menu-items`
- `POST /{restaurantId}/menu-items`

Base path: `/api/menu-items`

- `GET /{itemId}`
- `PATCH /{itemId}`
- `DELETE /{itemId}`

### Order Service

Base path: `/api/cart`

- `POST /items`
- `GET /{restaurantId}`

Base path: `/api/orders`

- `GET /{orderId}`
- `POST /checkout`

Base path: `/api/customer-orders`

- `GET /orders`

Base path: `/api/restaurant-orders`

- `GET /`

### Payment Service

Base path: `/api/payments`

- `POST /`
- `GET /vnpay-return`
- `POST /vnpay-ipn`
- `GET /dev/gen-hash`

### Notification Service

Base path: `/api/notifications`

- `GET /`

## Swagger UI

Nhieu service co dependency `springdoc-openapi-ui`, co the thu truy cap:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8082/swagger-ui.html
http://localhost:8083/swagger-ui.html
http://localhost:8084/swagger-ui.html
http://localhost:8086/swagger-ui.html
```

Neu truy cap qua Gateway, can cau hinh them route OpenAPI rieng neu muon gom tai mot noi.

## Cau Hinh Quan Trong

### Eureka

Cac service dang dang ky vao:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

### Kafka

Ung dung dung Kafka local tai:

```properties
spring.kafka.bootstrap-servers=localhost:29092
```

### Redis

`auth-service` dung Redis:

```properties
spring.redis.host=localhost
spring.redis.port=6379
```

### VNPay Sandbox

`payment-service` dang cau hinh VNPay sandbox:

```properties
vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8084/api/payments/vnpay-return
vnpay.ipn-url=http://localhost:8084/api/payments/vnpay-ipn
```

Khi goi thanh toan qua Gateway, can de y `return-url` hien dang tro truc tiep ve port `8084`.

## Loi Thuong Gap

### Service khong hien tren Eureka

- Kiem tra `eureka-server` da chay chua: `http://localhost:8761`
- Kiem tra port service co bi trung khong
- Kiem tra log service co loi ket noi Eureka khong

### Loi ket noi PostgreSQL

- Dam bao PostgreSQL dang chay
- Tao dung database: `authdb`, `restaurantdb`, `orderdb`, `paymentdb`, `notificationdb`
- Kiem tra username/password trong `application.properties`

### Loi ket noi Kafka

- Chay `docker compose ps` de xem Kafka da len chua
- Kiem tra `spring.kafka.bootstrap-servers=localhost:29092`
- Mo Kafka UI tai `http://localhost:8085`

### Gateway tra ve 503

- Service dich chua chay hoac chua dang ky Eureka
- Doi vai giay de Eureka cap nhat instance
- Kiem tra `spring.application.name` cua service co khop route `lb://...` trong gateway khong

## Cau Truc Thu Muc

```text
.
+-- api-gateway
+-- auth-service
+-- eureka-server
+-- notification-service
+-- order-service
+-- payment-service
+-- restaurant-service
`-- docker-compose.yml
```

## Ghi Chu Bao Mat

Hien tai mot so thong tin nhu database password, JWT secret va VNPay secret dang nam trong `application.properties`. Khi deploy that, nen chuyen cac gia tri nay sang bien moi truong hoac secret manager, va khong commit secret len repository public.
