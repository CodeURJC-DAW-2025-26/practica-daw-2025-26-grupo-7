param(
    [Parameter(Mandatory=$true)][string]$DockerHubUser
)

$Image = "$DockerHubUser/fuego-lento:latest"

docker login
docker build -t $Image -f docker/Dockerfile .
docker push $Image