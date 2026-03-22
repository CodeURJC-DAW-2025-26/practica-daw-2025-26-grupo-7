param(
    [Parameter(Mandatory=$true)][string]$DockerHubUser
)

$env:DOCKERHUB_USER = $DockerHubUser

docker login
docker compose -f docker/docker-compose.yml publish "$DockerHubUser/fuego-lento-compose:latest" --with-env