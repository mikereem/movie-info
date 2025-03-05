# Movie Info API

## Overview

Movie Info is a Spring Boot-based REST API that retrieves movie information from external movie databases. The API allows users to search for movies by title and retrieve details such as the year of release and director(s).

## API Specification

The API specification can be accessed at: `/v3/api-docs`

A Swagger UI is also provided at: `/swagger-ui/index.html`

## Build and startup

The Spring Boot application uses some environment variables. Some of them are optional, two of them are mandatory:

| Env variable        | Description              | Required (default)    |
|---------------------|--------------------------|-----------------------|
| OMDB_API_KEY        | API key for the OMDB API | Yes                   |
| TMDB_API_KEY        | API key for the TMDB API | Yes                   |
| MYSQL_ROOT_PASSWORD | Root password for MySQL  | Only for dependencies |
| MYSQL_PASSWORD      | Password for MySQL       | Yes                   |
| MYSQL_USER          | User for MySQL           | Yes                   |
| REDIS_PASSWORD      | Password for Redis       | Yes                   |
| MYSQL_HOST          | Host of MySQL            | No (mysql)            |
| MYSQL_PORT          | Port of MySQL            | No (3306)             |
| REDIS_HOST          | Host of Redis            | No (redis)            |
| REDIS_PORT          | Port of Redis            | No (6379)             |

### Local startup for development

For local development, we can start the dependencies in Docker, and we can start the Spring Boot application independently, in debug mode also.

Start the dependencies using:

``
./build.sh up-dev
``

Stop the dependencies using:

``
./build.sh down-dev
``

For starting the Spring Boot application, you have to define at least the mandatory environment variables. For local development, you also have to redefine the host of the dependencies:

``
MYSQL_HOST=localhost
REDIS_HOST=localhost
``

There is a `dev` Spring profile which can be used for development.

### Local startup

For local usage, we can start the dependencies and the Spring Boot application itself in Docker.

Start everything using:

``
./build.sh up
``

Stop everything using:

``
./build.sh down
``

For starting up everything, you have to define the required environment variables in the `.env` file

### Build and deploy using Helm on a K8S cluster

In this mode, we are building and publishing the Spring Boot application as a Docker image.
Then using the provided Helm chart, it is possible to install the whole package as a deployment in a target K8S cluster by providing some custom values.

#### Building and publishing the Docker image

1. Create a docker repository and update the build.sh according to your repository (docker-build and docker-publish targets)
2. Login to your docker repo (optional)
   ``
   docker login
   ``
3. `./build.sh build`
4. `./build.sh publish`

#### Deploy using Helm

1. Create a custom-values.yaml file or specify the custom values using the `--set` parameter for the helm command:

   movieInfo.apiKeys.omdb=<YOUR_OMDB_API_KEY>

   movieInfo.apiKeys.tmdb=<YOUR_TMDB_API_KEY>

   movieInfo.image.repository=<YOUR_DOCKER_REPO>
2. Create namespace

   `kubectl create ns movie-info`
3. Install using Helm
 
   First update the helm dependencies:

   ``
   ./build.sh helm deps
   ``

   Then do the installation:

   ``
   helm -n movie-info upgrade --install movie-info k8s/helm --set movieInfo.apiKeys.omdb="<YOUR_OMDB_API_KEY>" --set movieInfo.apiKeys.tmdb="<YOUR_TMDB_API_KEY>"
   ``

   or

   ``
   helm -n movie-info upgrade --install movie-info k8s/helm -f custom-values.yaml
   ``
4. Optionally you can modify your values (for example scale up the application) and upgrade the deployment with the same command.
5. Uninstall the deployment

   ``
   helm -n movie-info uninstall movie-info
   ``