# TransportCompany

Конзолно Java приложение за управление на транспортна компания: компании, клиенти, служители, превозни средства и курсове (transports). Приложението работи с база данни **PostgreSQL** и използва **Hibernate ORM** за персистентност.

## Основни функционалности

- Управление на транспортни компании (CRUD + сортиране по име и приходи)
- Управление на клиенти (CRUD)
- Управление на превозни средства (CRUD)
- Управление на служители (CRUD + справки/сортиране по квалификация и заплата)
- Управление на транспорти (CRUD операции през команди + справки)
- Маркиране на транспорт като платен
- Експорт на транспорти във файл и визуализиране от файл

## Технологии

- Java (конзолно приложение)
- Gradle (wrapper)
- Hibernate ORM
- PostgreSQL (в Docker)
- JUnit 5 (за тестове)
- H2 (за тестове)

## Изисквания

- Инсталиран Docker и Docker Compose
- Инсталиран JDK (препоръчително 17+)
- (По желание) IntelliJ IDEA за най-лесно стартиране на приложението

## Стартиране (Docker + база данни)

1) Увери се, че имаш инсталиран Docker.

2) Стартирай контейнерите (в root директорията на проекта):

```bash
docker-compose up -d
```

3) Ако искаш да изтриеш базата **без да се запазва информация във volume-и** (чисто начало), изпълни:

```bash
docker-compose down -v
```

## Стартиране на приложението

### Вариант A (препоръчително): през IntelliJ IDEA
1) Отвори проекта.
2) Увери се, че базата е пусната с `docker-compose up -d`.
3) Стартирай main класа (примерно):
   - `src/main/java/.../ConsoleApp.java`

### Вариант B: през терминал (build + тестове)
```bash
./gradlew clean build
./gradlew test
```

## Команди в конзолата

В приложението напиши `help`, за да видиш менюто. Налични са команди за:
- Компании (`create-company`, `list-companies`, `edit-company`, `delete-company`, сортирания)
- Клиенти (`create-client`, `list-clients`, `edit-client`, `delete-client`)
- Превозни средства (`create-vehicle`, `list-vehicles`, `edit-vehicle`, `delete-vehicle`)
- Служители (`create-employee`, `list-employees`, `edit-employee`, `delete-employee`, справки/сортирания)
- Транспорти (`create-transport`, `list-transports`, филтри/сортирания, `mark-transport-paid`)
- Справки и файлови операции (summary/revenue reports, export/import)
- `exit` — изход

## Troubleshooting

- Ако приложението не се свързва с базата:
  - провери дали контейнерът работи: `docker ps`
  - провери портовете в `docker-compose.yml`
  - провери настройките в `src/main/resources/hibernate.properties`
- За чиста база:
  - `docker-compose down -v` (изтрива и volume-а)
