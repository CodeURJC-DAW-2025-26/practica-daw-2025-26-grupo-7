param(
    [string]$ImageName = "fuego-lento:latest"
)

docker build -t $ImageName -f docker/Dockerfile .