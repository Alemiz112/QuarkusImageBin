# Quarkus-ImageBin project
> ImageBin simple image hosting project based on Java Quarkus Framework.

## Goals
- Implement simple form to upload new image and provide user image link.
- Store uploaded content using MySQL database.
- Provide user unique link to image.
- Application should be non-blocking for greater performance.
- Easily start whole application using Docker Compose.

## Usage
### Config
Application can be configured using `config.json` file or using env variables.

| Config option | Environment variable | Description |
| :---: | :---: | :---: |
| mysql-host | DB_HOST | MySQL server address | 
| mysql-database | DB_DATABASE | MySQL database | 
| mysql-user | DB_USER | MySQL user with read, write permissions to database | 
| mysql-password | DB_PASS | MySQL user password | 
| public-key-length | IMG_KEY_LEN | Public key part length | 
| max-image-size | IMG_MAX_SIZE | The highest allowed size in `MB` of image |

### Start-up
ImageBin can be started using `docker-compose up` command.  